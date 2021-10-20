package pers.mr.ft.inventory.client.forms;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.longfield.AbstractLongField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import pers.mr.ft.inventory.client.fields.AbstractIdField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.DimensionsBox.HeightField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.DimensionsBox.LengthField;
import pers.mr.ft.inventory.client.forms.BoxForm.MainBox.GroupBox.BoxFieldsBox.DimensionsBox.WidthField;
import pers.mr.ft.inventory.client.forms.BoxTypeForm.MainBox.CancelButton;
import pers.mr.ft.inventory.client.forms.BoxTypeForm.MainBox.GroupBox;
import pers.mr.ft.inventory.client.forms.BoxTypeForm.MainBox.GroupBox.DescriptionField;
import pers.mr.ft.inventory.client.forms.BoxTypeForm.MainBox.GroupBox.IdField;
import pers.mr.ft.inventory.client.forms.BoxTypeForm.MainBox.OkButton;
import pers.mr.ft.inventory.shared.forms.BoxTypeFormData;
import pers.mr.ft.inventory.shared.forms.CreateBoxTypePermission;
import pers.mr.ft.inventory.shared.forms.IBoxTypeService;
import pers.mr.ft.inventory.shared.forms.UpdateBoxTypePermission;

@FormData(value = BoxTypeFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class BoxTypeForm extends AbstractForm {
  private Long boxTypeId = 0L;
  
  @FormData
  public Long getBoxTypeId() {
    return boxTypeId;
  }

  @FormData
  public void setBoxTypeId(Long boxTypeId) {
    this.boxTypeId = boxTypeId;
  }

  @Override
  protected String getConfiguredTitle() {
    // TODO [mreverbel] verify translation
    return TEXTS.get("BoxType");
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

  public DescriptionField getDescriptionField() {
    return getFieldByClass(DescriptionField.class);
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
      public class DescriptionField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Description");
        }

        @Override
        protected int getConfiguredMaxLength() {
          return 250;
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
      
      @Order(3000)
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

      @Order(4000)
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

      @Order(5000)
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
      IBoxTypeService service = BEANS.get(IBoxTypeService.class);
      BoxTypeFormData formData = new BoxTypeFormData();
      exportFormData(formData);
      formData = service.prepareCreate(formData);
      importFormData(formData);

      setEnabledPermission(new CreateBoxTypePermission());
    }

    @Override
    protected void execStore() {
      IBoxTypeService service = BEANS.get(IBoxTypeService.class);
      BoxTypeFormData formData = new BoxTypeFormData();
      exportFormData(formData);
      service.create(formData);
    }
  }

  public class ModifyHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      IBoxTypeService service = BEANS.get(IBoxTypeService.class);
      BoxTypeFormData formData = new BoxTypeFormData();
      exportFormData(formData);
      formData = service.load(formData);
      importFormData(formData);

      setEnabledPermission(new UpdateBoxTypePermission());
    }

    @Override
    protected void execStore() {
      IBoxTypeService service = BEANS.get(IBoxTypeService.class);
      BoxTypeFormData formData = new BoxTypeFormData();
      exportFormData(formData);
      service.store(formData);
    }
  }
}
