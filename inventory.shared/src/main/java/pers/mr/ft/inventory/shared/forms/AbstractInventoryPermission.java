package pers.mr.ft.inventory.shared.forms;

import org.eclipse.scout.rt.api.data.security.PermissionId;
import org.eclipse.scout.rt.security.AbstractPermission;

public class AbstractInventoryPermission extends AbstractPermission {

  private static final long serialVersionUID = 1L;

  public AbstractInventoryPermission(String name) {
    super(PermissionId.of(name));
  }

}
