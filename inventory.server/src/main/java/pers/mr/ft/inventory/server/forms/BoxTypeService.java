package pers.mr.ft.inventory.server.forms;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.security.ACCESS;
import org.eclipse.scout.rt.server.jdbc.SQL;

import pers.mr.ft.inventory.shared.forms.BoxTypeFormData;
import pers.mr.ft.inventory.shared.forms.CreateBoxTypePermission;
import pers.mr.ft.inventory.shared.forms.IBoxTypeService;
import pers.mr.ft.inventory.shared.forms.ReadBoxTypePermission;
import pers.mr.ft.inventory.shared.forms.UpdateBoxTypePermission;

public class BoxTypeService implements IBoxTypeService {
  @Override
  public BoxTypeFormData prepareCreate(BoxTypeFormData formData) {
    if (!ACCESS.check(new CreateBoxTypePermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO [mreverbel] add business logic here.
    return formData;
  }

  @Override
  public BoxTypeFormData create(BoxTypeFormData formData) {
    if (!ACCESS.check(new CreateBoxTypePermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    
    SQL.insert("INSERT INTO boxtype (description, length, width, height) " + 
        " VALUES (:description, :length, :width, :height)", 
        formData);
    
    Object[][] rows = SQL.select("SELECT LASTVAL()");
    Long boxTypeId = (Long) rows[0][0];
   
    formData.setBoxTypeId(boxTypeId);
    formData.getId().setValue(boxTypeId);

    return formData;
  }

  @Override
  public BoxTypeFormData load(BoxTypeFormData formData) {
    if (!ACCESS.check(new ReadBoxTypePermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }

    SQL.selectInto("SELECT id, description, length, width, height " +
        " FROM boxtype " +
        " WHERE id = :boxTypeId " +
        " INTO :id, :description, :length, :width, :height",
        formData);
    return formData;
  }

  @Override
  public BoxTypeFormData store(BoxTypeFormData formData) {
    if (!ACCESS.check(new UpdateBoxTypePermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    
    SQL.update("UPDATE boxtype SET description = :description, length = :length, width = :width, height = :height " +
        " WHERE id = :boxTypeId", 
        formData);
    return formData;
  }
}
