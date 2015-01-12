package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.components.LoginWindow;
import cz.muni.fi.xryvola.components.RegistrationWindow;

/**
 * Created by adam on 26.10.14.
 */

public class MainMenu extends VerticalLayout implements View {

    public MainMenu(){
        HorizontalLayout butts = new HorizontalLayout();

        Image logo = new Image();
        logo.setSource(new ThemeResource("images/keep-calm.png"));
        logo.setWidth(300, Unit.PIXELS);
        this.addComponent(logo);

        Button reg = new Button("REGISTRACE");
        Button login = new Button("PŘIHLÁŠENÍ");

        reg.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
        reg.setIcon(FontAwesome.CHEVRON_UP);
        login.setStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
        login.setIcon(FontAwesome.SIGN_IN);

        butts.addComponent(reg);
        butts.addComponent(login);
        butts.setSizeUndefined();
        //butts.addStyleName("butts");
        this.setSpacing(true);
        this.setMargin(true);
        this.addComponent(butts);
        this.setSizeFull();
        this.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
        this.setComponentAlignment(butts, Alignment.MIDDLE_CENTER);

        reg.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().addWindow(new RegistrationWindow());
            }
        });

        login.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                    UI.getCurrent().addWindow(new LoginWindow());
            }
        });
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
