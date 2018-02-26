package ca.mcgill.ecse321.treeple.dto;

public class SpeciesDto {

    private String name;
    private String species;
    private String genus;

    public SpeciesDto() {
    }

    public SpeciesDto(String name) {
        this(name, "", "");
    }

    public SpeciesDto(String name, String species, String genus) {
        this.name = name;
        this.species = species;
        this.genus = genus;
    }

    public String getName() {
        return name;
    }

    public String getSpecies() {
        return species;
    }

    public String getGenus() {
        return genus;
    }
}
