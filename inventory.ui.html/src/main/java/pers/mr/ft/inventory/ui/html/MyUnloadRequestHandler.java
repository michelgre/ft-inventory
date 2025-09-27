package pers.mr.ft.inventory.ui.html;

import java.io.IOException;

import org.eclipse.scout.rt.platform.Replace;
import org.eclipse.scout.rt.ui.html.json.UnloadRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * Résolution du problème erreur 404 sur /unload après une session expirée: apparemment
 * dans cette situation le navigateur envoie une requête /unload de type GET alors que
 * normalement c'est POST. La classe originale UnloadRequestHandler ne traite que les POST /unload,
 * ce qui fait qu'aucun handler ne traite GET /unload => erreur 404.
 */
@Replace
public class MyUnloadRequestHandler extends UnloadRequestHandler {
  private static final Logger LOG = LoggerFactory.getLogger(MyUnloadRequestHandler.class);
  
  @Override
  protected boolean handleGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    boolean processed = handlePost(req, resp);
    if (processed) {
      LOG.info("Processed GET "+req.getRequestURI());
      
      // On a traité une requête /unload hors séquence => il faut rediriger le navigateur
      resp.sendRedirect(req.getContextPath() + "/");
    }
    return processed;
  }
}
