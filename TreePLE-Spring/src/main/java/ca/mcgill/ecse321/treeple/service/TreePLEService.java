package ca.mcgill.ecse321.treeple.service;

import java.sql.Date;
import java.util.*;

import org.json.*;
import org.apache.commons.lang3.EnumUtils;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.model.Tree.*;
import ca.mcgill.ecse321.treeple.model.User.*;
import ca.mcgill.ecse321.treeple.sqlite.SQLiteJDBC;

@Service
public class TreePLEService {
    private SQLiteJDBC sql;
    private final String gmapsKey = "AIzaSyDzb0p2lAcypZ2IbhVyhJYu6rTQLPncY5g";

    public TreePLEService(SQLiteJDBC sql) {
        this.sql = sql;
    }

    // ==============================
    // CREATE API
    // ==============================

    // Create a new Tree
    public Tree createTree(JSONObject jsonParams) throws InvalidInputException {
        // User data
        String username = jsonParams.getString("user");

        // Tree data
        JSONObject treeParams = jsonParams.getJSONObject("tree");
        int height = treeParams.getInt("height");
        int diameter = treeParams.getInt("diameter");
        String datePlanted = treeParams.getString("datePlanted");
        String land = treeParams.getString("land");
        String status = treeParams.getString("status");
        String ownership = treeParams.getString("ownership");
        String species = treeParams.getString("species");
        Double latitude = treeParams.getDouble("latitude");
        Double longitude = treeParams.getDouble("longitude");
        String municipality = treeParams.getString("municipality");


        if (height < 0)
            throw new InvalidInputException("Height cannot be negative!");
        if (diameter < 0)
            throw new InvalidInputException("Diameter cannot be negative!");
        if (datePlanted == null || !datePlanted.matches("^([0-9]{4})-([0-9]{1,2})-([0-9]{1,2})$"))
            throw new InvalidInputException("Date doesn't match YYYY-(M)M-(D)D format!");
        if (username == null || username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("User is not logged in/Missing username!");
        if (!EnumUtils.isValidEnum(Land.class, land))
            throw new InvalidInputException("That land type doesn't exist!");
        if (!EnumUtils.isValidEnum(Status.class, status))
            throw new InvalidInputException("That status doesn't exist!");
        if (!EnumUtils.isValidEnum(Ownership.class, ownership))
            throw new InvalidInputException("That ownership doesn't exist!");

        String address = "";
        try {
            String gmapsUrl = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.8f,%.8f&key=%s",
                                            latitude, longitude, gmapsKey);
            CloseableHttpResponse response = HttpClients.createDefault().execute(new HttpGet(gmapsUrl));
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                JSONObject gmapsJSON = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
                address = gmapsJSON.getJSONArray("results").getJSONObject(0).getString("place_id");
            }
            response.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        User userObj;
        Species speciesObj;
        Municipality municipalityObj;
        if ((userObj = sql.getUser(username)) == null)
            throw new InvalidInputException("User does not exist!");
        if ((speciesObj = sql.getSpecies(species)) == null)
            throw new InvalidInputException("Species does not exist!");
        if ((municipalityObj = sql.getMunicipality(municipality)) == null)
            throw new InvalidInputException("Municipality does not exist!");

        Location locationObj = new Location(latitude, longitude);
        SurveyReport surveyReportObj = new SurveyReport(Date.valueOf(datePlanted), username);

        Tree tree = new Tree(height, diameter, address, Date.valueOf(datePlanted), Land.valueOf(land),
                             Status.valueOf(status), Ownership.valueOf(ownership), speciesObj, locationObj, municipalityObj);

        tree.addReport(surveyReportObj);
        sql.insertTree(tree.getTreeId(), height, diameter, address, datePlanted, land, status, ownership, species,
                       locationObj.getLocationId(), municipality, Integer.toString(surveyReportObj.getReportId()));

        sql.insertLocation(locationObj.getLocationId(), latitude, longitude);
        sql.insertSurveyReport(surveyReportObj.getReportId(), surveyReportObj.getReportDate().toString(), username);

        userObj.addMyTree(locationObj.getLocationId());
        sql.updateUserTrees(username, userObj.getMyTrees().toString().replaceAll("(\\[)|(\\])", ""));

        return tree;
    }

    // Create a new User
    public User createUser(JSONObject jsonParams) throws InvalidInputException {
        String username = jsonParams.getString("username");
        String password = jsonParams.getString("password");
        String role = jsonParams.getString("role");
        String myAddresses = jsonParams.getString("myAddresses");
        String myTrees = "";

        if (username == null || username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Username cannot be empty!");
        if (User.hasWithUsername(username))
            throw new InvalidInputException("Username is already taken!");
        if (password == null || password.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Password cannot be empty!");
        if (!EnumUtils.isValidEnum(UserRole.class, role))
            throw new InvalidInputException("That role doesn't exist!");
        if (role.equals("Residential") && (myAddresses == null || myAddresses.replaceAll("\\s", "").isEmpty()))
            throw new InvalidInputException("Address cannot be empty!");

        User user = new User(username, password, UserRole.valueOf(role));

        for (String addressId : myAddresses.split(",")) {
            if (addressId != null && !addressId.replaceAll("\\s", "").isEmpty()) {
                user.addMyAddress(addressId);
            }
        }

        sql.insertUser(username, password, role, myAddresses, myTrees);

        return user;
    }

    // Create a new Species
    public Species createSpecies(JSONObject jsonParams) throws InvalidInputException {
        String name = jsonParams.getString("name");
        String species = jsonParams.getString("species");
        String genus = jsonParams.getString("genus");

        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Species cannot be empty!");
        if (Species.hasWithName(name))
            throw new InvalidInputException("Species already exists!");

        Species speciesObj = new Species(name, species, genus);

        sql.insertSpecies(name, species, genus);

        return speciesObj;
    }


    // Create a new Municipality
    public Municipality createMunicipality(JSONObject jsonParams) throws InvalidInputException {
        String name = jsonParams.getString("name");
        int totalTrees = jsonParams.getInt("totalTrees");
        JSONArray borders = jsonParams.getJSONArray("borders");

        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Municipality cannot be empty!");
        if (Municipality.hasWithName(name))
            throw new InvalidInputException("Municipality already exists!");
        if (borders.length() < 3)
            throw new InvalidInputException("Municipality requires minimum 3 borders!");

        Municipality municipality = new Municipality(name, totalTrees);

        ArrayList<Integer> locationIdList = new ArrayList<>();
        borders.forEach(border -> {
            JSONArray locationJSON = (JSONArray) border;
            Location location = new Location(locationJSON.getDouble(0), locationJSON.getDouble(1));
            municipality.addBorder(location);
            locationIdList.add(location.getLocationId());
        });

        sql.insertMunicipality(name, totalTrees, locationIdList.toString().replaceAll("(\\[)|(\\])", ""));

        for (Location location : municipality.getBorders()) {
            sql.insertLocation(location.getLocationId(), location.getLatitude(), location.getLongitude());
        }

        return municipality;
    }


    // ==============================
    // GET ALL API
    // ==============================

    // Get a list of all Trees
    public List<Tree> getAllTrees() {
        return Collections.unmodifiableList(sql.getAllTrees());
    }

    // Get a list of all Users
    public List<User> getAllUsers() {
        return Collections.unmodifiableList(sql.getAllUsers());
    }

    // Get a list of all Species
    public List<Species> getAllSpecies() {
        return Collections.unmodifiableList(sql.getAllSpecies());
    }

    // Get a list of all Locations
    public List<Location> getAllLocations() {
        return Collections.unmodifiableList(sql.getAllLocations());
    }

    // Get a list of all Municipalities
    public List<Municipality> getAllMunicipalities() {
        return Collections.unmodifiableList(sql.getAllMunicipalities());
    }

    // Get a list of all Survey Reports
    public List<SurveyReport> getAllSurveyReports() {
        return Collections.unmodifiableList(sql.getAllSurveyReports());
    }


    // ==============================
    // GET API
    // ==============================

    // Get a specific Tree
    public Tree getTreeById(int treeId) throws InvalidInputException {
        if (treeId <= 0)
            throw new InvalidInputException("Tree ID cannot be negative!");

        return sql.getTree(treeId);
    }

    // Get a specific User
    public User getUserByUsername(String username) throws InvalidInputException {
        if (username == null || username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Username cannot be empty!");

        if (User.hasWithUsername(username)) {
            return User.getWithUsername(username);
        } else {
            return sql.getUser(username);
        }
    }


    // ==============================
    // DELETE API
    // ==============================

    // Delete a Tree
    public Tree deleteTree(JSONObject jsonParams) throws InvalidInputException {
        int treeId = jsonParams.getInt("treeId");

        if (treeId <= 0)
            throw new InvalidInputException("Tree's ID cannot be negative or zero!");

        Tree tree = sql.getTree(treeId);

        if (tree != null) {
            sql.deleteTree(treeId);
        } else {
            throw new InvalidInputException("No Tree with that ID exists!");
        }

        return tree;
    }

    // Delete the database
    public boolean resetDatabase() {
        return sql.deleteDB();
    }

    // public Location getLocationById(String id) throws InvalidInputException {
    //     for (Location location : rm.getLocations()) {
    //         if (location.getId().equals(id))
    //             return location;
    //     }
    //     throw new InvalidInputException("Location does not exist!");
    // }

    // public User updateUserPoints(User u, int points) throws InvalidInputException {
    //     int newPoints = u.getPoints() + points;

    //     rm.getRMUsers().get(rm.indexOfUser(u)).setPoints(newPoints);
    //     sql.updateUserPoints(u.getId(), newPoints);
    //     u.setPoints(newPoints);
    //     return u;
    // }

    // public User updateUserPassword(User u, String password) throws InvalidInputException {
    //     rm.getRMUsers().get(rm.indexOfUser(u)).setPassword(password);
    //     sql.updateUserPassword(u.getId(), password);
    //     u.setPassword(password);
    //     return u;
    // }

    // public User updateLocationPassword(User u, String password) throws InvalidInputException {
    //     rm.getRMUsers().get(rm.indexOfUser(u)).setPassword(password);
    //     sql.updateUserPassword(u.getId(), password);
    //     u.setPassword(password);
    //     return u;
    // }

    // public Location updateLocationCheckIn(String id, String username, String checkIn) throws InvalidInputException {
    //     Location l = getLocationById(id);
    //     JSONObject checkTimes = sql.updateLocationCheckIn(id, username, checkIn);
    //     rm.getRMLocations().get(rm.indexOfLocation(l)).setCheckTimes(checkTimes);
    //     l.setCheckTimes(checkTimes);
    //     return l;
    // }

    // public Location updateLocationCheckOut(String id, String username, String checkOut) throws InvalidInputException {
    //     Location l = getLocationById(id);
    //     JSONObject checkTimes = sql.updateLocationCheckOut(id, username, checkOut);
    //     rm.getRMLocations().get(rm.indexOfLocation(l)).setCheckTimes(checkTimes);
    //     l.setCheckTimes(checkTimes);
    //     return l;
    // }
}
