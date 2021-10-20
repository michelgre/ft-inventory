package pers.mr.ft.inventory.client.columns;

import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.platform.text.TEXTS;

import pers.mr.ft.inventory.client.PartNumberComparator;

public class AbstractPartNumberColumn extends AbstractStringColumn {
  @Override
  protected String getConfiguredHeaderText() {
    return TEXTS.get("PartNumber");
  }

  @Override
  protected int getConfiguredWidth() {
    return 163;
  }
  @Override
  public int compareTableRows(ITableRow r1, ITableRow r2) {
    String s1 = getValue(r1);
    String s2 = getValue(r2);
    return new PartNumberComparator().compare(s1, s2);
  }
}
