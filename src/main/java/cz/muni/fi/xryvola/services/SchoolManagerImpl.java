package cz.muni.fi.xryvola.services;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Created by adam on 11.1.15.
 */
public class SchoolManagerImpl implements SchoolManager {

    private EntityManager em;

    public SchoolManagerImpl(EntityManager em){
        this.em = em;
    }

    @Override
    public void createSchool(School school) {
        em.getTransaction().begin();
        em.persist(school);
        em.getTransaction().commit();
    }

    @Override
    public void updateSchool(School school) {
        em.getTransaction().begin();
        School sch = em.find(School.class, school.getId());
        sch.setName(school.getName());
        sch.setTeachers(school.getTeachers());
        em.merge(sch);
        em.getTransaction().commit();
    }

    @Override
    public void deleteSchool(Long id) {
        School sch = em.find(School.class, id);
        em.getTransaction().begin();
        em.remove(sch);
        em.getTransaction().commit();
    }

    @Override
    public School getSchoolById(Long id) {
        return em.find(School.class, id);
    }

    @Override
    public Collection<School> getAllSchools() {
        Query query = em.createQuery("SELECT sch FROM School sch");
        return (Collection<School>) query.getResultList();
    }

    @Override
    public School getSchoolByTeacher(Person person) {
        Collection<School> schools = getAllSchools();
        for (School school : schools){
            for (Person teacher : school.getTeachers()){
                if (teacher.getId() == person.getId()){
                    return school;
                }
            }
        }
        System.err.println("SKOLA PODLE UCITELE NENALEZENA!");
        return null;
    }

}