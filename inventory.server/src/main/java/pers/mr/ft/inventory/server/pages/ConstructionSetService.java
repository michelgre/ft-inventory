package pers.mr.ft.inventory.server.pages;

import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import pers.mr.ft.inventory.shared.pages.ConstructionSetTablePageData;
import pers.mr.ft.inventory.shared.pages.IConstructionSetService;

public class ConstructionSetService implements IConstructionSetService {
  @Override
  public ConstructionSetTablePageData getConstructionSetTableData(SearchFilter filter) {
    ConstructionSetTablePageData pageData = new ConstructionSetTablePageData();
    // TODO [michel] fill pageData.
    return pageData;
  }
}
