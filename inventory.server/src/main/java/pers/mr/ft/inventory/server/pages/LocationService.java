package pers.mr.ft.inventory.server.pages;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.shared.pages.ILocationService;
import pers.mr.ft.inventory.shared.pages.LocationTablePageData;
import pers.mr.ft.inventory.shared.pages.ShopTablePageData;

public class LocationService implements ILocationService {
  @Override
  public LocationTablePageData getLocationTableData(SearchFilter filter) {
    LocationTablePageData pageData = new LocationTablePageData();
    
    SQL.selectInto("SELECT id, location, description FROM Location INTO :{page.id}, :{page.location}, :{page.description}", 
        new NVPair("page", pageData));
    return pageData;
  }
}
