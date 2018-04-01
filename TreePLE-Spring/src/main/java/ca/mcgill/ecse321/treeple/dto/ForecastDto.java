package ca.mcgill.ecse321.treeple.dto;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class ForecastDto {

    private int forecastId;
    private Date fcDate;
    private String fcUser;
    private double stormwater;
    private double co2Reduced;
    private double biodiversity;
    private double energyConserved;
    private List<TreeDto> fcTrees;

    public ForecastDto() {
    }

    public ForecastDto(int forecastId, Date fcDate, String fcUser, double stormwater,
                       double co2Reduced, double biodiversity, double energyConserved,
                       ArrayList<TreeDto> fcTrees) {
        this.forecastId = forecastId;
        this.fcDate = fcDate;
        this.fcUser = fcUser;
        this.stormwater = stormwater;
        this.co2Reduced = co2Reduced;
        this.biodiversity = biodiversity;
        this.energyConserved = energyConserved;
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

    public List<TreeDto> getFcTrees() {
        return fcTrees;
    }

    public void setFcTrees(List<TreeDto> fcTrees) {
        this.fcTrees = fcTrees;
    }
}