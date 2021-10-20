package pers.mr.ft.inventory.client.pages;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.client.pages.ReportModelTablePage.Table;
import pers.mr.ft.inventory.shared.pages.IReportModelService;
import pers.mr.ft.inventory.shared.pages.ReportModelTablePageData;

@Data(ReportModelTablePageData.class)
public class ReportModelTablePage extends AbstractPageWithTable<Table> {
  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("ReportModelTablePage");
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IReportModelService.class).getReportTableData(null, filter));
  }

  public class Table extends AbstractTable {

    public StyleSheetColumn getStyleSheetColumn() {
      return getColumnSet().getColumnByClass(StyleSheetColumn.class);
    }

    public TypeColumn getTypeColumn() {
      return getColumnSet().getColumnByClass(TypeColumn.class);
    }

    public NameColumn getNameColumn() {
      return getColumnSet().getColumnByClass(NameColumn.class);
    }

    public IdColumn getIdColumn() {
      return getColumnSet().getColumnByClass(IdColumn.class);
    }

    @Order(1000)
    public class IdColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Id");
      }

      @Override
      protected int getConfiguredWidth() {
        return 75;
      }
    }


    @Order(1500)
    public class TypeColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Type");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    
    @Order(2000)
    public class NameColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Name");
      }

      @Override
      protected int getConfiguredWidth() {
        return 200;
      }
    }

    @Order(3000)
    public class StyleSheetColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("StyleSheet");
      }

      @Override
      protected int getConfiguredWidth() {
        return 200;
      }
    }

  }
}
