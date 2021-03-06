package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.converter.StringToDateConverter;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.Pair;
import cz.muni.fi.xryvola.components.MenuComponent;
import cz.muni.fi.xryvola.filteredTable.MyFilterDecorator;
import cz.muni.fi.xryvola.services.*;
import org.tepi.filtertable.FilterTable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by adam on 3.12.14.
 */
public class TeacherStudentsView extends HorizontalLayout implements View {

    private MenuComponent menu;
    private HorizontalLayout content;

    private HorizontalLayout leftContent;
    private VerticalLayout rightContent;

    //left side
    private Tree listOfClasses = new Tree("Moje třídy");
    private VerticalLayout classesButts;

    //right side - top
    private HorizontalLayout infoButts;
    private HorizontalLayout infoButtsMat;
    private HorizontalLayout infotButtsStu;
    //right side - down
    private TabSheet classInfo = new TabSheet();
    private Table students = new Table();
    private FilterTable studentsToAdd = new FilterTable();
    private Table classContent = new Table();
    private Button editContentSharing = new Button("Upravit");

    private SuperManager superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();

    private ClassroomManager classroomManager = superManager.getClassroomManager();
    private PersonManager personManager = superManager.getPersonManager();
    private SchoolManager schoolManager = superManager.getSchoolManager();
    private ContentSharingManager CHManager = superManager.getContentSharingManager();
    private PresentationManager presentationManager = superManager.getPresentationManager();
    private TestManager testManager = superManager.getTestManager();
    private ContentSharingManager contentSharingManager = superManager.getContentSharingManager();

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
                            Person p = personManager.getPersonById(((MyVaadinUI)UI.getCurrent()).getCurrentUser().getId());
                            p.getClassrooms().add(c);
                            personManager.updatePerson(p);
                            ((MyVaadinUI)UI.getCurrent()).setCurrentUser(p);
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
                        (schoolManager.getSchoolByTeacher(((MyVaadinUI)UI.getCurrent()).getCurrentUser()));
                classrooms1.removeAll(((MyVaadinUI)UI.getCurrent()).getCurrentUser().getClassrooms());
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
                        Person p = ((MyVaadinUI)UI.getCurrent()).getCurrentUser();
                        //((Teacher)p).getClassrooms().add(classroomManager.getClassroomById((Long) classes.getValue()));
                        personManager.updatePerson(p);
                        ((MyVaadinUI)UI.getCurrent()).setCurrentUser(p);
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
                    ((MyVaadinUI)UI.getCurrent()).getCurrentUser().getClassrooms().remove(classroomManager.getClassroomById((Long) listOfClasses.getValue()));
                    personManager.updatePerson(((MyVaadinUI)UI.getCurrent()).getCurrentUser());
                    //MyVaadinUI.currUser = personManager.getPersonById(MyVaadinUI.currUser.getId());
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
                    Person p = ((MyVaadinUI)UI.getCurrent()).getCurrentUser();
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

                                final Long presId = (Long) newPresTree.getValue();
                                addMatWin.close();
                                final Window timeWin = new Window();
                                HorizontalLayout timeWinLay = new HorizontalLayout();
                                timeWin.center();
                                timeWin.setModal(true);
                                timeWin.setContent(timeWinLay);
                                timeWinLay.setMargin(true);
                                timeWinLay.setSpacing(true);

                                final DateField when = new DateField();
                                when.setValue(new Date());
                                when.setResolution(Resolution.MINUTE);
                                when.setCaption("Zveřejněno od:");

                                final DateField till = new DateField();
                                till.setValue(new Date());
                                till.setResolution(Resolution.MINUTE);
                                till.setCaption("Zveřejněno do:");

                                timeWinLay.addComponents(when, till);

                                Button acc = new Button("Přidat");
                                timeWinLay.addComponent(acc);
                                timeWinLay.setComponentAlignment(acc, Alignment.BOTTOM_RIGHT);
                                acc.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                                acc.addClickListener(new Button.ClickListener() {
                                    @Override
                                    public void buttonClick(Button.ClickEvent clickEvent) {
                                        ContentSharing ch = new ContentSharing();
                                        ch.setDocumentType("PRESENTATION");
                                        ch.setTeacherId(((MyVaadinUI)UI.getCurrent()).getCurrentUser().getId());
                                        ch.setDocumentId(presId);
                                        ch.setClassroomId(currClassId);
                                        ch.setWhen(when.getValue());
                                        ch.getWhen().setSeconds(0);
                                        ch.setTill(till.getValue());
                                        ch.getTill().setSeconds(0);
                                        superManager.getContentSharingManager().createContentSharing(ch);

                                        loadContentTable();
                                        timeWin.close();
                                        addMatWin.close();

                                    }
                                });
                                UI.getCurrent().addWindow(timeWin);
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
                                final Long testId = (Long) newTestTree.getValue();
                                addMatWin.close();
                                final Window timeWin = new Window();
                                HorizontalLayout timeWinLay = new HorizontalLayout();
                                timeWin.center();
                                timeWin.setModal(true);
                                timeWin.setContent(timeWinLay);
                                timeWinLay.setMargin(true);
                                timeWinLay.setSpacing(true);

                                final DateField when = new DateField();
                                when.setValue(new Date());
                                when.setResolution(Resolution.MINUTE);
                                when.setCaption("Zveřejněno od:");

                                final DateField till = new DateField();
                                till.setValue(new Date());
                                till.setResolution(Resolution.MINUTE);
                                till.setCaption("Zveřejněno do:");

                                timeWinLay.addComponents(when, till);

                                Button acc = new Button("Přidat");
                                timeWinLay.addComponent(acc);
                                timeWinLay.setComponentAlignment(acc, Alignment.BOTTOM_RIGHT);
                                acc.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                                acc.addClickListener(new Button.ClickListener() {
                                    @Override
                                    public void buttonClick(Button.ClickEvent clickEvent) {
                                        ContentSharing ch = new ContentSharing();
                                        ch.setDocumentType("TEST");
                                        ch.setTeacherId(((MyVaadinUI)UI.getCurrent()).getCurrentUser().getId());
                                        ch.setDocumentId(testId);
                                        ch.setClassroomId(currClassId);
                                        ch.setWhen(when.getValue());
                                        ch.getWhen().setSeconds(0);
                                        ch.setTill(till.getValue());
                                        ch.getTill().setSeconds(0);
                                        superManager.getContentSharingManager().createContentSharing(ch);
                                        loadContentTable();
                                        timeWin.close();
                                        addMatWin.close();
                                    }
                                });
                                UI.getCurrent().addWindow(timeWin);
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
                    contentSharingManager.deleteContentSharing((Long) classContent.getValue());
                    loadContentTable();
                }
            }
        });

        infoButtsMat.addComponent(editContentSharing);
        editContentSharing.setIcon(FontAwesome.EDIT);
        editContentSharing.addStyleName(ValoTheme.BUTTON_QUIET);
        editContentSharing.setVisible(false);
        editContentSharing.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                final ContentSharing cs = contentSharingManager.getContentSharingById((Long) classContent.getValue());

                final Window timeWin = new Window();
                timeWin.center();
                timeWin.setModal(true);

                HorizontalLayout timeLay = new HorizontalLayout();
                timeLay.setSpacing(true);
                timeLay.setMargin(true);

                final DateField when = new DateField("Zveřejněno od:");
                when.setResolution(Resolution.MINUTE);
                when.setValue(cs.getWhen());
                final DateField till = new DateField("Zveřejněno do:");
                till.setResolution(Resolution.MINUTE);
                till.setValue(cs.getTill());
                timeLay.addComponents(when, till);
                timeWin.setContent(timeLay);

                Button save = new Button("Uložit");
                save.setIcon(FontAwesome.SAVE);
                timeLay.addComponent(save);
                timeLay.setComponentAlignment(save, Alignment.BOTTOM_RIGHT);
                save.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        cs.setWhen(when.getValue());
                        cs.setTill(till.getValue());
                        contentSharingManager.updateContentSharing(cs);
                        loadContentTable();
                        timeWin.close();
                    }
                });

                UI.getCurrent().addWindow(timeWin);
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
                    root.addComponent(buildtudentsFilterTable());
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
        classContent.addContainerProperty("Od", Date.class, null);
        classContent.addContainerProperty("Do", Date.class, null);
        classContent.setSelectable(true);
        classContent.setSizeFull();

        StringToDateConverter tableConverter = new StringToDateConverter(){
            @Override
            public DateFormat getFormat(Locale locale){
                return new SimpleDateFormat("dd.MM.yyyy HH:mm");
            }
        };

        classContent.setConverter("Od", tableConverter);
        classContent.setConverter("Do", tableConverter);

        classInfo.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent selectedTabChangeEvent) {
                if (classInfo.getSelectedTab() == students) {
                    infotButtsStu.setVisible(true);
                    infoButtsMat.setVisible(false);
                } else {
                    infotButtsStu.setVisible(false);
                    infoButtsMat.setVisible(true);
                }
            }
        });

        classContent.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (classContent.getValue() != null){
                    editContentSharing.setVisible(true);
                }else{
                    editContentSharing.setVisible(false);
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
            for (Classroom c : ((MyVaadinUI)UI.getCurrent()).getCurrentUser().getClassrooms()){
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
            //LIST OF SHARED CONTENT
            Collection<ContentSharing> press = CHManager.getContentSharingFromClassroom(currClassId);
            for (ContentSharing cs : press){
                Item it = classContent.addItem(cs.getId());
                if (cs.getDocumentType().equals("PRESENTATION")) {
                    it.getItemProperty("Název").setValue(presentationManager.getPresentationById(cs.getDocumentId()).getName());
                    it.getItemProperty("Typ").setValue("prezentace");
                }else if (cs.getDocumentType().equals("TEST")){
                    it.getItemProperty("Název").setValue(testManager.getTestById(cs.getDocumentId()).getName());
                    it.getItemProperty("Typ").setValue("test");
                }
                it.getItemProperty("Od").setValue(cs.getWhen());
                it.getItemProperty("Do").setValue(cs.getTill());
            }
        }
        classContent.setPageLength(classContent.size());
        classContent.setColumnCollapsingAllowed(true);
    }

    private void loadStudentsTable(){
        students.removeAllItems();
        if (listOfClasses.getValue() != null){
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

    private FilterTable buildtudentsFilterTable() {
        studentsToAdd.setFilterDecorator(new MyFilterDecorator());

        studentsToAdd.setFilterBarVisible(true);

        studentsToAdd.setSelectable(true);
        studentsToAdd.setImmediate(true);
        //studentsToAdd.setMultiSelect(true);

        studentsToAdd.setColumnCollapsingAllowed(true);
        studentsToAdd.setColumnReorderingAllowed(true);
        studentsToAdd.setContainerDataSource(buildStudentsContainer());
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

    private Container buildStudentsContainer() {

        IndexedContainer cont = new IndexedContainer();

        cont.addContainerProperty("Jméno", String.class, null);
        cont.addContainerProperty("Třída", String.class, null);

        School school = schoolManager.getSchoolByTeacher(((MyVaadinUI)UI.getCurrent()).getCurrentUser());
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