package cz.muni.fi.xryvola.services;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by adam on 19.1.15.
 */
public class ActionManagerImpl implements ActionManager {

    private EntityManager em;

    public ActionManagerImpl(EntityManager em){
        this.em = em;
    }

    @Override
    public void createAction(Action action) {
        em.getTransaction().begin();
        em.persist(action);
        em.getTransaction().commit();
    }

    @Override
    public void deleteAction(Long id) {
        Action a = em.find(Action.class, id);
        em.getTransaction().begin();
        em.remove(a);
        em.getTransaction().commit();
    }

    @Override
    public Action getActionById(Long id) {
        return em.find(Action.class, id);
    }

    @Override
    public List<Action> getActionsByPerson(Long userId) {
        return null;
    }

    @Override
    public List<Action> getActionsByDocument(Long docId) {
        return null;
    }

    @Override
    public List<Action> getActionByPersonByDocument(Long userId, Long docId) {
        Query q = em.createQuery("SELECT action FROM Action action WHERE action.who = :userId AND action.what = :docId")
                .setParameter("userId", userId).setParameter("docId", docId);
        return q.getResultList();
    }
}
