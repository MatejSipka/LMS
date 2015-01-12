package cz.muni.fi.xryvola.services;

import cz.muni.fi.xryvola.services.*;

/**
 * Created by adam on 11.1.15.
 */
public interface AnswerManager {

    public void createAnswer(Answer answer);

    public void updateAnswer(Answer answer);

    public void deleteAnswer(Answer answer);

    public Answer getAnswerById(Long id);
}
