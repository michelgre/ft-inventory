package pers.mr.ft.inventory.shared.forms;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface IBoxTypeService extends IService {
  BoxTypeFormData prepareCreate(BoxTypeFormData formData);

  BoxTypeFormData create(BoxTypeFormData formData);

  BoxTypeFormData load(BoxTypeFormData formData);

  BoxTypeFormData store(BoxTypeFormData formData);
}
