package pers.mr.ft.inventory.server.forms;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.ImmutablePair;
import org.eclipse.scout.rt.platform.util.Pair;
import org.eclipse.scout.rt.security.ACCESS;
import org.eclipse.scout.rt.server.jdbc.SQL;

import pers.mr.ft.ftdbsync.FTDBSync;
import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.server.sql.DatabaseProperties.JdbcMappingNameProperty;
import pers.mr.ft.inventory.server.sql.DatabaseProperties.JdbcPasswordProperty;
import pers.mr.ft.inventory.server.sql.DatabaseProperties.JdbcUsernameProperty;
import pers.mr.ft.inventory.shared.common.ILabelService;
import pers.mr.ft.inventory.shared.forms.CreatePartPermission;
import pers.mr.ft.inventory.shared.forms.IPartService;
import pers.mr.ft.inventory.shared.forms.PartFormData;
import pers.mr.ft.inventory.shared.forms.ReadPartPermission;
import pers.mr.ft.inventory.shared.forms.UpdatePartPermission;
import pers.mr.ft.inventory.shared.model.Document;
import pers.mr.ft.inventory.shared.security.FTPrincipal;

public class PartService implements IPartService {
  private FTDBSync ftdbSync = null;
  
  @Override
  public PartFormData prepareCreate(PartFormData formData) {
    if (!ACCESS.check(new CreatePartPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    return formData;
  }

  @Override
  public PartFormData create(PartFormData formData) {
    if (!ACCESS.check(new CreatePartPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO [michel] add business logic here.
    return formData;
  }

  private PartFormData load(PartFormData formData, String cond) {
    // TODO: Choix de langue dans la session
    String userLanguage = ServerSession.get().getSessionLanguage();
    String defaultLanguage =  ServerSession.get().getDefaultLanguage();
    
    FTPrincipal principal = ServerSession.get().getPrincipal();
    Long userId = 0L;
    if (principal!=null) {
      userId = principal.getId();
    }
    
    // TODO icone
    // '/icons/?image=' || p.ft_icon, 
    String query = "SELECT p.id, pn.part_numbers, type_id, COALESCE(l1.label, l2.label), COALESCE(d1.label, d2.label), color_id, COALESCE(cost, 0.0), p.ft_icon, p.ft_cat, p.weight, p.ft_variant_uuid, p.rarity " +
        " FROM part p " +
        " LEFT JOIN multilingual_label l1 ON l1.id = p.title_id AND l1.langcode = :userLanguage " +
        " LEFT JOIN multilingual_label l2 ON l2.id = p.title_id AND l2.langcode = :defaultLanguage " +
        " LEFT JOIN multilingual_label d1 ON d1.id = p.description_id AND d1.langcode = :userLanguage " +
        " LEFT JOIN multilingual_label d2 ON d2.id = p.description_id AND d2.langcode = :defaultLanguage " +
        " LEFT JOIN v_part_numbers pn ON pn.part_id = p.id ";
    query = query + " WHERE " + cond;
    query = query + " INTO :id, :partNumbers, :partType, :title, :description, :color, :value, :imageId, :category, :weight, :datenbankUUID, :rarity ";
    SQL.selectInto(query, formData,
        new NVPair("userLanguage", userLanguage), 
        new NVPair("defaultLanguage", defaultLanguage));
    
    // Pièces incluses
    String partsQuery = "SELECT p.id, p.id, pn.year_number, COALESCE(l1.label, l2.label), 'icons/?image=' || p.ft_icon, pc.count, color_id, COALESCE (p.cost, 0.0), COALESCE (pc.count * p.cost, 0.0), pc.ftdb_count,pc.inv_sum " +
        " FROM v_part_contains pc " +
        " JOIN part p ON p.id = pc.part_id " + 
        " LEFT JOIN multilingual_label l1 ON l1.id = p.title_id AND l1.langcode = :userLanguage " +
        " LEFT JOIN multilingual_label l2 ON l2.id = p.title_id AND l2.langcode = :defaultLanguage " +
        " LEFT JOIN v_one_part_number pn ON pn.part_id = p.id " +
        " WHERE pc.container_id = :partId AND (pc.inv_user_id IS NULL OR pc.inv_user_id = :userId) " +
        " INTO :{parts.oldId}, :{parts.id}, :{parts.partNumber}, :{parts.partLabel}, :{parts.icon}, :{parts.count}, :{parts.color}, :{parts.partValue}, :{parts.value}, :{parts.fTDBCount}, :{parts.inventoryCount}"
    ;
    SQL.selectInto(partsQuery, 
        formData, 
        new NVPair("userLanguage", userLanguage), 
        new NVPair("defaultLanguage", defaultLanguage), 
        new NVPair("userId", userId));
    
    // Boîtes contenant
    String boxesQuery = "SELECT b.id, b.label, bc.count, b.lot_achat, b.location_id " +
        " FROM box_contains bc " +
        " JOIN box b ON b.id = bc.container_id AND b.user_id = :userId " +
        " WHERE bc.part_id = :partId " + // AND NOT b.lot_achat
        " INTO :{boxes.id}, :{boxes.label}, :{boxes.thisPartCount}, :{boxes.lotAchat}, :{boxes.location} ";
    SQL.selectInto(boxesQuery, 
        formData, 
        new NVPair("userLanguage", userLanguage), 
        new NVPair("defaultLanguage", defaultLanguage), 
        new NVPair("userId", userId));
    
    // Kits contenant
    String kitsQuery = "SELECT k.id, COALESCE(l1.label, l2.label), 'icons/?image=' || k.ft_icon, kc.count " +
        " FROM part_contains kc " +
        " JOIN part k ON k.id = kc.container_id " +
        " LEFT JOIN multilingual_label l1 ON l1.id = k.title_id AND l1.langcode = :userLanguage " +
        " LEFT JOIN multilingual_label l2 ON l2.id = k.title_id AND l2.langcode = :defaultLanguage " +
        " WHERE kc.part_id = :partId " +
        " INTO :{kits.id}, :{kits.label}, :{kits.icon}, :{kits.thisPartCount} ";
    SQL.selectInto(kitsQuery, 
        formData, 
        new NVPair("userLanguage", userLanguage), 
        new NVPair("defaultLanguage", defaultLanguage));

    // Documents
    String docsQuery = "SELECT d.id, d.title, d.language " + 
        " FROM document d " + 
        " WHERE " + 
        " d.id IN (SELECT doc_id FROM doc_part WHERE part_id = :partId) "
        ;
    formData.setDocuments(new LinkedList<Document>());
    Object[][] docsData = SQL.select(docsQuery, formData);
    for (Object[] docData: docsData) {
      Long id = (Long) docData[0];
      String name = (String) docData[1];
      String lang = (String) docData[2];
      Document doc = new Document(id, name, lang);
      formData.getDocuments().add(doc);
    }
    
    return formData;
  }
  @Override
  public PartFormData load(PartFormData formData) {
    if (!ACCESS.check(new ReadPartPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    Long partId = formData.getPartId();
    
    return load(formData, "p.id = "+partId);
  }

  @Override
  public PartFormData store(PartFormData formData) {
    if (!ACCESS.check(new UpdatePartPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    
    // TODO: Choix de langue dans la session
    String userLanguage = ServerSession.get().getSessionLanguage();

    Long partId = formData.getId().getValue();
    String query = "UPDATE part SET type_id = :partType, color_id = :color, weight = :weight, cost = :value, rarity = :rarity WHERE id = :id";
    SQL.update(query, formData);
    
    // Anciennes valeurs
    PartFormData oldFormData = new PartFormData();
    oldFormData.setPartId(partId);
    oldFormData = load(oldFormData);
    
    // Part Numbers : [year:]number [, [year:]number] ...
    String newPartNumbers = formData.getPartNumbers().getValue();
    if (newPartNumbers!=null && !newPartNumbers.equals(oldFormData.getPartNumbers().getValue())) {
      boolean valueOk = true;
      List<Pair<String,String>> numberList = new LinkedList<>();
      Pattern ynPattern = Pattern.compile("((\\d{4})\\s*:\\s*)?\\s*(\\d+)");
      String partNumbers = newPartNumbers;
      for (String yearAndNumber : partNumbers.split(",")) {
        yearAndNumber = yearAndNumber.trim();
        if (yearAndNumber.length()>0) {
          Matcher m = ynPattern.matcher(yearAndNumber);
          if (m.matches()) {
            String year = m.group(2);
            String number = m.group(3);
            numberList.add(new ImmutablePair<String,String>(year, number));
          }
          else {
            valueOk = false;
            break;
          }
        }
      }
      
      if (valueOk) {
        SQL.delete("DELETE FROM part_number WHERE part_id = :partId", new NVPair("partId", partId));
        for (Pair<String,String> pair: numberList) {
          SQL.insert("INSERT INTO part_number (part_id, year, number) VALUES (:partId, :year, :number)", 
              new NVPair("partId", partId),
              new NVPair("year", pair.getLeft()),
              new NVPair("number", pair.getRight()));
        }
      }
      else {
        throw new VetoException ("Erreur dans les n° de pièce");
      }
    }
    
    // Libellés
    ILabelService labelService = BEANS.get(ILabelService.class);
    Object[][] rows = SQL.select("SELECT title_id, description_id FROM part WHERE id = :partId", new NVPair("partId", partId));
    Long titleId = (Long) rows[0][0];
    Long descriptionId = (Long) rows[0][1];
    String newTitle = formData.getTitle().getValue();
    if (newTitle!=null && !newTitle.equals(oldFormData.getTitle().getValue())) {
      Long newLabelId = labelService.save(titleId, userLanguage, newTitle);
      if (newLabelId!=titleId) {
        SQL.update("UPDATE part SET title_id = :newLabelId WHERE id = :partId", 
            new NVPair("newLabelId", newLabelId), new NVPair("partId", partId));
      }
    }
    String newDescription = formData.getDescription().getValue();
    if (newDescription!=null && !newDescription.equals(oldFormData.getDescription().getValue())) {
      Long newLabelId = labelService.save(descriptionId, userLanguage, newDescription);
      if (newLabelId!=descriptionId) {
        SQL.update("UPDATE part SET description_id = :newLabelId WHERE id = :partId", 
            new NVPair("newLabelId", newLabelId), new NVPair("partId", partId));
      }
    }
    return formData;
  }
  
  @Override
  public PartFormData loadByNumber(String partNumber) {
    if (!ACCESS.check(new ReadPartPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    String yearCond = "";
    partNumber = partNumber.trim();
    Pattern pnPattern = Pattern.compile("((\\d+)\\s*:)?\\s*([\\da-z]+)", Pattern.CASE_INSENSITIVE);
    Matcher m = pnPattern.matcher(partNumber);
    if (m.matches()) {
      if (m.group(2)!=null) {
        partNumber = m.group(3).toUpperCase();
        yearCond = " AND year = '"+m.group(2) + "' ";
      }
    }
    PartFormData formData = new PartFormData();
    return load(formData, "p.id = (SELECT part_id FROM part_number WHERE UPPER(number) = '"+partNumber+"'"+yearCond + " ORDER BY YEAR LIMIT 1)");
  }

  @Override
  public void syncFromDatenbank(Long partId) {
    if (ftdbSync==null) {
      String dbUrl = CONFIG.getPropertyValue(JdbcMappingNameProperty.class);
      String dbUser = CONFIG.getPropertyValue(JdbcUsernameProperty.class);
      String dbPwd = CONFIG.getPropertyValue(JdbcPasswordProperty.class);
      ftdbSync = new FTDBSync(dbUrl)
          .withDbUser(dbUser)
          .withDbPwd(dbPwd)
          .withDoParts(true)
          .withDoImages(true);
      
    }
    ftdbSync.synchronizePart(partId.intValue());
  }

  @Override
  public void syncCategoryFromDatenbank(Integer catId) {
    if (ftdbSync==null) {
      String dbUrl = CONFIG.getPropertyValue(JdbcMappingNameProperty.class);
      String dbUser = CONFIG.getPropertyValue(JdbcUsernameProperty.class);
      String dbPwd = CONFIG.getPropertyValue(JdbcPasswordProperty.class);
      ftdbSync = new FTDBSync(dbUrl)
          .withDbUser(dbUser)
          .withDbPwd(dbPwd)
          .withDoParts(true)
          .withDoImages(true);
      
    }
    ftdbSync.synchronizeCategory(catId);
  }

  @Override
  public void syncImagesFromDatenbank() {
    String dbUrl = CONFIG.getPropertyValue(JdbcMappingNameProperty.class);
    String dbUser = CONFIG.getPropertyValue(JdbcUsernameProperty.class);
    String dbPwd = CONFIG.getPropertyValue(JdbcPasswordProperty.class);
    ftdbSync = new FTDBSync(dbUrl)
        .withDbUser(dbUser)
        .withDbPwd(dbPwd);
    ftdbSync.synchronizeImages();
  }

  @Override
  public void syncImagesFromDatenbank(Long partId) {
    String dbUrl = CONFIG.getPropertyValue(JdbcMappingNameProperty.class);
    String dbUser = CONFIG.getPropertyValue(JdbcUsernameProperty.class);
    String dbPwd = CONFIG.getPropertyValue(JdbcPasswordProperty.class);
    ftdbSync = new FTDBSync(dbUrl)
        .withDbUser(dbUser)
        .withDbPwd(dbPwd);
    ftdbSync.synchronizeImages(partId.intValue());
  }

  @Override
  public List<Long> getBoxesFromModel(Long modelId) {
    List<Long> boxesIds = new LinkedList<>();
    
    Object[][] data = SQL.select("SELECT id FROM box WHERE model_id = :modelId", new NVPair("modelId", modelId));
    for (Object[] row: data) {
      Long boxId = (Long) row[0];
      if (boxId != null) {
        boxesIds.add(boxId);
      }
    }
    return boxesIds;
  }
}
