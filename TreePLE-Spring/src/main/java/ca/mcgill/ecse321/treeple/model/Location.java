/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse321.treeple.model;

// line 111 "../../../../../TreePLE.ump"
public class Location
{

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static int nextLocationId = 1;

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Location Attributes
  private double latitude;
  private double longitude;

  //Autounique Attributes
  private int locationId;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Location(double aLatitude, double aLongitude)
  {
    latitude = aLatitude;
    longitude = aLongitude;
    locationId = nextLocationId++;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setLatitude(double aLatitude)
  {
    boolean wasSet = false;
    latitude = aLatitude;
    wasSet = true;
    return wasSet;
  }

  public boolean setLongitude(double aLongitude)
  {
    boolean wasSet = false;
    longitude = aLongitude;
    wasSet = true;
    return wasSet;
  }

  public double getLatitude()
  {
    return latitude;
  }

  public double getLongitude()
  {
    return longitude;
  }

  public int getLocationId()
  {
    return locationId;
  }

  public void delete()
  {}

  // line 118 "../../../../../TreePLE.ump"
   public  Location(double aLatitude, double aLongitude, int aLocationId){
    latitude = aLatitude;
    longitude = aLongitude;
    locationId = aLocationId;
  }

  // line 124 "../../../../../TreePLE.ump"
   public static  boolean setNextLocationId(int minLocationId){
    boolean wasSet = false;
    nextLocationId = minLocationId;
    wasSet = true;
    return wasSet;
  }

  // line 131 "../../../../../TreePLE.ump"
   public static  int getNextLocationId(){
    return nextLocationId;
  }


  public String toString()
  {
    return super.toString() + "["+
            "locationId" + ":" + getLocationId()+ "," +
            "latitude" + ":" + getLatitude()+ "," +
            "longitude" + ":" + getLongitude()+ "]";
  }
}