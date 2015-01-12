package cz.muni.fi.xryvola.services;

import java.util.Collection;

/**
 * Created by adam on 11.1.15.
 */
public interface SchoolManager {

    public void createSchool(School school);

    public void updateSchool(School school);

    public void deleteSchool(Long id);

    public School getSchoolById(Long id);

    public Collection<School> getAllSchools();

    public School getSchoolByTeacher(Person person);

}
