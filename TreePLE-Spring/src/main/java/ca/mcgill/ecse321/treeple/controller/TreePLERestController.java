package ca.mcgill.ecse321.treeple.controller;

import java.util.*;

import org.json.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ca.mcgill.ecse321.treeple.dto.*;
import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.service.TreePLEService;

@CrossOrigin(maxAge = 3600)
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

    private ForecastDto convertToDto(Forecast forecast) {
        return new ForecastDto(forecast.getForecastId(), forecast.getFcDate(), forecast.getFcUser(),
                               forecast.getStormwater(), forecast.getCo2Reduced(), forecast.getBiodiversity(),
                               forecast.getEnergyConserved(), createTreeDtos(forecast.getFcTrees()));
    }

    private ArrayList<TreeDto> createTreeDtos(List<Tree> trees) {
        ArrayList<TreeDto> treesDto = new ArrayList<TreeDto>();
        for (Tree tree : trees) {
            treesDto.add(convertToDto(tree));
        }
        return treesDto;
    }

    private ArrayList<LocationDto> createLocationDtos(List<Location> locations) {
        ArrayList<LocationDto> locationDtos = new ArrayList<LocationDto>();
        for (Location location : locations) {
            locationDtos.add(convertToDto(location));
        }
        return locationDtos;
    }

    private ArrayList<SurveyReportDto> createSurveyReportDtos(List<SurveyReport> reports) {
        ArrayList<SurveyReportDto> reportDtos = new ArrayList<SurveyReportDto>();
        for (SurveyReport report : reports) {
            reportDtos.add(convertToDto(report));
        }
        return reportDtos;
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

    @GetMapping(value = {"/forecasts/"})
    public List<ForecastDto> getAllForecasts() {
        List<ForecastDto> forecasts = new ArrayList<ForecastDto>();
        for (Forecast forecast : service.getAllForecasts())
            forecasts.add(convertToDto(forecast));
        return forecasts;
    }


    // ==============================
    // GET ONE MAPPING API
    // ==============================

    @GetMapping(value = {"/trees/{treeid}/"})
    public TreeDto getTreeById(@PathVariable("treeid") int treeId) throws Exception {
        Tree tree = service.getTreeById(treeId);
        return convertToDto(tree);
    }

    @GetMapping(value = {"/trees/{treeId}/sustainability/"})
    public Map<String, Map<String, Double>> getTreeSustainability(@PathVariable("treeId") int treeId) throws Exception {
        return service.getTreeSustainability(service.getTreeById(treeId));
    }

    @GetMapping(value = {"/users/{username}/"})
    public UserDto getUserByUsername(@PathVariable("username") String username) throws Exception {
        User user = service.getUserByUsername(username);
        return convertToDto(user);
    }

    @GetMapping(value = {"/users/{username}/trees/"})
    public List<Tree> getUserTrees(@PathVariable("username") String username) throws Exception {
        return service.getTreesOfUser(username);
    }

    @GetMapping(value = {"/users/{username}/forecasts/"})
    public List<Forecast> getUserForecasts(@PathVariable("username") String username) throws Exception {
        return service.getForecastsOfUser(username);
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

    @GetMapping(value = {"/municipalities/{name}/sustainability/"})
    public Map<String, Map<String, Double>> getMunicipalitySustainability(@PathVariable("name") String name) throws Exception {
        return service.getGroupSustainability(service.getTreesOfMunicipality(name));
    }

    @GetMapping(value = {"/reports/{reportid}/"})
    public SurveyReportDto getSurveyReportById(@PathVariable("reportid") int reportId) throws Exception {
        SurveyReport report = service.getSurveyReportById(reportId);
        return convertToDto(report);
    }

    @GetMapping(value = {"/forecasts/{forecastid}/"})
    public ForecastDto getForecastById(@PathVariable("forecastid") int forecastId) throws Exception {
        Forecast forecast = service.getForecastById(forecastId);
        return convertToDto(forecast);
    }

    @PostMapping(value = {"/sustainability/"})
    public Map<String, Map<String, Double>> getGroupSustainability(@RequestBody String jsonBody) throws Exception {
        return service.getGroupSustainability(service.getTreesFromIdList(new JSONArray(jsonBody)));
    }

    @GetMapping(value = {"/sustainability/treeple/"})
    public Map<String, Map<String, Double>> getTreePLESustainability() throws Exception {
        return service.getGroupSustainability(service.getAllTrees());
    }


    // ==============================
    // POST MAPPING API
    // ==============================

    @PostMapping(value = {"/login/"})
    public UserDto login(@RequestBody String jsonBody) throws Exception {
        User user = service.login(new JSONObject(jsonBody));
        return convertToDto(user);
    }

    @PostMapping(value = {"/authenticated/"})
    public Map<String, Boolean> authenticated(@RequestBody String jsonBody) throws Exception {
        return service.authenticated(new JSONObject(jsonBody));
    }

    @PostMapping(value = {"/tree/new/"})
    public TreeDto createTree(@RequestBody String jsonBody) throws Exception {
        Tree tree = service.createTree(new JSONObject(jsonBody));
        return convertToDto(tree);
    }

    @PostMapping(value = {"/user/new/"})
    public UserDto createUser(@RequestBody String jsonBody) throws Exception {
        User user = service.createUser(new JSONObject(jsonBody));
        return convertToDto(user);
    }

    @PostMapping(value = {"/species/new/"})
    public SpeciesDto createSpecies(@RequestBody String jsonBody) throws Exception {
        Species species = service.createSpecies(new JSONObject(jsonBody));
        return convertToDto(species);
    }

    @PostMapping(value = {"/municipality/new/"})
    public MunicipalityDto createMunicipality(@RequestBody String jsonBody) throws Exception {
        Municipality municipality = service.createMunicipality(new JSONObject(jsonBody));
        return convertToDto(municipality);
    }

    @PostMapping(value = {"/forecast/new/"})
    public ForecastDto createForecast(@RequestBody String jsonBody) throws Exception {
        Forecast forecast = service.createForecast(new JSONObject(jsonBody));
        return convertToDto(forecast);
    }


    // ==============================
    // PATCH MAPPING API
    // ==============================

    @PatchMapping(value = {"/tree/update/"})
    public TreeDto updateTree(@RequestBody String jsonBody) throws Exception {
        Tree tree = service.updateTree(new JSONObject(jsonBody));
        return convertToDto(tree);
    }

    @PatchMapping(value = {"/user/update/"})
    public UserDto updateUser(@RequestBody String jsonBody) throws Exception {
        User user = service.updateUser(new JSONObject(jsonBody));
        return convertToDto(user);
    }

    @PatchMapping(value = {"/user/update/password/"})
    public UserDto updateUserPassword(@RequestBody String jsonBody) throws Exception {
        User user = service.updateUserPassword(new JSONObject(jsonBody));
        return convertToDto(user);
    }

    @PatchMapping(value = {"/species/update/"})
    public SpeciesDto updateSpecies(@RequestBody String jsonBody) throws Exception {
        Species species = service.updateSpecies(new JSONObject(jsonBody));
        return convertToDto(species);
    }

    @PatchMapping(value = {"/municipality/update/"})
    public MunicipalityDto updateMunicipalityBorders(@RequestBody String jsonBody) throws Exception {
        Municipality municipality = service.updateMunicipalityBorders(new JSONObject(jsonBody));
        return convertToDto(municipality);
    }


    // ==============================
    // DELETE MAPPING API
    // ==============================

    @PostMapping(value = {"/tree/delete/"})
    public TreeDto deleteTree(@RequestBody String jsonBody) throws Exception {
        Tree tree = service.deleteTree(new JSONObject(jsonBody));
        return convertToDto(tree);
    }

    @PostMapping(value = {"/user/delete/"})
    public UserDto deleteUser(@RequestBody String jsonBody) throws Exception {
        User user = service.deleteUser(new JSONObject(jsonBody));
        return convertToDto(user);
    }

    @PostMapping(value = {"/species/delete/"})
    public SpeciesDto deleteSpecies(@RequestBody String jsonBody) throws Exception {
        Species species = service.deleteSpecies(new JSONObject(jsonBody));
        return convertToDto(species);
    }

    @PostMapping(value = {"/location/delete/"})
    public LocationDto deleteLocation(@RequestBody String jsonBody) throws Exception {
        Location location = service.deleteLocation(new JSONObject(jsonBody));
        return convertToDto(location);
    }

    @PostMapping(value = {"/municipality/delete/"})
    public MunicipalityDto deleteMunicipality(@RequestBody String jsonBody) throws Exception {
        Municipality municipality = service.deleteMunicipality(new JSONObject(jsonBody));
        return convertToDto(municipality);
    }

    @PostMapping(value = {"/forecast/delete/"})
    public ForecastDto deleteForecast(@RequestBody String jsonBody) throws Exception {
        Forecast forecast = service.deleteForecast(new JSONObject(jsonBody));
        return convertToDto(forecast);
    }

    @PostMapping(value = {"/reset/"})
    public void resetDatabase(@RequestBody String jsonBody) throws Exception {
        service.resetDatabase(new JSONObject(jsonBody));
    }

    @PostMapping(value = {"/delete/"})
    public void deleteDatabase(@RequestBody String jsonBody) throws Exception {
        service.deleteDatabase(new JSONObject(jsonBody));
    }
}