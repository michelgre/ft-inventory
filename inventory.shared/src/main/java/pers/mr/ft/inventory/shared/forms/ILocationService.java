package pers.mr.ft.inventory.shared.forms;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface ILocationService extends IService {
  LocationFormData prepareCreate(LocationFormData formData);

  LocationFormData create(LocationFormData formData);

  LocationFormData load(LocationFormData formData);

  LocationFormData store(LocationFormData formData);
}
