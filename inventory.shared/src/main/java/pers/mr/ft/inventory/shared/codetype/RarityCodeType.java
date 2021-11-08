package pers.mr.ft.inventory.shared.codetype;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;
import org.eclipse.scout.rt.shared.services.common.code.CodeRow;
import org.eclipse.scout.rt.shared.services.common.code.ICodeRow;

public class RarityCodeType extends AbstractCodeType<String, Integer> {
  private static final long serialVersionUID = 1L;
  public static final String ID = null;

  @Override
  public String getId() {
    return ID;
  }
  @Override
  protected List<? extends ICodeRow<Integer>> execLoadCodes(Class<? extends ICodeRow<Integer>> codeRowType) {
    List<ICodeRow<Integer>> codes = new ArrayList<>();

    codes.add(new CodeRow<>(0, TEXTS.get("RarityNotRare")));
    codes.add(new CodeRow<>(1, TEXTS.get("RarityRare")));
    codes.add(new CodeRow<>(2, TEXTS.get("RarityVeryRare")));
    codes.add(new CodeRow<>(3, TEXTS.get("RarityExtremelyRare")));
    codes.add(new CodeRow<>(4, TEXTS.get("RarityNotAvailable")));

    return codes;
  }
}
