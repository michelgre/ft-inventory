package pers.mr.ft.inventory.shared.forms;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface IReportModelService extends IService {
  ReportModelFormData prepareCreate(ReportModelFormData formData);

  ReportModelFormData create(ReportModelFormData formData);

  ReportModelFormData load(ReportModelFormData formData);

  ReportModelFormData store(ReportModelFormData formData);
}
