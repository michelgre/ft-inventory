package pers.mr.ft.inventory.client.columns;

import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;

public class AbstractDimensionColumn extends AbstractLongColumn {
  @Override
  protected int getConfiguredWidth() {
    return 76;
  }
}
