package pers.mr.ft.inventory.client.settings;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import pers.mr.ft.inventory.client.pages.BoxTypesTablePage;
import pers.mr.ft.inventory.client.pages.LocationTablePage;
import pers.mr.ft.inventory.client.pages.ReportModelTablePage;
import pers.mr.ft.inventory.client.pages.ShopTablePage;
import pers.mr.ft.inventory.shared.Icons;

/**
 * @author michel
 */
@Order(3000)
public class SettingsOutline extends AbstractOutline {

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Settings");
  }

  @Override
  protected String getConfiguredIconId() {
    return Icons.Gear;
  }
  @Override
  protected void execCreateChildPages(List<IPage<?>> pageList) {
    super.execCreateChildPages(pageList);
    
    pageList.add(new BoxTypesTablePage());
    pageList.add(new LocationTablePage());
    pageList.add(new ShopTablePage());
    pageList.add(new ReportModelTablePage());
  }
}
