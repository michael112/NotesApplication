package api.login;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.json.*;

import hibernate.mappedclasses.Subject;

import java.util.*;

// DO KLASY DOSTAJEMY SIÊ url'ami: /api/subjects/* (resolve'uje je Jersey, a nie Spring)
@Path("/subjects/")
public class SubjectsController {

    @Context
    private UriInfo context;

    public SubjectsController() {}

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createSubject(String inputData) {
        JSONObject inputJSON = new JSONObject(inputData);
        if( ( inputJSON.has("sessionID") ) && UsersController.checkLogin(inputJSON.getString("sessionID")) ) {
            if (inputJSON.has("subjectID")) inputJSON.remove("subjectID"); // na wszelki wypadek - chcemy generowaæ ID automatycznie
            Subject newSubject = resolveSubjectData(inputJSON);
            hibernate.controllers.SubjectsController hibernateController = new hibernate.controllers.SubjectsController();
            hibernateController.addSubject(newSubject);
        }
    }

    @POST
    @Path("/view")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String viewAllSubjects(String inputData) {
        JSONObject inputJSON = new JSONObject(inputData);
        if ((inputJSON.has("sessionID")) && UsersController.checkLogin(inputJSON.getString("sessionID"))) {
            hibernate.controllers.SubjectsController hibernateController = new hibernate.controllers.SubjectsController();
            List<Subject> subjects = hibernateController.readAllSubjects();
            JSONArray results = new JSONArray();
            JSONObject jsonSubject;
            for( Subject subject : subjects ) {
                jsonSubject = getJSONsubject(subject);
                results.put(jsonSubject);
            }
            return results.toString();
        }
        else return "Invalid session!";
    }

    @POST
    @Path("/view/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String viewSubject(String inputData, @PathParam("id") int id) {
        JSONObject inputJSON = new JSONObject(inputData);
        if ((inputJSON.has("sessionID")) && UsersController.checkLogin(inputJSON.getString("sessionID"))) {
            hibernate.controllers.SubjectsController hibernateController = new hibernate.controllers.SubjectsController();
            Subject subject = hibernateController.readSubject(id);
            if( subject != null ) {
                JSONObject jsonSubject = getJSONsubject(subject);
                return jsonSubject.toString();
            }
            else {
                return "No subject of ID == " + id + "!";
            }
        }
        else return "Invalid session!";
    }

    public static JSONObject getJSONsubject( Subject subject ) {
        JSONObject jsonSubject = new JSONObject();
        jsonSubject.put("subjectID", subject.getSubjectID());
        jsonSubject.put("subjectName", subject.getSubjectName());
        if( !( subject.subjectDescriptionIsNull() ) ) jsonSubject.put("subjectDescription", subject.getSubjectDescription());
        return jsonSubject;
    }

    @PUT
    @Path("/edit")
    @Consumes(MediaType.APPLICATION_JSON)
    public void editSubject(String inputData) {
        JSONObject inputJSON = new JSONObject(inputData);
        if( ( inputJSON.has("sessionID") ) && UsersController.checkLogin(inputJSON.getString("sessionID")) ) {
            if (inputJSON.has("subjectID")) {
                int id = Integer.parseInt(inputJSON.getString("subjectID"));
                editSubject(inputData, id);
            }
            // w przeciwnym wypadku nic nie robi
        }
    }

    @PUT
    @Path("/edit/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void editSubject(String inputData, @PathParam("id") int id) {
        JSONObject inputJSON = new JSONObject(inputData);
        if ((inputJSON.has("sessionID")) && UsersController.checkLogin(inputJSON.getString("sessionID"))) {
            Subject newSubject = resolveSubjectData(inputJSON);
            if (inputJSON.has("subjectID")) inputJSON.remove("subjectID"); // na wszelki wypadek - ID podano
            hibernate.controllers.SubjectsController hibernateController = new hibernate.controllers.SubjectsController();
            hibernateController.updateSubject(newSubject, id);
        }
    }

    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/plain")
    public String deleteSubject(String inputData, @PathParam("id") int id) {
        JSONObject inputJSON = new JSONObject(inputData);
        if ((inputJSON.has("sessionID")) && UsersController.checkLogin(inputJSON.getString("sessionID"))) {
            try {
                hibernate.controllers.SubjectsController hibernateController = new hibernate.controllers.SubjectsController();
                hibernateController.deleteSubject(id);

                return "OK";
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }
        else return "Invalid session!";
    }

    private Subject resolveSubjectData( JSONObject inputJSON ) {
        if( !( inputJSON.has("subjectName") ) ) {
            return null;
        }
        else if( !( inputJSON.has("subjectDescription") ) ) {
            return new Subject(inputJSON.getString("subjectName"));
        }
        else {
            return new Subject(inputJSON.getString("subjectName"), inputJSON.getString("subjectDescription"));
        }
    }

}
