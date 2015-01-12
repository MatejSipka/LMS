package cz.muni.fi.xryvola.services;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by adam on 11.1.15.
 */
public class ContentSharingManagerImpl implements ContentSharingManager {

    private EntityManager em;

    public ContentSharingManagerImpl(EntityManager em){
        this.em = em;
    }

    @Override
    public void addPresentationInClassroom(Long presentation, Long classroom, Long author) {
        ContentSharing ch = new ContentSharing();
        ch.setClassroomId(classroom);
        ch.setDocumentType("PRESENTATION");
        ch.setDocumentId(presentation);
        ch.setTeacherId(author);

        em.getTransaction().begin();
        em.persist(ch);
        em.getTransaction().commit();
    }

    @Override
    public void addTestInClassroom(Long test, Long classroom, Long author) {
        ContentSharing ch = new ContentSharing();
        ch.setClassroomId(classroom);
        ch.setDocumentType("TEST");
        ch.setDocumentId(test);
        ch.setTeacherId(author);

        em.getTransaction().begin();
        em.persist(ch);
        em.getTransaction().commit();
    }

    @Override
    public void deleteTestFromClassroom(Long test, Long classroom) {

        System.out.println("DELETE >> TEST: " + test + " CLASSROOM: " + classroom);
        Query q = em.createNativeQuery("DELETE FROM CONTENTSHARING " +
                "WHERE CLASSROOMID = ? AND DOCUMENTID = ? AND DOCUMENTTYPE LIKE ?");
        q.setParameter(1, classroom).setParameter(2, test).setParameter(3, "TEST");
        em.getTransaction().begin();
        q.executeUpdate();
        em.getTransaction().commit();


    }

    @Override
    public void deletePresentationFromClassroom(Long presentation, Long classroom) {
        System.out.println("DELETE >> PRESENTATION: " + presentation + " CLASSROOM: " + classroom);
        Query q = em.createNativeQuery("DELETE FROM CONTENTSHARING " +
                "WHERE CLASSROOMID = ? AND DOCUMENTID = ? AND DOCUMENTTYPE LIKE ?");
        q.setParameter(1, classroom).setParameter(2, presentation).setParameter(3, "PRESENTATION");
        em.getTransaction().begin();
        q.executeUpdate();
        em.getTransaction().commit();
    }

    @Override
    public Collection<Long> getPresentationsFromClassroom(Long classroom) {
        Query q = em.createNativeQuery("SELECT DOCUMENTID FROM CONTENTSHARING WHERE CLASSROOMID = ? AND DOCUMENTTYPE LIKE ?")
                .setParameter(1, classroom).setParameter(2, "PRESENTATION");
        List<BigInteger> list = q.getResultList();
        List<Long> result = new ArrayList<Long>();
        for(BigInteger bi : list){
            result.add(bi.longValue());
        }
        return result;
    }

    @Override
    public Collection<Long> getTestsFromClassroom(Long classroom) {
        Query q = em.createNativeQuery("SELECT DOCUMENTID FROM CONTENTSHARING WHERE CLASSROOMID = ? AND DOCUMENTTYPE LIKE ?")
                .setParameter(1, classroom).setParameter(2, "TEST");
        List<BigInteger> list = q.getResultList();
        List<Long> result = new ArrayList<Long>();
        for(BigInteger bi : list){
            result.add(bi.longValue());
        }
        return result;
    }
}
