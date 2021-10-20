/*
 * Copyright (c) 2020 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package pers.mr.ft.inventory.server.sql;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.config.CONFIG;
import org.eclipse.scout.rt.server.jdbc.postgresql.AbstractPostgreSqlService;

import pers.mr.ft.inventory.server.sql.DatabaseProperties.JdbcMappingNameProperty;
import pers.mr.ft.inventory.server.sql.DatabaseProperties.JdbcPasswordProperty;
import pers.mr.ft.inventory.server.sql.DatabaseProperties.JdbcUsernameProperty;

@Order(1950)
// tag::service[]
public class PostgreSqlService extends AbstractPostgreSqlService  {
  @Override
  protected String getConfiguredJdbcMappingName() {
    String mappingName = CONFIG.getPropertyValue(JdbcMappingNameProperty.class);

    return mappingName;
  }
  
  @Override
  protected String getConfiguredUsername() {
    return CONFIG.getPropertyValue(JdbcUsernameProperty.class);
  }
  
  @Override
  protected String getConfiguredPassword() {
    return CONFIG.getPropertyValue(JdbcPasswordProperty.class);
  }
}
// end::service[]
