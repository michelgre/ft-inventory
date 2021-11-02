package pers.mr.ft.inventory.server.forms;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.security.ACCESS;
import org.eclipse.scout.rt.server.jdbc.SQL;

import pers.mr.ft.inventory.shared.forms.CreateReportModelPermission;
import pers.mr.ft.inventory.shared.forms.IReportModelService;
import pers.mr.ft.inventory.shared.forms.ReadReportModelPermission;
import pers.mr.ft.inventory.shared.forms.ReportModelFormData;
import pers.mr.ft.inventory.shared.forms.ReportModelFormData.Parameters.ParametersRowData;
import pers.mr.ft.inventory.shared.forms.UpdateReportModelPermission;

public class ReportModelService implements IReportModelService {
  @Override
  public ReportModelFormData prepareCreate(ReportModelFormData formData) {
    if (!ACCESS.check(new CreateReportModelPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO [mreverbel] add business logic here.
    return formData;
  }

  @Override
  public ReportModelFormData create(ReportModelFormData formData) {
    if (!ACCESS.check(new CreateReportModelPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    SQL.insert("INSERT INTO report (type, name, style_sheet) " + 
        " VALUES (:type, :name, :styleSheet)", 
        formData);
    
    Object[][] rows = SQL.select("SELECT LASTVAL()");
    Long createdId = (Long) rows[0][0];
   
    formData.setObjectId(createdId);
    formData.getId().setValue(createdId);
    
    formData = updateParameters(formData);
    
    return formData;
  }

  @Override
  public ReportModelFormData load(ReportModelFormData formData) {
    if (!ACCESS.check(new ReadReportModelPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    SQL.selectInto("SELECT id, type, name, style_sheet " +
        " FROM report " +
        " WHERE id = :objectId " +
        " INTO :id, :type, :name, :styleSheet",
        formData);
    
    SQL.selectInto("SELECT id, name, value " + 
        " FROM report_parameter " + 
        " WHERE report_id = :objectId " + 
        " INTO :{parameters.id}, :{parameters.name}, :{parameters.value}", 
        formData);
    return formData;
  }

  @Override
  public ReportModelFormData store(ReportModelFormData formData) {
    if (!ACCESS.check(new UpdateReportModelPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    SQL.update("UPDATE report SET type = :type, name = :name, style_sheet = :styleSheet " +
        " WHERE id = :objectId", 
        formData);
    formData = updateParameters(formData);
    return formData;
  }
  
  private ReportModelFormData updateParameters(ReportModelFormData formData) {
    for (ParametersRowData paramData: formData.getParameters().getRows()) {
      switch (paramData.getRowState()) {
      case ParametersRowData.STATUS_INSERTED:
        SQL.insert("INSERT INTO report_parameter (report_id, name, value) "+
            " VALUES (:reportId, :name, :value)",
            new NVPair("reportId", formData.getObjectId()),
            paramData);
        break;
      case ParametersRowData.STATUS_UPDATED:
        SQL.update("UPDATE report_parameter SET "+
            " name = :name, " +
            " value = :value " +
            " WHERE id = :id", 
            paramData);
        break;
      case ParametersRowData.STATUS_DELETED:
        SQL.delete("DELETE FROM report_parameter WHERE id = :id", paramData);
        break;
      case ParametersRowData.STATUS_NON_CHANGED:
        break;
      }
    }
    return formData;
  }
}
