package pers.mr.ft.inventory.server.pages;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.shared.pages.IReportModelService;
import pers.mr.ft.inventory.shared.pages.ReportModelTablePageData;
import pers.mr.ft.inventory.shared.pages.ReportModelTablePageData.ReportModelTableRowData;

public class ReportModelService implements IReportModelService {
  @Override
  public ReportModelTablePageData getReportTableData(String type, SearchFilter filter) {
    ReportModelTablePageData pageData = new ReportModelTablePageData();
    String cond = "1 = 1";
    if (type!=null) {
      cond += " AND type = :type ";
    }
    SQL.selectInto("SELECT id, type, name, style_sheet FROM Report WHERE " + cond + " INTO :{page.id}, :{page.type}, :{page.name}, :{page.styleSheet}", 
        new NVPair("type", type),
        new NVPair("page", pageData));

    return pageData;
  }

  @Override
  public ReportModelTableRowData loadReportData(Long reportId) {
    ReportModelTableRowData rowData = new ReportModelTableRowData();
    SQL.selectInto("SELECT id, type, name, style_sheet FROM Report WHERE id = :reportId INTO :id, :type, :name, :styleSheet",
        rowData,
        new NVPair("reportId", reportId));

    return rowData;
  }

  @Override
  public Map<String, String> getReportParameters(Long reportId) {
    Map<String, String> reportParameters = new HashMap<String,String>();
    Object [][] data = SQL.select("SELECT id, name, value FROM Report_Parameter WHERE report_id = :reportId ", 
        new NVPair("reportId", reportId));
    for (Object[] rowData: data) {
      String paramName = (String) rowData[1];
      String paramValue = (String) rowData[2];
      reportParameters.put(paramName, paramValue);
    }
    return reportParameters;
  }
  @Override
  public void delete(Long id) {
    if (id > 0) {
      SQL.delete("DELETE from report WHERE id = :id", new NVPair("id", id));
    }
  }
}
