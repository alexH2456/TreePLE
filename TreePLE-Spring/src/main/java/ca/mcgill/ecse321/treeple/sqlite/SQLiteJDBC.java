package ca.mcgill.ecse321.treeple.sqlite;

import java.sql.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.model.Tree.*;
import ca.mcgill.ecse321.treeple.model.User.*;

public class SQLiteJDBC {
    private static Connection c;
    private static Statement stmt;
    private static String dbPath;

    public SQLiteJDBC() {
        dbPath = new File(System.getProperty("user.dir")).getParent() + "/db/treeple.db";
    }

    public SQLiteJDBC(String filename) {
        dbPath = new File(System.getProperty("user.dir")).getParent() + filename;
    }

    public String getDbPath() {
        return dbPath;
    }

    // ==============================
    // CONNECTION API
    // ==============================

    // Connect to a database
    public void connect() {
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
                            + " species      INT,"
                            + " location     INT          NOT NULL,"
                            + " municipality VARCHAR(50),"
                            + " reports      TEXT         NOT NULL)";

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
                                    + " reportingUser VARCHAR(50) NOT NULL)";


            // Users DB Table
            String sqlUsers = "CREATE TABLE IF NOT EXISTS USERS "
                            + "(username VARCHAR(50) PRIMARY KEY NOT NULL,"
                            + " password    VARCHAR(50) NOT NULL,"
                            + " role        VARCHAR(50) NOT NULL,"
                            + " myAddresses TEXT,"
                            + " myTrees     TEXT)";

            stmt = c.createStatement();
            stmt.executeUpdate(sqlTrees);
            stmt.executeUpdate(sqlSpecies);
            stmt.executeUpdate(sqlLocations);
            stmt.executeUpdate(sqlMunicipalities);
            stmt.executeUpdate(sqlSurveyReports);
            stmt.executeUpdate(sqlUsers);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Close connection to database
    public void closeConnection() {
        try {
            if (stmt != null)
                stmt.close();
            if (c != null)
                c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void deleteDB() {
        try {
            Files.deleteIfExists(new File(dbPath).toPath());
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }



    // ==============================
    // TREES TABLE API
    // ==============================

    // Add a new Tree
    public void insertTree(int treeId, int height, int diameter, String address,
                           String datePlanted, String land, String status, String ownership,
                           String species, int location, String municipality, String reports) {
        String insertTree = String.format(
            "INSERT INTO TREES (treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports) " +
            "VALUES (%d, %d, %d, '%s', '%s', '%s', '%s', '%s', '%s', %d, '%s' '%s');",
            treeId, height, diameter, address, datePlanted, land, status, ownership, species, location, municipality, reports);

        try {
            stmt.executeUpdate(insertTree);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Update a Tree
    public void updateTree(int treeId, int height, int diameter, String land, String status,
                           String ownership, String species, String municipality, String reports) {
        String updateTree = String.format(
            "UPDATE TREES " +
            "SET height = %d, diameter = %d, land = '%s', status = '%s', ownership = '%s', species = '%s', municipality = '%s', reports = '%s' " +
            "WHERE treeId = %d;",
            height, diameter, land, status, ownership, species, municipality, reports, treeId);

        try {
            stmt.executeUpdate(updateTree);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Update a Tree's Survey Reports
    public void updateTreeSurveyReport(int treeId, String reports) {
        String updateTreeReport = String.format("UPDATE TREES SET reports = '%s' WHERE treeId = %d;", reports, treeId);

        try {
            stmt.executeUpdate(updateTreeReport);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Get all Trees
    public ArrayList<Tree> getAllTrees() {
        ArrayList<Tree> treeList = new ArrayList<Tree>();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM TREES;");

            while (rs.next()) {
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

                for (String reportId : rs.getString("reports").split(",")) {
                    if (reportId.matches("^\\d+$") && getSurveyReport(Integer.parseInt(reportId)) != null) {
                        reports.add(getSurveyReport(Integer.parseInt(reportId)));
                    }
                }

                treeList.add(new Tree(height, diameter, address, datePlanted, land, status,
                                      ownership, species, location, municipality, treeId, reports));
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
            ResultSet rs = stmt.executeQuery(getTree);

            if (rs.next()) {
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

                for (String reportId : rs.getString("reports").split(",")) {
                    if (reportId.matches("^\\d+$") && getSurveyReport(Integer.parseInt(reportId)) != null) {
                        reports.add(getSurveyReport(Integer.parseInt(reportId)));
                    }
                }

                tree = new Tree(height, diameter, address, datePlanted, land, status,
                                ownership, species, location, municipality, treeId, reports);
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return tree;
    }

    // Get the highest treeId
    public int getMaxTreeId() {
        int getMaxTreeId = -1;
        try {
            ResultSet rs = stmt.executeQuery("SELECT MAX(treeId) AS maxTreeId FROM TREES;");

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
    public void deleteTree(int treeId) {
        String deleteTree = String.format("DELETE FROM TREES WHERE treeId = %d;", treeId);

        try {
            stmt.executeUpdate(deleteTree);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }



    // ==============================
    // SPECIES TABLE API
    // ==============================

    // Add a new Species
    public void insertSpecies(String name, String species, String genus) {
        String insertSpecies = String.format(
            "INSERT INTO SPECIES (name, species, genus) " +
            "VALUES ('%s', '%s', '%s');",
            name, species, genus);

        try {
            stmt.executeUpdate(insertSpecies);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Update a Species
    public void updateSpecies(String name, String species, String genus) {
        String updateSpecies = String.format(
            "UPDATE SPECIES " +
            "SET species = '%s', genus = '%s' " +
            "WHERE name = '%s';",
            species, genus, name);

        try {
            stmt.executeUpdate(updateSpecies);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Get all Species
    public ArrayList<Species> getAllSpecies() {
        ArrayList<Species> speciesList = new ArrayList<Species>();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM SPECIES;");

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
        String getSpecies = String.format("SELECT * FROM SPECIES WHERE name = '%s';", name);

        try {
            ResultSet rs = stmt.executeQuery(getSpecies);

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
    public void deleteSpecies(String name) {
        String speciesDelete = String.format("DELETE FROM SPECIES WHERE name = '%s';", name);

        try {
            stmt.executeUpdate(speciesDelete);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }



    // ==============================
    // LOCATIONS TABLE API
    // ==============================

    // Add a new Location
    public void insertLocation(int locationId, Double latitude, Double longitude) {
        String insertLocation = String.format(
            "INSERT INTO LOCATIONS (locationId, latitude, longitude) " +
            "VALUES (%d, %.8f, %.8f);",
            locationId, latitude, longitude);

        try {
            stmt.executeUpdate(insertLocation);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Update a Location
    public void updateLocation(int locationId, Double latitude, Double longitude) {
        String updateLocation = String.format(
            "UPDATE LOCATIONS " +
            "SET latitude = %.8f, longitude = %.8f " +
            "WHERE locationId = %d;",
            latitude, longitude, locationId);

        try {
            stmt.executeUpdate(updateLocation);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Get a Location
    public Location getLocation(int locationId) {
        Location location = null;
        String getLocation = String.format("SELECT * FROM LOCATIONS WHERE locationId = %d;", locationId);

        try {
            ResultSet rs = stmt.executeQuery(getLocation);

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
            ResultSet rs = stmt.executeQuery("SELECT MAX(locationId) AS maxLocationId FROM LOCATIONS;");

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
    public void deleteLocation(int locationId) {
        String deleteLocation = String.format("DELETE FROM LOCATIONS WHERE locationId = %d;", locationId);

        try {
            stmt.executeUpdate(deleteLocation);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }



    // ==============================
    // MUNICIPALITIES TABLE API
    // ==============================

    // Add a new Municipality
    public void insertMunicipality(String name, int totalTrees, String borders) {
        String insertMunicipality = String.format(
            "INSERT INTO MUNICIPALITIES (name, totalTrees, borders) " +
            "VALUES ('%s', %d, '%s');",
            name, totalTrees, borders);

        try {
            stmt.executeUpdate(insertMunicipality);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Update a Municipality
    public void updateMunicipality(String name, int totalTrees, String borders) {
        String updateMunicipality = String.format(
            "UPDATE MUNICIPALITIES " +
            "SET totalTrees = %d, borders = '%s' " +
            "WHERE name = '%s';",
            totalTrees, borders, name);

        try {
            stmt.executeUpdate(updateMunicipality);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Get all Municipalities
    public ArrayList<Municipality> getAllMunicipalities() {
        ArrayList<Municipality> municipalityList = new ArrayList<Municipality>();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM MUNICIPALITIES;");

            while (rs.next()) {
                String name = rs.getString("name");

                if (Municipality.hasWithName(name)) {
                    municipalityList.add(Municipality.getWithName(name));
                } else {
                    Municipality municipality = new Municipality(name, rs.getInt("totalTrees"));

                    for (String locationId : rs.getString("borders").split(",")) {
                        if (locationId.matches("^\\d+$") && getLocation(Integer.parseInt(locationId)) != null) {
                            municipality.addBorder(getLocation(Integer.parseInt(locationId)));
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
        String getMunicipality = String.format("SELECT * FROM MUNICIPALITIES WHERE name = '%s';", name);

        try {
            ResultSet rs = stmt.executeQuery(getMunicipality);

            if (rs.next()) {
                if (Municipality.hasWithName(name)) {
                    municipality = Municipality.getWithName(name);
                } else {
                    municipality = new Municipality(name, rs.getInt("totalTrees"));

                    for (String locationId : rs.getString("borders").split(",")) {
                        if (locationId.matches("^\\d+$") && getLocation(Integer.parseInt(locationId)) != null) {
                            municipality.addBorder(getLocation(Integer.parseInt(locationId)));
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
    public void deleteMunicipality(String name) {
        String deleteMunicipality = String.format("DELETE FROM MUNICIPALITIES WHERE name = '%s';", name);

        try {
            stmt.executeUpdate(deleteMunicipality);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }



    // ==============================
    // SURVEYREPORTS TABLE API
    // ==============================

    // Add a new Survey Report
    public void insertSurveyReport(int reportId, String reportDate, String reportingUser) {
        String insertSurveyReport = String.format(
            "INSERT INTO SURVEYREPORTS (reportId, reportDate, reportingUser) " +
            "VALUES (%d, '%s', '%s');",
            reportId, reportDate, reportingUser);

        try {
            stmt.executeUpdate(insertSurveyReport);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Get all Survey Reports
    public ArrayList<SurveyReport> getAllSurveyReports() {
        ArrayList<SurveyReport> reportList = new ArrayList<SurveyReport>();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM SURVEYREPORTS;");

            while (rs.next()) {
                reportList.add(new SurveyReport(Date.valueOf(rs.getString("reportDate")), rs.getString("reportingUser"), rs.getInt("reportId")));
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
            ResultSet rs = stmt.executeQuery(getSurveyReport);

            if (rs.next()) {
                report = new SurveyReport(Date.valueOf(rs.getString("reportDate")), rs.getString("reportingUser"), reportId);
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
            ResultSet rs = stmt.executeQuery("SELECT MAX(reportId) AS getMaxReportId FROM SURVEYREPORTS;");

            if (rs.next()) {
                getMaxReportId = rs.getInt("getMaxReportId");
            }

            rs.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return getMaxReportId;
    }

    // Delete a Survey Report
    public void deleteSurveyReport(int reportId) {
        String deleteSurveyReport = String.format("DELETE FROM SURVEYREPORTS WHERE reportId = %d;", reportId);

        try {
            stmt.executeUpdate(deleteSurveyReport);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }



    // ==============================
    // USERS TABLE API
    // ==============================

    // Add a new User
    public void insertUser(String username, String password, String role, String myAddresses, String myTrees) {
        String insertUser = String.format(
            "INSERT INTO USERS (username, password, role, myAddresses, myTrees) " +
            "VALUES ('%s', '%s', '%s', '%s', '%s');",
            username, password, role, myAddresses, myTrees);

        try {
            stmt.executeUpdate(insertUser);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Update a User's password
    public void updateUserPassword(String username, String password) {
        String updateUserPassword = String.format(
            "UPDATE USERS " +
            "SET password = '%s' " +
            "WHERE username = '%s';",
            password, username);

        try {
            stmt.executeUpdate(updateUserPassword);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Update a User's role
    public void updateUserRole(String username, String role) {
        String updateUserRole = String.format(
            "UPDATE USERS " +
            "SET role = '%s' " +
            "WHERE username = '%s';",
            role, username);

        try {
            stmt.executeUpdate(updateUserRole);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }


    // Update a User's addresses
    public void updateUserAddresses(String username, String myAddresses) {
        String updateUserAddresses = String.format(
            "UPDATE USERS " +
            "SET myAddresses = '%s' " +
            "WHERE username = '%s';",
            myAddresses, username);

        try {
            stmt.executeUpdate(updateUserAddresses);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }


    // Update a User's trees
    public void updateUserTrees(String username, String myTrees) {
        String updateUserTrees = String.format(
            "UPDATE USERS " +
            "SET myTrees = '%s' " +
            "WHERE username = '%s';",
            myTrees, username);

        try {
            stmt.executeUpdate(updateUserTrees);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // Get all Users
    public ArrayList<User> getAllUsers() {
        ArrayList<User> userList = new ArrayList<User>();

        try {
            ResultSet rs = stmt.executeQuery("SELECT * FROM USERS;");

            while (rs.next()) {
                String username = rs.getString("username");

                if (User.hasWithUsername(username)) {
                    userList.add(User.getWithUsername(username));
                } else {
                    User user = new User(username, rs.getString("password"), UserRole.valueOf(rs.getString("role")));

                    for (String addressId : rs.getString("myAddresses").split(",")) {
                        if (addressId != null && !addressId.replaceAll("\\s", "").isEmpty()) {
                            user.addMyAddress(addressId);
                        }
                    }

                    for (String treeId : rs.getString("myTrees").split(",")) {
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
            ResultSet rs = stmt.executeQuery(getUser);

            if (rs.next()) {
                if (User.hasWithUsername(username)) {
                    user = User.getWithUsername(username);
                } else {
                    user = new User(username, rs.getString("password"), UserRole.valueOf(rs.getString("role")));

                    for (String addressId : rs.getString("myAddresses").split(",")) {
                        if (addressId != null && !addressId.replaceAll("\\s", "").isEmpty()) {
                            user.addMyAddress(addressId);
                        }
                    }

                    for (String treeId : rs.getString("myTrees").split(",")) {
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
    public void deleteUser(String username) {
        String deleteUser = String.format("DELETE FROM USERS WHERE username = '%s';", username);

        try {
            stmt.executeUpdate(deleteUser);
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }



    // ==============================
    // MAIN FUNCTION
    // ==============================

    /*
    public void main(String[] args) {
        c = null;
        stmt = null;

        connect();
        insertUser(2, "Abbas", "1q2w3e", 21, 100, "{\"AXIOS1EV2\": 3, \"ELEK56VUA\": 9, \"IDEK1053R\": 10}");
        insertLocation("AXAX", "Pizza Pizza", "1846", "Saint-Catherine Street", 600,
        "{" + "\"Martin\": {\"checkIn\": \"17:30:00\", \"checkOut\": \"17:45:00\"},"
                    + "\"Jennifer\": {\"checkIn\": \"09:11:11\", \"checkOut\": \"09:12:34\"},"
                    + "\"Motassaem\": {\"checkIn\": \"09:59:00\", \"checkOut\": \"19:00:00\"}"
                    + "}");

        showUsers();
        showLocations();

        deleteUser(2);
        deleteLocation("AXAX");

        showUsers();
        showLocations();

        updateUserPassword(1, "fgtboi");

        closeConnection();
    }
    */
}
