package cz.muni.fi.xryvola.auth;

import com.vaadin.ui.UI;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.services.*;
import org.apache.shiro.authc.*;
import org.apache.shiro.realm.jdbc.JdbcRealm;

/**
 * Created by adam on 23.11.14.
 */
public class MyRealm extends JdbcRealm {

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(

    AuthenticationToken token) throws AuthenticationException {
        // identify account to log to
        UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;
        final String username = userPassToken.getUsername();

        if (username == null) {
            System.out.println("Username is null.");
            return null;
        }

        // read password hash and salt from db
        SuperManager superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();
        Person person = superManager.getPersonManager().getPersonByUsername(username);

        if (person == null) {
            System.out.println("Nenalezen ucet pro uzivatele [" + username + "]");
            return null;
        }

        // return salted credentials
        SaltedAuthenticationInfo info = new MySaltedAuthentificationInfo(
            username, person.getPassword(), person.getSalt());

        return info;

    }
}
