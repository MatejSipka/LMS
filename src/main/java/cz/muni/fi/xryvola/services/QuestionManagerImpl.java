package cz.muni.fi.xryvola.services;

import javax.persistence.EntityManager;

/**
 * Created by adam on 11.1.15.
 */
public class QuestionManagerImpl implements QuestionManager {

    private EntityManager em;

    public QuestionManagerImpl(EntityManager em){
        this.em = em;
    }

    @Override
    public void createQuestion(Question question) {
        em.getTransaction().begin();
        em.persist(question);
        em.getTransaction().commit();
    }

    @Override
    public void updateQuestion(Question question) {
        em.getTransaction().begin();
        Question q = em.find(Question.class, question.getId());
        q.setQuestion(question.getQuestion());
        //q.setAnswers(question.getAnswers());
        em.merge(q);
        em.getTransaction().commit();
        em.refresh(q);
    }

    @Override
    public void deleteQuestion(Long id) {
        Question q = em.find(Question.class, id);
        em.getTransaction().begin();
        em.remove(q);
        em.getTransaction().commit();
    }

    @Override
    public Question getQuestionById(Long id) {
        return em.find(Question.class, id);
    }
}
