package pers.mr.ft.inventory.ui.html;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.scout.rt.platform.Bean;
import org.eclipse.scout.rt.platform.Replace;
import org.eclipse.scout.rt.platform.util.ImmutablePair;
import org.eclipse.scout.rt.platform.util.Pair;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.server.commons.authentication.FormBasedAccessController;

@Bean
@Replace
public class MyFormBasedAccessController extends FormBasedAccessController {
  @Override
  protected Pair<String, char[]> readCredentials(HttpServletRequest request) {
    String typedUser = request.getParameter("user");
    if (typedUser!=null) {
      typedUser = typedUser.trim();
    }
    final String user = typedUser;
    if (StringUtility.isNullOrEmpty(user)) {
      return null;
    }

    final String password = request.getParameter("password");
    if (StringUtility.isNullOrEmpty(password)) {
      return null;
    }

    // Generally, passwords should be stored as char array, because Strings are immutable and
    // might stay in the memory forever, while char arrays _could_ be overwritten after user:
    // http://stackoverflow.com/questions/8881291/why-is-char-preferred-over-string-for-passwords-in-java
    // However, the password is already a String when using the servlet API, so this technique
    // is basically useless... http://stackoverflow.com/questions/15016250/in-java-how-do-i-extract-a-password-from-a-httpservletrequest-header-without-ge
    // We do it nevertheless to prevent accidental logging of passwords.
    return new ImmutablePair<>(user, password.toCharArray());
  }
}
