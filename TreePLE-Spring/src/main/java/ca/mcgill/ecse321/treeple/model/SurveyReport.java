/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse321.treeple.model;
import java.sql.Date;

// line 120 "../../../../../TreePLE.ump"
public class SurveyReport
{

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static int nextReportId = 1;

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //SurveyReport Attributes
  private Date reportDate;
  private String reportingUser;

  //Autounique Attributes
  private int reportId;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public SurveyReport(Date aReportDate, String aReportingUser)
  {
    reportDate = aReportDate;
    reportingUser = aReportingUser;
    reportId = nextReportId++;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setReportDate(Date aReportDate)
  {
    boolean wasSet = false;
    reportDate = aReportDate;
    wasSet = true;
    return wasSet;
  }

  public boolean setReportingUser(String aReportingUser)
  {
    boolean wasSet = false;
    reportingUser = aReportingUser;
    wasSet = true;
    return wasSet;
  }

  public Date getReportDate()
  {
    return reportDate;
  }

  public String getReportingUser()
  {
    return reportingUser;
  }

  public int getReportId()
  {
    return reportId;
  }

  public void delete()
  {}

  // line 127 "../../../../../TreePLE.ump"
   public  SurveyReport(Date aReportDate, String aReportingUser, int aReportId){
    reportDate = aReportDate;
    reportingUser = aReportingUser;
    reportId = aReportId;
  }

  // line 133 "../../../../../TreePLE.ump"
   public static  boolean setNextReportId(int minReportId){
    boolean wasSet = false;
    nextReportId = minReportId;
    wasSet = true;
    return wasSet;
  }

  // line 140 "../../../../../TreePLE.ump"
   public static  int getNextReportId(){
    return nextReportId;
  }


  public String toString()
  {
    return super.toString() + "["+
            "reportId" + ":" + getReportId()+ "," +
            "reportingUser" + ":" + getReportingUser()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "reportDate" + "=" + (getReportDate() != null ? !getReportDate().equals(this)  ? getReportDate().toString().replaceAll("  ","    ") : "this" : "null");
  }
}