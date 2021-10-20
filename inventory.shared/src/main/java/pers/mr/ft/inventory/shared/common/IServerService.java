package pers.mr.ft.inventory.shared.common;

import java.io.Serializable;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;


@TunnelToServer
public interface IServerService extends IService {
  void putSharedVariable(String name, Serializable value);
}
