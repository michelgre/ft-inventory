package pers.mr.ft.inventory.client.columns;

import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class AbstractLabelColumn extends AbstractStringColumn {
  @Override
  protected String getConfiguredHeaderText() {
    return TEXTS.get("Label");
  }

  @Override
  protected int getConfiguredWidth() {
    return 300;
  }
  
  @Override
  protected boolean getConfiguredSummary() {
    return true;
  }
}
