package ca.mcgill.ecse321.treeple.sqlite;

import java.io.File;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.json.JSONObject;

import ca.mcgill.ecse321.treeple.model.*;

public class TestSQLiteJDBC {

    private static SQLiteJDBC sql;
    private static File dbFile;
    private static final String dbPath = "/output/treeple_test.db";

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
}
