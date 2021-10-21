package pers.mr.ft.inventory.server.forms;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.security.ACCESS;
import org.eclipse.scout.rt.server.jdbc.SQL;

import pers.mr.ft.inventory.shared.forms.CreateLocationPermission;
import pers.mr.ft.inventory.shared.forms.ILocationService;
import pers.mr.ft.inventory.shared.forms.LocationFormData;
import pers.mr.ft.inventory.shared.forms.ReadLocationPermission;
import pers.mr.ft.inventory.shared.forms.UpdateLocationPermission;

public class LocationService implements ILocationService {
  @Override
  public LocationFormData prepareCreate(LocationFormData formData) {
    if (!ACCESS.check(new CreateLocationPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO [mreverbel] add business logic here.
    return formData;
  }

  @Override
  public LocationFormData create(LocationFormData formData) {
    if (!ACCESS.check(new CreateLocationPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    SQL.insert("INSERT INTO location (location, description) " + 
        " VALUES (:location, :description)", 
        formData);
    
    Object[][] rows = SQL.select("SELECT LASTVAL()");
    Long createdId = (Long) rows[0][0];
   
    formData.setObjectId(createdId);
    formData.getId().setValue(createdId);

    return formData;
  }

  @Override
  public LocationFormData load(LocationFormData formData) {
    if (!ACCESS.check(new ReadLocationPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    SQL.selectInto("SELECT id, location, description " +
        " FROM location " +
        " WHERE id = :objectId " +
        " INTO :id, :location, :description",
        formData);
    return formData;
  }

  @Override
  public LocationFormData store(LocationFormData formData) {
    if (!ACCESS.check(new UpdateLocationPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    SQL.update("UPDATE location SET location = :location, description = :description " +
        " WHERE id = :objectId", 
        formData);
    return formData;
  }
}
