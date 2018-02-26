package ca.mcgill.ecse321.treeple.dto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.ecse321.treeple.model.Tree.*;;

public class TreeDto {

    private int treeId;
    private int height;
    private int diameter;
    private String address;
    private Date datePlanted;
    private Land land;
    private Status status;
    private Ownership ownership;
    private SpeciesDto species;
    private LocationDto location;
    private MunicipalityDto municipality;
    private List<SurveyReportDto> reports;

    public TreeDto() {
    }

    public TreeDto(int treeId, int height, int diameter,
                   String address, Date datePlanted, Land land, Status status,
                   Ownership ownership, LocationDto location, ArrayList<SurveyReportDto> reports) {
        this(treeId, height, diameter, address, datePlanted, land, status, ownership, null, location, null, reports);
    }

    public TreeDto(int treeId, int height, int diameter, String address,
                   Date datePlanted, Land land, Status status, Ownership ownership, SpeciesDto species,
                   LocationDto location, MunicipalityDto municipality, ArrayList<SurveyReportDto> reports) {
        this.treeId = treeId;
        this.height = height;
        this.diameter = diameter;
        this.address = address;
        this.datePlanted = datePlanted;
        this.land = land;
        this.status = status;
        this.ownership = ownership;
        this.species = species;
        this.location = location;
        this.municipality = municipality;
        this.reports = reports;
    }

    public int getTreeId() {
        return treeId;
    }

    public int getHeight() {
        return height;
    }

    public int getDiameter() {
        return diameter;
    }

    public String getAddress() {
        return address;
    }

    public Date getDatePlanted() {
        return datePlanted;
    }

    public Land getLand() {
        return land;
    }

    public Status getStatus() {
        return status;
    }

    public Ownership getOwnership() {
        return ownership;
    }

    public SpeciesDto getSpecies() {
        return species;
    }

    public LocationDto getLocation() {
        return location;
    }

    public MunicipalityDto getMunicipality() {
        return municipality;
    }

    public List<SurveyReportDto> getReports() {
        return reports;
    }

    public void setReports(List<SurveyReportDto> reports) {
        this.reports = reports;
    }
}
