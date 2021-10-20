package pers.mr.ft.inventory.shared.security;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface IAuthorizationService extends IService {
  public int verify(String username, char[] password);
  public FTPrincipal getPrincipal(String username);
}
