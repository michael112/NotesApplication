package hibernate.controllers;

import hibernate.mappedclasses.Subject;

import java.util.*;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Criteria;
import org.hibernate.cfg.Configuration;
import hibernate.config.*;

public class SubjectsController {

    private static SessionFactory factory;

    public SubjectsController() {
        try{
            //this.factory = new Configuration().configure().buildSessionFactory();
            this.factory = HibernateUtil.getSessionFactory();
        }
        catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    // CREATE
    public Integer addSubject( Subject subject ) {
        if( !( titleExists(subject.getSubjectName()) ) ) {
            Session session = factory.openSession();
            //Session session = factory.getCurrentSession();
            Transaction tx = null;
            Integer subjectID = null;
            try {
                tx = session.beginTransaction();
                subjectID = (Integer) session.save(subject);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            } finally {
                session.close();
            }
            return subjectID;
        }
        else {
            return null;
        }
    }

    // READ (all fields) - BY ID!
    public Subject readSubject( int subjectID ) {
        String query = "FROM Subject WHERE subjectID = " + subjectID;
        return readSubjectByQuery(query);
    }

    // READ (all fields) - BY LOGIN!
    public Subject readSubject( String subjectName ) {
        String query = "FROM Subject WHERE subjectName = '" + subjectName + "'";
        return readSubjectByQuery(query);
    }

    // READ (all fields) - BY QUERY!
    private Subject readSubjectByQuery( String readQuery ) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Subject> subjects = (List<Subject>)session.createQuery(readQuery).list();
            tx.commit();
            return subjects.get(0);
        }
        catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            return null;
        }
        catch(IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return null;
        }
        finally {
            session.close();
        }
    }

    // READ (all subjects with all fields)
    public List<Subject> readAllSubjects( ){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Subject> subjects = (List<Subject>)session.createQuery("FROM Subject s ORDER BY s.subjectID").list();
            tx.commit();
            return subjects;
        }
        catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
            return null;
        }
        finally {
            session.close();
        }
    }

    // UPDATE
    public void updateSubject(Subject updatedSubject, Integer subjectID) {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Subject subject = (Subject)session.get(Subject.class, subjectID);
            subject.update(updatedSubject);
            session.update(subject);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
    }

    // DELETE
    public void deleteSubject(Integer subjectID) {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Subject subject = (Subject)session.get(Subject.class, subjectID);
            session.delete(subject);
            tx.commit();
        }
        catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }
        finally {
            session.close();
        }
    }

    private boolean titleExists( String subjectTitle ) {
        List<Subject> subjects = readAllSubjects();
        for( Subject subject : subjects ) {
            if( subject.getSubjectName().equals(subjectTitle) ) {
                return true;
            }
        }
        return false;
    }

}
