package api.login;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.json.*;

import hibernate.mappedclasses.User;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.MessageDigest;

import java.util.*;

import javax.servlet.http.*; // Dodaæ do "classpath" œcie¿kê: <katalog instalacyjny Tomcata>/lib/servlet-api.jar. (Module Settings -> SKD -> Classpath.)

// DO KLASY DOSTAJEMY SIÊ url'ami: /api/users/* (resolve'uje je Jersey, a nie Spring)
@Path("/users/")
public class UsersController {

    @Context
    private UriInfo context;

    public UsersController() {}

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String loginUser( String inputData, @Context HttpServletRequest request ) {
        JSONObject inputJSON = new JSONObject(inputData);
        JSONObject returnObject = new JSONObject();
        if (inputJSON.has("login") && inputJSON.has("password")) {
            // mo¿na rozwa¿yæ sprawdzanie zahashowanego has³a
            hibernate.controllers.UsersController hibernateController = new hibernate.controllers.UsersController();
            String login = inputJSON.getString("login");
            User user = hibernateController.readUser(login);
            String passwordRaw = inputJSON.getString("password");
            String hashedPassword = encryptPassword(user.getPasswordSalt(), passwordRaw);

            if( hashedPassword.equals(user.getPasswordHash()) ) {
                //return setSessionID();
                returnObject.put("sessionID", setSessionID());
                returnObject.put("message", "login OK");
            }
            else {
                returnObject.put("message", "Inappropriate login and/or password!");
            }
        }
        else {
            returnObject.put("message", "No username / password provided!");
        }
        return returnObject.toString();
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public void createUser(String inputData) {
        JSONObject inputJSON = new JSONObject(inputData);
        if( inputJSON.has("userID") ) inputJSON.remove("userID"); // na wszelki wypadek - chcemy generowaæ ID automatycznie
        User newUser = resolveUserData(inputJSON);
        hibernate.controllers.UsersController hibernateController = new hibernate.controllers.UsersController();
        hibernateController.addUser(newUser);
    }

    @POST
    @Path("/view")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String viewAllUsers(String inputData) {
        JSONObject inputJSON = new JSONObject(inputData);
        if ((inputJSON.has("sessionID")) && UsersController.checkLogin(inputJSON.getString("sessionID"))) {
            hibernate.controllers.UsersController hibernateController = new hibernate.controllers.UsersController();
            List<User> users = hibernateController.readAllUsers();
            JSONArray results = new JSONArray();
            JSONObject jsonUser;
            for( User user : users ) {
                jsonUser = getJSONuser(user);
                results.put(jsonUser);
            }
            return results.toString();
        }
        else return "Invalid session!";
    }

    @POST
    @Path("/view/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String viewUser(String inputData, @PathParam("id") int id) {
        JSONObject inputJSON = new JSONObject(inputData);
        if ((inputJSON.has("sessionID")) && UsersController.checkLogin(inputJSON.getString("sessionID"))) {
            hibernate.controllers.UsersController hibernateController = new hibernate.controllers.UsersController();
            User user = hibernateController.readUser(id);
            if( user != null ) {
                JSONObject jsonUser = getJSONuser(user);
                return jsonUser.toString();
            }
            else {
                return "No subject of ID == " + id + "!";
            }
        }
        else return "Invalid session!";
    }

    public static JSONObject getJSONuser( User user ) {
        JSONObject jsonUser = new JSONObject();
        jsonUser.put("userID", user.getUserID());
        jsonUser.put("login", user.getLogin());
        jsonUser.put("eMail", user.geteMail());
        if( !( user.nameIsNull() ) ) jsonUser.put("name", user.getName());
        if( !( user.surnameIsNull() ) ) jsonUser.put("surname", user.getSurname());
        return jsonUser;
    }

    @PUT
    @Path("/edit")
    @Consumes(MediaType.APPLICATION_JSON)
    public void editUser(String inputData) {
        JSONObject inputJSON = new JSONObject(inputData);
        if( inputJSON.has("userID") ) {
            int id = Integer.parseInt(inputJSON.getString("userID"));
            editUser(inputData, id);
        }
        // w przeciwnym wypadku nic nie robi
    }

    @PUT
    @Path("/edit/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void editUser(String inputData, @PathParam("id") int id) {
            JSONObject inputJSON = new JSONObject(inputData);
            User newUser = resolveUserData(inputJSON);
            if (inputJSON.has("userID")) inputJSON.remove("userID"); // na wszelki wypadek - ID podano
            hibernate.controllers.UsersController hibernateController = new hibernate.controllers.UsersController();
            hibernateController.updateUser(newUser, id);
    }

    @DELETE
    @Path("/{id}")
    @Produces("text/plain")
    public String deleteUser(@PathParam("id") int id) {
        try {
            hibernate.controllers.UsersController hibernateController = new hibernate.controllers.UsersController();
            hibernateController.deleteUser(id);

            return "OK";
        }
        catch( Exception ex ) {
            return ex.getMessage();
        }
    }

    private User resolveUserData(JSONObject inputJSON) {
        String login = inputJSON.getString("login");

        String passwordSalt;
        String passwordHash;

        if( inputJSON.has("passwordSalt") && inputJSON.has("passwordHash") ) {
            passwordSalt = inputJSON.getString("passwordSalt");
            passwordHash = inputJSON.getString("passwordHash");
        }
        else if( inputJSON.has("password") ) {
            Map<String, String> passwordSaltAndHash = encryptPassword(inputJSON.getString("password"));
            passwordSalt = passwordSaltAndHash.get("passwordSalt");
            passwordHash = passwordSaltAndHash.get("passwordHash");
        }
        else {
            passwordSalt = "";
            passwordHash = "";
        }

        String eMail = inputJSON.getString("eMail");
        String name;
        String surname;

        if( inputJSON.has("name") ) {
            name = inputJSON.getString("name");
        }
        else {
            name = "";
        }
        if( inputJSON.has("surname") ) {
            surname = inputJSON.getString("surname");
        }
        else {
            surname = "";
        }

        if( inputJSON.has("userID") ) {
            int userID = Integer.parseInt(inputJSON.getString("userID"));
            return new User( userID, login, passwordSalt, passwordHash, eMail, name, surname );
        }
        else {
            return new User(login, passwordSalt, passwordHash, eMail, name, surname);
        }
    }

    private Map<String,String> encryptPassword( String password ) {
        final int saltLength = 16;

        Map resultMap = new HashMap<String, String>();
        String passwordSalt = generateSalt(saltLength); // needs to be implemented
        String passwordHash = encryptPassword(passwordSalt, password); // needs to be implemented

        resultMap.put("passwordSalt", passwordSalt);
        resultMap.put("passwordHash", passwordHash);

        return resultMap;
    }

    private String encryptPassword( String salt, String rawPassword ) {

        String input = rawPassword.concat(salt);

        String strHashCode = "";

        try {
            MessageDigest objSHA = MessageDigest.getInstance("SHA-512");
            byte[] bytSHA = objSHA.digest(input.getBytes());
            java.math.BigInteger intNumber = new java.math.BigInteger(1, bytSHA);
            strHashCode = intNumber.toString(16);

            // pad with 0 if the hexa digits are less then 128.
            while (strHashCode.length() < 128) {
                strHashCode = "0" + strHashCode;
            }
        }
        catch( NoSuchAlgorithmException ex ) {}

        return strHashCode;
    }

    private String generateSalt(int length) {
        final Random r = new SecureRandom();
        byte[] salt = new byte[length];
        r.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String setSessionID(  ) {
        // TODO
        return "";
    }

    public static boolean checkLogin( String sessionID ) {
        // TODO
        return true;
    }

}
