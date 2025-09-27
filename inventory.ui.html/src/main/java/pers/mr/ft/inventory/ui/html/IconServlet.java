package pers.mr.ft.inventory.ui.html;

import java.io.IOException;
import java.io.OutputStream;
import java.security.AccessController;
import java.util.Base64;

import javax.security.auth.Subject;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.context.CorrelationId;
import org.eclipse.scout.rt.platform.context.RunContext;
import org.eclipse.scout.rt.platform.context.RunContexts;
import org.eclipse.scout.rt.platform.exception.DefaultExceptionTranslator;
import org.eclipse.scout.rt.server.commons.servlet.IHttpServletRoundtrip;
import org.eclipse.scout.rt.server.commons.servlet.logging.ServletDiagnosticsProviderFactory;
import org.eclipse.scout.rt.shared.ui.UserAgent;
import org.eclipse.scout.rt.shared.ui.UserAgents;
import org.eclipse.scout.rt.ui.html.UiServlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pers.mr.ft.inventory.shared.images.IImageService;

public class IconServlet extends UiServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!handleRequest(req, resp)) {
      super.doGet(req, resp);
    }
    
  }
  
  protected boolean handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) {
    UserAgent ua = UserAgents.createByIdentifier("UNKNOWN|UNKNOWN|UNKNOWN|UNKNOWN|UNKNOWN");
    UserAgent.CURRENT.set(ua);
    String imageIdStr = req.getParameter("image");
    
    if (imageIdStr!=null) {
      try {
        Long imageId = Long.parseLong(imageIdStr);
        byte[] imageData = null;
        if (imageId > 0) {
          IImageService service = BEANS.get(IImageService.class);
          imageData = service.getImageData(imageId, true);
        }
        else {
          // Image PNG 1x1 transparente
          imageData = Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z/C/HgAGgwJ/lK3Q6wAAAABJRU5ErkJggg==");
        }
        resp.setContentType("image/png");
        OutputStream s = resp.getOutputStream();
        s.write(imageData);
        return true;
      } catch (NumberFormatException e) {
        
      } catch (IOException e) {
      }
    }
    return false;
  }
  protected boolean handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    try {
      return createServletRunContext(req, resp).call(() -> handleRequestInternal(req, resp), DefaultExceptionTranslator.class);
    }
    catch (Exception e) {
      return false;
    }
  }


  @Override
  protected RunContext createServletRunContext(final HttpServletRequest req, final HttpServletResponse resp) {
    final String cid = req.getHeader(CorrelationId.HTTP_HEADER_NAME);

    return RunContexts.copyCurrent(true)
        .withSubject(Subject.getSubject(AccessController.getContext()))
        .withThreadLocal(IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_REQUEST, req)
        .withThreadLocal(IHttpServletRoundtrip.CURRENT_HTTP_SERVLET_RESPONSE, resp)
        .withDiagnostics(BEANS.get(ServletDiagnosticsProviderFactory.class).getProviders(req, resp))
        .withLocale(getPreferredLocale(req))
        .withCorrelationId(cid != null ? cid : BEANS.get(CorrelationId.class).newCorrelationId());
  }

}
