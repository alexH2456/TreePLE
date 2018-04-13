package ca.mcgill.ecse321.treeple.service;

import static org.assertj.core.api.Assertions.useRepresentation;
import static org.junit.Assert.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import org.junit.*;

import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.model.Tree.*;
import ca.mcgill.ecse321.treeple.model.User.*;
import ca.mcgill.ecse321.treeple.sqlite.SQLiteJDBC;

public class TestTreePLEService {

    private static SQLiteJDBC sql;
    private static TreePLEService service;
    private static final String dbPath = "/output/treeple_test.db";

    private static JSONObject testTree;
    private static JSONObject testUser;
    private static JSONObject testSpecies;
    private static JSONObject testLocation;
    private static JSONObject testMunicipality;

    @BeforeClass
    public static void setUpBeforeClass() {
        sql = new SQLiteJDBC(dbPath);
        sql.connect();
        service = new TreePLEService(sql);

        testTree = buildTestTree();
        testUser = buildTestUser();
        testSpecies = buildTestSpecies();
        testLocation = buildTestLocation();
        testMunicipality = buildTestMunicipality();
    }

    @AfterClass
    public static void tearDownAfterClass() {
        sql.deleteDB();
        sql.closeConnection();
    }

    @After
    public void tearDown() throws Exception {
        service.resetDatabase();
    }

    @Test
    public void testSetMaxIdOnStartup() {
        assertEquals(true, service.setMaxId());
        assertEquals(1, Tree.getNextTreeId());
        assertEquals(1, Location.getNextLocationId());
        assertEquals(1, SurveyReport.getNextReportId());
    }


    // ==============================
    // CREATE USER TEST
    // ==============================

    @Test
    public void testCreateUser() {
        try {
            service.createUser(testUser);
            assertEquals(true, User.hasWithUsername(testUser.getString("username")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = JSONException.class)
    public void testCreateUserNullUsername() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", (String) null);
        user.put("password", "123yunus");
        user.put("role", "Scientist");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        service.createUser(user);
    }

    @Test
    public void testCreateUserNameEmpty() throws Exception {
    	String error = "";
        JSONObject user = new JSONObject();
        user.put("username", "           ");
        user.put("password", "123yunus");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.createUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Username cannot be empty!", error);
    }

    @Test
    public void testCreateUserNameNonAlphanumeric() throws Exception {
    	String error = "";
        JSONObject user = new JSONObject();
        user.put("username", "???////***$$$!@#!@#!@#");
        user.put("password", "123yunus");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.createUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Username must be alphanumeric!", error);
    }

    @Test
    public void testCreateUserAlreadyExists() throws Exception {
    	String error = "";
        JSONObject user = new JSONObject();
        service.createUser(testUser);
        user.put("username", "Abbas");
        user.put("password", "123yunus");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.createUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Username is already taken!", error);
    }

    @Test
    public void testCreateUserPasswordEmpty() throws Exception {
    	String error = "";
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "         ");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.createUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Password cannot be empty!", error);
    }
    @Test
    public void testCreateUserPasswordNonAlphanumeric() throws Exception {
    	String error = "";
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "*****@@@###$$$%%%%>>>??");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.createUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Password must be alphanumeric!", error);
    }

    @Test
    public void testCreateUserWrongScientistKey() throws Exception {
    	String error = "";
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "Scientist");
        user.put("scientistKey", "Wrong");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.createUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Authorization key for Scientist role is invalid!", error);
    }

    @Test(expected = JSONException.class)
    public void testCreateUserNullPassword() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", (String) null);
        user.put("role", "Scientist");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        service.createUser(user);
    }

    @Test(expected = JSONException.class)
    public void testCreateUserNullRole() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", (String) null);
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        service.createUser(user);
    }

    @Test(expected = JSONException.class)
    public void testCreateUserNullAddress() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "Scientist");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", (String) null);

        service.createUser(user);
    }

    @Test
    public void testCreateUserBadRole() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "NotARealRole");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
            service.createUser(user);
        } catch (InvalidInputException e) {
            assertEquals("That role doesn't exist!", e.getMessage());
        }
    }

    @Test
    public void testCreateUserResidentialWithEmptyAddress() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "   ");

        try {
            service.createUser(user);
        } catch (InvalidInputException e) {
            assertEquals("Address cannot be empty!", e.getMessage());
        }
    }


    // ==============================
    // CREATE SPECIES TEST
    // ==============================

    @Test
    public void testCreateSpecies() {
        try {
            service.createSpecies(testSpecies);
            assertEquals(true, Species.hasWithName(testSpecies.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateSpeciesNullSpeciesNullGenus() {
        JSONObject species = new JSONObject();
        species.put("name", "Maple");
        species.put("species", (String) null);
        species.put("genus", (String) null);

        try {
            service.createSpecies(species);
            assertEquals(true, Species.hasWithName(species.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateSpeciesNameSpaces() {
    	String error = "";
        JSONObject species = new JSONObject();
        species.put("name", "         ");
        species.put("species", (String) null);
        species.put("genus", (String) null);

        try {
            service.createSpecies(species);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals("Species name cannot be empty!", error);
    }

    @Test
    public void testCreateSpeciesExistsAlready() throws Exception {
    	String error = "";
        JSONObject species = new JSONObject();
        service.createSpecies(testSpecies);
        species.put("name", "Weeping Willow");
        species.put("species", (String) null);
        species.put("genus", (String) null);

        try {
            service.createSpecies(species);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals("Species already exists!", error);
    }

    @Test(expected = JSONException.class)
    public void testCreateSpeciesEmptyName() throws Exception {
        JSONObject species = new JSONObject();
        species.put("name", (String) null);
        species.put("species", "Acer Pseudoplatanus");
        species.put("genus", "Acer");

        service.createSpecies(species);
    }


    // ==============================
    // CREATE MUNICIPALITY TEST
    // ==============================

    @Test
    public void testCreateMunicipality() {
        try {
            service.createMunicipality(testMunicipality);
            assertEquals(true, Municipality.hasWithName(testMunicipality.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateMunicipalityEmptyBorders() {
        JSONObject municipality = new JSONObject();

        municipality.put("name", "Saint-Lazare");
        municipality.put("totalTrees", 12);
        municipality.put("borders", new JSONArray());

        try {
            service.createMunicipality(municipality);
            assertEquals(true, Municipality.hasWithName(municipality.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateMunicipalityEmptyName() {
    	String error = "";

    	JSONArray borders = new JSONArray();

        borders.put(new JSONArray(new double[]{45.497470, -73.772830}));
        borders.put(new JSONArray(new double[]{45.481864, -73.773715}));
        borders.put(new JSONArray(new double[]{45.460268, -73.750029}));
        borders.put(new JSONArray(new double[]{45.481208, -73.723422}));
        borders.put(new JSONArray(new double[]{45.459034, -73.683652}));
        borders.put(new JSONArray(new double[]{45.526536, -73.651208}));
        borders.put(new JSONArray(new double[]{45.522407, -73.730198}));
        JSONObject municipality = new JSONObject();
        municipality.put("name", "          ");
        municipality.put("totalTrees", 12);
        municipality.put("borders", borders);

        try {
            service.createMunicipality(municipality);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals("Municipality cannot be empty!", error);
    }

    @Test
    public void testCreateMunicipalityAlreadyExists() throws Exception {
    	String error = "";
    	service.createMunicipality(testMunicipality);

    	JSONArray borders = new JSONArray();
        borders.put(new JSONArray(new double[]{45.497470, -73.772830}));
        borders.put(new JSONArray(new double[]{45.481864, -73.773715}));
        borders.put(new JSONArray(new double[]{45.460268, -73.750029}));
        borders.put(new JSONArray(new double[]{45.481208, -73.723422}));
        borders.put(new JSONArray(new double[]{45.459034, -73.683652}));
        borders.put(new JSONArray(new double[]{45.526536, -73.651208}));
        borders.put(new JSONArray(new double[]{45.522407, -73.730198}));

        JSONObject municipality = new JSONObject();
        municipality.put("name", "Saint-Laurent");
        municipality.put("totalTrees", 12);
        municipality.put("borders", borders);

        try {
            service.createMunicipality(municipality);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals("Municipality already exists!", error);
    }



    @Test(expected = InvalidInputException.class)
    public void testCreateMunicipalityTwoBorders() throws Exception {
        JSONObject municipality = new JSONObject();
        municipality.put("name", "Saint-Lazare");
        municipality.put("totalTrees", 12);

        JSONArray borders = new JSONArray();
        borders.put(new JSONArray(new double[]{45.397067, -74.152067}));
        borders.put(new JSONArray(new double[]{45.411974, -74.152188}));
        municipality.put("borders", borders);

        service.createMunicipality(municipality);
    }


    // ==============================
    // CREATE FORECAST TEST
    // ==============================

    @Test
    public void testCreateForecast() throws Exception {
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", "Abbas");
        testForecast.put("fcTrees", trees);

        try {
            Forecast forecast = service.createForecast(testForecast);

            assertEquals(Date.valueOf("2018-04-16"), forecast.getFcDate());
            assertEquals("Abbas", forecast.getFcUser());

            for (int i = 0; i < trees.length(); i++) {
                Tree tree = service.getTreeById(i + 1);
                Tree treeFc = forecast.getFcTree(i);
                assertEquals(tree.getHeight(), treeFc.getHeight());
                assertEquals(tree.getDiameter(), treeFc.getDiameter());
                assertEquals(tree.getAddress(),treeFc.getAddress());
                assertEquals(tree.getDatePlanted(),treeFc.getDatePlanted());
                assertEquals(tree.getLand(),treeFc.getLand());
                assertEquals(tree.getLocation().getLatitude(),treeFc.getLocation().getLatitude(),0);
                assertEquals(tree.getLocation().getLongitude(),treeFc.getLocation().getLongitude(),0);
                assertEquals(tree.getMunicipality(),treeFc.getMunicipality());
                assertEquals(tree.getOwnership(), treeFc.getOwnership());
                assertEquals(tree.getSpecies(), treeFc.getSpecies());
                assertEquals(tree.getStatus(), treeFc.getStatus());
            }

            assertEquals(4*service.getCO2Sequestered(service.getTreeById(trees.getInt(0))),forecast.getCo2Reduced(), 0);
            assertEquals(4*service.getStormwaterIntercepted(service.getTreeById(trees.getInt(0))),forecast.getStormwater(), 0);
            assertEquals(4*service.getEnergyConserved(service.getTreeById(trees.getInt(0))),forecast.getEnergyConserved(), 0);
            assertEquals(0.25,forecast.getBiodiversity(), 0);
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = JSONException.class)
    public void testCreateForecastUserNull() throws Exception {
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", (String) null);
        testForecast.put("fcTrees", trees);

        service.createForecast(testForecast);
    }

    @Test
    public void testCreateForecastUsernamespaces() throws Exception {
        String error = "";
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", "             ");
        testForecast.put("fcTrees", trees);

        try {
            service.createForecast(testForecast);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "User is not logged in/Username is missing!");
    }

    @Test(expected = JSONException.class)
    public void testCreateForecastDateNull() throws Exception {
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", (String) null);
        testForecast.put("fcUser", "Abbas");
        testForecast.put("fcTrees", trees);

        service.createForecast(testForecast);
    }

    @Test
    public void testCreateForecastDateWrongFormat() throws Exception {
        String error = "";
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018/04/16");
        testForecast.put("fcUser", "Abbas");
        testForecast.put("fcTrees", trees);

        try {
            service.createForecast(testForecast);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "Date doesn't match YYYY-(M)M-(D)D format!");
    }

    @Test
    public void testCreateForecastTreeListSize() throws Exception {
        String error = "";
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);


        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", "Abbas");
        testForecast.put("fcTrees", trees);

        try {
            service.createForecast(testForecast);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "Forecast requires minimum 1 tree!");
    }

    @Test
    public void testCreateForecastUserDNE() throws Exception {
        String error = "";
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", "Gareth");
        testForecast.put("fcTrees", trees);

        try {
            service.createForecast(testForecast);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "User does not exist!");
    }

    // ==============================
    // CREATE TREE TEST
    // ==============================

    @Test
    public void testCreateTree() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        try {
            JSONObject testTreeObj = testTree.getJSONObject("tree");
            Tree tree = service.createTree(testTree);

            assertEquals(testTreeObj.getInt("treeId") + 1, Tree.getNextTreeId());
            assertEquals(testTreeObj.getInt("treeId") + testMunicipality.getJSONArray("borders").length() + 1, Location.getNextLocationId());
            assertEquals(testTreeObj.getInt("treeId") + 1, SurveyReport.getNextReportId());
            assertEquals(testTreeObj.getInt("height"), tree.getHeight());
            assertEquals(testTreeObj.getInt("diameter"), tree.getDiameter());
            assertEquals(Date.valueOf(testTreeObj.getString("datePlanted")), tree.getDatePlanted());
            assertEquals(Land.valueOf(testTreeObj.getString("land")), tree.getLand());
            assertEquals(Status.valueOf(testTreeObj.getString("status")), tree.getStatus());
            assertEquals(Ownership.valueOf(testTreeObj.getString("ownership")), tree.getOwnership());
            assertEquals(testTreeObj.getString("species"), tree.getSpecies().getName());
            assertEquals(testTreeObj.getDouble("latitude"), tree.getLocation().getLatitude(), 0);
            assertEquals(testTreeObj.getDouble("longitude"), tree.getLocation().getLongitude(), 0);
            assertEquals(testTreeObj.getString("municipality"), tree.getMunicipality().getName());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testCreateTreeNegativeHeight() throws Exception {
    	String error = "";
    	service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        JSONObject tree = new JSONObject(testTree.toString());
        tree.getJSONObject("tree").put("height", -30);

        try {
            service.createTree(tree);
        }catch(Exception e) {
        	error = e.getMessage();
        }
        assertEquals(error, "Height cannot be negative!");
    }

    @Test
    public void testCreateTreeNegativeDiameter() throws Exception {
    	String error = "";
    	service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        JSONObject tree = new JSONObject(testTree.toString());
        tree.getJSONObject("tree").put("diameter", -30);

        try {
        	service.createTree(tree);
        }catch(Exception e) {
        	error = e.getMessage();
        }
        assertEquals(error, "Diameter cannot be negative!");
    }

    @Test
    public void testCreateTreeWrongLandType() throws Exception {
    	String error = "";
    	service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        JSONObject tree = new JSONObject(testTree.toString());
        tree.getJSONObject("tree").put("land", "NonExistant");

        try {
        	service.createTree(tree);
        }catch(Exception e) {
        	error = e.getMessage();
        }
        assertEquals(error, "That land type doesn't exist!");
    }

    @Test
    public void testCreateTreeWrongOwnershipType() throws Exception {
    	String error = "";
    	service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        JSONObject tree = new JSONObject(testTree.toString());
        tree.getJSONObject("tree").put("ownership", "NonExistant");

        try {
        	service.createTree(tree);
        }catch(Exception e) {
        	error = e.getMessage();
        }
        assertEquals(error, "That ownership doesn't exist!");
    }

    @Test
    public void testCreateTreeWrongStatusType() throws Exception {
    	String error = "";
    	service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        JSONObject tree = new JSONObject(testTree.toString());
        tree.getJSONObject("tree").put("status", "NonExistant");

        try {
        	service.createTree(tree);
        }catch(Exception e) {
        	error = e.getMessage();
        }
        assertEquals(error, "That status doesn't exist!");
    }

    @Test
    public void testCreateTreeWrongDateFormat() throws Exception {
        try {
            JSONObject tree = new JSONObject(testTree.toString());
            tree.getJSONObject("tree").put("datePlanted", "18-03-2018");

            service.createTree(tree);
        } catch (InvalidInputException e) {
            assertEquals("Date doesn't match YYYY-(M)M-(D)D format!", e.getMessage());
        }
    }

    @Test
    public void testCreateTreeEmptyUser() throws Exception {
        try {
            JSONObject tree = new JSONObject(testTree.toString());
            tree.put("user", "   ");

            service.createTree(tree);
        } catch (InvalidInputException e) {
            assertEquals("User is not logged in/Username is missing!", e.getMessage());
        }
    }

    @Test
    public void testCreateTreeInvalidLocation() throws Exception {
        try {
            JSONObject tree = new JSONObject(testTree.toString());
            tree.getJSONObject("tree").put("latitude", "91");
            tree.getJSONObject("tree").put("longitude", "-181");

            service.createTree(tree);
        } catch (InvalidInputException e) {
            assertEquals("Invalid Google Maps API request!", e.getMessage());
        }
    }

    @Test
    public void testCreateTreeUserNonExistant() throws Exception {
        try {
            service.createTree(testTree);
        } catch (InvalidInputException e) {
            assertEquals("User does not exist!", e.getMessage());
        }
    }

    @Test
    public void testCreateTreeSpeciesNonExistant() throws Exception {
        service.createUser(testUser);

        try {
            service.createTree(testTree);
        } catch (InvalidInputException e) {
            assertEquals("Species does not exist!", e.getMessage());
        }
    }

    @Test
    public void testCreateTreeMunicipalityNonExistant() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);

        try {
            service.createTree(testTree);
        } catch (InvalidInputException e) {
            assertEquals("Municipality does not exist!", e.getMessage());
        }
    }

    // ==============================
    // GET USER TEST
    // ==============================

    @Test
    public void testGetUserByUsername() throws Exception {
        service.createUser(testUser);

        try {
            User user = service.getUserByUsername(testUser.getString("username"));

            assertEquals(testUser.getString("username"), user.getUsername());
            assertEquals(testUser.getString("password"), user.getPassword());
            assertEquals(UserRole.valueOf(testUser.getString("role")) ,user.getRole());
            assertEquals(testUser.getString("myAddresses"), user.getMyAddress(0));
            assertEquals(1, user.getMyAddresses().length);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetUserByUsernameNull() throws Exception {
        String username = null;

        try {
            service.getUserByUsername(username);
        } catch (InvalidInputException e) {
            assertEquals("Username cannot be empty!", e.getMessage());
        }
    }

    @Test
    public void testGetUserByUsernameEmpty() throws Exception {
        String username = "                   ";

        try {
            service.getUserByUsername(username);
        } catch (InvalidInputException e) {
            assertEquals("Username cannot be empty!", e.getMessage());
        }
    }

    @Test(expected = InvalidInputException.class)
    public void testGetUserByUsernameNonExistant() throws Exception {
        service.getUserByUsername("Filip");
    }


    // ==============================
    // GET MUNICIPALITY TEST
    // ==============================

    @Test
    public void testGetMunicipalityByName() throws Exception {
        service.createMunicipality(testMunicipality);

        try {
            Municipality municipality = service.getMunicipalityByName(testMunicipality.getString("name"));

            assertEquals(testMunicipality.getString("name"), municipality.getName());
            assertEquals(testMunicipality.getInt("totalTrees"), municipality.getTotalTrees());

            JSONArray borders = testMunicipality.getJSONArray("borders");
            for (int i = 0; i < borders.length(); i++) {
                assertEquals(borders.getJSONArray(i).getDouble(0), municipality.getBorder(i).getLatitude(), 0);
                assertEquals(borders.getJSONArray(i).getDouble(1), municipality.getBorder(i).getLongitude(), 0);
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetMunicipalityByNameEmpty() throws Exception {
        String name = "      ";

        try {
            service.getMunicipalityByName(name);
        } catch (InvalidInputException e) {
            assertEquals("Name cannot be empty!", e.getMessage());
        }
    }

    @Test
    public void testGetMunicipalityByNameNull() throws Exception {
        String name = null;

        try {
            service.getMunicipalityByName(name);
        } catch (InvalidInputException e) {
            assertEquals("Name cannot be empty!", e.getMessage());
        }
    }

    @Test(expected = InvalidInputException.class)
    public void testGetMunicipalityByNameNonExistant() throws Exception {
        service.getMunicipalityByName("Laval");
    }


    // ==============================
    // GET TREE TEST
    // ==============================

    @Test
    public void testGetTreeById() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = testTree.getJSONObject("tree");
            Tree tree = service.getTreeById(testTreeObj.getInt("treeId"));

            assertEquals(testTreeObj.getInt("height"), tree.getHeight());
            assertEquals(testTreeObj.getInt("diameter"), tree.getDiameter());
            assertEquals(Date.valueOf(testTreeObj.getString("datePlanted")), tree.getDatePlanted());
            assertEquals(Land.valueOf(testTreeObj.getString("land")), tree.getLand());
            assertEquals(Status.valueOf(testTreeObj.getString("status")), tree.getStatus());
            assertEquals(Ownership.valueOf(testTreeObj.getString("ownership")), tree.getOwnership());
            assertEquals(testTreeObj.getString("species"), tree.getSpecies().getName());
            assertEquals(testTreeObj.getDouble("latitude"), tree.getLocation().getLatitude(), 0);
            assertEquals(testTreeObj.getDouble("longitude"), tree.getLocation().getLongitude(), 0);
            assertEquals(testTreeObj.getString("municipality"), tree.getMunicipality().getName());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetTreeByIdNegativeId() throws Exception {
        try {
            service.getTreeById(-1);
        } catch (InvalidInputException e) {
            assertEquals("Tree's ID cannot be negative!", e.getMessage());
        }
    }

    @Test(expected = InvalidInputException.class)
    public void testGetTreeByIdNonExistantTree() throws Exception {
        service.getTreeById(100);
    }

    // ==============================
    // GET SPECIES BY NAME TEST
    // ==============================

    @Test
    public void testGetSpeciesByName() throws Exception {
        service.createSpecies(testSpecies);

        try {
            Species species = service.getSpeciesByName("Weeping Willow");
            assertEquals("Weeping Willow", species.getName());
            assertEquals("Salix Babylonica", species.getSpecies());
            assertEquals("Salix", species.getGenus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetSpeciesByNameNullName() throws Exception {
        String error = "";
        service.createSpecies(testSpecies);

        try {
            service.getSpeciesByName((String) null);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "Name cannot be empty!");
    }

    @Test
    public void testGetSpeciesByNameSpacesName() throws Exception {
        String error = "";
        service.createSpecies(testSpecies);

        try {
            service.getSpeciesByName("           ");
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "Name cannot be empty!");
    }

    @Test
    public void testGetSpeciesByNameSpeciesDNE() {
        String error = "";

        try {
            service.getSpeciesByName("Weeping Willow");
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "No Species with that name exists!");
    }

    // ==============================
    // GET LOCATION BY ID TEST
    // ==============================

    @Test
    public void testGetLocationByIdTest() throws Exception {
        service.createUser(testUser);
        service.createMunicipality(testMunicipality);
        service.createSpecies(testSpecies);
        service.createTree(testTree);
        int locationId = service.getTreeById(1).getLocation().getLocationId();

        try {
            Location location = service.getLocationById(locationId);
            assertEquals(45.515883, location.getLatitude(),0);
            assertEquals(-73.685552 , location.getLongitude(), 0);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetLocationByIdNegativeId() throws Exception {
        String error = "";
        try {
            service.getLocationById(-1);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals("Location's ID cannot be negative!", error);
    }

    @Test
    public void testGetLocationByIdLocationDNE() {
        String error = "";
            try {
                service.getLocationById(1);
            } catch (Exception e) {
                error = e.getMessage();
            }
            assertEquals("No Location with that ID exists!", error);
    }

    // ==============================
    // GET SURVEY REPORT BY ID TEST
    // ==============================

    @Test
    public void testGetSurveyReportById() throws Exception {
    	service.createMunicipality(testMunicipality);
    	service.createSpecies(testSpecies);
    	service.createUser(testUser);
    	Tree tree = service.createTree(testTree);
    	
    	SurveyReport report = tree.getReport(0);
    	
    	try {
    		SurveyReport databaseReport = service.getSurveyReportById(report.getReportId());
    		assertEquals(report.getReportDate().toString(), databaseReport.getReportDate().toString());
    		assertEquals(report.getReportId(), databaseReport.getReportId());
    		assertEquals(report.getReportUser(),databaseReport.getReportUser());
    	}catch(Exception e) {
    		fail();
    	}
    }
    
    @Test
    public void testGetSurveyReportByIdNegativeId() {
    	String error = "";
    	try {
    		service.getSurveyReportById(-1);
    	}catch(Exception e) {
    		error = e.getMessage();
    	}
    	assertEquals("Report's ID cannot be negative!", error);
    }
    
    @Test
    public void testGetSurveyReportByIdReportDNE() {
    	String error = "";
    	try {
    		service.getSurveyReportById(SurveyReport.getNextReportId());
    	}catch(Exception e) {
    		error = e.getMessage();
    	}
    	assertEquals("No Survey Report with that ID exists!", error);
    }
    
    // ==============================
    // GET FORECAST BY ID TEST
    // ==============================
    
    @Test
    public void testGetForecastById() throws Exception {
    	JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", "Abbas");
        testForecast.put("fcTrees", trees);
        
        Forecast forecast = service.createForecast(testForecast);
        
        try {
        	Forecast databaseForecast = service.getForecastById(forecast.getForecastId());
        	for(int i = 0; i < databaseForecast.getFcTrees().size(); i++) {
        		Tree fcTree = forecast.getFcTree(i);
                Tree dbTree = databaseForecast.getFcTree(i);
                assertEquals(fcTree.getHeight(), dbTree.getHeight());
                assertEquals(fcTree.getDiameter(), dbTree.getDiameter());
                assertEquals(fcTree.getAddress(),dbTree.getAddress());
                assertEquals(fcTree.getDatePlanted(),dbTree.getDatePlanted());
                assertEquals(fcTree.getLand(),dbTree.getLand());
                assertEquals(fcTree.getLocation().getLatitude(),dbTree.getLocation().getLatitude(),0);
                assertEquals(fcTree.getLocation().getLongitude(),dbTree.getLocation().getLongitude(),0);
                assertEquals(fcTree.getMunicipality(),dbTree.getMunicipality());
                assertEquals(fcTree.getOwnership(), dbTree.getOwnership());
                assertEquals(fcTree.getSpecies(), dbTree.getSpecies());
                assertEquals(fcTree.getStatus(), dbTree.getStatus());
        	}
        	assertEquals(forecast.getBiodiversity(),databaseForecast.getBiodiversity(),0.01);
        	assertEquals(forecast.getCo2Reduced(), databaseForecast.getCo2Reduced(), 0.01);
        	assertEquals(forecast.getEnergyConserved(), databaseForecast.getEnergyConserved(), 0.01);
        	assertEquals(forecast.getStormwater(), databaseForecast.getStormwater(), 0.01);
        	assertEquals(forecast.getFcDate().toString(), forecast.getFcDate().toString());
        	assertEquals(forecast.getFcUser(),databaseForecast.getFcUser());
        	assertEquals(forecast.getForecastId(),databaseForecast.getForecastId());
        	
        }catch(Exception e) {
        	fail();
        }
    }
    @Test
    public void testGetForecastByIdNegativeId() {
    	String error = "";
    	try {
    		service.getForecastById(-1);
    	}catch(Exception e) {
    		error = e.getMessage();
    	}
    	assertEquals("Forecast's ID cannot be negative!", error);
    }
    
    @Test
    public void testGetForecastByIdReportDNE() {
    	String error = "";
    	try {
    		service.getForecastById(Forecast.getNextForecastId());
    	}catch(Exception e) {
    		error = e.getMessage();
    	}
    	assertEquals("No Forecast with that ID exists!", error);
    }
    
    // ==============================
    // GET TREES OF USER
    // ==============================

    @Test
    public void testGetTreesOfUser() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        List<Tree> trees = new ArrayList<Tree>();

        for (int i = 0; i < 4; i++) {
            trees.add(service.createTree(testTree));
        }
        try {
        	List<Tree> userTrees = service.getTreesOfUser(testUser.getString("username"));

            for (int i = 0; i < userTrees.size(); i++) {
                Tree tree = trees.get(i);
                Tree userTree = userTrees.get(i);
                assertEquals(tree.getHeight(), userTree.getHeight());
                assertEquals(tree.getDiameter(), userTree.getDiameter());
                assertEquals(tree.getAddress(),userTree.getAddress());
                assertEquals(tree.getDatePlanted(),userTree.getDatePlanted());
                assertEquals(tree.getLand(),userTree.getLand());
                assertEquals(tree.getLocation().getLatitude(),userTree.getLocation().getLatitude(),0);
                assertEquals(tree.getLocation().getLongitude(),userTree.getLocation().getLongitude(),0);
                assertEquals(tree.getMunicipality(),userTree.getMunicipality());
                assertEquals(tree.getOwnership(), userTree.getOwnership());
                assertEquals(tree.getSpecies(), userTree.getSpecies());
                assertEquals(tree.getStatus(), userTree.getStatus());
            }
        } catch (Exception e) {
            fail();
        }
    }
    
    // ==============================
    // GET TREES OF SPECIES
    // ==============================

    @Test
    public void testGetTreesOfSpecies() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        List<Tree> trees = new ArrayList<Tree>();

        for (int i = 0; i < 4; i++) {
            trees.add(service.createTree(testTree));
        }
        try {
        	List<Tree> speciesTrees = service.getTreesOfSpecies(testSpecies.getString("name"));

            for (int i = 0; i < speciesTrees.size(); i++) {
                Tree tree = trees.get(i);
                Tree speciesTree = speciesTrees.get(i);
                assertEquals(tree.getHeight(), speciesTree.getHeight());
                assertEquals(tree.getDiameter(), speciesTree.getDiameter());
                assertEquals(tree.getAddress(),speciesTree.getAddress());
                assertEquals(tree.getDatePlanted(),speciesTree.getDatePlanted());
                assertEquals(tree.getLand(),speciesTree.getLand());
                assertEquals(tree.getLocation().getLatitude(),speciesTree.getLocation().getLatitude(),0);
                assertEquals(tree.getLocation().getLongitude(),speciesTree.getLocation().getLongitude(),0);
                assertEquals(tree.getMunicipality(),speciesTree.getMunicipality());
                assertEquals(tree.getOwnership(), speciesTree.getOwnership());
                assertEquals(tree.getSpecies(), speciesTree.getSpecies());
                assertEquals(tree.getStatus(), speciesTree.getStatus());
            }
        } catch (Exception e) {
            fail();
        }
    }
    
    // ==============================
    // GET TREES OF MUNICIPALITY
    // ==============================
    
    @Test
    public void testGetTreesOfMunicipality() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        List<Tree> trees = new ArrayList<Tree>();

        for (int i = 0; i < 4; i++) {
            trees.add(service.createTree(testTree));
        }
        try {
        	List<Tree> municipalityTrees = service.getTreesOfMunicipality(testMunicipality.getString("name"));

            for (int i = 0; i < municipalityTrees.size(); i++) {
                Tree tree = trees.get(i);
                Tree municipalityTree = municipalityTrees.get(i);
                assertEquals(tree.getHeight(), municipalityTree.getHeight());
                assertEquals(tree.getDiameter(), municipalityTree.getDiameter());
                assertEquals(tree.getAddress(),municipalityTree.getAddress());
                assertEquals(tree.getDatePlanted(),municipalityTree.getDatePlanted());
                assertEquals(tree.getLand(),municipalityTree.getLand());
                assertEquals(tree.getLocation().getLatitude(),municipalityTree.getLocation().getLatitude(),0);
                assertEquals(tree.getLocation().getLongitude(),municipalityTree.getLocation().getLongitude(),0);
                assertEquals(tree.getMunicipality(),municipalityTree.getMunicipality());
                assertEquals(tree.getOwnership(), municipalityTree.getOwnership());
                assertEquals(tree.getSpecies(), municipalityTree.getSpecies());
                assertEquals(tree.getStatus(), municipalityTree.getStatus());
            }
        } catch (Exception e) {
            fail();
        }
    }

    // ==============================
    // UPDATE TREE TEST
    // ==============================

    @Test
    public void testUpdateTree() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", 1);
            testTreeObj.put("height", 20);
            testTreeObj.put("diameter", 40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "Residential");
            testTreeObj.put("status", "Planted");
            testTreeObj.put("ownership", "Private");
            testTreeObj.put("species", "Weeping Willow");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Laurent");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "Abbas");
            updateTreeObj.put("tree", testTreeObj);
            Tree tree = service.updateTree(updateTreeObj);

            assertEquals(testTreeObj.getInt("treeId") +1, Tree.getNextTreeId());
            assertEquals(testTreeObj.getInt("treeId") + testMunicipality.getJSONArray("borders").length() + 1, Location.getNextLocationId());
            assertEquals(testTreeObj.getInt("treeId") +2, SurveyReport.getNextReportId());
            assertEquals(20, tree.getHeight());
            assertEquals(testTreeObj.getInt("diameter"), tree.getDiameter());
            assertEquals(Date.valueOf(testTreeObj.getString("datePlanted")), tree.getDatePlanted());
            assertEquals(Land.valueOf(testTreeObj.getString("land")), tree.getLand());
            assertEquals(Status.valueOf(testTreeObj.getString("status")), tree.getStatus());
            assertEquals(Ownership.valueOf(testTreeObj.getString("ownership")), tree.getOwnership());
            assertEquals(testTreeObj.getString("species"), tree.getSpecies().getName());
            assertEquals(testTreeObj.getDouble("latitude"), tree.getLocation().getLatitude(), 0);
            assertEquals(testTreeObj.getDouble("longitude"), tree.getLocation().getLongitude(), 0);
            assertEquals(testTreeObj.getString("municipality"), tree.getMunicipality().getName());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateTreeNegativeId() throws Exception {
        String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", -1);
            testTreeObj.put("height", 420);
            testTreeObj.put("diameter", 40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "Residential");
            testTreeObj.put("status", "Planted");
            testTreeObj.put("ownership", "Private");
            testTreeObj.put("species", "Weeping Willow");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Laurent");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "Abbas");
            updateTreeObj.put("tree", testTreeObj);

            service.updateTree(updateTreeObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "Tree's ID cannot be negative!");
    }

    @Test
    public void testUpdateTreeNegativeHeight() throws Exception {
        String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", 1);
            testTreeObj.put("height", -420);
            testTreeObj.put("diameter", 40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "Residential");
            testTreeObj.put("status", "Planted");
            testTreeObj.put("ownership", "Private");
            testTreeObj.put("species", "Weeping Willow");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Laurent");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "Abbas");
            updateTreeObj.put("tree", testTreeObj);

            service.updateTree(updateTreeObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "Height cannot be negative!");
    }

    @Test
    public void testUpdateTreeNegativeDiameter() throws Exception {
        String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", 1);
            testTreeObj.put("height", 420);
            testTreeObj.put("diameter", -40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "Residential");
            testTreeObj.put("status", "Planted");
            testTreeObj.put("ownership", "Private");
            testTreeObj.put("species", "Weeping Willow");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Laurent");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "Abbas");
            updateTreeObj.put("tree", testTreeObj);

            service.updateTree(updateTreeObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error,"Diameter cannot be negative!");
    }

    @Test
    public void testUpdateTreeUsernameEmpty() throws Exception {
        String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", 1);
            testTreeObj.put("height", 420);
            testTreeObj.put("diameter", 40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "Residential");
            testTreeObj.put("status", "Planted");
            testTreeObj.put("ownership", "Private");
            testTreeObj.put("species", "Weeping Willow");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Laurent");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "      ");
            updateTreeObj.put("tree", testTreeObj);

            service.updateTree(updateTreeObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "User is not logged in/Username is missing!");
    }

    @Test(expected = JSONException.class)
    public void testUpdateTreeUsernameNull() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        JSONObject testTreeObj = new JSONObject();

        testTreeObj.put("treeId", 1);
        testTreeObj.put("height", 420);
        testTreeObj.put("diameter", 40);
        testTreeObj.put("datePlanted", "2018-03-16");
        testTreeObj.put("land", "Residential");
        testTreeObj.put("status", "Planted");
        testTreeObj.put("ownership", "Private");
        testTreeObj.put("species", "Weeping Willow");
        testTreeObj.put("latitude", 45.515883);
        testTreeObj.put("longitude", -73.685552);
        testTreeObj.put("municipality", "Saint-Laurent");

        JSONObject updateTreeObj = new JSONObject();
        updateTreeObj.put("user", (String) null);
        updateTreeObj.put("tree", testTreeObj);

        service.updateTree(updateTreeObj);

    }

    @Test
    public void testUpdateTreeInvalidLandEnum() throws Exception {
        String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", 1);
            testTreeObj.put("height", 420);
            testTreeObj.put("diameter", 40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "NonExistant");
            testTreeObj.put("status", "Planted");
            testTreeObj.put("ownership", "Private");
            testTreeObj.put("species", "Weeping Willow");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Laurent");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "Abbas");
            updateTreeObj.put("tree", testTreeObj);

            service.updateTree(updateTreeObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "That land type doesn't exist!");
    }

    @Test
    public void testUpdateTreeInvalidStatusEnum() throws Exception {
        String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", 1);
            testTreeObj.put("height", 420);
            testTreeObj.put("diameter", 40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "Residential");
            testTreeObj.put("status", "Nonexistant");
            testTreeObj.put("ownership", "Private");
            testTreeObj.put("species", "Weeping Willow");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Laurent");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "Abbas");
            updateTreeObj.put("tree", testTreeObj);

            service.updateTree(updateTreeObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "That status doesn't exist!");
    }

    @Test
    public void testUpdateTreeInvalidOwnershipEnum() throws Exception {
        String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", 1);
            testTreeObj.put("height", 420);
            testTreeObj.put("diameter", 40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "Residential");
            testTreeObj.put("status", "Planted");
            testTreeObj.put("ownership", "NonExistant");
            testTreeObj.put("species", "Weeping Willow");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Laurent");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "Abbas");
            updateTreeObj.put("tree", testTreeObj);

            service.updateTree(updateTreeObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "That ownership doesn't exist!");
    }

    @Test
    public void testUpdateTreeTreeDNE() throws Exception {
        String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", 1);
            testTreeObj.put("height", 420);
            testTreeObj.put("diameter", 40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "Residential");
            testTreeObj.put("status", "Planted");
            testTreeObj.put("ownership", "Private");
            testTreeObj.put("species", "Weeping Willow");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Laurent");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "Abbas");
            updateTreeObj.put("tree", testTreeObj);

            service.updateTree(updateTreeObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "No Tree with that ID exists!");
    }

    @Test
    public void testUpdateTreeUserDNE() throws Exception {
        String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", 1);
            testTreeObj.put("height", 420);
            testTreeObj.put("diameter", 40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "Residential");
            testTreeObj.put("status", "Planted");
            testTreeObj.put("ownership", "Private");
            testTreeObj.put("species", "Weeping Willow");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Laurent");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "Gareth");
            updateTreeObj.put("tree", testTreeObj);

            service.updateTree(updateTreeObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "User does not exist!");
    }

    @Test
    public void testUpdateTreeSpeciesDNE() throws Exception {
        String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", 1);
            testTreeObj.put("height", 420);
            testTreeObj.put("diameter", 40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "Residential");
            testTreeObj.put("status", "Planted");
            testTreeObj.put("ownership", "Private");
            testTreeObj.put("species", "Maple");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Lazare");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "Abbas");
            updateTreeObj.put("tree", testTreeObj);

            service.updateTree(updateTreeObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "Species does not exist!");
    }

    @Test
    public void testUpdateMunicipalityDNE() throws Exception {
        String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        try {
            JSONObject testTreeObj = new JSONObject();

            testTreeObj.put("treeId", 1);
            testTreeObj.put("height", 420);
            testTreeObj.put("diameter", 40);
            testTreeObj.put("datePlanted", "2018-03-16");
            testTreeObj.put("land", "Residential");
            testTreeObj.put("status", "Planted");
            testTreeObj.put("ownership", "Private");
            testTreeObj.put("species", "Weeping Willow");
            testTreeObj.put("latitude", 45.515883);
            testTreeObj.put("longitude", -73.685552);
            testTreeObj.put("municipality", "Saint-Lazare");

            JSONObject updateTreeObj = new JSONObject();
            updateTreeObj.put("user", "Abbas");
            updateTreeObj.put("tree", testTreeObj);

            service.updateTree(updateTreeObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "Municipality does not exist!");
    }

    // ==============================
    // UPDATE USER TEST
    // ==============================
    
    @Test
    public void testUpdateUser() throws Exception {
    	service.createUser(testUser);
    	
    	try {
    		JSONObject updateUserObj = new JSONObject();
    		updateUserObj.put("username", "Abbas");
            updateUserObj.put("password", "password123");
            updateUserObj.put("role", "Scientist");
            updateUserObj.put("scientistKey", "i<3tr33s");
            updateUserObj.put("myAddresses", "H4L3N1");
            
            User user = service.updateUser(updateUserObj);
            
            assertEquals("Abbas", user.getUsername());
            assertEquals("password123", user.getPassword());
            assertEquals(UserRole.Scientist, user.getRole());
            assertEquals("H4L3N1", user.getMyAddress(0));
    	}catch(Exception e) {
    		fail();
    	}
    }
    
    @Test(expected = JSONException.class)
    public void testUpdateUserNullUsername() throws Exception {
        service.createUser(testUser);
        
        JSONObject updateUserObj = new JSONObject();
		updateUserObj.put("username", (String) null);
        updateUserObj.put("password", "password123");
        updateUserObj.put("role", "Scientist");
        updateUserObj.put("scientistKey", "i<3tr33s");
        updateUserObj.put("myAddresses", "H4L3N1");

        service.updateUser(updateUserObj);
    }

    @Test
    public void testUpdateUserNameEmpty() throws Exception {
    	String error = "";
    	service.createUser(testUser);
        JSONObject user = new JSONObject();
        user.put("username", "           ");
        user.put("password", "123yunus");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.updateUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Username cannot be empty!", error);
    }

    @Test
    public void testUpdateUserNameNonAlphanumeric() throws Exception {
    	String error = "";
    	service.createUser(testUser);
        JSONObject user = new JSONObject();
        user.put("username", "???////***$$$!@#!@#!@#");
        user.put("password", "123yunus");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.updateUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Username must be alphanumeric!", error);
    }

    @Test
    public void testUpdateUserAlreadyExists() throws Exception {
    	String error = "";
        JSONObject user = new JSONObject();
        user.put("username", "Abbas");
        user.put("password", "123yunus");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.updateUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Username does not exist!", error);
    }

    @Test
    public void testUpdateUserPasswordEmpty() throws Exception {
    	String error = "";
        JSONObject user = new JSONObject();
        service.createUser(testUser);
        user.put("username", "Abbas");
        user.put("password", "         ");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.updateUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Password cannot be empty!", error);
    }
    @Test
    public void testUpdateUserPasswordNonAlphanumeric() throws Exception {
    	String error = "";
    	service.createUser(testUser);
        JSONObject user = new JSONObject();
        user.put("username", "Abbas");
        user.put("password", "*****@@@###$$$%%%%>>>??");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.updateUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Password must be alphanumeric!", error);
    }

    @Test
    public void testUpdateUserWrongScientistKey() throws Exception {
    	String error = "";
    	service.createUser(testUser);
        JSONObject user = new JSONObject();
        user.put("username", "Abbas");
        user.put("password", "123yunus");
        user.put("role", "Scientist");
        user.put("scientistKey", "Wrong");
        user.put("myAddresses", "St-Lazare");

        try {
        	service.updateUser(user);
        }catch(Exception e) {
        	error = e.getMessage();
        }

        assertEquals("Authorization key for Scientist role is invalid!", error);
    }

    @Test(expected = JSONException.class)
    public void testUpdateUserNullPassword() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Abbas");
        user.put("password", (String) null);
        user.put("role", "Scientist");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        service.updateUser(user);
    }

    @Test(expected = JSONException.class)
    public void testUpdateUserNullRole() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Abbas");
        user.put("password", "123yunus");
        user.put("role", (String) null);
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        service.updateUser(user);
    }

    @Test(expected = JSONException.class)
    public void testUpdateUserNullAddress() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Abbas");
        user.put("password", "123yunus");
        user.put("role", "Scientist");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", (String) null);

        service.updateUser(user);
    }

    @Test
    public void testUpdateUserBadRole() throws Exception {
        JSONObject user = new JSONObject();
        service.createUser(testUser);
        user.put("username", "Abbas");
        user.put("password", "123yunus");
        user.put("role", "NotARealRole");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "St-Lazare");

        try {
            service.updateUser(user);
        } catch (InvalidInputException e) {
            assertEquals("That role doesn't exist!", e.getMessage());
        }
    }

    @Test
    public void testUpdateUserResidentialWithEmptyAddress() throws Exception {
        JSONObject user = new JSONObject();
        service.createUser(testUser);
        user.put("username", "Abbas");
        user.put("password", "123yunus");
        user.put("role", "Resident");
        user.put("scientistKey", "i<3tr33s");
        user.put("myAddresses", "   ");

        try {
            service.updateUser(user);
        } catch (InvalidInputException e) {
            assertEquals("Address cannot be empty!", e.getMessage());
        }
    }

    // ==============================
    // UPDATE SPECIES TEST
    // ==============================

    @Test
    public void testUpdateSpecies() throws Exception {
        service.createSpecies(testSpecies);

        JSONObject newSpecies = new JSONObject();
        newSpecies.put("name", testSpecies.getString("name"));
        newSpecies.put("species", "Salix Alba");
        newSpecies.put("genus", "Willow");

        try {
            Species species = service.updateSpecies(newSpecies);

            assertEquals(newSpecies.getString("name"), species.getName());
            assertEquals(newSpecies.getString("species"), species.getSpecies());
            assertEquals(newSpecies.getString("genus"), species.getGenus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateSpeciesNullSpeciesNullGenus() throws Exception {
        service.createSpecies(testSpecies);

        JSONObject newSpecies = new JSONObject();
        newSpecies.put("name", testSpecies.getString("name"));
        newSpecies.put("species", (String) null);
        newSpecies.put("genus", (String) null);

        try {
            Species species = service.updateSpecies(newSpecies);

            assertEquals(newSpecies.getString("name"), species.getName());
            assertEquals("", species.getSpecies());
            assertEquals("", species.getGenus());
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = JSONException.class)
    public void testUpdateSpeciesNullName() throws Exception {
        JSONObject species = new JSONObject();
        species.put("name", (String) null);
        species.put("species", "Acer Pseudoplatanus");
        species.put("genus", "Acer");

        service.updateSpecies(species);
    }

    @Test
    public void testUpdateSpeciesEmptyName() throws Exception {
        JSONObject newSpecies = new JSONObject();
        newSpecies.put("name", "     ");
        newSpecies.put("species", "Acer Pseudoplatanus");
        newSpecies.put("genus", "Acer");

        try {
            service.updateSpecies(newSpecies);
        } catch (Exception e) {
            assertEquals("Species name cannot be empty!", e.getMessage());
        }
    }
    
    @Test
    public void testUpdateSpeciesDNE() throws Exception {
        JSONObject newSpecies = new JSONObject();
        newSpecies.put("name", testSpecies.getString("name"));
        newSpecies.put("species", "Salix Alba");
        newSpecies.put("genus", "Willow");

        try {
        	service.updateSpecies(newSpecies);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "Species does not exist!");
        }
    }

    // ==============================
    // UPDATE MUNICIPALITY TEST
    // ==============================

    /*
    @Test
    public void testUpdateMunicipality() throws Exception {
        JSONObject oldMunicipality = new JSONObject();
        oldMunicipality.put("name", testMunicipality.get("name"));
        oldMunicipality.put("totalTrees", testMunicipality.get("totalTrees"));
        JSONArray borders = new JSONArray();

        borders.put(new JSONArray(new double[]{35.497470, -73.772830}));
        borders.put(new JSONArray(new double[]{35.481864, -73.773715}));
        borders.put(new JSONArray(new double[]{35.460268, -73.750029}));
        borders.put(new JSONArray(new double[]{35.481208, -73.723422}));
        borders.put(new JSONArray(new double[]{35.459034, -73.683652}));
        borders.put(new JSONArray(new double[]{35.526536, -73.651208}));
        borders.put(new JSONArray(new double[]{35.522407, -73.730198}));
        borders.put(new JSONArray(new double[]{35.528407, -73.730198}));
        borders.put(new JSONArray(new double[]{35.538407, -73.730198}));

        oldMunicipality.put("borders", borders);

        service.createMunicipality(oldMunicipality);
        try {
            service.updateMunicipality(testMunicipality);
            assertEquals(true, Municipality.hasWithName(testMunicipality.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdateMunicipalityEmptyBorders() {
        JSONObject municipality = new JSONObject();
        municipality.put("name", "Saint-Lazare");
        municipality.put("totalTrees", 12);
        municipality.put("borders", new JSONArray());

        try {
            service.createMunicipality(municipality);
            assertEquals(true, Municipality.hasWithName(municipality.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }

    @Test(expected = InvalidInputException.class)
    public void testUpdateMunicipalityTwoBorders() throws Exception {
        JSONObject municipality = new JSONObject();
        municipality.put("name", "Saint-Lazare");
        municipality.put("totalTrees", 12);

        JSONArray borders = new JSONArray();
        borders.put(new JSONArray(new double[]{45.397067, -74.152067}));
        borders.put(new JSONArray(new double[]{45.411974, -74.152188}));
        municipality.put("borders", borders);

        service.createMunicipality(municipality);
    }*/


    // ==============================
    // DELETE USER TEST
    // ==============================

    @Test
    public void testDeleteUser() throws Exception {
        service.createUser(testUser);

        JSONObject deleteUser = new JSONObject();
        deleteUser.put("username", testUser.getString("username"));

        try {
            User user = service.deleteUser(deleteUser);

            assertEquals(testUser.getString("username"), user.getUsername());
            assertEquals(testUser.getString("password"), user.getPassword());
            assertEquals(UserRole.valueOf(testUser.getString("role")), user.getRole());
            assertEquals(testUser.getString("myAddresses"), user.getMyAddress(0));
            assertEquals(false, User.hasWithUsername(testUser.getString("username")));
            try {
                service.getUserByUsername(testUser.getString("username"));
            } catch (InvalidInputException e) {
                assertEquals("That username doesn't exist!", e.getMessage());
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testDeleteUserEmpty() throws Exception {
        JSONObject deleteUser = new JSONObject();
        deleteUser.put("username", "      ");

        try {
            service.deleteUser(deleteUser);
        } catch (InvalidInputException e) {
            assertEquals ("User is not logged in/Username is missing!", e.getMessage());
        }
    }

    @Test(expected = JSONException.class)
    public void testDeleteUserNull() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", (String) null);

        service.deleteUser(user);
    }

    @Test
    public void testDeleteUserNonExistant() throws Exception {
        JSONObject deleteUser = new JSONObject();
        deleteUser.put("username", testUser.getString("username"));

        try {
            service.deleteUser(deleteUser);
        } catch (InvalidInputException e) {
            assertEquals("That username doesn't exist!", e.getMessage());
        }
    }


    // ==============================
    // DELETE SPECIES TEST
    // ==============================

    @Test
    public void testDeleteSpecies() throws Exception {
        service.createSpecies(testSpecies);

        JSONObject deleteSpecies = new JSONObject();
        deleteSpecies.put("name", testSpecies.getString("name"));

        try {
            Species species = service.deleteSpecies(deleteSpecies);

            assertEquals(testSpecies.getString("name"), species.getName());
            assertEquals(testSpecies.getString("species"), species.getSpecies());
            assertEquals(testSpecies.getString("genus"), species.getGenus());
            assertEquals(false, Species.hasWithName(testSpecies.getString("name")));
            // try {
            //     service.getSpeciesByName(testSpecies.getString("name"));
            // } catch (InvalidInputException e) {
            //     assertEquals("No Species with that name exists!", e.getMessage());
            // }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testDeleteSpeciesEmpty() throws Exception {
        JSONObject deleteSpecies = new JSONObject();
        deleteSpecies.put("name", "      ");

        try {
            service.deleteSpecies(deleteSpecies);
        } catch (InvalidInputException e) {
            assertEquals("Species' name is missing!", e.getMessage());
        }
    }

    @Test(expected = JSONException.class)
    public void testDeleteSpeciesNull() throws Exception {
        JSONObject deleteSpecies = new JSONObject();
        deleteSpecies.put("name", (String) null);

        service.deleteSpecies(deleteSpecies);
    }

    @Test
    public void testDeleteSpeciesNonExistant() throws Exception {
        JSONObject deleteSpecies = new JSONObject();
        deleteSpecies.put("name", testSpecies.getString("name"));

        try {
            service.deleteSpecies(deleteSpecies);
        } catch (InvalidInputException e) {
            assertEquals("No Species with that name exists!", e.getMessage());
        }
    }


    // ==============================
    // DELETE LOCATION TEST
    // ==============================

    @Test
    public void testDeleteLocation() throws Exception {
        Location locationObj = new Location(testLocation.getDouble("latitude"), testLocation.getDouble("longitude"));
        sql.insertLocation(locationObj.getLocationId(), locationObj.getLatitude(), locationObj.getLongitude());


        JSONObject deleteLocation = new JSONObject();
        deleteLocation.put("locationId", locationObj.getLocationId());

        try {
            Location location = service.deleteLocation(deleteLocation);

            assertEquals(testLocation.getDouble("latitude"), location.getLatitude(), 0);
            assertEquals(testLocation.getDouble("longitude"), location.getLongitude(), 0);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testDeleteLocationNegativeId() throws Exception {
        JSONObject deleteLocation = new JSONObject();
        deleteLocation.put("locationId", -1);

        try {
            service.deleteLocation(deleteLocation);
        } catch (InvalidInputException e) {
            assertEquals("Location's ID cannot be negative or zero!", e.getMessage());
        }
    }

    @Test
    public void testDeleteLocationNonExistant() throws Exception {
        JSONObject deleteLocation = new JSONObject();
        deleteLocation.put("locationId", testLocation.getInt("locationId"));

        try {
            service.deleteLocation(deleteLocation);
        } catch (InvalidInputException e) {
            assertEquals("No Location with that ID exists!", e.getMessage());
        }
    }


    // ==============================
    // DELETE MUNICIPALITY TEST
    // ==============================

    @Test
    public void testDeleteMunicipality() throws Exception {
        service.createMunicipality(testMunicipality);

        JSONObject deleteMunicipality = new JSONObject();
        deleteMunicipality.put("name", testMunicipality.getString("name"));

        try {
            Municipality municipality = service.deleteMunicipality(deleteMunicipality);

            assertEquals(testMunicipality.getString("name"), municipality.getName());
            assertEquals(testMunicipality.getInt("totalTrees"), municipality.getTotalTrees());
            assertEquals(false, Municipality.hasWithName(testMunicipality.getString("name")));

            for (int i = 0; i < municipality.getBorders().size(); i++) {
                assertEquals(testMunicipality.getJSONArray("borders").getJSONArray(i).getDouble(0),
                             municipality.getBorder(i).getLatitude(), 0);
                assertEquals(testMunicipality.getJSONArray("borders").getJSONArray(i).getDouble(1),
                             municipality.getBorder(i).getLongitude(), 0);
            }
            try {
                service.getMunicipalityByName(testMunicipality.getString("name"));
            } catch (InvalidInputException e) {
                assertEquals("No Municipality with that name exists!", e.getMessage());
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testDeleteMunicipalityEmpty() throws Exception {
        JSONObject deleteMunicipality = new JSONObject();
        deleteMunicipality.put("name", "    ");

        try {
            service.deleteMunicipality(deleteMunicipality);
        } catch (InvalidInputException e) {
            assertEquals("Municipality's name is missing!", e.getMessage());
        }
    }

    @Test(expected = JSONException.class)
    public void testDeleteMunicipalityNull() throws Exception {
        JSONObject deleteMunicipality = new JSONObject();
        deleteMunicipality.put("name", (String) null);

        service.deleteMunicipality(deleteMunicipality);
    }

    @Test
    public void testDeleteMunicipalityNonExistant() throws Exception {
        JSONObject deleteMunicipality = new JSONObject();
        deleteMunicipality.put("name", testMunicipality.getString("name"));

        try {
            service.deleteMunicipality(deleteMunicipality);
        } catch (InvalidInputException e) {
            assertEquals("No Municipality with that name exists!", e.getMessage());
        }
    }


    // ==============================
    // DELETE TREE TEST
    // ==============================

    @Test
    public void testDeleteTree() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        JSONObject treeObj = testTree.getJSONObject("tree");
        JSONObject deleteTree = new JSONObject();
        deleteTree.put("user", testUser.getString("username"));
        deleteTree.put("treeId", treeObj.getInt("treeId"));

        try {
            Tree tree = service.deleteTree(deleteTree);

            assertEquals(treeObj.getInt("height"), tree.getHeight());
            assertEquals(treeObj.getInt("diameter"), tree.getDiameter());
            assertEquals(Date.valueOf(treeObj.getString("datePlanted")), tree.getDatePlanted());
            assertEquals(Land.valueOf(treeObj.getString("land")), tree.getLand());
            assertEquals(Status.valueOf(treeObj.getString("status")), tree.getStatus());
            assertEquals(Ownership.valueOf(treeObj.getString("ownership")), tree.getOwnership());
            assertEquals(treeObj.getString("species"), tree.getSpecies().getName());
            assertEquals(treeObj.getDouble("latitude"), tree.getLocation().getLatitude(), 0);
            assertEquals(treeObj.getDouble("longitude"), tree.getLocation().getLongitude(), 0);
            assertEquals(treeObj.getString("municipality"), tree.getMunicipality().getName());
        } catch (InvalidInputException e) {
            fail();
        }
    }

    @Test
    public void testDeleteTreeNegativeTreeID() throws Exception {
    	String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        JSONObject deleteTree = new JSONObject();
        deleteTree.put("user", testUser.getString("username"));
        deleteTree.put("treeId", -1);

        try {
        	service.deleteTree(deleteTree);
        } catch (InvalidInputException e) {
            error = e.getMessage();
        }
        assertEquals("Tree's ID cannot be negative or zero!", error);
    }

    @Test
    public void testDeleteTreeUserNameEmpty() throws Exception {
    	String error = "";
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        JSONObject deleteTree = new JSONObject();
        deleteTree.put("user", "             ");
        deleteTree.put("treeId", 3);

        try {
        	service.deleteTree(deleteTree);
        } catch (InvalidInputException e) {
            error = e.getMessage();
        }
        assertEquals("User is not logged in/Username is missing!", error);
    }
    @Test
    public void testDeleteTreeNonExistantTree() throws Exception {
        JSONObject deleteTree = new JSONObject();
        deleteTree.put("user", testUser.getString("username"));
        deleteTree.put("treeId", testTree.getJSONObject("tree").getInt("treeId"));

        try {
           service.deleteTree(deleteTree);
        } catch (InvalidInputException e) {
           assertEquals("No Tree with that ID exists!", e.getMessage());
        }
    }

    @Test
    public void testDeleteTreeNonExistantUser() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        JSONObject deleteTree = new JSONObject();
        deleteTree.put("user", "IDontExist");
        deleteTree.put("treeId", testTree.getJSONObject("tree").getInt("treeId"));

        try {
            service.deleteTree(deleteTree);
        } catch (InvalidInputException e) {
            assertEquals("That username doesn't exist!", e.getMessage());
        }
    }

    @Test
    public void testDeleteTreeUserDoesntOwnTree() throws Exception {
        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);
        service.createTree(testTree);

        JSONObject user = new JSONObject(testUser.toString());
        user.put("username", "RandomUser");
        service.createUser(user);

        JSONObject deleteTree = new JSONObject();
        deleteTree.put("user", user.getString("username"));
        deleteTree.put("treeId", testTree.getJSONObject("tree").getInt("treeId"));

        try {
            service.deleteTree(deleteTree);
        } catch (InvalidInputException e) {
            assertEquals("This Tree wasn't planted by you!", e.getMessage());
        }
    }

    //TODO
    // ==============================
    // DELETE FORECAST
    // ==============================
    
    @Test
    public void testDeleteForecast() throws Exception {
    	JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", "Abbas");
        testForecast.put("fcTrees", trees);
        
        service.createForecast(testForecast);
        
        try {
        	JSONObject forecastObj = new JSONObject();
        	forecastObj.put("forecastId", Forecast.getNextForecastId() - 1);
        	forecastObj.put("user", "Abbas");
        	
        	Forecast forecast = service.deleteForecast(forecastObj);

            assertEquals(Date.valueOf("2018-04-16"), forecast.getFcDate());
            assertEquals("Abbas", forecast.getFcUser());

            for (int i = 0; i < trees.length(); i++) {
                Tree tree = service.getTreeById(i + 1);
                Tree treeFc = forecast.getFcTree(i);
                assertEquals(tree.getHeight(), treeFc.getHeight());
                assertEquals(tree.getDiameter(), treeFc.getDiameter());
                assertEquals(tree.getAddress(),treeFc.getAddress());
                assertEquals(tree.getDatePlanted(),treeFc.getDatePlanted());
                assertEquals(tree.getLand(),treeFc.getLand());
                assertEquals(tree.getLocation().getLatitude(),treeFc.getLocation().getLatitude(),0);
                assertEquals(tree.getLocation().getLongitude(),treeFc.getLocation().getLongitude(),0);
                assertEquals(tree.getMunicipality(),treeFc.getMunicipality());
                assertEquals(tree.getOwnership(), treeFc.getOwnership());
                assertEquals(tree.getSpecies(), treeFc.getSpecies());
                assertEquals(tree.getStatus(), treeFc.getStatus());
            }

            assertEquals(4*service.getCO2Sequestered(service.getTreeById(trees.getInt(0))),forecast.getCo2Reduced(), 0.01);
            assertEquals(4*service.getStormwaterIntercepted(service.getTreeById(trees.getInt(0))),forecast.getStormwater(), 0.01);
            assertEquals(4*service.getEnergyConserved(service.getTreeById(trees.getInt(0))),forecast.getEnergyConserved(), 0.01);
            assertEquals(0.25,forecast.getBiodiversity(), 0.01);
        	
        }catch(Exception e){
        	fail();
        }
    }
    
    @Test(expected = JSONException.class)
    public void testDeleteForecastUserNull() throws Exception {
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", "Abbas");
        testForecast.put("fcTrees", trees);

        service.createForecast(testForecast);
        
        JSONObject forecastObj = new JSONObject();
    	forecastObj.put("forecastId", Forecast.getNextForecastId() - 1);
    	forecastObj.put("user", (String) null);
    	
    	service.deleteForecast(forecastObj);
    }

    @Test
    public void testUpdateForecastUsernamespaces() throws Exception {
        String error = "";
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", "Abbas");
        testForecast.put("fcTrees", trees);

        service.createForecast(testForecast);
        try {
        	JSONObject forecastObj = new JSONObject();
        	forecastObj.put("forecastId", Forecast.getNextForecastId() - 1);
        	forecastObj.put("user", "          ");
        	
        	service.deleteForecast(forecastObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "User is not logged in/Username is missing!");
    }

    @Test
    public void testUpdateForecastUserDNE() throws Exception {
        String error = "";
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", "Abbas");
        testForecast.put("fcTrees", trees);
        service.createForecast(testForecast);

        try {
            JSONObject forecastObj = new JSONObject();
        	forecastObj.put("forecastId", Forecast.getNextForecastId() - 1);
        	forecastObj.put("user", "Gareth");
        	
        	service.deleteForecast(forecastObj);
        } catch (Exception e) {
            error = e.getMessage();
        }

        assertEquals(error, "That username doesn't exist!");
    }
    
    @Test
    public void testDeleteForecastNegativeId() throws Exception {
    	String error = "";
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", "Abbas");
        testForecast.put("fcTrees", trees);
        service.createForecast(testForecast);

        try {
            JSONObject forecastObj = new JSONObject();
        	forecastObj.put("forecastId", -1);
        	forecastObj.put("user", "Abbas");
        	
        	service.deleteForecast(forecastObj);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "Forecast's ID cannot be negative or zero!");
    }
    
    @Test
    public void testDeleteForecastNonexistantForecast() throws Exception {
    	String error = "";
        service.createUser(testUser);

        try {
            JSONObject forecastObj = new JSONObject();
        	forecastObj.put("forecastId", Forecast.getNextForecastId());
        	forecastObj.put("user", "Abbas");
        	
        	service.deleteForecast(forecastObj);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assertEquals(error, "No Forecast with that ID exists!");
    }
    
    @Test
    public void testDeleteForecastNotCreatedByUser() throws Exception {
    	String error = "";
        JSONObject testForecast = new JSONObject();
        JSONArray trees = new JSONArray();

        service.createUser(testUser);
        service.createSpecies(testSpecies);
        service.createMunicipality(testMunicipality);

        for (int i = 0; i < 4; i++) {
            Tree tree = service.createTree(testTree);
            trees.put(tree.getTreeId());
        }

        testForecast.put("fcDate", "2018-04-16");
        testForecast.put("fcUser", "Abbas");
        testForecast.put("fcTrees", trees);
        service.createForecast(testForecast);
        
        JSONObject secondUser = new JSONObject();

        secondUser.put("username", "Gareth");
        secondUser.put("password", "ecse321pw");
        secondUser.put("role", "Resident");
        secondUser.put("scientistKey", "");
        secondUser.put("myAddresses", "H4L3N1");
        
        service.createUser(secondUser);
        
        try {
        	JSONObject forecastObj = new JSONObject();
        	forecastObj.put("forecastId", Forecast.getNextForecastId()-1);
        	forecastObj.put("user", "Gareth");
        	service.deleteForecast(forecastObj);
        }catch(Exception e) {
        	error = e.getMessage();
        }
        assertEquals(error, "This Forecast wasn't created by you!");
        
    }


    // ==============================
    // BUILD TEST OBJECTS API
    // ==============================

    public static JSONObject buildTestTree() {
        JSONObject testTree = new JSONObject();
        JSONObject tree = new JSONObject();

        tree.put("treeId", 1);
        tree.put("height", 420);
        tree.put("diameter", 40);
        tree.put("datePlanted", "2018-03-16");
        tree.put("land", "Residential");
        tree.put("status", "Planted");
        tree.put("ownership", "Private");
        tree.put("species", "Weeping Willow");
        tree.put("latitude", 45.515883);
        tree.put("longitude", -73.685552);
        tree.put("municipality", "Saint-Laurent");

        testTree.put("user", "Abbas");
        testTree.put("tree", tree);

        return testTree;
    }

    public static JSONObject buildTestUser() {
        JSONObject testUser = new JSONObject();

        testUser.put("username", "Abbas");
        testUser.put("password", "ecse321pw");
        testUser.put("role", "Resident");
        testUser.put("scientistKey", "");
        testUser.put("myAddresses", "H4L3N1");

        return testUser;
    }

    public static JSONObject buildTestSpecies() {
        JSONObject testSpecies = new JSONObject();

        testSpecies.put("name", "Weeping Willow");
        testSpecies.put("species", "Salix Babylonica");
        testSpecies.put("genus", "Salix");

        return testSpecies;
    }

    public static JSONObject buildTestLocation() {
        JSONObject testLocation = new JSONObject();

        testLocation.put("locationId", 1);
        testLocation.put("latitude", 45.515897);
        testLocation.put("longitude", -73.685548);

        return testLocation;
    }

    public static JSONObject buildTestMunicipality() {
        JSONObject testMunicipality = new JSONObject();
        JSONArray borders = new JSONArray();

        borders.put(new JSONArray(new double[]{45.497470, -73.772830}));
        borders.put(new JSONArray(new double[]{45.481864, -73.773715}));
        borders.put(new JSONArray(new double[]{45.460268, -73.750029}));
        borders.put(new JSONArray(new double[]{45.481208, -73.723422}));
        borders.put(new JSONArray(new double[]{45.459034, -73.683652}));
        borders.put(new JSONArray(new double[]{45.526536, -73.651208}));
        borders.put(new JSONArray(new double[]{45.522407, -73.730198}));

        testMunicipality.put("name", "Saint-Laurent");
        testMunicipality.put("totalTrees", 55);
        testMunicipality.put("borders", borders);

        return testMunicipality;
    }
}
