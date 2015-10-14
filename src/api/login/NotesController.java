package api.login;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.json.*;

import hibernate.mappedclasses.*;

import java.util.*;

// DO KLASY DOSTAJEMY SIÊ url'ami: /api/notes/* (resolve'uje je Jersey, a nie Spring)
@Path("/notes/")
public class NotesController {

    @Context
    private UriInfo context;

    public NotesController() {}

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createNote(String inputData) {
        JSONObject inputJSON = new JSONObject(inputData);
        if( ( inputJSON.has("sessionID") ) && UsersController.checkLogin(inputJSON.getString("sessionID")) ) {
            if (inputJSON.has("noteID")) inputJSON.remove("noteID"); // na wszelki wypadek - chcemy generowaæ ID automatycznie
            Note newNote = resolveNoteData(inputJSON);
            hibernate.controllers.NotesController notesController = new hibernate.controllers.NotesController();
            if( newNote != null ) notesController.addNote(newNote);
        }
    }

    @POST
    @Path("/view")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String viewAllNotes(String inputData) {
        JSONObject inputJSON = new JSONObject(inputData);
        if ((inputJSON.has("sessionID")) && UsersController.checkLogin(inputJSON.getString("sessionID"))) {
            hibernate.controllers.NotesController notesController = new hibernate.controllers.NotesController();
            List<Note> notes = notesController.readAllNotes();
            JSONArray results = new JSONArray();
            JSONObject jsonNote;
            for( Note note : notes ) {
                jsonNote = getJSONNote(note);
                results.put(jsonNote);
            }
            return results.toString();
        }
        else return "Invalid session!";
    }

    @POST
    @Path("/view/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String viewNote(String inputData, @PathParam("id") int id) {
        JSONObject inputJSON = new JSONObject(inputData);
        if ((inputJSON.has("sessionID")) && UsersController.checkLogin(inputJSON.getString("sessionID"))) {
            hibernate.controllers.NotesController notesController = new hibernate.controllers.NotesController();
            Note note = notesController.readNote(id);
            if( note != null ) {
                JSONObject jsonNote = getJSONNote(note);
                return jsonNote.toString();
            }
            else {
                return "No subject of ID == " + id + "!";
            }
        }
        else return "Invalid session!";
    }

    private JSONObject getJSONNote( Note note ) {
        JSONObject jsonNote = new JSONObject();
        jsonNote.put("noteID", note.getNoteID());
        jsonNote.put("noteTitle", note.getNoteTitle());
        jsonNote.put("noteURL", note.getNoteURL());

        JSONObject jsonSubject = api.login.SubjectsController.getJSONsubject( note.getSubject()  );
        jsonNote.put("subject", jsonSubject);

        JSONObject jsonUser = api.login.UsersController.getJSONuser( note.getUser() );
        jsonNote.put("user", jsonUser);

        return jsonNote;
    }

    @PUT
    @Path("/edit")
    @Consumes(MediaType.APPLICATION_JSON)
    public void editNote(String inputData) {
        JSONObject inputJSON = new JSONObject(inputData);
        if( ( inputJSON.has("sessionID") ) && UsersController.checkLogin(inputJSON.getString("sessionID")) ) {
            if (inputJSON.has("noteID")) {
                int id = Integer.parseInt(inputJSON.getString("noteID"));
                editNote(inputData, id);
            }
            // w przeciwnym wypadku nic nie robi
        }
    }

    @PUT
    @Path("/edit/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void editNote(String inputData, @PathParam("id") int id) {
        JSONObject inputJSON = new JSONObject(inputData);
        if ((inputJSON.has("sessionID")) && UsersController.checkLogin(inputJSON.getString("sessionID"))) {
            Note newNote = resolveNoteData(inputJSON);
            if (inputJSON.has("noteID")) inputJSON.remove("noteID"); // na wszelki wypadek - ID podano
            hibernate.controllers.NotesController notesController = new hibernate.controllers.NotesController();
            if( newNote != null ) notesController.updateNote(newNote, id);
        }
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/plain")
    public String deleteNote(String inputData, @PathParam("id") int id) {
        JSONObject inputJSON = new JSONObject(inputData);
        if ((inputJSON.has("sessionID")) && UsersController.checkLogin(inputJSON.getString("sessionID"))) {
            try {
                hibernate.controllers.NotesController notesController = new hibernate.controllers.NotesController();
                notesController.deleteNote(id);

                return "OK";
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }
        else return "Invalid session!";
    }

    private Note resolveNoteData( JSONObject inputJSON ) {
        if( !( ( inputJSON.has("noteTitle") ) && ( inputJSON.has("noteURL") ) && ( inputJSON.has("subjectID") ) && ( inputJSON.has("userID") ) ) ) {
            return null;
        }
        else {
            try {
                hibernate.controllers.SubjectsController subjectsController = new hibernate.controllers.SubjectsController();
                Subject subject = subjectsController.readSubject(Integer.parseInt(inputJSON.get("subjectID").toString()));

                hibernate.controllers.UsersController usersController = new hibernate.controllers.UsersController();
                User user = usersController.readUser(Integer.parseInt(inputJSON.get("userID").toString()));

                return new Note(inputJSON.getString("noteTitle"), inputJSON.getString("noteURL"), subject, user);
            }
            catch( NullPointerException ex ) {
                return null;
            }
        }
    }

}
