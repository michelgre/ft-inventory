package pers.mr.ft.inventory.shared.common;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;


@TunnelToServer
public interface ILabelService extends IService {
  String load(Long id, String lang, boolean useDefault);
  Long save(Long id, String lang, String value);
}
