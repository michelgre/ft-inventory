package pers.mr.ft.inventory.client.pages;

import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.client.ui.messagebox.IMessageBox;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.client.ClientSession;
import pers.mr.ft.inventory.client.Desktop;
import pers.mr.ft.inventory.client.columns.AbstractIdColumn;
import pers.mr.ft.inventory.client.forms.BoxTypeForm;
import pers.mr.ft.inventory.client.forms.ReportModelForm;
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
  protected boolean getConfiguredLeaf() {
    return true;
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
    public class IdColumn extends AbstractIdColumn {
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
    @Order(1000)
    public class EditMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Editer");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        Long id = getIdColumn().getSelectedValue();
        @SuppressWarnings("unused")
        ReportModelForm form = ((Desktop) ClientSession.get().getDesktop()).findReportModelForm(id, new ReportModelFormListener());
      }
    }

    @Order(1500)
    public class CreateMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Create");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.EmptySpace, TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        ReportModelForm form = new ReportModelForm();
        form.setObjectId(0L);
        form.addFormListener(new ReportModelFormListener());
        form.startNew();
      }
    }

    @Order(2000)
    public class DeleteMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Delete");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execAction() {
        List<ITableRow> rows = getSelectedRows();
        int res = MessageBoxes.createYesNo()
                          .withHeader(TEXTS.get("TitleConfirmDelete"))
                          .withBody(TEXTS.get("ConfirmDeleteReportModel", String.valueOf(rows.size())))
                          .withYesButtonText(TEXTS.get("Delete"))
                          .show(IMessageBox.NO_OPTION);
        if (res==IMessageBox.YES_OPTION) {
          IReportModelService service = BEANS.get(IReportModelService.class);
          for (ITableRow row: rows) {
            Long id = getIdColumn().getValue(row);
            service.delete(id);
          }
          reloadPage();
        }
      }
    }

    @Override
    protected Class<? extends IMenu> getConfiguredDefaultMenu() {
      return EditMenu.class;
    }
    
    
    
  }
  private class ReportModelFormListener implements FormListener {
    @Override
    public void formChanged(FormEvent e) {
      // reload page to reflect new/changed data after saving any changes
      if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
        reloadPage();
      }
    }
  }
}
