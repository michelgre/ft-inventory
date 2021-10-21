package pers.mr.ft.inventory.shared.pages;

import java.util.Map;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.shared.pages.ReportModelTablePageData.ReportModelTableRowData;

@TunnelToServer
public interface IReportModelService extends IService {
  ReportModelTablePageData getReportTableData(String type, SearchFilter filter);
  ReportModelTableRowData loadReportData(Long reportId);
  Map<String,String> getReportParameters(Long reportId);
  void delete(Long id);

}
