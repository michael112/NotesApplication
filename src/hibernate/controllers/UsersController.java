package hibernate.controllers;

import hibernate.mappedclasses.User;

import java.util.*;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Criteria;
import org.hibernate.cfg.Configuration;
import hibernate.config.*;

public class UsersController {

    private static SessionFactory factory;

    public UsersController() {
        try{
            //this.factory = new Configuration().configure().buildSessionFactory();
            this.factory = HibernateUtil.getSessionFactory();
        }
        catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    // CREATE
    public Integer addUser( User user ) {
        if( !(loginExists(user.getLogin())) ) {
            Session session = factory.openSession();
            //Session session = factory.getCurrentSession();
            Transaction tx = null;
            Integer userID = null;
            try {
                tx = session.beginTransaction();
                userID = (Integer) session.save(user);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            } finally {
                session.close();
            }
            return userID;
        }
        else {
            return null;
        }
    }

    // READ (specific fields - Scalar Query)
    public List<User> readUserBySpecificQuery(String readQuery) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            SQLQuery query = session.createSQLQuery(readQuery);
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            List<Map> data = (List<Map>)query.list();
            User[] users = new User[data.size()];
            User user;
            int i = 0;
            for( Map row : data )
            {
                user = new User();
                if( row.containsKey("userID") ) {
                    user.setUserID(Integer.parseInt(row.get("userID").toString()));
                }
                if( row.containsKey("login") ) {
                    user.setLogin(row.get("login").toString());
                }
                if( row.containsKey("passwordSalt") ) {
                    user.setPasswordSalt(row.get("passwordSalt").toString());
                }
                if( row.containsKey("passwordHash") ) {
                    user.setPasswordHash(row.get("passwordHash").toString());
                }
                if( row.containsKey("eMail") ) {
                    user.seteMail(row.get("eMail").toString());
                }
                if( row.containsKey("name") ) {
                    user.setName(row.get("name").toString());
                }
                if( row.containsKey("surname") ) {
                    user.setSurname(row.get("surname").toString());
                }

                users[i] = user;
                i++;
            }
            List<User> usersList = Arrays.asList(users);

            tx.commit();

            return usersList;
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

    // READ (all fields) - BY ID!
    public User readUser( int userID ) {
        String query = "FROM User WHERE userID = " + userID;
        return readUserByQuery(query);
    }

    // READ (all fields) - BY LOGIN!
    public User readUser( String userLogin ) {
        String query = "FROM User WHERE login = '" + userLogin + "'";
        return readUserByQuery(query);
    }

    // READ (all fields) - BY QUERY!
    private User readUserByQuery( String readQuery ) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<User> users = (List<User>)session.createQuery(readQuery).list();
            tx.commit();
            return users.get(0);
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

    // READ (all users with all fields)
    public List<User> readAllUsers( ){
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List<User> users = (List<User>)session.createQuery("FROM User u ORDER BY u.userID").list();
            tx.commit();
            return users;
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
    public void updateUser(User updatedUser, Integer UserID) {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            User user = (User)session.get(User.class, UserID);
            user.update(updatedUser);
            session.update(user);
            tx.commit();
        }catch (HibernateException e) {
            if (tx!=null) tx.rollback();
            e.printStackTrace();
        }finally {
            session.close();
        }
    }

    // DELETE
    public void deleteUser(Integer UserID) {
        Session session = factory.openSession();
        Transaction tx = null;
        try{
            tx = session.beginTransaction();
            User user = (User)session.get(User.class, UserID);
            session.delete(user);
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

    private boolean loginExists( String login ) {
        List<User> users = readAllUsers();
        for( User user: users ) {
            if( user.getLogin().equals(login) ) {
                return true;
            }
        }
        return false;
    }

}
