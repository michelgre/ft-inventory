package pers.mr.ft.inventory.server.security;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pers.mr.ft.inventory.shared.security.IAuthorizationService;
import pers.mr.ft.inventory.shared.security.FTPrincipal;

public class SqlAuthService implements IAuthorizationService {
  private static final Logger logger = LoggerFactory.getLogger(SqlAuthService.class);
  
  @Override
  public int verify(String username, char[] password) {
    // Essai avec mdp en clair
    logger.info("Tentative authentification "+username);
    Object[][] rows = SQL.select("SELECT id FROM \"user\" WHERE username = :username AND password = :password",
        new NVPair("username", username),
        new NVPair("password", password));
    logger.info("  =>"+rows.length);
    
    if (rows.length==1) {
      return 1;
    }
    
    // TODO: Hash avec salt
    return 0;
  }

  @Override
  public FTPrincipal getPrincipal(String username) {
    Object[][] rows = SQL.select("SELECT id FROM \"user\" WHERE username = :username",
        new NVPair("username", username));
    
    if (rows.length==1) {
      Long userId = (Long) rows[0][0];
      return new FTPrincipal(userId, username);
    }
    else {
      return null;
    }
  }

}
