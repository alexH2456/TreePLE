package ca.mcgill.ecse321.treeple.persistence;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.json.JSONObject;

import ca.mcgill.ecse321.treeple.model.User;
import ca.mcgill.ecse321.treeple.model.Location;
import ca.mcgill.ecse321.treeple.model.RegistrationManager;
import ca.mcgill.ecse321.treeple.persistence.PersistenceXStream;

public class TestPersistence {

	private RegistrationManager rm;
	private PersistenceXStream persX;
	private String testDBPath = "/db/testDB.db";

	@Before
	public void setUp() throws Exception {
		rm = new RegistrationManager();
		persX = new PersistenceXStream(testDBPath);

		JSONObject u1JSON = null;
		JSONObject u2JSON = null;
		JSONObject lJSON = null;

		try {
			u1JSON = new JSONObject("{}");
			u2JSON = new JSONObject("{\"AXIOS1EV2\": 3, \"ELEK56VUA\": 9, \"IDEK1053R\": 10}");
			lJSON = new JSONObject("{"
				+ "\"Martin\": {\"checkIn\": \"17:30:00\", \"checkOut\": \"17:45:00\"},"
				+ "\"Jennifer\": {\"checkIn\": \"09:11:11\", \"checkOut\": \"09:12:34\"},"
				+ "\"Abdullah\": {\"checkIn\": \"09:59:00\", \"checkOut\": \"19:00:00\"}"
				+ "}");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

	    // Create participants
	    User user1 = new User("Martin", "shitpass", 25, 69, u1JSON);
	    User user2 = new User("Jennifer", "swag123", 45, 1000, u2JSON);

		// Create event
	    Location location = new Location("AXIOS1EV2", "Vua", "2020", "Boulevard Robert-Bourassa", 600, lJSON);

	    // Manage registrations
	    rm.addUser(user1);
	    rm.addUser(user2);
	    rm.addLocation(location);
	}

	@After
	public void tearDown() throws Exception {
		rm.delete();
		persX.sql.deleteDB();
	}

	@Test
	public void test() {
	    // Initialize model
		rm = persX.initializeModelManager();
	    if (rm == null)
			fail("Could not load file.");

		JSONObject u1JSON = null;
		JSONObject u2JSON = null;
		JSONObject lJSON = null;
		try {
			u1JSON = new JSONObject("{}");
			u2JSON = new JSONObject("{\"AXIOS1EV2\": 3, \"ELEK56VUA\": 9, \"IDEK1053R\": 10}");
			lJSON = new JSONObject("{"
				+ "\"Martin\": {\"checkIn\": \"17:30:00\", \"checkOut\": \"17:45:00\"},"
				+ "\"Jennifer\": {\"checkIn\": \"09:11:11\", \"checkOut\": \"09:12:34\"},"
				+ "\"Abdullah\": {\"checkIn\": \"09:59:00\", \"checkOut\": \"19:00:00\"}"
				+ "}");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}

	    // Check participants
	    assertEquals(2, rm.getUsers().size());
		assertEquals("Martin", rm.getUser(0).getUsername());
		assertEquals("shitpass", rm.getUser(0).getPassword());
		assertEquals(25, rm.getUser(0).getAge());
		assertEquals(69, rm.getUser(0).getPoints());
		assertEquals(u1JSON.toString(), rm.getUser(0).getFavs().toString());

	    assertEquals("Jennifer", rm.getUser(1).getUsername());
		assertEquals("swag123", rm.getUser(1).getPassword());
		assertEquals(45, rm.getUser(1).getAge());
		assertEquals(1000, rm.getUser(1).getPoints());
		assertEquals(u2JSON.toString(), rm.getUser(1).getFavs().toString());

	    // Check location
		assertEquals(1, rm.getLocations().size());
		assertEquals("AXIOS1EV2", rm.getLocation(0).getId());
	    assertEquals("Vua", rm.getLocation(0).getName());
	    assertEquals("2020", rm.getLocation(0).getStrtNum().toString());
	    assertEquals("Boulevard Robert-Bourassa", rm.getLocation(0).getAddress());
		assertEquals(600, rm.getLocation(0).getQTime());
		assertEquals(lJSON.toString(), rm.getLocation(0).getCheckTimes().toString());

		// Clear the model in memory
		rm.delete();
		assertEquals(0, rm.getUsers().size());
		assertEquals(0, rm.getLocations().size());
	}
}
