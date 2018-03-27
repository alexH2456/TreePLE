/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse321.treeple.model;
import java.util.*;

// line 91 "../../../../../TreePLE.ump"
public class Species
{

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static Map<String, Species> speciessByName = new HashMap<String, Species>();

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Species Attributes
  private String name;
  private String species;
  private String genus;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Species(String aName, String aSpecies, String aGenus)
  {
    species = aSpecies;
    genus = aGenus;
    if (!setName(aName))
    {
      throw new RuntimeException("Cannot create due to duplicate name");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    String anOldName = getName();
    if (hasWithName(aName)) {
      return wasSet;
    }
    name = aName;
    wasSet = true;
    if (anOldName != null) {
      speciessByName.remove(anOldName);
    }
    speciessByName.put(aName, this);
    return wasSet;
  }

  public boolean setSpecies(String aSpecies)
  {
    boolean wasSet = false;
    species = aSpecies;
    wasSet = true;
    return wasSet;
  }

  public boolean setGenus(String aGenus)
  {
    boolean wasSet = false;
    genus = aGenus;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public static Species getWithName(String aName)
  {
    return speciessByName.get(aName);
  }

  public static boolean hasWithName(String aName)
  {
    return getWithName(aName) != null;
  }

  public String getSpecies()
  {
    return species;
  }

  public String getGenus()
  {
    return genus;
  }

  public void delete()
  {
    speciessByName.remove(getName());
  }

  // line 98 "../../../../../TreePLE.ump"
   public static  boolean clearSpecies(){
    speciessByName.clear();
    return speciessByName.isEmpty();
  }


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "species" + ":" + getSpecies()+ "," +
            "genus" + ":" + getGenus()+ "]";
  }
}