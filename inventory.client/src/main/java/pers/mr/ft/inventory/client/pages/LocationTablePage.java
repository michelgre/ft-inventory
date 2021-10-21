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
import pers.mr.ft.inventory.client.forms.LocationForm;
import pers.mr.ft.inventory.client.pages.LocationTablePage.Table;
import pers.mr.ft.inventory.shared.pages.ILocationService;
import pers.mr.ft.inventory.shared.pages.LocationTablePageData;

@Data(LocationTablePageData.class)
public class LocationTablePage extends AbstractPageWithTable<Table> {
  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("LocationTablePage");
  }

  @Override
  protected boolean getConfiguredLeaf() {
    return true;
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
    public class IdColumn extends AbstractIdColumn {
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
        return 500;
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
        LocationForm form = ((Desktop) ClientSession.get().getDesktop()).findLocationForm(id, new LocationFormListener());
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
        LocationForm form = new LocationForm();
        form.setObjectId(0L);
        form.addFormListener(new LocationFormListener());
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
                          .withBody(TEXTS.get("ConfirmDeleteLocation", String.valueOf(rows.size())))
                          .withYesButtonText(TEXTS.get("Delete"))
                          .show(IMessageBox.NO_OPTION);
        if (res==IMessageBox.YES_OPTION) {
          ILocationService service = BEANS.get(ILocationService.class);
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
  private class LocationFormListener implements FormListener {
    @Override
    public void formChanged(FormEvent e) {
      // reload page to reflect new/changed data after saving any changes
      if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
        reloadPage();
      }
    }
  }
}
