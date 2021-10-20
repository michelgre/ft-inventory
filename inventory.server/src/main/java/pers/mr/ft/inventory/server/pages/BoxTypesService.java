package pers.mr.ft.inventory.server.pages;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.shared.pages.BoxTypesTablePageData;
import pers.mr.ft.inventory.shared.pages.IBoxTypesService;

public class BoxTypesService implements IBoxTypesService {
  @Override
  public BoxTypesTablePageData getBoxTypesTableData(SearchFilter filter) {
    BoxTypesTablePageData pageData = new BoxTypesTablePageData();
    
    String query = 
         "SELECT id, description, length, width, height, part_id " +
         " FROM boxtype " +
         " INTO :{page.id}, :{page.description}, :{page.length}, :{page.width}, :{page.height}, :{page.part}"
         ;
    SQL.select(query, new NVPair("page", pageData));
    return pageData;
  }
  
  @Override
  public void delete(Long boxTypeId) {
    if (boxTypeId > 0) {
      SQL.delete("DELETE from boxtype WHERE id = :boxTypeId", new NVPair("boxTypeId", boxTypeId));
    }
  }


}
