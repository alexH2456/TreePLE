package ca.mcgill.ecse321.treeple.dto;

import java.util.ArrayList;
import java.util.List;

public class MunicipalityDto {

    private String name;
    private int totalTrees;
    private List<LocationDto> borders;


    public MunicipalityDto() {
    }

    public MunicipalityDto(String name, int totalTrees) {
        this(name, totalTrees, new ArrayList<LocationDto>());
    }

    public MunicipalityDto(String name, int totalTrees, ArrayList<LocationDto> borders) {
        this.name = name;
        this.totalTrees = totalTrees;
        this.borders = borders;
    }

    public String getName() {
        return name;
    }

    public int getTotalTrees() {
        return totalTrees;
    }

    public List<LocationDto> getBorders() {
        return borders;
    }

    public void setBorders(List<LocationDto> borders) {
        this.borders = borders;
    }
}
