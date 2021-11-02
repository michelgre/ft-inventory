package pers.mr.ft.inventory.server.pages;

import org.eclipse.scout.rt.platform.exception.PlatformException;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pers.mr.ft.inventory.shared.pages.ILocationService;
import pers.mr.ft.inventory.shared.pages.LocationTablePageData;

public class LocationService implements ILocationService {
  private static final Logger logger = LoggerFactory.getLogger(LocationService.class);
  @Override
  public LocationTablePageData getLocationTableData(SearchFilter filter) {
    LocationTablePageData pageData = new LocationTablePageData();
    
    SQL.selectInto("SELECT id, location, description, (SELECT count(*) FROM box WHERE location_id = loc.id) FROM Location loc INTO :{page.id}, :{page.location}, :{page.description}, :{page.boxCount}", 
        new NVPair("page", pageData));
    return pageData;
  }

  @Override
  public void delete(Long id) {
    if (id > 0) {
      try {
        SQL.delete("DELETE from location WHERE id = :id", new NVPair("id", id));
      }
      catch (PlatformException e) {
        logger.info("Delete Location "+id+": "+e.getMessage());
        Throwable cause = e.getCause();
        String errorMessage = cause.getMessage();
        if ("ERROR: row is used".equals(errorMessage)) {
          throw new VetoException(TEXTS.get("ErrorLocationUsed"));
        }
        else {
          throw new VetoException(errorMessage);
        }
      }
    }
  }
}
