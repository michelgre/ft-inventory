package pers.mr.ft.inventory.server.pages;

import org.eclipse.scout.rt.platform.exception.PlatformException;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pers.mr.ft.inventory.shared.pages.BoxTypesTablePageData;
import pers.mr.ft.inventory.shared.pages.IBoxTypesService;

public class BoxTypesService implements IBoxTypesService {
  private static final Logger logger = LoggerFactory.getLogger(BoxTypesService.class);
  @Override
  public BoxTypesTablePageData getBoxTypesTableData(SearchFilter filter) {
    BoxTypesTablePageData pageData = new BoxTypesTablePageData();
    
    String query = 
         "SELECT bt.id, description, length, width, height, part_id, (SELECT count(*) FROM box WHERE boxtype_id = bt.id) " +
         " FROM boxtype bt" +
         " INTO :{page.id}, :{page.description}, :{page.length}, :{page.width}, :{page.height}, :{page.part}, :{page.boxCount}"
         ;
    SQL.select(query, new NVPair("page", pageData));
    return pageData;
  }
  
  @Override
  public void delete(Long boxTypeId) {
    if (boxTypeId > 0) {
      try {
        SQL.delete("DELETE from boxtype WHERE id = :boxTypeId", new NVPair("boxTypeId", boxTypeId));
      }
      catch (PlatformException e) {
        logger.info("Delete Location "+boxTypeId+": "+e.getMessage());
        Throwable cause = e.getCause();
        String errorMessage = cause.getMessage();
        if ("ERROR: row is used".equals(errorMessage)) {
          throw new VetoException(TEXTS.get("ErrorBoxTypeUsed"));
        }
        else {
          throw new VetoException(errorMessage);
        }
      }

    }
  }


}
