package ca.mcgill.ecse321.treeple.sqlite;

import java.io.File;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.json.JSONObject;

import ca.mcgill.ecse321.treeple.model.*;

public class TestSQLiteJDBC {

    private static SQLiteJDBC sql;
    private static File dbFile;
    private static final String dbPath = "/output/treeple_test.db";
    private static JSONObject defaultUser;

    @BeforeClass
    public static void setUpBeforeClass() {
        sql = new SQLiteJDBC(dbPath);
        dbFile =  (new File(System.getProperty("user.dir") + dbPath)).getAbsoluteFile();
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
    	defaultUser.put("addresses", "2030 Mulberry Street, QC, Canada J01 10J");
    	defaultUser.put("trees", "");
    	sql.insertUser(defaultUser.getString("username"), 
    			defaultUser.getString("password"),
    			defaultUser.getString("role"), 
    			defaultUser.getString("addresses"), 
    			defaultUser.getString("trees"));
    	
    	// Default Tree Setup
    	
    }

    @After
    public void tearDown() {
    	sql.resetDB();
    }

    @Test
    public void testConnectDB() {
        assertEquals(true, sql.connect());
    }

    @Test
    public void testResetDB() {
        assertEquals(true, sql.resetDB());
    }

    @Test
    public void testDeleteDB() {
        assertEquals(true, dbFile.exists());
        assertEquals(true, sql.deleteDB());
        assertEquals(false, dbFile.exists());
        assertEquals(true, sql.connect());
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
    	
    	boolean success = sql.insertUser(defaultUser.getString("username"), 
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
    	
//    	Integer[] treeList = user.getMyTrees();
//    	System.out.println(treeList.length);
//    	i = 0;
//    	for (String s : trees.replaceAll(" ", "").split(",")) {
//    		Integer tree = Integer.parseInt(s);
//    		assertEquals(tree, treeList[i]);
//    		i++;
//    	}
    	assertEquals(user.getMyTrees().length, 0);
    }
    
    
//    @Test
//    public void testInsertTree() {
//    	int treeId = 1;
//    	int height = 10;
//    	int diameter = 20;
//    	String address = "101 Infinite Loop, Cupertino, CA";
//    	String datePlanted = "2001-12-22";
//    	String land = "Residential";
//    	String status = "Planted";
//    	String ownership = "Private";
//    	String species = "Homo sapiens";
//    	int location = 5;
//    	String municipality = "Saint-Lazare";
//    	String reports = "1, 4, 7";
//    	
//    	sql.insertTree(treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);
//    	
//    	Tree tree = sql.getTree(treeId);
//    	
//    	assertEquals(tree.getTreeId(), treeId);
//    	assertEquals(tree.getHeight(), height);
//    	assertEquals(tree.getDiameter(), diameter);
//    	assertEquals(tree.getAddress(), address);
//    	assertEquals(tree.getDatePlanted(), datePlanted);
//    	assertEquals(tree.getLand(), land);
//    	assertEquals(tree.getStatus(), status);
//    	assertEquals(tree.getOwnership(), ownership);
//    	assertEquals(tree.getSpecies(), species);
//    	assertEquals(tree.getLocation(), location);
//    	assertEquals(tree.getMunicipality(), municipality);
//    }
    
}
