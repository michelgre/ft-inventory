package pers.mr.ft.inventory.client.columns;

import org.eclipse.scout.rt.platform.text.TEXTS;

public class AbstractLengthColumn extends AbstractDimensionColumn {
  @Override
  protected String getConfiguredHeaderText() {
    return TEXTS.get("Length");
  }

}
