package ca.mcgill.ecse321.treeple.service;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.apache.commons.lang3.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.*;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.model.Tree.*;
import ca.mcgill.ecse321.treeple.model.User.*;
import ca.mcgill.ecse321.treeple.sqlite.SQLiteJDBC;

@Service
public class TreePLEService {

    private SQLiteJDBC sql;
    private String gmapsKey;
    private final String[] gmapsKeyList = new String[]{
        "AIzaSyDzb0p2lAcypZ2IbhVyhJYu6rTQLPncY5g",
        "AIzaSyDeo4TnWCcvE-yZlpmsv9FAEyYogAzzcBk",
        "AIzaSyC2kAeJiONkZEJCcFRcO_esNEqchbuub7o"
    };
    private final String sRoleKey = "i<3tr33s";
    private final String dbKey = "ih8tr33s";

    public TreePLEService(SQLiteJDBC sql) {
        this.sql = sql;
        this.gmapsKey = gmapsKeyList[(new Random()).nextInt(gmapsKeyList.length)];
    }

    public boolean setMaxId() {
        return Tree.setNextTreeId(sql.getMaxTreeId() + 1) &&
               Location.setNextLocationId(sql.getMaxLocationId() + 1) &&
               SurveyReport.setNextReportId(sql.getMaxReportId() + 1) &&
               Forecast.setNextForecastId(sql.getMaxForecastId() + 1);
    }

    public boolean reduceMaxId() {
        return Tree.setNextTreeId(Tree.getNextTreeId() - 1) &&
               Location.setNextLocationId(Location.getNextLocationId() - 1) &&
               SurveyReport.setNextReportId(SurveyReport.getNextReportId() - 1);
    }


    // ==============================
    // CREATE API
    // ==============================

    // Login
    public User login(JSONObject jsonParams) throws Exception {
        String username = jsonParams.getString("username");
        String password = jsonParams.getString("password");

        if (username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Username cannot be empty!");
        if (password.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Password cannot be empty!");

        User user = null;
        if ((user = User.getWithUsername(username)) != null || (user = sql.getUser(username)) != null) {
            if (BCrypt.checkpw(password, user.getPassword())) {
                return user;
            } else {
                throw new InvalidInputException("Entered password is incorrect!");
            }
        } else {
            throw new InvalidInputException("No User with that username exists!");
        }
    }

    // Check if User is a scientist
    public Map<String, Boolean> authenticated(JSONObject jsonParams) throws Exception {
        String username = jsonParams.getString("username");

        if (username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Username cannot be empty!");

        User user = null;
        if ((user = User.getWithUsername(username)) != null || (user = sql.getUser(username)) != null) {
            Map<String, Boolean> authenticated = new HashMap<String, Boolean>();
            authenticated.put("authenticated", user.getRole() == UserRole.Scientist);
            return authenticated;
        } else {
            throw new InvalidInputException("No User with that username exists!");
        }
    }

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
        double latitude = treeParams.getDouble("latitude");
        double longitude = treeParams.getDouble("longitude");
        String municipality = treeParams.getString("municipality");


        if (height < 0)
            throw new InvalidInputException("Height cannot be negative!");
        if (diameter < 0)
            throw new InvalidInputException("Diameter cannot be negative!");
        if (!datePlanted.matches("^([0-9]{4})-([0-9]{1,2})-([0-9]{1,2})$"))
            throw new InvalidInputException("Date doesn't match YYYY-(M)M-(D)D format!");
        if (username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("User is not logged in/Username is missing!");
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
                JSONArray addressInfo = gmapsJSON.getJSONArray("results").getJSONObject(0).getJSONArray("address_components");
                address = addressInfo.getJSONObject(addressInfo.length() - 1).getString("long_name").replaceAll("\\s", "");
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
            throw new InvalidInputException("No User with that username exists!");
        if (userObj.getRole() == UserRole.Resident && !ArrayUtils.contains(userObj.getMyAddresses(), address))
            throw new InvalidInputException("You cannot plant on someone else's property!");
        if ((speciesObj = sql.getSpecies(species)) == null)
            throw new InvalidInputException("No Species with that name exists!");
        if ((municipalityObj = sql.getMunicipality(municipality)) == null)
            throw new InvalidInputException("No Municipality with that name exists!");

        Location locationObj = new Location(latitude, longitude);
        SurveyReport surveyReportObj = new SurveyReport(Date.valueOf(datePlanted), username);

        Tree treeObj = new Tree(height, diameter, address, Date.valueOf(datePlanted), Land.valueOf(land),
                                Status.valueOf(status), Ownership.valueOf(ownership), speciesObj, locationObj, municipalityObj);

        treeObj.addReport(surveyReportObj);

        if (!sql.insertSurveyReport(surveyReportObj.getReportId(), surveyReportObj.getReportDate().toString(), username)) {
            reduceMaxId();
            throw new SQLException("SQL Survey Report insert query failed!");
        }

        if (!sql.insertLocation(locationObj.getLocationId(), latitude, longitude)) {
            reduceMaxId();
            sql.deleteSurveyReport(surveyReportObj.getReportId());
            throw new SQLException("SQL Location insert query failed!");
        }

        if (!sql.insertTree(treeObj.getTreeId(), height, diameter, address, datePlanted, land, status, ownership, species,
                            locationObj.getLocationId(), municipality, Integer.toString(surveyReportObj.getReportId()))) {
            reduceMaxId();
            sql.deleteSurveyReport(surveyReportObj.getReportId());
            sql.deleteLocation(locationObj.getLocationId());
            throw new SQLException("SQL Tree insert query failed!");
        }

        userObj.addMyTree(treeObj.getTreeId());
        municipalityObj.setTotalTrees(municipalityObj.getTotalTrees() + 1);
        sql.updateUserTrees(username, Arrays.toString(userObj.getMyTrees()).replaceAll("(\\[)|(\\])", ""));
        sql.updateMunicipalityIncDecTotalTrees(municipality, 1);

        return treeObj;
    }

    // Create a new User
    public User createUser(JSONObject jsonParams) throws Exception {
        String username = jsonParams.getString("username").trim();
        String password = jsonParams.getString("password");
        String role = jsonParams.getString("role");
        String scientistKey = jsonParams.optString("scientistKey");
        JSONArray myAddresses = jsonParams.getJSONArray("myAddresses");
        String myTrees = "";

        if (username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Username cannot be empty!");
        if (!username.matches("[a-zA-Z0-9]+"))
            throw new InvalidInputException("Username must be alphanumeric!");
        if (User.hasWithUsername(username))
            throw new InvalidInputException("Username is already taken!");
        if (password.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Password cannot be empty!");
        if (!EnumUtils.isValidEnum(UserRole.class, role))
            throw new InvalidInputException("That role doesn't exist!");
        if (role.equals(UserRole.Resident) && myAddresses.length() == 0)
            throw new InvalidInputException("Address cannot be empty!");
        if (role.equals(UserRole.Scientist) && !sRoleKey.equals(scientistKey))
            throw new InvalidInputException("Authorization key for Scientist role is invalid!");

        password = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User(username, password, UserRole.valueOf(role));

        for (Object address : myAddresses) {
            String postalCode = (String) address;
            if (postalCode != null && !(postalCode = postalCode.replaceAll("\\s", "")).isEmpty()) {
                user.addMyAddress(postalCode.toUpperCase());
            }
        }

        if (!sql.insertUser(username, password, role, myAddresses.toString().replaceAll("(\\[)|(\\])", ""), myTrees)) {
            user.delete();
            throw new SQLException("SQL User insert query failed!");
        }

        return user;
    }

    // Create a new Species
    public Species createSpecies(JSONObject jsonParams) throws Exception {
        String name = jsonParams.getString("name").trim();
        String species = jsonParams.optString("species").trim();
        String genus = jsonParams.optString("genus").trim();

        if (name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Species name cannot be empty!");
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
        String name = jsonParams.getString("name").trim();
        int totalTrees = jsonParams.getInt("totalTrees");
        JSONArray borders = jsonParams.getJSONArray("borders");

        if (name.replaceAll("\\s", "").isEmpty())
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
                for (int mlId = municipality.getBorder(0).getLocationId(); mlId < location.getLocationId(); mlId++) {
                    sql.deleteLocation(mlId);
                }
                municipality.delete();
                throw new SQLException("SQL Location insert query failed!");
            }
        }

        return municipality;
    }

    // Create a new Forecast
    public Forecast createForecast(JSONObject jsonParams) throws Exception {
        String fcDate = jsonParams.getString("fcDate");
        String fcUser = jsonParams.getString("fcUser");
        JSONArray fcTrees = jsonParams.getJSONArray("fcTrees");

        if (fcUser.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("User is not logged in/Username is missing!");
        if (!fcDate.matches("^([0-9]{4})-([0-9]{1,2})-([0-9]{1,2})$"))
            throw new InvalidInputException("Date doesn't match YYYY-(M)M-(D)D format!");
        if (fcTrees == null || fcTrees.length() < 1)
            throw new InvalidInputException("Forecast requires minimum 1 tree!");
        if (sql.getUser(fcUser) == null)
            throw new InvalidInputException("No User with that username exists!");

        ArrayList<Tree> treeList = new ArrayList<Tree>();
        ArrayList<Integer> treeIdList = new ArrayList<Integer>();

        for (int i = 0; i < fcTrees.length(); i++) {
            Tree tree;
            if ((tree = sql.getTree(fcTrees.getInt(i))) != null) {
                treeList.add(getFutureTree(tree, Date.valueOf(fcDate)));
                treeIdList.add(fcTrees.getInt(i));
            }
        }

        double stormwater = forecastStormwaterIntercepted(treeList);
        double co2Reduced = forecastCO2Sequestered(treeList);
        double biodiversity = forecastBiodiversityIndex(treeList);
        double energyConserved = forecastEnergyConserved(treeList);

        Forecast forecast = new Forecast(Date.valueOf(fcDate), fcUser, stormwater,
                                         co2Reduced, biodiversity, energyConserved);

        for (Tree tree : treeList) {
            forecast.addFcTree(tree);
        }

        if (!sql.insertForecast(forecast.getForecastId(),fcDate, fcUser, co2Reduced, biodiversity, stormwater,
                                energyConserved, treeIdList.toString().replaceAll("(\\[)|(\\])", ""))) {
            Forecast.setNextForecastId(Forecast.getNextForecastId() - 1);
            throw new SQLException("SQL Forecast insert query failed!");
        }

        return forecast;
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

    // Get a list of all Forecasts
    public List<Forecast> getAllForecasts() {
        return Collections.unmodifiableList(sql.getAllForecasts());
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
                throw new InvalidInputException("No User with that username exists!");

            return user;
        }
    }

    // Get a specific Species
    public Species getSpeciesByName(String name) throws Exception {
        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Species name cannot be empty!");

        if (Species.hasWithName(name)) {
            return Species.getWithName(name);
        } else {
            Species species;
            if ((species = sql.getSpecies(name)) == null)
                throw new InvalidInputException("No Species with that name exists!");

            return species;
        }
    }

    // Get a specific Location
    public Location getLocationById(int locationId) throws Exception {
        if (locationId <= 0)
            throw new InvalidInputException("Location's ID cannot be negative!");

        Location location;
        if ((location = sql.getLocation(locationId)) == null)
            throw new InvalidInputException("No Location with that ID exists!");

        return location;
    }

    // Get a specific Municipality
    public Municipality getMunicipalityByName(String name) throws Exception {
        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Municipality name cannot be empty!");

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
    public SurveyReport getSurveyReportById(int reportId) throws Exception {
        if (reportId <= 0)
            throw new InvalidInputException("Report's ID cannot be negative!");

        SurveyReport report;
        if ((report = sql.getSurveyReport(reportId)) == null)
            throw new InvalidInputException("No Survey Report with that ID exists!");

        return report;
    }

    // Get a specific Forecast
    public Forecast getForecastById(int forecastId) throws Exception {
        if (forecastId <= 0)
            throw new InvalidInputException("Forecast's ID cannot be negative!");

        Forecast forecast;
        if ((forecast = sql.getForecast(forecastId)) == null)
            throw new InvalidInputException("No Forecast with that ID exists!");

        return forecast;
    }

    // Get sustainability factors for a Tree
    public Map<String, Map<String, Double>> getTreeSustainability(Tree tree) throws Exception {
        Map<String, Map<String, Double>> sustainabilityFactors = new HashMap<String, Map<String, Double>>();
        Map<String, Double> stormwater = new HashMap<String, Double>();
        Map<String, Double> co2Reduced = new HashMap<String, Double>();
        Map<String, Double> biodiversity = new HashMap<String, Double>();
        Map<String, Double> energyConserved = new HashMap<String, Double>();

        double factor = 0;
        stormwater.put("factor", (factor = getStormwaterIntercepted(tree)));
        stormwater.put("worth", stormwaterWorth(factor));

        co2Reduced.put("factor", (factor = getCO2Sequestered(tree)));
        co2Reduced.put("worth", co2ReducedWorth(factor));

        biodiversity.put("factor", 1.0);

        energyConserved.put("factor", (factor = getEnergyConserved(tree)));
        energyConserved.put("worth", energyConservedWorth(factor));

        sustainabilityFactors.put("stormwater", stormwater);
        sustainabilityFactors.put("co2Reduced", co2Reduced);
        sustainabilityFactors.put("biodiversity", biodiversity);
        sustainabilityFactors.put("energyConserved", energyConserved);

        return sustainabilityFactors;
    }

    // Get sustainability factors for entire TreePLE system
    public Map<String, Map<String, Double>> getGroupSustainability(List<Tree> allTrees) throws Exception {
        Map<String, Map<String, Double>> sustainabilityFactors = new HashMap<String, Map<String, Double>>();
        Map<String, Double> stormwater = new HashMap<String, Double>();
        Map<String, Double> co2Reduced = new HashMap<String, Double>();
        Map<String, Double> biodiversity = new HashMap<String, Double>();
        Map<String, Double> energyConserved = new HashMap<String, Double>();

        double factor = 0;
        stormwater.put("factor", (factor = forecastStormwaterIntercepted(allTrees)));
        stormwater.put("worth", stormwaterWorth(factor));

        co2Reduced.put("factor", (factor = forecastCO2Sequestered(allTrees)));
        co2Reduced.put("worth", co2ReducedWorth(factor));

        biodiversity.put("factor", forecastBiodiversityIndex(allTrees));

        energyConserved.put("factor", (factor = forecastEnergyConserved(allTrees)));
        energyConserved.put("worth", energyConservedWorth(factor));

        sustainabilityFactors.put("stormwater", stormwater);
        sustainabilityFactors.put("co2Reduced", co2Reduced);
        sustainabilityFactors.put("biodiversity", biodiversity);
        sustainabilityFactors.put("energyConserved", energyConserved);

        return sustainabilityFactors;
    }


    // ==============================
    // GET FILTERED API
    // ==============================

    // Get trees from a list of Tree ID's
    public List<Tree> getTreesFromIdList(JSONArray treeIdList) throws Exception {
        ArrayList<Tree> treeList = new ArrayList<Tree>();
        for (Object treeId : treeIdList) {
            treeList.add(getTreeById((int) treeId));
        }

        return treeList;
    }

    // Get trees owned by a specifc user
    public List<Tree> getTreesOfUser(String username) throws Exception {
        User user = getUserByUsername(username);

        ArrayList<Tree> myTrees = new ArrayList<Tree>();
        for (int treeId : user.getMyTrees()) {
            myTrees.add(getTreeById(treeId));
        }

        return myTrees;
    }

    // Get trees of a specific species
    public List<Tree> getTreesOfSpecies(String name) throws Exception {
        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Species name cannot be empty!");
        if (!Species.hasWithName(name) && sql.getSpecies(name) == null)
            throw new InvalidInputException("No Species with that name exists!");

        return Collections.unmodifiableList(sql.getAllTreesOfSpecies(name));
    }

    // Get trees of a specific municipality
    public List<Tree> getTreesOfMunicipality(String name) throws Exception {
        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Municipality name cannot be empty!");
        if (!Municipality.hasWithName(name) && sql.getMunicipality(name) == null)
            throw new InvalidInputException("No Municipality with that name exists!");

        return Collections.unmodifiableList(sql.getAllTreesOfMunicipality(name));
    }

    // Get forecasts created by a specific user
    public List<Forecast> getForecastsOfUser(String username) throws Exception {
        if (username == null || username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Username cannot be empty!");
        if (!User.hasWithUsername(username) && sql.getUser(username) == null)
            throw new InvalidInputException("No User with that username exists!");

        return Collections.unmodifiableList(sql.getAllForecastsOfUser(username));
    }


    // ==============================
    // UPDATE API
    // ==============================

    // Update a Tree
    public Tree updateTree(JSONObject jsonParams) throws Exception {
        // User data
        String username = jsonParams.getString("user");

        // Tree data
        JSONObject treeParams = jsonParams.getJSONObject("tree");
        int treeId = treeParams.getInt("treeId");
        int height = treeParams.getInt("height");
        int diameter = treeParams.getInt("diameter");
        String land = treeParams.getString("land");
        String status = treeParams.getString("status");
        String ownership = treeParams.getString("ownership");
        String species = treeParams.getString("species");
        String municipality = treeParams.getString("municipality");

        if (treeId <= 0)
            throw new InvalidInputException("Tree's ID cannot be negative!");
        if (height < 0)
            throw new InvalidInputException("Height cannot be negative!");
        if (diameter < 0)
            throw new InvalidInputException("Diameter cannot be negative!");
        if (username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("User is not logged in/Username is missing!");
        if (!EnumUtils.isValidEnum(Land.class, land))
            throw new InvalidInputException("That land type doesn't exist!");
        if (!EnumUtils.isValidEnum(Status.class, status))
            throw new InvalidInputException("That status doesn't exist!");
        if (!EnumUtils.isValidEnum(Ownership.class, ownership))
            throw new InvalidInputException("That ownership doesn't exist!");

        Tree treeObj;
        User userObj;
        Species speciesObj;
        Municipality municipalityObj;
        if ((treeObj = sql.getTree(treeId)) == null)
            throw new InvalidInputException("No Tree with that ID exists!");
        if ((userObj = sql.getUser(username)) == null)
            throw new InvalidInputException("No User with that username exists!");
        if (userObj.getRole() == UserRole.Resident && !ArrayUtils.contains(userObj.getMyTrees(), treeId))
            throw new InvalidInputException("You cannot update someone else's tree!");
        if ((speciesObj = sql.getSpecies(species)) == null)
            throw new InvalidInputException("No Species with that name exists!");
        if ((municipalityObj = sql.getMunicipality(municipality)) == null)
            throw new InvalidInputException("No Municipality with that name exists!");

        SurveyReport surveyReportObj = new SurveyReport(new Date(Calendar.getInstance().getTimeInMillis()), username);

        treeObj.setHeight(height);
        treeObj.setDiameter(diameter);
        treeObj.setLand(Land.valueOf(land));
        treeObj.setStatus(Status.valueOf(status));
        treeObj.setOwnership(Ownership.valueOf(ownership));
        treeObj.setSpecies(speciesObj);
        treeObj.setMunicipality(municipalityObj);
        treeObj.addReport(surveyReportObj);

        ArrayList<Integer> reportIdList = new ArrayList<Integer>();
        for (SurveyReport report : treeObj.getReports()) {
            if (sql.getSurveyReport(report.getReportId()) != null) {
                reportIdList.add(report.getReportId());
            }
        }
        reportIdList.add(surveyReportObj.getReportId());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!sql.insertSurveyReport(surveyReportObj.getReportId(), dateFormat.format(surveyReportObj.getReportDate()), username)) {
            SurveyReport.setNextReportId(SurveyReport.getNextReportId() - 1);
            throw new SQLException("SQL Survey Report insert query failed!");
        }

        if (!sql.updateTree(treeObj.getTreeId(), height, diameter, land, status, ownership, species,
                            municipality, reportIdList.toString().replaceAll("(\\[)|(\\])", ""))) {
            SurveyReport.setNextReportId(SurveyReport.getNextReportId() - 1);
            sql.deleteSurveyReport(surveyReportObj.getReportId());
            throw new SQLException("SQL Tree update query failed!");
        }

        return treeObj;
    }

    // Update a User
    public User updateUser(JSONObject jsonParams) throws Exception {
        String username = jsonParams.getString("username");
        String password = jsonParams.getString("password");
        String role = jsonParams.getString("role");
        String scientistKey = jsonParams.optString("scientistKey");
        JSONArray myAddresses = jsonParams.getJSONArray("myAddresses");

        User user;
        if (username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Username cannot be empty!");
        if (!username.matches("[a-zA-Z0-9]+"))
            throw new InvalidInputException("Username must be alphanumeric!");
        if ((user = sql.getUser(username)) == null)
            throw new InvalidInputException("No User with that username exists!");
        if (password.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Password cannot be empty!");
        if (!EnumUtils.isValidEnum(UserRole.class, role))
            throw new InvalidInputException("That role doesn't exist!");
        if (role.equals(UserRole.Resident) && myAddresses.length() == 0)
            throw new InvalidInputException("Address cannot be empty!");
        if (role.equals(UserRole.Scientist) && !sRoleKey.equals(scientistKey))
            throw new InvalidInputException("Authorization key for Scientist role is invalid!");

        password = BCrypt.hashpw(password, BCrypt.gensalt());

        user.setPassword(password);
        user.setRole(UserRole.valueOf(role));
        user.clearMyAddresses();

        for (Object address : myAddresses) {
            String postalCode = (String) address;
            if (postalCode != null && !(postalCode = postalCode.replaceAll("\\s", "")).isEmpty()) {
                user.addMyAddress(postalCode.toUpperCase());
            }
        }

        if (!sql.updateUser(username, password, role, myAddresses.toString().replaceAll("(\\[)|(\\])", ""))) {
            user.delete();
            throw new SQLException("SQL User update query failed!");
        }

        return user;
    }

    // Update a User Password
    public User updateUserPassword(JSONObject jsonParams) throws Exception {
        String username = jsonParams.getString("username");
        String oldPass = jsonParams.getString("oldPass");
        String newPass = jsonParams.getString("newPass");

        User user;
        if (username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Username cannot be empty!");
        if (!username.matches("[a-zA-Z0-9]+"))
            throw new InvalidInputException("Username must be alphanumeric!");
        if ((user = sql.getUser(username)) == null)
            throw new InvalidInputException("No User with that username exists!");
        if (newPass.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("New password cannot be empty!");


        if (BCrypt.checkpw(oldPass, user.getPassword())) {
            newPass = BCrypt.hashpw(newPass, BCrypt.gensalt());
            user.setPassword(newPass);
        }

        if (!sql.updateUserPassword(username, newPass)) {
            user.delete();
            throw new SQLException("SQL User update query failed!");
        }

        return user;
    }

    // Update a Species
    public Species updateSpecies(JSONObject jsonParams) throws Exception {
        String name = jsonParams.getString("name").trim();
        String species = jsonParams.optString("species").trim();
        String genus = jsonParams.optString("genus").trim();

        Species speciesObj;
        if (name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Species name cannot be empty!");
        if ((speciesObj = sql.getSpecies(name)) == null)
            throw new InvalidInputException("No Species with that name exists!");

        speciesObj.setGenus(genus);
        speciesObj.setSpecies(species);

        if (!sql.updateSpecies(name, species, genus)) {
            speciesObj.delete();
            throw new SQLException("SQL Species update query failed!");
        }

        return speciesObj;
    }

    // Update a Municipality
    public Municipality updateMunicipalityBorders(JSONObject jsonParams) throws Exception {
        String name = jsonParams.getString("name").trim();
        JSONArray borders = jsonParams.getJSONArray("borders");

        Municipality municipalityObj;
        if (name == null || name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Municipality cannot be empty!");
        if ((municipalityObj = sql.getMunicipality(name)) == null)
            throw new InvalidInputException("No Municipality with that name exists!");
        if (borders.length() > 0 && borders.length() < 3)
            throw new InvalidInputException("Municipality requires minimum 3 borders!");

        ArrayList<Location> prevLocations = new ArrayList<Location>(municipalityObj.getBorders());
        municipalityObj.clearBorders();

        ArrayList<Integer> locationIdList = new ArrayList<>();
        borders.forEach(border -> {
            JSONArray locationJSON = (JSONArray) border;
            Location location = new Location(locationJSON.getDouble(0), locationJSON.getDouble(1));
            municipalityObj.addBorder(location);
            locationIdList.add(location.getLocationId());
        });

        if (!sql.updateMunicipalityBorders(name, locationIdList.toString().replaceAll("(\\[)|(\\])", ""))) {
            Location.setNextLocationId(Location.getNextLocationId() - municipalityObj.numberOfBorders());
            municipalityObj.delete();
            throw new SQLException("SQL Municipality insert query failed!");
        }

        for (Location location : municipalityObj.getBorders()) {
            if (!sql.insertLocation(location.getLocationId(), location.getLatitude(), location.getLongitude())) {
                Location.setNextLocationId(Location.getNextLocationId() - municipalityObj.numberOfBorders());
                for (int mlId = municipalityObj.getBorder(0).getLocationId(); mlId < location.getLocationId(); mlId++) {
                    sql.deleteLocation(mlId);
                }
                municipalityObj.delete();
                throw new SQLException("SQL Location insert query failed!");
            }
        }

        for (Location location : prevLocations) {
            sql.deleteLocation(location.getLocationId());
        }

        return municipalityObj;
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
        if (username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("User is not logged in/Username is missing!");

        Tree tree = sql.getTree(treeId);
        User user = sql.getUser(username);

        if (tree == null)
            throw new InvalidInputException("No Tree with that ID exists!");
        if (user == null)
            throw new InvalidInputException("No User with that username exists!");
        if (UserRole.Resident == user.getRole() && !ArrayUtils.contains(user.getMyTrees(), tree.getTreeId()))
            throw new InvalidInputException("This Tree wasn't planted by you!");

        if (!sql.deleteTree(treeId))
            throw new SQLException("SQL Tree delete query failed!");

        Municipality municipality = tree.getMunicipality();
        municipality.setTotalTrees(municipality.getTotalTrees() - 1);
        sql.updateMunicipalityIncDecTotalTrees(municipality.getName(), -1);
        sql.deleteLocation(tree.getLocation().getLocationId());

        return tree;
    }

    // Delete a User
    public User deleteUser(JSONObject jsonParams) throws Exception {
        String username = jsonParams.getString("username");

        if (username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("User is not logged in/Username is missing!");

        User user = sql.getUser(username);

        if (user == null)
            throw new InvalidInputException("No User with that username exists!");

        user.delete();
        if (!sql.deleteUser(username))
            throw new SQLException("SQL User delete query failed!");

        return user;
    }

    // Delete a Species
    public Species deleteSpecies(JSONObject jsonParams) throws Exception {
        String name = jsonParams.getString("name");

        if (name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Species name cannot be empty!");

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

        return location;
    }

    // Delete a Municipality
    public Municipality deleteMunicipality(JSONObject jsonParams) throws Exception {
        String name = jsonParams.getString("name");

        if (name.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("Municipality name cannot be empty!");

        Municipality municipality = sql.getMunicipality(name);

        if (municipality == null)
            throw new InvalidInputException("No Municipality with that name exists!");

        municipality.deleteKeepBorders();
        if (!sql.deleteMunicipality(name))
            throw new SQLException("SQL Municipality delete query failed!");

        for (Location location : municipality.getBorders()) {
            sql.deleteLocation(location.getLocationId());
        }

        return municipality;
    }

    // Delete a Forecast
    public Forecast deleteForecast(JSONObject jsonParams) throws Exception {
        String username = jsonParams.getString("user");
        int forecastId = jsonParams.getInt("forecastId");

        if (forecastId <= 0)
            throw new InvalidInputException("Forecast's ID cannot be negative or zero!");
        if (username.replaceAll("\\s", "").isEmpty())
            throw new InvalidInputException("User is not logged in/Username is missing!");

        Forecast forecast = sql.getForecast(forecastId);
        User user = sql.getUser(username);

        if (forecast == null)
            throw new InvalidInputException("No Forecast with that ID exists!");
        if (user == null)
            throw new InvalidInputException("No User with that username exists!");
        if (!forecast.getFcUser().equals(user.getUsername()))
            throw new InvalidInputException("This Forecast wasn't created by you!");

        if (!sql.deleteForecast(forecastId))
            throw new SQLException("SQL Forecast delete query failed!");

        return forecast;
    }


    // ==============================
    // DATABASE RESET API
    // ==============================

    // Reset the database
    public void resetDatabase(JSONObject jsonParams) throws Exception {
        String dbAccessKey = jsonParams.getString("dbAccessKey");

        if (!dbKey.equals(dbAccessKey))
            throw new InvalidInputException("Authorization key for Reset Database is invalid!");

        if (!User.clearUsers() || !Species.clearSpecies() || !Municipality.clearMunicipalities())
            throw new SQLException("Unable to reset SQL database!");

        if (!Tree.setNextTreeId(1) || !Location.setNextLocationId(1) || !SurveyReport.setNextReportId(1) || !sql.resetDB()) {
            setMaxId();
            throw new SQLException("Unable to reset SQL database!");
        }
    }

    // Delete the database
    public void deleteDatabase(JSONObject jsonParams) throws Exception {
        String dbAccessKey = jsonParams.getString("dbAccessKey");

        if (!dbKey.equals(dbAccessKey))
            throw new InvalidInputException("Authorization key for Reset Database is invalid!");

        if (!User.clearUsers() || !Species.clearSpecies() || !Municipality.clearMunicipalities())
            throw new SQLException("Unable to delete SQL database!");

        if (!Tree.setNextTreeId(1) || !Location.setNextLocationId(1) || !SurveyReport.setNextReportId(1) || !sql.deleteDB()) {
            setMaxId();
            throw new SQLException("Unable to delete SQL database!");
        }
    }


    // ==============================
    // FORECASTING
    // ==============================

    // Returns the biodiversity index for a list of trees
    public double forecastBiodiversityIndex(List<Tree> trees) {
        double totalTrees = trees.size();
        double totalSpecies = getUniqueSpecies(trees).size();

        if (totalTrees == 0) {
            return 0;
        }

        return totalSpecies/totalTrees;
    }

    // Returns the amount of CO2 reduced for a list of trees (in kg/yr)
    public double forecastCO2Sequestered(List<Tree> trees) throws Exception {
        double totalCO2Reduced = 0;

        for (Tree tree: trees) {
            totalCO2Reduced  += getCO2Sequestered(tree);
        }

        return totalCO2Reduced;
    }

    // Returns the total amount of stormwater intercepted for a list of trees (in L/yr)
    public double forecastStormwaterIntercepted(List<Tree> trees) throws Exception {
        double totalStormwater = 0;

        for (Tree tree: trees) {
            totalStormwater  += getStormwaterIntercepted(tree);
        }

        return totalStormwater ;
    }

    // Returns the total amount of energy conserved for a list of trees (in kWh/yr)
    public double forecastEnergyConserved(List<Tree> trees) throws Exception {
        double totalEnergyConserved = 0;

        for (Tree tree: trees) {
            totalEnergyConserved += getEnergyConserved(tree);
        }

        return totalEnergyConserved;
    }


    // ==============================
    // SUSTAINABILITY ATTRIBUTES
    // ==============================

    // Returns the amount of stormwater runoff by the tree (in L/yr)
    public double getStormwaterIntercepted(Tree tree) throws Exception {
        if (tree == null)
            throw new InvalidInputException("Tree cannot be null!");

        double curveNumber = 0;
        double stormwaterCaptured = 0;
        Land landType = tree.getLand();

        if (landType == Land.Park) {
            curveNumber = 73.5;
        } else if (landType == Land.Residential) {
            curveNumber = 83.2;
        } else if (landType == Land.Institutional) {
            curveNumber = 86.3;
        } else if (landType == Land.Municipal) {
            curveNumber = 91.6;
        }

        // Sorptivity of the tree (in inches)
        double sorptivity = (1000/curveNumber) - 5;

        // Estimation of the canopy area
        double canopyArea = getCanopyArea(tree);
        stormwaterCaptured = sorptivity * canopyArea;

        return cubicFeetToLiters(stormwaterCaptured);
    }

    // Returns the amount of CO2 sequestered by the tree (in kg/yr)
    public double getCO2Sequestered(Tree tree) throws Exception {
        if (tree == null)
            throw new InvalidInputException("Tree cannot be null!");

        double weight = getWeightOfTree(tree);

        // To account for the dry weight of the tree
        // Survey was done at University of Nebraska showing avg dry weight 72.5%
        double dryWeight = 0.725 * weight;

        // Percentage of Carbon in a tree is about 68% of the dry weight
        double carbonWeight = 0.68 * dryWeight;

        // CO2 to Carbon ratio in a CO2 molecule is 3.6663
        double co2Sequestered = 3.6663 * carbonWeight;

        return co2Sequestered/getAgeOfTree(tree);
    }

    // Returns the amount of energy conserved by the tree (in kWh/yr)
    public double getEnergyConserved(Tree tree) throws Exception {
        if (tree == null)
            throw new InvalidInputException("Tree cannot be null!");

        // Average energy consumed per citizen (in kWh/yr)
        double averageEnergyConsumed = 12332.2;

        double energyCoefficient = 0;
        Land landType = tree.getLand();

        if (landType == Land.Park) {
            energyCoefficient = 0.9510;
        } else if (landType == Land.Residential) {
            energyCoefficient = 0.9074;
        } else if (landType == Land.Institutional) {
            energyCoefficient = 0.8905;
        } else if (landType == Land.Municipal) {
            energyCoefficient = 0.9194;
        }

        // Average diameter of a tree is 50
        double diameterCoefficient = tree.getDiameter()/50.0;

        return averageEnergyConsumed * (1 - energyCoefficient) * diameterCoefficient;
    }


    // ==============================
    // SUSTAINABILITY MONETARY WORTH
    // ==============================

    // Monetary worth of stormwater intercepted (in CAD)
    public double stormwaterWorth(double stormwater) {
        return 0.0033732774 * stormwater;
    }

    // Monetary worth of CO2 reduced (in CAD)
    public double co2ReducedWorth(double co2Reduced) {
        return 2 * 0.009498572 * co2Reduced;
    }

    // Monetary worth of energy conserved (in CAD)
    public double energyConservedWorth(double energyConserved) {
        return 0.142861 * energyConserved;
    }


    // ==============================
    // TREE HELPER METHODS
    // ==============================

    // Returns the approximate age of the tree using height and diameter (in years)
    public int getAgeOfTree(Tree tree) throws Exception {
        if (tree == null)
            throw new InvalidInputException("Tree cannot be null!");

        double diameter = cmToInches(tree.getDiameter());

        // Average growth rate of a tree is about 6 yrs/inch
        return (int) Math.round(6 * diameter);
    }

    // Returns the tree with updated height and diameter for the given date
    public Tree getFutureTree(Tree tree, Date futureDate) throws Exception {
        long currentTime = Instant.now().truncatedTo(ChronoUnit.DAYS).getEpochSecond()*1000;
        long futureTime = futureDate.getTime();

        if (currentTime >= futureTime)
            throw new InvalidInputException("Forecast date cannot be before the current date!");

        double yearsDiff = (futureTime - currentTime) / (1000*60*60*24*365.25);

        tree.setDiameter((int) (tree.getDiameter() + inchesToCm(yearsDiff/6)));
        int futureHeight = (int) Math.exp(2.447 + 0.7 * Math.log(Math.log(getAgeOfTree(tree) + 1)));

        if (futureHeight > tree.getHeight()) {
            tree.setHeight(futureHeight);
        }

        return tree;
    }

    // Returns the approximate weight of the tree (in kg)
    public double getWeightOfTree(Tree tree) throws Exception {
        if (tree == null)
            throw new InvalidInputException("Tree cannot be null!");

        double height = cmToFeet(tree.getHeight());
        double diameter = cmToInches(tree.getDiameter());
        double weight = 0;

        // A rough estimation of a weight for trees (in pounds)
        // Times 0.45 or 0.35 depending on the species
        // Times 1.2 to account for the underground weight of the tree
        if (diameter < 11) {
            weight = 0.45 * 1.25 * Math.pow(diameter, 2) * height;
        } else {
            weight = 0.35 * 1.25 * Math.pow(diameter, 2) * height;
        }

        return poundsToKG(weight);
    }

    // Returns the approximate canopy area using diameter (in square feet)
    public double getCanopyArea(Tree tree) {
        double crownDiameter = 1.945 * tree.getDiameter();
        double canopyArea = Math.PI * Math.pow(cmToFeet(crownDiameter)/2, 2);
        return canopyArea;
    }

    // Returns list of unique species from list of trees
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

    public double cmToInches(double centimeters) {
        return 12 * cmToFeet(centimeters);
    }

    public double cmToFeet(double centimeters) {
        return 0.0328084 * centimeters;
    }

    public double inchesToCm(double inches) {
        return 2.54 * inches;
    }

    public double feetToMeter(double feet) {
        return 0.3048 * feet;
    }

    public double poundsToKG(double weight) {
        return 0.453592 * weight;
    }

    public double cubicFeetToLiters(double volume) {
        return 28.3168 * volume;
    }
}