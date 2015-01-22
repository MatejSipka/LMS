package cz.muni.fi.xryvola.components;

import com.sun.javafx.collections.UnmodifiableListSet;
import com.vaadin.ui.*;
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
        css.setStyles(".question-test{font-family: 'Play', cursive; font-size: 20px;} .answer-test{ font-family: 'Play'; }");

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
}
