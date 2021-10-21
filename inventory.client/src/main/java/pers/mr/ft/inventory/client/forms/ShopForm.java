package pers.mr.ft.inventory.client.forms;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import pers.mr.ft.inventory.client.fields.AbstractIdField;
import pers.mr.ft.inventory.client.forms.ShopForm.MainBox.CancelButton;
import pers.mr.ft.inventory.client.forms.ShopForm.MainBox.GroupBox;
import pers.mr.ft.inventory.client.forms.ShopForm.MainBox.GroupBox.QueryField;
import pers.mr.ft.inventory.client.forms.ShopForm.MainBox.OkButton;
import pers.mr.ft.inventory.shared.forms.CreateShopPermission;
import pers.mr.ft.inventory.shared.forms.IShopService;
import pers.mr.ft.inventory.shared.forms.ShopFormData;
import pers.mr.ft.inventory.shared.forms.UpdateShopPermission;

@FormData(value = ShopFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class ShopForm extends AbstractDesktopForm {
  @Override
  protected String getConfiguredTitle() {
    // TODO [mreverbel] verify translation
    return TEXTS.get("Shop");
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public GroupBox getGroupBox() {
    return getFieldByClass(GroupBox.class);
  }

  public QueryField getQueryField() {
    return getFieldByClass(QueryField.class);
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
        protected int getConfiguredMaxLength() {
          return 250;
        }
      }

      @Order(3000)
      public class QueryField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Query");
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
          return 500;
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
      ShopFormData formData = new ShopFormData();
      exportFormData(formData);
      formData = BEANS.get(IShopService.class).prepareCreate(formData);
      importFormData(formData);

      setEnabledPermission(new CreateShopPermission());
    }

    @Override
    protected void execStore() {
      ShopFormData formData = new ShopFormData();
      exportFormData(formData);
      formData = BEANS.get(IShopService.class).create(formData);
      importFormData(formData);
    }
  }

  public class ModifyHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      ShopFormData formData = new ShopFormData();
      exportFormData(formData);
      formData = BEANS.get(IShopService.class).load(formData);
      importFormData(formData);

      setEnabledPermission(new UpdateShopPermission());
    }

    @Override
    protected void execStore() {
      ShopFormData formData = new ShopFormData();
      exportFormData(formData);
      formData = BEANS.get(IShopService.class).store(formData);
      importFormData(formData);
    }
  }
}
