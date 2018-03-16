package ca.mcgill.ecse321.treeple.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.json.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.sqlite.SQLiteJDBC;

public class TestTreePLEService {

    private static SQLiteJDBC sql;
    private static TreePLEService service;
    private static final String dbPath = "/output/treeple_test.db";

    @BeforeClass
    public static void setUpBeforeClass() {
        sql = new SQLiteJDBC(dbPath);
        sql.connect();
        service = new TreePLEService(sql);
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
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "Scientist");
        user.put("myAddresses", "St-Lazare");

        try {
            service.createUser(user);
            assertEquals(true, User.hasWithUsername(user.getString("username")));
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
        user.put("myAddresses", "St-Lazare");

        service.createUser(user);
    }

    @Test(expected = JSONException.class)
    public void testCreateUserNullPassword() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", (String) null);
        user.put("role", "Scientist");
        user.put("myAddresses", "St-Lazare");

        service.createUser(user);
    }

    @Test(expected = JSONException.class)
    public void testCreateUserNullRole() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", (String) null);
        user.put("myAddresses", "St-Lazare");

        service.createUser(user);
    }

    @Test(expected = JSONException.class)
    public void testCreateUserNullAddress() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "Scientist");
        user.put("myAddresses", (String) null);

        service.createUser(user);
    }

    @Test
    public void testCreateUserBadRole() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "NotARealRole");
        user.put("myAddresses", "St-Lazare");

        try {
            service.createUser(user);
        } catch(InvalidInputException e) {
            assertEquals("That role doesn't exist!", e.getMessage());
        }
    }

    @Test
    public void testCreateUserResidentialWithEmptyAddress() throws Exception {
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "Resident");
        user.put("myAddresses", "   ");

        try {
            service.createUser(user);
        } catch(InvalidInputException e) {
            assertEquals("Address cannot be empty!", e.getMessage());
        }
    }


    // ==============================
    // CREATE SPECIES TEST
    // ==============================

    @Test
    public void testCreateSpecies() {
        JSONObject species = new JSONObject();
        species.put("name", "Maple");
        species.put("species", "Pseudoplatanus");
        species.put("genus", "Acer");

        try {
            service.createSpecies(species);
            assertEquals(true, Species.hasWithName(species.getString("name")));
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

    @Test(expected = JSONException.class)
    public void testCreateSpeciesEmptyName() throws Exception {
        JSONObject species = new JSONObject();
        species.put("name", (String) null);
        species.put("species", "Pseudoplatanus");
        species.put("genus", "Acer");

        service.createSpecies(species);
    }


    // ==============================
    // CREATE MUNICIPALITY TEST
    // ==============================

    @Test
    public void testCreateMunicipality() {
        JSONObject municipality = new JSONObject();
        municipality.put("name", "Saint-Laurent");
        municipality.put("totalTrees", 12);

        JSONArray borders = new JSONArray();
        borders.put(new JSONArray(new double[]{45.497470, -73.772830}));
        borders.put(new JSONArray(new double[]{45.481864, -73.773715}));
        borders.put(new JSONArray(new double[]{45.460268, -73.750029}));
        borders.put(new JSONArray(new double[]{45.481208, -73.723422}));
        borders.put(new JSONArray(new double[]{45.459034, -73.683652}));
        borders.put(new JSONArray(new double[]{45.526536, -73.651208}));
        borders.put(new JSONArray(new double[]{45.522407, -73.730198}));
        municipality.put("borders", borders);

        try {
            service.createMunicipality(municipality);
            assertEquals(true, Municipality.hasWithName(municipality.getString("name")));
        } catch (Exception e) {
            fail();
        }
    }


    public void testCreateMunicipalityEmptyBorders() {
        JSONObject municipality = new JSONObject();
        municipality.put("name", "Saint-Laurent");
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
    public void testCreateMunicipalityTwoBorders() throws Exception {
        JSONObject municipality = new JSONObject();
        municipality.put("name", "Saint-Laurent");
        municipality.put("totalTrees", 12);

        JSONArray borders = new JSONArray();
        borders.put(new JSONArray(new double[]{45.497470, -73.772830}));
        borders.put(new JSONArray(new double[]{45.481864, -73.773715}));
        municipality.put("borders", borders);

        service.createMunicipality(municipality);
    }


    // ==============================
    // CREATE TREE TEST
    // ==============================



}
