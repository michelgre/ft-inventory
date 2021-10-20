package pers.mr.ft.inventory.client.pages;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.client.pages.ShopTablePage.Table;
import pers.mr.ft.inventory.shared.pages.IShopService;
import pers.mr.ft.inventory.shared.pages.ShopTablePageData;

@Data(ShopTablePageData.class)
public class ShopTablePage extends AbstractPageWithTable<Table> {
  @Override
  protected String getConfiguredTitle() {
    // TODO [mreverbel] verify translation
    return TEXTS.get("ShopTablePage");
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IShopService.class).getShopTableData(filter));
  }

  public class Table extends AbstractTable {

    public QueryColumn getQueryColumn() {
      return getColumnSet().getColumnByClass(QueryColumn.class);
    }

    public LabelColumn getLabelColumn() {
      return getColumnSet().getColumnByClass(LabelColumn.class);
    }

    public IdColumn getIdColumn() {
      return getColumnSet().getColumnByClass(IdColumn.class);
    }

    @Order(1000)
    public class IdColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Id");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(2000)
    public class LabelColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Label");
      }

      @Override
      protected int getConfiguredWidth() {
        return 200;
      }
    }

    @Order(3000)
    public class QueryColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Query");
      }

      @Override
      protected int getConfiguredWidth() {
        return 400;
      }
    }
    
    
    
    
  }
}
