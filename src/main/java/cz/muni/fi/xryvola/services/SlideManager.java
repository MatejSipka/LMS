package cz.muni.fi.xryvola.services;

/**
 * Created by adam on 11.1.15.
 */
public interface SlideManager {

    public void createSlide(Slide slide);

    public void updateSlide(Slide slide);

    public void deleteSlide(Long id);

    public Slide getSlideById(Long id);
}
