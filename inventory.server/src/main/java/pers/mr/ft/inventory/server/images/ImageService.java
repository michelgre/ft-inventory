package pers.mr.ft.inventory.server.images;

import org.eclipse.scout.rt.platform.holders.NVPair;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.server.jdbc.SQL;

import pers.mr.ft.inventory.shared.images.IImageService;

public class ImageService implements IImageService {

  @Override
  public byte[] getImageData(Long imageId, boolean iconic) {
    
    String field = "image";
    if (iconic) {
      field = "tn_100";
    }
    String query = "SELECT "+field+" FROM image WHERE id = :imageId ";
    Object[][] rows = SQL.select(query, 
        new NVPair("imageId", imageId));
    if (rows.length==1) {
      BinaryResource imageData = new BinaryResource ("image", (byte[]) rows[0][0]);
      return imageData.getContent();
    }
    return new byte[0];

  }

}
