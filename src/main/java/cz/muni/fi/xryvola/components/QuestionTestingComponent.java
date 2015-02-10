package cz.muni.fi.xryvola.components;

import com.sun.javafx.collections.UnmodifiableListSet;
import com.vaadin.data.Item;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.services.*;
import org.vaadin.cssinject.CSSInject;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by adam on 12.12.14.
 */
public class QuestionTestingComponent extends CustomComponent {

    private SuperManager superManager;

    private Question question;
    private OptionGroup answers;
    private Boolean multiselect;

    public QuestionTestingComponent(Question question){

        this.superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();
        this.question = question;

        int multi = 0;
        for (Answer a : question.getAnswers()){
            if (a.getIsCorrect()) multi++;
        }
        if (multi >1){
            this.multiselect = true;
        }else{
            this.multiselect = false;
        }

        VerticalLayout root = new VerticalLayout();
        setCompositionRoot(root);

        Label quest = new Label(question.getQuestion());
        quest.setStyleName("question-test");
        CSSInject css = new CSSInject(UI.getCurrent());
        css.setStyles(".question-test{font-family: 'Play', cursive; font-size: 20px;} .answer-test{ font-family: 'Play'; font-color: #000; }");

        answers = new OptionGroup();
        answers.setMultiSelect(multiselect);
        answers.setStyleName("answer-test");
        for (Answer answer : question.getAnswers()){
            answers.addItem(answer.getId());
            answers.setItemCaption(answer.getId(), answer.getAnswer());
        }
        root.addComponents(quest, answers);
    }

    public int getIsCorrect(){
        if (answers.getValue() != null) {
            if (multiselect) {
                int score = 0;
                Set<Long> ans = (Set<Long>) answers.getValue();
                for (Long l : ans) {
                    Answer a = superManager.getAnswerManager().getAnswerById(l);
                    if (a.getIsCorrect()) {
                        score++;
                    } else {
                        score--;
                    }
                }
                if (score < 0) {
                    return 0;
                } else {
                    return score;
                }
            } else {
                Answer a = superManager.getAnswerManager().getAnswerById((Long) answers.getValue());
                if (a.getIsCorrect()) return 1;
            }
        }
        return 0;
    }

    public void check(){
        CSSInject css = new CSSInject(UI.getCurrent());
        css.setStyles(".answer-true{ background-color: #00A20B; margin: 0px; padding: 0px; } .answer-false{ background-color: #f00; margin: 0px; padding: 0px; }");

        Object marked = answers.getValue();
        answers.setHtmlContentAllowed(true);
        answers.removeAllItems();
        for (Answer answer : question.getAnswers()){
            answers.addItem(answer.getId());
            if (answer.getIsCorrect()) {
                answers.setItemCaption(answer.getId(), "<p class=answer-true>" + answer.getAnswer() + "</p>");
            }else{
                answers.setItemCaption(answer.getId(), answer.getAnswer());
            }
        }

        if (multiselect){
            Set<Long> marketSet = (Set<Long>)marked;
            for (Long l : marketSet){
                Answer a = getAnswer(l);
                if (!a.getIsCorrect()){
                    answers.setItemCaption(a.getId(), "<p class=answer-false>" + a.getAnswer() + "</p>");
                }
            }
        }else{
            Answer a = getAnswer((Long) marked);
            if (!a.getIsCorrect()){
                answers.setItemCaption(marked, "<p class=answer-false>" + a.getAnswer() + "</p>");
            }
        }
        answers.setValue(marked);
        answers.setReadOnly(true);
    }

    private Answer getAnswer(long id){
        for (Answer a : question.getAnswers()){
            if (a.getId() == id) return a;
        }
        return null;
    }
}
