package ca.mcgill.ecse321.treeple.controller;

import java.util.*;

import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import ca.mcgill.ecse321.treeple.dto.*;
import ca.mcgill.ecse321.treeple.model.*;
import ca.mcgill.ecse321.treeple.service.TreePLEService;
import ca.mcgill.ecse321.treeple.service.InvalidInputException;

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
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return new TreeDto(tree.getTreeId(), tree.getHeight(), tree.getDiameter(), tree.getAddress(),
                           tree.getDatePlanted(), tree.getLand(), tree.getStatus(), tree.getOwnership(),
                           convertToDto(tree.getSpecies()), convertToDto(tree.getLocation()),
                           convertToDto(tree.getMunicipality()), createSurveyReportDtos(tree.getReports()));
    }

    private UserDto convertToDto(User user) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(user, UserDto.class);
    }

    private SpeciesDto convertToDto(Species species) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(species, SpeciesDto.class);
    }

    private LocationDto convertToDto(Location location) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(location, LocationDto.class);
    }

    private MunicipalityDto convertToDto(Municipality municipality) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return new MunicipalityDto(municipality.getName(), municipality.getTotalTrees(),
                                   createLocationDtos(municipality.getBorders()));
    }

    private SurveyReportDto convertToDto(SurveyReport report) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
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



    // @GetMapping(value = {"/trees/{treeId}/"})
    // public TreeDto getTreeById(@PathVariable("treeId") int treeId) throws InvalidInputException {
    //     Tree tree = service.getTreeById(treeId);
    //     return convertToDto(tree);
    // }


    // @GetMapping(value = {"/locations/"})
    // public List<LocationDto> findAllLocations() {
    //     List<LocationDto> locations = new ArrayList<LocationDto>();
    //     for (Location location : service.findAllLocations())
    //         locations.add(convertToDto(location));
    //     return locations;
    // }

    // @GetMapping(value = {"/locations/{id}", "/locations/{id}/"})
    // public LocationDto getLocationById(@PathVariable("id") String id) throws InvalidInputException {
    //     Location location = service.getLocationById(id);
    //     return convertToDto(location);
    // }


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
    	System.out.println(jsonBody);
        User user = service.createUser(new JSONObject(jsonBody));
        System.out.println(user.getUsername());
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

    // @PostMapping(value = {"/locations/{id}", "/locations/{id}/"})
    // public LocationDto createLocation(@PathVariable("id") String id,
    //                                      @RequestParam String name,
    //                                      @RequestParam String strtNum,
    //                                   @RequestParam String address,
    //                                   @RequestParam(value="qTime", defaultValue="-1") int qTime
    //                                      ) throws InvalidInputException {
    //     Location location = service.createLocation(id, name, strtNum, address, qTime, emptyJSON());
    //     return convertToDto(location);
    // }

    // @PatchMapping(value = {"/users/points/{username}", "/users/points/{username}/"})
    // public UserDto updateUserPoints(@PathVariable("username") String username,
    //                                 @RequestParam int points
    //                                 ) throws InvalidInputException {
    //     User user = service.getUserByName(username);
    //     user = service.updateUserPoints(user, points);
    //     return convertToDto(user);
    // }

    // @PatchMapping(value = {"/users/pass/{username}", "/users/pass/{username}/"})
    // public UserDto updateUserPassword(@PathVariable("username") String username,
    //                                    @RequestParam String password
    //                                    ) throws InvalidInputException {
    //     User user = service.getUserByName(username);
    //     user = service.updateUserPassword(user, password);
    //     return convertToDto(user);
    // }

    // @PatchMapping(value = {"/locations/checkIn/{id}", "/locations/checkIn/{id}/"})
    // public LocationDto updateLocationCheckIn(@PathVariable("id") String id,
    //                                          @RequestParam String username,
    //                                          @RequestParam String checkIn
    //                                          ) throws InvalidInputException {
    //     Location location = service.updateLocationCheckIn(id, username, checkIn);
    //     return convertToDto(location);
    // }

    // @PatchMapping(value = {"/locations/checkOut/{id}", "/locations/checkOut/{id}/"})
    // public LocationDto updateLocationCheckOut(@PathVariable("id") String id,
    //                                           @RequestParam String username,
    //                                           @RequestParam String checkOut
    //                                           ) throws InvalidInputException {
    //     Location location = service.updateLocationCheckOut(id, username, checkOut);
    //     return convertToDto(location);
    // }

    // ==============================
    // DELETE MAPPING API
    // ==============================

    @PostMapping(value = {"/deletetree/"})
    public TreeDto deleteTree(@RequestBody String jsonBody) throws Exception {
        Tree tree = service.deleteTree(new JSONObject(jsonBody));
        return convertToDto(tree);
    }

    @DeleteMapping(value = {"/reset/"})
    public void resetDatabase() {
        service.resetDatabase();
    }
    
    @DeleteMapping(value = {"/deleteuser/{username}/"})
    public void deleteUser(@PathVariable("username") String username) throws Exception {
    	service.deleteUser(username);
    }
}
