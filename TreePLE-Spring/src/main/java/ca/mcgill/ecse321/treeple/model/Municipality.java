/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse321.treeple.model;
import java.util.*;

// line 108 "../../../../../TreePLE.ump"
public class Municipality
{

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static Map<String, Municipality> municipalitysByName = new HashMap<String, Municipality>();

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Municipality Attributes
  private String name;
  private int totalTrees;

  //Municipality Associations
  private List<Location> borders;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Municipality(String aName, int aTotalTrees)
  {
    totalTrees = aTotalTrees;
    if (!setName(aName))
    {
      throw new RuntimeException("Cannot create due to duplicate name");
    }
    borders = new ArrayList<Location>();
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
      municipalitysByName.remove(anOldName);
    }
    municipalitysByName.put(aName, this);
    return wasSet;
  }

  public boolean setTotalTrees(int aTotalTrees)
  {
    boolean wasSet = false;
    totalTrees = aTotalTrees;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public static Municipality getWithName(String aName)
  {
    return municipalitysByName.get(aName);
  }

  public static boolean hasWithName(String aName)
  {
    return getWithName(aName) != null;
  }

  public int getTotalTrees()
  {
    return totalTrees;
  }

  public Location getBorder(int index)
  {
    Location aBorder = borders.get(index);
    return aBorder;
  }

  public List<Location> getBorders()
  {
    List<Location> newBorders = Collections.unmodifiableList(borders);
    return newBorders;
  }

  public int numberOfBorders()
  {
    int number = borders.size();
    return number;
  }

  public boolean hasBorders()
  {
    boolean has = borders.size() > 0;
    return has;
  }

  public int indexOfBorder(Location aBorder)
  {
    int index = borders.indexOf(aBorder);
    return index;
  }

  public static int minimumNumberOfBorders()
  {
    return 0;
  }

  public boolean addBorder(Location aBorder)
  {
    boolean wasAdded = false;
    if (borders.contains(aBorder)) { return false; }
    borders.add(aBorder);
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeBorder(Location aBorder)
  {
    boolean wasRemoved = false;
    if (borders.contains(aBorder))
    {
      borders.remove(aBorder);
      wasRemoved = true;
    }
    return wasRemoved;
  }

  public boolean addBorderAt(Location aBorder, int index)
  {  
    boolean wasAdded = false;
    if(addBorder(aBorder))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfBorders()) { index = numberOfBorders() - 1; }
      borders.remove(aBorder);
      borders.add(index, aBorder);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveBorderAt(Location aBorder, int index)
  {
    boolean wasAdded = false;
    if(borders.contains(aBorder))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfBorders()) { index = numberOfBorders() - 1; }
      borders.remove(aBorder);
      borders.add(index, aBorder);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addBorderAt(aBorder, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    municipalitysByName.remove(getName());
    borders.clear();
  }

  // line 115 "../../../../../TreePLE.ump"
   public static  boolean clearMunicipalities(){
    municipalitysByName.clear();
    return municipalitysByName.isEmpty();
  }


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "totalTrees" + ":" + getTotalTrees()+ "]";
  }
}