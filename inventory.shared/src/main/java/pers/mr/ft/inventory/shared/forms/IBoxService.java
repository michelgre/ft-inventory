package pers.mr.ft.inventory.shared.forms;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface IBoxService extends IService {
  BoxFormData prepareCreate(BoxFormData formData);

  BoxFormData create(BoxFormData formData);

  BoxFormData load(BoxFormData formData);

  BoxFormData store(BoxFormData formData);
  
  BoxFormData moveParts(BoxFormData formData, MovePartsFormData moveData, boolean removeEmptyLines);
}
