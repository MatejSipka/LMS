package cz.muni.fi.xryvola.components;

import com.vaadin.data.Property;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.window.WindowMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cz.muni.fi.xryvola.MyVaadinUI;
import cz.muni.fi.xryvola.services.FileUploader;
import cz.muni.fi.xryvola.services.*;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by adam on 26.10.14.
 */

public class RegistrationWindow extends Window {

    SuperManager superManager = ((MyVaadinUI)UI.getCurrent()).getSuperManager();

    public RegistrationWindow(){
        VerticalLayout main = new VerticalLayout();
        this.setContent(main);
        this.center();
        this.setModal(true);
        this.setWindowMode(WindowMode.MAXIMIZED);

        main.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        main.setMargin(true);
        main.setSpacing(true);
        final TextField name = new TextField("jméno:");
        name.focus();
        final TextField email = new TextField("e-mail:");
        final TextField userName = new TextField("přihlašovací jméno:");
        final PasswordField password = new PasswordField("heslo:");
        final PasswordField checkPassword = new PasswordField("heslo pro kontrolu:");
        final ComboBox roleBox = new ComboBox("role:");
        final ComboBox schoolBox = new ComboBox("škola:");
        final ComboBox classBox = new ComboBox("třída:");
        Button acc = new Button("Zaregistrovat");

        acc.setStyleName(ValoTheme.BUTTON_FRIENDLY);

        roleBox.setNullSelectionAllowed(false);
        schoolBox.setNullSelectionAllowed(false);
        classBox.setNullSelectionAllowed(false);
        schoolBox.setVisible(false);
        classBox.setVisible(false);

        main.addComponent(name);
        main.addComponent(email);
        main.addComponent(userName);
        main.addComponent(password);
        main.addComponent(checkPassword);
        main.addComponent(roleBox);
        main.addComponent(schoolBox);
        main.addComponent(classBox);
        main.addComponent(acc);
        main.setMargin(true);

        roleBox.addItem("učitel");
        roleBox.addItem("student");

        roleBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                schoolBox.setVisible(true);
                classBox.setVisible(false);
                schoolBox.setValue(null);
            }
        });

        Collection<School> schools = superManager.getSchoolManager().getAllSchools();
        for (School school : schools){
            schoolBox.addItem(school.getId());
            schoolBox.setItemCaption(school.getId(), school.getName());
        }

        schoolBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                if (schoolBox.getValue() != null) {
                    if (roleBox.getValue() == "student") {
                        classBox.setVisible(true);
                        classBox.removeAllItems();
                        School sch = superManager.getSchoolManager().getSchoolById((Long) schoolBox.getValue());
                        Set<Classroom> classes = new HashSet<Classroom>();
                        for (Person teacher : sch.getTeachers()) {
                            classes.addAll(teacher.getClassrooms());
                        }
                        for (Classroom classroom : classes) {
                            classBox.addItem(classroom.getId());
                            classBox.setItemCaption(classroom.getId(), classroom.getName());
                        }
                    } else {
                        classBox.setVisible(false);
                    }
                }
            }
        });

        acc.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        acc.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                //EMPTY FIELDS CHECK
                if (name.getValue() == ""){
                    Notification not = new Notification("Vyplňte jméno");
                    not.show(Page.getCurrent());
                }else if (email.getValue()== ""){
                    Notification not = new Notification("Vyplňte e-mail");
                    not.show(Page.getCurrent());
                }else if (userName.getValue() == ""){
                    Notification not = new Notification("Vyplňte přihlašovací jméno");
                    not.show(Page.getCurrent());
                }else if (password.getValue() == ""){
                    Notification not = new Notification("Vyplňte heslo");
                    not.show(Page.getCurrent());
                }else if (checkPassword.getValue() == ""){
                    Notification not = new Notification("Vyplňte heslo pro kontrolu");
                    not.show(Page.getCurrent());
                }else if (roleBox.getValue() == null){
                    Notification not = new Notification("Zvolte si roli");
                    not.show(Page.getCurrent());
                }else if (schoolBox.getValue() == null){
                    Notification not = new Notification("Zvolte si školu");
                    not.show(Page.getCurrent());
                }else if (roleBox.getValue() == "student" && classBox.getValue() == null){
                    Notification not = new Notification("Zvolte si třídu");
                    not.show(Page.getCurrent());
                }else{
                    Person person = superManager.getPersonManager().getPersonByUsername(userName.getValue());
                    if (person != null){
                        Notification not = new Notification("Přihlašovací jméno již existuje");
                        not.show(Page.getCurrent());
                        password.setValue("");
                        checkPassword.setValue("");
                        userName.setValue("");
                    }else if (password.getValue().equals(checkPassword.getValue())){
                        Person newUser = new Person();
                        if (roleBox.getValue() == "učitel"){
                            newUser.setRole("TEACHER");
                        }else{
                            newUser.setRole("STUDENT");
                            newUser.setMyClass(superManager.getClassroomManager().getClassroomById((Long) classBox.getValue()));
                        }
                        newUser.setName(name.getValue());
                        newUser.setEmail(email.getValue());
                        newUser.setUsername(userName.getValue());
                        newUser.setScore((long) 0);
                        newUser.setPassword(password.getValue());
                        saltIt(newUser, password.getValue());
                        superManager.getPersonManager().createPerson(newUser);
                        if (roleBox.getValue() == "student"){
                            Classroom c = superManager.getClassroomManager().getClassroomById((Long) classBox.getValue());
                            c.getStudents().add(newUser);
                            superManager.getClassroomManager().updateClassroom(c);
                        }else{
                            School sch = superManager.getSchoolManager().getSchoolById((Long) schoolBox.getValue());
                            sch.getTeachers().add(newUser);
                            superManager.getSchoolManager().updateSchool(sch);
                        }
                        System.out.println("VYTVOREM UZIVATEL S ID: " + newUser.getUsername());
                        FileUploader uploader = new FileUploader();
                        uploader.copyProfilePic(newUser.getUsername());
                        Notification notification = new Notification("UŽIVATEL VYTVOŘEN", Notification.TYPE_HUMANIZED_MESSAGE);
                        notification.setDelayMsec(600);
                        notification.show(Page.getCurrent());
                        UI.getCurrent().getNavigator().navigateTo("");
                    }else{
                        password.setValue("");
                        checkPassword.setValue("");
                        Notification notification = new Notification("ŠPATNÉ HESLO", "Heslo a kontrola hesla se neshodují", Notification.TYPE_HUMANIZED_MESSAGE);
                        notification.setDelayMsec(600);
                        notification.show(Page.getCurrent());
                    }
                    close();

                }
            }
        });
    }

    private void saltIt(Person person, String password){
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        Object salt = rng.nextBytes();

        String hashedPassword = new Sha256Hash(password, salt, 1024).toBase64();

        person.setSalt(salt.toString());
        person.setPassword(hashedPassword );
    }
}
