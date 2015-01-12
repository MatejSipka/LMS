package cz.muni.fi.xryvola.components;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.services.PDFGenerator;
import cz.muni.fi.xryvola.services.Test;

import java.io.File;
import java.util.Collections;

/**
 * Created by adam on 1.12.14.
 */
public class TestGenerator extends Window {

    public TestGenerator(final Test test){
        VerticalLayout root = new VerticalLayout();
        root.setMargin(true);
        root.setSpacing(true);

        this.setContent(root);
        this.center();
        this.setModal(true);
        HorizontalLayout numQuestLay = new HorizontalLayout();

        final TextField numQuest = new TextField("Počet otázek");
        //final CheckBox allQuest = new CheckBox("všechy otázky");

        numQuest.setStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        numQuest.setIcon(FontAwesome.QUESTION);
        numQuest.setInputPrompt(String.valueOf(test.getQuestions().size()));

        numQuest.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent textChangeEvent) {

            }
        });

        numQuestLay.setSpacing(true);
        numQuestLay.addComponent(numQuest);

        final TextField numTests = new TextField("Počet testů");
        numTests.setStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        numTests.setIcon(FontAwesome.FILES_O);
        numTests.focus();
        root.addComponent(numTests);
        root.addComponent(numQuestLay);

        Button create = new Button("Vytvořit");
        create.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        root.addComponent(create);
        root.setComponentAlignment(create, Alignment.BOTTOM_CENTER);
        create.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        create.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    int questNum = Integer.parseInt(numQuest.getValue());
                    int testNum = Integer.parseInt(numTests.getValue());
                    if (questNum > test.getQuestions().size()){
                        numQuest.setValue(String.valueOf(test.getQuestions().size()));
                        questNum = test.getQuestions().size();
                        Notification notif = new Notification("Test obsahuje mene otazek");
                        notif.setPosition(Position.BOTTOM_RIGHT);
                        notif.setDelayMsec(1000);
                        notif.show(Page.getCurrent());
                    }
                    PDFGenerator pdfGenerator = new PDFGenerator();
                    Collections.shuffle(test.getQuestions());
                    int size = test.getQuestions().size();
                    for (int i = 0; i + questNum != size; i++){
                        test.getQuestions().remove(0);
                    }
                    pdfGenerator.generateTests(test, testNum);
                    FileResource resource = new FileResource(new File(MyVaadinUI.MYFILEPATH + test.getId() + ".zip"));
                    Page.getCurrent().open(resource, null, false);
                    close();
                    System.out.println("ZIP FILE WAS GENERATED");
                } catch (NumberFormatException ex){
                    Notification notification = new Notification("Zadejte číslo");
                    notification.setPosition(Position.BOTTOM_RIGHT);
                    notification.show(Page.getCurrent());
                }
            }
        });
    }
}
