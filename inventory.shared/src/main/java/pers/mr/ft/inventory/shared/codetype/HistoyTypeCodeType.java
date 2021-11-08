package pers.mr.ft.inventory.shared.codetype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;
import org.eclipse.scout.rt.shared.services.common.code.CodeRow;
import org.eclipse.scout.rt.shared.services.common.code.ICodeRow;

public class HistoyTypeCodeType extends AbstractCodeType<String, String> {
  private static final long serialVersionUID = 1L;
  public static final String ID = null;
  
  public enum HistoryType {
    Move("MO"),
    Add("AD"),
    Remove("RE"),
    ;
    private String code;
    private static Map<String,HistoryType> valuesByCode = new HashMap<>();
    
    static {
      for (HistoryType ht: values()) {
        valuesByCode.put(ht.getCode(), ht);
      }
    }
    
    private HistoryType(String code) {
      this.code = code;
    }
    
    public String getCode() {
      return code;
    }
    
    public String getLabel() {
      return TEXTS.get("HT" + this.name());
    }
    
    public static HistoryType getByCode(String code) {
      return valuesByCode.get(code);
    }
  }

  @Override
  public String getId() {
    return ID;
  }
  @Override
  protected List<? extends ICodeRow<String>> execLoadCodes(Class<? extends ICodeRow<String>> codeRowType) {
    List<ICodeRow<String>> codes = new ArrayList<>();

    for (HistoryType type: HistoryType.values()) {
      codes.add(new CodeRow<>(type.getCode(), type.getLabel()));
    }

    return codes;
  }
}
