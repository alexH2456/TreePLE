package ca.mcgill.ecse321.treeple.sqlite;

import java.sql.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

import ca.mcgill.ecse321.treeple.TreePLESpringApplication;
import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.model.Tree.*;
import ca.mcgill.ecse321.treeple.model.User.*;

public class SQLiteJDBC {

    private static Connection c;
    private static String dbPath;

    public SQLiteJDBC() {
        if (TreePLESpringApplication.env.acceptsProfiles("prod")) {
            dbPath = System.getProperty("catalina.base") + "/webapps/treeple.db";
        } else if (TreePLESpringApplication.env.acceptsProfiles("dev")) {
            dbPath = System.getProperty("catalina.base") + "/webapps/treeple.db";
        } else {
            dbPath = System.getProperty("user.dir") + "/src/main/resources/treeple.db";
        }
    }

    public SQLiteJDBC(String filename) {
        dbPath = System.getProperty("user.dir") + filename;
    }

    public String getDbPath() {
        return dbPath;
    }


    // ==============================
    // CONNECTION API
    // ==============================

    // Connect to a database
    public boolean connect() {
        try {
            Class.forName("org.sqlite.JDBC");

            // Create a connection to the database
            String url = String.format("jdbc:sqlite:%s", dbPath);
            c = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

            // Trees DB Table
            String sqlTrees = "CREATE TABLE IF NOT EXISTS TREES "
                            + "(treeId INT PRIMARY KEY    NOT NULL,"
                            + " height       INT          NOT NULL,"
                            + " diameter     INT          NOT NULL,"
                            + " address      VARCHAR(200) NOT NULL,"
                            + " datePlanted  VARCHAR(50)  NOT NULL,"
                            + " land         VARCHAR(50)  NOT NULL,"
                            + " status       VARCHAR(50)  NOT NULL,"
                            + " ownership    VARCHAR(50)  NOT NULL,"
                            + " species      INT          NOT NULL,"
                            + " location     INT          NOT NULL,"
                            + " municipality VARCHAR(50)  NOT NULL,"
                            + " reports      TEXT         NOT NULL)";

            // Users DB Table
            String sqlUsers = "CREATE TABLE IF NOT EXISTS USERS "
                            + "(username VARCHAR(50) PRIMARY KEY NOT NULL,"
                            + " password    VARCHAR(100) NOT NULL,"
                            + " role        VARCHAR(50)  NOT NULL,"
                            + " myAddresses TEXT,"
                            + " myTrees     TEXT)";

            // Species DB Table
            String sqlSpecies = "CREATE TABLE IF NOT EXISTS SPECIES "
                              + "(name VARCHAR(50) PRIMARY KEY NOT NULL,"
                              + " species VARCHAR(50),"
                              + " genus   VARCHAR(50))";

            // Locations DB Table
            String sqlLocations = "CREATE TABLE IF NOT EXISTS LOCATIONS "
                                + "(locationId INT PRIMARY KEY NOT NULL,"
                                + " latitude  DOUBLE NOT NULL,"
                                + " longitude DOUBLE NOT NULL)";

            // Municipalities DB Table
            String sqlMunicipalities = "CREATE TABLE IF NOT EXISTS MUNICIPALITIES "
                                     + "(name VARCHAR(50) PRIMARY KEY NOT NULL,"
                                     + " totalTrees INT  NOT NULL,"
                                     + " borders    TEXT)";

            // SurveyReports DB Table
            String sqlSurveyReports = "CREATE TABLE IF NOT EXISTS SURVEYREPORTS "
                                    + "(reportId INT PRIMARY KEY  NOT NULL,"
                                    + " reportDate    VARCHAR(50) NOT NULL,"
                                    + " reportUser    VARCHAR(50) NOT NULL)";

            // Forecasts DB Table
            String sqlForecasts = "CREATE TABLE IF NOT EXISTS FORECASTS "
                                + "(forecastId INT PRIMARY KEY  NOT NULL,"
                                + " fcDate          VARCHAR(50) NOT NULL,"
                                + " fcUser          VARCHAR(50) NOT NULL,"
                                + " co2Reduced      DOUBLE      NOT NULL,"
                                + " biodiversity    DOUBLE      NOT NULL,"
                                + " stormwater      DOUBLE      NOT NULL,"
                                + " energyConserved DOUBLE      NOT NULL,"
                                + " fcTrees         TEXT        NOT NULL)";

            Statement stmt = c.createStatement();
            stmt.executeUpdate(sqlTrees);
            stmt.executeUpdate(sqlUsers);
            stmt.executeUpdate(sqlSpecies);
            stmt.executeUpdate(sqlLocations);
            stmt.executeUpdate(sqlMunicipalities);
            stmt.executeUpdate(sqlSurveyReports);
            stmt.executeUpdate(sqlForecasts);
            stmt.close();
            return true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Close connection to database
    public boolean closeConnection() {
        try {
            if (c != null) {
                c.close();
                System.out.println("Connection to SQLite has been closed.");
                return true;
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    public boolean resetDB() {
        if (deleteDB()) {
            return connect();
        }
        return false;
    }

    public boolean deleteDB() {
        try {
            if (closeConnection()) {
                return Files.deleteIfExists(new File(dbPath).toPath());
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }



    // ==============================
    // TREES TABLE API
    // ==============================

    // Add a new Tree
    public boolean insertTree(int treeId, int height, int diameter, String address,
                              String datePlanted, String land, String status, String ownership,
                              String species, int location, String municipality, String reports) {
        String insertTree = String.format(
            "INSERT INTO TREES (treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports) " +
            "VALUES (%d, %d, %d, '%s', '%s', '%s', '%s', '%s', '%s', %d, '%s', '%s');",
            treeId, height, diameter, address, datePlanted, land, status, ownership, species.replaceAll("'", "''"), location, municipality.replaceAll("'", "''"), reports);

        try {
            return c.createStatement().executeUpdate(insertTree) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Update a Tree
    public boolean updateTree(int treeId, int height, int diameter, String land, String status,
                              String ownership, String species, String municipality, String reports) {
        String updateTree = String.format(
            "UPDATE TREES " +
            "SET height = %d, diameter = %d, land = '%s', status = '%s', ownership = '%s', species = '%s', municipality = '%s', reports = '%s' " +
            "WHERE treeId = %d;",
            height, diameter, land, status, ownership, species.replaceAll("'", "''"), municipality.replaceAll("'", "''"), reports, treeId);

        try {
            return c.createStatement().executeUpdate(updateTree) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Get all Trees
    public ArrayList<Tree> getAllTrees() {
        ArrayList<Tree> treeList = new ArrayList<Tree>();

        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM TREES;");

            while (rs.next()) {
                treeList.add(createTree(rs));
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return treeList;
    }

    // Get a Tree
    public Tree getTree(int treeId) {
        Tree tree = null;
        String getTree = String.format("SELECT * FROM TREES WHERE treeId = %d;", treeId);

        try {
            ResultSet rs = c.createStatement().executeQuery(getTree);

            if (rs.next()) {
                tree = createTree(rs);
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return tree;
    }

    // Get all trees of type Species
    public ArrayList<Tree> getAllTreesOfSpecies(String species) {
        ArrayList<Tree> trees = new ArrayList<Tree>();
        String getTreesOfSpecies = String.format("SELECT * FROM TREES WHERE species = '%s';", species.replaceAll("'", "''"));

        try {
            ResultSet rs = c.createStatement().executeQuery(getTreesOfSpecies);

            while (rs.next()) {
                trees.add(createTree(rs));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return trees;
    }

    // Get all Trees from a Municipality
    public ArrayList<Tree> getAllTreesOfMunicipality(String municipality) {
        ArrayList<Tree> trees = new ArrayList<>();
        String getTreesFromMunicipality = String.format("SELECT * FROM TREES WHERE municipality = '%s';", municipality.replaceAll("'", "''"));

        try {
            ResultSet rs = c.createStatement().executeQuery(getTreesFromMunicipality);

            while (rs.next()) {
                trees.add(createTree(rs));
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return trees;
    }

    // Get Tree count of a Municipality
    public int getTreeCountOfMunicipality(String municipality) {
        int treeCount = -1;
        String getTreeCountOfMunicipality = String.format("SELECT COUNT(treeId) AS treeCount FROM TREES WHERE municipality = '%s';", municipality.replaceAll("'", "''"));

        try {
            ResultSet rs = c.createStatement().executeQuery(getTreeCountOfMunicipality);

            if (rs.next()) {
                treeCount = rs.getInt("treeCount");
                updateMunicipalityTotalTrees(municipality, treeCount);
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return treeCount;
    }

    // Get the highest treeId
    public int getMaxTreeId() {
        int getMaxTreeId = -1;

        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT MAX(treeId) AS maxTreeId FROM TREES;");

            if (rs.next()) {
                getMaxTreeId = rs.getInt("maxTreeId");
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return getMaxTreeId;
    }

    // Delete a Tree
    public boolean deleteTree(int treeId) {
        String deleteTree = String.format("DELETE FROM TREES WHERE treeId = %d;", treeId);

        try {
            return c.createStatement().executeUpdate(deleteTree) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Create a Tree object
    private Tree createTree(ResultSet rs) throws Exception {
        int treeId = rs.getInt("treeId");
        int height = rs.getInt("height");
        int diameter = rs.getInt("diameter");
        String address = rs.getString("address");
        Date datePlanted = Date.valueOf(rs.getString("datePlanted"));
        Land land = Land.valueOf(rs.getString("land"));
        Status status = Status.valueOf(rs.getString("status"));
        Ownership ownership = Ownership.valueOf(rs.getString("ownership"));
        Species species = getSpecies(rs.getString("species"));
        Location location = getLocation(rs.getInt("location"));
        Municipality municipality = getMunicipality(rs.getString("municipality"));
        ArrayList<SurveyReport> reports = new ArrayList<SurveyReport>();

        for (String reportId : rs.getString("reports").replaceAll("\\s", "").split(",")) {
            SurveyReport report;
            if (reportId.matches("^\\d+$") && (report = getSurveyReport(Integer.parseInt(reportId))) != null) {
                reports.add(report);
            }
        }

        return new Tree(height, diameter, address, datePlanted, land, status,
                        ownership, species, location, municipality, treeId, reports);
    }



    // ==============================
    // USERS TABLE API
    // ==============================

    // Add a new User
    public boolean insertUser(String username, String password, String role, String myAddresses, String myTrees) {
        String insertUser = String.format(
            "INSERT INTO USERS (username, password, role, myAddresses, myTrees) " +
            "VALUES ('%s', '%s', '%s', '%s', '%s');",
            username, password, role, myAddresses, myTrees);

        try {
            return c.createStatement().executeUpdate(insertUser) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Update a User
    public boolean updateUser(String username, String password, String role, String myAddresses) {
        String updateUser = String.format(
            "UPDATE USERS " +
            "SET password = '%s', role = '%s', myAddresses = '%s' " +
            "WHERE username = '%s';",
            password, role, myAddresses, username);

        try {
            return c.createStatement().executeUpdate(updateUser) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Update a User's password
    public boolean updateUserPassword(String username, String password) {
        String updateUser = String.format(
            "UPDATE USERS " +
            "SET password = '%s' " +
            "WHERE username = '%s';",
            password, username);

        try {
            return c.createStatement().executeUpdate(updateUser) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Update a User's trees
    public boolean updateUserTrees(String username, String myTrees) {
        String updateUserTrees = String.format(
            "UPDATE USERS " +
            "SET myTrees = '%s' " +
            "WHERE username = '%s';",
            myTrees, username);

        try {
            return c.createStatement().executeUpdate(updateUserTrees) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Get all Users
    public ArrayList<User> getAllUsers() {
        ArrayList<User> userList = new ArrayList<User>();

        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM USERS;");

            while (rs.next()) {
                String username = rs.getString("username");

                if (User.hasWithUsername(username)) {
                    userList.add(User.getWithUsername(username));
                } else {
                    User user = new User(username, rs.getString("password"), UserRole.valueOf(rs.getString("role")));

                    for (String addressId : rs.getString("myAddresses").replaceAll("\\s", "").split(",")) {
                        if (addressId != null && !addressId.isEmpty()) {
                            user.addMyAddress(addressId);
                        }
                    }

                    for (String treeId : rs.getString("myTrees").replaceAll("\\s", "").split(",")) {
                        if (treeId.matches("^\\d+$") && getTree(Integer.parseInt(treeId)) != null) {
                            user.addMyTree(Integer.parseInt(treeId));
                        }
                    }

                    userList.add(user);
                }
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return userList;
    }

    // Get a User
    public User getUser(String username) {
        User user = null;
        String getUser = String.format("SELECT * FROM USERS WHERE username = '%s';", username);

        try {
            ResultSet rs = c.createStatement().executeQuery(getUser);

            if (rs.next()) {
                if (User.hasWithUsername(username)) {
                    user = User.getWithUsername(username);
                } else {
                    user = new User(username, rs.getString("password"), UserRole.valueOf(rs.getString("role")));

                    for (String addressId : rs.getString("myAddresses").replaceAll("\\s", "").split(",")) {
                        if (addressId != null && !addressId.isEmpty()) {
                            user.addMyAddress(addressId);
                        }
                    }

                    for (String treeId : rs.getString("myTrees").replaceAll("\\s", "").split(",")) {
                        if (treeId.matches("^\\d+$") && getTree(Integer.parseInt(treeId)) != null) {
                            user.addMyTree(Integer.parseInt(treeId));
                        }
                    }
                }
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return user;
    }

    // Delete a User
    public boolean deleteUser(String username) {
        String deleteUser = String.format("DELETE FROM USERS WHERE username = '%s';", username);

        try {
            return c.createStatement().executeUpdate(deleteUser) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }



    // ==============================
    // SPECIES TABLE API
    // ==============================

    // Add a new Species
    public boolean insertSpecies(String name, String species, String genus) {
        String insertSpecies = String.format(
            "INSERT INTO SPECIES (name, species, genus) " +
            "VALUES ('%s', '%s', '%s');",
            name.replaceAll("'", "''"), species, genus);

        try {
            return c.createStatement().executeUpdate(insertSpecies) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Update a Species
    public boolean updateSpecies(String name, String species, String genus) {
        String updateSpecies = String.format(
            "UPDATE SPECIES " +
            "SET species = '%s', genus = '%s' " +
            "WHERE name = '%s';",
            species, genus, name.replaceAll("'", "''"));

        try {
            return c.createStatement().executeUpdate(updateSpecies) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Get all Species
    public ArrayList<Species> getAllSpecies() {
        ArrayList<Species> speciesList = new ArrayList<Species>();

        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM SPECIES;");

            while (rs.next()) {
                String name = rs.getString("name");

                if (Species.hasWithName(name)) {
                    speciesList.add(Species.getWithName(name));
                } else {
                    speciesList.add(new Species(name, rs.getString("species"), rs.getString("genus")));
                }
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return speciesList;
    }

    // Get a Species
    public Species getSpecies(String name) {
        Species species = null;
        String getSpecies = String.format("SELECT * FROM SPECIES WHERE name = '%s';", name.replaceAll("'", "''"));

        try {
            ResultSet rs = c.createStatement().executeQuery(getSpecies);

            if (rs.next()) {
                if (Species.hasWithName(name)) {
                    species = Species.getWithName(name);
                } else {
                    species = new Species(name, rs.getString("species"), rs.getString("genus"));
                }
            }

                rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return species;
    }

    // Delete a Species
    public boolean deleteSpecies(String name) {
        String speciesDelete = String.format("DELETE FROM SPECIES WHERE name = '%s';", name.replaceAll("'", "''"));

        try {
            return c.createStatement().executeUpdate(speciesDelete) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }



    // ==============================
    // LOCATIONS TABLE API
    // ==============================

    // Add a new Location
    public boolean insertLocation(int locationId, double latitude, double longitude) {
        String insertLocation = String.format(
            "INSERT INTO LOCATIONS (locationId, latitude, longitude) " +
            "VALUES (%d, %.8f, %.8f);",
            locationId, latitude, longitude);

        try {
            return c.createStatement().executeUpdate(insertLocation) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Update a Location
    public boolean updateLocation(int locationId, double latitude, double longitude) {
        String updateLocation = String.format(
            "UPDATE LOCATIONS " +
            "SET latitude = %.8f, longitude = %.8f " +
            "WHERE locationId = %d;",
            latitude, longitude, locationId);

        try {
            return c.createStatement().executeUpdate(updateLocation) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Get all Locations
    public ArrayList<Location> getAllLocations() {
        ArrayList<Location> locations = new ArrayList<>();

        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM LOCATIONS;");

            while (rs.next()) {
                locations.add(new Location(rs.getDouble("latitude"), rs.getDouble("longitude"), rs.getInt("locationId")));
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return locations;
    }

    // Get a Location
    public Location getLocation(int locationId) {
        Location location = null;
        String getLocation = String.format("SELECT * FROM LOCATIONS WHERE locationId = %d;", locationId);

        try {
            ResultSet rs = c.createStatement().executeQuery(getLocation);

            if (rs.next()) {
                location = new Location(rs.getDouble("latitude"), rs.getDouble("longitude"), locationId);
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return location;
    }

    // Get the highest locationId
    public int getMaxLocationId() {
        int getMaxLocationId = -1;

        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT MAX(locationId) AS maxLocationId FROM LOCATIONS;");

            if (rs.next()) {
                getMaxLocationId = rs.getInt("maxLocationId");
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return getMaxLocationId;
    }

    // Delete a Location
    public boolean deleteLocation(int locationId) {
        String deleteLocation = String.format("DELETE FROM LOCATIONS WHERE locationId = %d;", locationId);

        try {
            return c.createStatement().executeUpdate(deleteLocation) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }



    // ==============================
    // MUNICIPALITIES TABLE API
    // ==============================

    // Add a new Municipality
    public boolean insertMunicipality(String name, int totalTrees, String borders) {
        String insertMunicipality = String.format(
            "INSERT INTO MUNICIPALITIES (name, totalTrees, borders) " +
            "VALUES ('%s', %d, '%s');",
            name.replaceAll("'", "''"), totalTrees, borders);

        try {
            return c.createStatement().executeUpdate(insertMunicipality) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Update a Municipality
    public boolean updateMunicipalityBorders(String name, String borders) {
        String updateMunicipality = String.format(
            "UPDATE MUNICIPALITIES " +
            "SET borders = '%s' " +
            "WHERE name = '%s';",
            borders, name.replaceAll("'", "''"));

        try {
            return c.createStatement().executeUpdate(updateMunicipality) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Update a Municipality's total trees
    public boolean updateMunicipalityTotalTrees(String name, int totalTrees) {
        String updateMunicipality = String.format(
            "UPDATE MUNICIPALITIES " +
            "SET totalTrees = %d " +
            "WHERE name = '%s';",
            totalTrees, name.replaceAll("'", "''"));

        try {
            return c.createStatement().executeUpdate(updateMunicipality) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Update a Municipality's total trees by incrementing/decrementing
    public boolean updateMunicipalityIncDecTotalTrees(String name, int incDec) {
        String updateMunicipality = String.format(
            "UPDATE MUNICIPALITIES " +
            "SET totalTrees = totalTrees + %d " +
            "WHERE name = '%s';",
            incDec, name.replaceAll("'", "''"));

        try {
            return c.createStatement().executeUpdate(updateMunicipality) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Get all Municipalities
    public ArrayList<Municipality> getAllMunicipalities() {
        ArrayList<Municipality> municipalityList = new ArrayList<Municipality>();

        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM MUNICIPALITIES;");

            while (rs.next()) {
                String name = rs.getString("name");

                if (Municipality.hasWithName(name)) {
                    municipalityList.add(Municipality.getWithName(name));
                } else {
                    Municipality municipality = new Municipality(name, rs.getInt("totalTrees"));

                    for (String locationId : rs.getString("borders").replaceAll("\\s", "").split(",")) {
                        Location location;
                        if (locationId.matches("^\\d+$") && (location = getLocation(Integer.parseInt(locationId))) != null) {
                            municipality.addBorder(location);
                        }
                    }

                    municipalityList.add(municipality);
                }
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return municipalityList;
    }

    // Get a Municipality
    public Municipality getMunicipality(String name) {
        Municipality municipality = null;
        String getMunicipality = String.format("SELECT * FROM MUNICIPALITIES WHERE name = '%s';", name.replaceAll("'", "''"));

        try {
            ResultSet rs = c.createStatement().executeQuery(getMunicipality);

            if (rs.next()) {
                if (Municipality.hasWithName(name)) {
                    municipality = Municipality.getWithName(name);
                } else {
                    municipality = new Municipality(name, rs.getInt("totalTrees"));

                    for (String locationId : rs.getString("borders").replaceAll("\\s", "").split(",")) {
                        Location location;
                        if (locationId.matches("^\\d+$") && (location = getLocation(Integer.parseInt(locationId))) != null) {
                            municipality.addBorder(location);
                        }
                    }
                }
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return municipality;
    }

    // Delete a Municipality
    public boolean deleteMunicipality(String name) {
        String deleteMunicipality = String.format("DELETE FROM MUNICIPALITIES WHERE name = '%s';", name.replaceAll("'", "''"));

        try {
            return c.createStatement().executeUpdate(deleteMunicipality) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }



    // ==============================
    // SURVEYREPORTS TABLE API
    // ==============================

    // Add a new Survey Report
    public boolean insertSurveyReport(int reportId, String reportDate, String reportUser) {
        String insertSurveyReport = String.format(
            "INSERT INTO SURVEYREPORTS (reportId, reportDate, reportUser) " +
            "VALUES (%d, '%s', '%s');",
            reportId, reportDate, reportUser);

        try {
            return c.createStatement().executeUpdate(insertSurveyReport) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Get all Survey Reports
    public ArrayList<SurveyReport> getAllSurveyReports() {
        ArrayList<SurveyReport> reportList = new ArrayList<SurveyReport>();

        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM SURVEYREPORTS;");

            while (rs.next()) {
                reportList.add(new SurveyReport(Date.valueOf(rs.getString("reportDate")), rs.getString("reportUser"), rs.getInt("reportId")));
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return reportList;
    }

    // Get a Survey Report
    public SurveyReport getSurveyReport(int reportId) {
        SurveyReport report = null;
        String getSurveyReport = String.format("SELECT * FROM SURVEYREPORTS WHERE reportId = %d;", reportId);

        try {
            ResultSet rs = c.createStatement().executeQuery(getSurveyReport);

            if (rs.next()) {
                report = new SurveyReport(Date.valueOf(rs.getString("reportDate")), rs.getString("reportUser"), reportId);
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return report;
    }

    // Get the highest reportId
    public int getMaxReportId() {
        int getMaxReportId = -1;

        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT MAX(reportId) AS maxReportId FROM SURVEYREPORTS;");

            if (rs.next()) {
                getMaxReportId = rs.getInt("maxReportId");
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return getMaxReportId;
    }

    // Delete a Survey Report
    public boolean deleteSurveyReport(int reportId) {
        String deleteSurveyReport = String.format("DELETE FROM SURVEYREPORTS WHERE reportId = %d;", reportId);

        try {
            return c.createStatement().executeUpdate(deleteSurveyReport) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }



    // ==============================
    // FORECASTS TABLE API
    // ==============================

    // Add a new Forecast
    public boolean insertForecast(int forecastId, String fcDate, String fcUser, double co2Reduced,
                                  double biodiversity, double stormwater, double energyConserved, String fcTrees) {
        String insertForecast = String.format(
            "INSERT INTO FORECASTS (forecastId, fcDate, fcUser, co2Reduced, biodiversity, stormwater, energyConserved, fcTrees) " +
            "VALUES (%d, '%s', '%s', %.2f, %.5f, %.2f, %.2f, '%s');",
            forecastId, fcDate, fcUser, co2Reduced, biodiversity, stormwater, energyConserved, fcTrees);

        try {
            return c.createStatement().executeUpdate(insertForecast) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Get all Forecasts
    public ArrayList<Forecast> getAllForecasts() {
        ArrayList<Forecast> forecastList = new ArrayList<Forecast>();

        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM FORECASTS;");

            while (rs.next()) {
                forecastList.add(createForecast(rs));
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return forecastList;
    }

    // Get a Forecast
    public Forecast getForecast(int forecastId) {
        Forecast forecast = null;
        String getForecast = String.format("SELECT * FROM FORECASTS WHERE forecastId = %d;", forecastId);

        try {
            ResultSet rs = c.createStatement().executeQuery(getForecast);

            if (rs.next()) {
                forecast = createForecast(rs);
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return forecast;
    }

    // Get all Forecasts of a User
    public ArrayList<Forecast> getAllForecastsOfUser(String fcUser) {
        ArrayList<Forecast> forecastList = new ArrayList<Forecast>();
        String getForecastsOfUser = String.format("SELECT * FROM FORECASTS WHERE fcUser = '%s';", fcUser);

        try {
            ResultSet rs = c.createStatement().executeQuery(getForecastsOfUser);

            while (rs.next()) {
                forecastList.add(createForecast(rs));
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return forecastList;
    }

    // Get the highest forecastId
    public int getMaxForecastId() {
        int getMaxForecastId = -1;
        try {
            ResultSet rs = c.createStatement().executeQuery("SELECT MAX(forecastId) AS getMaxForecastId FROM FORECASTS;");

            if (rs.next()) {
                getMaxForecastId = rs.getInt("getMaxForecastId");
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return getMaxForecastId;
    }

    // Delete a Forecast
    public boolean deleteForecast(int forecastId) {
        String deleteForecast = String.format("DELETE FROM FORECASTS WHERE forecastId = %d;", forecastId);

        try {
            return c.createStatement().executeUpdate(deleteForecast) <= 0 ? false : true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return false;
    }

    // Create a Forecast object
    private Forecast createForecast(ResultSet rs) throws Exception {
        int forecastId = rs.getInt("forecastId");
        Date fcDate = Date.valueOf(rs.getString("fcDate"));
        String fcUser = rs.getString("fcUser");
        double stormwater = rs.getDouble("stormwater");
        double co2Reduced = rs.getDouble("co2Reduced");
        double biodiversity = rs.getDouble("biodiversity");
        double energyConserved = rs.getDouble("energyConserved");
        ArrayList<Tree> fcTrees = new ArrayList<Tree>();

        for (String treeId : rs.getString("fcTrees").replaceAll("\\s", "").split(",")) {
            Tree tree;
            if (treeId.matches("^\\d+$") && (tree = getTree(Integer.parseInt(treeId))) != null) {
                fcTrees.add(tree);
            }
        }

        return new Forecast(fcDate, fcUser, stormwater, co2Reduced,
                            biodiversity, energyConserved, forecastId, fcTrees);
    }
}