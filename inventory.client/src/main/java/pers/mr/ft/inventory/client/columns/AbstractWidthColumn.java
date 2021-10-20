package pers.mr.ft.inventory.client.columns;

import org.eclipse.scout.rt.platform.text.TEXTS;

public class AbstractWidthColumn extends AbstractDimensionColumn {
  @Override
  protected String getConfiguredHeaderText() {
    return TEXTS.get("Width");
  }

}
