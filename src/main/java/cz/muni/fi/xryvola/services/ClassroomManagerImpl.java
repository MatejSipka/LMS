package cz.muni.fi.xryvola.services;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by adam on 11.1.15.
 */
public class ClassroomManagerImpl implements ClassroomManager {

    private EntityManager entityManager;

    public ClassroomManagerImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public void createClassroom(Classroom classroom) {
        entityManager.getTransaction().begin();
        entityManager.persist(classroom);
        entityManager.getTransaction().commit();
    }

    @Override
    public void updateClassroom(Classroom classroom) {
        entityManager.getTransaction().begin();
        Classroom c = entityManager.find(Classroom.class, classroom.getId());
        c.setName(classroom.getName());
        c.setStudents(classroom.getStudents());
        entityManager.merge(c);
        entityManager.getTransaction().commit();
    }

    @Override
    public void deleteClassroom(Classroom classroom) {
        Classroom c = entityManager.find(Classroom.class, classroom.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(c);
        entityManager.getTransaction().commit();
    }

    @Override
    public Classroom getClassroomById(Long id) {
        return entityManager.find(Classroom.class, id);
    }

    @Override
    public Collection<Classroom> getClassroomsFromSchool(School school) {
        List<Person> teachers = school.getTeachers();
        Collection<Classroom> classes = new ArrayList<Classroom>();
        for (Person teacher : teachers){
            classes.addAll(teacher.getClassrooms());
        }
        return classes;
    }
}
