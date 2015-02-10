package cz.muni.fi.xryvola.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.navigateApp.StartView;
import cz.muni.fi.xryvola.services.Person;
import cz.muni.fi.xryvola.services.SuperManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;

/**
 * Created by adam on 26.10.14.
 */
public class LoginWindow extends Window {

    private IniSecurityManagerFactory factory = new IniSecurityManagerFactory("classpath:shiro.Ini");
    private SecurityManager securityManager = factory.getInstance();
    private SuperManager superManager;


    public LoginWindow(){

        this.superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();

        this.center();
        VerticalLayout main = new VerticalLayout();
        this.setContent(main);
        this.setModal(true);

        SecurityUtils.setSecurityManager(securityManager);

        Label welcome = new Label("VÍTEJTE");
        welcome.addStyleName(ValoTheme.LABEL_HUGE);
        main.addComponent(welcome);
        main.setMargin(true);
        final TextField userName = new TextField("Přihlašovací jméno:");
        final PasswordField password = new PasswordField("Heslo:");
        Button acc = new Button("Přihlásit");
        acc.setStyleName(ValoTheme.BUTTON_FRIENDLY);

        userName.setIcon(FontAwesome.USER);
        userName.setStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        userName.setImmediate(true);
        userName.focus();

        password.setIcon(FontAwesome.LOCK);
        password.setStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

        HorizontalLayout inputs = new HorizontalLayout();
        inputs.setSpacing(true);
        main.addComponent(inputs);
        inputs.addComponent(userName);
        inputs.addComponent(password);
        inputs.addComponent(acc);

        inputs.setComponentAlignment(acc, Alignment.BOTTOM_LEFT);

        acc.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        acc.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Subject currentUser = SecurityUtils.getSubject();
                if (!currentUser.isAuthenticated()) {
                    UsernamePasswordToken token = new UsernamePasswordToken(userName.getValue(), password.getValue());
                    token.setRememberMe(true);
                    try {
                        currentUser.login(token);
                        Person p = superManager.getPersonManager().getPersonByUsername(currentUser.getPrincipal().toString());
                        ((MyVaadinUI)UI.getCurrent()).setCurrentUser(p);
                        System.out.println("User [" + ((MyVaadinUI)UI.getCurrent()).getCurrentUser().getUsername() + "] logged in successfully.");
                        UI.getCurrent().getNavigator().addView(MyVaadinUI.STARTVIEW, new StartView());
                        UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.STARTVIEW);
                        close();
                    } catch (UnknownAccountException uae) {
                        Notification notif = new Notification("Uživateské jméno neexistuje");
                        notif.show(Page.getCurrent());
                    } catch (IncorrectCredentialsException ice) {
                        Notification notif = new Notification("Heslo je špatné");
                        notif.show(Page.getCurrent());
                        System.out.println("Password for account " + token.getPrincipal() + " was incorrect!");
                    } catch (LockedAccountException lae) {
                        Notification notif = new Notification("Uživateské jméno je zablokované");
                        notif.show(Page.getCurrent());
                        System.out.println("The account for username " + token.getPrincipal() + " is locked.  " +
                                "Please contact your administrator to unlock it.");
                    } catch (AuthenticationException ae) {
                        //unexpected condition?  error?
                        Notification notif = new Notification("Vyskytla se neočekávaná chyba, kontaktujte správce");
                        notif.show(Page.getCurrent());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
