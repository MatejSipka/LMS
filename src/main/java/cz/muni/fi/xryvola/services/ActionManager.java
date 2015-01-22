package cz.muni.fi.xryvola.services;

import java.util.List;

/**
 * Created by adam on 19.1.15.
 */
public interface ActionManager {

    public void createAction(Action action);

    public void deleteAction(Long id);

    public Action getActionById(Long id);

    public List<Action> getActionsByPerson(Long userId);

    public List<Action> getActionsByDocument(Long docId);

    public List<Action> getActionByPersonByDocument(Long userId, Long docId);

}
