package ca.mcgill.ecse321.treeple.dto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.ecse321.treeple.model.Tree;

public class ForecastDto {

    private int forecastId;
    private Date fcDate;
    private String fcUser;
    private double stormwater;
    private double co2Reduced;
    private double biodiversity;
    private double energyConserved;
    private String fcMunicipality;
    private List<Tree> fcTrees;

    public ForecastDto() {
    }

    public ForecastDto(int forecastId, Date fcDate, String fcUser, double stormwater,
                       double co2Reduced, double biodiversity, double energyConserved,
                       ArrayList<Tree> fcTrees) {
        this(forecastId, fcDate, fcUser, stormwater, co2Reduced, biodiversity, energyConserved, "", fcTrees);
    }

    public ForecastDto(int forecastId, Date fcDate, String fcUser, double stormwater,
                       double co2Reduced, double biodiversity, double energyConserved,
                       String fcMunicipality, ArrayList<Tree> fcTrees) {
        this.forecastId = forecastId;
        this.fcDate = fcDate;
        this.fcUser = fcUser;
        this.stormwater = stormwater;
        this.co2Reduced = co2Reduced;
        this.biodiversity = biodiversity;
        this.energyConserved = energyConserved;
        this.fcMunicipality = fcMunicipality;
        this.fcTrees = fcTrees;
    }

    public int getForecastId() {
        return forecastId;
    }

    public Date getFcDate() {
        return fcDate;
    }

    public String getFcUser() {
        return fcUser;
    }

    public double getStormwater() {
        return stormwater;
    }

    public double getCo2Reduced() {
        return co2Reduced;
    }

    public double getBiodiversity() {
        return biodiversity;
    }

    public double getEnergyConserved() {
        return energyConserved;
    }

    public String getFcMunicipality() {
        return fcMunicipality;
    }

    public List<Tree> getFcTrees() {
        return fcTrees;
    }

    public void setFcTrees(List<Tree> fcTrees) {
        this.fcTrees = fcTrees;
    }
}
