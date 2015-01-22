package cz.muni.fi.xryvola.components;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.services.Presentation;
import cz.muni.fi.xryvola.services.SuperManager;
import org.vaadin.cssinject.CSSInject;

/**
 * Created by adam on 7.12.14.
 */
public class SlideShowWIndow extends Window {

    private Presentation presentation;
    private Label slideArea;
    private int i = 0;

    public SlideShowWIndow(Presentation pres){
        presentation = pres;
        UI.getCurrent().addWindow(this);
        this.center();
        this.setModal(true);
        this.setWindowMode(WindowMode.MAXIMIZED);
        HorizontalLayout tmp = new HorizontalLayout();
        HorizontalLayout tmp2 = new HorizontalLayout();
        Panel lay = new Panel();
        lay.setStyleName(ValoTheme.PANEL_BORDERLESS);

        tmp.setSizeFull();
        this.setContent(tmp);
        tmp.addComponent(tmp2);
        tmp.setComponentAlignment(tmp2, Alignment.MIDDLE_CENTER);
        Button next = new Button();
        Button prev = new Button();
        tmp2.addComponent(prev);

        tmp2.addComponent(lay);
        slideArea = new Label("", ContentMode.HTML);
        slideArea.setPrimaryStyleName("slide-label");
        CSSInject css = new CSSInject(UI.getCurrent());
        css.setStyles(".slide-label{ line-height: 1; word-wrap: break-word; font-family:\"Times New Roman\"; }");

        lay.setContent(slideArea);
        lay.setWidth("800px");
        lay.setHeight("600px");

        tmp2.addComponent(next);
        slideArea.setValue(presentation.getSlides().get(0).getHtmlContent());

        next.setClickShortcut(ShortcutAction.KeyCode.ARROW_RIGHT);
        prev.setClickShortcut(ShortcutAction.KeyCode.ARROW_LEFT);

        tmp2.setComponentAlignment(prev, Alignment.MIDDLE_LEFT);
        tmp2.setComponentAlignment(next, Alignment.MIDDLE_RIGHT);

        next.setIcon(FontAwesome.ANGLE_DOUBLE_RIGHT);
        prev.setIcon(FontAwesome.ANGLE_DOUBLE_LEFT);
        next.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        next.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        prev.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        prev.setStyleName(ValoTheme.BUTTON_BORDERLESS);

        /*
        final Label pages = new Label("1/" + presentation.getSlides().size());
        lay.addComponent(pages);
        lay.setComponentAlignment(pages, Alignment.BOTTOM_LEFT);

        Button fullscreen = new Button();
        fullscreen.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        fullscreen.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        fullscreen.setIcon(FontAwesome.ARROWS_ALT);
        lay.addComponent(fullscreen);
        lay.setComponentAlignment(fullscreen, Alignment.BOTTOM_LEFT);
        fullscreen.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    Robot robot = new Robot();
                    robot.keyPress(KeyEvent.VK_F11);
                    robot.keyRelease(KeyEvent.VK_F11);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            }
        });
        */

        //FOR TESTING
        CSSInject border = new CSSInject(UI.getCurrent());
        border.setStyles(".test-border { border: 2px solid red; }");


        next.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (i < presentation.getSlides().size()-1) {
                    i++;
                    slideArea.setValue(presentation.getSlides().get(i).getHtmlContent());
                    //pages.setValue(i+1 + "/" + presentation.getSlides().size());
                }

            }
        });

        prev.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (i > 0){
                    i--;
                    slideArea.setValue(presentation.getSlides().get(i).getHtmlContent());
                    //pages.setValue(i+1 + "/" + presentation.getSlides().size());
                }
            }
        });

    }
}
