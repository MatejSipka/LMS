package cz.muni.fi.xryvola.services;

import java.math.BigInteger;

/**
 * Created by adam on 10.1.15.
 */
public interface PresentationManager {

    public void createPresentation(Presentation presentation);

    public void updatePresentation(Presentation presentation);

    public void deletePresentation(Long id);

    public Presentation getPresentationById(Long id);
}
