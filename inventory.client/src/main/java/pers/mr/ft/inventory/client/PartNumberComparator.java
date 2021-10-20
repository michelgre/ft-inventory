package pers.mr.ft.inventory.client;

import java.util.Comparator;
import java.util.regex.Pattern;

import org.eclipse.scout.rt.platform.util.StringUtility;

public class PartNumberComparator implements Comparator<String> {

  private String getPartNumber(String s) {
    if (s==null) return ""; // Elimine les nuls
    
    // Prendra le 1e n° s'il y en a plusieurs
    int posComma = s.indexOf(',');
    if (posComma==-1) {
      posComma = s.length();
    }
    
    int posColon = s.indexOf(':');
    
    if (posColon>=0) {
      if (posComma<posColon) {
        // Le 1e n° n'a pas d'année
        return s.substring(0, posComma);
      }
      else {
        return s.substring(posColon+1, posComma);
      }
    }
    else if (posComma<s.length()) {
      return s.substring(0,posComma); // Pas d'année, plusieurs n°
    }
    else {
      return s;  // 1 seul n°, pas d'année
    }
    
  }
  
  private String getNumericPart(String s) {  
    int i = 0;
    for (i=0; i<s.length(); i++) {
      char c = s.charAt(i);
      if (c<'0'||c>'9') {
        break;
      }
    }
    return s.substring(0, i);
  }
  
  @Override
  public int compare(String s1, String s2) {    
    
    s1 = getPartNumber(s1);
    s2 = getPartNumber(s2);
    
    Long i1 = 0L;
    Long i2 = 0L;
    
    String s1Num = getNumericPart(s1);
    if (s1Num.length()>0) {
      i1 = Long.parseLong(s1Num);
    }
    String s2Num = getNumericPart(s2);
    if (s2Num.length()>0) {
      i2 = Long.parseLong(s2Num);
    }
    
    if (i1 < i2) {
      return -1;
    }
    else if (i1 > i2) {
      return 1;
    }
    else {
      s1 = s1.substring(s1Num.length());
      s2 = s2.substring(s2Num.length());
      
      return StringUtility.compareIgnoreCase(s1, s2);
    }
  }

}
