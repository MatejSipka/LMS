package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.StringToDateConverter;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.components.MenuComponent;
import cz.muni.fi.xryvola.components.SlideShowWIndow;
import cz.muni.fi.xryvola.services.Classroom;
import cz.muni.fi.xryvola.services.ContentSharing;
import cz.muni.fi.xryvola.services.SuperManager;
import cz.muni.fi.xryvola.services.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

/**
 * Created by adam on 7.12.14.
 */
public class StudentMaterialsView extends HorizontalLayout implements View {

    private SuperManager superManager;

    private HorizontalLayout content;
    private HorizontalLayout conTableLay;
    private VerticalLayout butts;
    private MenuComponent menu;

    private Table contentTable = new Table();
    private Button present;
    private Button testing;

    public StudentMaterialsView(){

        this.superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();
        this.setSizeFull();

        menu = new MenuComponent(4);
        this.addComponent(menu);

        content = new HorizontalLayout();
        content.setSizeFull();
        content.setMargin(true);
        content.setSpacing(true);
        addComponent(content);
        setExpandRatio(content, 1.0f);


        conTableLay = new HorizontalLayout();

        Button refresh = new Button();
        refresh.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        refresh.setStyleName(ValoTheme.BUTTON_QUIET);
        refresh.setIcon(FontAwesome.REFRESH);
        refresh.setDescription("znovu načíst tabulku");
        refresh.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                loadTable();
            }
        });
        conTableLay.addComponent(refresh);
        conTableLay.setComponentAlignment(refresh, Alignment.TOP_LEFT);


        initTable();
        initButts();

        content.addComponent(conTableLay);
        content.addComponent(butts);
    }
    private void initTable(){
        contentTable = new Table();
        contentTable.setSelectable(true);
        contentTable.setImmediate(true);
        contentTable.addContainerProperty("Název", String.class, null);
        contentTable.addContainerProperty("Typ", String.class, null);
        contentTable.addContainerProperty("Autor", String.class, null);
        contentTable.addContainerProperty("Od", Date.class, null);
        contentTable.addContainerProperty("Do", Date.class, null);

        loadTable();

        contentTable.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (contentTable.getValue() != null) {
                    Item it = contentTable.getItem(contentTable.getValue());
                    String type = (String) it.getItemProperty("Typ").getValue();
                    if (type.equals("test")) {
                        testing.setVisible(true);
                        present.setVisible(false);
                    }else if (type.equals("prezentace")){
                        testing.setVisible(false);
                        present.setVisible(true);
                    }
                }else{
                    present.setVisible(false);
                    testing.setVisible(false);
                }
            }
        });

        StringToDateConverter tableConverter = new StringToDateConverter(){
            @Override
            public DateFormat getFormat(Locale locale){
                return new SimpleDateFormat("dd.MM.yyyy HH:mm");
            }
        };
        contentTable.setConverter("Do", tableConverter);
        contentTable.setConverter("Od", tableConverter);

        conTableLay.addComponent(contentTable);
    }

    private void initButts(){
        butts = new VerticalLayout();

        present = new Button("Spustit prezentaci");
        present.setVisible(false);
        present.setIcon(FontAwesome.DESKTOP);
        present.addStyleName(ValoTheme.BUTTON_QUIET);
        butts.addComponent(present);

        present.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Long presId = superManager.getContentSharingManager().getContentSharingById((Long) contentTable.getValue()).getDocumentId();
                Window slideShow = new SlideShowWIndow(superManager.getPresentationManager().getPresentationById(presId));
            }
        });
        testing = new Button("Spustit test");
        testing.setVisible(false);
        testing.setIcon(FontAwesome.QUESTION);
        testing.addStyleName(ValoTheme.BUTTON_QUIET);
        butts.addComponent(testing);
        testing.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Long testId = superManager.getContentSharingManager().getContentSharingById((Long) contentTable.getValue()).getDocumentId();
                Test test = superManager.getTestManager().getTestById(testId);
                superManager.getEm().refresh(test);
                UI.getCurrent().getNavigator().addView(MyVaadinUI.STUDENTTESTING, new StudentTestView(test));
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.STUDENTTESTING);
            }
        });
    }

    private void loadTable(){
        contentTable.removeAllItems();
        Classroom myClassroom = MyVaadinUI.currUser.getMyClass();
        Collection<ContentSharing> contentListPres = superManager.getContentSharingManager().getContentSharingFromClassroom(myClassroom.getId());
        Date curr = new Date();
        //TODO FILTERED TABLE
        for (ContentSharing cs : contentListPres){
            if (cs.getWhen().before(curr) && cs.getTill().after(curr)) {
                Item it = contentTable.addItem(cs.getId());
                if (cs.getDocumentType().equals("PRESENTATION")){
                    it.getItemProperty("Typ").setValue("prezentace");
                    it.getItemProperty("Název").setValue(superManager.getPresentationManager().getPresentationById(cs.getDocumentId()).getName());
                }else if (cs.getDocumentType().equals("TEST")){
                    it.getItemProperty("Typ").setValue("test");
                    it.getItemProperty("Název").setValue(superManager.getTestManager().getTestById(cs.getDocumentId()).getName());
                }
                it.getItemProperty("Autor").setValue(superManager.getPersonManager().getPersonById(cs.getTeacherId()).getName());
                it.getItemProperty("Od").setValue(cs.getWhen());
                it.getItemProperty("Do").setValue(cs.getTill());
            }
        }
        contentTable.setPageLength(contentTable.size());
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        loadTable();
    }
}
