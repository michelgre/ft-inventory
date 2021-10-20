package pers.mr.ft.inventory.server.pages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.shared.forms.BoxSearchFormData;
import pers.mr.ft.inventory.shared.pages.BoxTablePageData;
import pers.mr.ft.inventory.shared.pages.IBoxService;
import pers.mr.ft.inventory.shared.security.FTPrincipal;

public class BoxService implements IBoxService {
  @Override
  public BoxTablePageData getBoxTableData(SearchFilter filter, Long parentId, Boolean lotAchat, Boolean given) {
    BoxTablePageData pageData = new BoxTablePageData();
    FTPrincipal principal = ServerSession.get().getPrincipal();
    String cond = "";
    
    // Lot ou rangement ?
    if (lotAchat != null && lotAchat) {
      cond = "WHERE lot_achat ";
    }
    else {
      cond = "WHERE (lot_achat is null or not lot_achat)";
    }
    
    // Donné ou pas
    if (given!=null) {
      if (given) {
        cond += " AND (given)";
      }
      else {
        cond += " AND (given is NULL or NOT given)";
      }
    }
    
    // Propriétaire
    if (principal!=null) {
      cond += " AND user_id = "+principal.getId();
    }
    
    // Parent
    if (parentId != null) {
      cond += " AND parentid = " + parentId;
    }
    
    // Autres filtres
    AbstractFormData fd = filter.getFormData();
    if (fd instanceof BoxSearchFormData) {
      BoxSearchFormData boxSearchData = (BoxSearchFormData) fd;
      
      // Id
      if (boxSearchData.getId().isValueSet() && boxSearchData.getId().getValue()!= null && boxSearchData.getId().getValue()>0) {
        cond += " AND b.id = :{filter.id} ";
      }
      
      // Label
      if (boxSearchData.getLabel().isValueSet()) {
        String label = boxSearchData.getLabel().getValue();
        if (label != null && label.length()>0) {
          boxSearchData.getLabel().setValue("%" + boxSearchData.getLabel().getValue().toUpperCase() + "%");
          cond += " AND UPPER(label) LIKE :{filter.label} ";
        }
      }
      
      // Location
      if (boxSearchData.getLocation().isValueSet()) {
        Long idLocation = boxSearchData.getLocation().getValue();
        if (idLocation != null && idLocation > 0L) {
          cond += " AND location_id = :{filter.location} ";
        }
        else if (idLocation != null && idLocation == 0L) {
          cond += " AND  (location_id = 0 OR location_id IS NULL) ";
        }
      }
      
      // Type
      if (boxSearchData.getBoxType().isValueSet()) {
        Long idBoxType = boxSearchData.getBoxType().getValue();
        if (idBoxType != null && idBoxType>0) {
          cond += " AND boxtype_id = :{filter.boxType} ";
        }
      }
      
      // Inclus ?
      if (boxSearchData.getMainBox().isValueSet()) {
        Boolean isMainbox = boxSearchData.getMainBox().getValue();
        if (isMainbox != null) {
          if (isMainbox) {
            cond += " AND (parentid IS NULL OR parentid = 0) ";
          }
          else {
            cond += " AND parentid > 0 ";
          }
        }
      }
      
    }
    
    String query = "SELECT " + 
        "id, " + 
        "label, " +
        "(SELECT SUM (count) FROM v_box_contains WHERE container_id = b.id) part_count, " + 
        "(SELECT COUNT(part_id) from v_box_contains WHERE container_id = b.id), " +
        "(SELECT SUM (count * COALESCE(cost, 0.0)) FROM v_box_contains bc LEFT JOIN part p ON bc.part_id = p.id WHERE container_id = b.id) , " +
        "location_id, model_id, parentid, " + 
        "length, width, height, description, boxtype_id, " +
        "given " +
        "FROM box b " + 
        cond +
        "INTO :{page.id}, :{page.label}, :{page.partCount}, :{page.diffPartCount}, :{page.value}, :{page.location}, :{page.model}, :{page.includedIn}, :{page.length}, :{page.width}, :{page.height}, :{page.description}, :{page.boxType}, :{page.given}";
    SQL.selectInto(query, new NVPair("page", pageData), new NVPair("filter", filter.getFormData()));
    return pageData;
  }

  @Override
  public void delete(Long boxId) {
    if (boxId > 0) {
      // Les contenus
      SQL.delete("DELETE from box_contains WHERE container_id = :boxId", new NVPair("boxId", boxId));
      
      // La boite
      SQL.delete("DELETE from box WHERE id = :boxId", new NVPair("boxId", boxId));
    }
  }

  @Override
  public boolean hasBins(Long boxId) {
    Map<String,Long> results = new HashMap<>();
    results.put("binCount", 0L);
        // SQL.selectInto("SELECT count(*) FROM v_box_contains WHERE container_id = :boxId AND bin_id > 0 INTO :binCount", 
        SQL.selectInto("SELECT count(*) FROM box WHERE parentid = :boxId INTO :binCount", 
        new NVPair("boxId", boxId),
        results);
    return results.get("binCount") > 0;
  }

  @Override
  public boolean hasSubBins(Long boxId) {
    Map<String,Long> results = new HashMap<>();
    results.put("binCount", 0L);
    SQL.selectInto("SELECT count(*) FROM v_box_contains c1 JOIN v_box_contains c2 ON c2.container_id = c1.bin_id WHERE c1.container_id = :boxId AND c2.bin_id > 0 INTO :binCount", 
        new NVPair("boxId", boxId),
        results);
    return results.get("binCount") > 0;
  }

  @Override
  public void moveToLocation(Long locationId, Long boxId) {
    SQL.update("UPDATE box SET location_id = :locationId WHERE id = :boxId" , 
        new NVPair("boxId", boxId),
        new NVPair("locationId", locationId)
        );
    
  }
}
