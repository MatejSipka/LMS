package cz.muni.fi.xryvola.components;

import com.vaadin.ui.*;
import cz.muni.fi.xryvola.services.*;
import org.vaadin.cssinject.CSSInject;

/**
 * Created by adam on 12.12.14.
 */
public class QuestionTestingComponent extends CustomComponent {

    public QuestionTestingComponent(Question question){
        VerticalLayout root = new VerticalLayout();
        setCompositionRoot(root);

        Label quest = new Label(question.getQuestion());
        quest.setStyleName("question-test");
        CSSInject css = new CSSInject(UI.getCurrent());
        css.setStyles(".question-test{font-family: 'Dancing Script', cursive;}");


        OptionGroup answers = new OptionGroup();
        for (Answer answer : question.getAnswers()){
            answers.addItem(answer.getId());
            answers.setItemCaption(answer.getId(), answer.getAnswer());
        }

        root.addComponents(quest, answers);
    }
}
