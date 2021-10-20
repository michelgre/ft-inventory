package pers.mr.ft.inventory.client.inventory;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import pers.mr.ft.inventory.client.pages.BoxTablePage;
import pers.mr.ft.inventory.client.pages.BoxesByLocationNodePage;
import pers.mr.ft.inventory.shared.Icons;

/**
 * @author michel
 */
@Order(1000)
public class InventoryOutline extends AbstractOutline {

  @Override
  protected void execCreateChildPages(List<IPage<?>> pageList) {
    super.execCreateChildPages(pageList);
    
    pageList.add(new BoxTablePage("Boites").withGiven(false));
    pageList.add(new BoxTablePage("BoitesDonnees").withGiven(true));
    pageList.add(new BoxesByLocationNodePage());

    pageList.add(new BoxTablePage("LotAchat").withLotAchat(true));
    
    
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Inventory");
  }

  @Override
  protected String getConfiguredIconId() {
    return Icons.Pencil;
  }
}
