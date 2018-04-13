package ca.mcgill.ecse321.treeple.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.sql.Date;
import java.util.List;

import org.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.mcgill.ecse321.treeple.dto.TreeDto;
import ca.mcgill.ecse321.treeple.model.Location;
import ca.mcgill.ecse321.treeple.model.SurveyReport;
import ca.mcgill.ecse321.treeple.model.Tree;
import ca.mcgill.ecse321.treeple.model.Tree.Land;
import ca.mcgill.ecse321.treeple.model.Tree.Ownership;
import ca.mcgill.ecse321.treeple.model.Tree.Status;
import ca.mcgill.ecse321.treeple.service.TestTreePLEService;
import ca.mcgill.ecse321.treeple.service.TreePLEService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class TestTreePLERestController {

    private MockMvc mockMvc;
    private static JSONObject testTree;
    private static JSONObject testUser;
    private static JSONObject testSpecies;
    private static JSONObject testLocation;
    private static JSONObject testMunicipality;

    @Mock
    TreePLEService mockService;

    @InjectMocks
    TreePLERestController mockController = new TreePLERestController();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mockController).build();
    }
//    @Test
//    public void testGetAllTrees() throws Exception{
//    	mockService.createSpecies(testSpecies);
//    	mockService.createMunicipality(testMunicipality);
//    	mockService.createUser(testUser);
//    	for (int i = 0; i < 4; i++) {
//            mockService.createTree(testTree);
//        }
//    	
//    	try {
//    		List<TreeDto> trees = mockController.getAllTrees();
//    		
//    		for(TreeDto treeDto: trees) {
//                assertEquals(testTree.getInt("treeId"), treeDto.getTreeId());
//                assertEquals(testTree.getInt("height"), treeDto.getHeight());
//                assertEquals(testTree.getInt("diameter"), treeDto.getDiameter());
//                assertEquals(Date.valueOf(testTree.getString("datePlanted")), treeDto.getDatePlanted());
//                assertEquals(Land.valueOf(testTree.getString("land")), treeDto.getLand());
//                assertEquals(Status.valueOf(testTree.getString("status")), treeDto.getStatus());
//                assertEquals(Ownership.valueOf(testTree.getString("ownership")), treeDto.getOwnership());
//                assertEquals(testTree.getString("species"), treeDto.getSpecies().getName());
//                assertEquals(testTree.getDouble("latitude"), treeDto.getLocation().getLatitude(), 0);
//                assertEquals(testTree.getDouble("longitude"), treeDto.getLocation().getLongitude(), 0);
//                assertEquals(testTree.getString("municipality"), treeDto.getMunicipality().getName());
//    		}
//    	}catch(Exception e) {
//    		fail();
//    	}
//    }

    @Test
    public void testGetAllTreesEmptyDB() throws Exception {
        mockMvc.perform(get("/trees/").contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(content().string("[]"));
    }
    
    

    @Test
    public void testGetAllUsersEmptyDB() throws Exception {
        mockMvc.perform(get("/users/").contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(content().string("[]"));
    }

    @Test
    public void testGetAllSpeciesEmptyDB() throws Exception {
        mockMvc.perform(get("/species/").contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(content().string("[]"));
    }

    @Test
    public void testGetAllLocationsEmptyDB() throws Exception {
        mockMvc.perform(get("/locations/").contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(content().string("[]"));
    }

    @Test
    public void testGetAllMunicipalitiesEmptyDB() throws Exception {
        mockMvc.perform(get("/municipalities/").contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(content().string("[]"));
    }

    // @Test
    // public void testGetTreeNonExistant() throws Exception {
    //     mockMvc.perform(get("/trees/10/").contentType(APPLICATION_JSON))
    //     .andExpect(status().isInternalServerError())
    //     .andExpect(content().contentType(APPLICATION_JSON_UTF8))
    //     .andExpect(content().string("[]"));
    // }
}
