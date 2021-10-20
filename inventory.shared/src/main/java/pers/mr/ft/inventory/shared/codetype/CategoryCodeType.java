package pers.mr.ft.inventory.shared.codetype;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;
import org.eclipse.scout.rt.shared.services.common.code.CodeRow;
import org.eclipse.scout.rt.shared.services.common.code.ICodeRow;

public class CategoryCodeType extends AbstractCodeType<String, Long> {
  private static final long serialVersionUID = 1L;
  public static final String ID = null;

  @Override
  public String getId() {
    return ID;
  }
  @Override
  protected List<? extends ICodeRow<Long>> execLoadCodes(Class<? extends ICodeRow<Long>> codeRowType) {
    
    Object[][] data = BEANS.get(ICodeTypeService.class).load("category", "id", "label_id", "parent_id", true);
    List<ICodeRow<Long>> codeRows = new ArrayList<ICodeRow<Long>>();
    for (Object[] row : data) {
      Object[] cells = { row[0], row[1] };
      Long parentId = (Long) row[2];
      codeRows.add(new CodeRow<Long>(cells, Long.class).withParentKey(parentId));
    }
    return codeRows;
  }
}
