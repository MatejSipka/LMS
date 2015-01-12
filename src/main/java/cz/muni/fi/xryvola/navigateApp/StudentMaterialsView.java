package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.data.Item;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.components.MenuComponent;
import cz.muni.fi.xryvola.components.SlideShowWIndow;
import cz.muni.fi.xryvola.services.Classroom;
import cz.muni.fi.xryvola.services.SuperManager;

import java.util.Collection;

/**
 * Created by adam on 7.12.14.
 */
public class StudentMaterialsView extends HorizontalLayout implements View {

    private SuperManager superManager;

    private VerticalLayout conTableLay;
    private VerticalLayout butts;
    private MenuComponent menu;

    Table contentTable = new Table();

    public StudentMaterialsView(){

        this.superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();

        setSizeFull();

        menu = new MenuComponent(4);
        this.addComponent(menu);

        conTableLay = new VerticalLayout();
        butts = new VerticalLayout();
        this.addComponents(conTableLay, butts);

        initTable();
        initButts();


    }
    private void initTable(){
        contentTable = new Table();
        contentTable.setSelectable(true);
        conTableLay.addComponent(contentTable);
        contentTable.addContainerProperty("Nazev", String.class, null);
        contentTable.addContainerProperty("Typ", String.class, null);

        Classroom myClassroom = MyVaadinUI.currUser.getMyClass();
        Collection<Long> contentListPres = superManager.getContentSharingManager().getPresentationsFromClassroom(myClassroom.getId());
        Collection<Long> contentListTest = superManager.getContentSharingManager().getTestsFromClassroom(myClassroom.getId());

        for(Long l : contentListPres){
            Item it = contentTable.addItem(l);
            it.getItemProperty("Nazev").setValue(superManager.getPresentationManager().getPresentationById(l).getName());
            it.getItemProperty("Typ").setValue("prezentace");
        }

        for(Long l : contentListTest){
            Item it = contentTable.addItem(l);
            it.getItemProperty("Nazev").setValue(superManager.getTestManager().getTestById(l).getName());
            it.getItemProperty("Typ").setValue("test");
        }
    }

    private void initButts(){
        final Button present = new Button("Prezentovat");
        present.setIcon(FontAwesome.DESKTOP);
        present.addStyleName(ValoTheme.BUTTON_QUIET);
        butts.addComponent(present);

        present.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Window slideShow = new SlideShowWIndow(superManager.getPresentationManager().getPresentationById((Long) contentTable.getValue()));
            }
        });

        final Button testing = new Button("Testovat");
        testing.setIcon(FontAwesome.QUESTION);
        testing.addStyleName(ValoTheme.BUTTON_QUIET);
        butts.addComponent(testing);
        testing.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().getNavigator().addView(MyVaadinUI.STUDENTTESTING, new StudentTestView(superManager.getTestManager().getTestById((Long)contentTable.getValue())));
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.STUDENTTESTING);
            }
        });


    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
