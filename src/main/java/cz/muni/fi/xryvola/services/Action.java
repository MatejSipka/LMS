package cz.muni.fi.xryvola.services;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by adam on 11.1.15.
 */

@Entity
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long who;

    private Long what;

    private Date when;

    private String verb;

    private Long result;
}
