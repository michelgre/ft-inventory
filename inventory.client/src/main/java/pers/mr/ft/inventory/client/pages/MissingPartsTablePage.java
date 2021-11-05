package pers.mr.ft.inventory.client.pages;

import java.util.Set;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractDecimalColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIconColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIntegerColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.client.ui.form.fields.decimalfield.IDecimalField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import pers.mr.ft.inventory.client.ClientSession;
import pers.mr.ft.inventory.client.Desktop;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.EditPartMenu;
import pers.mr.ft.inventory.client.forms.BoxForm;
import pers.mr.ft.inventory.client.forms.PartForm;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.BinColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.BoxCountColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.BoxIdColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.BoxLabelColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.ColorColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.DeltaCountColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.IconColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.KitCountColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.ModelColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.PartColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.PartIdColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.PartNumberColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.PartValueColumn;
import pers.mr.ft.inventory.client.pages.MissingPartsTablePage.Table.ValueColumn;
import pers.mr.ft.inventory.shared.codetype.ColorCodeType;
import pers.mr.ft.inventory.shared.codetype.ModelCodeType;
import pers.mr.ft.inventory.shared.lookup.BoxLookupCall;
import pers.mr.ft.inventory.shared.pages.IMissingPartsService;
import pers.mr.ft.inventory.shared.pages.MissingPartsTablePageData;

@Data(MissingPartsTablePageData.class)
public class MissingPartsTablePage extends AbstractPageWithTable<Table> {
  @Override
  protected boolean getConfiguredLeaf() {
    return true;
  }

  @Override
  protected void execLoadData(SearchFilter filter) {
    importPageData(BEANS.get(IMissingPartsService.class).getMissingPartsTableData(filter));
  }

  @Override
  protected String getConfiguredTitle() {
    // TODO [mreverbel] verify translation
    return TEXTS.get("MissingPartsTablePage");
  }

  public class Table extends AbstractTable {

    @Override
    protected String getConfiguredCssClass() {
      return "parts-table";
    }
    @Override
    protected Class<? extends IMenu> getConfiguredDefaultMenu() {
      return EditPartMenu.class;
    }
    
    public ModelColumn getModelColumn() {
      return getColumnSet().getColumnByClass(ModelColumn.class);
    }

    public PartNumberColumn getPartNumberColumn() {
      return getColumnSet().getColumnByClass(PartNumberColumn.class);
    }

    public BoxCountColumn getBoxCountColumn() {
      return getColumnSet().getColumnByClass(BoxCountColumn.class);
    }

    public KitCountColumn getKitCountColumn() {
      return getColumnSet().getColumnByClass(KitCountColumn.class);
    }

    public IconColumn getIconColumn() {
      return getColumnSet().getColumnByClass(IconColumn.class);
    }

    public ValueColumn getValueColumn() {
      return getColumnSet().getColumnByClass(ValueColumn.class);
    }

    public PartValueColumn getPartValueColumn() {
      return getColumnSet().getColumnByClass(PartValueColumn.class);
    }

    public BinColumn getBinColumn() {
      return getColumnSet().getColumnByClass(BinColumn.class);
    }

    public ColorColumn getColorColumn() {
      return getColumnSet().getColumnByClass(ColorColumn.class);
    }

    public DeltaCountColumn getDeltaCountColumn() {
      return getColumnSet().getColumnByClass(DeltaCountColumn.class);
    }
    public PartColumn getPartColumn() {
      return getColumnSet().getColumnByClass(PartColumn.class);
    }

    public PartIdColumn getPartIdColumn() {
      return getColumnSet().getColumnByClass(PartIdColumn.class);
    }

    public BoxLabelColumn getBoxLabelColumn() {
      return getColumnSet().getColumnByClass(BoxLabelColumn.class);
    }

    public BoxIdColumn getBoxIdColumn() {
      return getColumnSet().getColumnByClass(BoxIdColumn.class);
    }

    @Order(1000)
    public class BoxIdColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("BoxId");
      }

      @Override
      protected int getConfiguredWidth() {
        return 67;
      }
      protected String getConfiguredAggregationFunction() {
        return "none";
      }
    }

    @Order(2000)
    public class BoxLabelColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("BoxLabel");
      }

      @Override
      protected int getConfiguredWidth() {
        return 225;
      }
    }

    @Order(2250)
    public class BinColumn extends AbstractSmartColumn<Long> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Bin");
      }

      protected int getConfiguredWidth() {
        return 160;
      }
      
      protected Class<? extends ILookupCall<Long>> getConfiguredLookupCall() {
        return BoxLookupCall.class;
      }
      protected boolean getConfiguredVisible() {
        return false;
      };
    }

    
    @Order(2500)
    public class ModelColumn extends AbstractSmartColumn<Long> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Model");
      }

      @Override
      protected int getConfiguredWidth() {
        return 210;
      }
      @Override
      protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
        return ModelCodeType.class;
      }
      protected boolean getConfiguredVisible() {
        return false;
      };
    }

    
    @Order(3000)
    public class PartIdColumn extends AbstractLongColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("PartId");
      }

      @Override
      protected int getConfiguredWidth() {
        return 78;
      }
      
      protected boolean getConfiguredVisible() {
        return false;
      };
    }

    @Order(4000)
    public class PartNumberColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("PartNumber");
      }

      @Override
      protected int getConfiguredWidth() {
        return 100;
      }
    }

    @Order(4500)
    public class IconColumn extends AbstractIconColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Icon");
      }

      @Override
      protected int getConfiguredWidth() {
        return 109;
      }
      protected String getConfiguredCssClass() {
        return "part-icon";
      }
    }

    @Order(5000)
    public class PartColumn extends AbstractStringColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Part");
      }

      @Override
      protected int getConfiguredWidth() {
        return 315;
      }
    }

    @Order(6000)
    public class BoxCountColumn extends AbstractIntegerColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("PartCount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 70;
      }
    }

    @Order(7000)
    public class KitCountColumn extends AbstractIntegerColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("KitCount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 60;
      }
    }

    @Order(7500)
    public class DeltaCountColumn extends AbstractIntegerColumn {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("DeltaCount");
      }

      @Override
      protected int getConfiguredWidth() {
        return 75;
      }
    }

    
    @Order(8250)
    public class ColorColumn extends AbstractSmartColumn<Long> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("Color");
      }

      protected int getConfiguredWidth() {
        return 80;
      }
      
      protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
        return ColorCodeType.class;
      }
    }
    
    

    @Order(8500)
    public class PartValueColumn extends AbstractDecimalColumn<Double> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("PartValue");
      }

      protected int getConfiguredWidth() {
        return 70;
      }
      
      protected IDecimalField<Double> createDefaultEditor() {
        return null;
      }
      
      protected Double getConfiguredMinValue() {
        return Double.valueOf(0);
      }
      
      protected Double getConfiguredMaxValue() {
        return Double.valueOf(9999999.99D);
      }
      
      protected String getConfiguredAggregationFunction() {
        return "none";
      }
    }

    
    @Order(9000)
    public class ValueColumn extends AbstractDecimalColumn<Double> {
      @Override
      protected String getConfiguredHeaderText() {
        return TEXTS.get("CurrencySign");
      }

      @Override
      protected int getConfiguredWidth() {
        return 85;
      }

      @Override
      protected IDecimalField<Double> createDefaultEditor() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      protected Double getConfiguredMinValue() {
        return 0.0;
      }

      @Override
      protected Double getConfiguredMaxValue() {
        return 9999999.99;
      }
    }

    @Order(1000)
    public class EditPartMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("EditPart");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        Long partId = getPartIdColumn().getSelectedValue();
        if (partId!=null && partId>0) {
          @SuppressWarnings("unused")
          PartForm form = ((Desktop) ClientSession.get().getDesktop()).findPartForm(partId, new RowChangedListener());
        }
      }
    }

    @Order(2000)
    public class OpenBoxMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("OpenBox");
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet(TableMenuType.SingleSelection);
      }

      @Override
      protected void execAction() {
        Long boxId = getBoxIdColumn().getSelectedValue();
        if (boxId != null && boxId > 0) {
          @SuppressWarnings("unused")
          BoxForm boxForm = ((Desktop) ClientSession.get().getDesktop()).findBoxForm(boxId, new RowChangedListener());
        }
      }
    }

    
    private class RowChangedListener implements FormListener {
      @Override
      public void formChanged(FormEvent e) {
        // reload page to reflect new/changed data after saving any changes
      }
    }

  }    
    
}
