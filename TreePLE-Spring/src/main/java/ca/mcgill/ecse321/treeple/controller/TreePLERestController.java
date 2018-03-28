package ca.mcgill.ecse321.treeple.controller;

import java.util.*;

import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ca.mcgill.ecse321.treeple.dto.*;
import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.service.TreePLEService;

@RestController
public class TreePLERestController {

    @Autowired
    private TreePLEService service;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping("/")
    public String index() {
        return "TreePLE application root. Web-based frontend is a TODO. Use the REST API to manage TreePLE objects.\n";
    }


    // ==============================
    // DTO CONVERSION API
    // ==============================

    private TreeDto convertToDto(Tree tree) {
        return new TreeDto(tree.getTreeId(), tree.getHeight(), tree.getDiameter(), tree.getAddress(),
                           tree.getDatePlanted(), tree.getLand(), tree.getStatus(), tree.getOwnership(),
                           convertToDto(tree.getSpecies()), convertToDto(tree.getLocation()),
                           convertToDto(tree.getMunicipality()), createSurveyReportDtos(tree.getReports()));
    }

    private UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private SpeciesDto convertToDto(Species species) {
        return modelMapper.map(species, SpeciesDto.class);
    }

    private LocationDto convertToDto(Location location) {
        return modelMapper.map(location, LocationDto.class);
    }

    private MunicipalityDto convertToDto(Municipality municipality) {
        return new MunicipalityDto(municipality.getName(), municipality.getTotalTrees(),
                                   createLocationDtos(municipality.getBorders()));
    }

    private SurveyReportDto convertToDto(SurveyReport report) {
        return modelMapper.map(report, SurveyReportDto.class);
    }

    private ArrayList<SurveyReportDto> createSurveyReportDtos(List<SurveyReport> reports) {
        ArrayList<SurveyReportDto> reportDtos = new ArrayList<SurveyReportDto>();
        for (SurveyReport report : reports) {
            reportDtos.add(convertToDto(report));
        }
        return reportDtos;
    }

    private ArrayList<LocationDto> createLocationDtos(List<Location> locations) {
        ArrayList<LocationDto> locationDtos = new ArrayList<LocationDto>();
        for (Location location : locations) {
            locationDtos.add(convertToDto(location));
        }
        return locationDtos;
    }


    // ==============================
    // GET ALL MAPPING API
    // ==============================

    @GetMapping(value = {"/trees/"})
    public List<TreeDto> getAllTrees() {
        List<TreeDto> trees = new ArrayList<TreeDto>();
        for (Tree tree : service.getAllTrees())
            trees.add(convertToDto(tree));
        return trees;
    }

    @GetMapping(value = {"/users/"})
    public List<UserDto> getAllUsers() {
        List<UserDto> users = new ArrayList<UserDto>();
        for (User user : service.getAllUsers())
            users.add(convertToDto(user));
        return users;
    }

    @GetMapping(value = {"/species/"})
    public List<SpeciesDto> getAllSpecies() {
        List<SpeciesDto> species = new ArrayList<SpeciesDto>();
        for (Species name : service.getAllSpecies())
            species.add(convertToDto(name));
        return species;
    }

    @GetMapping(value = {"/locations/"})
    public List<LocationDto> getAllLocations() {
        List<LocationDto> locations = new ArrayList<LocationDto>();
        for (Location location : service.getAllLocations())
            locations.add(convertToDto(location));
        return locations;
    }

    @GetMapping(value = {"/municipalities/"})
    public List<MunicipalityDto> getAllMunicipalities() {
        List<MunicipalityDto> municipalities = new ArrayList<MunicipalityDto>();
        for (Municipality municipality : service.getAllMunicipalities())
            municipalities.add(convertToDto(municipality));
        return municipalities;
    }


    // ==============================
    // GET ONE MAPPING API
    // ==============================

    @GetMapping(value = {"/trees/{treeid}/"})
    public TreeDto getTreeById(@PathVariable("treeid") int treeId) throws Exception {
        Tree tree = service.getTreeById(treeId);
        return convertToDto(tree);
    }

    @GetMapping(value = {"/users/{username}/"})
    public UserDto getUserByUsername(@PathVariable("username") String username) throws Exception {
        User user = service.getUserByUsername(username);
        return convertToDto(user);
    }

    @GetMapping(value = {"/species/{name}/"})
    public SpeciesDto getSpeciesByName(@PathVariable("name") String name) throws Exception {
        Species species = service.getSpeciesByName(name);
        return convertToDto(species);
    }

    @GetMapping(value = {"/locations/{locationid}/"})
    public LocationDto getLocationById(@PathVariable("locationid") int locationId) throws Exception {
        Location location = service.getLocationById(locationId);
        return convertToDto(location);
    }

    @GetMapping(value = {"/municipalities/{name}/"})
    public MunicipalityDto getMunicipalityByName(@PathVariable("name") String name) throws Exception {
        Municipality municipality = service.getMunicipalityByName(name);
        return convertToDto(municipality);
    }

    @GetMapping(value = {"/reports/{reportid}/"})
    public SurveyReportDto getSurveyReportById(@PathVariable("reportid") int reportId) throws Exception {
        SurveyReport report = service.getSurveyReportById(reportId);
        return convertToDto(report);
    }


    // ==============================
    // POST MAPPING API
    // ==============================

    @PostMapping(value = {"/newtree/"})
    public TreeDto createTree(@RequestBody String jsonBody) throws Exception {
        Tree tree = service.createTree(new JSONObject(jsonBody));
        return convertToDto(tree);
    }

    @PostMapping(value = {"/newuser/"})
    public UserDto createUser(@RequestBody String jsonBody) throws Exception {
        User user = service.createUser(new JSONObject(jsonBody));
        return convertToDto(user);
    }

    @PostMapping(value = {"/newspecies/"})
    public SpeciesDto createSpecies(@RequestBody String jsonBody) throws Exception {
        Species species = service.createSpecies(new JSONObject(jsonBody));
        return convertToDto(species);
    }

    @PostMapping(value = {"/newmunicipality/"})
    public MunicipalityDto createMunicipality(@RequestBody String jsonBody) throws Exception {
        Municipality municipality = service.createMunicipality(new JSONObject(jsonBody));
        return convertToDto(municipality);
    }


    // ==============================
    // PATCH MAPPING API
    // ==============================

    @PatchMapping(value = {"/trees/update/"})
    public TreeDto updateTree(@RequestBody String jsonBody) throws Exception {
        Tree tree = service.updateTree(new JSONObject(jsonBody));
        return convertToDto(tree);
    }

    @PatchMapping(value = {"/users/update/"})
    public UserDto patchDto(@RequestBody String jsonBody) throws Exception {
        User user = service.updateUser(new JSONObject(jsonBody));
        return convertToDto(user);
    }

    @PatchMapping(value = {"/species/update/"})
    public SpeciesDto updateUserPoints(@RequestBody String jsonBody) throws Exception {
        Species species = service.updateSpecies(new JSONObject(jsonBody));
        return convertToDto(species);
    }

    @PatchMapping(value = {"/municipalities/update/"})
    public MunicipalityDto updateUserPassword(@RequestBody String jsonBody) throws Exception {
        Municipality municipality = service.updateMunicipality(new JSONObject(jsonBody));
        return convertToDto(municipality);
    }


    // ==============================
    // DELETE MAPPING API
    // ==============================

    @PostMapping(value = {"/deletetree/"})
    public TreeDto deleteTree(@RequestBody String jsonBody) throws Exception {
        Tree tree = service.deleteTree(new JSONObject(jsonBody));
        return convertToDto(tree);
    }

    @PostMapping(value = {"/deleteuser/"})
    public UserDto deleteUser(@RequestBody String jsonBody) throws Exception {
        User user = service.deleteUser(new JSONObject(jsonBody));
        return convertToDto(user);
    }

    @PostMapping(value = {"/deletespecies/"})
    public SpeciesDto deleteSpecies(@RequestBody String jsonBody) throws Exception {
        Species species = service.deleteSpecies(new JSONObject(jsonBody));
        return convertToDto(species);
    }

    @PostMapping(value = {"/deletelocation/"})
    public LocationDto deleteLocation(@RequestBody String jsonBody) throws Exception {
        Location location = service.deleteLocation(new JSONObject(jsonBody));
        return convertToDto(location);
    }

    @PostMapping(value = {"/deletemunicipality/"})
    public MunicipalityDto deleteMunicipality(@RequestBody String jsonBody) throws Exception {
        Municipality municipality = service.deleteMunicipality(new JSONObject(jsonBody));
        return convertToDto(municipality);
    }

    @DeleteMapping(value = {"/reset/"})
    public void resetDatabase() throws Exception {
        service.resetDatabase();
    }

    @DeleteMapping(value = {"/delete/"})
    public void deleteDatabase() throws Exception {
        service.deleteDatabase();
    }
}
