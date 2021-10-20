package pers.mr.ft.inventory.client.referential;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import pers.mr.ft.inventory.client.pages.ConstructionSetTablePage;
import pers.mr.ft.inventory.client.pages.DocumentsTablePage;
import pers.mr.ft.inventory.client.pages.PartsTablePage;
import pers.mr.ft.inventory.shared.Icons;

/**
 * @author michel
 */
@Order(1000)
public class ReferentialOutline extends AbstractOutline {

  @Override
  protected void execCreateChildPages(List<IPage<?>> pageList) {
    super.execCreateChildPages(pageList);
    
    pageList.add(new ConstructionSetTablePage());
    pageList.add(new PartsTablePage());
    pageList.add(new DocumentsTablePage());
  }

  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("Referential");
  }

  @Override
  protected String getConfiguredIconId() {
    return Icons.World;
  }

  
}
