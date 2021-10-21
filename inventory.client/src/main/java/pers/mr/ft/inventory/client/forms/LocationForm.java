package pers.mr.ft.inventory.client.forms;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import pers.mr.ft.inventory.client.fields.AbstractIdField;
import pers.mr.ft.inventory.client.forms.LocationForm.MainBox.CancelButton;
import pers.mr.ft.inventory.client.forms.LocationForm.MainBox.GroupBox;
import pers.mr.ft.inventory.client.forms.LocationForm.MainBox.OkButton;
import pers.mr.ft.inventory.shared.forms.CreateLocationPermission;
import pers.mr.ft.inventory.shared.forms.ILocationService;
import pers.mr.ft.inventory.shared.forms.LocationFormData;
import pers.mr.ft.inventory.shared.forms.UpdateLocationPermission;
import pers.mr.ft.inventory.client.forms.LocationForm.MainBox.GroupBox.LocationField;
import pers.mr.ft.inventory.client.forms.LocationForm.MainBox.GroupBox.DescriptionField;

@FormData(value = LocationFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class LocationForm extends AbstractDesktopForm {
  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Location");
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public GroupBox getGroupBox() {
    return getFieldByClass(GroupBox.class);
  }

  public DescriptionField getDescriptionField() {
    return getFieldByClass(DescriptionField.class);
  }

  public LocationField getLocationField() {
    return getFieldByClass(LocationField.class);
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
      @Order(1000)
      public class IdField extends AbstractIdField {
      }

      @Order(2000)
      public class LocationField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Name");
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
          return 300;
        }
      }

      @Order(3000)
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
        protected boolean getConfiguredStatusVisible() {
          return false;
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 400;
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

  public class NewHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      LocationFormData formData = new LocationFormData();
      exportFormData(formData);
      formData = BEANS.get(ILocationService.class).prepareCreate(formData);
      importFormData(formData);

      setEnabledPermission(new CreateLocationPermission());
    }

    @Override
    protected void execStore() {
      LocationFormData formData = new LocationFormData();
      exportFormData(formData);
      formData = BEANS.get(ILocationService.class).create(formData);
      importFormData(formData);
    }
  }

  public class ModifyHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      LocationFormData formData = new LocationFormData();
      exportFormData(formData);
      formData = BEANS.get(ILocationService.class).load(formData);
      importFormData(formData);

      setEnabledPermission(new UpdateLocationPermission());
    }

    @Override
    protected void execStore() {
      LocationFormData formData = new LocationFormData();
      exportFormData(formData);
      formData = BEANS.get(ILocationService.class).store(formData);
      importFormData(formData);
    }
  }
}
