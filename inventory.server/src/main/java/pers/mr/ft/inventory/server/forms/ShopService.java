package pers.mr.ft.inventory.server.forms;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.security.ACCESS;
import org.eclipse.scout.rt.server.jdbc.SQL;

import pers.mr.ft.inventory.shared.forms.CreateShopPermission;
import pers.mr.ft.inventory.shared.forms.IShopService;
import pers.mr.ft.inventory.shared.forms.ReadShopPermission;
import pers.mr.ft.inventory.shared.forms.ShopFormData;
import pers.mr.ft.inventory.shared.forms.UpdateShopPermission;

public class ShopService implements IShopService {
  @Override
  public ShopFormData prepareCreate(ShopFormData formData) {
    if (!ACCESS.check(new CreateShopPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    // TODO [mreverbel] add business logic here.
    return formData;
  }

  @Override
  public ShopFormData create(ShopFormData formData) {
    if (!ACCESS.check(new CreateShopPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    SQL.insert("INSERT INTO shop (label, query) " + 
        " VALUES (:label, :query)", 
        formData);
    
    Object[][] rows = SQL.select("SELECT LASTVAL()");
    Long createdId = (Long) rows[0][0];
   
    formData.setObjectId(createdId);
    formData.getId().setValue(createdId);
    return formData;
  }

  @Override
  public ShopFormData load(ShopFormData formData) {
    if (!ACCESS.check(new ReadShopPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    SQL.selectInto("SELECT id, label, query " +
        " FROM shop " +
        " WHERE id = :objectId " +
        " INTO :id, :label, :query",
        formData);
    return formData;
  }

  @Override
  public ShopFormData store(ShopFormData formData) {
    if (!ACCESS.check(new UpdateShopPermission())) {
      throw new VetoException(TEXTS.get("AuthorizationFailed"));
    }
    SQL.update("UPDATE shop SET label = :label, query = :query " +
        " WHERE id = :objectId", 
        formData);
    return formData;
  }
}
