package pers.mr.ft.inventory.client.fields;

import org.eclipse.scout.rt.client.ui.form.fields.longfield.AbstractLongField;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class AbstractIdField extends AbstractLongField {
  @Override
  protected String getConfiguredLabel() {
    return TEXTS.get("Ident");
  }

  @Override
  protected byte getConfiguredLabelPosition() {
    return LABEL_POSITION_TOP;
  }
  @Override
  protected int getConfiguredGridW() {
    return 1;
  }
  @Override
  protected boolean getConfiguredEnabled() {
    return false;
  }
  @Override
  protected boolean getConfiguredStatusVisible() {
    return false;
  }
  @Override
  protected boolean getConfiguredGroupingUsed() {
    return false;
  }
}
