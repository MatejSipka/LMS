package cz.muni.fi.xryvola.components;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.navigateApp.*;
import cz.muni.fi.xryvola.services.FileUploader;
import org.apache.shiro.SecurityUtils;
import org.vaadin.cssinject.CSSInject;

import java.io.File;

/**
 * Created by adam on 26.11.14.
 */
public class MenuComponent extends CustomComponent {

    private VerticalLayout menuContent;
    private int currView;

    public MenuComponent(int currView){
        this.currView = currView;
        addStyleName("valo-menu");
        setSizeUndefined();

        menuContent = new VerticalLayout();
        menuContent.addStyleName("sidebar");
        menuContent.addStyleName(ValoTheme.MENU_PART);
        menuContent.addStyleName("no-vertical-drag-hints");
        menuContent.addStyleName("no-horizontal-drag-hints");
        menuContent.setWidth(null);
        menuContent.setHeight("100%");

        menuContent.addComponent(buildTitle());
        menuContent.addComponent(buildUserInfo());

        if (((MyVaadinUI)UI.getCurrent()).getCurrentUser().getRole().equals("TEACHER")) {
            buildTeacherContent();
        }else if (((MyVaadinUI)UI.getCurrent()).getCurrentUser().getRole().equals("STUDENT")){
            buildStudentContent();
        }
        setCompositionRoot(menuContent);
    }


    private void buildTeacherContent(){
        Button item0 = new Button("Domů");
        Button item1 = new Button("Studijní materiály");
        Button item2 = new Button("Třídy a žáci");
        Button item3 = new Button("Statistiky");

        item0.setPrimaryStyleName("valo-menu-item");
        item0.setIcon(FontAwesome.HOME);

        item1.setPrimaryStyleName("valo-menu-item");
        item1.setIcon(FontAwesome.FILES_O);

        item2.setPrimaryStyleName("valo-menu-item");
        item2.setIcon(FontAwesome.USERS);

        item3.setPrimaryStyleName("valo-menu-item");
        item3.setIcon(FontAwesome.BAR_CHART_O);

        menuContent.addComponent(item0);
        menuContent.addComponent(item1);
        menuContent.addComponent(item2);
        menuContent.addComponent(item3);

        item0.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().getNavigator().addView(MyVaadinUI.STARTVIEW, new StartView());
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.STARTVIEW);
            }
        });

        item1.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().getNavigator().addView(MyVaadinUI.MATERIALSVIEW, new TeacherMaterialsView());
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.MATERIALSVIEW);
            }
        });

        item2.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                    UI.getCurrent().getNavigator().addView(MyVaadinUI.TEACHERSTUDENTVIEW, new TeacherStudentsView());
                    UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.TEACHERSTUDENTVIEW);
            }
        });

        item3.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().getNavigator().addView(MyVaadinUI.TEACHERSTATICS, new TeacherStaticsView());
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.TEACHERSTATICS);
            }
        });

        switch(currView){
            case 0: item0.addStyleName("selected");
                    break;
            case 1: item1.addStyleName("selected");
                    break;
            case 2: item2.addStyleName("selected");
                    break;
            case 3: item3.addStyleName("selected");
        }

        Button logout = new Button("Odhlásit");
        logout.setPrimaryStyleName("valo-menu-title");
        logout.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                SecurityUtils.getSubject().logout();
                ((MyVaadinUI)UI.getCurrent()).setCurrentUser(null);
                UI.getCurrent().getNavigator().navigateTo("");
            }
        });

        menuContent.addComponent(logout);
        menuContent.setComponentAlignment(logout, Alignment.BOTTOM_CENTER);
    }

    private void buildStudentContent(){
        Button item0 = new Button("Domů");
        Button item1 = new Button("Moje studijní výsledky");
        Button item2 = new Button("Herní statistika");
        Button item3 = new Button("Novinky na škole");
        Button item4 = new Button("Studijní materiály");

        item0.setPrimaryStyleName("valo-menu-item");
        item0.setIcon(FontAwesome.HOME);

        item1.setPrimaryStyleName("valo-menu-item");
        item1.setIcon(FontAwesome.BAR_CHART_O);

        item2.setPrimaryStyleName("valo-menu-item");
        item2.setIcon(FontAwesome.GAMEPAD);

        item3.setPrimaryStyleName("valo-menu-item");
        item3.setIcon(FontAwesome.INSTITUTION);

        item4.setPrimaryStyleName("valo-menu-item");
        item4.setIcon(FontAwesome.FILES_O);

        menuContent.addComponent(item0);
        menuContent.addComponent(item1);
        menuContent.addComponent(item2);
        menuContent.addComponent(item3);
        menuContent.addComponent(item4);

        item0.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().getNavigator().addView(MyVaadinUI.STARTVIEW, new StartView());
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.STARTVIEW);
            }
        });

        item4.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().getNavigator().addView(MyVaadinUI.STUDENTCONTENTVIEW, new StudentMaterialsView());
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.STUDENTCONTENTVIEW);
            }
        });

        switch (currView){
            case 0: item0.addStyleName("selected");
                    break;
            case 1: item1.addStyleName("selected");
                    break;
            case 2: item2.addStyleName("selected");
                    break;
            case 3: item3.addStyleName("selected");
                    break;
            case 4: item4.addStyleName("selected");
                    break;
        }

        Button logout = new Button("Odhlásit");
        logout.setPrimaryStyleName("valo-menu-title");
        logout.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                SecurityUtils.getSubject().logout();
                ((MyVaadinUI)UI.getCurrent()).setCurrentUser(null);
                UI.getCurrent().getNavigator().navigateTo("");
            }
        });

        menuContent.addComponent(logout);
        menuContent.setComponentAlignment(logout, Alignment.BOTTOM_CENTER);
    }

    private Component buildTitle(){
        Label logo = new Label("LMS <strong>menu</strong>", ContentMode.HTML);
        logo.setSizeUndefined();
        HorizontalLayout logoWrapper = new HorizontalLayout(logo);
        logoWrapper.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
        logoWrapper.addStyleName("valo-menu-title");
        return logoWrapper;
    }

    private Component buildUserInfo(){
        VerticalLayout userInfo = new VerticalLayout();

        Image userIcon = new Image();
        userIcon.setPrimaryStyleName("circular");
        userInfo.addComponent(userIcon);
        CSSInject cssInject = new CSSInject(UI.getCurrent());
        cssInject.setStyles(".circular {width: 100px; height: 100px; border-radius: 150px; -webkit-border-radius: 150px; -moz-border-radius: 150px; margin: 10px;");
        userInfo.setComponentAlignment(userIcon, Alignment.TOP_CENTER);
        userIcon.setSource(new FileResource(new File(MyVaadinUI.MYFILEPATH + "images/" + ((MyVaadinUI)UI.getCurrent()).getCurrentUser().getUsername() +".png")));
        userIcon.addClickListener(
                new MouseEvents.ClickListener() {
                    @Override
                    public void click(MouseEvents.ClickEvent clickEvent) {
                        final Window profilePic = new Window("Profilový obrázek");
                        profilePic.center();
                        profilePic.setModal(true);
                        UI.getCurrent().addWindow(profilePic);
                        FileUploader uploader = new FileUploader();
                        VerticalLayout lay = new VerticalLayout();
                        lay.setMargin(true);
                        lay.addComponent(uploader);
                        profilePic.setContent(lay);
                    }
                });
        Label userName = new Label(((MyVaadinUI)UI.getCurrent()).getCurrentUser().getName());
        userName.setPrimaryStyleName("username");
        CSSInject cssInjectName = new CSSInject(UI.getCurrent());
        cssInjectName.setStyles(".username{text-align: center; margin-bottom: 10px;}");
        userInfo.addComponent(userName);
        userInfo.setComponentAlignment(userName, Alignment.TOP_CENTER);
        return userInfo;
    }
}
