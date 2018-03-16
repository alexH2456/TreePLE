package ca.mcgill.ecse321.treeple.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.ecse321.treeple.model.User;
import ca.mcgill.ecse321.treeple.service.InvalidInputException;
import ca.mcgill.ecse321.treeple.service.TreePLEService;

public class TestTreePLEService {

	private RegistrationManager rm;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PersistenceXStream.initializeModelManager("output/data.xml");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		rm = new RegistrationManager();
	}

	@After
	public void tearDown() throws Exception {
		rm.delete();
	}

	@Test
	public void testCreateUser() {
		assertEquals(0, rm.getUsers().size());

		String username = "Oscar";
		String password = "bigboi";
		int age = 19;
		int points = 0;

		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.createUser(username, password, age, points);
		} catch (InvalidInputException e) {
			// Check that no error occurred
			fail();
		}

		checkResultUser(username, rm);

		rm = (RegistrationManager) PersistenceXStream.loadFromXMLwithXStream();

		// Check file contents
		checkResultUser(username, rm);
	}

	@Test
	public void testCreateUserNull() {
		assertEquals(0, rm.getUsers().size());

		String username = null;
		String password = null;
		int age = 20;
		int points = 100;
		String error = null;

		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.createUser(username, password, age, points);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("Username cannot be empty!", error);
		// Check no change in memory
		assertEquals(0, rm.getUsers().size());
	}

	@Test
	public void testCreateUserEmpty() {
		assertEquals(0, rm.getUsers().size());

		String username = "";
		String password = "";
		int age = 10;
		int points = 100;
		String error = null;

		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.createUser(username, password, age, points);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("Username cannot be empty!", error);
		// Check no change in memory
		assertEquals(0, rm.getUsers().size());
	}

	@Test
	public void testCreateUserSpaces() {
		assertEquals(0, rm.getUsers().size());

		String username = " ";
		String password = " ";
		int age = 25;
		int points = 100;
		String error = null;

		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.createUser(username, password, age, points);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("Username cannot be empty!", error);
		// Check no change in memory
		assertEquals(0, rm.getUsers().size());
	}

	@Test
	public void testFindAllUsers() {
		assertEquals(0, rm.getUsers().size());

		String[] usernames = {"John Doe", "Foo Bar"};
		String password = "fgtboi";
		int age = 20;
		int points = 100;

		TreePLEService erc = new TreePLEService(rm);
		for (String username : usernames) {
			try {
				erc.createUser(username, password, age, points);
			} catch (InvalidInputException e) {
				// Check that no error occurred
				fail();
			}
		}

		List<User> registeredUsers = erc.findAllUsers();

		// Check number of registered participants
		assertEquals(2, registeredUsers.size());
		// Check each participant
		for (int i = 0; i < usernames.length; i++)
			assertEquals(usernames[i], registeredUsers.get(i).getUsername());
	}

	@Test
	public void testCreateEvent() {
		RegistrationManager rm = new RegistrationManager();
		assertEquals(0, rm.getEvents().size());

		String name = "Soccer Game";
		Calendar c = Calendar.getInstance();
		c.set(2017, Calendar.MARCH, 16, 9, 0, 0);
		Date eventDate = new Date(c.getTimeInMillis());
		Time startTime = new Time(c.getTimeInMillis());
		c.set(2017, Calendar.MARCH, 16, 10, 30, 0);
		Time endTime = new Time(c.getTimeInMillis());

		// Test model in memory
		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.createEvent(name, eventDate, startTime, endTime);
		} catch (InvalidInputException e) {
			fail();
		}
		checkResultEvent(name, eventDate, startTime, endTime, rm);

		// Test file
		RegistrationManager rm2 = (RegistrationManager) PersistenceXStream.loadFromXMLwithXStream();
		checkResultEvent(name, eventDate, startTime, endTime, rm2);
		rm2.delete();
	}

	@Test
	public void testRegister() {
		RegistrationManager rm = new RegistrationManager();
		assertEquals(0, rm.getRegistrations().size());

		String nameU = "Oscar";
		String password = "Wild";
		int age = 40;
		int points = 1000;
		User user = new User(nameU, password, age, points);
		rm.addUser(user);
		assertEquals(1, rm.getUsers().size());

		String nameE = "Soccer Game";
		Calendar c = Calendar.getInstance();
		c.set(2017, Calendar.MARCH, 16, 9, 0, 0);
		Date eventDate = new Date(c.getTimeInMillis());
		Time startTime = new Time(c.getTimeInMillis());
		c.set(2017, Calendar.MARCH, 16, 10, 30, 0);
		Time endTime = new Time(c.getTimeInMillis());
		Event event = new Event(nameE, eventDate, startTime, endTime);
		rm.addEvent(event);
		assertEquals(1, rm.getEvents().size());

		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.register(user, event);
		} catch (InvalidInputException e) {
			fail();
		}
		checkResultRegister(nameU, nameE, eventDate, startTime, endTime, rm);

		RegistrationManager rm2 = (RegistrationManager) PersistenceXStream.loadFromXMLwithXStream();

		// Check file contents
		checkResultRegister(nameU, nameE, eventDate, startTime, endTime, rm2);
		rm2.delete();
	}

	@Test
	public void testCreateEventNull() {
		assertEquals(0, rm.getRegistrations().size());

		String name = null;
		Date eventDate = null;
		Time startTime = null;
		Time endTime = null;

		String error = null;
		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.createEvent(name, eventDate, startTime, endTime);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("Event name cannot be empty! Event date cannot be empty! Event start time cannot be empty! Event end time cannot be empty!", error);
		// Check model in memory
		assertEquals(0, rm.getEvents().size());
	}

	@Test
	public void testCreateEventEmpty() {
		assertEquals(0, rm.getEvents().size());

		String name = "";
		Calendar c = Calendar.getInstance();
		c.set(2017, Calendar.FEBRUARY, 16, 10, 00, 0);
		Date eventDate = new Date(c.getTimeInMillis());
		Time startTime = new Time(c.getTimeInMillis());
		c.set(2017, Calendar.FEBRUARY, 16, 11, 30, 0);
		Time endTime = new Time(c.getTimeInMillis());

		String error = null;
		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.createEvent(name, eventDate, startTime, endTime);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("Event name cannot be empty!", error);
		// Check model in memory
		assertEquals(0, rm.getEvents().size());
	}

	@Test
	public void testCreateEventSpaces() {
		assertEquals(0, rm.getEvents().size());

		String name = " ";
		Calendar c = Calendar.getInstance();
		c.set(2016, Calendar.OCTOBER, 16, 9, 00, 0);
		Date eventDate = new Date(c.getTimeInMillis());
		Time startTime = new Time(c.getTimeInMillis());
		c.set(2016, Calendar.OCTOBER, 16, 10, 30, 0);
		Time endTime = new Time(c.getTimeInMillis());

		String error = null;
		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.createEvent(name, eventDate, startTime, endTime);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("Event name cannot be empty!", error);
		// Check model in memory
		assertEquals(0, rm.getEvents().size());
	}

	@Test
	public void testCreateEventEndTimeBeforeStartTime() {
		assertEquals(0, rm.getEvents().size());

		String name = "Soccer Game";
		Calendar c = Calendar.getInstance();
		c.set(2016, Calendar.OCTOBER, 16, 9, 00, 0);
		Date eventDate = new Date(c.getTimeInMillis());
		Time startTime = new Time(c.getTimeInMillis());
		c.set(2016, Calendar.OCTOBER, 16, 8, 59, 59);
		Time endTime = new Time(c.getTimeInMillis());

		String error = null;
		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.createEvent(name, eventDate, startTime, endTime);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("Event end time cannot be before event start time!", error);
		// Check model in memory
		assertEquals(0, rm.getEvents().size());
	}

	@Test
	public void testRegisterNull() {
		assertEquals(0, rm.getRegistrations().size());

		User user = null;
		assertEquals(0, rm.getUsers().size());

		Event event = null;
		assertEquals(0, rm.getEvents().size());

		String error = null;
		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.register(user, event);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("User needs to be selected for registration! Event needs to be selected for registration!", error);
		// Check model in memory
		assertEquals(0, rm.getRegistrations().size());
		assertEquals(0, rm.getUsers().size());
		assertEquals(0, rm.getEvents().size());
	}

	@Test
	public void testRegisterUserAndEventDoNotExist() {
		assertEquals(0, rm.getRegistrations().size());

		String nameU = "Oscar";
		String password = "Wild";
		int age = 50;
		int points = 100;
		User user = new User(nameU, password, age, points);
		assertEquals(0, rm.getUsers().size());

		String nameE = "Soccer Game";
		Calendar c = Calendar.getInstance();
		c.set(2016, Calendar.OCTOBER, 16, 9, 00, 0);
		Date eventDate = new Date(c.getTimeInMillis());
		Time startTime = new Time(c.getTimeInMillis());
		c.set(2016, Calendar.OCTOBER, 16, 10, 30, 0);
		Time endTime = new Time(c.getTimeInMillis());
		Event event = new Event(nameE, eventDate, startTime, endTime);
		assertEquals(0, rm.getEvents().size());

		String error = null;
		TreePLEService erc = new TreePLEService(rm);
		try {
			erc.register(user, event);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}

		// Check error
		assertEquals("User does not exist! Event does not exist!", error);

		// Check model in memory
		assertEquals(0, rm.getRegistrations().size());
		assertEquals(0, rm.getUsers().size());
		assertEquals(0, rm.getEvents().size());
	}



	private void checkResultUser(String name, RegistrationManager rm2) {
		assertEquals(1, rm2.getUsers().size());
		assertEquals(name, rm2.getUser(0).getUsername());
		assertEquals(0, rm2.getEvents().size());
		assertEquals(0, rm2.getRegistrations().size());
	}

	private void checkResultEvent(String name, Date eventDate, Time startTime, Time endTime, RegistrationManager rm2) {
		assertEquals(0, rm2.getUsers().size());
		assertEquals(1, rm2.getEvents().size());
		assertEquals(name, rm2.getEvent(0).getName());
		assertEquals(eventDate.toString(), rm2.getEvent(0).getEventDate().toString());
		assertEquals(startTime.toString(), rm2.getEvent(0).getStartTime().toString());
		assertEquals(endTime.toString(), rm2.getEvent(0).getEndTime().toString());
		assertEquals(0, rm2.getRegistrations().size());
	}

	private void checkResultRegister(String nameP, String nameE, Date eventDate, Time startTime, Time endTime, RegistrationManager rm2) {
		assertEquals(1, rm2.getUsers().size());
		assertEquals(nameP, rm2.getUser(0).getUsername());
		assertEquals(1, rm2.getEvents().size());
		assertEquals(nameE, rm2.getEvent(0).getName());
		assertEquals(eventDate.toString(), rm2.getEvent(0).getEventDate().toString());
		assertEquals(startTime.toString(), rm2.getEvent(0).getStartTime().toString());
		assertEquals(endTime.toString(), rm2.getEvent(0).getEndTime().toString());
		assertEquals(1, rm2.getRegistrations().size());
		assertEquals(rm2.getEvent(0), rm2.getRegistration(0).getEvent());
		assertEquals(rm2.getUser(0), rm2.getRegistration(0).getUser());
	}
}
