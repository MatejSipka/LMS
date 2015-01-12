package cz.muni.fi.xryvola.services;

import javax.persistence.EntityManager;

/**
 * Created by adam on 10.1.15.
 */
public class PresentationManagerImpl implements PresentationManager {

    private EntityManager entityManager;

    public PresentationManagerImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public void createPresentation(Presentation presentation) {
        entityManager.getTransaction().begin();
        entityManager.persist(presentation);
        entityManager.getTransaction().commit();
    }

    @Override
    public void updatePresentation(Presentation presentation) {
        entityManager.getTransaction().begin();
        Presentation p = entityManager.find(Presentation.class, presentation.getId());
        p.setName(presentation.getName());
        p.setSlides(presentation.getSlides());
        entityManager.merge(p);
        entityManager.getTransaction().commit();
    }

    @Override
    public void deletePresentation(Long id) {
        Presentation p = entityManager.find(Presentation.class, id);
        entityManager.getTransaction().begin();
        entityManager.remove(p);
        entityManager.getTransaction().commit();
    }

    @Override
    public Presentation getPresentationById(Long id) {
        return entityManager.find(Presentation.class, id);
    }
}
