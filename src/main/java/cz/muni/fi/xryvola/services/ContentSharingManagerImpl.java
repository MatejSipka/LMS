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
    public void createContentSharing(ContentSharing cs) {
        em.getTransaction().begin();
        em.persist(cs);
        em.getTransaction().commit();
    }

    @Override
    public void deleteContentSharing(Long id) {
        ContentSharing cs = em.find(ContentSharing.class, id);
        em.getTransaction().begin();
        em.remove(cs);
        em.getTransaction().commit();
    }

    @Override
    public ContentSharing getContentSharingById(Long id) {
        return em.find(ContentSharing.class, id);
    }

    @Override
    public void updateContentSharing(ContentSharing cs) {
        em.getTransaction().begin();
        ContentSharing csh = em.find(ContentSharing.class, cs.getId());
        csh.setWhen(cs.getWhen());
        csh.setTill(cs.getTill());
        em.merge(csh);
        em.getTransaction().commit();
    }

    @Override
    public void deletePresentation(Long id) {
        Query q = em.createNativeQuery("DELETE FROM CONTENTSHARING " +
                "WHERE DOCUMENTID = ? AND DOCUMENTTYPE LIKE ?");
        q.setParameter(1, id).setParameter(2, "PRESENTATION");
        em.getTransaction().begin();
        q.executeUpdate();
        em.getTransaction().commit();
    }

    @Override
    public void deleteTest(Long id) {
        Query q = em.createNativeQuery("DELETE FROM CONTENTSHARING " +
                "WHERE DOCUMENTID = ? AND DOCUMENTTYPE LIKE ?");
        q.setParameter(1, id).setParameter(2, "TEST");
        em.getTransaction().begin();
        q.executeUpdate();
        em.getTransaction().commit();
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
    public Collection<ContentSharing> getContentSharingFromClassroom(Long classroom) {
        Query q = em.createQuery("SELECT cs FROM ContentSharing cs WHERE cs.classroomId = :classId")
                .setParameter("classId", classroom);
        return q.getResultList();
    }

    @Override
    public Collection<ContentSharing> getPresentationsFromClassroom(Long classroom) {
        Query q = em.createQuery("SELECT cs FROM ContentSharing cs WHERE cs.classroomId = :classId AND cs.documentType LIKE :docType")
                .setParameter("classId", classroom).setParameter("docType", "PRESENTATION");
        return q.getResultList();
    }

    @Override
    public Collection<ContentSharing> getTestsFromClassroom(Long classroom) {
        Query q = em.createNativeQuery("SELECT DOCUMENTID FROM CONTENTSHARING WHERE CLASSROOMID = ? AND DOCUMENTTYPE LIKE ?")
                .setParameter(1, classroom).setParameter(2, "TEST");
        return q.getResultList();
    }
}
