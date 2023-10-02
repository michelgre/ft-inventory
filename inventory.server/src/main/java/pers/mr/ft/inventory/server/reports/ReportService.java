package pers.mr.ft.inventory.server.reports;

import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.PlatformExceptionTranslator;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.server.jdbc.SQL;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.shared.pages.IBoxService;
import pers.mr.ft.inventory.shared.pages.IReportModelService;
import pers.mr.ft.inventory.shared.pages.ReportModelTablePageData;
import pers.mr.ft.inventory.shared.pages.ReportModelTablePageData.ReportModelTableRowData;
import pers.mr.ft.inventory.shared.reports.IReportService;

public class ReportService implements IReportService {

  @Override
  public BinaryResource buildReport(List<Long> boxIds) {
    IBoxService boxService = BEANS.get(IBoxService.class);
    
    String reportName = "/reports/PartsList.jrxml";
    InputStream reportModelStream = getClass().getResourceAsStream(reportName);

    try {
      JasperReport jasperReport = JasperCompileManager.compileReport(reportModelStream);
      Map<String, Object> parameters = new HashMap<>();
      
      StringBuffer boxCond = new StringBuffer();
      String reportFilename = "";
      if (boxIds == null || boxIds.size()==0) {
        boxCond.append("1 = 1");
        reportFilename = "Report.pdf";
      }
      else {
        for (Long boxId: boxIds) {
          if (boxCond.length()==0) {
            boxCond.append("b.id IN (");
          }
          else {
            boxCond.append(",");
          }
          boxCond.append(String.valueOf(boxId));
        }
        boxCond.append(")");
        
        if (boxIds.size()==1) {
          reportFilename = "Box "+boxIds.get(0)+".pdf";
        }
        else {
          reportFilename = "Report.pdf"; // TODO: date dans le nom ?
        }
      }
      
      parameters.put("BoxCondition", boxCond.toString());
      
      JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, SQL.getConnection());
      byte[] pdfReportData = JasperExportManager.exportReportToPdf(print);
      BinaryResource br = new BinaryResource(reportFilename, pdfReportData); // TODO: mettre le label de la box ?
      return br;
    }
    catch (JRException e) {
      throw BEANS.get(PlatformExceptionTranslator.class).translate(e);
    }
  }

  public BinaryResource buildLabelSheets(List<Long> boxIds, String reportName) {
    InputStream reportModelStream = getClass().getResourceAsStream(reportName);

    //TODO: Récupérer le paramétrage d'un modèle d'étiquettes
    //  - nom du modèle Jasper
    //  - paramètres et valeurs
    String InventoryBoxURL = "https://192.168.0.14:8443/inventory/?id=";// TODO
    
    try {
      JasperReport jasperReport = JasperCompileManager.compileReport(reportModelStream);
      StringBuffer boxCondition = new StringBuffer();
      if (boxIds != null) {
        for (Long boxId: boxIds) {
          if (boxCondition.length()>0) {
            boxCondition.append(",");
          }
          else {
            boxCondition.append("id IN (");
          }
          boxCondition.append(String.valueOf(boxId));
        }
        if (boxCondition.length()>0) {
          boxCondition.append(")");
        }
      }
      if (boxCondition.length()==0) {
        // Toutes
        boxCondition.append("1=1");
      }
      Map<String, Object> parameters = new HashMap<>();
      String reportFilename = "";
      reportFilename = "Labels"+".pdf";
      parameters.put("BoxCondition", boxCondition.toString());
      parameters.put("InventoryBoxURL", InventoryBoxURL); // TODO
      
      JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, SQL.getConnection());
      byte[] pdfReportData = JasperExportManager.exportReportToPdf(print);
      BinaryResource br = new BinaryResource(reportFilename, pdfReportData); // TODO: mettre le label de la box ?
      return br;
    }
    catch (JRException e) {
      throw BEANS.get(PlatformExceptionTranslator.class).translate(e);
    }
  }

  @Override
  public BinaryResource buildLabelSheets(List<Long> boxIds) {
    return buildLabelSheets(boxIds, "/reports/BoxLabelsV2.jrxml");
  }
  
  @Override
  public BinaryResource buildSmallLabelSheets(List<Long> boxIds) {
    return buildLabelSheets(boxIds, "/reports/BinCassLabels.jrxml");
  }

  @Override
  public BinaryResource buildReport(Long reportId, List<Long> ids) {
    ReportModelTablePageData reportPageData = new ReportModelTablePageData();
    IReportModelService reportModelService = BEANS.get(IReportModelService.class);
    ReportModelTableRowData reportData = reportModelService.loadReportData(reportId);
    
    String reportName = reportData.getName();
    String reportStyleSheet = reportData.getStyleSheet();
    String reportType = reportData.getType();
    
    InputStream reportModelStream = getClass().getResourceAsStream(reportStyleSheet);
    if (reportModelStream==null) {
      reportModelStream = getClass().getResourceAsStream("/reports/" + reportStyleSheet);
    }
    Map<String, String> reportParameters = reportModelService.getReportParameters(reportId);
    
    // ========================================================================================
    // Attention: Jasper liste les polices connues du système: 
    //       GraphicsEnvironment.getAvailableFontFamilyNames()
    // et cette méthode échoue sur les JRE > 8 car un fichier de configuration n'est plus livré.
    // C'est son absence qui crée un NullPointerException. Il faut, après installation du JRE,
    // créer le fichier : $JAVA_HOME/lib/fontconfig.properties:
    //    version=1
    //    sequence.allfonts=default
    // Ce problème n'est toujours pas corrigé en Java 17 !!!
    // ========================================================================================
    try {
      JasperReport jasperReport = JasperCompileManager.compileReport(reportModelStream);
      StringBuffer condition = new StringBuffer();
      String reportFilename = "";
      String idName = "";
      
      if ("L".equals(reportType)) {
        reportFilename = "Labels"+" "+reportName+".pdf";
        idName = "id";
      }
      else if ("R".equals(reportType)) {
        if (ids.size()==1) {
          reportFilename = "Box "+ids.get(0)+".pdf";
        }
        else {
          reportFilename = "Rapport"+" "+reportName+".pdf";
        }
        idName = "b.id";
      }
      
      if (ids != null) {
        for (Long id: ids) {
          if (condition.length()>0) {
            condition.append(",");
          }
          else {
            condition.append(idName+ " IN (");
          }
          condition.append(String.valueOf(id));
        }
        if (condition.length()>0) {
          condition.append(")");
        }
      }
      if (condition.length()==0) {
        // Toutes
        condition.append("1=1");
      }
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("BoxCondition", condition.toString());
      
      for (String reportParameter: reportParameters.keySet()) {
        String clientURL = (String) ServerSession.get().getData("ClientURI");
        String value = reportParameters.get(reportParameter);
        value = value.replaceAll("\\{root\\}", clientURL);
        parameters.put(reportParameter, value);
      }
      
      JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, SQL.getConnection());
      byte[] pdfReportData = JasperExportManager.exportReportToPdf(print);
      BinaryResource br = new BinaryResource(reportFilename, pdfReportData); // TODO: mettre le label de la box ?
      return br;
    }
    catch (JRException e) {
      throw BEANS.get(PlatformExceptionTranslator.class).translate(e);
    }
  }

}
