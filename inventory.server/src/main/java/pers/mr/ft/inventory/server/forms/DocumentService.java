package pers.mr.ft.inventory.server.forms;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.security.ACCESS;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;

import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.shared.forms.CreateDocumentPermission;
import pers.mr.ft.inventory.shared.forms.DocumentFormData;
import pers.mr.ft.inventory.shared.forms.DocumentFormData.Parts.PartsRowData;
import pers.mr.ft.inventory.shared.forms.IDocumentService;
import pers.mr.ft.inventory.shared.forms.ReadDocumentPermission;
import pers.mr.ft.inventory.shared.forms.UpdateDocumentPermission;

public class DocumentService implements IDocumentService {
  static MessageDigest md = null;
  
  {
    try {
      md = MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public DocumentFormData prepareCreate(DocumentFormData formData) {
    if (!ACCESS.check(new CreateDocumentPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO [michel] add business logic here.
    return formData;
  }

  @Override
  public DocumentFormData create(DocumentFormData formData) {
    if (!ACCESS.check(new CreateDocumentPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    
    calcExtensions(formData);

    SQL.insert("INSERT INTO document (datenbank_id, part_number, year, ft_title, title, language, update_date, extension) " +
        " VALUES (:fTDBId, :partNumber, :year, :fTDBName, :name, :lang, CURRENT_TIMESTAMP, :extension)", 
        formData);
    Object[][] rows = SQL.select("SELECT LASTVAL()");
    Long documentId = (Long) rows[0][0];
    
    if (formData.getDocumentFileChooser().isValueSet()) {
      storeContent(documentId, formData.getDocumentFileChooser().getValue());
    }
    formData.setDocId(documentId);
    
    storeLinks(formData);

    return formData;
  }

  @Override
  public DocumentFormData load(DocumentFormData formData) {
    if (!ACCESS.check(new ReadDocumentPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    
    // TODO: Choix de langue dans la session
    String userLanguage = ServerSession.get().getSessionLanguage();
    String defaultLanguage =  ServerSession.get().getDefaultLanguage();
    
    SQL.select("SELECT id, datenbank_id, part_number, year, ft_title, title, language, extension " + 
        " FROM document " +
        " WHERE id = :docId" +
        " INTO :id, :fTDBId, :partNumber, :year, :fTDBName, :name, :lang, :extension", 
        formData);
    
    SQL.select("SELECT dp.part_id, dp.part_id, pn.year_number, COALESCE(l1.label, l2.label), 'icons?image=' || p.ft_icon " +
        " FROM doc_part dp " +
        " JOIN part p ON p.id = dp.part_id " + 
        " LEFT JOIN multilingual_label l1 ON l1.id = p.title_id AND l1.langcode = :userLanguage " +
        " LEFT JOIN multilingual_label l2 ON l2.id = p.title_id AND l2.langcode = :defaultLanguage " +
        " LEFT JOIN v_one_part_number pn ON pn.part_id = p.id " +
        " WHERE doc_id = :docId " +
        " INTO :{parts.oldId}, :{parts.id}, :{parts.partNumber}, :{parts.partLabel}, :{parts.icon}",
        formData, 
        new NVPair("userLanguage", userLanguage), 
        new NVPair("defaultLanguage", defaultLanguage));
    return formData;
  }

  private String calcExtensions(DocumentFormData formData) {
    String filename = "";
    if (! formData.getExtension().isValueSet()
        || formData.getExtension().getValue() == null 
        || formData.getExtension().getValue().equals("")) {
      // Si extension pas fournie on la calcule par rapport au contenu
      BinaryResource docContent = null;
      if (formData.getDocumentFileChooser().isValueSet()) {
        docContent = formData.getDocumentFileChooser().getValue();
      }
      //TODO: voir avec le mimetype ?
      if (docContent!=null) {
        filename = docContent.getFilename();
        int posDot = filename.lastIndexOf('.');
        if (posDot>=0) {
          String extension = filename.substring(posDot);
          formData.getExtension().setValue(extension);
        }
      }
    }
    return filename;
  }
  
  @Override
  public DocumentFormData store(DocumentFormData formData) {
    if (!ACCESS.check(new UpdateDocumentPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    
    Long documentId = formData.getDocId();
    calcExtensions(formData);
    
    SQL.update("UPDATE document SET " +
        " datenbank_id = :fTDBId, " + 
        " part_number = :partNumber, " + 
        " year = :year, " + 
        " ft_title = :fTDBName, " + 
        " title = :name, " + 
        " language = :lang, " + 
        " update_date = CURRENT_TIMESTAMP, " +
        " extension = :extension " + 
        " WHERE id = :docId", 
        formData);
    
    storeLinks(formData);
    
    if (formData.getDocumentFileChooser().isValueSet()) {
      BinaryResource docContent = formData.getDocumentFileChooser().getValue();
      if (docContent!=null) {
        storeContent(documentId, docContent);
      }
    }
    return formData;
  }

  private void storeLinks(DocumentFormData formData) {
    
    Long documentId = formData.getDocId();
    
    // Liens avec pièces et/ou boites
    for (PartsRowData partData: formData.getParts().getRows()) {
      Long partId = partData.getId();
      switch (partData.getRowState()) {
      case AbstractTableRowData.STATUS_NON_CHANGED:
        break;
      case AbstractTableRowData.STATUS_INSERTED:
        if (isGT0(partId)) {
          SQL.insert("INSERT INTO doc_part (part_id, doc_id) VALUES (:partId, :documentId)", 
              new NVPair("partId", partId),
              new NVPair("documentId", documentId)
              );
        }
        break;
      case AbstractTableRowData.STATUS_UPDATED:
        // Si partId est nul, on a supprimé le lien avec la pièce. Sinon en principe c'est une mise à jour,
        // mais la ligne peut être marquée UPDATED alors que l'id de pièce n'a pas changé au final.
        if (isGT0(partId)) {
          if (!partId.equals(partData.getOldId())) {
            SQL.update("UPDATE doc_part SET part_id = :partId WHERE doc_id = :documentId and part_id = :oldId", 
                partData,
                new NVPair("partId", partId),
                new NVPair("documentId", documentId));
          }
        }
        else {
          SQL.delete("DELETE FROM doc_part WHERE doc_id = :documentId and part_id = :oldId", 
              partData,
              new NVPair("documentId", documentId));
        }
        break;
      case AbstractTableRowData.STATUS_DELETED:
        SQL.delete("DELETE FROM doc_part WHERE doc_id = :documentId and part_id = :oldId", 
            partData,
            new NVPair("documentId", documentId));
        break;
      }
    }
    
  }
  
  @Override
  public BinaryResource loadContent(Long documentId) {
    String query = "SELECT title, part_number, year, content, extension FROM document WHERE id = :documentId ";
    Object[][] rows = SQL.select(query, 
        new NVPair("documentId", documentId));
    if (rows.length==1) {
      int numField = 0;
      String documentName = (String) rows[0][numField++];
      String partNumber = (String) rows[0][numField++];
      String year = (String) rows[0][numField++];
      byte [] content = (byte[]) rows[0][numField++];
      String extension = (String) rows[0][numField++];
      String fileName = "";
      if (partNumber != null && partNumber.length()>0) {
        fileName = partNumber;
        
        if (year!=null && year.length()>0) {
          fileName += " ("+year+")";
        }
        fileName += " ";
      }
      fileName += documentName;
      
      if (!fileName.endsWith(extension)) {
        fileName = fileName + extension;
      }
      BinaryResource documentData = new BinaryResource (fileName, content);
      return documentData;
    }
    return null;
  }

  @Override
  public String computeDigest(BinaryResource docContent) {
    String hexDigest = "";
    
    // Calcul du digest (pour la recherche des doublons)
    if (md!=null && docContent!=null) {
      byte[] digest = md.digest(docContent.getContent());
      Formatter formatter = new Formatter();
      for (byte b: digest) {
        formatter.format("%02X", b);
      }
      hexDigest = formatter.toString();
      formatter.close();
    }
    return hexDigest;
  }
  
  @Override
  public void storeContent(Long documentId, BinaryResource docContent) {
    if (!ACCESS.check(new UpdateDocumentPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    
    String hexDigest = computeDigest(docContent);
    
    SQL.update("UPDATE document SET content = :content, digest = :digest WHERE id = :documentId", 
        new NVPair("documentId", documentId),
        new NVPair("content", docContent.getContent()), 
        new NVPair("digest", hexDigest));

  }
  
  private boolean isGT0(Long value) {
    if (value==null) return false;
    if (value>0) return true;
    return false;
  }

  @Override
  public void updateDigests() {
    if (!ACCESS.check(new UpdateDocumentPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }

    Object [][] docs = SQL.select("SELECT id FROM Document WHERE content IS NOT NULL AND digest IS NULL");
    for (Object[] row: docs) {
      Long docId = (Long) row[0];
      
      BinaryResource docContent = loadContent(docId);
      String docDigest = computeDigest(docContent);
      SQL.update("UPDATE Document SET digest = :digest WHERE id = :id", 
          new NVPair("id",docId), 
          new NVPair("digest", docDigest));
      SQL.commit();
    }
    
  }

  @Override
  public Long findDigest(String digest) {
    Object[][] rows = SQL.select("SELECT id FROM Document where digest = :digest", new NVPair("digest", digest));
    if (rows.length>0) {
      return (Long) rows[0][0];
    }
    else {
      return 0L;
    }
  }
}
