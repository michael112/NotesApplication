package hibernate.mappedclasses;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="\"public\".\"users\"")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID", nullable=false, unique=true)
    private int userID;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "passwordSalt", nullable = false)
    private String passwordSalt;

    @Column(name = "passwordHash", nullable = false)
    private String passwordHash;

    @Column(name = "eMail", nullable = false)
    private String eMail;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "surname", nullable = true)
    private String surname;

    @OneToMany(mappedBy="user")
    private Set<Note> notes;

    public User() {
    }

    public User(String login, String passwordSalt, String passwordHash, String eMail, String name, String surname) {
        this.login = login;
        this.passwordSalt = passwordSalt;
        this.passwordHash = passwordHash;
        this.eMail = eMail;
        this.name = name;
        this.surname = surname;
    }

    public User(int userID, String login, String passwordSalt, String passwordHash, String eMail, String name, String surname) {
        this.userID = userID;
        this.login = login;
        this.passwordSalt = passwordSalt;
        this.passwordHash = passwordHash;
        this.eMail = eMail;
        this.name = name;
        this.surname = surname;
    }

    public void update(User updatedUser) {
        this.login = updatedUser.getLogin();
        this.passwordSalt = updatedUser.getPasswordSalt();
        this.passwordHash = updatedUser.getPasswordHash();
        this.eMail = updatedUser.geteMail();
        this.name = updatedUser.getName();
        this.surname = updatedUser.getSurname();
    }

    public boolean nameIsNull() { return ( ( name == null ) || ( name.length() <= 0 ) ) ? true : false; }

    public boolean surnameIsNull() { return ( ( surname == null ) || ( surname.length() <= 0 ) ) ? true : false; }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }
}
