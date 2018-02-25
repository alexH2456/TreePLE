/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse321.treeple.model;
import java.sql.Date;
import java.util.*;

// line 3 "../../../../../TreePLE.ump"
public class Tree
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum Land { ParkLand, ResidentialLand, InstitutionalLand, MunicipalLand }
  public enum Status { Planted, Diseased, MarkedForCutdown, Cutdown }
  public enum Ownership { Private, Public }

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static int nextTreeId = 1;

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Tree Attributes
  private int height;
  private int diameter;
  private String address;
  private Date datePlanted;
  private Land land;
  private Status status;
  private Ownership ownership;

  //Autounique Attributes
  private int treeId;

  //Tree Associations
  private Species species;
  private Location location;
  private Municipality municipality;
  private List<SurveyReport> reports;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Tree(int aHeight, int aDiameter, String aAddress, Date aDatePlanted, Land aLand, Status aStatus, Ownership aOwnership, Species aSpecies, Location aLocation, Municipality aMunicipality)
  {
    height = aHeight;
    diameter = aDiameter;
    address = aAddress;
    datePlanted = aDatePlanted;
    land = aLand;
    status = aStatus;
    ownership = aOwnership;
    treeId = nextTreeId++;
    if (!setSpecies(aSpecies))
    {
      throw new RuntimeException("Unable to create Tree due to aSpecies");
    }
    if (!setLocation(aLocation))
    {
      throw new RuntimeException("Unable to create Tree due to aLocation");
    }
    if (!setMunicipality(aMunicipality))
    {
      throw new RuntimeException("Unable to create Tree due to aMunicipality");
    }
    reports = new ArrayList<SurveyReport>();
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setHeight(int aHeight)
  {
    boolean wasSet = false;
    height = aHeight;
    wasSet = true;
    return wasSet;
  }

  public boolean setDiameter(int aDiameter)
  {
    boolean wasSet = false;
    diameter = aDiameter;
    wasSet = true;
    return wasSet;
  }

  public boolean setAddress(String aAddress)
  {
    boolean wasSet = false;
    address = aAddress;
    wasSet = true;
    return wasSet;
  }

  public boolean setDatePlanted(Date aDatePlanted)
  {
    boolean wasSet = false;
    datePlanted = aDatePlanted;
    wasSet = true;
    return wasSet;
  }

  public boolean setLand(Land aLand)
  {
    boolean wasSet = false;
    land = aLand;
    wasSet = true;
    return wasSet;
  }

  public boolean setStatus(Status aStatus)
  {
    boolean wasSet = false;
    status = aStatus;
    wasSet = true;
    return wasSet;
  }

  public boolean setOwnership(Ownership aOwnership)
  {
    boolean wasSet = false;
    ownership = aOwnership;
    wasSet = true;
    return wasSet;
  }

  public int getHeight()
  {
    return height;
  }

  public int getDiameter()
  {
    return diameter;
  }

  public String getAddress()
  {
    return address;
  }

  public Date getDatePlanted()
  {
    return datePlanted;
  }

  public Land getLand()
  {
    return land;
  }

  public Status getStatus()
  {
    return status;
  }

  public Ownership getOwnership()
  {
    return ownership;
  }

  public int getTreeId()
  {
    return treeId;
  }

  public Species getSpecies()
  {
    return species;
  }

  public Location getLocation()
  {
    return location;
  }

  public Municipality getMunicipality()
  {
    return municipality;
  }

  public SurveyReport getReport(int index)
  {
    SurveyReport aReport = reports.get(index);
    return aReport;
  }

  public List<SurveyReport> getReports()
  {
    List<SurveyReport> newReports = Collections.unmodifiableList(reports);
    return newReports;
  }

  public int numberOfReports()
  {
    int number = reports.size();
    return number;
  }

  public boolean hasReports()
  {
    boolean has = reports.size() > 0;
    return has;
  }

  public int indexOfReport(SurveyReport aReport)
  {
    int index = reports.indexOf(aReport);
    return index;
  }

  public boolean setSpecies(Species aNewSpecies)
  {
    boolean wasSet = false;
    if (aNewSpecies != null)
    {
      species = aNewSpecies;
      wasSet = true;
    }
    return wasSet;
  }

  public boolean setLocation(Location aNewLocation)
  {
    boolean wasSet = false;
    if (aNewLocation != null)
    {
      location = aNewLocation;
      wasSet = true;
    }
    return wasSet;
  }

  public boolean setMunicipality(Municipality aNewMunicipality)
  {
    boolean wasSet = false;
    if (aNewMunicipality != null)
    {
      municipality = aNewMunicipality;
      wasSet = true;
    }
    return wasSet;
  }

  public static int minimumNumberOfReports()
  {
    return 0;
  }

  public boolean addReport(SurveyReport aReport)
  {
    boolean wasAdded = false;
    if (reports.contains(aReport)) { return false; }
    reports.add(aReport);
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeReport(SurveyReport aReport)
  {
    boolean wasRemoved = false;
    if (reports.contains(aReport))
    {
      reports.remove(aReport);
      wasRemoved = true;
    }
    return wasRemoved;
  }

  public boolean addReportAt(SurveyReport aReport, int index)
  {  
    boolean wasAdded = false;
    if(addReport(aReport))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfReports()) { index = numberOfReports() - 1; }
      reports.remove(aReport);
      reports.add(index, aReport);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveReportAt(SurveyReport aReport, int index)
  {
    boolean wasAdded = false;
    if(reports.contains(aReport))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfReports()) { index = numberOfReports() - 1; }
      reports.remove(aReport);
      reports.add(index, aReport);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addReportAt(aReport, index);
    }
    return wasAdded;
  }

  public void delete()
  {
    species = null;
    location = null;
    municipality = null;
    reports.clear();
  }

  // line 40 "../../../../../TreePLE.ump"
   public  Tree(int aHeight, int aDiameter, String aAddress, Date aDatePlanted, Land aLand, Status aStatus, Ownership aOwnership, Species aSpecies, Location aLocation, Municipality aMunicipality, int aTreeId, ArrayList<SurveyReport> aSurveyReports){
    height = aHeight;
    diameter = aDiameter;
    address = aAddress;
    datePlanted = aDatePlanted;
    land = aLand;
    status = aStatus;
    ownership = aOwnership;
    treeId = aTreeId;
    reports = aSurveyReports;
    if (!setSpecies(aSpecies)) {
    throw new RuntimeException("Unable to create Tree due to aSpecies");
    }
    if (!setLocation(aLocation)) {
    throw new RuntimeException("Unable to create Tree due to aLocation");
    }
    if (!setMunicipality(aMunicipality)) {
    throw new RuntimeException("Unable to create Tree due to aMunicipality");
    }
  }

  // line 61 "../../../../../TreePLE.ump"
   public static  boolean setNextTreeId(int minTreeId){
    boolean wasSet = false;
    nextTreeId = minTreeId;
    wasSet = true;
    return wasSet;
  }


  public String toString()
  {
    return super.toString() + "["+
            "treeId" + ":" + getTreeId()+ "," +
            "height" + ":" + getHeight()+ "," +
            "diameter" + ":" + getDiameter()+ "," +
            "address" + ":" + getAddress()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "datePlanted" + "=" + (getDatePlanted() != null ? !getDatePlanted().equals(this)  ? getDatePlanted().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "land" + "=" + (getLand() != null ? !getLand().equals(this)  ? getLand().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "status" + "=" + (getStatus() != null ? !getStatus().equals(this)  ? getStatus().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "ownership" + "=" + (getOwnership() != null ? !getOwnership().equals(this)  ? getOwnership().toString().replaceAll("  ","    ") : "this" : "null") + System.getProperties().getProperty("line.separator") +
            "  " + "species = "+(getSpecies()!=null?Integer.toHexString(System.identityHashCode(getSpecies())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "location = "+(getLocation()!=null?Integer.toHexString(System.identityHashCode(getLocation())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "municipality = "+(getMunicipality()!=null?Integer.toHexString(System.identityHashCode(getMunicipality())):"null");
  }
}