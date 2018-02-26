package ca.mcgill.ecse321.treeple.service;

import java.sql.Date;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import org.apache.commons.lang3.EnumUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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

    // Get a list of all Trees
    public List<Tree> getAllTrees() {
        return Collections.unmodifiableList(sql.getAllTrees());
    }

    // Get a list of all Users
    public List<User> getAllUsers() {
        return Collections.unmodifiableList(sql.getAllUsers());
    }

    // Create a new Tree
    public Tree createTree(JSONObject treeParams) throws InvalidInputException {
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
        String reports = "";

        if (height < 0)
            throw new InvalidInputException("Height cannot be negative!");
        if (diameter < 0)
            throw new InvalidInputException("Diameter cannot be negative!");
        // if (latitude == null)
        //     throw new InvalidInputException("Latitude cannot be null!");
        // if (longitude == null)
        //     throw new InvalidInputException("Longitude cannot be null!");
        if (datePlanted == null || !datePlanted.matches("^([0-9]{4})-([0-9]{1,2})-([0-9]{1,2})$"))
            throw new InvalidInputException("Date doesn't match YYYY-(M)M-(D)D format!");
        if (!EnumUtils.isValidEnum(Land.class, land))
            throw new InvalidInputException("That land type doesn't exist!");
        if (!EnumUtils.isValidEnum(Status.class, status))
            throw new InvalidInputException("That status doesn't exist!");
        if (!EnumUtils.isValidEnum(Ownership.class, ownership))
            throw new InvalidInputException("That ownership doesn't exist!");


        String address = "sdf";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String gmapsUrl = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.8f,%.8f&key=%s",
                                        latitude, longitude, gmapsKey);
        HttpGet httpGet = new HttpGet(gmapsUrl);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                JSONObject gmapsJSON = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
                address = gmapsJSON.getJSONArray("results").getJSONObject(0).getString("place_id");
            }
            response.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        Location locationObj = new Location(latitude, longitude);
        Species speciesObj = sql.getSpecies(species);
        Municipality municipalityObj = sql.getMunicipality(municipality);

        Tree tree = new Tree(height, diameter, address, Date.valueOf(datePlanted), Land.valueOf(land),
                             Status.valueOf(status), Ownership.valueOf(ownership), speciesObj, locationObj, municipalityObj);

        sql.insertLocation(locationObj.getLocationId(), latitude, longitude);

        sql.insertTree(tree.getTreeId(), height, diameter, address, datePlanted, land,
                       ownership, status, species, locationObj.getLocationId(), municipality, reports);
                    //    species, locationObj.getLocationId(), municipality, tree.getReports().toString().replaceAll("(\\[)|(\\])", ""));

        return tree;
    }

    // Create a new User
    public User createUser(JSONObject userParams) throws InvalidInputException {
        String username = userParams.getString("username");
        String password = userParams.getString("password");
        String role = userParams.getString("role");
        String myAddresses = userParams.getString("myAddresses");
        String myTrees = "";

        if (username == null || username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Username cannot be empty!");
        if (User.hasWithUsername(username))
            throw new InvalidInputException("Username is already taken!");
        if (password == null || password.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Password cannot be empty!");
        if (myAddresses == null || myAddresses.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Address cannot be empty!");
        if (!EnumUtils.isValidEnum(UserRole.class, role))
            throw new InvalidInputException("That role doesn't exist!");

        User user = new User(username, password, UserRole.valueOf(role));

        for (String addressId : myAddresses.split(",")) {
            if (addressId != null && !addressId.replaceAll("\\s", "").isEmpty()) {
                user.addMyAddress(addressId);
            }
        }

        // for (String treeId : myTrees.split(",")) {
        //     if (treeId.matches("^\\d+$") && sql.getTree(Integer.parseInt(treeId)) != null) {
        //         user.addMyTree(Integer.parseInt(treeId));
        //     }
        // }

        sql.insertUser(username, password, role, myAddresses, myTrees);

        return user;
    }

    // Delete a Tree
    public Tree deleteTree(JSONObject treeParams) throws InvalidInputException {
        int treeId = treeParams.getInt("treeId");

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


    // public Location createLocation(String id, String name, String strtNum, String address, int qTime, JSONObject checkTimes) throws InvalidInputException {
    //     if (id == null || name.trim().length() == 0)
    //         throw new InvalidInputException("Location name cannot be empty!");
    //     if (name == null || name.trim().length() == 0)
    //         throw new InvalidInputException("Location name cannot be empty!");
    //     if (strtNum == null || strtNum.trim().length() == 0)
    //         throw new InvalidInputException("Location street number cannot be empty!");
    //     if (address == null || address.trim().length() == 0)
    //         throw new InvalidInputException("Location address cannot be empty!");
    //     if (qTime < -1)
    //         throw new InvalidInputException("Location queue times cannot be negative!");
    //     if (checkTimes == null)
    //         throw new InvalidInputException("Location check times cannot be null!");

    //     Location l = new Location(id, name, strtNum, address, qTime, checkTimes);
    //     for (Location location : rm.getLocations()) {
    //         if (location.getId().equals(l.getId()) && location.getName().equals(l.getName())
    //             && location.getStrtNum().equals(l.getStrtNum()) && location.getAddress().equals(l.getAddress())
    //             && location.getQTime() == l.getQTime() && location.getCheckTimes().toString().equals(l.getCheckTimes().toString())) {
    //             throw new InvalidInputException("Cannot create identical locations!");
    //         }
    //     }

    //     rm.addLocation(l);
    //     sql.insertLocation(l.getId(), l.getName(), l.getStrtNum(), l.getAddress(), l.getQTime(), l.getCheckTimes().toString());
    //     return l;
    // }

    // public User getUserByName(String username) throws InvalidInputException {
    //     for (User user : rm.getUsers()) {
    //         if (user.getUsername().equals(username))
    //             return user;
    //     }
    //     throw new InvalidInputException("User does not exist!");
    // }

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

    // Delete the database
    public void resetDatabase() {
        sql.deleteDB();
    }

}
