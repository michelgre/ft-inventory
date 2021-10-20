package pers.mr.ft.inventory.client;

import java.net.URI;
import java.util.Locale;

import org.eclipse.scout.rt.client.AbstractClientSession;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.ClientUIPreferences;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.common.code.CODES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pers.mr.ft.inventory.shared.common.IServerService;

/**
 * @author michel
 */
public class ClientSession extends AbstractClientSession {
  private static final Logger LOG = LoggerFactory.getLogger(ClientSession.class);
  
  public ClientSession() {
    super(true);
    
    /*
    String username = getSubject().getPrincipals().iterator().next().getName();
    ClientConfigProperties.UserAreaProperty prop = BEANS.get(ClientConfigProperties.UserAreaProperty.class);
    if (prop instanceof MyUserAreaProperty) {
      MyUserAreaProperty propUserArea = (MyUserAreaProperty) prop;
      propUserArea.setParameter("user", username);
      propUserArea.applyParameters();
    }
    */
  }

  /**
   * @return The {@link IClientSession} which is associated with the current
   *         thread, or {@code null} if not found.
   */
  public static ClientSession get() {
    return ClientSessionProvider.currentSession(ClientSession.class);
  }

  @Override
  protected void initializeSharedVariables() {
    super.initializeSharedVariables();
    
    IServerService service = BEANS.get(IServerService.class);
    URI myURI = getBrowserURI();
    String appPath = myURI.toString();
    service.putSharedVariable("ClientURI", appPath);
  }
  
  @Override
  protected void execLoadSession() {
    super.execLoadSession();
    
    initializeSharedVariables();

    // The locale needs to be set before the Desktop is created.
    String localeString = ClientUIPreferences.getClientPreferences(get()).get("PREF_USER_LOCALE", null);
    if (localeString != null) {
      setLocale(Locale.forLanguageTag(localeString));
    }

    setDesktop(new Desktop());
    
    // pre-load all known code types
    CODES.getAllCodeTypes("pers.mr.ft.inventory.shared");
    
    String userId = getUserId();
    LOG.info("Nouvelle session user "+userId);
  }
}
