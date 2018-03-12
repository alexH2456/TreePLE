package ca.mcgill.ecse321.treeple;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ca.mcgill.ecse321.treeple.service.TestTreePLEService;
import ca.mcgill.ecse321.treeple.sqlite.TestSQLiteJDBC;

@RunWith(Suite.class)
@SuiteClasses({TestTreePLEService.class, TestSQLiteJDBC.class})
public class AllTests {
}
