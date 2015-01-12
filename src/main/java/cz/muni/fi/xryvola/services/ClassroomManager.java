package cz.muni.fi.xryvola.services;

import java.util.Collection;

/**
 * Created by adam on 11.1.15.
 */
public interface ClassroomManager {

    public void createClassroom(Classroom classroom);

    public void updateClassroom(Classroom classroom);

    public void deleteClassroom(Classroom classroom);

    public Classroom getClassroomById(Long id);

    public Collection<Classroom> getClassroomsFromSchool(School school);

}
