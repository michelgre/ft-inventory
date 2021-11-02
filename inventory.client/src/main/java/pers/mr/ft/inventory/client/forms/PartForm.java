package pers.mr.ft.inventory.client.forms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIconColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIntegerColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.basic.table.controls.AggregateTableControl;
import org.eclipse.scout.rt.client.ui.basic.table.controls.ITableControl;
import org.eclipse.scout.rt.client.ui.basic.table.userfilter.ColumnUserFilterState;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.client.ui.dnd.ResourceListTransferObject;
import org.eclipse.scout.rt.client.ui.dnd.TransferObject;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractLinkButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.decimalfield.AbstractDecimalField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.IGroupBoxBodyGrid;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.internal.HorizontalGroupBoxBodyGrid;
import org.eclipse.scout.rt.client.ui.form.fields.imagefield.AbstractImageField;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.platform.util.collection.OrderedCollection;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;

import pers.mr.ft.inventory.client.ClientSession;
import pers.mr.ft.inventory.client.Desktop;
import pers.mr.ft.inventory.client.fields.AbstractIdField;
import pers.mr.ft.inventory.client.fields.AbstractPartsField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.CancelButton;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.DocumentsMenu;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.BoxesField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.KitsField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.CategoryField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.ColorField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.DatenbankUUIDField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.DescriptionField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.GotoDatenbankButton;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.IconField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.IdField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.PartNumbersField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.PartTypeField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.RarityField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.TitleField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.ValueField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartFieldsBox.WeightField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.GroupBox.PartsField;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.OkButton;
import pers.mr.ft.inventory.client.forms.PartForm.MainBox.SyncButton;
import pers.mr.ft.inventory.shared.codetype.CategoryCodeType;
import pers.mr.ft.inventory.shared.codetype.ColorCodeType;
import pers.mr.ft.inventory.shared.codetype.LocationCodeType;
import pers.mr.ft.inventory.shared.codetype.PartTypeCodeType;
import pers.mr.ft.inventory.shared.codetype.RarityCodeType;
import pers.mr.ft.inventory.shared.forms.CreatePartPermission;
import pers.mr.ft.inventory.shared.forms.IDocumentService;
import pers.mr.ft.inventory.shared.forms.IPartService;
import pers.mr.ft.inventory.shared.forms.PartFormData;
import pers.mr.ft.inventory.shared.forms.UpdatePartPermission;
import pers.mr.ft.inventory.shared.images.IImageService;
import pers.mr.ft.inventory.shared.model.Document;
import pers.mr.ft.inventory.shared.pages.IShopService;
import pers.mr.ft.inventory.shared.pages.ShopTablePageData;
import pers.mr.ft.inventory.shared.pages.ShopTablePageData.ShopTableRowData;

@FormData(value = PartFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class PartForm extends AbstractForm {
  private Long partId = 0L;
  private Long imageId = 0L;
  private List<Document> documents = new LinkedList<>();

  @FormData
  public Long getPartId() {
    return partId;
  }

  @FormData
  public void setPartId(Long partId) {
    this.partId = partId;
  }

  @FormData
  public Long getImageId() {
    return imageId;
  }

  @FormData
  public void setImageId(Long imageId) {
    this.imageId = imageId;
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

  @Override
  protected String getConfiguredTitle() {
    // TODO [michel] verify translation
    return TEXTS.get("Part");
  }

  @Override
  protected String getConfiguredCssClass() {
    return "part-form";
  }
  
  @Override
  protected int getConfiguredDisplayHint() { // <4>
    return IForm.DISPLAY_HINT_VIEW;
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public GroupBox getGroupBox() {
    return getFieldByClass(GroupBox.class);
  }

  public IdField getIdField() {
    return getFieldByClass(IdField.class);
  }

  public PartTypeField getPartTypeField() {
    return getFieldByClass(PartTypeField.class);
  }

  public TitleField getTitleField() {
    return getFieldByClass(TitleField.class);
  }

  public IconField getIconField() {
    return getFieldByClass(IconField.class);
  }

  public DescriptionField getDescriptionField() {
    return getFieldByClass(DescriptionField.class);
  }

  public ColorField getColorField() {
    return getFieldByClass(ColorField.class);
  }

  public ValueField getValueField() {
    return getFieldByClass(ValueField.class);
  }

  public PartNumbersField getPartNumbersField() {
    return getFieldByClass(PartNumbersField.class);
  }

  public CategoryField getCategoryField() {
    return getFieldByClass(CategoryField.class);
  }

  public WeightField getWeightField() {
    return getFieldByClass(WeightField.class);
  }

  public SyncButton getSyncButton() {
    return getFieldByClass(SyncButton.class);
  }

  public DatenbankUUIDField getDatenbankUUIDField() {
    return getFieldByClass(DatenbankUUIDField.class);
  }

  public PartsField getPartsField() {
    return getFieldByClass(PartsField.class);
  }

  public BoxesField getBoxesField() {
    return getFieldByClass(BoxesField.class);
  }

  public GotoDatenbankButton getGotoDatenbankButton() {
    return getFieldByClass(GotoDatenbankButton.class);
  }

  public RarityField getRarityField() {
    return getFieldByClass(RarityField.class);
  }

  public KitsField getKitsField() {
    return getFieldByClass(KitsField.class);
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
      protected Class<? extends IGroupBoxBodyGrid> getConfiguredBodyGrid() {
        return HorizontalGroupBoxBodyGrid.class;
      }
      
      @Order(0)
      public class PartFieldsBox extends AbstractGroupBox {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Details");
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
        protected boolean getConfiguredExpandable() {
          return true;
        }
        
        
        @Override
        protected int getConfiguredGridW() {
          return 2;
        }
        @Override
        protected double getConfiguredGridWeightY() {
          return 0.1;
        }
        @Override
        protected int getConfiguredGridColumnCount() {
          return 6;
        }
        
        @Override
        protected Class<? extends IGroupBoxBodyGrid> getConfiguredBodyGrid() {
          return HorizontalGroupBoxBodyGrid.class;
        }
        
        @Override
        protected String getConfiguredCssClass() {
          return "fieldbox";
        }
      
        @Order(1000)
        public class IdField extends AbstractIdField {
        }
  
        @Order(1500)
        public class TitleField extends AbstractStringField {
          @Override
          protected int getConfiguredGridW() {
            return 2;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("PartName");
          }
  
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected int getConfiguredMaxLength() {
            return 200;
          }
        }
  
        @Order(2000)
        public class PartTypeField extends AbstractSmartField<Long> {
          @Override
          protected int getConfiguredGridW() {
            return 2;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("PartType");
          }
          
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected Class<? extends ICodeType<String, Long>> getConfiguredCodeType() {
            return PartTypeCodeType.class;
          }
        }
  
        @Order(3000)
        public class ColorField extends AbstractSmartField<Long> {
          @Override
          protected int getConfiguredGridW() {
            return 1;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Color");
          }
          
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected Class<? extends ICodeType<String, Long>> getConfiguredCodeType() {
            return ColorCodeType.class;
          }
        }
  
        @Order(4000)
        public class CategoryField extends AbstractSmartField<Long> {
          @Override
          protected int getConfiguredGridW() {
            return 2;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Category");
          }
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
            return CategoryCodeType.class;
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
  
        
        @Order(5000)
        public class DescriptionField extends AbstractStringField {
          @Override
          protected int getConfiguredGridW() {
            return 2;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Description");
          }
  
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected int getConfiguredMaxLength() {
            return 200;
          }
        }
  
        @Order(5100)
        public class WeightField extends AbstractDecimalField<Double> {
          @Override
          protected int getConfiguredGridW() {
            return 1;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Weight");
          }
  
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected Double getConfiguredMinValue() {
            return 0.0;
          }
  
          @Override
          protected Double getConfiguredMaxValue() {
            return 999999.99;
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
            text = text.replaceAll(" ", "").replaceAll(",", ".");
            return Double.parseDouble(text);
          }
          @Override
          protected int getConfiguredFractionDigits() {
            return 5;
          }
          @Override
          protected int getConfiguredMinFractionDigits() {
            return 5;
          }
          @Override
          protected int getConfiguredMaxFractionDigits() {
            return 5;
          }
        }

        @Order(5175)
        public class RarityField extends AbstractSmartField<Integer> {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Rarity");
          }
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected Class<? extends ICodeType<?, Integer>> getConfiguredCodeType() {
            return RarityCodeType.class;
          }
        }
  
        
        @Order(5250)
        public class DatenbankUUIDField extends AbstractStringField {
          @Override
          protected int getConfiguredGridW() {
            return 2;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("DatenbankUUID");
          }
  
          @Override
          protected int getConfiguredMaxLength() {
            return 40;
          }
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
        }
  
        @Order(5300)
        public class ValueField extends AbstractDecimalField<Double> {
          @Override
          protected int getConfiguredGridW() {
            return 1;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
         @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("PartValue");
          }
  
          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
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
        @Order(5400)
        public class IconField extends AbstractImageField {
          @Override
          protected int getConfiguredGridW() {
            return 3;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("Icon");
          }
  
          @Override
          protected boolean getConfiguredAutoFit() {
            return true;
          }
  
          @Override
          protected int getConfiguredGridH() {
            return 3;
          }
  
          @Override
          protected boolean getConfiguredLabelVisible() {
            return false;
          }
          
          @Override
          protected int getConfiguredDropType() {
            return TYPE_FILE_TRANSFER;
          }
          
          @Override
          public void setImageId(String imageId) {
            super.setImageId(imageId);
            
            if (imageId!=null && imageId.length()>0) {
              Long myImageId = Long.parseLong(imageId);
              IImageService service = BEANS.get(IImageService.class);
              byte [] imageData = service.getImageData(myImageId, false);
              InputStream imageStream = new ByteArrayInputStream(imageData);
              setImage(IOUtility.readBytes(imageStream));
              
            }
          }
          
          @Override
          protected void execDropRequest(TransferObject transferObject) {
            clearErrorStatus();
  
            MessageBoxes.createOk().withBody("Expérimental: dépôt de fichier").show();
            
            if (transferObject instanceof ResourceListTransferObject) {
              List<Long> partsList = new LinkedList<>();
              partsList.add(partId);
              
              ResourceListTransferObject rlto = (ResourceListTransferObject) transferObject;
              for (BinaryResource document : rlto.getResources()) {
                DocumentForm form = new DocumentForm();
                form.startNew(document);
                form.getPartsField().getTable().addParts(partsList);
              }
              
            }
          }
  
        }
        
        @Order(5500)
        public class PartNumbersField extends AbstractStringField {
          @Override
          protected int getConfiguredGridW() {
            return 2;
          }
          @Override
          protected boolean getConfiguredStatusVisible() {
            return false;
          }
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("PartNumbers");
          }

          @Override
          protected byte getConfiguredLabelPosition() {
            return LABEL_POSITION_TOP;
          }
          @Override
          protected int getConfiguredMaxLength() {
            return 128;
          }
        }

        @Order(7000)
        public class GotoDatenbankButton extends AbstractLinkButton {
          @Override
          protected String getConfiguredLabel() {
            return TEXTS.get("GotoDatenbank");
          }

          @Override
          protected void execClickAction() {
            ClientSessionProvider.currentSession().getDesktop().openUri("https://ft-datenbank.de/ft-article/"+getPartId(), OpenUriAction.NEW_WINDOW);
          }
          @Override
          protected boolean getConfiguredProcessButton() {
            return false;
          }
        }

        
      }
      
      @Order(7000)
      public class PartsField extends AbstractPartsField {

        @Override
        protected void setTotalValue(Double value) {
          getValueField().setValue(value);
        }

        @Override
        protected void setPartsCount(Integer partsCount) {
          // TODO Auto-generated method stub
          
        }
        
        @Override
        protected int getConfiguredGridW() {
          return 6;
        }
      }

      @Order(8000)
      public class BoxesField extends AbstractTableField<BoxesField.Table> {
        protected ITableControl createAggregateTableControl() {
          return new AggregateTableControl();
        }

        @Override
        protected void execInitField() {
          super.execInitField();
          ITableControl control = createAggregateTableControl();
          if (control != null) {
            getTable().addTableControl(control);
          }
          
        }
        
        public class Table extends AbstractTable {

          public LocationColumn getLocationColumn() {
            return getColumnSet().getColumnByClass(LocationColumn.class);
          }

          public LotAchatColumn getLotAchatColumn() {
            return getColumnSet().getColumnByClass(LotAchatColumn.class);
          }

          public ThisPartCountColumn getThisPartCountColumn() {
            return getColumnSet().getColumnByClass(ThisPartCountColumn.class);
          }

          public LabelColumn getLabelColumn() {
            return getColumnSet().getColumnByClass(LabelColumn.class);
          }

          public IdColumn getIdColumn() {
            return getColumnSet().getColumnByClass(IdColumn.class);
          }
          
          @Override
          protected Class<? extends IMenu> getConfiguredDefaultMenu() {
            return EditMenu.class;
          }

          @Override
          protected void execInitTable() {
            super.execInitTable();
            ColumnUserFilterState state = new ColumnUserFilterState(getLotAchatColumn());
            Set<Object> values = new HashSet<>();
            values.add(0);
            state.setSelectedValues(values);
            getUserFilterManager().addFilter(state);
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
              BoxForm form = ((Desktop) ClientSession.get().getDesktop()).findBoxForm(boxId, new RowChangedListener());
            }
          }

          
          @Order(1000)
          public class IdColumn extends AbstractLongColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("Ident");
            }

            @Override
            protected int getConfiguredWidth() {
              return 60;
            }
            @Override
            protected boolean getConfiguredVisible() {
              return false;
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
              return 300;
            }
          }

          @Order(3000)
          public class ThisPartCountColumn extends AbstractIntegerColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("ThisPartCount");
            }

            @Override
            protected int getConfiguredWidth() {
              return 60;
            }
          }


          @Order(4000)
          public class LocationColumn extends AbstractSmartColumn<Long> {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("Location");
            }

            @Override
            protected int getConfiguredWidth() {
              return 150;
            }
            
            @Override
            protected Class<? extends ICodeType<?, Long>> getConfiguredCodeType() {
              return LocationCodeType.class;
            }
          }


          @Order(5000)
          public class LotAchatColumn extends AbstractBooleanColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("LotAchat");
            }

            @Override
            protected int getConfiguredWidth() {
              return 10;
            }
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
                  
                  getLabelColumn().setValue(row, partTitle);
                }
                reloadTableData();
              }
            }
          }
          
        }

        @Override
        protected int getConfiguredGridW() {
          return 1;
        }
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Boxes");
        }

        @Override
        protected byte getConfiguredLabelPosition() {
          return LABEL_POSITION_TOP;
        }
        @Override
        protected int getConfiguredGridH() {
          return 6;
        }
        @Override
        protected double getConfiguredGridWeightY() {
          return 1.0;
        }
        
        @Override
        protected boolean getConfiguredStatusVisible() {
          return false;
        }
      }

      @Order(9000)
      public class KitsField extends AbstractTableField<KitsField.Table> {
        public class Table extends AbstractTable {

          public IconColumn getIconColumn() {
            return getColumnSet().getColumnByClass(IconColumn.class);
          }

          public ThisPartCountColumn getThisPartCountColumn() {
            return getColumnSet().getColumnByClass(ThisPartCountColumn.class);
          }

          public LabelColumn getLabelColumn() {
            return getColumnSet().getColumnByClass(LabelColumn.class);
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
              return 60;
            }
            @Override
            protected boolean getConfiguredVisible() {
              return false;
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
              return 300;
            }
          }

          @Order(3000)
          public class IconColumn extends AbstractIconColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("Icon");
            }

            @Override
            protected int getConfiguredWidth() {
              return 100;
            }
            
            @Override
            protected String getConfiguredCssClass() {
              return "part-icon";
            }
          }

          @Order(4000)
          public class ThisPartCountColumn extends AbstractIntegerColumn {
            @Override
            protected String getConfiguredHeaderText() {
              return TEXTS.get("ThisPartCount");
            }

            @Override
            protected int getConfiguredWidth() {
              return 60;
            }
          }
          
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
              form.startModify();
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
          
          
          
        }

        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Kits");
        }

        @Override
        protected int getConfiguredGridH() {
          return 6;
        }
        @Override
        protected int getConfiguredGridW() {
          return 1;
        }
        @Override
        protected double getConfiguredGridWeightY() {
          return 1.0;
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
        protected void execInitField() {
          super.execInitField();
          ITableControl control = createAggregateTableControl();
          if (control != null) {
            getTable().addTableControl(control);
          }
          
        }
        protected ITableControl createAggregateTableControl() {
          return new AggregateTableControl();
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
    public class SyncButton extends AbstractButton {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("SyncFromDatenbank");
      }

      @Override
      protected void execClickAction() {
        IPartService service = BEANS.get(IPartService.class);
        service.syncFromDatenbank(getIdField().getValue());
      }
    }

    @Order(0)
    public class ShopMenu extends AbstractMenu {
      @Override
      protected String getConfiguredText() {
        return TEXTS.get("Shop");
      }

      @Override
      protected void execAction() {
      }

      @Override
      protected void injectActionNodesInternal(OrderedCollection<IMenu> actionNodes) {
        super.injectActionNodesInternal(actionNodes);
        
        IShopService service = BEANS.get(IShopService.class);
        ShopTablePageData shopPageData = service.getShopTableData(null);
        for (ShopTableRowData shopData :shopPageData.getRows()) {
          String shopName = shopData.getLabel();
          String shopQuery = shopData.getQuery();
          
          IMenu shopMenu = new AbstractMenu() {
            @Override
            public String getText() {
              return shopName;
            }
            
            @Override
            protected void execAction() {
              // Récupérer le part number dans le champ (prendre la dernière valeur si plusieurs)
              String partNumberData = getPartNumbersField().getValue();
              Pattern pnPattern = Pattern.compile(".*(?:\\D|^)(\\d+)");
              Matcher m = pnPattern.matcher(partNumberData);
              if (m.matches()) {
                String partNumber = m.group(1);
                
                // Construire l'URL
                String query = shopQuery;
                query = query.replaceAll("\\{partNumber\\}", partNumber);
                ClientSessionProvider.currentSession().getDesktop().openUri(query, OpenUriAction.NEW_WINDOW);              
              }
            }
          };
          actionNodes.addLast(shopMenu);
        }
      }
      
      
    }
    
    
    
    @Order(1000)
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
  }

  public class NewHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      IPartService service = BEANS.get(IPartService.class);
      PartFormData formData = new PartFormData();
      exportFormData(formData);
      formData = service.prepareCreate(formData);
      importFormData(formData);

      setEnabledPermission(new CreatePartPermission());
    }

    @Override
    protected void execStore() {
      IPartService service = BEANS.get(IPartService.class);
      PartFormData formData = new PartFormData();
      exportFormData(formData);
      service.create(formData);
    }
  }

  public class ModifyHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      IPartService service = BEANS.get(IPartService.class);
      PartFormData formData = new PartFormData();
      exportFormData(formData);
      formData = service.load(formData);
      importFormData(formData);
      Long imageId = formData.getImageIdProperty().getValue();
      if (imageId != null) {
        getIconField().setImageId(String.valueOf(imageId));
        
        getPartsField().setVisible(formData.getParts().getRowCount()>0);
        getBoxesField().setVisible(formData.getBoxes().getRowCount()>0);
        getKitsField().setVisible(formData.getKits().getRowCount()>0);
        setTitle(formData.getTitle().getValue());
        
        if (formData.getParts().getRowCount()>0) {
          getPartsField().updateCalculatedValues(null);
        }
      } 
      
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
      
      setEnabledPermission(new UpdatePartPermission());
    }

    @Override
    protected void execStore() {
      IPartService service = BEANS.get(IPartService.class);
      PartFormData formData = new PartFormData();
      exportFormData(formData);
      service.store(formData);
    }
  }
}
