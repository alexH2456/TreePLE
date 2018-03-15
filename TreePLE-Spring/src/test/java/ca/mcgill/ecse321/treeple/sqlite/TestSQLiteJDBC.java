package ca.mcgill.ecse321.treeple.sqlite;

import java.io.File;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.json.JSONObject;

import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.sqlite.*;

public class TestSQLiteJDBC {

    private SQLiteJDBC sql;
    private File dbFile;
    private final String dbPath = "/output/treeple_test.db";

    @Before
    public void setUp() throws Exception {
        sql = new SQLiteJDBC(dbPath);
        dbFile =  new File(dbPath);
    }

    @After
    public void tearDown() throws Exception {
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
    }

    @Test
    public void test() {
        u1JSON = new JSONObject("{}");
        u2JSON = new JSONObject("{\"AXIOS1EV2\": 3, \"ELEK56VUA\": 9, \"IDEK1053R\": 10}");
        lJSON = new JSONObject("{"
            + "\"Martin\": {\"checkIn\": \"17:30:00\", \"checkOut\": \"17:45:00\"},"
            + "\"Jennifer\": {\"checkIn\": \"09:11:11\", \"checkOut\": \"09:12:34\"},"
            + "\"Abdullah\": {\"checkIn\": \"09:59:00\", \"checkOut\": \"19:00:00\"}"
            + "}");

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
