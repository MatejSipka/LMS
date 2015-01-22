package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.components.QuestionTestingComponent;
import cz.muni.fi.xryvola.components.ResultTestWindow;
import cz.muni.fi.xryvola.services.Action;
import cz.muni.fi.xryvola.services.Question;
import cz.muni.fi.xryvola.services.SuperManager;
import cz.muni.fi.xryvola.services.Test;
import org.vaadin.cssinject.CSSInject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by adam on 12.12.14.
 */

@StyleSheet("http://fonts.googleapis.com/css?family=Russo+One&subset=latin-ext,latin")
public class StudentTestView extends VerticalLayout implements View {

    private List<QuestionTestingComponent> questions;
    private Test t;
    private SuperManager superManager;

    public StudentTestView(Test test){
        this.t = test;
        this.superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();

        this.setMargin(true);
        this.setSpacing(true);

        CSSInject css = new CSSInject(UI.getCurrent());
        css.setStyles(".test-title{ font-family: 'Russo One'; font-size: 35px; font-color: #000; border-bottom: 1px solid black; } .test-background{ background-color: #fff; }");

        this.setStyleName("test-background");

        Label testTitle = new Label("Test: " + t.getName());
        testTitle.setStyleName("test-title");
        this.addComponent(testTitle);

        questions = new ArrayList<QuestionTestingComponent>();
        for (Question question : t.getQuestions()){
            questions.add(new QuestionTestingComponent(question));
        }

        CSSInject cssInject = new CSSInject(UI.getCurrent());
        cssInject.setStyles(".test-question{ background-color: #fff; box-shadow: 0px 0px 15px #888888; padding: 12px; }");

        for(QuestionTestingComponent component : questions){
            component.setStyleName("test-question");
            this.addComponent(component);
        }

        Button close = new Button("Vyhodnotit");
        close.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Action act = new Action();
                act.setWhen(new Date());
                act.setWhat(t.getId());
                act.setWho(((MyVaadinUI)UI.getCurrent()).currUser.getId());
                act.setVerb("passed");
                int score = 0;
                for (QuestionTestingComponent com : questions){
                   score += com.getIsCorrect();
                }
                act.setResult((long) score);
                superManager.getActionManager().createAction(act);
                MyVaadinUI.currUser.setScore(MyVaadinUI.currUser.getScore() + score);
                superManager.getPersonManager().updatePerson(MyVaadinUI.currUser);
                UI.getCurrent().addWindow(new ResultTestWindow(t, score));
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.STUDENTCONTENTVIEW);
            }
        });
        close.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        this.addComponent(close);
        this.setComponentAlignment(close, Alignment.BOTTOM_CENTER);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
