package pers.mr.ft.inventory.shared.pages;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

@TunnelToServer
public interface IBoxService extends IService {
  BoxTablePageData getBoxTableData(SearchFilter filter, Long parentId, Boolean lotAchat, Boolean given);
  void delete(Long boxId);
  boolean hasBins(Long boxId);
  boolean hasSubBins(Long boxId);
  void moveToLocation(Long locationId, Long boxId);
}
