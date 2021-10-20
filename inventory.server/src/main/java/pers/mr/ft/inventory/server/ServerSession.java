package pers.mr.ft.inventory.server;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.server.AbstractServerSession;
import org.eclipse.scout.rt.server.session.ServerSessionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pers.mr.ft.inventory.shared.security.FTPrincipal;
import pers.mr.ft.inventory.shared.security.IAuthorizationService;

/**
 * @author mreverbel
 */
public class ServerSession extends AbstractServerSession {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

  public ServerSession() {
    super(true);
  }

  /**
   * @return The {@link ServerSession} which is associated with the current
   *         thread, or {@code null} if not found.
   */
  public static ServerSession get() {
    return ServerSessionProvider.currentSession(ServerSession.class);
  }

  @Override
  protected void execLoadSession() {
    String userId = getUserId();
    LOG.info("created a new session for {}", userId);
    
    setSharedContextVariable("UserLanguage", String.class, "fr");
    
    FTPrincipal principal = BEANS.get(IAuthorizationService.class).getPrincipal(userId);
    setSharedContextVariable("principal", FTPrincipal.class, principal);
  }
  
  public FTPrincipal getPrincipal() {
    return getSharedContextVariable("principal", FTPrincipal.class);
  }
  
  //TODO Choix de langue par l'utilisateur / le navigateur
  public String getDefaultLanguage() {
    return "de";
  }
  
  public String getSessionLanguage() {
    return "fr";
  }
}
