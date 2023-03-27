package pers.mr.ft.inventory.client.forms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.controls.AggregateTableControl;
import org.eclipse.scout.rt.client.ui.basic.table.controls.ITableControl;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.LogicalGridLayoutConfig;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.filechooserfield.AbstractFileChooserField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.IGroupBoxBodyGrid;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.internal.HorizontalGroupBoxBodyGrid;
import org.eclipse.scout.rt.client.ui.form.fields.longfield.AbstractLongField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;

import pers.mr.ft.inventory.client.ClientSession;
import pers.mr.ft.inventory.client.fields.AbstractIdField;
import pers.mr.ft.inventory.client.fields.AbstractPartsField;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.CancelButton;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.GroupBox;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.GroupBox.DocumentFileChooserField;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.GroupBox.FTDBIdField;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.GroupBox.FTDBNameField;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.GroupBox.IdField;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.GroupBox.NameField;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.GroupBox.PartNumberField;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.GroupBox.PartsField;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.OkButton;
import pers.mr.ft.inventory.shared.forms.CreateDocumentPermission;
import pers.mr.ft.inventory.shared.forms.DocumentFormData;
import pers.mr.ft.inventory.shared.forms.IDocumentService;
import pers.mr.ft.inventory.shared.forms.UpdateDocumentPermission;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.GroupBox.YearField;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.OpenButton;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.GroupBox.LangField;
import pers.mr.ft.inventory.client.forms.DocumentForm.MainBox.GroupBox.ExtensionField;

@FormData(value = DocumentFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class DocumentForm extends AbstractForm {
  private Long docId = null;
  
  @FormData
  public Long getDocId() {
    return docId;
  }

  @FormData
  public void setDocId(Long docId) {
    this.docId = docId;
  }

  public void setDocument(BinaryResource document) {
    getDocumentFileChooserField().setValue(document);
  }
  
  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Document");
  }

  @Override
  protected int getConfiguredDisplayHint() {
    return IForm.DISPLAY_HINT_VIEW;
  }
  
  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public GroupBox getGroupBox() {
    return getFieldByClass(GroupBox.class);
  }

  public DocumentFileChooserField getDocumentFileChooserField() {
    return getFieldByClass(DocumentFileChooserField.class);
  }

  public IdField getIdField() {
    return getFieldByClass(IdField.class);
  }

  public FTDBNameField getFTDBNameField() {
    return getFieldByClass(FTDBNameField.class);
  }

  public FTDBIdField getFTDBIdField() {
    return getFieldByClass(FTDBIdField.class);
  }

  public PartNumberField getPartNumberField() {
    return getFieldByClass(PartNumberField.class);
  }

  public YearField getYearField() {
    return getFieldByClass(YearField.class);
  }

  public OpenButton getOpenButton() {
    return getFieldByClass(OpenButton.class);
  }

  public LangField getLangField() {
    return getFieldByClass(LangField.class);
  }

  public ExtensionField getExtensionField() {
    return getFieldByClass(ExtensionField.class);
  }

  public NameField getNameField() {
    return getFieldByClass(NameField.class);
  }

  public PartsField getPartsField() {
    return getFieldByClass(PartsField.class);
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
      protected int getConfiguredGridColumnCount() {
        return 6;
      }
      
      @Override
      protected Class<? extends IGroupBoxBodyGrid> getConfiguredBodyGrid() {
        return HorizontalGroupBoxBodyGrid.class;
      }
      
      @Order(1000)
      public class IdField extends AbstractIdField {
      }


      @Order(2000)
      public class FTDBIdField extends AbstractLongField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("FTDBId");
        }

        @Override
        protected Long getConfiguredMinValue() {
          return 0L;
        }

        @Override
        protected Long getConfiguredMaxValue() {
          return 999999999999L;
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
        protected boolean getConfiguredGroupingUsed() {
          return false;
        }
      }

      @Order(3000)
      public class PartNumberField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("PartNumber");
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 6;
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


      @Order(4000)
      public class YearField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Year");
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 4;
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


      @Order(4500)
      public class LangField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Lang");
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
        protected int getConfiguredMaxLength() {
          return 2;
        }
      }
      
      

      @Order(5000)
      public class NameField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Name");
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 200;
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
      }


      @Order(6000)
      public class FTDBNameField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("FTDBName");
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 200;
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
      }



      
      @Order(10000)
      public class DocumentFileChooserField extends AbstractFileChooserField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("ChooseFile");
        }
        
        @Override
        protected void execChangedValue() {
          if (getValue()!=null) {
            String name = getValue().getFilename();
            String docName = name;
            Pattern pnPattern = Pattern.compile("(\\d{5,6})[ab]?([- _]+(\\d{4})?[- _]*)?(.*)");
            Matcher m = pnPattern.matcher(name);
            if (m.matches()) {
              String partNumber = m.group(1);
              String year = m.group(3);
              
              if (getPartNumberField().getValue()==null) {
                getPartNumberField().setValue(partNumber);
              }
              if (getYearField().getValue()==null) {
                getYearField().setValue(year);
              }
              docName = m.group(4);

            }
            if (getNameField().getValue()==null) {
              getNameField().setValue(docName);
            }
            /*
            if (getFTDBNameField().getValue()==null) {
              getFTDBNameField().setValue(name);
            }
            */
            
          }
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
      }


      @Order(11000)
      public class ExtensionField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Extension");
        }

        @Override
        protected byte getConfiguredLabelPosition() {
          return LABEL_POSITION_TOP;
        }
        @Override
        protected int getConfiguredMaxLength() {
          return 20;
        }
      }

      
      @Order(15000)
      public class PartsField extends AbstractPartsField {

        @Override
        protected boolean getConfiguredEditable() {
          return true;
        }
        @Override
        protected void setTotalValue(Double value) {
        }

        @Override
        protected void setPartsCount(Integer partsCount) {
          
        }
        
        @Override
        protected ITableControl createAggregateTableControl() {
          return new AggregateTableControl();
        }
        
        @Override
        protected int getConfiguredGridW() {
          return 4;
        }

        @Override
        protected boolean getConfiguredStatusVisible() {
          return false;
        }
        
        @Override
        protected double getConfiguredGridWeightY() {
          return 1.0;
        }
        
        @Override
        protected void execInitField() {
          super.execInitField();
          getTable().getCountColumn().setDisplayable(false);
          getTable().getColorColumn().setDisplayable(false);
          getTable().getPartValueColumn().setDisplayable(false);
          getTable().getValueColumn().setDisplayable(false);
          getTable().getKitCountColumn().setDisplayable(false);
          getTable().getOldIdColumn().setDisplayable(false);
        }
      }
    }


    @Order(1500)
    public class OpenButton extends AbstractButton {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("Open");
      }

      @Override
      protected void execClickAction() {
        BinaryResource document = getDocumentFileChooserField().getValue();
        if (document==null && docId!=null) {
          IDocumentService service = BEANS.get(IDocumentService.class);
          document = service.loadContent(docId);
        }
        if (document != null) {
          ClientSession.get().getDesktop().openUri(document, OpenUriAction.DOWNLOAD);
        }
      }
    }

    
    @Order(2000)
    public class OkButton extends AbstractOkButton {

    }

    @Order(3000)
    public class CancelButton extends AbstractCancelButton {

    }
  }

  public void startModify() {
    startInternalExclusive(new ModifyHandler());
  }

  public void startNew() {
    startInternal(new NewHandler());
  }

  public void startNew(BinaryResource document) {
    startInternal(new NewHandler());
    setDocument(document);
  }

  public class NewHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      IDocumentService service = BEANS.get(IDocumentService.class);
      DocumentFormData formData = new DocumentFormData();
      exportFormData(formData);
      formData = service.prepareCreate(formData);
      importFormData(formData);

      setEnabledPermission(new CreateDocumentPermission());
    }

    @Override
    protected void execStore() {
      IDocumentService service = BEANS.get(IDocumentService.class);
      DocumentFormData formData = new DocumentFormData();
      exportFormData(formData);
      service.create(formData);
    }
  }

  public class ModifyHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      IDocumentService service = BEANS.get(IDocumentService.class);
      DocumentFormData formData = new DocumentFormData();
      exportFormData(formData);
      formData = service.load(formData);
      importFormData(formData);
      setTitle(formData.getName().getValue());
      
      setEnabledPermission(new UpdateDocumentPermission());
    }

    @Override
    protected void execStore() {
      IDocumentService service = BEANS.get(IDocumentService.class);
      DocumentFormData formData = new DocumentFormData();
      exportFormData(formData);
      service.store(formData);
    }
  }
}
