package cz.muni.fi.xryvola.services;

/**
 * Created by adam on 11.1.15.
 */
public interface QuestionManager {

    public void createQuestion(Question question);

    public void updateQuestion(Question question);

    public void deleteQuestion(Long id);

    public Question getQuestionById(Long id);
}
