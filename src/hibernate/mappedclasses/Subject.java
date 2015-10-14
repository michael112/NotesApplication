package hibernate.mappedclasses;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="\"public\".\"subjects\"")
public class Subject implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subjectID", nullable=false, unique=true)
    private int subjectID;

    @Column(name = "subjectName", nullable = false)
    private String subjectName;

    @Column(name = "subjectDescription", nullable = true)
    private String subjectDescription;

    @OneToMany(mappedBy="subject")
    private Set<Note> notes;

    public Subject() {}

    public Subject(String subjectName) {
        this.subjectName = subjectName;
    }

    public Subject(String subjectName, String subjectDescription) {
        this.subjectName = subjectName;
        this.subjectDescription = subjectDescription;
    }

    public void update(Subject updatedSubject) {
        this.subjectName = updatedSubject.getSubjectName();
        this.subjectDescription = updatedSubject.getSubjectDescription();
        this.notes = updatedSubject.getNotes();
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public boolean subjectDescriptionIsNull() { return ( ( subjectDescription == null ) || ( subjectDescription.length() <= 0 ) ) ? true : false; }

    public String getSubjectDescription() {
        return subjectDescription;
    }

    public void setSubjectDescription(String subjectDescription) {
        this.subjectDescription = subjectDescription;
    }

    public Set<Note> getNotes() { return notes; }

    public void setNotes(Set<Note> notes) { this.notes = notes; }
}
