package cz.muni.fi.xryvola;

import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import cz.muni.fi.xryvola.navigateApp.MainMenu;
import cz.muni.fi.xryvola.services.*;

@Theme("mytheme")
@SuppressWarnings("serial")
public class MyVaadinUI extends UI
{
    public static final String MYFILEPATH = "/home/adam/Plocha/BPGenerator/";
    public Navigator navigator;
    public static final String STARTVIEW = "start";
    public static final String PRESENTATIONVIEW = "presentation";
    public static final String TESTVIEW = "test";
    public static final String MATERIALSVIEW = "materials";
    public static final String TEACHERSTUDENTVIEW = "teacherstudent";
    public static final String STUDENTCONTENTVIEW = "studentcontent";
    public static final String STUDENTTESTING = "studenttesting";
    public static Person currUser = null;
    public static SuperManager superManager = null;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MyVaadinUI.class, widgetset = "cz.muni.fi.xryvola.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        superManager = new SuperManager();
        final VerticalLayout layout = new VerticalLayout();
        setContent(layout);
        Navigator.ComponentContainerViewDisplay viewDisplay = new Navigator.ComponentContainerViewDisplay(layout);
        navigator = new Navigator(UI.getCurrent(), viewDisplay);
        navigator.addView("", new MainMenu());
    }

    public SuperManager getSuperManager() {
        return superManager;
    }
}
