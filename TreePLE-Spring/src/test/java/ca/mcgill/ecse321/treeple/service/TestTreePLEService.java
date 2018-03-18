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

    @Test
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

    // ==============================
    // GET TREE BY ID
    // ==============================
    
	@Test
    public void testGetTreeById() throws Exception {
    	JSONObject species = new JSONObject();
        species.put("name", "Maple");
        species.put("species", "Pseudoplatanus");
        species.put("genus", "Acer");
        
		service.createSpecies(species);
        
        JSONObject user = new JSONObject();
        user.put("username", "Yunus");
        user.put("password", "123yunus");
        user.put("role", "Scientist");
        user.put("myAddresses", "St-Lazare");
        
		service.createUser(user);
        
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
        
		service.createMunicipality(municipality);
		
		JSONObject plantObj = new JSONObject();
		plantObj.put("user", "Yunus");
		
    	JSONObject tree = new JSONObject();
    	tree.put("height", 12);
    	tree.put("diameter", 3);
    	tree.put("datePlanted", "2018-03-23");
    	tree.put("land", "Residential");
    	tree.put("status","Planted");
    	tree.put("ownership", "Public");
    	tree.put("species", "Maple");
    	tree.put("municipality","Saint-Laurent");
    	tree.put("longitude", 45.451208);
    	tree.put("latitude", -73.750029);
    	
    	plantObj.put("tree",tree);
		service.createTree(plantObj);
		
		Tree returnedTree = null;
		try {
			returnedTree = service.getTreeById(1);
			
		}catch(Exception e){
			fail();
		} 	
		
		assertEquals(12,returnedTree.getHeight());
		assertEquals(3,returnedTree.getDiameter());
		assertEquals("2018-03-23",returnedTree.getDatePlanted().toString());
		assertEquals(Tree.Land.Residential, returnedTree.getLand());
		assertEquals(Tree.Status.Planted, returnedTree.getStatus());
		assertEquals(Tree.Ownership.Public, returnedTree.getOwnership());
		assertEquals("Maple", returnedTree.getSpecies().getName());
		assertEquals("Saint-Laurent",returnedTree.getMunicipality().getName());
		assertEquals(45.451208,returnedTree.getLocation().getLongitude(),0);
		assertEquals(-73.750029, returnedTree.getLocation().getLatitude(),0);
		
    }
    @Test
    public void testGetTreeByIdNonexistantTree() throws Exception {
    	Tree tree = service.getTreeById(1000);
    	
    	assertEquals(null,tree);
    	
    }
    
    @Test
    public void testGetTreeByIdNegativeId() throws Exception {
    	String error = "";
    	try {
    		service.getTreeById(-1);
    	}catch(InvalidInputException e) {
    		error = e.getMessage();
    	} 
    	assertEquals("Tree's ID cannot be negative!",error);
    }

    // ==============================
    // GET USER BY USERNAME TEST
    // ==============================
    
    @Test
    public void testGetUserByUsername() throws Exception{
    	
    	JSONObject user = new JSONObject();
        user.put("username", "Gareth");
        user.put("password", "password123");
        user.put("role", "Resident");
        user.put("myAddresses", "2787 Rue Post");
        
		service.createUser(user);
		
		User returnedUser = service.getUserByUsername("Gareth");
		
		assertEquals("Gareth",returnedUser.getUsername());
		assertEquals("password123",returnedUser.getPassword());
		assertEquals(User.UserRole.Resident,returnedUser.getRole());
		assertEquals("2787RuePost", returnedUser.getMyAddress(0));
    }
    
    @Test
    public void testGetUserByUsernameSpaces() throws Exception{
    	String username = "                   ";
    	String error = "";
    	
    	try {
    		service.getUserByUsername(username);
    	}catch(InvalidInputException e) {
    		error = e.getMessage();
    	}
    	
    	assertEquals("Username cannot be empty!",error);
    }
    
    @Test
    public void testGetUserByUsernameNull() throws Exception{
    	String username = null;
    	String error = "";
    	
    	try {
    		service.getUserByUsername(username);
    	}catch(InvalidInputException e){
    		error = e.getMessage();
    	}
    	
    	assertEquals("Username cannot be empty!", error);
    	
    }
    
    @Test
    public void testGetUserByUsernameEmpty() throws Exception{
    	String username = "";
    	String error = "";
    	
    	try {
    		service.getUserByUsername(username);
    	}catch(InvalidInputException e) {
    		error = e.getMessage();
    	}
    	
    	assertEquals("Username cannot be empty!", error);
    }
    
    @Test
    public void testGetUserByUsernameNonExistant() throws Exception{
    	JSONObject user = new JSONObject();
        user.put("username", "Gareth");
        user.put("password", "password123");
        user.put("role", "Resident");
        user.put("myAddresses", "2787 Rue Post");
        
		service.createUser(user);
		
		User returnedUser = service.getUserByUsername("Filip");
		
		assertEquals(null,returnedUser);
    }
    

    // ==============================
    // GET MUNICIPALITY BY NAME TEST
    // ==============================
    
    @Test
    public void testGetMunicipalityByName() throws Exception{
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
         
         service.createMunicipality(municipality);
         
         Municipality returnedMunicipality = null;
         
         try {
        	 returnedMunicipality = service.getMunicipalityByName("Saint-Laurent");
         }catch(Exception e) {
        	 fail();
         }
         
         assertEquals("Saint-Laurent", returnedMunicipality.getName());
         assertEquals(12,returnedMunicipality.getTotalTrees());
         assertEquals(45.497470, returnedMunicipality.getBorder(0).getLatitude(),0);
         assertEquals(-73.772830, returnedMunicipality.getBorder(0).getLongitude(),0);
         assertEquals(45.481864, returnedMunicipality.getBorder(1).getLatitude(),0);
         assertEquals(-73.773715, returnedMunicipality.getBorder(1).getLongitude(),0);
         assertEquals(45.460268, returnedMunicipality.getBorder(2).getLatitude(),0);
         assertEquals(-73.750029, returnedMunicipality.getBorder(2).getLongitude(),0);
         assertEquals(45.481208, returnedMunicipality.getBorder(3).getLatitude(),0);
         assertEquals(-73.723422, returnedMunicipality.getBorder(3).getLongitude(),0);
         assertEquals(45.459034, returnedMunicipality.getBorder(4).getLatitude(),0);
         assertEquals(-73.683652, returnedMunicipality.getBorder(4).getLongitude(),0);
         assertEquals(45.526536, returnedMunicipality.getBorder(5).getLatitude(),0);
         assertEquals(-73.651208, returnedMunicipality.getBorder(5).getLongitude(),0);
         assertEquals(45.522407, returnedMunicipality.getBorder(6).getLatitude(),0);
         assertEquals(-73.730198, returnedMunicipality.getBorder(6).getLongitude(),0);
    }
    
    @Test
    public void testGetMunicipalityByNameEmpty() throws Exception{
    	String name = "";
    	String error ="";
    	
    	try {
    		service.getMunicipalityByName(name);
    	}catch(InvalidInputException e) {
    		error = e.getMessage();
    	}
    	
    	assertEquals("Name cannot be empty!", error);
    }
    
    @Test
    public void testGetMunicipalityByNameSpaces() throws Exception{
    	String name ="          ";
    	String error = "";
    	
    	try {
    		service.getMunicipalityByName(name);
    	}catch(InvalidInputException e) {
    		error = e.getMessage();
    	}
    	
    	assertEquals("Name cannot be empty!",error);
    }
    
    @Test
    public void testGetMunicipalityByNameNull() throws Exception{
    	String name = null;
    	String error = "";
    	
    	try {
    		service.getMunicipalityByName(name);
    	}catch(InvalidInputException e) {
    		error = e.getMessage();
    	}
    	
    	assertEquals("Name cannot be empty!",error);
    }
    
    @Test
    public void testGetMunicipalityByNameNonExistant() throws Exception{
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
         
         service.createMunicipality(municipality);
    	Municipality returnedMunicipality = service.getMunicipalityByName("Laval");
    	
    	assertEquals(null, returnedMunicipality);
    }
    
    // ==============================
    // DELETE USER TEST
    // ==============================
    
    @Test
    public void testDeleteUser() throws Exception{
    	JSONObject user = new JSONObject();
        user.put("username", "Gareth");
        user.put("password", "password123");
        user.put("role", "Resident");
        user.put("myAddresses", "2787 Rue Post");
        
		service.createUser(user);
		User returnedUser = null;
		
		try {
			returnedUser = service.deleteUser(user);
		}catch(Exception e) {
			fail();
		}
		
		assertEquals("Gareth",returnedUser.getUsername());
		assertEquals("password123",returnedUser.getPassword());
		assertEquals(User.UserRole.Resident,returnedUser.getRole());
		assertEquals("2787RuePost", returnedUser.getMyAddress(0));
		
		User userInDB = service.getUserByUsername("Gareth");
		assertEquals(null,userInDB);
    }
    
    @Test
    public void testDeleteUserEmptyName() throws Exception{
    	String error = "";
    	JSONObject user = new JSONObject();
        user.put("username", "");
        user.put("password", "password123");
        user.put("role", "Resident");
        user.put("myAddresses", "2787 Rue Post");
        
        try {
        	service.deleteUser(user);
        }catch(InvalidInputException e) {
        	error = e.getMessage();
        }
        
        assertEquals ("User is not logged in/Username is missing!",error);
    }
    
    @Test
    public void testDeleteUserSpacesName() throws Exception{
    	String error = "";
    	JSONObject user = new JSONObject();
        user.put("username", "          ");
        user.put("password", "password123");
        user.put("role", "Resident");
        user.put("myAddresses", "2787 Rue Post");
        
        try {
        	service.deleteUser(user);
        }catch(InvalidInputException e) {
        	error = e.getMessage();
        }
        
        assertEquals ("User is not logged in/Username is missing!",error);
    }
    
    @Test(expected = JSONException.class)
    public void testDeleteUserNullName() throws Exception{
    	String error = "";
    	JSONObject user = new JSONObject();
        user.put("username", (String) null);
        user.put("password", "password123");
        user.put("role", "Resident");
        user.put("myAddresses", "2787 Rue Post");
        
        service.deleteUser(user);
       
    }
    
    public void testDeleteUserUserDNEInDB() throws Exception{
    	String error = "";
    	JSONObject user = new JSONObject();
        user.put("username", "Gareth");
        user.put("password", "password123");
        user.put("role", "Resident");
        user.put("myAddresses", "2787 Rue Post");
        
        try {
        	service.deleteUser(user);
        }catch(InvalidInputException e) {
        	error = e.getMessage();
        }
        
        assertEquals("That username doesn't exist!", error);
    }
}
