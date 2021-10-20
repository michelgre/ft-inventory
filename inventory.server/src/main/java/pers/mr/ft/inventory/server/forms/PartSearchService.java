package pers.mr.ft.inventory.server.forms;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.security.ACCESS;

import pers.mr.ft.inventory.shared.forms.CreatePartSearchPermission;
import pers.mr.ft.inventory.shared.forms.IPartSearchService;
import pers.mr.ft.inventory.shared.forms.PartSearchFormData;
import pers.mr.ft.inventory.shared.forms.ReadPartSearchPermission;
import pers.mr.ft.inventory.shared.forms.UpdatePartSearchPermission;

public class PartSearchService implements IPartSearchService {
  @Override
  public PartSearchFormData prepareCreate(PartSearchFormData formData) {
    if (!ACCESS.check(new CreatePartSearchPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO [mreverbel] add business logic here.
    return formData;
  }

  @Override
  public PartSearchFormData create(PartSearchFormData formData) {
    if (!ACCESS.check(new CreatePartSearchPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO [mreverbel] add business logic here.
    return formData;
  }

  @Override
  public PartSearchFormData load(PartSearchFormData formData) {
    if (!ACCESS.check(new ReadPartSearchPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO [mreverbel] add business logic here.
    return formData;
  }

  @Override
  public PartSearchFormData store(PartSearchFormData formData) {
    if (!ACCESS.check(new UpdatePartSearchPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO [mreverbel] add business logic here.
    return formData;
  }
}
