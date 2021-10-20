package pers.mr.ft.inventory.client.pages;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class ReferentialNodePage extends AbstractPageWithNodes {
  @Override
  protected void execCreateChildPages(List<IPage<?>> pageList) {
    super.execCreateChildPages(pageList);
    pageList.add(new ConstructionSetTablePage());
    pageList.add(new PartsTablePage());
  }

  @Override
  protected String getConfiguredTitle() {
    // TODO [michel] verify translation
    return TEXTS.get("Referential");
  }
}
