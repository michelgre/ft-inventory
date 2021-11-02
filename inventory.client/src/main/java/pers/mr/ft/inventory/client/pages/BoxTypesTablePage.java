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
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIntegerColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
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
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import pers.mr.ft.inventory.client.ClientSession;
import pers.mr.ft.inventory.client.Desktop;
import pers.mr.ft.inventory.client.columns.AbstractHeightColumn;
import pers.mr.ft.inventory.client.columns.AbstractIdColumn;
import pers.mr.ft.inventory.client.columns.AbstractLabelColumn;
import pers.mr.ft.inventory.client.columns.AbstractLengthColumn;
import pers.mr.ft.inventory.client.columns.AbstractWidthColumn;
import pers.mr.ft.inventory.client.forms.BoxTypeForm;
import pers.mr.ft.inventory.client.pages.BoxTypesTablePage.Table;
import pers.mr.ft.inventory.shared.lookup.PartLookupCall;
import pers.mr.ft.inventory.shared.pages.BoxTypesTablePageData;
import pers.mr.ft.inventory.shared.pages.IBoxTypesService;

@Data(BoxTypesTablePageData.class)
public class BoxTypesTablePage extends AbstractPageWithTable<Table> {
  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("BoxTypes");
  }

  @Override
	protected boolean getConfiguredLeaf() {
    return true;
  }
  
  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IBoxTypesService.class).getBoxTypesTableData(filter));
  }

  public class Table extends AbstractTable {

    public HeightColumn getHeightColumn() {
      return getColumnSet().getColumnByClass(HeightColumn.class);
    }

    public BoxCountColumn getBoxCountColumn() {
      return getColumnSet().getColumnByClass(BoxCountColumn.class);
    }

    public PartColumn getPartColumn() {
      return getColumnSet().getColumnByClass(PartColumn.class);
    }

    public WidthColumn getWidthColumn() {
      return getColumnSet().getColumnByClass(WidthColumn.class);
    }

    public LengthColumn getLengthColumn() {
      return getColumnSet().getColumnByClass(LengthColumn.class);
    }

    public DescriptionColumn getDescriptionColumn() {
      return getColumnSet().getColumnByClass(DescriptionColumn.class);
    }

    public IdColumn getIdColumn() {
      return getColumnSet().getColumnByClass(IdColumn.class);
    }

    @Order(1000)
    public class IdColumn extends AbstractIdColumn {
    }

    @Order(2000)
    public class DescriptionColumn extends AbstractLabelColumn {
    }

    @Order(3000)
    public class LengthColumn extends AbstractLengthColumn {
    }

    @Order(4000)
    public class WidthColumn extends AbstractWidthColumn {
    }

    @Order(5000)
    public class HeightColumn extends AbstractHeightColumn {
    }

    @Order(6000)
    public class PartColumn extends AbstractSmartColumn<Long> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Part");
      }

      @Override
      protected int getConfiguredWidth() {
        return 280;
      }
      @Override
      protected Class<? extends ILookupCall<Long>> getConfiguredLookupCall() {
        return PartLookupCall.class;
      }
    }

    @Order(7000)
    public class BoxCountColumn extends AbstractIntegerColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("BoxCount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 75;
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
        Long boxId = getIdColumn().getSelectedValue();
        @SuppressWarnings("unused")
        BoxTypeForm form = ((Desktop) ClientSession.get().getDesktop()).findBoxTypeForm(boxId, new BoxTypeFormListener());
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
        BoxTypeForm form = new BoxTypeForm();
        form.setBoxTypeId(0L);
        form.addFormListener(new BoxTypeFormListener());
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
                          .withBody(TEXTS.get("ConfirmDeleteBoxTypes", String.valueOf(rows.size())))
                          .withYesButtonText(TEXTS.get("Delete"))
                          .show(IMessageBox.NO_OPTION);
        if (res==IMessageBox.YES_OPTION) {
          IBoxTypesService service = BEANS.get(IBoxTypesService.class);
          for (ITableRow row: rows) {
            Long boxTypeId = getIdColumn().getValue(row);
            service.delete(boxTypeId);
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
  private class BoxTypeFormListener implements FormListener {
    @Override
    public void formChanged(FormEvent e) {
      // reload page to reflect new/changed data after saving any changes
      if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
        reloadPage();
      }
    }
  }
}
