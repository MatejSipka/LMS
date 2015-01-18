package cz.muni.fi.xryvola.services;

import java.math.BigInteger;
import java.util.Collection;

/**
 * Created by adam on 11.1.15.
 */
public interface ContentSharingManager {

    void createContentSharing(ContentSharing cs);

    void deleteContentSharing(Long id);

    ContentSharing getContentSharingById(Long id);

    void updateContentSharing(ContentSharing cs);

    void addPresentationInClassroom(Long presentation, Long classroom, Long author);

    void addTestInClassroom(Long test, Long classroom, Long author);

    void deleteTestFromClassroom(Long test, Long classroom);

    void deletePresentationFromClassroom(Long presentation, Long classroom);

    Collection<ContentSharing> getContentSharingFromClassroom(Long classroom);

    Collection<ContentSharing> getPresentationsFromClassroom(Long classroom);

    Collection<ContentSharing> getTestsFromClassroom(Long classroom);
}
