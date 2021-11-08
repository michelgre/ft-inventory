package pers.mr.ft.inventory.client.forms;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.eclipse.scout.rt.client.ui.form.ScoutInfoForm;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class InfoForm extends ScoutInfoForm {
  
  @Override
  protected Map<String,Object> getProperties() {
    Map<String,Object> props = super.getProperties();
    
    Properties clientProps = new Properties();
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("build.properties");
    if (inputStream!=null) {
      try {
        clientProps.load(inputStream);
        props.put(TEXTS.get("ClientVersion"), clientProps.get("build.version"));
        
        SimpleDateFormat mavenDateFormat = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String dateInfo = (String) clientProps.get("build.date");
        try {
          Date buildDate = mavenDateFormat.parse(dateInfo);
          props.put(TEXTS.get("ClientBuild"), displayDateFormat.format(buildDate));
        }
        catch(ParseException e) {
        }
        inputStream.close();
      } catch(IOException e) {
        
      }
    }
    else {
      props.put(TEXTS.get("ClientVersion"), TEXTS.get("Development"));
    }
    return props;
  };
}
