package cz.muni.fi.xryvola.navigateApp;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.components.QuestionTestingComponent;
import cz.muni.fi.xryvola.services.Question;
import cz.muni.fi.xryvola.services.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adam on 12.12.14.
 */
public class StudentTestView extends VerticalLayout implements View {

    public StudentTestView(Test test){
        this.setMargin(true);
        this.setSpacing(true);
        List<QuestionTestingComponent> questions = new ArrayList<QuestionTestingComponent>();
        for (Question question : test.getQuestions()){
            questions.add(new QuestionTestingComponent(question));
        }

        this.addComponent(new Label("Test: " + test.getName()));
        for(QuestionTestingComponent component : questions){
            this.addComponent(component);
        }

        Button close = new Button("Zavřit");
        close.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UI.getCurrent().getNavigator().navigateTo(MyVaadinUI.STUDENTCONTENTVIEW);
            }
        });
        this.addComponent(close);

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {

    }
}
