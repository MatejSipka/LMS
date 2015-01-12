package cz.muni.fi.xryvola.services;

import javax.persistence.EntityManager;

/**
 * Created by adam on 11.1.15.
 */
public class SlideManagerImpl implements SlideManager {

    private EntityManager em;

    public SlideManagerImpl(EntityManager em){
        this.em = em;
    }

    @Override
    public void createSlide(Slide slide) {
        em.getTransaction().begin();
        em.persist(slide);
        em.getTransaction().commit();
    }

    @Override
    public void updateSlide(Slide slide) {
        em.getTransaction().begin();
        Slide s = em.find(Slide.class, slide.getId());
        s.setName(slide.getName());
        s.setHtmlContent(slide.getHtmlContent());
        em.merge(s);
        em.getTransaction().commit();
    }

    @Override
    public void deleteSlide(Long id) {
        Slide s = em.find(Slide.class, id);
        em.getTransaction().begin();
        em.remove(s);
        em.getTransaction().commit();
    }

    @Override
    public Slide getSlideById(Long id) {
        return em.find(Slide.class, id);
    }
}
