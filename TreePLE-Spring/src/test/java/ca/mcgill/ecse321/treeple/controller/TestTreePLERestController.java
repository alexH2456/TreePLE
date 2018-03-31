package ca.mcgill.ecse321.treeple.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.mcgill.ecse321.treeple.service.TreePLEService;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class TestTreePLERestController {

    private MockMvc mockMvc;

    @Mock
    TreePLEService mockService;

    @InjectMocks
    TreePLERestController mockController = new TreePLERestController();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mockController).build();
    }

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
