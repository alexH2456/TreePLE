package ca.mcgill.ecse321.treeple.controller;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ca.mcgill.ecse321.treeple.service.InvalidInputException;
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

    // @Autowired
    // private WebApplicationContext wac;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mockController).build();
        // mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testGetAllTreesEmptyDB() throws Exception {
        mockMvc.perform(get("/trees/").contentType(APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_UTF8))
            .andExpect(content().string("[]"));
    }

    // @Test(expected = InvalidInputException.class)
    // public void testGetTreeNonExistant() throws Exception {
    //     mockMvc.perform(get("/trees/10/").contentType(APPLICATION_JSON))
    //     .andExpect(status().isInternalServerError())
    //     .andExpect(content().contentType(APPLICATION_JSON_UTF8))
    //     .andExpect(content().string("[]"));
    // }
}
