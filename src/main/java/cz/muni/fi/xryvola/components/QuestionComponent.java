package cz.muni.fi.xryvola.components;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ChameleonTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.services.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 10.11.14.
 */
public class QuestionComponent extends CustomComponent {

    private Question question;
    private List<AnswerComponent> listOfAns = new ArrayList();
    private TextField questionField = new TextField();
    private SuperManager superManager;
    public QuestionComponent(Question quest){

        this.superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();
        this.question = quest;

        VerticalLayout mainTestLay = new VerticalLayout();
        mainTestLay.setSizeFull();
        mainTestLay.setMargin(true);
        mainTestLay.setSpacing(true);

        questionField.setValue(question.getQuestion());
        questionField.setWidth("500px");
        mainTestLay.addComponent(questionField);

        final VerticalLayout answerLay = new VerticalLayout();
        answerLay.setSpacing(true);
        mainTestLay.addComponent(answerLay);

        for (Answer a : question.getAnswers()){
            AnswerComponent ac = new AnswerComponent(a);
            listOfAns.add(ac);
            answerLay.addComponent(ac);
        }

        HorizontalLayout butts = new HorizontalLayout();
        mainTestLay.addComponent(butts);

        Button addAnswer = new Button();
        addAnswer.setDescription("Přidat odpověď");
        addAnswer.setIcon(FontAwesome.PLUS_SQUARE);
        addAnswer.setStyleName(ChameleonTheme.BUTTON_BORDERLESS);
        butts.addComponent(addAnswer);

        addAnswer.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Answer temp = new Answer();
                temp.setAnswer("");
                temp.setIsCorrect(false);
                superManager.getAnswerManager().createAnswer(temp);
                question.getAnswers().add(temp);
                superManager.getQuestionManager().updateQuestion(question);
                question = superManager.getQuestionManager().getQuestionById(question.getId());
                AnswerComponent newAnswer = new AnswerComponent(temp);
                answerLay.addComponent(newAnswer);
                listOfAns.add(newAnswer);
            }
        });

        Button delAnswer = new Button();
        delAnswer.setDescription("Smazat odpověď");
        delAnswer.setIcon(FontAwesome.MINUS_SQUARE);
        delAnswer.setStyleName(ChameleonTheme.BUTTON_BORDERLESS);
        butts.addComponent(delAnswer);

        delAnswer.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (listOfAns.size() < 3){
                    Notification not = new Notification("NELZE SMAZAT", "Je treba uvest alespon dve odpovedi");
                    not.setDelayMsec(600);
                    not.show(Page.getCurrent());
                }else {
                    answerLay.removeComponent(listOfAns.get(listOfAns.size() - 1));
                    listOfAns.remove(listOfAns.size() - 1);
                    question.getAnswers().remove(question.getAnswers().size() - 1);
                }
            }
        });

        setCompositionRoot(mainTestLay);

    }

    private List<Answer> getListOfAnswers() {
        List<Answer> listOfAnswers = new ArrayList<Answer>();
        for(AnswerComponent component : listOfAns){
            listOfAnswers.add(component.getAnswer());
        }
        return listOfAnswers;
    }

    public Question getQuestion() {
        this.question.setQuestion(questionField.getValue());
        this.question.setAnswers(this.getListOfAnswers());
        return this.question;
    }
}
