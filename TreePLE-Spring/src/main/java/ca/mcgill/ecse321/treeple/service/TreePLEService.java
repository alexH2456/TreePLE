package ca.mcgill.ecse321.treeple.service;

import java.sql.Date;
import java.sql.SQLException;
import java.util.*;

import org.apache.commons.lang3.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.*;
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

    public boolean setMaxId() {
        return Tree.setNextTreeId(sql.getMaxTreeId() + 1) &&
               Location.setNextLocationId(sql.getMaxLocationId() + 1) &&
               SurveyReport.setNextReportId(sql.getMaxReportId() + 1);
    }

    public boolean reduceMaxId() {
        return Tree.setNextTreeId(Tree.getNextTreeId() - 1) &&
               Location.setNextLocationId(Location.getNextLocationId() - 1) &&
               SurveyReport.setNextReportId(SurveyReport.getNextReportId() - 1);
    }

    // ==============================
    // CREATE API
    // ==============================

    // Create a new Tree
    public Tree createTree(JSONObject jsonParams) throws Exception {
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
            throw new InvalidInputException("User is not logged in/Username is missing!");
        if (!EnumUtils.isValidEnum(Land.class, land))
            throw new InvalidInputException("That land type doesn't exist!");
        if (!EnumUtils.isValidEnum(Status.class, status))
            throw new InvalidInputException("That status doesn't exist!");
        if (!EnumUtils.isValidEnum(Ownership.class, ownership))
            throw new InvalidInputException("That ownership doesn't exist!");

        String address = "";
        String postalCode = "";
        try {
            String gmapsUrl = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%.8f,%.8f&key=%s",
                                            latitude, longitude, gmapsKey);
            CloseableHttpResponse response = HttpClients.createDefault().execute(new HttpGet(gmapsUrl));
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 200 && statusCode < 300) {
                JSONObject gmapsJSON = new JSONObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
                JSONArray addressInfo = gmapsJSON.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
                address = gmapsJSON.getJSONArray("results").getJSONObject(0).getString("place_id");
                postalCode = addressInfo.getJSONObject(addressInfo.length() - 1).getString("long_name").replaceAll("\\s", "");
            } else if (statusCode >= 400) {
                throw new InvalidInputException("Invalid Google Maps API request!");
            }
            response.close();
        } catch (InvalidInputException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        User userObj;
        Species speciesObj;
        Municipality municipalityObj;
        if ((userObj = sql.getUser(username)) == null)
            throw new InvalidInputException("User does not exist!");
        if (userObj.getRole() == UserRole.Resident && !ArrayUtils.contains(userObj.getMyAddresses(), postalCode))
            throw new InvalidInputException("You cannot plant on someone else's property!");
        if ((speciesObj = sql.getSpecies(species)) == null)
            throw new InvalidInputException("Species does not exist!");
        if ((municipalityObj = sql.getMunicipality(municipality)) == null)
            throw new InvalidInputException("Municipality does not exist!");

        Location locationObj = new Location(latitude, longitude);
        SurveyReport surveyReportObj = new SurveyReport(Date.valueOf(datePlanted), username);

        Tree treeObj = new Tree(height, diameter, address, Date.valueOf(datePlanted), Land.valueOf(land),
                                Status.valueOf(status), Ownership.valueOf(ownership), speciesObj, locationObj, municipalityObj);

        treeObj.addReport(surveyReportObj);
        if (!sql.insertTree(treeObj.getTreeId(), height, diameter, address, datePlanted, land, status, ownership, species,
                            locationObj.getLocationId(), municipality, Integer.toString(surveyReportObj.getReportId()))) {
            reduceMaxId();
            throw new SQLException("SQL Tree insert query failed!");
        }

        if (!sql.insertLocation(locationObj.getLocationId(), latitude, longitude)) {
            reduceMaxId();
            throw new SQLException("SQL Location insert query failed!");
        }

        if (!sql.insertSurveyReport(surveyReportObj.getReportId(), surveyReportObj.getReportDate().toString(), username)) {
            reduceMaxId();
            throw new SQLException("SQL Survey Report insert query failed!");
        }

        userObj.addMyTree(treeObj.getTreeId());
        sql.updateUserTrees(username, userObj.getMyTrees().toString().replaceAll("(\\[)|(\\])", ""));

        return treeObj;
    }

    // Create a new User
    public User createUser(JSONObject jsonParams) throws Exception {
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
        if (role.equals("Resident") && (myAddresses == null || myAddresses.replaceAll("\\s", "").isEmpty()))
            throw new InvalidInputException("Address cannot be empty!");

        User user = new User(username, password, UserRole.valueOf(role));

        for (String addressId : myAddresses.split(",")) {
            if (addressId != null && !addressId.replaceAll("\\s", "").isEmpty()) {
                user.addMyAddress(addressId.replaceAll("\\s", ""));
            }
        }

        if (!sql.insertUser(username, password, role, myAddresses, myTrees)) {
            user.delete();
            throw new SQLException("SQL User insert query failed!");
        }

        return user;
    }

    // Create a new Species
    public Species createSpecies(JSONObject jsonParams) throws Exception {
        String name = jsonParams.getString("name");
        String species;
        String genus;
        try {
            species = jsonParams.getString("species");
        } catch (JSONException e) {
            species = "";
        }
        try {
            genus = jsonParams.getString("genus");
        } catch (JSONException e) {
            genus = "";
        }

        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Species cannot be empty!");
        if (Species.hasWithName(name))
            throw new InvalidInputException("Species already exists!");

        Species speciesObj = new Species(name, species, genus);

        if (!sql.insertSpecies(name, species, genus)) {
            speciesObj.delete();
            throw new SQLException("SQL Species insert query failed!");
        }

        return speciesObj;
    }


    // Create a new Municipality
    public Municipality createMunicipality(JSONObject jsonParams) throws Exception {
        String name = jsonParams.getString("name");
        int totalTrees = jsonParams.getInt("totalTrees");
        JSONArray borders = jsonParams.getJSONArray("borders");

        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Municipality cannot be empty!");
        if (Municipality.hasWithName(name))
            throw new InvalidInputException("Municipality already exists!");
        if (borders.length() > 0 && borders.length() < 3)
            throw new InvalidInputException("Municipality requires minimum 3 borders!");

        Municipality municipality = new Municipality(name, totalTrees);

        ArrayList<Integer> locationIdList = new ArrayList<>();
        borders.forEach(border -> {
            JSONArray locationJSON = (JSONArray) border;
            Location location = new Location(locationJSON.getDouble(0), locationJSON.getDouble(1));
            municipality.addBorder(location);
            locationIdList.add(location.getLocationId());
        });

        if (!sql.insertMunicipality(name, totalTrees, locationIdList.toString().replaceAll("(\\[)|(\\])", ""))) {
            Location.setNextLocationId(Location.getNextLocationId() - municipality.numberOfBorders());
            municipality.delete();
            throw new SQLException("SQL Municipality insert query failed!");
        }

        for (Location location : municipality.getBorders()) {
            if (!sql.insertLocation(location.getLocationId(), location.getLatitude(), location.getLongitude())) {
                Location.setNextLocationId(Location.getNextLocationId() - municipality.numberOfBorders());
                municipality.delete();
                throw new SQLException("SQL Location insert query failed!");
            }
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
    public Tree getTreeById(int treeId) throws Exception {
        if (treeId <= 0)
            throw new InvalidInputException("Tree's ID cannot be negative!");

        Tree tree;
        if ((tree = sql.getTree(treeId)) == null)
            throw new InvalidInputException("No Tree with that ID exists!");

        return tree;
    }

    // Get a specific User
    public User getUserByUsername(String username) throws Exception {
        if (username == null || username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Username cannot be empty!");

        if (User.hasWithUsername(username)) {
            return User.getWithUsername(username);
        } else {
            User user;
            if ((user = sql.getUser(username)) == null)
                throw new InvalidInputException("That username doesn't exist!");

            return user;
        }
    }

    // Get a specific Location
    public Location getLocationById(int locationId) {

    }

    // Get a specific Municipality
    public Municipality getMunicipalityByName(String name) throws Exception {
        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Name cannot be empty!");

        if (Municipality.hasWithName(name)) {
            return Municipality.getWithName(name);
        } else {
            Municipality municipality;
            if ((municipality = sql.getMunicipality(name)) == null)
                throw new InvalidInputException("No Municipality with that name exists!");

            return municipality;
        }
    }

    // Get a specific Survey Report
    public SurveyReport getSurveyReportById(int reportId) {

    }


    // ==============================
    // UPDATE API
    // ==============================

    // Update a Tree
    public Tree updateTree(JSONObject jsonParams) throws Exception {

    }

    // Update a User
    public Tree updateUser(JSONObject jsonParams) throws Exception {

    }

    // Update a Species
    public Tree updateSpecies(JSONObject jsonParams) throws Exception {

    }

    // Update a Municipality
    public Tree updateMunicipality(JSONObject jsonParams) throws Exception {

    }


    // ==============================
    // DELETE API
    // ==============================

    // Delete a Tree
    public Tree deleteTree(JSONObject jsonParams) throws Exception {
        String username = jsonParams.getString("user");
        int treeId = jsonParams.getInt("treeId");

        if (treeId <= 0)
            throw new InvalidInputException("Tree's ID cannot be negative or zero!");
        if (username == null || username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("User is not logged in/Username is missing!");

        Tree tree = sql.getTree(treeId);
        User user = sql.getUser(username);

        if (tree == null)
            throw new InvalidInputException("No Tree with that ID exists!");
        if (user == null)
            throw new InvalidInputException("That username doesn't exist!");
        if (UserRole.Resident == user.getRole() && !ArrayUtils.contains(user.getMyTrees(), tree.getTreeId()))
            throw new InvalidInputException("This Tree wasn't planted by you!");

        if (!sql.deleteTree(treeId))
            throw new SQLException("SQL Tree delete query failed!");

        Tree.setNextTreeId(Tree.getNextTreeId() - 1);

        return tree;
    }

    // Delete a User
    public User deleteUser(JSONObject jsonParams) throws Exception {
        String username = jsonParams.getString("username");

        if (username == null || username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("User is not logged in/Username is missing!");

        User user = sql.getUser(username);

        if (user == null)
            throw new InvalidInputException("That username doesn't exist!");

        user.delete();
        if (!sql.deleteUser(username))
            throw new SQLException("SQL User delete query failed!");

        return user;
    }

    // Delete a Species
    public Species deleteSpecies(JSONObject jsonParams) throws Exception {
        String name = jsonParams.getString("name");

        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Species' name is missing!");

        Species species = sql.getSpecies(name);

        if (species == null)
            throw new InvalidInputException("No Species with that name exists!");

        species.delete();
        if (!sql.deleteSpecies(name))
            throw new SQLException("SQL Species delete query failed!");

        return species;
    }

    // Delete a Location
    public Location deleteLocation(JSONObject jsonParams) throws Exception {
        int locationId = jsonParams.getInt("locationId");

        if (locationId <= 0)
            throw new InvalidInputException("Location's ID cannot be negative or zero!");

        Location location = sql.getLocation(locationId);

        if (location == null)
            throw new InvalidInputException("No Location with that ID exists!");

        if (!sql.deleteLocation(locationId))
            throw new SQLException("SQL Location delete query failed!");

        Location.setNextLocationId(Location.getNextLocationId() - 1);

        return location;
    }

    // Delete a Municipality
    public Municipality deleteMunicipality(JSONObject jsonParams) throws Exception {
        String name = jsonParams.getString("name");

        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Municipality's name is missing!");

        Municipality municipality = sql.getMunicipality(name);

        if (municipality == null)
            throw new InvalidInputException("No Municipality with that name exists!");

        municipality.delete();
        if (!sql.deleteMunicipality(name))
            throw new SQLException("SQL Municipality delete query failed!");

        return municipality;
    }


    // ==============================
    // DATABASE RESET API
    // ==============================

    // Reset the database
    public void resetDatabase() throws Exception {
        if (!User.clearUsers() || !Species.clearSpecies() || !Municipality.clearMunicipalities())
            throw new SQLException("Unable to reset SQL database!");

        if (!Tree.setNextTreeId(1) || !Location.setNextLocationId(1) || !SurveyReport.setNextReportId(1) || !sql.resetDB()) {
            setMaxId();
            throw new SQLException("Unable to reset SQL database!");
        }
    }

    // Delete the database
    public void deleteDatabase() throws Exception {
        if (!User.clearUsers() || !Species.clearSpecies() || !Municipality.clearMunicipalities())
            throw new SQLException("Unable to delete SQL database!");

        if (!Tree.setNextTreeId(1) || !Location.setNextLocationId(1) || !SurveyReport.setNextReportId(1) || !sql.deleteDB()) {
            setMaxId();
            throw new SQLException("Unable to delete SQL database!");
        }
    }


    // ==============================
    // SUSTAINABILITY ATTRIBUTES
    // ==============================

    // Get the approximate age of the tree using height and diameter (in years)
    public int getAgeOfTree(Tree tree) throws Exception {
        if (tree == null)
            throw new InvalidInputException("Tree cannot be null!");

        double diameter = cmToInches(tree.getDiameter());

        // Average growth rate of a tree is about 6 yrs/inch
        return (int) Math.round(6*diameter);
    }

    // Returns the weight of the tree in kg
    public double getWeightOfTree(Tree tree) throws Exception {
        if (tree == null)
            throw new InvalidInputException("Tree cannot be null!");

        int height = tree.getHeight();
        int diameter = tree.getDiameter();
        double weight = 0;

        // A rough estimation of a weight for trees (in pounds)
        // Times 0.25 or 0.15 depending on the species
        // Times 1.2 to account for the underground weight of the tree
        if (diameter < 11) {
            weight = 0.25 * 1.2 * Math.pow(cmToInches(diameter), 2) * cmToFeet(height);
        } else {
            weight = 0.15 * 1.2 * Math.pow(cmToInches(diameter), 2) * cmToFeet(height);
        }

        return poundsToKG(weight);

    }

    // Returns the amount of CO2 sequestered by the tree (in kg/yr)
    public double getCO2Sequestered(Tree tree) throws Exception {
        if (tree == null)
            throw new InvalidInputException("Tree cannot be null!");

        double weight = getWeightOfTree(tree);

        // To account for the dry weight of the tree
        // Survey was done at University of Nebraska showing avg dry weight 72.5%
        double dryWeight = 0.725 * weight;

        // Percentage of Carbon in a tree is about 50% of the dry weight
        double carbonWeight = 0.5 * dryWeight;

        // CO2 to Carbon ratio in a CO2 molecule is 3.6663
        double co2Sequestered = 3.6663 * carbonWeight;

        return co2Sequestered/getAgeOfTree(tree);
    }

    // Returns the forecasted impact of removing a list of trees
    public double forecastCO2SequesteredReduced(List<Tree> trees) throws Exception {
        double impactOfCutdown = 0;

        for (Tree tree: trees) {
            impactOfCutdown += getCO2Sequestered(tree);
        }

        return impactOfCutdown;
    }

    // Calculates the biodiversity index of a list of trees
    public double biodiversityIndex(List<Tree> trees) {
        int totalTrees = trees.size();
        int totalSpecies = getUniqueSpecies(trees).size();

        return (double) totalTrees/totalSpecies;
    }

    // ==============================
    // HELPER METHODS
    // ==============================

    // Get list of unique species from list of trees
    public List<Species> getUniqueSpecies(List<Tree> trees) {
        HashSet<Species> uniqueSpecies = new HashSet<Species>();

        for (Tree tree : trees) {
            uniqueSpecies.add(tree.getSpecies());
        }

        return new ArrayList<Species>(uniqueSpecies);
    }


    // ==============================
    // CONVERSIONS
    // ==============================
    public double cmToFeet(int centimeters) {
        return 0.0328084 * centimeters;
    }

    public double cmToInches(int centimeters) {
        return 12 * cmToFeet(centimeters);
    }

    public double poundsToKG(double weight) {
        return 0.453592 * weight;
    }
}
