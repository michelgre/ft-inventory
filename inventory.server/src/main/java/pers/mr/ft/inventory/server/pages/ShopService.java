package pers.mr.ft.inventory.server.pages;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.shared.pages.IShopService;
import pers.mr.ft.inventory.shared.pages.ShopTablePageData;

public class ShopService implements IShopService {
  @Override
  public ShopTablePageData getShopTableData(SearchFilter filter) {
    ShopTablePageData pageData = new ShopTablePageData();
    
    SQL.selectInto("SELECT id, label, query FROM Shop INTO :{page.id}, :{page.label}, :{page.query}", 
        new NVPair("page", pageData));
    return pageData;
  }
  @Override
  public void delete(Long id) {
    if (id > 0) {
      SQL.delete("DELETE from shop WHERE id = :id", new NVPair("id", id));
    }
  }
}
