package pers.mr.ft.inventory.client.columns;

import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class AbstractIdColumn extends AbstractLongColumn {
  @Override
  protected String getConfiguredHeaderText() {
    return TEXTS.get("Ident");
  }

  @Override
  protected int getConfiguredWidth() {
    return 55;
  }
  
  @Override
  protected boolean getConfiguredVisible() {
    return false;
  }
  @Override
  protected boolean getConfiguredGroupingUsed() {
    return false;
  }
}
