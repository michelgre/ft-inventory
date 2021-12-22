package pers.mr.ft.inventory.client.pages;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.keystroke.AbstractKeyStroke;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.action.keystroke.KeyStroke;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDecimalColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIntegerColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.ISearchForm;
import org.eclipse.scout.rt.client.ui.dnd.IDNDSupport;
import org.eclipse.scout.rt.client.ui.dnd.ResourceListTransferObject;
import org.eclipse.scout.rt.client.ui.dnd.TransferObject;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.client.ui.form.fields.decimalfield.IDecimalField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.client.ClientSession;
import pers.mr.ft.inventory.client.Desktop;
import pers.mr.ft.inventory.client.columns.AbstractIdColumn;
import pers.mr.ft.inventory.client.columns.AbstractLabelColumn;
import pers.mr.ft.inventory.client.columns.AbstractPartIconColumn;
import pers.mr.ft.inventory.client.columns.AbstractPartNumberColumn;
import pers.mr.ft.inventory.client.forms.BoxForm;
import pers.mr.ft.inventory.client.forms.DocumentForm;
import pers.mr.ft.inventory.client.forms.PartForm;
import pers.mr.ft.inventory.client.forms.PartSearchForm;
import pers.mr.ft.inventory.client.pages.PartsTablePage.Table;
import pers.mr.ft.inventory.shared.codetype.CategoryCodeType;
import pers.mr.ft.inventory.shared.codetype.ColorCodeType;
import pers.mr.ft.inventory.shared.forms.IPartService;
import pers.mr.ft.inventory.shared.pages.IPartsService;
import pers.mr.ft.inventory.shared.pages.PartsTablePageData;

@Data(PartsTablePageData.class)
public class PartsTablePage extends AbstractPageWithTable<Table> {
  private boolean buildingKits = false;
  
  public boolean isBuildingKits() {
    return buildingKits;
  }

  public PartsTablePage withBuildingKits(boolean buildingKits) {
    this.buildingKits = buildingKits;
    return this;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Parts");
  }

  @Override
  protected Class<? extends ISearchForm> getConfiguredSearchForm() {
    return PartSearchForm.class;
  }
  
  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IPartsService.class).getPartsTableData(filter, buildingKits));
  }

  public class Table extends AbstractTable {

    @Override
    protected String getConfiguredCssClass() {
      return "parts-page";
    }
    
    public IconColumn getIconColumn() {
      return getColumnSet().getColumnByClass(IconColumn.class);
    }

    public TitleColumn getTitleColumn() {
      return getColumnSet().getColumnByClass(TitleColumn.class);
    }

    public ColorColumn getColorColumn() {
      return getColumnSet().getColumnByClass(ColorColumn.class);
    }

    public DefaultTitleColumn getDefaultTitleColumn() {
      return getColumnSet().getColumnByClass(DefaultTitleColumn.class);
    }

    public CategoryColumn getCategoryColumn() {
      return getColumnSet().getColumnByClass(CategoryColumn.class);
    }

    public PartsCountColumn getPartsCountColumn() {
      return getColumnSet().getColumnByClass(PartsCountColumn.class);
    }

    public InventoryCountColumn getInventoryCountColumn() {
      return getColumnSet().getColumnByClass(InventoryCountColumn.class);
    }

    public DocsCountColumn getDocsCountColumn() {
      return getColumnSet().getColumnByClass(DocsCountColumn.class);
    }

    public ValueColumn getValueColumn() {
      return getColumnSet().getColumnByClass(ValueColumn.class);
    }

    public PartNumberColumn getPartNumberColumn() {
      return getColumnSet().getColumnByClass(PartNumberColumn.class);
    }

    public IdColumn getIdColumn() {
      return getColumnSet().getColumnByClass(IdColumn.class);
    }

    @Order(1000)
    public class IdColumn extends AbstractIdColumn {
    }

    @Order(2000)
    public class PartNumberColumn extends AbstractPartNumberColumn {
    }

    @Order(3000)
    public class IconColumn extends AbstractPartIconColumn {
    }

    @Order(4000)
    public class TitleColumn extends AbstractLabelColumn {
    }

    @Order(4500)
    public class DefaultTitleColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("DefaultTitle");
      }

      @Override
      protected int getConfiguredWidth() {
        return 300;
      }
      
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }

    
    @Order(5000)
    public class ColorColumn extends AbstractSmartColumn<Long> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Color");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
      @Override
      protected Class<? extends ICodeType<String, Long>> getConfiguredCodeType() {
        return ColorCodeType.class;
      }
    }

    @Order(6000)
    public class CategoryColumn extends AbstractSmartColumn<Long> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Category");
      }

      @Override
      protected int getConfiguredWidth() {
        return 200;
      }
      @Override
      protected Class<? extends ICodeType<String, Long>> getConfiguredCodeType() {
        return CategoryCodeType.class;
      }
    }

    @Order(7000)
    public class PartsCountColumn extends AbstractIntegerColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("PartsCount");
      }

      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
      @Override
      protected int getConfiguredWidth() {
        return 82;
      }
    }

    @Order(8000)
    public class InventoryCountColumn extends AbstractIntegerColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("InventoryCount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 75;
      }
    }

    @Order(8500)
    public class ValueColumn extends AbstractDecimalColumn<Double> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Value");
      }

      @Override
      protected int getConfiguredWidth() {
        return 75;
      }
      @Override
      protected Double getConfiguredMinValue() {
        return 0.0;
      }

      @Override
      protected Double getConfiguredMaxValue() {
        return 99999999.99;
      }

      @Override
      protected IDecimalField<Double> createDefaultEditor() {
        return null;
      }
      
      @Override
      protected boolean getConfiguredVisible() {
        return false;
      }
    }

    
    @Order(9000)
    public class DocsCountColumn extends AbstractIntegerColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Docs");
      }

      @Override
      protected int getConfiguredWidth() {
        return 50;
      }
    }

    
    
    // =============================================================================
    // Menus
    // ----------------------------
    @Override
    protected Class<? extends IMenu> getConfiguredDefaultMenu() {
      return EditMenu.class;
    }
    
    @Order(1000)
    public class EditMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("EditPart");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execAction() {
        PartForm form = new PartForm();
        form.setPartId(getIdColumn().getSelectedValue());
        form.addFormListener(new PartFormListener());
        form.startModify();
      }
    }

    @Order(2000)
    public class CreateBoxMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("CreateBox");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        BoxForm form = new BoxForm();
        form.setKitId(getIdColumn().getSelectedValue());
        form.setLotAchat(false);
        form.startNew();
      }
    }

    @Order(3000)
    public class OpenBoxesMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("OpenBoxes");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        IPartService service = BEANS.get(IPartService.class);
        List<Long> boxesIds = service.getBoxesFromModel(getIdColumn().getSelectedValue());
        for (Long boxId: boxesIds) {
          ((Desktop) ClientSession.get().getDesktop()).findBoxForm(boxId, null);        
        }
      }
    }

    
    
    @Order(4000)
    public class DatenbankMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("GotoDatenbank");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        ClientSessionProvider.currentSession().getDesktop().openUri("https://ft-datenbank.de/ft-article/"+getIdColumn().getSelectedValue(), OpenUriAction.NEW_WINDOW);
      }
    }

    @Order(5000)
    public class SyncPartsMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("SyncParts");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execAction() {
        IPartService service = BEANS.get(IPartService.class);
        List<ITableRow> rows = getSelectedRows();
        for (ITableRow row: rows) {
          Long partId = getIdColumn().getValue(row);
          service.syncFromDatenbank(partId);
        }
        reloadPage();
      }
    }

    @Order(6000)
    public class SyncImagesMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("SyncImages");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
      }

      @Override
      protected void execAction() {
        IPartService service = BEANS.get(IPartService.class);
        List<ITableRow> rows = getSelectedRows();
        for (ITableRow row: rows) {
          Long partId = getIdColumn().getValue(row);
          service.syncImagesFromDatenbank(partId);
        }
        reloadPage();
      }
    }
    
    

    @Order(1000)
    public class CopyKeyStroke extends AbstractKeyStroke {
      @Override
      protected String getConfiguredKeyStroke() {
        return KeyStroke.combineKeyStrokes(IKeyStroke.CONTROL, "C");
      }

      @Override
      protected void execAction() {
        /*
        String pn = getPartNumberColumn().getSelectedValue();
        if (pn!=null) {
          BEANS.get(IClipboardService.class).setTextContents(pn);
        }
        */
        List<Long> parts = getIdColumn().getSelectedValues();
        ((Desktop) ClientSession.get().getDesktop()).copyPartsClipboard(parts, true);
      }
    }
 
    
    @Override
    protected void execRowsSelected(List<? extends ITableRow> rows) {
      super.execRowsSelected(rows);
      
      List<ITableRow> sel = getSelectedRows();
      if (sel.size()==1) {
        ITableRow row = sel.get(0);
        Integer partsCount = getPartsCountColumn().getValue(row);
        boolean enabled = (partsCount != null && partsCount>0);
        getMenuByClass(CreateBoxMenu.class).setEnabled(enabled);
      }
    }
    
    @Override
    protected int getConfiguredDropType() {
      return IDNDSupport.TYPE_FILE_TRANSFER;
    }
    
    @Override
    protected long getConfiguredDropMaximumSize() {
      return 300 * 1024 * 1024;
    }
    
    @Override
    protected void execDrop(ITableRow row, TransferObject t) {
      if (row!=null) {
        Long partId = getIdColumn().getValue(row);
        List<Long> partsList = new LinkedList<>();
        partsList.add(partId);
        
        if (t instanceof ResourceListTransferObject) {
          ResourceListTransferObject rlto = (ResourceListTransferObject) t;
          for (BinaryResource document : rlto.getResources()) {
            DocumentForm form = new DocumentForm();
            form.startNew(document);
            form.getPartsField().getTable().addParts(partsList);
          }
        }
      }
      else {
        // Création de nouveau(x) document(s) ?
      }
      super.execDrop(row, t);
    }
  
  }
  
  private class PartFormListener implements FormListener {
    @Override
    public void formChanged(FormEvent e) {
      // reload page to reflect new/changed data after saving any changes
      if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
        reloadPage();
      }
    }
  }
}
