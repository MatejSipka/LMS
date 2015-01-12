package cz.muni.fi.xryvola.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by adam on 10.1.15.
 */
public class SuperManager {

    private EntityManagerFactory factory;
    private EntityManager em;

    private AnswerManager answerManager;
    private ClassroomManager classroomManager;
    private PersonManager personManager;
    private PresentationManager presentationManager;
    private QuestionManager questionManager;
    private SchoolManager schoolManager;
    private SlideManager slideManager;
    private TestManager testManager;
    private ContentSharingManager contentSharingManager;

    public SuperManager(){

        factory = Persistence.createEntityManagerFactory("lms");
        em = factory.createEntityManager();

        answerManager = new AnswerManagerImpl(em);
        classroomManager = new ClassroomManagerImpl(em);
        personManager = new PersonManagerImpl(em);
        presentationManager = new PresentationManagerImpl(em);
        questionManager = new QuestionManagerImpl(em);
        schoolManager = new SchoolManagerImpl(em);
        slideManager = new SlideManagerImpl(em);
        testManager = new TestManagerImpl(em);
        contentSharingManager = new ContentSharingManagerImpl(em);
    }

    public EntityManagerFactory getFactory() {
        return factory;
    }

    public EntityManager getEm() {
        return em;
    }

    public AnswerManager getAnswerManager() {
        return answerManager;
    }

    public ClassroomManager getClassroomManager() {
        return classroomManager;
    }

    public PersonManager getPersonManager() {
        return personManager;
    }

    public PresentationManager getPresentationManager() {
        return presentationManager;
    }

    public QuestionManager getQuestionManager() {
        return questionManager;
    }

    public SchoolManager getSchoolManager() {
        return schoolManager;
    }

    public SlideManager getSlideManager() {
        return slideManager;
    }

    public TestManager getTestManager() {
        return testManager;
    }

    public ContentSharingManager getContentSharingManager() {
        return contentSharingManager;
    }
}
