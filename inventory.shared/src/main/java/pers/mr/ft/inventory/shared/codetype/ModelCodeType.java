package pers.mr.ft.inventory.shared.codetype;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;
import org.eclipse.scout.rt.shared.services.common.code.CodeRow;
import org.eclipse.scout.rt.shared.services.common.code.ICodeRow;

public class ModelCodeType extends AbstractCodeType<String, Long> {
  private static final long serialVersionUID = 1L;
  // TODO [michel] set id value
  public static final String ID = null;

  @Override
  public String getId() {
    return ID;
  }
  @Override
  protected List<? extends ICodeRow<Long>> execLoadCodes(Class<? extends ICodeRow<Long>> codeRowType) {
    
    Object[][] data = BEANS.get(ICodeTypeService.class).load("part", "id", "title_id", "", "(select count(*) from part_contains where container_id = c.id)>0", true);
    List<ICodeRow<Long>> codeRows = new ArrayList<ICodeRow<Long>>();
    for (Object[] cells : data) {
      codeRows.add(new CodeRow<Long>(cells, Long.class));
    }
    return codeRows;
  }
}
