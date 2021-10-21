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
import pers.mr.ft.inventory.client.forms.ReportModelForm.MainBox.CancelButton;
import pers.mr.ft.inventory.client.forms.ReportModelForm.MainBox.GroupBox;
import pers.mr.ft.inventory.client.forms.ReportModelForm.MainBox.OkButton;
import pers.mr.ft.inventory.shared.forms.CreateReportModelPermission;
import pers.mr.ft.inventory.shared.forms.IReportModelService;
import pers.mr.ft.inventory.shared.forms.ReportModelFormData;
import pers.mr.ft.inventory.shared.forms.UpdateReportModelPermission;
import pers.mr.ft.inventory.client.forms.ReportModelForm.MainBox.GroupBox.TypeField;
import pers.mr.ft.inventory.client.forms.ReportModelForm.MainBox.GroupBox.NameField;
import pers.mr.ft.inventory.client.forms.ReportModelForm.MainBox.GroupBox.StyleSheetField;

@FormData(value = ReportModelFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class ReportModelForm extends AbstractDesktopForm {
  @Override
  protected String getConfiguredTitle() {
    // TODO [mreverbel] verify translation
    return TEXTS.get("ReportModel");
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public GroupBox getGroupBox() {
    return getFieldByClass(GroupBox.class);
  }

  public TypeField getTypeField() {
    return getFieldByClass(TypeField.class);
  }

  public StyleSheetField getStyleSheetField() {
    return getFieldByClass(StyleSheetField.class);
  }

  public NameField getNameField() {
    return getFieldByClass(NameField.class);
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
      public class TypeField extends AbstractStringField {
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
        protected int getConfiguredMaxLength() {
          return 40;
        }
      }

      @Order(3000)
      public class NameField extends AbstractStringField {
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
          return 250;
        }
      }

      @Order(4000)
      public class StyleSheetField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("StyleSheet");
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
          return 250;
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
      ReportModelFormData formData = new ReportModelFormData();
      exportFormData(formData);
      formData = BEANS.get(IReportModelService.class).prepareCreate(formData);
      importFormData(formData);

      setEnabledPermission(new CreateReportModelPermission());
    }

    @Override
    protected void execStore() {
      ReportModelFormData formData = new ReportModelFormData();
      exportFormData(formData);
      formData = BEANS.get(IReportModelService.class).create(formData);
      importFormData(formData);
    }
  }

  public class ModifyHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      ReportModelFormData formData = new ReportModelFormData();
      exportFormData(formData);
      formData = BEANS.get(IReportModelService.class).load(formData);
      importFormData(formData);

      setEnabledPermission(new UpdateReportModelPermission());
    }

    @Override
    protected void execStore() {
      ReportModelFormData formData = new ReportModelFormData();
      exportFormData(formData);
      formData = BEANS.get(IReportModelService.class).store(formData);
      importFormData(formData);
    }
  }
}
