package pers.mr.ft.inventory.server.pages;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.server.jdbc.SQL;
import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.server.ServerSession;
import pers.mr.ft.inventory.shared.codetype.HistoyTypeCodeType.HistoryType;
import pers.mr.ft.inventory.shared.pages.HistoryTablePageData;
import pers.mr.ft.inventory.shared.pages.HistoryTablePageData.HistoryTableRowData;
import pers.mr.ft.inventory.shared.pages.IHistoryService;
import pers.mr.ft.inventory.shared.security.FTPrincipal;

public class HistoryService implements IHistoryService {
  static class PartData extends AbstractFormData {
    private static final long serialVersionUID = 1L;
    
    String label;
    String defaultLabel;
    String color;
    String icon;
    
    public String getLabel() {
      return label;
    }
    
    public void setLabel(String label) {
      this.label = label;
    }
    public String getDefaultLabel() {
      return defaultLabel;
    }
    public void setDefaultLabel(String defaultLabel) {
      this.defaultLabel = defaultLabel;
    }
    public String getColor() {
      return color;
    }
    public void setColor(String color) {
      this.color = color;
    }
    public String getIcon() {
      return icon;
    }
    public void setIcon(String icon) {
      this.icon = icon;
    }
    
    
  }
  
  @Override
  public HistoryTablePageData getHistoryTableData(SearchFilter filter) {
    HistoryTablePageData pageData = new HistoryTablePageData();
    FTPrincipal principal = ServerSession.get().getPrincipal();
    
    SQL.selectInto("SELECT id, ts, type, info, i1, i2, i3, i4, s1, s2, user_id FROM history "
        + " WHERE user_id = :principalId "
        + " ORDER BY ts DESC,id DESC " +
        " INTO :id, :date, :type, :info, :i1, :i2, :i3, :i4, :s1, :s2, :user", 
        new NVPair("principalId", principal.getId()),
        pageData);
    
    // Contenu dynamique colonne info
    for (HistoryTableRowData row: pageData.getRows()) {
      String typeCode = row.getType();
      HistoryType type = HistoryType.getByCode(typeCode);
      String icon = null;
      
      Long i1 = row.getI1();
      Long i2 = row.getI2();
      Long i3 = row.getI3();
      Long i4 = row.getI4();
      
      String info = row.getInfo();
      if (info == null) { // Info par défaut
        switch (type) {
        case Move:
          PartData partData = getPartData(row, i3);
          info = i4 + " x " + partData.getLabel()  + "\n" + i1 + " => " + i2;
          row.setInfo(info);
          row.setIcon(partData.getIcon());
          break;
        default:
          break;
        }
      }
    }
    return pageData;
  }
  
  private PartData getPartData(HistoryTableRowData row, Long partId) {
    String userLanguage = ServerSession.get().getSessionLanguage();
    PartData partData = new PartData();
    Object [][] data = SQL.select("SELECT part_label, default_label, part_icon, mlc.label color "
        + " FROM v_parts_page "
        + " LEFT JOIN COLOR c ON c.id = color_id "
        + " LEFT JOIN multilingual_label mlc ON mlc.id = c.label_id AND mlc.langcode = :userLanguage"
        + " WHERE v_parts_page.id = :partId ", 
        new NVPair("partId", partId),
        new NVPair("userLanguage", userLanguage));
    if (data.length>0) {
      Object[] dr = data[0];
      Integer field = 0;
      partData.label = (String) dr[field++];
      partData.defaultLabel = (String) dr[field++];
      partData.icon = (String) dr[field++];
      partData.color = (String) dr[field++];
      
    }
    return partData;
  }
}
