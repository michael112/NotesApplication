package hibernate.controllers;

import hibernate.mappedclasses.*;

import java.util.*;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Criteria;
import org.hibernate.cfg.Configuration;
import hibernate.config.*;


public class NotesController {

    private static SessionFactory factory;

    public NotesController() {
        try{
            //this.factory = new Configuration().configure().buildSessionFactory();
            this.factory = HibernateUtil.getSessionFactory();
        }
        catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    // CREATE
    public Integer addNote( Note note ) {
        Session session = factory.openSession();
        //Session session = factory.getCurrentSession();
        Transaction tx = null;
        Integer noteID = null;
        try {
            tx = session.beginTransaction();

            if( !( subjectExists(note.getSubject().getSubjectID()) ) ) {
                // add subject to database
                session.save(note.getSubject());
            }
            if( !( userExists(note.getUser().getUserID()) ) ) {
                // add user to database
                session.save(note.getUser());
            }

            noteID = (Integer) session.save(note);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return noteID;
    }

    // READ (all fields) - BY ID!
    public Note readNote( int noteID ) {
        String query = "FROM Note n WHERE n.noteID = " + noteID;
        return readNoteByQuery(query);
    }

    // READ (all fields) - BY QUERY!
    private Note readNoteByQuery( String readQuery ) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<Note> notes = (List<Note>)session.createQuery(readQuery).list();
            tx.commit();
            return notes.get(0);
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

    // READ (all notes with all fields)
    public List<Note> readAllNotes( ){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            //List<Note> notes = (List<Note>)session.createQuery("FROM Note n JOIN n.Subject s JOIN n.User u ORDER BY n.noteID").list();
            List<Note> notes = (List<Note>)session.createQuery("FROM Note n ORDER BY n.noteID").list();
            tx.commit();
            return notes;
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
    public void updateNote(Note updatedNote, Integer noteID) {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Note note = (Note)session.get(Note.class, noteID);

            // Przy update'owaniu notki nie wstawiamy nowego usera, ani przedmiotu. Mo¿na tylko zmieniæ odnoœnik do istniej¹cego przedmiotu.

            note.update(updatedNote);
            session.update(note);
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

    // DELETE
    public void deleteNote(Integer noteID) {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            Note note = (Note)session.get(Note.class, noteID);
            session.delete(note);
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

    private boolean subjectExists( int subjectID ) {
        hibernate.controllers.SubjectsController subjectsController = new SubjectsController();
        Subject subject = subjectsController.readSubject(subjectID);
        return subject != null;
    }

    private boolean userExists( int userID ) {
        hibernate.controllers.UsersController usersController = new UsersController();
        User user = usersController.readUser(userID);
        return user != null;
    }

}
