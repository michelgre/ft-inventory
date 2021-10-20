package pers.mr.ft.inventory.client.forms;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractSearchForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.AbstractValueField;
import org.eclipse.scout.rt.client.ui.form.fields.IFormField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractSearchButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import pers.mr.ft.inventory.client.forms.BoxSearchForm.MainBox.ClearButton;
import pers.mr.ft.inventory.client.forms.PartSearchForm.MainBox.GroupBox;
import pers.mr.ft.inventory.client.forms.PartSearchForm.MainBox.GroupBox.LabelField;
import pers.mr.ft.inventory.client.forms.PartSearchForm.MainBox.GroupBox.PartNumberField;
import pers.mr.ft.inventory.client.forms.PartSearchForm.MainBox.OkButton;
import pers.mr.ft.inventory.shared.forms.CreatePartSearchPermission;
import pers.mr.ft.inventory.shared.forms.IPartSearchService;
import pers.mr.ft.inventory.shared.forms.PartSearchFormData;
import pers.mr.ft.inventory.shared.forms.UpdatePartSearchPermission;

@FormData(value = PartSearchFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class PartSearchForm extends AbstractSearchForm {
  //PartSearchFormData initialData = null;
  //PartSearchFormData mandatoryData = null;
  @Override
  protected String getConfiguredTitle() {
    // TODO [mreverbel] verify translation
    return TEXTS.get("PartSearch");
  }

  @Override
  protected String getConfiguredCssClass() {
    return "part-search-form";
  }
  
  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public GroupBox getGroupBox() {
    return getFieldByClass(GroupBox.class);
  }

  public LabelField getLabelField() {
    return getFieldByClass(LabelField.class);
  }

  public PartNumberField getPartNumberField() {
    return getFieldByClass(PartNumberField.class);
  }

  public OkButton getOkButton() {
    return getFieldByClass(OkButton.class);
  }

  public ClearButton getClearButtonn() {
    return getFieldByClass(ClearButton.class);
  }

  @Order(1000)
  public class MainBox extends AbstractGroupBox {
    @Order(1000)
    public class GroupBox extends AbstractGroupBox {

      @Order(1000)
      public class LabelField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Label");
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 200;
        }
      }

      @Order(2000)
      public class PartNumberField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("PartNumber");
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 128;
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

  public void clearFields () {
    // Vide tous les champs (comment faire + simple ?)
    for (IFormField field: getAllFields()) {
      if (field instanceof AbstractValueField) {
        AbstractValueField<?> valueField = (AbstractValueField<?>) field;
        valueField.setValue(null);
      }
    }
    
    /*
    setSearchData(initialData, false);
    setSearchData(mandatoryData, true);
    */
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
      IPartSearchService service = BEANS.get(IPartSearchService.class);
      PartSearchFormData formData = new PartSearchFormData();
      exportFormData(formData);
      formData = service.prepareCreate(formData);
      importFormData(formData);

      setEnabledPermission(new CreatePartSearchPermission());
    }

    @Override
    protected void execStore() {
      IPartSearchService service = BEANS.get(IPartSearchService.class);
      PartSearchFormData formData = new PartSearchFormData();
      exportFormData(formData);
      service.create(formData);
    }
  }

  public class ModifyHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      IPartSearchService service = BEANS.get(IPartSearchService.class);
      PartSearchFormData formData = new PartSearchFormData();
      exportFormData(formData);
      formData = service.load(formData);
      importFormData(formData);

      setEnabledPermission(new UpdatePartSearchPermission());
    }

    @Override
    protected void execStore() {
      IPartSearchService service = BEANS.get(IPartSearchService.class);
      PartSearchFormData formData = new PartSearchFormData();
      exportFormData(formData);
      service.store(formData);
    }
  }
}
