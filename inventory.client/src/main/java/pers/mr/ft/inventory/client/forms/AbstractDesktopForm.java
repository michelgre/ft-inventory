package pers.mr.ft.inventory.client.forms;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;

import pers.mr.ft.inventory.shared.forms.AbstractDesktopFormData;

@FormData(value = AbstractDesktopFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public abstract class AbstractDesktopForm extends AbstractForm {
  private Long objectId = 0L;
  
  @FormData
  public void setObjectId(Long objectId) {
    this.objectId = objectId;
  }

  @FormData
  public Long getObjectId() {
    return objectId;
  }

  abstract public void startModify() ;
  
}
