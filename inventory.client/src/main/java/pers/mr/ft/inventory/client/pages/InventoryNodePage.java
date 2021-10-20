package pers.mr.ft.inventory.client.pages;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class InventoryNodePage extends AbstractPageWithNodes {
  @Override
  protected void execCreateChildPages(List<IPage<?>> pageList) {
    super.execCreateChildPages(pageList);
    pageList.add(new BoxTablePage("Inventory"));
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Inventory");
  }
}
