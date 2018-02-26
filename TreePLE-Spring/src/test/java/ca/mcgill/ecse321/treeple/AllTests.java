package ca.mcgill.ecse321.treeple;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ca.mcgill.ecse321.treeple.service.TestTreePLEService;
import ca.mcgill.ecse321.treeple.persistence.TestPersistence;

@RunWith(Suite.class)
@SuiteClasses({TestTreePLEService.class, TestPersistence.class})
public class AllTests {
}
