package pers.mr.ft.inventory.shared.forms;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface IShopService extends IService {
  ShopFormData prepareCreate(ShopFormData formData);

  ShopFormData create(ShopFormData formData);

  ShopFormData load(ShopFormData formData);

  ShopFormData store(ShopFormData formData);
}
