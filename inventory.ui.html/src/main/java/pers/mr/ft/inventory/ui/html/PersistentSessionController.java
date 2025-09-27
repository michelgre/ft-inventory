package pers.mr.ft.inventory.ui.html;

import java.io.IOException;
import java.security.Principal;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Bean;
import org.eclipse.scout.rt.platform.security.SimplePrincipal;
import org.eclipse.scout.rt.server.commons.authentication.IAccessController;
import org.eclipse.scout.rt.server.commons.authentication.ServletFilterHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Bean
public class PersistentSessionController implements IAccessController {
  private static final Logger logger = LoggerFactory.getLogger(PersistentSessionController.class);
  private static final String authCookieName = "auth";
  
  Encryption encryption = null;

  public PersistentSessionController init() {
    encryption = new Encryption();
    try {
      encryption.init();
    } catch (Exception e) {
      logger.error("Impossible d'initialiser le module d'encryption: "+e.getMessage());
    }
    return this;
  }
  
  private Cookie getPersistentAuthCookie(HttpServletRequest request) {
    Cookie[] cookieBox = request.getCookies();
    if (cookieBox!=null) {
      for (Cookie cookie: cookieBox) {
        if (cookie.getName().equals(authCookieName)) {
          return cookie;
        }
      }
    }
    return null;
  }
  
  private void doLogout(HttpServletRequest request, HttpServletResponse response) {
    Cookie cookie = getPersistentAuthCookie(request);
    if (cookie!=null) {
      setPersistentCookie(null, response, 0);
    }
  }
  
  public static boolean setPrincipal(HttpServletRequest request, HttpServletResponse response, String principalName) {
    response.setHeader("Cache-Control", "private, no-store, no-cache, max-age=0"); // HTTP 1.1
    response.setHeader("Pragma", "no-cache"); // HTTP 1.0
    response.setDateHeader("Expires", 0); // prevents caching at the proxy server

    // OWASP: force a new HTTP session to be created.
    ServletFilterHelper helper = BEANS.get(ServletFilterHelper.class);
    helper.invalidateSessionForLogin(request);

    // Put authenticated principal onto (new) HTTP session
    final Principal principal = new SimplePrincipal(principalName);
    helper.putPrincipalOnSession(request, principal);
    return true;
  }

  private boolean checkPersistentLogin(HttpServletRequest request, HttpServletResponse response) {
    // Y a-t-il un cookie de session persistant ?
    Cookie cookie = getPersistentAuthCookie(request);
    if (cookie!=null) {
      String value = cookie.getValue();
      if (value!=null) {
        try {
          String principalName = encryption.decrypt(value);
          if (!principalName.equals("")) {
            setPrincipal(request, response, principalName);
            return true;
          }
        } catch (Exception e) {
          logger.error("Cookie persistant incorrect ("+value+"): "+e.getMessage());
        }
      }
    }
    return false;
  }
  
  private void setPersistentCookie(Principal principal, HttpServletResponse response, int age) {
    try {
      String encoded = "----";
      if (principal!=null) {
        String data = principal.getName();
        encoded = encryption.encrypt(data);
      }
      Cookie persistentMiam  = new Cookie(authCookieName, encoded);
      persistentMiam.setHttpOnly(true);
      persistentMiam.setMaxAge(age); 
      response.addCookie(persistentMiam);
      
    } catch (Exception e) {
      logger.error("Generation token persistant: "+e.getMessage());
    }
  }
  
  public boolean handleLogout(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
    if (getTarget(request).equals("/logout")) {
      doLogout(request, response);
      return true;
    }
    return false;
  }
  
  public boolean handleLogin(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
    if ("/auth".equals(request.getPathInfo())) {
      ServletFilterHelper helper = BEANS.get(ServletFilterHelper.class);
      Principal principal = helper.getPrincipalOnSession(request);
      if (principal!=null) {
        setPersistentCookie(principal, response, 8640000); // 100 j
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean handle(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    return checkPersistentLogin(request, response);
  }

  @Override
  public void destroy() {
    
  }

  protected String getTarget(final HttpServletRequest request) {
    final String pathInfo = request.getPathInfo();
    if (pathInfo != null) {
      return pathInfo;
    }

    final String requestURI = request.getRequestURI();
    return requestURI.substring(requestURI.lastIndexOf('/'));
  }


}
