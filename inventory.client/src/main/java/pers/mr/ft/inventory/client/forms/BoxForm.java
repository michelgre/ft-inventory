package pers.mr.ft.inventory.client.forms;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.controls.AggregateTableControl;
import org.eclipse.scout.rt.client.ui.basic.table.controls.ITableControl;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.bigdecimalfield.AbstractBigDecimalField;
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.IButton;
import org.eclipse.scout.rt.client.ui.form.fields.decimalfield.AbstractDecimalField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.IGroupBoxBodyGrid;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.internal.HorizontalGroupBoxBodyGrid;
import org.eclipse.scout.rt.client.ui.form.fields.longfield.AbstractLongField;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import pers.mr.ft.inventory.client.ClientSession;
import pers.mr.ft.inventory.client.Desktop;
import pers.mr.ft.inventory.client.fields.AbstractIdField;
import pers.mr.ft.inventory.client.fields.AbstractPartsField;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.BinColumn;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.ColorColumn;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.CountColumn;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.IconColumn;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.IdColumn;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.PartLabelColumn;
import pers.mr.ft.inventory.client.fields.AbstractPartsField.Table.PartNumberColumn;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.CancelButton;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.DatenbankMenu;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.DocumentsMenu;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.EditMainBoxButton;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.BoughtSetField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.BoxTypeField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.ColorField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.DescriptionField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.DimensionsBox;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.DimensionsBox.HeightField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.DimensionsBox.LengthField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.DimensionsBox.WidthField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.GivenField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.IdField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.LabelField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.LocationField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.ModelField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.ParentField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.RemarksField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.TotalValueField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.PartsField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.OkButton;
import pers.mr.ft.inventory.shared.codetype.ColorCodeType;
import pers.mr.ft.inventory.shared.codetype.LocationCodeType;
import pers.mr.ft.inventory.shared.codetype.ModelCodeType;
import pers.mr.ft.inventory.shared.forms.BoxFormData;
import pers.mr.ft.inventory.shared.forms.BoxTypeFormData;
import pers.mr.ft.inventory.shared.forms.CreateBoxPermission;
import pers.mr.ft.inventory.shared.forms.IBoxService;
import pers.mr.ft.inventory.shared.forms.IBoxTypeService;
import pers.mr.ft.inventory.shared.forms.IDocumentService;
import pers.mr.ft.inventory.shared.forms.MovePartsFormData;
import pers.mr.ft.inventory.shared.forms.UpdateBoxPermission;
import pers.mr.ft.inventory.shared.lookup.BoxLookupCall;
import pers.mr.ft.inventory.shared.lookup.BoxTypeLookupCall;
import pers.mr.ft.inventory.shared.model.Document;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.BuyCostField;

@FormData(value = BoxFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class BoxForm extends AbstractForm {
  private Long boxId = 0L;
  private boolean lotAchat = false;
  private Long kitId = 0L;
  private List<Document> documents = new LinkedList<>();
  private Long copiedId = 0L;
  
  @FormData
  public Long getBoxId() {
    return boxId;
  }

  @FormData
  public void setBoxId(Long boxId) {
    this.boxId = boxId;
  }

  @FormData
  public boolean isLotAchat() {
    return lotAchat;
  }

  @FormData
  public void setLotAchat(boolean lotAchat) {
    this.lotAchat = lotAchat;
  }

  @FormData
  public Long getKitId() {
    return kitId;
  }

  @FormData
  public void setKitId(Long kitId) {
    this.kitId = kitId;
  }

  @FormData
  public List<Document> getDocuments() {
    return this.documents;
  }
  
  @FormData
  public void setDocuments(List<Document> documents) {
    this.documents.clear();
    for (Document d: documents) {
      this.documents.add(d);
    }
  }
  
  @FormData
  public Long getCopiedId() {
    return copiedId;
  }

  @FormData
  public void setCopiedId(Long copiedId) {
    this.copiedId = copiedId;
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Box");
  }

  @Override
  protected String getConfiguredCssClass() {
    return "box-form";
  }
  
  @Override
  protected int getConfiguredDisplayHint() { // <4>
    return IForm.DISPLAY_HINT_VIEW;
  }
  // ===============================================================================
  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public GroupBox getGroupBox() {
    return getFieldByClass(GroupBox.class);
  }

  public IdField getIdField() {
    return getFieldByClass(IdField.class);
  }

  public LabelField getLabelField() {
    return getFieldByClass(LabelField.class);
  }

  public PartsField getPartsField() {
    return getFieldByClass(PartsField.class);
  }

  public BoxTypeField getBoxTypeField() {
    return getFieldByClass(BoxTypeField.class);
  }

  public DescriptionField getDescriptionField() {
    return getFieldByClass(DescriptionField.class);
  }

  public ParentField getParentField() {
    return getFieldByClass(ParentField.class);
  }

  public LocationField getLocationField() {
    return getFieldByClass(LocationField.class);
  }

  public ColorField getColorField() {
    return getFieldByClass(ColorField.class);
  }

  public LengthField getLengthField() {
    return getFieldByClass(LengthField.class);
  }

  public WidthField getWidthField() {
    return getFieldByClass(WidthField.class);
  }

  public HeightField getHeightField() {
    return getFieldByClass(HeightField.class);
  }

  public RemarksField getRemarksField() {
    return getFieldByClass(RemarksField.class);
  }

  public ModelField getModelField() {
    return getFieldByClass(ModelField.class);
  }

  public BoxFieldsBox getBoxFieldsBox() {
    return getFieldByClass(BoxFieldsBox.class);
  }

  public DimensionsBox getDimensionsBox() {
    return getFieldByClass(DimensionsBox.class);
  }

  public TotalValueField getTotalValueField() {
    return getFieldByClass(TotalValueField.class);
  }

  public EditMainBoxButton getEditMainBoxButton() {
    return getFieldByClass(EditMainBoxButton.class);
  }

  public BoughtSetField getBoughtSetField() {
    return getFieldByClass(BoughtSetField.class);
  }

  public GivenField getGivenField() {
    return getFieldByClass(GivenField.class);
  }

  public BuyCostField getBuyCostField() {
    return getFieldByClass(BuyCostField.class);
  }

  public OkButton getOkButton() {
    return getFieldByClass(OkButton.class);
  }

  public CancelButton getCancelButton() {
    return getFieldByClass(CancelButton.class);
  }

  @Order(1000)
  public class MainBox extends AbstractGroupBox {
    @Order(1000)
    public class GroupBox extends AbstractGroupBox {
      
      @Override
      protected byte getConfiguredLabelPosition() {
        return LABEL_POSITION_TOP;
      }

      @Override
      protected Class<? extends IGroupBoxBodyGrid> getConfiguredBodyGrid() {
        return HorizontalGroupBoxBodyGrid.class;
      }
      
      @Override
      protected int getConfiguredGridColumnCount() {
        return 2;
      }

      @Override
      protected String getConfiguredBorderDecoration() {
        return BORDER_DECORATION_EMPTY;
      }

      @Order(0)
      public class BoxFieldsBox extends AbstractGroupBox {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Box");
        }

        @Override
        protected byte getConfiguredLabelPosition() {
          return LABEL_POSITION_TOP;
        }
        @Override
        protected boolean getConfiguredStatusVisible() {
          return false;
        }
        @Override
        protected boolean getConfiguredLabelVisible() {
          return true;
        }
        @Override
        protected boolean getConfiguredBorderVisible() {
          return false;
        }
        @Override
        protected int getConfiguredGridW() {
          return 2;
        }
        @Override
        protected double getConfiguredGridWeightY() {
          return 0.0;
        }
        @Override
        protected int getConfiguredGridColumnCount() {
          return 10;
        }
        
        @Override
        protected Class<? extends IGroupBoxBodyGrid> getConfiguredBodyGrid() {
          return HorizontalGroupBoxBodyGrid.class;
        }
        
        @Override
        protected boolean getConfiguredExpandable() {
          return true;
        }
        
        @Override
        protected String getConfiguredCssClass() {
          return "fieldbox";
        }
        @Order(1000)
        public class IdField extends AbstractIdField {
        }

        @Order(2000)
        public class LabelField extends AbstractStringField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Label");
          }

          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected int getConfiguredMaxLength() {
            return 50;
          }
          @Override
          protected int getConfiguredGridW() {
            return 4;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
        }

        @Order(2200)
        public class DimensionsBox extends AbstractGroupBox {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Dimensions");
          }
          @Override
          protected boolean getConfiguredLabelVisible() {
            return false;
          }
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected String getConfiguredBorderDecoration() {
            return BORDER_DECORATION_EMPTY;
          }

          @Override
          protected int getConfiguredGridColumnCount() {
            return 1;
          }
          
          @Override
          protected int getConfiguredGridW() {
            return 1;
          }
          
          @Override
          protected int getConfiguredGridH() {
            return 3;
          }
          
          @Override
          protected String getConfiguredCssClass() {
            return "fieldbox";
          }
          
          @Order(8000)
          public class LengthField extends AbstractLongField {
            @Override
            protected String getConfiguredLabel() {
              return TEXTS.get("Length");
            }

            @Override
            protected Long getConfiguredMinValue() {
              return 0L;
            }

            @Override
            protected Long getConfiguredMaxValue() {
              return 1000L;
            }
            @Override
            protected byte getConfiguredLabelPosition() {
              return LABEL_POSITION_TOP;
            }
            @Override
            protected boolean getConfiguredStatusVisible() {
              return false;
            }
            @Override
            protected String getConfiguredTooltipText() {
              return TEXTS.get("DimensionInMM");
            }

          }

          @Order(9000)
          public class WidthField extends AbstractLongField {
            @Override
            protected String getConfiguredLabel() {
              return TEXTS.get("Width");
            }

            @Override
            protected Long getConfiguredMinValue() {
              return 0L;
            }

            @Override
            protected Long getConfiguredMaxValue() {
              return 1000L;
            }
            @Override
            protected byte getConfiguredLabelPosition() {
              return LABEL_POSITION_TOP;
            }
            @Override
            protected boolean getConfiguredStatusVisible() {
              return false;
            }
            @Override
            protected String getConfiguredTooltipText() {
              return TEXTS.get("DimensionInMM");
            }
          }

          @Order(10000)
          public class HeightField extends AbstractLongField {
            @Override
            protected String getConfiguredLabel() {
              return TEXTS.get("Height");
            }

            @Override
            protected Long getConfiguredMinValue() {
              return 0L;
            }

            @Override
            protected Long getConfiguredMaxValue() {
              return 1000L;
            }
            @Override
            protected byte getConfiguredLabelPosition() {
              return LABEL_POSITION_TOP;
            }
            @Override
            protected boolean getConfiguredStatusVisible() {
              return false;
            }
            @Override
            protected String getConfiguredTooltipText() {
              return TEXTS.get("DimensionInMM");
            }
          }
        }

        @Order(2500)
        public class DescriptionField extends AbstractStringField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Description");
          }

          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected int getConfiguredGridW() {
            return 4;
          }
          
          @Override
          protected int getConfiguredGridH() {
            return 2;
          }
          
          @Override
          protected boolean getConfiguredMultilineText() {
            return true;
          }
          @Override
          protected int getConfiguredMaxLength() {
            return 300;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
        }

        @Order(3000)
        public class BoxTypeField extends AbstractSmartField<Long> {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Type");
          }
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected int getConfiguredGridW() {
            return 3;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected Class<? extends ILookupCall<Long>> getConfiguredLookupCall() {
            return BoxTypeLookupCall.class;
          }
          @Override
          protected void execChangedValue() {
            Long idBoxType = getValue();
            if (idBoxType!=null && idBoxType>0) {
              IBoxTypeService service = BEANS.get(IBoxTypeService.class);
              BoxTypeFormData formData = new BoxTypeFormData();
              formData.setBoxTypeId(idBoxType);
              formData = service.load(formData);
              
              getLengthField().setValue(formData.getLength().getValue());
              getWidthField().setValue(formData.getWidth().getValue());
              getHeightField().setValue(formData.getHeight().getValue());
            }
          }
        }

        @Order(5000)
        public class LocationField extends AbstractSmartField<Long> {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Location");
          }

          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected int getConfiguredGridW() {
            return 2;
          }
          @Override
          protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
            return LocationCodeType.class;
          }
        }

        @Order(6000)
        public class ParentField extends AbstractSmartField<Long> {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("IncludedIn");
          }
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected int getConfiguredGridW() {
            return 3;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected Class<? extends ILookupCall<Long>> getConfiguredLookupCall() {
            return BoxLookupCall.class;
          }
        }

        @Order(7000)
        public class ColorField extends AbstractSmartField<Long> {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Color");
          }
          
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected int getConfiguredGridW() {
            return 2;
          }
          @Override
          protected Class<? extends ICodeType<String, Long>> getConfiguredCodeType() {
            return ColorCodeType.class;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return true;
          }
        }

        

        @Order(12000)
        public class RemarksField extends AbstractStringField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Remarks");
          }

          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected int getConfiguredGridW() {
            return 4;
          }
          
          @Override
          protected int getConfiguredGridH() {
            return 2;
          }
          
          @Override
          protected boolean getConfiguredMultilineText() {
            return true;
          }
        }
        @Order(13000)
        public class ModelField extends AbstractSmartField<Long> {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Model");
          }
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected int getConfiguredGridW() {
            return 3;
          }
          @Override
          protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
            return ModelCodeType.class;
          }
          @Override
          protected boolean getConfiguredBrowseHierarchy() {
            return true;
          }
          @Override
          protected boolean getConfiguredBrowseLoadIncremental() {
            return true;
          }
          
          @Override
          protected int getConfiguredBrowseMaxRowCount() {
            return 1000;
          }
        }

        @Order(14000)
        public class TotalValueField extends AbstractDecimalField<Double> {
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected boolean getConfiguredEnabled() {
            return false;
          }
          @Override
          protected int getConfiguredGridW() {
            return 1;
          }
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("TotalValue");
          }

          @Override
          protected Double getMinPossibleValue() {
            return 0.0;
          }

          @Override
          protected Double getMaxPossibleValue() {
            return 999999.99;
          }

          @Override
          protected Double parseValueInternal(String text) {
            return Double.parseDouble(text);
          }

          @Override
          protected Double getConfiguredMinValue() {
            return 0.0;
          }

          @Override
          protected Double getConfiguredMaxValue() {
            return 999999.99;
          }
        }

        @Order(15000)
        public class BoughtSetField extends AbstractBooleanField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Purchase");
          }
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected void execChangedValue() {
            setupFields();
          }
          
        }

        @Order(16000)
        public class GivenField extends AbstractBooleanField {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Given");
          }
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
        }

        @Order(17000)
        public class BuyCostField extends AbstractDecimalField<Double> {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("BuyCost");
          }

          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }

          @Override
          protected boolean getConfiguredVisible() {
            return false;
          }
          
          @Override
          protected Double getConfiguredMinValue() {
            return 0.0;
          }

          @Override
          protected Double getConfiguredMaxValue() {
            return 9999.99;
          }

          @Override
          protected Double getMinPossibleValue() {
            return 0.0;
          }

          @Override
          protected Double getMaxPossibleValue() {
            return 9999.99;
          }

          @Override
          protected Double parseValueInternal(String text) {
            text = text.replaceAll(" ", "").replaceAll(",", ".");
            return Double.parseDouble(text);
          }

        }

        
        
      }
      
      /*
      @Order(15000)
      public class PartsField extends AbstractTableField<PartsField.Table> {
        @Override
        protected byte getConfiguredLabelPosition() {
          return LABEL_POSITION_TOP;
        }
        @Override
        protected int getConfiguredGridW() {
          return 2;
        }
        
        protected void setTotalValue(Double value) {
          getTotalValueField().setValue(value);
        }
        
        public void updateTotals(ITableRow row) {
          Double totalValue = computeTotalValue(row);
          setTotalValue(totalValue);
        }

        public Double computeTotalValue(ITableRow changedRow) {
          // Mise à jour de la ligne modifiée éventuelle
          if (changedRow != null) {
            Double partValue = getTable().getPartValueColumn().getValue(changedRow);
            Integer count = getTable().getCountColumn().getValue(changedRow);
            if (partValue!=null && count != null) {
              Double rowValue = count * partValue;
              getTable().getValueColumn().setValue(changedRow, rowValue);
            }
          }
          
          // Recalcul global
          // Calcul valeur
          double totalValue = 0.0;
          for (ITableRow row: getTable().getRows()) {
            Double rowValue = getTable().getValueColumn().getValue(row);
            if (rowValue!=null) {
              totalValue += rowValue;
            }
          }
          return totalValue;
          
        }
        public class Table extends AbstractTable {

          @Override
          protected Class<? extends IMenu> getConfiguredDefaultMenu() {
            return EditPartMenu.class;
          }
          
          @Order(1000)
          public class AddRowMenu extends AbstractMenu {
            @Override
            protected String getConfiguredText() {
              return TEXTS.get("AddRow");
            }

            @Override
            protected Set<? extends IMenuType> getConfiguredMenuTypes() {
              return CollectionUtility.hashSet(TableMenuType.EmptySpace);
            }

            @Override
            protected void execAction() {
              appendRowAndEdit();
            }
          }


          @Order(2000)
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
              PartForm form = new PartForm();
              form.setPartId(getIdColumn().getSelectedValue());
              form.addFormListener(new RowChangedListener());
              form.startModify();
            }
          }


          @Order(3000)
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
              getTable().deleteRows(getTable().getSelectedRows());
            }
          }

          
          public CountColumn getCountColumn() {
            return getColumnSet().getColumnByClass(CountColumn.class);
          }

          public IconColumn getIconColumn() {
            return getColumnSet().getColumnByClass(IconColumn.class);
          }

          public ValueColumn getValueColumn() {
            return getColumnSet().getColumnByClass(ValueColumn.class);
          }

          public PartNumberColumn getPartNumberColumn() {
            return getColumnSet().getColumnByClass(PartNumberColumn.class);
          }

          public PartValueColumn getPartValueColumn() {
            return getColumnSet().getColumnByClass(PartValueColumn.class);
          }

          public ColorColumn getColorColumn() {
            return getColumnSet().getColumnByClass(ColorColumn.class);
          }

          public OldIdColumn getOldIdColumn() {
            return getColumnSet().getColumnByClass(OldIdColumn.class);
          }

          public PartLabelColumn getPartLabelColumn() {
            return getColumnSet().getColumnByClass(PartLabelColumn.class);
          }

          public IdColumn getIdColumn() {
            return getColumnSet().getColumnByClass(IdColumn.class);
          }


          
          @Order(1000)
          public class IdColumn extends AbstractLongColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("Identifier");
            }

            @Override
            protected int getConfiguredWidth() {
              return 80;
            }
            
            @Override
            protected boolean getConfiguredVisible() {
              return false;
            }
          }


          @Order(2000)
          public class PartNumberColumn extends AbstractStringColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("PartNumber");
            }

            @Override
            protected int getConfiguredWidth() {
              return 100;
            }
            @Override
            protected boolean getConfiguredEditable() {
              return true;
            }
            @Override
            protected void execCompleteEdit(ITableRow row, IFormField editingField) {
              super.execCompleteEdit(row, editingField);
              
              String partNumber = ((IStringField) editingField).getValue();
              if (partNumber!=null) {
                partNumber = partNumber.trim();
              }
              else {
                partNumber = "";
              }
              if (partNumber.length()>0) {
                IPartService partService = BEANS.get(IPartService.class);
                PartFormData partData = partService.loadByNumber(partNumber);
                String partLabel = partData.getTitle().getValue();
                Double partValue = partData.getValue().getValue();
                getIdColumn().setValue(row, partData.getId().getValue());
                getPartLabelColumn().setValue(row, partLabel);
                getPartValueColumn().setValue(row, partValue);
                getColorColumn().setValue(row, partData.getColor().getValue());
                
                // Icone: on récupère l'id
                Long iconId = partData.getImageId();
                if (iconId != null && iconId>0) {
                  getIconColumn().setValue(row, "icons/?image=" + iconId);
                }
              }
              else {
                // Le n° a été effacé => vider le part Id
                getIdColumn().setValue(row, null);
                getPartLabelColumn().setValue(row, "");
                getIconColumn().setValue(row, "");
                getPartValueColumn().setValue(row, null);
                getValueColumn().setValue(row, 0.0);
                getColorColumn().setValue(row, null);
              }
              updateTotals(row);
            }            
            @Override
            protected String execValidateValue(ITableRow row, String rawValue) {
              String newPartNumber = super.execValidateValue(row, rawValue);
              if (newPartNumber!=null) {
                newPartNumber = newPartNumber.trim();
              }
              return newPartNumber;
            }
            @Override
            protected IFormField execPrepareEdit(ITableRow row) {
              // TODO Auto-generated method stub
              return super.execPrepareEdit(row);
            }
          }

          @Order(2500)
          public class CountColumn extends AbstractIntegerColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("Quantity");
            }

            @Override
            protected int getConfiguredWidth() {
              return 75;
            }
            
            @Override
            protected boolean getConfiguredEditable() {
              return true;
            }
            @Override
            protected void execCompleteEdit(ITableRow row, IFormField editingField) {
              super.execCompleteEdit(row, editingField);
              updateTotals(row);
              
              // Ligne suivante si sur dernière ligne
              int nbRows = getTable().getRowCount();
              if (row.getRowIndex()==nbRows-1) {
                appendRowAndEdit();
              }
            }
            
          }
          
          
          @Order(3000)
          public class PartLabelColumn extends AbstractStringColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("PartName");
            }

            @Override
            protected int getConfiguredWidth() {
              return 300;
            }
            @Override
            protected boolean getConfiguredEditable() {
              return false;
            }
            @Override
            protected String execValidateValue(ITableRow row, String rawValue) {
              // TODO Auto-generated method stub
              return super.execValidateValue(row, rawValue);
            }
          }


          @Order(3500)
          public class ColorColumn extends AbstractSmartColumn<Long> {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("Color");
            }

            @Override
            protected int getConfiguredWidth() {
              return 80;
            }
            
            @Override
            protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
              return ColorCodeType.class;
            }
          }


          @Order(4000)
          public class IconColumn extends AbstractIconColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("Icon");
            }

            @Override
            protected int getConfiguredWidth() {
              return 60;
            }
            @Override
            protected String getConfiguredCssClass() {
              return "part-icon";
            }
          }


          @Order(5000)
          public class PartValueColumn extends AbstractDecimalColumn<Double> {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("PartValue");
            }

            @Override
            protected int getConfiguredWidth() {
              return 60;
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



          
          @Order(6000)
          public class ValueColumn extends AbstractDecimalColumn<Double> {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("CurrencySign");
            }

            @Override
            protected int getConfiguredWidth() {
              return 60;
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

          @Order(7000)
          public class OldIdColumn extends AbstractLongColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("OldIdentifier");
            }

            @Override
            protected int getConfiguredWidth() {
              return 80;
            }
            @Override
            protected boolean getConfiguredVisible() {
              return false;
            }
          }

          private void updateCostsToDelete(ITableRow changedRow) {
            // Mise à jour de la ligne modifiée éventuelle
            if (changedRow != null) {
              Double partValue = getPartValueColumn().getValue(changedRow);
              Integer count = getCountColumn().getValue(changedRow);
              if (partValue!=null && count != null) {
                Double rowValue = count * partValue;
                getValueColumn().setValue(changedRow, rowValue);
              }
            }
            
            // Recalcul global
            // Calcul valeur
            double totalValue = 0.0;
            for (ITableRow row: getRows()) {
              Double rowValue = getValueColumn().getValue(row);
              if (rowValue!=null) {
                totalValue += rowValue;
              }
            }
            getTotalValueField().setValue(totalValue);
            
          }
          
          private void appendRowAndEdit() {
            ITableRow newRow = getTable().addRow(true);
            getTable().selectRow(newRow);
            getTable().requestFocusInCell(getPartNumberColumn(), newRow);
          }
          
          private class RowChangedListener implements FormListener {
            @Override
            public void formChanged(FormEvent e) {
              // reload page to reflect new/changed data after saving any changes
              if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
                Object source = e.getSource();
                if (source instanceof PartForm) {
                  // Les modifications qui nous intéressent sont le coût de la pièce et son nom
                  PartForm form = (PartForm) source;
                  String partTitle = form.getTitleField().getValue();
                  Double partValue = form.getValueField().getValue();
                  
                  // Ligne en cours d'édition
                  ITableRow row = getTable().getSelectedRow();
                  
                  getPartLabelColumn().setValue(row, partTitle);
                  getPartValueColumn().setValue(row, partValue);
                  
                  updateTotals(row);
                }
                reloadTableData();
              }
            }
          }
          
        }

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("PartsList");
        }

        @Override
        protected int getConfiguredGridH() {
          return super.getConfiguredGridH();
        }
        
        @Override
        protected double getConfiguredGridWeightY() {
          return 1;
        }
        @Override
        public IValidateContentDescriptor validateContent() {
          IValidateContentDescriptor vcd =  super.validateContent();
          if (vcd != null) {
            return vcd;
          }
          
          if (isSaveNeeded()) {
            // Il faut peut être joindre des lignes si c'est pour le même pièce
            Map<Long,ITableRow> rowsByPartId = new HashMap<>();
            IdColumn idColumn = getTable().getIdColumn();
            CountColumn countColumn = getTable().getCountColumn();
            for (ITableRow row: getTable().getRows()) {
              Long partId = idColumn.getValue(row);
              Integer count = countColumn.getValue(row);
              if (count==null) {
                count = 0;
              }
              if (partId != null && partId != 0) {
                // La pièce est déjà dans la table ?
                ITableRow prevRow = rowsByPartId.get(partId);
                if (prevRow == null) {
                  // Non
                  rowsByPartId.put(partId, row);
                }
                else {
                  // Supprimer cette ligne après avoir ajouté les pièces à l'autre
                  Integer prevCount = countColumn.getValue(prevRow);
                  if (prevCount==null) {
                    prevCount = 0;
                  }
                  Integer totalCount = prevCount + count;
                  countColumn.setValue(prevRow, totalCount);
                  
                  getTable().deleteRow(row);
                }
              }
            }
          }
          return null;
        }
        
      }
      */
      
      @Order(15000)
      public class PartsField extends AbstractPartsField {

        @Override
        protected boolean getConfiguredEditable() {
          return true;
        }
        @Override
        protected void setTotalValue(Double value) {
          getTotalValueField().setValue(value);
        }

        @Override
        protected void setPartsCount(Integer partsCount) {
          
        }
        
        protected ITableControl createAggregateTableControl() {
          return new AggregateTableControl();
        }
        
        @Override
        protected boolean hasKit() {
          return getModelField().getValue() != null;
        }
        
        @Override
        protected void execMoveParts() {
          MovePartsForm form = new MovePartsForm();
          // TODO: maj sélection parts
          MovePartsFormData moveFormData = new MovePartsFormData();
          moveFormData.getFromBoxId().setValue(boxId);
          moveFormData.getFromBoxLabel().setValue(getLabelField().getValue());
          IdColumn partIdColumn = getTable().getIdColumn();
          CountColumn countColumn = getTable().getCountColumn();
          PartLabelColumn partLabelColumn = getTable().getPartLabelColumn();
          IconColumn iconColumn = getTable().getIconColumn();
          ColorColumn colorColumn = getTable().getColorColumn();
          PartNumberColumn pnColumn = getTable().getPartNumberColumn();
          BinColumn binColumn = getTable().getBinColumn();
          
          for (ITableRow row: getTable().getSelectedRows()) {
            Integer count = countColumn.getValue(row);
            if (count==null) {
              count = 0;
            }
            if (count > 0) {
              MovePartsFormData.Parts.PartsRowData movedPartData = moveFormData.getParts().addRow();
              movedPartData.setId(partIdColumn.getValue(row));
              movedPartData.setPartLabel(partLabelColumn.getValue(row));
              movedPartData.setIcon(iconColumn.getValue(row));
              movedPartData.setColor(colorColumn.getValue(row));
              movedPartData.setPartNumber(pnColumn.getValue(row));
              movedPartData.setCount(count); 
              movedPartData.setMaxCount(count);
              movedPartData.setBin(binColumn.getValue(row));
            }            
          }
          form.startModify(moveFormData);
          form.waitFor();
          if (form.getCloseSystemType()==IButton.SYSTEM_TYPE_OK) {
            IBoxService service = BEANS.get(IBoxService.class);
            
            // Pièces déplacées
            moveFormData = new MovePartsFormData();
            form.exportData(moveFormData);
            
            // Pièces de la boîte
            BoxFormData formData = new BoxFormData();
            exportFormData(formData);
            
            BoxFormData fromBoxData = service.moveParts(formData, moveFormData,true);
            fromBoxData = new BoxFormData();
            fromBoxData.setBoxId(boxId);
            fromBoxData = service.load(fromBoxData);
            importFormData(fromBoxData);
            getPartsField().updateCalculatedValues(null);
            getPartsField().showKitStatus(null);
            getTotalValueField().markSaved();
            getPartsField().markSaved();
          }
        }
     }
      
    }

    @Order(2000)
    public class OkButton extends AbstractOkButton {

    }

    @Order(3000)
    public class CancelButton extends AbstractCancelButton {

    }

    @Order(4000)
    public class EditMainBoxButton extends AbstractButton {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("MainBox");
      }

      @Override
      protected void execClickAction() {
        Long parentId = ((BoxForm) getForm()).getParentField().getValue();
        if (parentId!=null && parentId>0) {
          ((Desktop) ClientSession.get().getDesktop()).findBoxForm(parentId, null);        
        }
      }
    }

    @Order(1000)
    public class DatenbankMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("GotoDatenbank");
      }

      @Override
      protected int getConfiguredActionStyle() {
        return ACTION_STYLE_BUTTON;
      }

      @Override
      protected Set<? extends IMenuType> getConfiguredMenuTypes() {
        return CollectionUtility.hashSet();
      }

      @Override
      protected void execAction() {
        Long modelId = getModelField().getValue();
        if (modelId!=null && modelId>0) {
          ClientSessionProvider.currentSession().getDesktop().openUri("https://ft-datenbank.de/ft-article/"+modelId, OpenUriAction.NEW_WINDOW);
        }
      }
    }

    @Order(2000)
    public class DocumentsMenu extends AbstractMenu {
      
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Documents");
      }
      
      @Override
      protected int getConfiguredActionStyle() {
        return ACTION_STYLE_BUTTON;
      }

      @Override
      protected void execAction() {
      }
    }
    
    
  }

  public void startModify() {
    startInternalExclusive(new ModifyHandler());
  }

  public void startNew() {
    startInternal(new NewHandler());
    
    // S'il y a des lignes elles doivent toutes être marquées insérées
    for (ITableRow row: getPartsField().getTable().getRows()) {
      row.setStatusInserted();
    }

  }

  public class NewHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      IBoxService service = BEANS.get(IBoxService.class);
      BoxFormData formData = new BoxFormData();
      exportFormData(formData);
      formData = service.prepareCreate(formData);
      importFormData(formData);
      getBoughtSetField().setValue(lotAchat);
      
      setupFields();
      setEnabledPermission(new CreateBoxPermission());
    }

    @Override
    protected void execStore() {
      IBoxService service = BEANS.get(IBoxService.class);
      BoxFormData formData = new BoxFormData();
      exportFormData(formData);
      service.create(formData);
    }
  }

  public class ModifyHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      IBoxService service = BEANS.get(IBoxService.class);
      BoxFormData formData = new BoxFormData();
      exportFormData(formData);
      formData = service.load(formData);
      importFormData(formData);

      setTitle(formData.getLabel().getValue());
      getPartsField().updateCalculatedValues(null);
      getPartsField().showKitStatus(null);

      Long parentBoxId = formData.getParent().getValue();
      getEditMainBoxButton().setVisible(parentBoxId!=null && parentBoxId>0);
      
      Long modelId = formData.getModel().getValue();
      getMainBox().getMenuByClass(DatenbankMenu.class).setVisible(modelId!=null && modelId>0);
      
      IMenu documentsMenu = getMainBox().getMenuByClass(DocumentsMenu.class);
      if (formData.getDocuments().size()>0) {
        for (Document doc: formData.getDocuments()) {
          IMenu menu = new AbstractMenu() {
            @Override
            protected String getConfiguredText() {
              return doc.getName();
            }
            
            @Override
            protected void execAction() {
              IDocumentService service = BEANS.get(IDocumentService.class);
              BinaryResource documentContent = service.loadContent(doc.getId());
              if (documentContent != null) {
                ClientSession.get().getDesktop().openUri(documentContent, OpenUriAction.DOWNLOAD);
              }
  
            }
          };
          documentsMenu.addChildAction(menu);
        }
      }
      else {
        documentsMenu.setVisible(false);
      }
      
      setupFields();
      setEnabledPermission(new UpdateBoxPermission());
    }

    @Override
    protected void execStore() {
      IBoxService service = BEANS.get(IBoxService.class);
      BoxFormData formData = new BoxFormData();
      exportFormData(formData);
      service.store(formData);
    }
  }
  
  private void setupFields() {
    boolean lotAchat = getBoughtSetField().getValue();
    getGivenField().setVisible(!lotAchat);
    getBuyCostField().setVisible(lotAchat);
  }
  
  public void gotoPart(Long partId) {
    getPartsField().gotoPart(partId);
    getPartsField().requestFocus();
  }
}
