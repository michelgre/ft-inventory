package pers.mr.ft.inventory.shared.codetype;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;


@TunnelToServer
public interface ICodeTypeService extends IService {
  Object[][] load (String codeType, String idName, String codeName, String parent_name, boolean isML);
  Object[][] load (String codeType, String idName, String codeName, String parent_name, String cond, boolean isML);
}
