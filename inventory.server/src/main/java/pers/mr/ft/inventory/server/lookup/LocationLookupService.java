package pers.mr.ft.inventory.server.lookup;

import org.eclipse.scout.rt.server.jdbc.lookup.AbstractSqlLookupService;

import pers.mr.ft.inventory.shared.lookup.ILocationLookupService;

public class LocationLookupService extends AbstractSqlLookupService<String> implements ILocationLookupService {
  @Override
  protected String getConfiguredSqlSelect() {
    return "" +
        "SELECT DISTINCT location, location FROM box " +
        "WHERE 1=1 " +
        "<key> AND location = :key </key>" +
        "<text> AND UPPER(location) LIKE UPPER(:text || '%') </text> " +
        "<all></all>";
  }

}
