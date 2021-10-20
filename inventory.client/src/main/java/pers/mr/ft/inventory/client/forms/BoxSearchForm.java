package pers.mr.ft.inventory.client.forms;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractSearchForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.AbstractValueField;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractSearchButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.IGroupBoxBodyGrid;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.internal.HorizontalGroupBoxBodyGrid;
import org.eclipse.scout.rt.client.ui.form.fields.longfield.AbstractLongField;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICodeType;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import pers.mr.ft.inventory.client.forms.BoxSearchForm.MainBox.ClearButton;
import pers.mr.ft.inventory.client.forms.BoxSearchForm.MainBox.GroupBox;
import pers.mr.ft.inventory.client.forms.BoxSearchForm.MainBox.GroupBox.BoxTypeField;
import pers.mr.ft.inventory.client.forms.BoxSearchForm.MainBox.GroupBox.IdField;
import pers.mr.ft.inventory.client.forms.BoxSearchForm.MainBox.GroupBox.LabelField;
import pers.mr.ft.inventory.client.forms.BoxSearchForm.MainBox.GroupBox.LocationField;
import pers.mr.ft.inventory.client.forms.BoxSearchForm.MainBox.GroupBox.MainBoxField;
import pers.mr.ft.inventory.client.forms.BoxSearchForm.MainBox.OkButton;
import pers.mr.ft.inventory.shared.codetype.LocationCodeType;
import pers.mr.ft.inventory.shared.forms.BoxSearchFormData;
import pers.mr.ft.inventory.shared.forms.CreateBoxSearchPermission;
import pers.mr.ft.inventory.shared.forms.UpdateBoxSearchPermission;
import pers.mr.ft.inventory.shared.lookup.BoxTypeLookupCall;

@FormData(value = BoxSearchFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class BoxSearchForm extends AbstractSearchForm {
  BoxSearchFormData initialData = null;
  BoxSearchFormData mandatoryData = null;
  
  @Override
  protected String getConfiguredTitle() {
    // TODO [michel] verify translation
    return TEXTS.get("BoxSearch");
  }

  @Override
  protected String getConfiguredCssClass() {
    return "box-search-form";
  }
  
  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public GroupBox getGroupBox() {
    return getFieldByClass(GroupBox.class);
  }

  public MainBoxField getMainBoxField() {
    return getFieldByClass(MainBoxField.class);
  }

  public BoxTypeField getBoxTypeField() {
    return getFieldByClass(BoxTypeField.class);
  }

  public IdField getIdField() {
    return getFieldByClass(IdField.class);
  }

  public LabelField getLabelField() {
    return getFieldByClass(LabelField.class);
  }

  public LocationField getLocationField() {
    return getFieldByClass(LocationField.class);
  }

  public ClearButton getClearButton() {
    return getFieldByClass(ClearButton.class);
  }

  public OkButton getOkButton() {
    return getFieldByClass(OkButton.class);
  }

  @Order(1000)
  public class MainBox extends AbstractGroupBox {
    
    @Order(1000)
    public class GroupBox extends AbstractGroupBox {
      
      @Override
      protected Class<? extends IGroupBoxBodyGrid> getConfiguredBodyGrid() {
        return HorizontalGroupBoxBodyGrid.class;
      }
      @Override
      protected int getConfiguredGridColumnCount() {
        return 9;
      }

      @Order(1000)
      public class MainBoxField extends AbstractBooleanField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("MainBox");
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
        protected boolean getConfiguredTriStateEnabled() {
          return true;
        }
        @Override
        protected int getConfiguredGridW() {
          return 2;
        }
      }

      @Order(2000)
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
        protected boolean getConfiguredStatusVisible() {
          return false;
        }
        @Override
        protected int getConfiguredGridW() {
          return 4;
        }
        @Override
        protected Class<? extends ILookupCall<Long>> getConfiguredLookupCall() {
          return BoxTypeLookupCall.class;
        }
      }

      @Order(3000)
      public class IdField extends AbstractLongField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Identifier");
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
          return 1;
        }
        @Override
        protected Long getConfiguredMinValue() {
          return 0L;
        }

        @Override
        protected Long getConfiguredMaxValue() {
          return 999999999999L;
        }
      }

      @Order(4000)
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
        protected boolean getConfiguredStatusVisible() {
          return false;
        }
        @Override
        protected int getConfiguredGridW() {
          return 3;
        }
        @Override
        protected int getConfiguredMaxLength() {
          return 128;
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
      
      
      
    }

    @Order(2000)
    public class OkButton extends AbstractSearchButton {

    }

    @Order(2500)
    public class ClearButton extends AbstractButton {
      @Override
      protected String getConfiguredLabel() {
        return TEXTS.get("Clear");
      }

      @Override
      protected int getConfiguredSystemType() {
        return SYSTEM_TYPE_NONE;
      }

      @Override
      protected void execClickAction() {
        clearFields ();
      }
    }
    
  }

  @Override
  protected void execInitForm() {
    clearFields ();
  }
  
  public void setInitialData(BoxSearchFormData formData) {
    initialData = formData;
  }
  
  public void setMandatoryData(BoxSearchFormData formData) {
    mandatoryData = formData;
  }
  
  protected void setSearchData(BoxSearchFormData formData, boolean isMandatory) {
    if (formData!=null) {
      getMainBoxField().setValue(null); // Pour le 3 états
      importFormData(formData);
      
      if (isMandatory) {
        getMainBoxField().setEnabled(!formData.getMainBox().isValueSet());
        getBoxTypeField().setEnabled(!formData.getBoxType().isValueSet());
        getIdField().setEnabled(!formData.getId().isValueSet());
        getLabelField().setEnabled(!formData.getLabel().isValueSet());
        getLocationField().setEnabled(!formData.getLocation().isValueSet());
      }
    }
  }
  public void clearFields () {
    // Vide tous les champs (comment faire + simple ?)
    for (IFormField field: getAllFields()) {
      if (field instanceof AbstractValueField) {
        AbstractValueField<?> valueField = (AbstractValueField<?>) field;
        valueField.setValue(null);
      }
    }
    
    setSearchData(initialData, false);
    setSearchData(mandatoryData, true);
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

      setEnabledPermission(new CreateBoxSearchPermission());
    }

    @Override
    protected void execStore() {
    }
  }

  public class ModifyHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {

      setEnabledPermission(new UpdateBoxSearchPermission());
    }

    @Override
    protected void execStore() {
    }
  }
  
  public String toString() {
    SearchFilter filter = getSearchFilter();
    if (filter==null) {
      return super.toString();
    }
    else {
      return "BoxSearchForm[" + filter.toString() + "]";
    }
  }
}
