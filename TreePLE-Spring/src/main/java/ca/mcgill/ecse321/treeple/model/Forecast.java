/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse321.treeple.model;
import java.sql.Date;
import java.util.*;

// line 179 "../../../../../TreePLE.ump"
public class Forecast
{

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static int nextForecastId = 1;

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Forecast Attributes
  private Date fcDate;
  private String fcUser;
  private double stormwater;
  private double co2Reduced;
  private double biodiversity;
  private double energyConserved;

  //Autounique Attributes
  private int forecastId;

  //Forecast Associations
  private List<Tree> fcTrees;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Forecast(Date aFcDate, String aFcUser, double aStormwater, double aCo2Reduced, double aBiodiversity, double aEnergyConserved)
  {
    fcDate = aFcDate;
    fcUser = aFcUser;
    stormwater = aStormwater;
    co2Reduced = aCo2Reduced;
    biodiversity = aBiodiversity;
    energyConserved = aEnergyConserved;
    forecastId = nextForecastId++;
    fcTrees = new ArrayList<Tree>();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setFcDate(Date aFcDate)
  {
    boolean wasSet = false;
    fcDate = aFcDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setFcUser(String aFcUser)
  {
    boolean wasSet = false;
    fcUser = aFcUser;
    wasSet = true;
    return wasSet;
  }

  public boolean setStormwater(double aStormwater)
  {
    boolean wasSet = false;
    stormwater = aStormwater;
    wasSet = true;
    return wasSet;
  }

  public boolean setCo2Reduced(double aCo2Reduced)
  {
    boolean wasSet = false;
    co2Reduced = aCo2Reduced;
    wasSet = true;
    return wasSet;
  }

  public boolean setBiodiversity(double aBiodiversity)
  {
    boolean wasSet = false;
    biodiversity = aBiodiversity;
    wasSet = true;
    return wasSet;
  }

  public boolean setEnergyConserved(double aEnergyConserved)
  {
    boolean wasSet = false;
    energyConserved = aEnergyConserved;
    wasSet = true;
    return wasSet;
  }

  public Date getFcDate()
  {
    return fcDate;
  }

  public String getFcUser()
  {
    return fcUser;
  }

  public double getStormwater()
  {
    return stormwater;
  }

  public double getCo2Reduced()
  {
    return co2Reduced;
  }

  public double getBiodiversity()
  {
    return biodiversity;
  }

  public double getEnergyConserved()
  {
    return energyConserved;
  }

  public int getForecastId()
  {
    return forecastId;
  }

  public Tree getFcTree(int index)
  {
    Tree aFcTree = fcTrees.get(index);
    return aFcTree;
  }

  public List<Tree> getFcTrees()
  {
    List<Tree> newFcTrees = Collections.unmodifiableList(fcTrees);
    return newFcTrees;
  }

  public int numberOfFcTrees()
  {
    int number = fcTrees.size();
    return number;
  }

  public boolean hasFcTrees()
  {
    boolean has = fcTrees.size() > 0;
    return has;
  }

  public int indexOfFcTree(Tree aFcTree)
  {
    int index = fcTrees.indexOf(aFcTree);
    return index;
  }

  public static int minimumNumberOfFcTrees()
  {
    return 0;
  }

  public boolean addFcTree(Tree aFcTree)
  {
    boolean wasAdded = false;
    if (fcTrees.contains(aFcTree)) { return false; }
    fcTrees.add(aFcTree);
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeFcTree(Tree aFcTree)
  {
    boolean wasRemoved = false;
    if (fcTrees.contains(aFcTree))
    {
      fcTrees.remove(aFcTree);
      wasRemoved = true;
    }
    return wasRemoved;
  }

  public boolean addFcTreeAt(Tree aFcTree, int index)
  {  
    boolean wasAdded = false;
    if(addFcTree(aFcTree))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfFcTrees()) { index = numberOfFcTrees() - 1; }
      fcTrees.remove(aFcTree);
      fcTrees.add(index, aFcTree);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveFcTreeAt(Tree aFcTree, int index)
  {
    boolean wasAdded = false;
    if(fcTrees.contains(aFcTree))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfFcTrees()) { index = numberOfFcTrees() - 1; }
      fcTrees.remove(aFcTree);
      fcTrees.add(index, aFcTree);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addFcTreeAt(aFcTree, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    fcTrees.clear();
  }

  // line 192 "../../../../../TreePLE.ump"
   public  Forecast(Date aFcDate, String aFcUser, double aStormwater, double aCo2Reduced, double aBiodiversity, double aEnergyConserved, int aForecastId, ArrayList<Tree> aFcTrees){
    fcDate = aFcDate;
    fcUser = aFcUser;
    stormwater = aStormwater;
    co2Reduced = aCo2Reduced;
    biodiversity = aBiodiversity;
    energyConserved = aEnergyConserved;
    forecastId = aForecastId;
    fcTrees = aFcTrees;
  }

  // line 203 "../../../../../TreePLE.ump"
   public static  boolean setNextForecastId(int minForecastId){
    boolean wasSet = false;
    nextForecastId = minForecastId;
    wasSet = true;
    return wasSet;
  }

  // line 210 "../../../../../TreePLE.ump"
   public static  int getNextForecastId(){
    return nextForecastId;
  }


  public String toString()
  {
    return super.toString() + "["+
            "forecastId" + ":" + getForecastId()+ "," +
            "fcUser" + ":" + getFcUser()+ "," +
            "stormwater" + ":" + getStormwater()+ "," +
            "co2Reduced" + ":" + getCo2Reduced()+ "," +
            "biodiversity" + ":" + getBiodiversity()+ "," +
            "energyConserved" + ":" + getEnergyConserved()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "fcDate" + "=" + (getFcDate() != null ? !getFcDate().equals(this)  ? getFcDate().toString().replaceAll("  ","    ") : "this" : "null");
  }
}