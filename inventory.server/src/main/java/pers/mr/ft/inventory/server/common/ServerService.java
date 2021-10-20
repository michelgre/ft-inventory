package pers.mr.ft.inventory.server.common;

import java.io.Serializable;

import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.shared.common.IServerService;

public class ServerService implements IServerService {

  @Override
  public void putSharedVariable(String name, Serializable value) {
    ServerSession.get().setData(name, value);
  }

}
