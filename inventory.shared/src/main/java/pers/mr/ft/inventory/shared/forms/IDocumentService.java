package pers.mr.ft.inventory.shared.forms;

import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface IDocumentService extends IService {
  DocumentFormData prepareCreate(DocumentFormData formData);

  DocumentFormData create(DocumentFormData formData);

  DocumentFormData load(DocumentFormData formData);

  DocumentFormData store(DocumentFormData formData);
  
  void storeContent(Long documentId, BinaryResource docContent);
  BinaryResource loadContent(Long documentId);
  void updateDigests();
  String computeDigest(BinaryResource docContent);
  Long findDigest(String digest);
  
}
