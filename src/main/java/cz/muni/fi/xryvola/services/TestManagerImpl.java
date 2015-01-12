package cz.muni.fi.xryvola.services;

import javax.persistence.EntityManager;

/**
 * Created by adam on 11.1.15.
 */
public class TestManagerImpl implements TestManager {

    private EntityManager em;

    public TestManagerImpl(EntityManager em){
        this.em = em;
    }

    @Override
    public void createTest(Test test) {
        em.getTransaction().begin();
        em.persist(test);
        em.getTransaction().commit();
    }

    @Override
    public void updateTest(Test test) {
        em.getTransaction().begin();
        Test t = em.find(Test.class, test.getId());
        t.setName(test.getName());
        t.setQuestions(test.getQuestions());
        em.merge(t);
        em.getTransaction().commit();
    }

    @Override
    public void deleteTest(Long id) {
        Test t = em.find(Test.class, id);
        em.getTransaction().begin();
        em.remove(t);
        em.getTransaction().commit();
    }

    @Override
    public Test getTestById(Long id) {
        return em.find(Test.class, id);
    }
}
