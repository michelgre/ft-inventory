/*
 * Copyright (c) 2015 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 */
package pers.mr.ft.inventory.server.sql;

import org.eclipse.scout.rt.platform.config.AbstractStringConfigProperty;

// tag::structure[]
public class DatabaseProperties {

  public static class JdbcMappingNameProperty extends AbstractStringConfigProperty {
    // defines default value and key
    // end::structure[]

    @Override
    public String getDefaultValue() {
      return "";
    }

    @Override
    public String getKey() {
      return "inventory.database.jdbc.mappingName";
    }

    @Override
    public String description() {
      return "JDBC mapping name for the database.";
    }
    // tag::structure[]
  }

  public static class JdbcUsernameProperty extends AbstractStringConfigProperty {
    // defines default value and key
    // end::structure[]

    @Override
    public String getDefaultValue() {
      return "";
    }

    @Override
    public String getKey() {
      return "inventory.database.jdbc.username";
    }

    @Override
    public String description() {
      return "JDBC user name.";
    }
    // tag::structure[]
  }
  public static class JdbcPasswordProperty extends AbstractStringConfigProperty {
    // defines default value and key
    // end::structure[]

    @Override
    public String getDefaultValue() {
      return "";
    }

    @Override
    public String getKey() {
      return "inventory.database.jdbc.password";
    }

    @Override
    public String description() {
      return "JDBC password.";
    }
    // tag::structure[]
  }
}
// end::structure[]
