package pers.mr.ft.inventory.server.codetype;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;

import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.shared.codetype.ICodeTypeService;

public class CodeTypeService implements ICodeTypeService {

  @Override
  public Object[][] load(String codeType, String idName, String codeName, String parent_name, String cond, boolean isML) {
    String userLanguage = ServerSession.get().getSessionLanguage();
    String defaultLanguage =  ServerSession.get().getDefaultLanguage();
    
    String sql = "";
    String optParent = "";
    if (parent_name!=null && parent_name.length()>0) {
      optParent = ", c." + parent_name;
    }
    if (cond==null) {
      cond = "";
    }
    else cond = cond.trim();
    if (cond.length()>0) {
      cond = " WHERE "+cond;
    }
    
    if (isML) {
      sql = "SELECT c." + idName + ", COALESCE(l1.label, l2.label)" + optParent + " FROM " + codeType + " c " +
      " LEFT JOIN multilingual_label l1 ON l1.id = c."+codeName+" AND l1.langcode = :userLanguage " +
      " LEFT JOIN multilingual_label l2 ON l2.id = c."+codeName+" AND l2.langcode = :defaultLanguage " +
      cond +
      " ORDER BY c." + idName
      ;
      return SQL.select(sql,
          new NVPair("userLanguage", userLanguage), 
          new NVPair("defaultLanguage", defaultLanguage));
    }
    else {
      sql = "SELECT "+idName+","+codeName + optParent + " FROM "+codeType + " c " + " ORDER BY "+idName;
      return SQL.select(sql);
    }
  }

  @Override
  public Object[][] load(String codeType, String idName, String codeName, String parent_name, boolean isML) {
    return load(codeType, idName, codeName, parent_name, "", isML);
  }

}
