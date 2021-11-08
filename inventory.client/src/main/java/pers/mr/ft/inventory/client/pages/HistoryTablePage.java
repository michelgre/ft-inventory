package pers.mr.ft.inventory.client.pages;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDateColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.client.columns.AbstractIdColumn;
import pers.mr.ft.inventory.client.columns.AbstractPartIconColumn;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table.DateColumn;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table.I1Column;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table.I2Column;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table.I3Column;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table.I4Column;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table.IdColumn;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table.InfoColumn;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table.S1Column;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table.S2Column;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table.TypeColumn;
import pers.mr.ft.inventory.client.pages.HistoryTablePage.Table.UserColumn;
import pers.mr.ft.inventory.shared.codetype.HistoyTypeCodeType;
import pers.mr.ft.inventory.shared.pages.HistoryTablePageData;
import pers.mr.ft.inventory.shared.pages.IHistoryService;

@Data(HistoryTablePageData.class)
public class HistoryTablePage extends AbstractPageWithTable<Table> {
  @Override
  protected boolean getConfiguredLeaf() {
    return true;
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IHistoryService.class).getHistoryTableData(filter));
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("History");
  }

  public class Table extends AbstractTable {

    @Override
    protected String getConfiguredCssClass() {
      return "parts-page";
    }
    
    public DateColumn getDateColumn() {
      return getColumnSet().getColumnByClass(DateColumn.class);
    }

    public I1Column getI1Column() {
      return getColumnSet().getColumnByClass(I1Column.class);
    }

    public I2Column getI2Column() {
      return getColumnSet().getColumnByClass(I2Column.class);
    }

    public I3Column getI3Column() {
      return getColumnSet().getColumnByClass(I3Column.class);
    }

    public I4Column getI4Column() {
      return getColumnSet().getColumnByClass(I4Column.class);
    }

    public S2Column getS2Column() {
      return getColumnSet().getColumnByClass(S2Column.class);
    }

    public InfoColumn getInfoColumn() {
      return getColumnSet().getColumnByClass(InfoColumn.class);
    }

    public UserColumn getUserColumn() {
      return getColumnSet().getColumnByClass(UserColumn.class);
    }

    public S1Column getS1Column() {
      return getColumnSet().getColumnByClass(S1Column.class);
    }

    public TypeColumn getTypeColumn() {
      return getColumnSet().getColumnByClass(TypeColumn.class);
    }

    public IdColumn getIdColumn() {
      return getColumnSet().getColumnByClass(IdColumn.class);
    }

    @Order(1000)
    public class IdColumn extends AbstractIdColumn {
    }

    @Order(2000)
    public class DateColumn extends AbstractDateColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Date");
      }

      @Override
      protected int getConfiguredWidth() {
        return 135;
      }
      
      @Override
      protected String getConfiguredFormat() {
        return "YYYY-MM-dd HH:mm:ss";
      };
    }

    @Order(3000)
    public class TypeColumn extends AbstractSmartColumn<String> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("HistoryType");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
      @Override
      protected Class<? extends ICodeType<?, String>> getConfiguredCodeType() {
        return HistoyTypeCodeType.class;
      }
    }

    @Order(4000)
    public class I1Column extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return "I1";
      }

      @Override
      protected int getConfiguredWidth() {
        return 70;
      }
    }

    @Order(5000)
    public class I2Column extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return "I2";
      }

      @Override
      protected int getConfiguredWidth() {
        return 70;
      }
    }

    @Order(6000)
    public class I3Column extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return "I3";
      }

      @Override
      protected int getConfiguredWidth() {
        return 70;
      }
    }

    @Order(7000)
    public class I4Column extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return "I4";
      }

      @Override
      protected int getConfiguredWidth() {
        return 70;
      }
    }

    @Order(8000)
    public class S1Column extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return "S1";
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(9000)
    public class S2Column extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return "S2";
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(9500)
    public class IconColumn extends AbstractPartIconColumn {
    }
    
    @Order(10000)
    public class InfoColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Info");
      }

      @Override
      protected int getConfiguredWidth() {
        return 380;
      }
      @Override
      protected boolean getConfiguredTextWrap() {
        return true;
      }
    }

    @Order(11000)
    public class UserColumn extends AbstractSmartColumn<Long> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("User");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
      
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }
    
    
  }
}
