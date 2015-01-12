package cz.muni.fi.xryvola.services;

import javax.persistence.EntityManager;

/**
 * Created by adam on 11.1.15.
 */
public class AnswerManagerImpl implements AnswerManager {

    private EntityManager entityManager;

    public AnswerManagerImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public void createAnswer(Answer answer) {
        entityManager.getTransaction().begin();
        entityManager.persist(answer);
        entityManager.getTransaction().commit();
    }

    @Override
    public void updateAnswer(Answer answer) {
        entityManager.getTransaction().begin();
        Answer a = entityManager.find(Answer.class, answer.getId());
        a.setAnswer(answer.getAnswer());
        a.setIsCorrect(answer.getIsCorrect());
        entityManager.merge(a);
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleteAnswer(Answer answer) {
        Answer a = entityManager.find(Answer.class, answer.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(a);
        entityManager.getTransaction().commit();
    }

    @Override
    public Answer getAnswerById(Long id) {
        return entityManager.find(Answer.class, id);
    }
}
