package pers.mr.ft.inventory.ui.html;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.security.ICredentialVerifier;
import org.eclipse.scout.rt.platform.util.Pair;
import org.eclipse.scout.rt.server.commons.authentication.FormBasedAccessController;
import org.eclipse.scout.rt.server.commons.authentication.ServletFilterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestFormBasedAccessController extends FormBasedAccessController {
  private static final Logger LOG = LoggerFactory.getLogger(TestFormBasedAccessController.class);
  
  protected boolean handleAuthRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
    // Never cache authentication requests.
    response.setHeader("Cache-Control", "private, no-store, no-cache, max-age=0"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setDateHeader("Expires", 0); // prevents caching at the proxy server

    LOG.info("handleAuthRequest");
    final Pair<String, char[]> credentials = readCredentials(request);
    if (credentials == null) {
      LOG.info("handleAuthRequest:credentials=null");
      handleForbidden(ICredentialVerifier.AUTH_CREDENTIALS_REQUIRED, response);
      return true;
    }
    String username = credentials.getLeft();
    if (username!=null) {
      username = username.trim();
    }

    LOG.info("handleAuthRequest:"+username+"("+new String(credentials.getRight())+")");
    LOG.info("handleAuthRequest verifier="+m_config.getCredentialVerifier().getClass().getName());

    final int status = m_config.getCredentialVerifier().verify(username, credentials.getRight());
    LOG.info("handleAuthRequest "+status);
    if (status != ICredentialVerifier.AUTH_OK) {
      handleForbidden(status, response);
      return true;
    }

    // OWASP: force a new HTTP session to be created.
    ServletFilterHelper helper = BEANS.get(ServletFilterHelper.class);
    LOG.info("handleAuthRequest helper="+helper);
    helper.invalidateSessionAfterLogin(request);

    // Put authenticated principal onto (new) HTTP session
    final Principal principal = m_config.getPrincipalProducer().produce(username);
    LOG.info("handleAuthRequest principal="+principal);
    helper.putPrincipalOnSession(request, principal);
    return true;
  }

}
