package cz.muni.fi.xryvola.services;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by adam on 10.1.15.
 */
public class PersonManagerImpl implements PersonManager {

    private EntityManager entityManager;

    public PersonManagerImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public void createPerson(Person person) {
        entityManager.getTransaction().begin();
        entityManager.persist(person);
        entityManager.getTransaction().commit();
    }

    @Override
    public void updatePerson(Person person) {
        entityManager.getTransaction().begin();
        Person p = entityManager.find(Person.class, person.getId());
        p.setName(person.getName());
        p.setRole(person.getRole());
        p.setClassrooms(person.getClassrooms());
        p.setEmail(person.getEmail());
        p.setMyClass(person.getMyClass());
        p.setPassword(person.getPassword());
        p.setPresentations(person.getPresentations());
        p.setSalt(person.getSalt());
        p.setScore(person.getScore());
        p.setTests(person.getTests());
        p.setUsername(person.getUsername());
        entityManager.merge(p);
        entityManager.getTransaction().commit();

    }

    @Override
    public void deletePerson(Person person) {
        Person p = entityManager.find(Person.class, person.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(p);
        entityManager.getTransaction().commit();
    }

    @Override
    public Person getPersonById(Long id) {
        Person p = entityManager.find(Person.class, id);
        return p;
    }

    @Override
    public Person getPersonByUsername(String username) {
        Query q = entityManager.createQuery("SELECT p FROM Person p WHERE p.username LIKE :username")
                .setParameter("username", username);
        List<Person> per = q.getResultList();
        if (per.size() != 1){
            return null;
        }else{
            return per.get(0);
        }
    }
}
