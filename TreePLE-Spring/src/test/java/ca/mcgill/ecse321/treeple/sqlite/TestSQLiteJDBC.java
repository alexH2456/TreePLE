package ca.mcgill.ecse321.treeple.sqlite;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONObject;
import org.junit.*;

import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.service.TreePLEService;

public class TestSQLiteJDBC {

    private static SQLiteJDBC sql;
    private static TreePLEService service;
    private static final String dbPath = "/output/treeple_test.db";


    private static JSONObject defaultUser;
    private static JSONObject defaultSpecies;
    private static JSONObject defaultLocation;
    private static JSONObject defaultMun;

    private static final int numTrees = 5;
    private static final int numUsers = 5;
    private static final int numSpecies = 5;
    private static final int numLocations = 5;
    private static final int numMunicipalities = 5;

    @BeforeClass
    public static void setUpBeforeClass() {
        sql = new SQLiteJDBC(dbPath);
        sql.connect();
        service = new TreePLEService(sql);
    }

    @AfterClass
    public static void tearDownAfterClass() {
        if (sql != null) {
            sql.deleteDB();
            sql.closeConnection();
        }
    }

    @Before
    public void setUp() {
        // Default User Setup
        defaultUser = new JSONObject();
        defaultUser.put("username", "testUser");
        defaultUser.put("password", "testPassword");
        defaultUser.put("role", "Resident");
        defaultUser.put("addresses", "2030 Mulberry Street, Montreal, QC, Canada J01 10J");
        defaultUser.put("trees", "");

        // Default Species Setup
        defaultSpecies = new JSONObject();
        defaultSpecies.put("name", "Maple");
        defaultSpecies.put("species", "pseudoplatanus");
        defaultSpecies.put("genus", "Acer");

        // Default Location Setup
        defaultLocation = new JSONObject();
        defaultLocation.put("locationId", 1);
        defaultLocation.put("latitude", 45.0);
        defaultLocation.put("longitude", 45.0);

        // Default Municipality Setup
        defaultMun = new JSONObject();
        defaultMun.put("name", "Pointe-Claire");
        defaultMun.put("totalTrees", 10);
        defaultMun.put("borders", "1, 2");
    }

    @After
    public void tearDown() throws Exception {
        JSONObject dbAccessKey = new JSONObject();
        dbAccessKey.put("dbAccessKey", "ih8tr33s");
        service.resetDatabase(dbAccessKey);
    }

    @Test
    public void testResetDB() {
        assertEquals(true, sql.resetDB());
    }

    @Test
    public void testDeleteDB() {
        File dbFile = (new File(System.getProperty("user.dir") + dbPath)).getAbsoluteFile();
        assertEquals(true, dbFile.exists());
        assertEquals(true, sql.deleteDB());
        assertEquals(false, dbFile.exists());
    }

    // ======================
    // USER TESTS
    // ======================

    @Test
    public void testInsertUser() {
        ArrayList<String> addrList = new ArrayList<String>();

        for (String addressId : defaultUser.getString("addresses").split(",")) {
            if (addressId != null && !addressId.replaceAll("\\s", "").isEmpty()) {
                addrList.add(addressId.replaceAll("\\s", ""));
            }
        }

        if (sql.getUser(defaultUser.getString("username")) != null) {
            sql.deleteUser(defaultUser.getString("username"));
        }

        boolean success = sql.insertUser(
                defaultUser.getString("username"),
                defaultUser.getString("password"),
                defaultUser.getString("role"),
                defaultUser.getString("addresses"),
                defaultUser.getString("trees"));

        assertEquals(true, success);

        User user = sql.getUser(defaultUser.getString("username"));
        assertEquals(user.getUsername(), defaultUser.getString("username"));
        assertEquals(user.getPassword(), defaultUser.getString("password"));
        assertEquals(user.getRole().name(), defaultUser.getString("role"));

        int i = 0;
        for (String s : user.getMyAddresses()) {
            assertEquals(s, addrList.get(i));
            i++;
        }

        // New user should have no trees
        assertEquals(user.getMyTrees().length, 0);
    }

    @Test
    public void testGetUser() {
        for (int i = 0; i < numUsers; i++) {
            JSONObject newUser = new JSONObject();
            newUser.put("username", defaultUser.getString("username") + i);
            newUser.put("password", defaultUser.getString("password") + i);
            newUser.put("role", defaultUser.getString("role"));
            newUser.put("addresses", defaultUser.getString("addresses"));
            newUser.put("trees", defaultUser.getString("trees"));

            boolean success = sql.insertUser(
                    newUser.getString("username"),
                    newUser.getString("password"),
                    newUser.getString("role"),
                    newUser.getString("addresses"),
                    newUser.getString("trees"));
            assertEquals(true, success);
        }

        for (int i = 0; i < numUsers; i++) {
            User user = sql.getUser(defaultUser.getString("username") + i);
            assertNotEquals(null, user);
            assertEquals(defaultUser.getString("username") + i, user.getUsername());
            assertEquals(defaultUser.getString("password") + i, user.getPassword());
            assertEquals(user.getRole().name(), defaultUser.getString("role"));
        }
    }

    @Test
    public void testGetAllUsers() {
        for (int i = 0; i < numUsers; i++) {
            JSONObject newUser = new JSONObject();
            newUser.put("username", defaultUser.getString("username") + i);
            newUser.put("password", defaultUser.getString("password") + i);
            newUser.put("role", defaultUser.getString("role"));
            newUser.put("addresses", defaultUser.getString("addresses"));
            newUser.put("trees", defaultUser.getString("trees"));

            boolean success = sql.insertUser(
                    newUser.getString("username"),
                    newUser.getString("password"),
                    newUser.getString("role"),
                    newUser.getString("addresses"),
                    newUser.getString("trees"));
            assertEquals(true, success);
        }

        ArrayList<User> users = sql.getAllUsers();
        assertEquals(numUsers, users.size());

        int i = 0;
        for (User user : users) {
            assertEquals(user.getUsername(), defaultUser.getString("username") + i);
            assertEquals(user.getPassword(), defaultUser.getString("password") + i);
            assertEquals(user.getRole().name(), defaultUser.getString("role"));
            i++;
        }
    }

    @Test
    public void testDeleteUser() {
        for (int i = 0; i < numUsers; i++) {
            JSONObject newUser = new JSONObject();
            newUser.put("username", defaultUser.getString("username") + i);
            newUser.put("password", defaultUser.getString("password") + i);
            newUser.put("role", defaultUser.getString("role"));
            newUser.put("addresses", defaultUser.getString("addresses"));
            newUser.put("trees", defaultUser.getString("trees"));

            boolean success = sql.insertUser(
                    newUser.getString("username"),
                    newUser.getString("password"),
                    newUser.getString("role"),
                    newUser.getString("addresses"),
                    newUser.getString("trees"));
            assertEquals(true, success);
        }

        for (int i = 0; i < numUsers; i++) {
            boolean success = sql.deleteUser(defaultUser.getString("username") + i);
            assertEquals(true, success);
        }

        // Check if all users removed
        assertEquals(0, sql.getAllUsers().size());
    }

    @Test
    public void testUpdateUserPassword() {
        for (int i = 0; i < numUsers; i++) {
            JSONObject newUser = new JSONObject();
            newUser.put("username", defaultUser.getString("username") + i);
            newUser.put("password", defaultUser.getString("password") + i);
            newUser.put("role", defaultUser.getString("role"));
            newUser.put("addresses", defaultUser.getString("addresses"));
            newUser.put("trees", defaultUser.getString("trees"));

            boolean success = sql.insertUser(
                    newUser.getString("username"),
                    newUser.getString("password"),
                    newUser.getString("role"),
                    newUser.getString("addresses"),
                    newUser.getString("trees"));
            assertEquals(true, success);
        }

        String newPassword = "newPassword";
        String newRole = "Scientist";
        String newAddress = "H9O1O2";
        for (int i = 0; i < numUsers; i++) {
            boolean success = sql.updateUser(defaultUser.getString("username") + i, newPassword + i, newRole, newAddress);
            assertEquals(true, success);
        }

        int i = 0;
        for (User user : sql.getAllUsers()) {
            assertEquals(newPassword + i, user.getPassword());
            i++;
        }
    }

    @Test
    public void testUpdateUserTrees() {
        JSONObject newUser = new JSONObject();
        newUser.put("username", defaultUser.getString("username"));
        newUser.put("password", defaultUser.getString("password"));
        newUser.put("role", defaultUser.getString("role"));
        newUser.put("addresses", defaultUser.getString("addresses"));
        newUser.put("trees", defaultUser.getString("trees"));
        boolean success = sql.insertUser(
                newUser.getString("username"),
                newUser.getString("password"),
                newUser.getString("role"),
                newUser.getString("addresses"),
                newUser.getString("trees"));
        assertEquals(true, success);

        sql.insertLocation(1, 45.0, 45.0);
        sql.insertLocation(2, 45.5, 45.5);
        sql.insertSpecies("Maple", "pinus", "Arbus");
        sql.insertMunicipality("Vaudreuil", 0, "46.0, 46.0");
        sql.insertTree(
                1,
                10,
                20,
                defaultUser.getString("addresses"),
                "2018-12-22",
                "Residential",
                "Planted",
                "Private",
                "Maple",
                1,
                "Vaudreuil",
                "");
        sql.insertTree(
                2,
                10,
                20,
                defaultUser.getString("addresses"),
                "2018-12-22",
                "Residential",
                "Planted",
                "Private",
                "Maple",
                2,
                "Vaudreuil",
                "");

        String trees = "1, 2";
        success = sql.updateUserTrees(defaultUser.getString("username"), trees);
        assertEquals(true, success);

        int[] testTrees = {1, 2};
        for (User user : sql.getAllUsers()) {
            Integer[] userTrees = user.getMyTrees();
            int i = 0;
            for (int tree : userTrees) {
                assertEquals(testTrees[i], tree);
                i++;
            }
        }
    }

    // ======================
    // SPECIES TESTS
    // ======================

    @Test
    public void testInsertSpecies() {
        boolean success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);

        Species species = sql.getSpecies(defaultSpecies.getString("name"));
        assertNotEquals(null, species);
        assertEquals(defaultSpecies.getString("name"), species.getName());
        assertEquals(defaultSpecies.getString("species"), species.getSpecies());
        assertEquals(defaultSpecies.getString("genus"), species.getGenus());
    }

    @Test
    public void testUpdateSpecies() {
        boolean success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);

        String newSpecies = "laurinum";
        String newGenus = "Salix";
        success = sql.updateSpecies(defaultSpecies.getString("name"), newSpecies, newGenus);
        assertEquals(true, success);

        Species species = sql.getSpecies(defaultSpecies.getString("name"));
        assertEquals(defaultSpecies.getString("name"), species.getName());
        assertEquals(newSpecies, species.getSpecies());
        assertEquals(newGenus, species.getGenus());
    }

    @Test
    public void testGetAllSpecies() {
        for (int i = 0; i < numSpecies; i++) {
            boolean success = sql.insertSpecies(defaultSpecies.getString("name") + i, defaultSpecies.getString("species") + i, defaultSpecies.getString("genus") + i);
            assertEquals(true, success);
        }

        int i = 0;
        for (Species species : sql.getAllSpecies()) {
            assertEquals(defaultSpecies.getString("name") + i, species.getName());
            assertEquals(defaultSpecies.getString("species") + i, species.getSpecies());
            assertEquals(defaultSpecies.getString("genus") + i, species.getGenus());
            i++;
        }
    }

    @Test
    public void getAllTreesOfSpecies() {

        boolean success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);

        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        sql.insertLocation(2, 50.8, 40.2);
        sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);

        try {
            ArrayList<Tree> trees = sql.getAllTreesOfSpecies("Maple");
            for(Tree tree : trees) {
                assertEquals(tree.getTreeId(), treeId);
                assertEquals(tree.getHeight(), height);
                assertEquals(tree.getDiameter(), diameter);
                assertEquals(tree.getAddress(), address);
                assertEquals(tree.getDatePlanted().toString(), datePlanted);
                assertEquals(tree.getLand().name(), land);
                assertEquals(tree.getStatus().name(), status);
                assertEquals(tree.getOwnership().name(), ownership);
                assertEquals(tree.getSpecies().getName(), species);
                assertEquals(tree.getLocation().getLocationId(), location);
                assertEquals(tree.getMunicipality().getName(), municipality);
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetSpecies() {
        for (int i = 0; i < numSpecies; i++) {
            boolean success = sql.insertSpecies(defaultSpecies.getString("name") + i, defaultSpecies.getString("species") + i, defaultSpecies.getString("genus") + i);
            assertEquals(true, success);
        }

        for (int i = 0; i < numSpecies; i++) {
            Species species = sql.getSpecies(defaultSpecies.getString("name") + i);
            assertNotEquals(null, species);
            assertEquals(defaultSpecies.getString("name") + i, species.getName());
            assertEquals(defaultSpecies.getString("species") + i, species.getSpecies());
            assertEquals(defaultSpecies.getString("genus") + i, species.getGenus());
        }
    }

    @Test
    public void testDeleteSpecies() {
        for (int i = 0; i < numSpecies; i++) {
            boolean success = sql.insertSpecies(defaultSpecies.getString("name") + i, defaultSpecies.getString("species") + i, defaultSpecies.getString("genus") + i);
            assertEquals(true, success);
        }

        for (int i = 0; i < numSpecies; i++) {
            boolean success = sql.deleteSpecies(defaultSpecies.getString("name") + i);
            assertEquals(true, success);
        }
        assertEquals(0, sql.getAllSpecies().size());
    }

    // ======================
    // LOCATIONS TESTS
    // ======================

    @Test
    public void testInsertLocation() {
        boolean success = sql.insertLocation(defaultLocation.getInt("locationId"), defaultLocation.getDouble("latitude"), defaultLocation.getDouble("longitude"));
        assertEquals(true, success);

        Location location = sql.getLocation(defaultLocation.getInt("locationId"));
        assertEquals(defaultLocation.getInt("locationId"), location.getLocationId());
        assertEquals(defaultLocation.getDouble("latitude"), location.getLatitude(), 0);
        assertEquals(defaultLocation.getDouble("longitude"), location.getLongitude(), 0);
    }

    @Test
    public void testUpdateLocation() {
        boolean success = sql.insertLocation(defaultLocation.getInt("locationId"), defaultLocation.getDouble("latitude"), defaultLocation.getDouble("longitude"));
        assertEquals(true, success);

        double newLat = 20.09;
        double newLong = 55.43;
        success = sql.updateLocation(defaultLocation.getInt("locationId"), newLat, newLong);
        assertEquals(true, success);

        Location location = sql.getLocation(defaultLocation.getInt("locationId"));
        assertEquals(defaultLocation.getInt("locationId"), location.getLocationId());
        assertEquals(newLat, location.getLatitude(), 0);
        assertEquals(newLong, location.getLongitude(), 0);
    }

    @Test
    public void testGetAllLocations() {
        for (int i = 0; i < numLocations; i++) {
            boolean success = sql.insertLocation(defaultLocation.getInt("locationId") + i, defaultLocation.getDouble("latitude") + i, defaultLocation.getDouble("longitude") + i);
            assertEquals(true, success);
        }

        int i = 0;
        for (Location location : sql.getAllLocations()) {
            assertEquals(defaultLocation.getInt("locationId") + i, location.getLocationId());
            assertEquals(defaultLocation.getDouble("latitude") + i, location.getLatitude(), 0);
            assertEquals(defaultLocation.getDouble("longitude") + i, location.getLongitude(), 0);
            i++;
        }
    }

    @Test
    public void testGetLocation() {
        for (int i = 0; i < numLocations; i++) {
            boolean success = sql.insertLocation(defaultLocation.getInt("locationId") + i, defaultLocation.getDouble("latitude") + i, defaultLocation.getDouble("longitude") + i);
            assertEquals(true, success);
        }

        for (int i = 0; i < numLocations; i++) {
            Location location = sql.getLocation(defaultLocation.getInt("locationId") + i);
            assertEquals(defaultLocation.getInt("locationId") + i, location.getLocationId());
            assertEquals(defaultLocation.getDouble("latitude") + i, location.getLatitude(), 0);
            assertEquals(defaultLocation.getDouble("longitude") + i, location.getLongitude(), 0);
        }
    }

    @Test
    public void testGetMaxLocationId() {
        for (int i = 0; i < numLocations; i++) {
            boolean success = sql.insertLocation(defaultLocation.getInt("locationId") + i, defaultLocation.getDouble("latitude") + i, defaultLocation.getDouble("longitude") + i);
            assertEquals(true, success);
        }

        int maxId = sql.getMaxLocationId();
        assertNotEquals(null, maxId);
        assertNotEquals(-1, maxId);
        assertEquals(defaultLocation.getInt("locationId") + numLocations - 1, maxId);
    }

    @Test
    public void testDeleteLocation() {
        for (int i = 0; i < numLocations; i++) {
            boolean success = sql.insertLocation(defaultLocation.getInt("locationId") + i, defaultLocation.getDouble("latitude") + i, defaultLocation.getDouble("longitude") + i);
            assertEquals(true, success);
        }

        for (int i = 0; i < numLocations; i++) {
            boolean success = sql.deleteLocation(defaultLocation.getInt("locationId") + i);
            assertEquals(true, success);
        }
        assertEquals(0, sql.getAllLocations().size());
    }

    // ======================
    // MUNICIPALITIES TESTS
    // ======================

    @Test
    public void testInsertMunicipality() {
        boolean success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        Municipality mun = sql.getMunicipality(defaultMun.getString("name"));
        assertEquals(defaultMun.getString("name"), mun.getName());
        assertEquals(defaultMun.getInt("totalTrees"), mun.getTotalTrees());

        int i = 0;
        int[] locIds = {1, 2};
        for (Location border : mun.getBorders()) {
            assertEquals(locIds[i], border.getLocationId());
            i++;
        }
    }

    @Test
    public void testUpdateMunicipality() {
        boolean success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(3, 33.7, 56.8);
        assertEquals(true, success);
        success = sql.insertLocation(4, 76.4, 64.1);
        assertEquals(true, success);

        String newBorders = "3, 4";
        success = sql.updateMunicipalityBorders(defaultMun.getString("name"), newBorders);
        assertEquals(true, success);

        Municipality mun = sql.getMunicipality(defaultMun.getString("name"));
        assertEquals(defaultMun.getString("name"), mun.getName());

        int i = 0;
        int[] locIds = {3, 4};
        for (Location border : mun.getBorders()) {
            assertEquals(locIds[i], border.getLocationId());
            i++;
        }
    }

    @Test
    public void testGetAllMunicipalities() {
        for (int i = 0; i < numMunicipalities; i++) {
            boolean success = sql.insertMunicipality(defaultMun.getString("name") + i, defaultMun.getInt("totalTrees") + i*10, defaultMun.getString("borders"));
            assertEquals(true, success);
        }

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        int i = 0;
        for (Municipality mun : sql.getAllMunicipalities()) {
            assertEquals(defaultMun.getString("name") + i, mun.getName());
            assertEquals(defaultMun.getInt("totalTrees") + i*10, mun.getTotalTrees());

            int k = 0;
            int[] locIds = {1, 2};
            for (Location border : mun.getBorders()) {
                assertEquals(locIds[k], border.getLocationId());
                k++;
            }
            i++;
        }
    }

    @Test
    public void testGetTreesOfMunicipality() {

        boolean success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);

        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        sql.insertLocation(2, 50.8, 40.2);
        sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);

        try {
            ArrayList<Tree> trees = sql.getAllTreesOfMunicipality(defaultMun.getString("name"));
            for(Tree tree: trees) {
                assertEquals(tree.getTreeId(), treeId);
                assertEquals(tree.getHeight(), height);
                assertEquals(tree.getDiameter(), diameter);
                assertEquals(tree.getAddress(), address);
                assertEquals(tree.getDatePlanted().toString(), datePlanted);
                assertEquals(tree.getLand().name(), land);
                assertEquals(tree.getStatus().name(), status);
                assertEquals(tree.getOwnership().name(), ownership);
                assertEquals(tree.getSpecies().getName(), species);
                assertEquals(tree.getLocation().getLocationId(), location);
                assertEquals(tree.getMunicipality().getName(), municipality);
            }
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetTreeCountOfMunicipality() {
        boolean success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);
        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);

        for(int i = 0; i < defaultMun.getInt("totalTrees"); i++) {
            success = sql.insertTree(treeId+i, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
            assertEquals(true, success);
        }

        try {
            int treeCount = sql.getTreeCountOfMunicipality(defaultMun.getString("name"));
            assertEquals(treeCount, defaultMun.getInt("totalTrees"));
        } catch (Exception e) {
            fail();
        }
    }
    @Test
    public void testGetMunicipality() {
        for (int i = 0; i < numMunicipalities; i++) {
            boolean success = sql.insertMunicipality(defaultMun.getString("name") + i, defaultMun.getInt("totalTrees") + i*10, defaultMun.getString("borders"));
            assertEquals(true, success);
        }

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        for (int i = 0; i < numMunicipalities; i++) {
            Municipality mun = sql.getMunicipality(defaultMun.getString("name") + i);
            assertEquals(defaultMun.getString("name") + i, mun.getName());
            assertEquals(defaultMun.getInt("totalTrees") + i*10, mun.getTotalTrees());

            int k = 0;
            int[] locIds = {1, 2};
            for (Location border : mun.getBorders()) {
                assertEquals(locIds[k], border.getLocationId());
                k++;
            }
        }
    }

    @Test
    public void testDeleteMunicipality() {
        for (int i = 0; i < numMunicipalities; i++) {
            boolean success = sql.insertMunicipality(defaultMun.getString("name") + i, defaultMun.getInt("totalTrees") + i*10, defaultMun.getString("borders"));
            assertEquals(true, success);
        }

        for (int i = 0; i < numMunicipalities; i++) {
            boolean success = sql.deleteMunicipality(defaultMun.getString("name") + i);
            assertEquals(true, success);
        }
        assertEquals(0, sql.getAllMunicipalities().size());
    }

    @Test
    public void testUpdateMunicipalityIncDecTrees() {
        boolean success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);
        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);

        for(int i = 0; i < defaultMun.getInt("totalTrees"); i++) {
            success = sql.insertTree(treeId+i, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
            assertEquals(true, success);
        }

        try {
            success = sql.updateMunicipalityIncDecTotalTrees(defaultMun.getString("name"), 1);
            assertEquals(true, success);
            assertEquals(defaultMun.getInt("totalTrees")+1, sql.getMunicipality(defaultMun.getString("name")).getTotalTrees());
        } catch (Exception e) {
            fail();
        }
    }

    // ======================
    // TREES TESTS
    // ======================

    @Test
    public void testInsertTree() {
        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        success = sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
        assertEquals(true, success);

        Tree tree = sql.getTree(treeId);

        assertEquals(tree.getTreeId(), treeId);
        assertEquals(tree.getHeight(), height);
        assertEquals(tree.getDiameter(), diameter);
        assertEquals(tree.getAddress(), address);
        assertEquals(tree.getDatePlanted().toString(), datePlanted);
        assertEquals(tree.getLand().name(), land);
        assertEquals(tree.getStatus().name(), status);
        assertEquals(tree.getOwnership().name(), ownership);
        assertEquals(tree.getSpecies().getName(), species);
        assertEquals(tree.getLocation().getLocationId(), location);
        assertEquals(tree.getMunicipality().getName(), municipality);
    }

    @Test
    public void testUpdateTree() {
        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        success = sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
        assertEquals(true, success);

        int newHeight = 39;
        int newDiameter = 15;
        String newLand = "Park";
        String newStatus = "Diseased";
        String newOwnership = "Public";
        String newSpecies = "Willow";
        String newMun = "Vaudreuil";

        success = sql.insertLocation(4, 34.9, 64.5);
        assertEquals(true, success);
        success = sql.insertLocation(5, 80.8, 30.2);
        assertEquals(true, success);

        success = sql.insertSpecies(newSpecies, "laudpiud", "Alele");
        assertEquals(true, success);
        success = sql.insertMunicipality(newMun, 0, "4, 5");
        assertEquals(true, success);

        success = sql.updateTree(treeId, newHeight, newDiameter, newLand, newStatus, newOwnership, newSpecies, newMun, reports);
        assertEquals(true, success);

        Tree tree = sql.getTree(treeId);
        assertEquals(tree.getTreeId(), treeId);
        assertEquals(tree.getHeight(), newHeight);
        assertEquals(tree.getDiameter(), newDiameter);
        assertEquals(tree.getAddress(), address);
        assertEquals(tree.getDatePlanted().toString(), datePlanted);
        assertEquals(tree.getLand().name(), newLand);
        assertEquals(tree.getStatus().name(), newStatus);
        assertEquals(tree.getOwnership().name(), newOwnership);
        assertEquals(tree.getSpecies().getName(), newSpecies);
        assertEquals(tree.getLocation().getLocationId(), location);
        assertEquals(tree.getMunicipality().getName(), newMun);
    }

    @Test
    public void testGetAllTrees() {
        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        success = sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
        assertEquals(true, success);

        for (Tree tree : sql.getAllTrees()) {
            assertEquals(tree.getTreeId(), treeId);
            assertEquals(tree.getHeight(), height);
            assertEquals(tree.getDiameter(), diameter);
            assertEquals(tree.getAddress(), address);
            assertEquals(tree.getDatePlanted().toString(), datePlanted);
            assertEquals(tree.getLand().name(), land);
            assertEquals(tree.getStatus().name(), status);
            assertEquals(tree.getOwnership().name(), ownership);
            assertEquals(tree.getSpecies().getName(), species);
            assertEquals(tree.getLocation().getLocationId(), location);
            assertEquals(tree.getMunicipality().getName(), municipality);
        }
    }

    @Test
    public void testGetTree() {
        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);
        success = sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
        assertEquals(true, success);

        Tree tree = sql.getTree(treeId);
        assertEquals(tree.getTreeId(), treeId);
        assertEquals(tree.getHeight(), height);
        assertEquals(tree.getDiameter(), diameter);
        assertEquals(tree.getAddress(), address);
        assertEquals(tree.getDatePlanted().toString(), datePlanted);
        assertEquals(tree.getLand().name(), land);
        assertEquals(tree.getStatus().name(), status);
        assertEquals(tree.getOwnership().name(), ownership);
        assertEquals(tree.getSpecies().getName(), species);
        assertEquals(tree.getLocation().getLocationId(), location);
        assertEquals(tree.getMunicipality().getName(), municipality);
    }

    @Test
    public void testGetMaxTreeId() {
        int treeId1 = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location1 = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location1, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        for (int i = 0; i < numTrees; i++) {
            success = sql.insertTree(treeId1 + i, height, diameter, address, datePlanted, land, status, ownership, species, location1, municipality, reports);
            assertEquals(true, success);
        }
        assertEquals(numTrees + treeId1 - 1, sql.getMaxTreeId());
    }

    @Test
    public void testDeleteTree() {
        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        success = sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
        assertEquals(true, success);

        success = sql.deleteTree(treeId);
        assertEquals(true, success);

        assertEquals(0, sql.getAllTrees().size());
    }

    //TODO
    // ======================
    // SURVEY REPORT TESTS
    // ======================

    //TODO
    // ======================
    // FORECASTING TESTS
    // ======================

    @Test
    public void testInsertForecast() {
        int forecastId = 1;
        String fcDate = "2001-12-22";
        String fcUser = defaultUser.getString("username");
        double co2Reduced = 1.0;
        double stormwater = 2.0;
        double energyConserved = 3.0;
        double biodiversity = 0.5;
        String fcTrees = "1,2,3,4";

        int treeId1 = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location1 = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location1, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        for (int i = 0; i < numTrees; i++) {
            success = sql.insertTree(treeId1 + i, height, diameter, address, datePlanted, land, status, ownership, species, location1, municipality, reports);
            assertEquals(true, success);
        }

        success = sql.insertForecast(forecastId, fcDate, fcUser, co2Reduced, biodiversity, stormwater, energyConserved, fcTrees);
        assertEquals(true, success);
    }

    @Test
    public void testGetAllForecast() {
        int forecastId = 1;
        String fcDate = "2001-12-22";
        String fcUser = defaultUser.getString("username");
        double co2Reduced = 1.0;
        double stormwater = 2.0;
        double energyConserved = 3.0;
        double biodiversity = 0.5;
        String fcTrees = "1,2,3,4";

        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location1 = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location1, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        for (int i = 0; i < numTrees; i++) {
            success = sql.insertTree(treeId + i, height, diameter, address, datePlanted, land, status, ownership, species, location1, municipality, reports);
            assertEquals(true, success);
        }

        success = sql.insertForecast(forecastId, fcDate, fcUser, co2Reduced, biodiversity, stormwater, energyConserved, fcTrees);
        assertEquals(true, success);
        success = sql.insertForecast(forecastId+1, fcDate, fcUser, co2Reduced, biodiversity, stormwater, energyConserved, fcTrees);
        assertEquals(true, success);

        int i = 0;
        for(Forecast forecast: sql.getAllForecasts()) {
            assertEquals(forecast.getForecastId(), forecastId + i);
            assertEquals(forecast.getFcDate().toString(), fcDate);
            assertEquals(forecast.getFcUser(), fcUser);
            assertEquals(forecast.getCo2Reduced(), co2Reduced, 0.01);
            assertEquals(forecast.getEnergyConserved(), energyConserved, 0.01);
            assertEquals(forecast.getStormwater(), stormwater, 0.01);
            assertEquals(forecast.getBiodiversity(), biodiversity, 0.01);
            int j = 0;
            for(Tree tree: forecast.getFcTrees()){
                assertEquals(tree.getTreeId(), treeId + j);
                assertEquals(tree.getHeight(), height);
                assertEquals(tree.getDiameter(), diameter);
                assertEquals(tree.getAddress(), address);
                assertEquals(tree.getDatePlanted().toString(), datePlanted);
                assertEquals(tree.getLand().name(), land);
                assertEquals(tree.getStatus().name(), status);
                assertEquals(tree.getOwnership().name(), ownership);
                assertEquals(tree.getSpecies().getName(), species);
                assertEquals(tree.getLocation().getLocationId(), location1);
                assertEquals(tree.getMunicipality().getName(), municipality);
                j++;
            }
            i++;
        }
    }

    @Test
    public void testGetForecast() {
        int forecastId = 1;
        String fcDate = "2001-12-22";
        String fcUser = defaultUser.getString("username");
        double co2Reduced = 1.0;
        double stormwater = 2.0;
        double energyConserved = 3.0;
        double biodiversity = 0.5;
        String fcTrees = "1,2,3,4";

        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location1 = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location1, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        for (int i = 0; i < numTrees; i++) {
            success = sql.insertTree(treeId + i, height, diameter, address, datePlanted, land, status, ownership, species, location1, municipality, reports);
            assertEquals(true, success);
        }

        success = sql.insertForecast(forecastId, fcDate, fcUser, co2Reduced, biodiversity, stormwater, energyConserved, fcTrees);
        assertEquals(true, success);
        Forecast forecast = sql.getForecast(forecastId);
        assertEquals(forecast.getForecastId(), forecastId);
        assertEquals(forecast.getFcDate().toString(), fcDate);
        assertEquals(forecast.getFcUser(), fcUser);
        assertEquals(forecast.getCo2Reduced(), co2Reduced, 0.01);
        assertEquals(forecast.getEnergyConserved(), energyConserved, 0.01);
        assertEquals(forecast.getStormwater(), stormwater, 0.01);
        assertEquals(forecast.getBiodiversity(), biodiversity, 0.01);
    }

    @Test
    public void testGetForecastMaxId() {
        int forecastId = 1;
        String fcDate = "2001-12-22";
        String fcUser = defaultUser.getString("username");
        double co2Reduced = 1.0;
        double stormwater = 2.0;
        double energyConserved = 3.0;
        double biodiversity = 0.5;
        String fcTrees = "1,2,3,4";

        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location1 = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location1, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        for (int i = 0; i < numTrees; i++) {
            success = sql.insertTree(treeId + i, height, diameter, address, datePlanted, land, status, ownership, species, location1, municipality, reports);
            assertEquals(true, success);
        }
        for(int i = 0; i < numTrees; i++) {
            success = sql.insertForecast(forecastId + i, fcDate, fcUser, co2Reduced, biodiversity, stormwater, energyConserved, fcTrees);
            assertEquals(true, success);
        }
        assertEquals(numTrees + forecastId - 1, sql.getMaxForecastId());
    }
    @Test
    public void testGetAllForecastsOfUser() {
        int forecastId = 1;
        String fcDate = "2001-12-22";
        String fcUser = defaultUser.getString("username");
        double co2Reduced = 1.0;
        double stormwater = 2.0;
        double energyConserved = 3.0;
        double biodiversity = 0.5;
        String fcTrees = "1,2,3,4";

        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location1 = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location1, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        for (int i = 0; i < numTrees; i++) {
            success = sql.insertTree(treeId + i, height, diameter, address, datePlanted, land, status, ownership, species, location1, municipality, reports);
            assertEquals(true, success);
        }

        success = sql.insertForecast(forecastId, fcDate, fcUser, co2Reduced, biodiversity, stormwater, energyConserved, fcTrees);
        assertEquals(true, success);
        success = sql.insertForecast(forecastId+1, fcDate, fcUser, co2Reduced, biodiversity, stormwater, energyConserved, fcTrees);
        assertEquals(true, success);
        success = sql.insertForecast(forecastId+2, fcDate, "Gareth", co2Reduced, biodiversity, stormwater, energyConserved, fcTrees);
        assertEquals(true, success);

        int i = 0;
        for(Forecast forecast: sql.getAllForecastsOfUser(defaultUser.getString("username"))) {
            assertEquals(forecast.getForecastId(), forecastId + i);
            assertEquals(forecast.getFcDate().toString(), fcDate);
            assertEquals(forecast.getFcUser(), fcUser);
            assertEquals(forecast.getCo2Reduced(), co2Reduced, 0.01);
            assertEquals(forecast.getEnergyConserved(), energyConserved, 0.01);
            assertEquals(forecast.getStormwater(), stormwater, 0.01);
            assertEquals(forecast.getBiodiversity(), biodiversity, 0.01);
            int j = 0;
            for(Tree tree: forecast.getFcTrees()){
                assertEquals(tree.getTreeId(), treeId + j);
                assertEquals(tree.getHeight(), height);
                assertEquals(tree.getDiameter(), diameter);
                assertEquals(tree.getAddress(), address);
                assertEquals(tree.getDatePlanted().toString(), datePlanted);
                assertEquals(tree.getLand().name(), land);
                assertEquals(tree.getStatus().name(), status);
                assertEquals(tree.getOwnership().name(), ownership);
                assertEquals(tree.getSpecies().getName(), species);
                assertEquals(tree.getLocation().getLocationId(), location1);
                assertEquals(tree.getMunicipality().getName(), municipality);
                j++;
            }
            i++;
        }
        assertEquals(i, 2);
    }

    @Test
    public void testDeleteForecast() {
        int forecastId = 1;
        String fcDate = "2001-12-22";
        String fcUser = defaultUser.getString("username");
        double co2Reduced = 1.0;
        double stormwater = 2.0;
        double energyConserved = 3.0;
        double biodiversity = 0.5;
        String fcTrees = "1,2,3,4";

        int treeId = 1;
        int height = 10;
        int diameter = 20;
        String address = defaultUser.getString("addresses");
        String datePlanted = "2001-12-22";
        String land = "Residential";
        String status = "Planted";
        String ownership = "Private";
        String species = "Maple";
        int location1 = 3;
        String municipality = "Pointe-Claire";
        String reports = "";

        boolean success = sql.insertLocation(1, 40.9, 34.5);
        assertEquals(true, success);
        success = sql.insertLocation(2, 50.8, 40.2);
        assertEquals(true, success);

        success = sql.insertLocation(location1, defaultLocation.getDouble("latitude") + 1, defaultLocation.getDouble("longitude") - 1);
        assertEquals(true, success);
        success = sql.insertSpecies(defaultSpecies.getString("name"), defaultSpecies.getString("species"), defaultSpecies.getString("genus"));
        assertEquals(true, success);
        success = sql.insertMunicipality(defaultMun.getString("name"), defaultMun.getInt("totalTrees"), defaultMun.getString("borders"));
        assertEquals(true, success);

        for (int i = 0; i < numTrees; i++) {
            success = sql.insertTree(treeId + i, height, diameter, address, datePlanted, land, status, ownership, species, location1, municipality, reports);
            assertEquals(true, success);
        }

        success = sql.insertForecast(forecastId, fcDate, fcUser, co2Reduced, biodiversity, stormwater, energyConserved, fcTrees);
        assertEquals(true, success);

        success = sql.deleteForecast(forecastId);
        assertEquals(true, success);

        assertEquals(0, sql.getAllForecasts().size());
    }
}
