package pers.mr.ft.inventory.client.pages;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.ICode;

import pers.mr.ft.inventory.shared.codetype.LocationCodeType;

public class BoxesByLocationNodePage extends AbstractPageWithNodes {
  @Override
  protected void execCreateChildPages(List<IPage<?>> pageList) {
    super.execCreateChildPages(pageList);
    
    List<? extends ICode<Long>> codes = new LocationCodeType().getCodes();
    for (ICode<Long> code: codes) {
      String locLabel = code.getText();
      pageList.add(new BoxTablePage("Location_"+locLabel).withLocation(code.getId(), true).withTitle(locLabel).withLotAchat(false).withMainBox(true, false).withGiven(null));
    }
    pageList.add(new BoxTablePage("Location_Defaut").withLocation(0L, true).withTitle(TEXTS.get("NotSet")).withLotAchat(false).withMainBox(true, false).withGiven(null));
   
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("BoxByLocation");
  }
}
