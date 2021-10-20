package pers.mr.ft.inventory.server.forms;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.security.ACCESS;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;

import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.shared.forms.BoxFormData;
import pers.mr.ft.inventory.shared.forms.BoxFormData.Parts.PartsRowData;
import pers.mr.ft.inventory.shared.forms.CreateBoxPermission;
import pers.mr.ft.inventory.shared.forms.IBoxService;
import pers.mr.ft.inventory.shared.forms.IPartService;
import pers.mr.ft.inventory.shared.forms.MovePartsFormData;
import pers.mr.ft.inventory.shared.forms.PartFormData;
import pers.mr.ft.inventory.shared.forms.ReadBoxPermission;
import pers.mr.ft.inventory.shared.forms.UpdateBoxPermission;
import pers.mr.ft.inventory.shared.model.Document;
import pers.mr.ft.inventory.shared.security.FTPrincipal;

public class BoxService implements IBoxService {
  @Override
  public BoxFormData prepareCreate(BoxFormData formData) {
    if (!ACCESS.check(new CreateBoxPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    
    // S'il y a un boxId c'est une copie
    Long boxId = formData.getBoxId();
    if (boxId != null && boxId>0) {
      formData = load(formData);
      
      // Nouvelle boite : effacer l'id de boite
      formData.setBoxId(0L);          
      formData.getId().setValue(0L);
      
    }
    else {
      // S'il y a un kitId c'est une création depuis un kit FT
      Long kitId = formData.getKitId();
      if (kitId != null && kitId >0) {
        formData = createFromKit(formData);
      }
    }
    return formData;
  }

  @Override
  public BoxFormData create(BoxFormData formData) {
    if (!ACCESS.check(new CreateBoxPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    
    FTPrincipal principal = ServerSession.get().getPrincipal();
    Long userId = principal.getId();
    
    // Box data
    SQL.insert("INSERT INTO box (boxtype_id, parentid, location_id, label, description, color_id, length, width, height, remarks, model_id, lot_achat, given, user_id) " +
        " VALUES (:boxType, :parent, :location, :label, :description, :color, :length, :width, :height, :remarks, :model, :boughtSet, :given, :userId)",
        formData, new NVPair("userId", userId));
    Object[][] rows = SQL.select("SELECT LASTVAL()");
    Long boxId = (Long) rows[0][0];
   
    formData.setBoxId(boxId);
    formData.getId().setValue(boxId);
    
    // Contenu
    storeParts(formData);

    return formData;
  }

  @Override
  public BoxFormData load(BoxFormData formData) {
    if (!ACCESS.check(new ReadBoxPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO: Choix de langue dans la session
    String userLanguage = ServerSession.get().getSessionLanguage();
    String defaultLanguage =  ServerSession.get().getDefaultLanguage();

    String query = "SELECT id, label, boxtype_id, description, parentid, location_id, color_id, length, width, height, remarks, model_id, lot_achat, given " +
        " FROM box " +
        " WHERE id = :boxId " +
        " INTO :id, :label, :boxType, :description, :parent, :location, :color, :length, :width, :height, :remarks, :model, :boughtSet, :given";
    SQL.selectInto(query, formData);
    
    String partsQuery = "SELECT p.id, p.id, pn.year_number, COALESCE(l1.label, l2.label), 'icons/?image=' || p.ft_icon, bc.count, pc.count, p.color_id, COALESCE (p.cost, 0.0), COALESCE (bc.count * p.cost, 0.0), bc.bin_id, pc.ftdb_count " +
        " FROM v_box_contains bc " +
        " JOIN part p ON p.id = bc.part_id " + 
        " LEFT JOIN multilingual_label l1 ON l1.id = p.title_id AND l1.langcode = :userLanguage " +
        " LEFT JOIN multilingual_label l2 ON l2.id = p.title_id AND l2.langcode = :defaultLanguage " +
        " LEFT JOIN v_one_part_number pn ON pn.part_id = p.id " +
        " LEFT JOIN box container on container.id = bc.bin_id " +
        " LEFT JOIN part_contains pc ON pc.container_id = COALESCE(container.model_id,:model) AND p.id = pc.part_id " +
        " WHERE bc.container_id = :boxId " +
        " INTO :{parts.oldId}, :{parts.id}, :{parts.partNumber}, :{parts.partLabel}, :{parts.icon}, :{parts.count}, :{parts.kitCount}, :{parts.color}, :{parts.partValue}, :{parts.value}, :{parts.bin}, :{parts.fTDBCount}"
    ;
    SQL.selectInto(partsQuery, 
        formData, 
        new NVPair("userLanguage", userLanguage), 
        new NVPair("defaultLanguage", defaultLanguage));
    
    String docsQuery = "SELECT d.id, d.title " + 
        " FROM document d " + 
        " WHERE " + 
        " d.id IN (SELECT doc_id FROM doc_part WHERE part_id = :model) " +
        " OR d.id IN (SELECT doc_id FROM doc_part WHERE part_id IN (SELECT model_id FROM box WHERE parentid = :boxId)) " +
        " OR d.id IN (SELECT doc_id FROM doc_box WHERE box_id = :boxId)";
    Object[][] docsData = SQL.select(docsQuery, formData);
    if (formData.getDocuments()!=null) {
      for (Object[] docData: docsData) {
        Long id = (Long) docData[0];
        String name = (String) docData[1];
        Document doc = new Document(id, name);
        formData.getDocuments().add(doc);
      }
    }    
    return formData;
  }

  public BoxFormData createFromKit(BoxFormData formData) {
    // TODO: Choix de langue dans la session
    String userLanguage = ServerSession.get().getSessionLanguage();
    String defaultLanguage =  ServerSession.get().getDefaultLanguage();
    
    Long kitId = formData.getKitId();
    PartFormData partData = new PartFormData();
    partData.setPartId(kitId);
    IPartService partService = BEANS.get(IPartService.class);
    partData = partService.load(partData);
    
    // Données Part => Box
    formData.getLabel().setValue(partData.getTitle().getValue());
    formData.getModel().setValue(kitId);
    formData.getColor().setValue(partData.getColor().getValue());
    
    // Contenu
    String partsQuery = "SELECT p.id, p.id, pn.year_number, COALESCE(l1.label, l2.label), 'icons/?image=' || p.ft_icon, pc.count, COALESCE (p.cost, 0.0), COALESCE (pc.count * p.cost, 0.0) " +
        " FROM part_contains pc " +
        " JOIN part p ON p.id = pc.part_id " + 
        " LEFT JOIN multilingual_label l1 ON l1.id = p.title_id AND l1.langcode = :userLanguage " +
        " LEFT JOIN multilingual_label l2 ON l2.id = p.title_id AND l2.langcode = :defaultLanguage " +
        " LEFT JOIN v_one_part_number pn ON pn.part_id = p.id " +
        " WHERE pc.container_id = :kitId " +
        " INTO :{parts.oldId}, :{parts.id}, :{parts.partNumber}, :{parts.partLabel}, :{parts.icon}, :{parts.count}, :{parts.partValue}, :{parts.value}"
    ;
    SQL.selectInto(partsQuery, 
        formData, 
        new NVPair("userLanguage", userLanguage), 
        new NVPair("kitId", kitId), 
        new NVPair("defaultLanguage", defaultLanguage));
    return formData;
  }
  
  @Override
  public BoxFormData store(BoxFormData formData) {
    if (!ACCESS.check(new UpdateBoxPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    
   
    // Box data
    SQL.update("UPDATE box SET " +
        "boxtype_id = :boxType, " +
        "parentid = :parent, " +
        "location_id = :location, " +
        "label = :label, " +
        "description = :description, " +
        "color_id = :color, " +
        "length = :length, " +
        "width = :width, " +
        "height = :height, " +
        "remarks = :remarks, " +
        "model_id = :model, " +
        "lot_achat = :boughtSet, " +
        "given = :given " +
        "WHERE id = :id", 
        formData);
    
   
    // Contenu
    storeParts(formData);
    return formData;
  }
  
  private void storeParts(BoxFormData formData) {
    // Enregistre le contenu
    Long boxId = formData.getId().getValue();
    
    PartsRowData[] partsRows = formData.getParts().getRows();
    
    for (PartsRowData partData: partsRows) {
      // Le conteneur peut être la boite ou un compartiment
      Long actualContainerId = boxId;
      Long binId = partData.getBin();
      if (binId!=null && binId!=0L) {
        actualContainerId = binId;
      }
      
      if (partData.getRowState()==AbstractTableRowData.STATUS_INSERTED) {
        Object [][] res = SQL.select("SELECT count FROM box_contains WHERE container_id = :boxId and part_id = :id", partData, new NVPair("boxId", boxId));
        if (res.length>0) {
          partData.setOldId(partData.getId());
          partData.setRowState(AbstractTableRowData.STATUS_UPDATED);
        }
      }
      switch (partData.getRowState()) {
      case AbstractTableRowData.STATUS_NON_CHANGED:
        break;
      case AbstractTableRowData.STATUS_INSERTED:
        if (isGT0(partData.getId())) {
          SQL.insert("INSERT INTO box_contains (container_id, part_id, count) VALUES (:boxId, :id, :count)", 
              partData,
              new NVPair("boxId", actualContainerId));
        }
        break;
      case AbstractTableRowData.STATUS_UPDATED:
        if (isGT0(partData.getId())) {
          SQL.update("UPDATE box_contains SET part_id = :id, count = :count WHERE container_id = :boxId AND part_id = :oldId", 
              partData,
              new NVPair("boxId", actualContainerId));
        }
        else {
          // Le part ID ou le Count a été RAZ => suppression
          SQL.delete("DELETE FROM box_contains WHERE container_id = :boxId AND part_id = :oldId", partData, new NVPair("boxId", actualContainerId));
        }
        break;
      case AbstractTableRowData.STATUS_DELETED:
        SQL.delete("DELETE FROM box_contains WHERE container_id = :boxId AND part_id = :oldId", partData, new NVPair("boxId", actualContainerId));
        break;
      }
    }
  }
  
  private boolean isGT0(Long value) {
    if (value==null) return false;
    if (value>0) return true;
    return false;
  }
  
  @Override
  public BoxFormData moveParts(BoxFormData fromBoxData, MovePartsFormData moveData, boolean removeEmptyLines) {
    Long fromBoxId = moveData.getFromBoxId().getValue();
    Long toBoxId = moveData.getToBox().getValue();
    
    // Données boîte destination
    BoxFormData toBoxData = new BoxFormData();
    toBoxData.setBoxId(toBoxId);
    load(toBoxData);
    
    if (fromBoxId != toBoxId) {
      for (MovePartsFormData.Parts.PartsRowData movedPartData: moveData.getParts().getRows()) {
        Long partId = movedPartData.getId();
        Integer count = movedPartData.getCount();
        
        // Recherche de la pièce dans les 2 boites
        for (PartsRowData partData: fromBoxData.getParts().getRows()) {
          if (partData.getId().equals(partId)) {
            int newCount = partData.getCount()-count;
            partData.setCount(newCount);
            if (removeEmptyLines && newCount==0) {
              partData.setRowState(AbstractTableRowData.STATUS_DELETED);
            }
            else {
              partData.setRowState(AbstractTableRowData.STATUS_UPDATED);
            }
            break;
          }
        }
        
        boolean partFound = false;
        for (PartsRowData partData: toBoxData.getParts().getRows()) {
          if (partData.getId().equals(partId)) {
            Integer oldCount = partData.getCount();
            if (oldCount==null) {
              oldCount = 0;
            }
            partData.setCount(oldCount+count);
            partData.setRowState(AbstractTableRowData.STATUS_UPDATED);
            partFound = true;
            break;
          }
        }
        
        // Si nouvelle pièce on l'ajoute
        if (!partFound) {
          PartsRowData partData = toBoxData.getParts().addRow();
          partData.setId(partId);
          partData.setCount(count);
          partData.setRowState(AbstractTableRowData.STATUS_INSERTED);
        }
      }
      store(toBoxData);
      store(fromBoxData);
    }
    
    return fromBoxData; 
  }
}
