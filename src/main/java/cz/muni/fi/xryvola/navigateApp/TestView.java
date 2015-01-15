package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ChameleonTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.components.QuestionComponent;
import cz.muni.fi.xryvola.services.*;

/**
 * Created by adam on 5.11.14.
 */
public class TestView extends HorizontalSplitPanel implements View {

    private Test currentTest;
    private Question currQuestion;

    private QuestionComponent questionCom;
    private Tree listOfQuestions = new Tree();

    private SuperManager superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();
    private TestManager testManager = superManager.getTestManager();
    private QuestionManager questionManager = superManager.getQuestionManager();
    private AnswerManager answerManager = superManager.getAnswerManager();

    public TestView(Test test){
        currentTest = test;
        questionCom = new QuestionComponent(currentTest.getQuestions().get(0));
        currQuestion = currentTest.getQuestions().get(0);
        this.setSplitPosition(50, UNITS_PERCENTAGE);
        this.setFirstComponent(questionCom);

        /*RIGHT SIDE - list of questions*/
        VerticalLayout rightSide = new VerticalLayout();
        rightSide.setMargin(true);
        rightSide.setSpacing(true);
        this.setSecondComponent(rightSide);

        HorizontalLayout testMenuButts = new HorizontalLayout();
        rightSide.addComponent(testMenuButts);
        testMenuButts.setSizeFull();

        HorizontalLayout testMenuButtsIn = new HorizontalLayout();
        testMenuButts.addComponent(testMenuButtsIn);

        Button addQuest = new Button();
        testMenuButtsIn.addComponent(addQuest);
        addQuest.setDescription("Přidat otázku");
        addQuest.setIcon(FontAwesome.PLUS_SQUARE);
        addQuest.setStyleName(ChameleonTheme.BUTTON_BORDERLESS);
        addQuest.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Question newQuest = new Question();
                newQuest.setQuestion("Otazka");
                Answer stAns = new Answer();
                stAns.setAnswer("");
                stAns.setIsCorrect(false);
                Answer ndAns = new Answer();
                ndAns.setAnswer("");
                ndAns.setIsCorrect(false);
                newQuest.getAnswers().add(stAns);
                newQuest.getAnswers().add(ndAns);

                questionManager.createQuestion(newQuest);

                currentTest.getQuestions().add(newQuest);
                testManager.updateTest(currentTest);
                currentTest = testManager.getTestById(currentTest.getId());
                questionCom = new QuestionComponent(newQuest);
                currQuestion = getQuestion(newQuest.getId());
                setFirstComponent(questionCom);
                loadQuestionTree();
            }
        });

        Button delQuest = new Button();
        testMenuButtsIn.addComponent(delQuest);
        delQuest.setDescription("Smazat otázku");
        delQuest.setIcon(FontAwesome.MINUS_SQUARE);
        delQuest.setStyleName(ChameleonTheme.BUTTON_BORDERLESS);
        delQuest.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                currentTest.getQuestions().remove(getQuestion(listOfQuestions.getValue()));
                currQuestion = currentTest.getQuestions().get(0);
                loadQuestionTree();
            }
        });

        final Button saveQuest = new Button();
        saveQuest.setIcon(FontAwesome.FLOPPY_O);
        saveQuest.setDescription("Uložit test");
        saveQuest.setStyleName(ChameleonTheme.BUTTON_BORDERLESS);
        testMenuButtsIn.addComponent(saveQuest);
        saveQuest.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                save();
            }
        });

        Button close = new Button();
        close.setIcon(FontAwesome.TIMES);
        close.setStyleName(ChameleonTheme.BUTTON_ICON_ONLY);
        close.addStyleName(ChameleonTheme.BUTTON_BORDERLESS);
        close.setDescription("Zavřít");
        close.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                final Window saveWin = new Window("Uložit");
                VerticalLayout saveLay = new VerticalLayout();
                saveLay.setMargin(true);
                saveLay.setSpacing(true);
                saveWin.setModal(true);
                saveWin.center();
                saveWin.setContent(saveLay);
                Label saveLab = new Label("Chcete test uložit?");
                saveLay.addComponent(saveLab);
                HorizontalLayout saveButts = new HorizontalLayout();
                saveLay.addComponent(saveButts);
                saveButts.setSpacing(true);
                Button yes = new Button("Ano");
                yes.setIcon(FontAwesome.CHECK);
                Button no = new Button("Ne");
                no.setIcon(FontAwesome.TIMES);
                saveButts.addComponents(yes, no);
                yes.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        save();
                        UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.MATERIALSVIEW);
                        saveWin.close();
                    }
                });
                no.addClickListener(new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.MATERIALSVIEW);
                        saveWin.close();
                    }
                });
                UI.getCurrent().addWindow(saveWin);;
            }
        });
        testMenuButts.addComponent(close);
        testMenuButts.setComponentAlignment(close, Alignment.MIDDLE_RIGHT);

        rightSide.addComponent(listOfQuestions);
        loadQuestionTree();


        listOfQuestions.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (listOfQuestions.getValue() != null && listOfQuestions.getParent(listOfQuestions.getValue()) == null && listOfQuestions.getValue()!=currQuestion.getId()){
                    currQuestion = questionCom.getQuestion();
                    questionCom = new QuestionComponent(getQuestion(listOfQuestions.getValue()));
                    currQuestion = getQuestion(listOfQuestions.getValue());
                    setFirstComponent(questionCom);
                    loadQuestionTree();
                }
            }
        });

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
    }

    private Question getQuestion(Object id){
        for (Question question : currentTest.getQuestions()){
            if (question.getId() == id){
                return question;
            }
        }
        return null;
    }

    private void loadQuestionTree() {
        listOfQuestions.removeAllItems();
        for (Question question : currentTest.getQuestions()){
            listOfQuestions.addItem(question.getId());
            listOfQuestions.setItemCaption(question.getId(), question.getQuestion());
            listOfQuestions.setChildrenAllowed(question.getId(), false);
            /*
            for (Answer answer : question.getAnswers()){
                listOfQuestions.addItem(answer.getId());
                listOfQuestions.setItemCaption(answer.getId(), answer.getAnswer());
                listOfQuestions.setParent(answer.getId(), question.getId());
                listOfQuestions.setChildrenAllowed(answer.getId(), false);
            }
            */
        }
        listOfQuestions.setImmediate(true);
        listOfQuestions.select(currQuestion.getId());
    }

    private void save(){
            currQuestion = questionCom.getQuestion();
            testManager.updateTest(currentTest);
            currentTest = testManager.getTestById(currentTest.getId());
            loadQuestionTree();
    }
}
