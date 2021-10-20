package pers.mr.ft.inventory.client.forms;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.basic.table.controls.AggregateTableControl;
import org.eclipse.scout.rt.client.ui.basic.table.controls.ITableControl;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.longfield.AbstractLongField;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import pers.mr.ft.inventory.client.fields.AbstractPartsField;
import pers.mr.ft.inventory.client.forms.MovePartsForm.MainBox.CancelButton;
import pers.mr.ft.inventory.client.forms.MovePartsForm.MainBox.GroupBox;
import pers.mr.ft.inventory.client.forms.MovePartsForm.MainBox.GroupBox.FromBoxIdField;
import pers.mr.ft.inventory.client.forms.MovePartsForm.MainBox.GroupBox.FromBoxLabelField;
import pers.mr.ft.inventory.client.forms.MovePartsForm.MainBox.GroupBox.ToBoxField;
import pers.mr.ft.inventory.client.forms.MovePartsForm.MainBox.OkButton;
import pers.mr.ft.inventory.shared.forms.BoxFormData;
import pers.mr.ft.inventory.shared.forms.CreateMovePartsPermission;
import pers.mr.ft.inventory.shared.forms.IBoxService;
import pers.mr.ft.inventory.shared.forms.MovePartsFormData;
import pers.mr.ft.inventory.shared.forms.UpdateMovePartsPermission;
import pers.mr.ft.inventory.shared.lookup.BoxLookupCall;

@FormData(value = MovePartsFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class MovePartsForm extends AbstractForm {
  private MovePartsFormData moveData = new MovePartsFormData();
  
  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("MoveParts");
  }

  public MainBox getMainBox() {
    return getFieldByClass(MainBox.class);
  }

  public GroupBox getGroupBox() {
    return getFieldByClass(GroupBox.class);
  }

  public FromBoxIdField getFromBoxIdField() {
    return getFieldByClass(FromBoxIdField.class);
  }

  public FromBoxLabelField getFromBoxLabelField() {
    return getFieldByClass(FromBoxLabelField.class);
  }

  public ToBoxField getToBoxField() {
    return getFieldByClass(ToBoxField.class);
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
        return 10;
      }

      @Order(1000)
      public class FromBoxIdField extends AbstractLongField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("Ident");
        }

        @Override
        protected byte getConfiguredLabelPosition() {
          return LABEL_POSITION_TOP;
        }
        @Override
        protected boolean getConfiguredStatusVisible() {
          return true;
        }
        @Override
        protected int getConfiguredGridW() {
          return 1;
        }
        @Override
        protected boolean getConfiguredEnabled() {
          return false;
        }

      }

      @Order(2000)
      public class FromBoxLabelField extends AbstractStringField {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("FromBox");
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
        protected int getConfiguredMaxLength() {
          return 128;
        }

        @Override
        protected boolean getConfiguredEnabled() {
          return false;
        }
      }



      @Order(3000)
      public class ToBoxField extends AbstractSmartField<Long> {
        @Override
        protected String getConfiguredLabel() {
          return TEXTS.get("ToBox");
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
        protected Class<? extends ILookupCall<Long>> getConfiguredLookupCall() {
          return BoxLookupCall.class;
        }
        @Override
        protected boolean getConfiguredMandatory() {
          return true;
        }
      }



      @Order(10000)
      public class PartsField extends AbstractPartsField {

        @Override
        protected int getConfiguredGridW() {
          return 10;
        }
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
        protected byte getConfiguredLabelPosition() {
          return LABEL_POSITION_TOP;
        }
        @Override
        protected boolean getConfiguredStatusVisible() {
          return false;
        }
        protected ITableControl createAggregateTableControl() {
          return new AggregateTableControl();
        }

        private void hideAndDisableMenu(AbstractMenu menu) {
          menu.setVisible(false);
          menu.setEnabled(false);
        }
        @Override
        protected void execInitField() {
          super.execInitField();

          Table partsTable = getTable();
          partsTable.getPartValueColumn().setDisplayable(false);
          partsTable.getValueColumn().setDisplayable(false);
          partsTable.getMaxCountColumn().setDisplayable(true);
          partsTable.getPartNumberColumn().setEditable(false);
          hideAndDisableMenu(partsTable.getMenuByClass(PartsField.Table.AddRowMenu.class));
          hideAndDisableMenu(partsTable.getMenuByClass(PartsField.Table.DeleteMenu.class));
          hideAndDisableMenu(partsTable.getMenuByClass(PartsField.Table.MovePartsMenu.class));          
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

  public void startModify(MovePartsFormData formData) {
    moveData = formData;
    startInternalExclusive(new ModifyHandler());
  }

  public void exportData(MovePartsFormData formData) {
    exportFormData(formData);
  }
  
  public class ModifyHandler extends AbstractFormHandler {
    @Override
    protected void execLoad() {
      importFormData(moveData);
      setEnabledPermission(new UpdateMovePartsPermission());
    }

    @Override
    protected void execStore() {
      moveData = new MovePartsFormData();
      exportFormData(moveData);
    }
  }
}
