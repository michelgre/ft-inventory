package pers.mr.ft.inventory.server.pages;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.shared.forms.BoxSearchFormData;
import pers.mr.ft.inventory.shared.forms.PartSearchFormData;
import pers.mr.ft.inventory.shared.pages.IPartsService;
import pers.mr.ft.inventory.shared.pages.PartsTablePageData;
import pers.mr.ft.inventory.shared.security.FTPrincipal;

public class PartsService implements IPartsService {
  @Override
  public PartsTablePageData getPartsTableData(SearchFilter filter, boolean buildingKits) {
    PartsTablePageData pageData = new PartsTablePageData();
    
    String userLanguage = ServerSession.get().getSessionLanguage();
    String defaultLanguage =  ServerSession.get().getDefaultLanguage();
    
    FTPrincipal principal = ServerSession.get().getPrincipal();
    Long userId = principal.getId();
    
    String cond = " WHERE 1=1 ";
    if (buildingKits) {
      cond += " AND kit_sum > 0 ";
    }
    else {
      cond += " AND kit_sum IS NULL ";
    }
    cond += " AND (inv_user_id = :userId OR inv_user_id IS NULL) ";

    // Autres filtres
    AbstractFormData fd = filter.getFormData();
    if (fd instanceof PartSearchFormData) {
      PartSearchFormData searchData = (PartSearchFormData) fd;
      
      // Label
      if (searchData.getLabel().isValueSet()) {
        String label = searchData.getLabel().getValue();
        if (label != null && label.length()>0) {
          searchData.getLabel().setValue("%" + searchData.getLabel().getValue().toUpperCase() + "%");
          cond += " AND UPPER(part_label) LIKE :{filter.label} ";
        }
      }
      
      // Part Number
      if (searchData.getPartNumber().isValueSet()) {
        String pn = searchData.getPartNumber().getValue();
        if (pn != null && pn.length()>0) {
          searchData.getPartNumber().setValue("%" + searchData.getPartNumber().getValue().toUpperCase() + "%");
          cond += " AND UPPER(part_numbers) LIKE :{filter.partNumber} ";
        }
      }
    }
    
    String invCount = "inv_sum";
    if (buildingKits) {
      invCount = "(SELECT NULLIF(COUNT(*),0) FROM box where model_id = p.id) ";
    }
    
    String query = "SELECT " + 
        " p.id, part_numbers, part_icon, color_id, part_label, default_label, ft_cat, kit_sum, cost, " + invCount + ", " +
        " (SELECT NULLIF(count(*),0) FROM doc_part WHERE part_id = id)" +
        " FROM v_parts_page p " + 
        cond +
        /*
        " p.id, part_numbers, 'icons/?image=' || p.ft_icon, p.color_id, COALESCE(l1.label, l2.label), " + 
        " l2.label, p.ft_cat, (select sum(count) from part_contains where container_id = p.id) kit_sum, " +
        " (SELECT SUM (count) FROM box_contains bc JOIN box b ON bc.container_id = b.id WHERE NOT b.lot_achat AND part_id = p.id) inv_sum " +
        " FROM part p " +
        " LEFT JOIN multilingual_label l1 ON l1.id = p.title_id AND l1.langcode = :userLanguage " +
        " LEFT JOIN multilingual_label l2 ON l2.id = p.title_id AND l2.langcode = :defaultLanguage " +
        " LEFT JOIN v_part_numbers pn ON pn.part_id = p.id " +        
         */
        " ORDER BY default_label " +
        " INTO :{page.id}, :{page.partNumber}, :{page.icon}, :{page.color}, :{page.title}, :{page.defaultTitle}, :{page.category}, :{page.partsCount}, :{page.value}, :{page.inventoryCount}, :{page.docsCount}";
    SQL.selectInto(query, 
        new NVPair("page", pageData), 
        new NVPair("userLanguage", userLanguage), 
        new NVPair("defaultLanguage", defaultLanguage),
        new NVPair("userId", userId),
        new NVPair("filter", filter.getFormData()));
    return pageData;
  }
}
