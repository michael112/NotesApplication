package hibernate.mappedclasses;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name="\"public\".\"notes\"")
public class Note implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noteID", nullable=false, unique=true)
    private int noteID;
    @Column(name = "noteTitle", nullable = false)
    private String noteTitle;
    @Column(name = "noteURL", nullable = false)
    private String noteURL;

    @ManyToOne
    @JoinColumn(name="subjectID")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name="userID")
    private User user;

    public Note() {}

    public Note( String noteTitle, String noteURL, Subject subject, User user ) {
        this.noteTitle = noteTitle;
        this.noteURL = noteURL;
        this.subject = subject;
        this.user = user;
    }

    public void update(Note updatedNote) {
        this.noteTitle = updatedNote.getNoteTitle();
        this.noteURL = updatedNote.getNoteURL();
        this.subject = updatedNote.getSubject();
        this.user = updatedNote.getUser();
    }

    public int getNoteID() {
        return noteID;
    }

    public void setNoteID(int noteID) {
        this.noteID = noteID;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteURL() {
        return noteURL;
    }

    public void setNoteURL(String noteURL) {
        this.noteURL = noteURL;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
