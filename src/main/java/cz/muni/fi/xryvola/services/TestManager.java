package cz.muni.fi.xryvola.services;

/**
 * Created by adam on 11.1.15.
 */
public interface TestManager {

    public void createTest(Test test);

    public void updateTest(Test test);

    public void deleteTest(Long id);

    public Test getTestById(Long id);
}
