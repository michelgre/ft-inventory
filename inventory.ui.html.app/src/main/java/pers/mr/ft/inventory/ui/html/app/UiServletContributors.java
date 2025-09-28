package pers.mr.ft.inventory.ui.html.app;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.ee10.servlet.FilterHolder;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.scout.rt.jetty.IServletContributor;
import org.eclipse.scout.rt.jetty.IServletFilterContributor;
import org.eclipse.scout.rt.platform.Replace;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.ui.html.app.UiServletContributors.AuthFilterContributor;

import pers.mr.ft.inventory.ui.html.IconServlet;
import pers.mr.ft.inventory.ui.html.UiServletFilter;

/**
 * {@link IServletContributor} and {@link IServletFilterContributor} for UI
 * server.
 */
public final class UiServletContributors {

  private UiServletContributors() {
  }

  @Replace
  public static class UiAuthFilterContributor extends AuthFilterContributor {

    @Override
    public void contribute(ServletContextHandler handler) {
      handler.addServlet(new ServletHolder(new IconServlet()), "/icons");
      FilterHolder filter = handler.addFilter(UiServletFilter.class, "/*", null);
      filter.setInitParameter("filter-exclude", StringUtility.join("\n", getFilterExcludes()));
    }

    @Override
    protected List<String> getFilterExcludes() {
      List<String> filterExcludes = super.getFilterExcludes();
      filterExcludes.addAll(Arrays.asList("/favicon/*", "/fonts/*", "/logo.png", "/*login*.js", "/*logout*.js",
          "/*inventory-theme*.css"));
      return filterExcludes;
    }
  }
}
