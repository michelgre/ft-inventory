package pers.mr.ft.inventory.shared.forms;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface IPartSearchService extends IService {
  PartSearchFormData prepareCreate(PartSearchFormData formData);

  PartSearchFormData create(PartSearchFormData formData);

  PartSearchFormData load(PartSearchFormData formData);

  PartSearchFormData store(PartSearchFormData formData);
}
