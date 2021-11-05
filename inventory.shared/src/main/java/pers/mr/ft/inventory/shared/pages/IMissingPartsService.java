package pers.mr.ft.inventory.shared.pages;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

@TunnelToServer
public interface IMissingPartsService extends IService {
  MissingPartsTablePageData getMissingPartsTableData(SearchFilter filter);
}
