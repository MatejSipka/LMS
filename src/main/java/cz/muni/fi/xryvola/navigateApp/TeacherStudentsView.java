package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.Pair;
import cz.muni.fi.xryvola.components.MenuComponent;
import cz.muni.fi.xryvola.filteredTable.MyFilterDecorator;
import cz.muni.fi.xryvola.services.*;
import org.tepi.filtertable.FilterTable;

import javax.management.NotificationFilter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by adam on 3.12.14.
 */
public class TeacherStudentsView extends HorizontalLayout implements View {

    private MenuComponent menu;
    private HorizontalLayout content;

    private HorizontalLayout leftContent;
    private VerticalLayout rightContent;

    //left side
    Tree listOfClasses = new Tree("Moje třídy");
    VerticalLayout classesButts;

    //right side - top
    HorizontalLayout infoButts;
    HorizontalLayout infoButtsMat;
    HorizontalLayout infotButtsStu;
    //right side - down
    TabSheet classInfo = new TabSheet();
    Table students = new Table();
    FilterTable studentsToAdd = new FilterTable();
    Table classContent = new Table();

    private SuperManager superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();

    private ClassroomManager classroomManager = superManager.getClassroomManager();
    private PersonManager personManager = superManager.getPersonManager();
    private SchoolManager schoolManager = superManager.getSchoolManager();
    private ContentSharingManager CHManager = superManager.getContentSharingManager();
    private PresentationManager presentationManager = superManager.getPresentationManager();
    private TestManager testManager = superManager.getTestManager();

    private Long currClassId = Long.valueOf(1);
    private List<Pair> studentClasses = new ArrayList<Pair>();


    public TeacherStudentsView(){
            setSizeFull();

            initMainMenu();
            initContent();
    }

    private void initMainMenu(){
        menu = new MenuComponent(2);
        this.addComponent(menu);
    }

    private void initContent(){
        content = new HorizontalLayout();
        content.setMargin(true);
        content.setSizeFull();

        initLeftContent();
        initRightContent();

        addComponent(content);
        setExpandRatio(content, 1.0f);
    }

    private void initLeftContent(){
        leftContent = new HorizontalLayout();
        leftContent.setSpacing(true);

        initListOfClases();
        initClassesButts();

        leftContent.addComponents(listOfClasses, classesButts);
        content.addComponent(leftContent);
    }

    private void initListOfClases(){
        loadClassTree();
        listOfClasses.setImmediate(true);
        listOfClasses.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                currClassId = (Long) listOfClasses.getValue();
                loadContentTable();
                loadStudentsTable();
            }
        });
        leftContent.addComponent(listOfClasses);
    }

    private void initClassesButts(){
        classesButts = new VerticalLayout();
        Button newClass = new Button("Přidat třídu");
        newClass.addStyleName(ValoTheme.BUTTON_QUIET);
        newClass.setIcon(FontAwesome.PLUS_SQUARE);
        newClass.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                final Window newClassWin = new Window();
                //TABLE SHEET TO DO
                TabSheet newClassSheet = new TabSheet();
                newClassSheet.setStyleName("valo-tabsheet");
                newClassWin.setModal(true);
                newClassWin.setWindowMode(WindowMode.MAXIMIZED);
                newClassWin.setContent(newClassSheet);
                VerticalLayout newClassSheetTab1 = new VerticalLayout();
                newClassSheetTab1.setSpacing(true);
                newClassSheet.addTab(newClassSheetTab1, "Vytvorit tridu");
                final TextField className = new TextField("Název třídy");
                newClassSheetTab1.addComponent(className);
                newClassSheetTab1.setMargin(true);
                Button acc = new Button("Vytvořit");
                newClassSheetTab1.addComponent(acc);
                acc.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        if (className.getValue() != null) {
                            Classroom c = new Classroom();
                            c.setName(className.getValue());
                            classroomManager.createClassroom(c);
                            //TEST OUTPUT
                            System.out.println("ADDED CLASS: " + c.getId() + " :: NAME: " + c.getName());
                            Person p = personManager.getPersonById(MyVaadinUI.currUser.getId());
                            p.getClassrooms().add(c);
                            personManager.updatePerson(p);
                            MyVaadinUI.currUser = p;
                            loadClassTree();
                            newClassWin.close();
                        }

                    }
                });

                VerticalLayout newClassSheetTab2 = new VerticalLayout();
                newClassSheet.addTab(newClassSheetTab2, "Pridat existujici");
                final Tree classes = new Tree();
                newClassSheetTab2.addComponent(classes);
                Collection<Classroom> classrooms1 = classroomManager.getClassroomsFromSchool
                        (schoolManager.getSchoolByTeacher(MyVaadinUI.currUser));
                classrooms1.removeAll(MyVaadinUI.currUser.getClassrooms());
                for (Classroom classroom : classrooms1){
                    classes.addItem(classroom.getId());
                    classes.setItemCaption(classroom.getId(), classroom.getName());
                    classes.setChildrenAllowed(classroom.getId(), false);
                }

                newClassSheetTab1.setMargin(true);
                newClassSheetTab2.setMargin(true);

                Button acc2 = new Button("Vložit");
                if (classrooms1.size() == 0){
                    acc2.setEnabled(false);
                }
                newClassSheetTab2.addComponent(acc2);
                acc2.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        Person p = MyVaadinUI.currUser;
                        //((Teacher)p).getClassrooms().add(classroomManager.getClassroomById((Long) classes.getValue()));
                        personManager.updatePerson(p);
                        MyVaadinUI.currUser = p;
                        loadClassTree();
                        newClassWin.close();
                    }
                });

                UI.getCurrent().addWindow(newClassWin);
            }
        });
        classesButts.addComponent(newClass);

        Button removeClass = new Button("Odebrat třídu");
        removeClass.addStyleName(ValoTheme.BUTTON_QUIET);
        removeClass.setIcon(FontAwesome.MINUS_SQUARE);
        removeClass.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (listOfClasses.getValue() != null) {
                    MyVaadinUI.currUser.getClassrooms().remove(classroomManager.getClassroomById((Long) listOfClasses.getValue()));
                    personManager.updatePerson(MyVaadinUI.currUser);
                    MyVaadinUI.currUser = personManager.getPersonById(MyVaadinUI.currUser.getId());
                    loadClassTree();
                }
            }
        });
        classesButts.addComponent(removeClass);
    }

    private void initRightContent(){
        rightContent = new VerticalLayout();

        initInfoButts();
        initClassInfo();

        rightContent.addComponents(infoButts, classInfo);
        content.addComponent(rightContent);
    }

    private void initInfoButts(){
        infoButts = new HorizontalLayout();

        initInfoButtsMat();
        initInfoButtsStu();

        infoButts.addComponents(infoButtsMat, infotButtsStu);
    }

    private void initInfoButtsMat(){
        infoButtsMat = new HorizontalLayout();
        infoButtsMat.setVisible(false);
        final Button addMatBut = new Button("Přidat");
        addMatBut.addStyleName(ValoTheme.BUTTON_QUIET);
        addMatBut.setIcon(FontAwesome.PLUS_SQUARE);
        infoButtsMat.addComponent(addMatBut);
        addMatBut.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (currClassId != null) {
                    final Window addMatWin = new Window("Sdílet materiály");
                    UI.getCurrent().addWindow(addMatWin);
                    addMatWin.setModal(true);
                    addMatWin.setWindowMode(WindowMode.MAXIMIZED);
                    addMatWin.center();
                    HorizontalLayout addMatLay = new HorizontalLayout();
                    addMatWin.setContent(addMatLay);
                    addMatLay.setMargin(true);
                    addMatLay.setSpacing(true);
                    final Tree newPresTree = new Tree("Moje prezentace");
                    //LIST OF MY CONTENT
                    addMatLay.addComponent(newPresTree);
                    Person p = MyVaadinUI.currUser;
                    for (Presentation pres : p.getPresentations()) {
                        newPresTree.addItem(pres.getId());
                        newPresTree.setItemCaption(pres.getId(), pres.getName());
                    }
                    Button acc = new Button("Přidat prezentaci");
                    addMatLay.addComponent(acc);
                    acc.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                    acc.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            if (newPresTree.getValue() != null) {
                                CHManager.addPresentationInClassroom((Long) newPresTree.getValue(), currClassId, MyVaadinUI.currUser.getId());
                                loadContentTable();
                                addMatWin.close();
                            }
                        }
                    });
                    final Tree newTestTree = new Tree("Moje testy");
                    addMatLay.addComponent(newTestTree);
                    for (Test test : p.getTests()){
                        newTestTree.addItem(test.getId());
                        newTestTree.setItemCaption(test.getId(), test.getName());
                    }
                    Button acc2 = new Button("Přidat test");
                    addMatLay.addComponent(acc2);
                    acc2.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            if (newTestTree.getValue() != null) {
                                CHManager.addTestInClassroom((Long) newTestTree.getValue(), currClassId, MyVaadinUI.currUser.getId());
                                Item it = classContent.addItem(newTestTree.getValue());
                                it.getItemProperty("Název").setValue(testManager.getTestById((Long) newTestTree.getValue()).getName());
                                it.getItemProperty("Typ").setValue("test");
                                addMatWin.close();
                            }
                        }
                    });
                }
            }
        });
        Button delete = new Button("Odebrat");
        delete.addStyleName(ValoTheme.BUTTON_QUIET);
        delete.setIcon(FontAwesome.MINUS_SQUARE);
        infoButtsMat.addComponent(delete);
        delete.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (classContent.getValue() != null){
                    String type = (String) classContent.getItem(classContent.getValue()).getItemProperty("Typ").getValue();
                    System.out.println("CONTENT TYPE: " + type);
                    if (type.equals("prezentace")){
                        CHManager.deletePresentationFromClassroom((Long) classContent.getValue(), currClassId);
                    }else{
                        CHManager.deleteTestFromClassroom((Long) classContent.getValue(), currClassId);
                    }
                    loadContentTable();
                }
            }
        });
    }

    private void initInfoButtsStu(){
        infotButtsStu = new HorizontalLayout();
        Button addStudentButt = new Button("Přidat");
        addStudentButt.addStyleName(ValoTheme.BUTTON_QUIET);
        addStudentButt.setIcon(FontAwesome.PLUS_SQUARE);
        infotButtsStu.addComponent(addStudentButt);

        addStudentButt.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (listOfClasses.getValue() != null) {
                    final Window addStudent = new Window();
                    addStudent.setModal(true);
                    addStudent.setWindowMode(WindowMode.MAXIMIZED);
                    addStudent.center();

                    Button acc = new Button("Přidat");
                    acc.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            if (studentsToAdd.getValue() != null) {
                                Long l = currClassId;
                                Person student = personManager.getPersonById((Long) studentsToAdd.getValue());
                                Long old = Long.valueOf(50);
                                for (Pair pair : studentClasses){
                                    if(pair.getFirst() == studentsToAdd.getValue()){ old = pair.getSecond(); }
                                }
                                Classroom oldclass = classroomManager.getClassroomById(old);
                                oldclass.getStudents().remove(student);
                                classroomManager.updateClassroom(oldclass);

                                Classroom newClass = classroomManager.getClassroomById(currClassId);
                                newClass.getStudents().add(student);
                                classroomManager.updateClassroom(newClass);

                                student.setMyClass(newClass);
                                personManager.updatePerson(student);

                                studentClasses.clear();
                                loadClassTree();
                                loadStudentsTable();
                                addStudent.close();
                                listOfClasses.select(l);
                            }
                        }
                    });
                    VerticalLayout root = new VerticalLayout();
                    root.setMargin(true);
                    root.setSpacing(true);
                    root.addComponent(buildFilterTable());
                    root.addComponent(acc);
                    addStudent.setContent(root);
                    UI.getCurrent().addWindow(addStudent);
                }
            }
        });
    }

    private void initClassInfo(){
        classInfo.setStyleName("valo-tabsheet");

        classInfo.addTab(students, "Seznam žáků");
        students.addContainerProperty("Jméno", String.class, null);
        students.addContainerProperty("Score", Long.class, null);
        students.setSelectable(true);
        students.setSizeFull();

        classInfo.addTab(classContent, "Zveřejněný obsah");
        classContent.addContainerProperty("Název", String.class, null);
        classContent.addContainerProperty("Typ", String.class, null);
        classContent.setSelectable(true);
        classContent.setSizeFull();

        classInfo.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent selectedTabChangeEvent) {
                if (classInfo.getSelectedTab() == students){
                    infotButtsStu.setVisible(true);
                    infoButtsMat.setVisible(false);
                }else{
                    infotButtsStu.setVisible(false);
                    infoButtsMat.setVisible(true);
                }
            }
        });

        loadContentTable();
        loadStudentsTable();
    }

    private void loadClassTree(){
        listOfClasses.removeAllItems();
        String name = "";
        try {
            for (Classroom c : MyVaadinUI.currUser.getClassrooms()){
                    listOfClasses.addItem(c.getId());
                    listOfClasses.setItemCaption(c.getId(), c.getName());
                    listOfClasses.setChildrenAllowed(c.getId(), false);
            }
        }catch(Exception ex){
            Notification not = new Notification(name);
            not.show(Page.getCurrent());
        }
    }

    private void loadContentTable(){
        classContent.removeAllItems();
        if (listOfClasses.getValue() != null) {
            //LIST OF SHARED CONTENT - PRESENTATIONS
            Collection<Long> press = CHManager.getPresentationsFromClassroom(currClassId);
            for (Long l : press){
                Item it = classContent.addItem(l);
                it.getItemProperty("Název").setValue(presentationManager.getPresentationById(l).getName());
                it.getItemProperty("Typ").setValue("prezentace");
            }
            //LIST OF SHARED CONTENT - TESTS
            Collection<Long> tests = CHManager.getTestsFromClassroom(currClassId);
            for (Long l : tests){
                Item it = classContent.addItem(l);
                it.getItemProperty("Název").setValue(testManager.getTestById(l).getName());
                it.getItemProperty("Typ").setValue("test");
            }
        }
        classContent.setPageLength(classContent.size());
        classContent.setColumnCollapsingAllowed(true);
    }

    private void loadStudentsTable(){
        students.removeAllItems();
        if (listOfClasses.getValue() != null){
            //LIST OF STUDENTS
            Classroom classroom = classroomManager.getClassroomById((Long) listOfClasses.getValue());
            for (Person student : classroom.getStudents()) {
                Item it = students.addItem(student.getId());
                it.getItemProperty("Jméno").setValue(student.getName());
                it.getItemProperty("Score").setValue(student.getScore());
            }
        }
        students.setPageLength(students.size());
        students.setColumnCollapsingAllowed(true);
        students.setSortContainerPropertyId("Jméno");
        students.sort();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }

    private FilterTable buildFilterTable() {
        studentsToAdd.setFilterDecorator(new MyFilterDecorator());

        studentsToAdd.setFilterBarVisible(true);

        studentsToAdd.setSelectable(true);
        studentsToAdd.setImmediate(true);
        //studentsToAdd.setMultiSelect(true);

        studentsToAdd.setColumnCollapsingAllowed(true);
        studentsToAdd.setColumnReorderingAllowed(true);
        studentsToAdd.setContainerDataSource(buildContainer());
        studentsToAdd.setVisibleColumns((Object[]) new String[] { "Jméno", "Třída" });
        studentsToAdd.setItemDescriptionGenerator(new AbstractSelect.ItemDescriptionGenerator() {

            @Override
            public String generateDescription(Component source, Object itemId,
                                              Object propertyId) {
                return "Just testing ItemDescriptionGenerator";
            }
        });

        studentsToAdd.setPageLength(studentsToAdd.getContainerDataSource().size());

        return studentsToAdd;
    }

    private Container buildContainer() {

        IndexedContainer cont = new IndexedContainer();

        cont.addContainerProperty("Jméno", String.class, null);
        cont.addContainerProperty("Třída", String.class, null);

        School school = schoolManager.getSchoolByTeacher(MyVaadinUI.currUser);
        final Collection<Classroom> classrooms = classroomManager.getClassroomsFromSchool(school);
        for (Classroom classroom : classrooms) {
            for (Person student : classroom.getStudents()) {
                cont.addItem(student.getId());
                cont.getContainerProperty(student.getId(), "Jméno").setValue(student.getName());
                cont.getContainerProperty(student.getId(), "Třída").setValue(classroom.getName());
                studentClasses.add(new Pair(student.getId(), classroom.getId()));
            }
        }
        for (Person student : classroomManager.getClassroomById(currClassId).getStudents()) {
            cont.removeItem(student.getId());
        }
        return cont;
    }
}