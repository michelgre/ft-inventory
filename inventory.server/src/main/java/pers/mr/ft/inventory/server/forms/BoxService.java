package pers.mr.ft.inventory.server.forms;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.security.ACCESS;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;

import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.shared.codetype.HistoyTypeCodeType.HistoryType;
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
      formData.setCopiedId(boxId);
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
    
    return createInternal(formData, true);
  }
  
  private BoxFormData createInternal(BoxFormData formData, boolean withParts) {
    if (!ACCESS.check(new CreateBoxPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    
    FTPrincipal principal = ServerSession.get().getPrincipal();
    Long userId = principal.getId();
    
    // Box data
    SQL.insert("INSERT INTO box (boxtype_id, parentid, location_id, label, description, color_id, length, width, height, remarks, model_id, lot_achat, given, cout_achat, isfull, user_id) " +
        " VALUES (:boxType, :parent, :location, :label, :description, :color, :length, :width, :height, :remarks, :model, :boughtSet, :given, :buyCost, :full, :userId)",
        formData, new NVPair("userId", userId));
    Object[][] rows = SQL.select("SELECT LASTVAL()");
    Long boxId = (Long) rows[0][0];
   
    formData.setBoxId(boxId);
    formData.getId().setValue(boxId);
    
    if (withParts) {
      // Contenu
      storeParts(formData, true);
    }
    
    return formData;
  }

  @Override
  public BoxFormData load(BoxFormData formData) {
    if (!ACCESS.check(new ReadBoxPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO: Choix de langue dans la session
    FTPrincipal principal = ServerSession.get().getPrincipal();
    Long userId = principal.getId();
    
    String userLanguage = ServerSession.get().getSessionLanguage();
    String defaultLanguage =  ServerSession.get().getDefaultLanguage();

    String query = "SELECT id, label, boxtype_id, description, parentid, location_id, color_id, length, width, height, remarks, model_id, lot_achat, given, cout_achat, isfull " +
        " FROM box " +
        " WHERE id = :boxId " +
        " INTO :id, :label, :boxType, :description, :parent, :location, :color, :length, :width, :height, :remarks, :model, :boughtSet, :given, :buyCost, :full ";
    SQL.selectInto(query, formData);
    
    String partsQuery = "SELECT p.id, p.id, pn.year_number, COALESCE(l1.label, l2.label), 'icons/?image=' || p.ft_icon, bc.count, pc.count, p.color_id, COALESCE (p.cost, 0.0), COALESCE (bc.count * p.cost, 0.0), bc.bin_id, pc.ftdb_count, inv_sum, bc.comment " +
        " FROM v_box_contains_with_inv bc " +
        " JOIN part p ON p.id = bc.part_id " + 
        " LEFT JOIN multilingual_label l1 ON l1.id = p.title_id AND l1.langcode = :userLanguage " +
        " LEFT JOIN multilingual_label l2 ON l2.id = p.title_id AND l2.langcode = :defaultLanguage " +
        " LEFT JOIN v_one_part_number pn ON pn.part_id = p.id " +
        " LEFT JOIN box container on container.id = bc.bin_id " +
        " LEFT JOIN part_contains pc ON pc.container_id = COALESCE(container.model_id,:model) AND p.id = pc.part_id " +
        " WHERE bc.container_id = :boxId AND (bc.inv_user_id = :userId OR bc.inv_user_id IS NULL) " +
        " INTO :{parts.oldId}, :{parts.id}, :{parts.partNumber}, :{parts.partLabel}, :{parts.icon}, :{parts.count}, :{parts.kitCount}, :{parts.color}, :{parts.partValue}, :{parts.value}, :{parts.bin}, :{parts.fTDBCount}, :{parts.inventoryCount}, :{parts.comment} "
    ;
    SQL.selectInto(partsQuery, 
        formData, 
        new NVPair("userLanguage", userLanguage), 
        new NVPair("defaultLanguage", defaultLanguage), 
        new NVPair("userId", userId));
    
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
        "cout_achat = :buyCost, " +
        "given = :given, " +
        "isfull = :full " +
        "WHERE id = :id", 
        formData);
    
    // Mise en conformité des compartiments avec la boite principale
    SQL.update("UPDATE box SET lot_achat = :boughtSet, given = :given WHERE id IN (SELECT DISTINCT bin_id FROM v_box_contains WHERE container_id = :id)", formData);
    
    // Contenu
    storeParts(formData, false);
    return formData;
  }
  
  private Long copyCompartment(Long binId, Boolean achat,  Map<Long,Long> createdCompartments) {
    // Si le compartiment a déjà été créé, on rend son Id
    if (createdCompartments.containsKey(binId)) {
      return createdCompartments.get(binId);
    }
    
    // Récupérer les données du compatiment
    BoxFormData binData = new BoxFormData();
    binData.setBoxId(binId);
    binData = load(binData);
    binData.setBoxId(0L);  // A créer
    binData.getBoughtSet().setValue(achat);// Le compartiment a le même statut que le conteneur principal

    // Y-a-t-il un compartiment parent ?
    Long parentId = binData.getParent().getValue();
    Long newParentId = 0L;
    if (parentId!=null && parentId>0) {
      // Copier le compartiment parent
      newParentId = copyCompartment(parentId, achat, createdCompartments);
    }
    
    binData.getParent().setValue(newParentId); // Boite pricipale du compatiment à créer
    binData = createInternal(binData, false);
    Long newBinId = binData.getBoxId();
    
    createdCompartments.put(binId, newBinId); // Table des compartiments copie màj
    return newBinId;
  }
  
  private void storeParts(BoxFormData formData, boolean creation) {
    // Enregistre le contenu (creation: indique si on est en création de boite, pour les compartiments)
    Long boxId = formData.getId().getValue();
    Boolean achat = formData.getBoughtSet().getValue();
    
    PartsRowData[] partsRows = formData.getParts().getRows();
    Map<Long,Long> createdBinsIds = new HashMap<>();
    
    // Boite principale si on est en train de copier
    Long copiedId = formData.getCopiedId();
    if (copiedId != null && copiedId !=0L) {
      createdBinsIds.put(copiedId, boxId);
    }
    
    for (PartsRowData partData: partsRows) {
      // Le conteneur peut être la boite ou un compartiment
      Long actualContainerId = boxId;
      Long binId = partData.getBin();  // Attention, en création il faut copier le compatiment
      if (binId!=null && binId!=0L) {
        // En création il faut copier le compatiment (et éventuellement ses parents)
        if (creation) {
          // Copier ou récupérer la copie
          binId = copyCompartment(binId, achat, createdBinsIds);
        }
        actualContainerId = binId;
      }
      
      if (partData.getRowState()==AbstractTableRowData.STATUS_INSERTED) {
        Object [][] res = SQL.select("SELECT count FROM box_contains WHERE container_id = :boxId and part_id = :id", partData, new NVPair("boxId", actualContainerId));
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
          SQL.insert("INSERT INTO box_contains (container_id, part_id, count, comment) VALUES (:boxId, :id, :count, :comment)", 
              partData,
              new NVPair("boxId", actualContainerId));
        }
        break;
      case AbstractTableRowData.STATUS_UPDATED:
        if (isGT0(partData.getId())) {
          SQL.update("UPDATE box_contains SET part_id = :id, count = :count, comment = :comment WHERE container_id = :boxId AND part_id = :oldId", 
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
    
    // Boite(s) origine
    Map<Long,BoxFormData> allFromBoxData = new HashMap<>();
    allFromBoxData.put(fromBoxId, fromBoxData);
    
    // Données boîte destination
    BoxFormData toBoxData = new BoxFormData();
    toBoxData.setBoxId(toBoxId);
    load(toBoxData);
    
    for (MovePartsFormData.Parts.PartsRowData movedPartData: moveData.getParts().getRows()) {
      Long fromBinId = movedPartData.getBin();
      Long actualFromBoxId = fromBoxId;
      BoxFormData actualFromBoxData = fromBoxData;
      if (fromBinId!=null && fromBinId!=0) {
        // La pièce est dans un compartiment, on récupère les données de celui-ci
        actualFromBoxId = fromBinId;
        actualFromBoxData = allFromBoxData.get(actualFromBoxId);
        if (actualFromBoxData==null) {
          // On ne les a pas encore, on les charge
          actualFromBoxData = new BoxFormData();
          actualFromBoxData.setBoxId(actualFromBoxId);
          load(actualFromBoxData);
          allFromBoxData.put(actualFromBoxId, actualFromBoxData);
        }
      }
      if (actualFromBoxId != toBoxId) {
        Long partId = movedPartData.getId();
        Integer count = movedPartData.getCount();
        int movedCount = 0;
        
        // Recherche de la pièce dans les 2 boites
        for (PartsRowData partData: actualFromBoxData.getParts().getRows()) {
          if (partData.getId().equals(partId) && (partData.getBin()==null || partData.getBin()==0)) {
            int newCount = partData.getCount()-count;
            movedCount = count;
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
        if (movedCount>0) { // Si on a réussi à enlever la pièce de la boite origine (pour éviter les bugs)
          for (PartsRowData partData: toBoxData.getParts().getRows()) {
            if (partData.getId().equals(partId) && (partData.getBin()==null || partData.getBin()==0)) {
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
        }
        
        // Si nouvelle pièce on l'ajoute
        if (!partFound) {
          PartsRowData partData = toBoxData.getParts().addRow();
          partData.setId(partId);
          partData.setCount(count);
          partData.setRowState(AbstractTableRowData.STATUS_INSERTED);
        }
      
        Long userId = ServerSession.get().getPrincipal().getId();
        /*
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
        Timestamp now = Timestamp.valueOf(nowUtc.toLocalDateTime());
        */
        Date now = new Date();
        HistoryType hType = HistoryType.Move;
        String info = null; // TODO ?
        Long i1 = actualFromBoxId;
        Long i2 = toBoxId;
        Long i3 = partId;
        Long i4 = count.longValue();
        SQL.insert("INSERT INTO history (user_id, ts, type, info, i1, i2, i3, i4) VALUES (:userId, :ts, :type, :info, :i1, :i2, :i3, :i4)",
            new NVPair("userId", userId),
            new NVPair("ts", now),
            new NVPair("type", hType.getCode()),
            new NVPair("info", info),
            new NVPair("i1", i1),
            new NVPair("i2", i2),
            new NVPair("i3", i3),
            new NVPair("i4", i4)
            );
      }
    }
    storeParts(toBoxData, false);
    for (BoxFormData boxData: allFromBoxData.values()) {
      storeParts(boxData, false);
    }
    
    return fromBoxData; 
  }
}
