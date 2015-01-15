package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.components.MenuComponent;
import cz.muni.fi.xryvola.components.SlideShowWIndow;
import cz.muni.fi.xryvola.components.TestGenerator;
import cz.muni.fi.xryvola.services.*;
import org.vaadin.cssinject.CSSInject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 30.11.14.
 */
public class TeacherMaterialsView extends HorizontalLayout implements View {

    private SuperManager superManager;

    private VerticalLayout presLay;
    private VerticalLayout testLay;
    private VerticalLayout presButt;
    private VerticalLayout presButt2;
    private VerticalLayout testButt;
    private VerticalLayout testButt2;
    private MenuComponent menu;
    private HorizontalLayout content;

    private Tree listOfPresentations = new Tree("Moje prezentace:");
    private Tree listOfTests = new Tree("Moje testy:");

    public TeacherMaterialsView(){

        this.superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();

        setSizeFull();

        content = new HorizontalLayout();
        presLay = new VerticalLayout();
        testLay = new VerticalLayout();
        presButt = new VerticalLayout();
        testButt = new VerticalLayout();

        initMainMenu();

        content.setMargin(true);
        content.setSizeFull();
        addComponent(content);
        setExpandRatio(content, 1.0f);

        initPresLay();
        presButt2 = initPresLayButtons();
        presButt2.setVisible(false);
        presButt.addComponent(presButt2);

        initTestLay();
        testButt2 = initTestLayButt();
        testButt2.setVisible(false);
        testButt.addComponent(testButt2);

        content.addComponent(presLay);
        content.addComponent(presButt);
        content.addComponent(testLay);
        content.addComponent(testButt);

        testLay.addStyleName("butt-border");
        CSSInject css = new CSSInject(UI.getCurrent());
        css.setStyles(".butt-border{border-left: 1px solid #e0e0e0; padding-left: 20px;}");

    }

    private void initMainMenu() {
        menu = new MenuComponent(1);
        this.addComponent(menu);
    }


    private void initPresLay(){
        loadPresTree();
        presLay.addComponent(listOfPresentations);

        listOfPresentations.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (listOfPresentations.getValue() != null){
                    presButt2.setVisible(true);
                }else{
                    presButt2.setVisible(false);
                }
            }
        });
    }

    private VerticalLayout initPresLayButtons(){

        final Button presentation = new Button("Nová prezentace");
        presentation.setStyleName(ValoTheme.BUTTON_QUIET);
        presentation.setIcon(FontAwesome.PLUS_SQUARE);
        presButt.addComponent(presentation);

        presentation.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                final Window preName = new Window();
                preName.setModal(true);
                VerticalLayout preNameLay = new VerticalLayout();
                preNameLay.setSpacing(true);
                preNameLay.setMargin(true);
                final TextField preNameField = new TextField("Název prezentace:");
                preNameField.focus();
                Button acc = new Button("OK");
                preNameLay.addComponent(preNameField);
                preNameLay.addComponent(acc);
                preName.center();
                preName.setContent(preNameLay);
                UI.getCurrent().addWindow(preName);
                acc.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                acc.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        if (preNameField.getValue() != "") {
                            Slide title = new Slide();
                            title.setHtmlContent("První slide v prezentaci");
                            title.setName(preNameField.getValue());
                            Presentation newPres = new Presentation();
                            newPres.setName(preNameField.getValue());
                            newPres.getSlides().add(title);

                            //UPDATE
                            Person p = superManager.getPersonManager().getPersonById(MyVaadinUI.currUser.getId());
                            superManager.getPresentationManager().createPresentation(newPres);
                            p.getPresentations().add(newPres);
                            superManager.getPersonManager().updatePerson(p);
                            MyVaadinUI.currUser = p;

                            //ADD PRES VIEW
                            UI.getCurrent().getNavigator().addView(MyVaadinUI.PRESENTATIONVIEW, new PresentationView(newPres));
                            UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.PRESENTATIONVIEW);
                            preName.close();
                        }
                    }
                });
            }
        });

        VerticalLayout presButtons = new VerticalLayout();

        final Button edit = new Button("Upravit");
        final Button delete = new Button("Smazat");
        final Button rename = new Button("Přejmenovat");
        final Button present = new Button("Prezentovat");
        final Button downloadPDF = new Button("Generovat PDF");

        edit.setIcon(FontAwesome.EDIT);
        delete.setIcon(FontAwesome.MINUS_SQUARE);
        rename.setIcon(FontAwesome.FILE_TEXT_O);
        present.setIcon(FontAwesome.DESKTOP);
        downloadPDF.setIcon(FontAwesome.FILE_PDF_O);

        edit.addStyleName(ValoTheme.BUTTON_QUIET);
        delete.addStyleName(ValoTheme.BUTTON_QUIET);
        rename.addStyleName(ValoTheme.BUTTON_QUIET);
        present.addStyleName(ValoTheme.BUTTON_QUIET);
        downloadPDF.addStyleName(ValoTheme.BUTTON_QUIET);

        presButtons.addComponent(edit);
        presButtons.addComponent(delete);
        presButtons.addComponent(rename);
        presButtons.addComponent(present);
        presButtons.addComponent(downloadPDF);

        edit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Presentation presToEdit = superManager.getPresentationManager().getPresentationById((Long) listOfPresentations.getValue());
                UI.getCurrent().getNavigator().addView(MyVaadinUI.PRESENTATIONVIEW, new PresentationView(presToEdit));
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.PRESENTATIONVIEW);
            }
        });

        delete.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Person p = MyVaadinUI.currUser;
                p.getPresentations().remove(superManager.getPresentationManager().getPresentationById((Long) listOfPresentations.getValue()));
                superManager.getPersonManager().updatePerson(p);
                superManager.getPresentationManager().deletePresentation((Long) listOfPresentations.getValue());
                loadPresTree();
            }
        });

        rename.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        rename.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                final Presentation p = superManager.getPresentationManager().getPresentationById((Long) listOfPresentations.getValue());
                final Presentation p2 = getPresentation(listOfPresentations.getValue());
                final Window renameWin = new Window();
                VerticalLayout renameLay = new VerticalLayout();
                renameLay.setMargin(true);
                renameLay.setSpacing(true);
                final TextField renameField = new TextField("Nové jméno prezentace:");
                renameField.focus();
                Button acc = new Button("Přejmenovat");
                renameLay.addComponent(renameField);
                renameLay.addComponent(acc);
                renameWin.setContent(renameLay);
                renameWin.center();
                UI.getCurrent().addWindow(renameWin);
                acc.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                acc.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        if (renameField.getValue() != "") {
                            p.setName(renameField.getValue());
                            p2.setName(renameField.getValue());
                            superManager.getPresentationManager().updatePresentation(p);
                            loadPresTree();
                            renameWin.close();
                        }
                    }
                });
            }
        });

        present.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Window slideShow = new SlideShowWIndow(superManager.getPresentationManager().getPresentationById((Long) listOfPresentations.getValue()));
            }
        });

        downloadPDF.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                PDFGenerator generator = new PDFGenerator();
                generator.generatePresentation(superManager.getPresentationManager().getPresentationById((Long) listOfPresentations.getValue()));
                FileResource resource = new FileResource(new File(MyVaadinUI.MYFILEPATH + listOfPresentations.getValue() + ".pdf"));
                Page.getCurrent().open(resource, null, false);
            }
        });
        return presButtons;
    }

    private void initTestLay(){
        loadTestTree();
        testLay.addComponent(listOfTests);

        listOfTests.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (listOfTests.getValue() != null){
                    testButt2.setVisible(true);
                }else{
                    testButt2.setVisible(false);
                }
            }
        });
    }

    private VerticalLayout initTestLayButt(){

        final Button newTest = new Button("Nový test");
        newTest.setStyleName(ValoTheme.BUTTON_QUIET);
        newTest.setIcon(FontAwesome.PLUS_SQUARE);
        testButt.addComponent(newTest);
        newTest.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                final Window newTestWin = new Window();
                newTestWin.setModal(true);
                newTestWin.center();
                VerticalLayout newTestLay = new VerticalLayout();
                newTestLay.setSpacing(true);
                newTestLay.setMargin(true);
                newTestWin.setContent(newTestLay);
                final TextField testName = new TextField("Jméno testu");
                testName.focus();
                newTestLay.addComponent(testName);
                UI.getCurrent().addWindow(newTestWin);
                Button acc = new Button("Vytvořit");
                newTestLay.addComponent(acc);
                acc.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                acc.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        if (testName.getValue() != "") {
                            Test newTest = new Test();
                            Question stQ = new Question();
                            Answer stA = new Answer();
                            Answer ndA = new Answer();

                            stA.setAnswer("Ano");
                            stA.setIsCorrect(true);
                            ndA.setAnswer("Ne");
                            ndA.setIsCorrect(false);
                            stQ.getAnswers().add(stA);
                            stQ.getAnswers().add(ndA);
                            stQ.setQuestion("Moje první otázka?");

                            newTest.getQuestions().add(stQ);
                            newTest.setName(testName.getValue());

                            //UPDATE
                            Person p = superManager.getPersonManager().getPersonById(MyVaadinUI.currUser.getId());
                            superManager.getTestManager().createTest(newTest);
                            p.getTests().add(newTest);
                            superManager.getPersonManager().updatePerson(p);
                            MyVaadinUI.currUser = p;

                            /*NAVIGATE TO TEST VIEW*/
                            UI.getCurrent().getNavigator().addView(MyVaadinUI.TESTVIEW, new TestView(newTest));
                            UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.TESTVIEW);

                            newTestWin.close();
                        }
                    }
                });
            }
        });

        VerticalLayout testButts = new VerticalLayout();

        final Button edit = new Button("Upravit");
        final Button delete = new Button("Smazat");
        final Button rename = new Button("Přejmenovat");
        final Button testGenerator = new Button("Generátor testů");
        final CheckBox withResult = new CheckBox();
        final Button downloadPDF = new Button("Generovat PDF");

        edit.setIcon(FontAwesome.EDIT);
        delete.setIcon(FontAwesome.MINUS_SQUARE);
        rename.setIcon(FontAwesome.FILE_TEXT_O);
        testGenerator.setIcon(FontAwesome.FILES_O);
        downloadPDF.setIcon(FontAwesome.FILE_PDF_O);

        edit.addStyleName(ValoTheme.BUTTON_QUIET);
        delete.addStyleName(ValoTheme.BUTTON_QUIET);
        rename.addStyleName(ValoTheme.BUTTON_QUIET);
        testGenerator.addStyleName(ValoTheme.BUTTON_QUIET);
        withResult.addStyleName("result-check");
        downloadPDF.addStyleName(ValoTheme.BUTTON_QUIET);

        CSSInject buttWidthCss = new CSSInject(UI.getCurrent());
        buttWidthCss.setStyles(".result-check{margin-left: 15px; font-weight: 400;}");

        testButts.addComponent(edit);
        testButts.addComponent(delete);
        testButts.addComponent(rename);
        testButts.addComponent(testGenerator);
        testButts.addComponent(downloadPDF);
        testButts.addComponent(withResult);

        edit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Test testToEdit = superManager.getTestManager().getTestById((Long) listOfTests.getValue());
                UI.getCurrent().getNavigator().addView(MyVaadinUI.TESTVIEW, new TestView(testToEdit));
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.TESTVIEW);
            }
        });

        delete.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Person p = MyVaadinUI.currUser;
                p.getTests().remove(superManager.getTestManager().getTestById((Long) listOfTests.getValue()));
                superManager.getPersonManager().updatePerson(p);
                superManager.getTestManager().deleteTest((Long) listOfTests.getValue());
                loadTestTree();
            }
        });

        rename.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                final Test t = superManager.getTestManager().getTestById((Long) listOfTests.getValue());
                final Test t2 = getTest(listOfTests.getValue());
                final Window renameWin = new Window();
                VerticalLayout renameLay = new VerticalLayout();
                renameLay.setMargin(true);
                renameLay.setSpacing(true);
                final TextField renameField = new TextField("Nové jméno testu:");
                renameField.focus();
                Button acc = new Button("Přejmenovat");
                renameLay.addComponent(renameField);
                renameLay.addComponent(acc);
                renameWin.setContent(renameLay);
                renameWin.center();
                UI.getCurrent().addWindow(renameWin);
                acc.setClickShortcut(ShortcutAction.KeyCode.ENTER);
                acc.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        if (renameField.getValue() != "") {
                            t.setName(renameField.getValue());
                            t2.setName(renameField.getValue());
                            superManager.getTestManager().updateTest(t);
                            loadTestTree();
                            renameWin.close();
                        }
                    }
                });
            }
        });

        testGenerator.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Test tmp = superManager.getTestManager().getTestById((Long) listOfTests.getValue());
                List<Question> tmpQ = new ArrayList<Question>();
                tmpQ.addAll(tmp.getQuestions());
                tmp.setQuestions(tmpQ);
                UI.getCurrent().addWindow(new TestGenerator(tmp));
                loadTestTree();
            }
        });

        withResult.setCaption("s řešením");

        downloadPDF.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                MyVaadinUI.currUser.setScore(MyVaadinUI.currUser.getScore()+5);
                superManager.getPersonManager().updatePerson(MyVaadinUI.currUser);
                PDFGenerator generator = new PDFGenerator();
                Test tmp = superManager.getTestManager().getTestById((Long) listOfTests.getValue());
                generator.generateTest(tmp, withResult.getValue(), String.valueOf(tmp.getId()));
                FileResource resource = new FileResource(new File(MyVaadinUI.MYFILEPATH + listOfTests.getValue() + ".pdf"));
                Page.getCurrent().open(resource, null, false);
            }
        });
        return testButts;

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        loadPresTree();
        loadTestTree();
    }

    private Presentation getPresentation(Object id){
        for (Presentation presentation : MyVaadinUI.currUser.getPresentations()){
            if (presentation.getId() == id){
                return presentation;
            }
        }
        return null;
    }

    private Test getTest(Object id){
        for (Test test : MyVaadinUI.currUser.getTests()){
            if (test.getId() == id){
                return test;
            }
        }
        return null;
    }

    private void loadPresTree(){
        listOfPresentations.removeAllItems();
        listOfPresentations.setImmediate(true);

        List<Presentation> myPresentations = MyVaadinUI.currUser.getPresentations();

        for (Presentation pres : myPresentations){
            listOfPresentations.addItem(pres.getId());
            listOfPresentations.setItemCaption(pres.getId(), pres.getName());
            listOfPresentations.setChildrenAllowed(pres.getId(), false);
        }
    }

    private void loadTestTree(){
        listOfTests.removeAllItems();
        listOfTests.setImmediate(true);

        List<Test> myTests = MyVaadinUI.currUser.getTests();

        for (Test test : myTests){
            listOfTests.addItem(test.getId());
            listOfTests.setItemCaption(test.getId(), test.getName());
            listOfTests.setChildrenAllowed(test.getId(), false);
        }
    }
}
