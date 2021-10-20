package pers.mr.ft.inventory.client.pages;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.platform.text.TEXTS;

import pers.mr.ft.inventory.shared.pages.ConstructionSetTablePageData;

@Data(ConstructionSetTablePageData.class)
public class ConstructionSetTablePage extends PartsTablePage {
  public ConstructionSetTablePage() {
    withBuildingKits(true);  
  }
  
  @Override
  protected String getConfiguredTitle() {
    return TEXTS.get("ConstructionSets");
  }

  @Override
  protected void execInitTable() {
    super.execInitTable();
    
    Table table = getTable();
    table.getPartsCountColumn().setVisible(true);
    table.getInventoryCountColumn().setVisible(false);
    table.getColorColumn().setVisible(false);
  }
}
