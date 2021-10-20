package pers.mr.ft.inventory.client.pages;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.client.pages.LocationTablePage.Table;
import pers.mr.ft.inventory.shared.pages.ILocationService;
import pers.mr.ft.inventory.shared.pages.LocationTablePageData;

@Data(LocationTablePageData.class)
public class LocationTablePage extends AbstractPageWithTable<Table> {
  @Override
  protected String getConfiguredTitle() {
    // TODO [mreverbel] verify translation
    return TEXTS.get("LocationTablePage");
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(ILocationService.class).getLocationTableData(filter));
  }

  public class Table extends AbstractTable {

    public DescriptionColumn getDescriptionColumn() {
      return getColumnSet().getColumnByClass(DescriptionColumn.class);
    }

    public LocationColumn getLocationColumn() {
      return getColumnSet().getColumnByClass(LocationColumn.class);
    }

    public IdColumn getIdColumn() {
      return getColumnSet().getColumnByClass(IdColumn.class);
    }

    @Order(1000)
    public class IdColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Ident");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(2000)
    public class LocationColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Location");
      }

      @Override
      protected int getConfiguredWidth() {
        return 300;
      }
    }

    @Order(3000)
    public class DescriptionColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Description");
      }

      @Override
      protected int getConfiguredWidth() {
        return 400;
      }
    }
    
  }
}
