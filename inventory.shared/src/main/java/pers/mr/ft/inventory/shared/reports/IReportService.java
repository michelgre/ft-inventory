package pers.mr.ft.inventory.shared.reports;

import java.util.List;

import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface IReportService extends IService {
  BinaryResource buildReport(List<Long> boxIds);
  BinaryResource buildLabelSheets(List<Long> boxIds);
  BinaryResource buildSmallLabelSheets(List<Long> boxIds);
  BinaryResource buildReport(Long reportId, List<Long> ids);
}
