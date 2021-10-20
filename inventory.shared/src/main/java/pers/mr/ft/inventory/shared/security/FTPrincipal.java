package pers.mr.ft.inventory.shared.security;

import org.eclipse.scout.rt.platform.security.SimplePrincipal;

public class FTPrincipal extends SimplePrincipal {
  private static final long serialVersionUID = 1L;
  
  Long id = 0L;
  
  public Long getId() {
    return id;
  }

  public FTPrincipal(Long id, String name) {
    super(name);
    this.id = id;
    
  }

}
