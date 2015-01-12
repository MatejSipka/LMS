package cz.muni.fi.xryvola.components;

import com.vaadin.ui.*;
import cz.muni.fi.xryvola.services.Answer;

/**
 * Created by adam on 10.11.14.
 */
public class AnswerComponent extends CustomComponent {

    private TextField answerField = new TextField();
    private CheckBox isCorrect = new CheckBox("Spravna");
    private Answer answer;

    public AnswerComponent(Answer answer){

        this.answer = answer;

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        setCompositionRoot(horizontalLayout);

        answerField.setValue(answer.getAnswer());
        isCorrect.setValue(answer.getIsCorrect());

        horizontalLayout.addComponent(answerField);
        horizontalLayout.addComponent(isCorrect);
        horizontalLayout.setComponentAlignment(isCorrect, Alignment.MIDDLE_LEFT);
    }

    public Answer getAnswer() {
        answer.setAnswer(answerField.getValue());
        answer.setIsCorrect(isCorrect.getValue());
        return answer;
    }
}
