package pers.mr.ft.inventory.shared.forms;

import java.util.List;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface IPartService extends IService {
  PartFormData prepareCreate(PartFormData formData);

  PartFormData create(PartFormData formData);

  PartFormData load(PartFormData formData);

  PartFormData store(PartFormData formData);
  
  PartFormData loadByNumber(String partNumber);
  
  List<Long> getBoxesFromModel(Long modelId);
  
  void syncFromDatenbank(Long partId);
  void syncCategoryFromDatenbank(Integer catId);
  void syncImagesFromDatenbank();
  void syncImagesFromDatenbank(Long partId);
}
