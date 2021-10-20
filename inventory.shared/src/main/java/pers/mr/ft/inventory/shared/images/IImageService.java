package pers.mr.ft.inventory.shared.images;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

@TunnelToServer
public interface IImageService extends IService {
  byte[] getImageData(Long imageId, boolean iconic);
}
