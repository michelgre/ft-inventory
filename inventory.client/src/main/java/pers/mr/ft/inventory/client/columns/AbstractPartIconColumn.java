package pers.mr.ft.inventory.client.columns;

import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIconColumn;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class AbstractPartIconColumn extends AbstractIconColumn {
  @Override
  protected String getConfiguredHeaderText() {
    return TEXTS.get("Icon");
  }

  @Override
  protected int getConfiguredWidth() {
    return 100;
  }
  
  @Override
  protected String getConfiguredCssClass() {
    return "part-icon";
  }
}
