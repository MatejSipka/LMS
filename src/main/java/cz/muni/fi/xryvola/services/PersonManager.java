package cz.muni.fi.xryvola.services;

/**
 * Created by adam on 10.1.15.
 */
public interface PersonManager {

    public void createPerson(Person person);

    public void updatePerson(Person person);

    public void deletePerson(Person person);

    public Person getPersonById(Long id);

    public Person getPersonByUsername(String username);
}
