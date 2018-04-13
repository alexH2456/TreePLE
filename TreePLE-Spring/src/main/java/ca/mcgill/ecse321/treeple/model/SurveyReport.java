/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse321.treeple.model;
import java.sql.Date;

// line 155 "../../../../../TreePLE.ump"
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
  private String reportUser;

  //Autounique Attributes
  private int reportId;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public SurveyReport(Date aReportDate, String aReportUser)
  {
    reportDate = aReportDate;
    reportUser = aReportUser;
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

  public boolean setReportUser(String aReportUser)
  {
    boolean wasSet = false;
    reportUser = aReportUser;
    wasSet = true;
    return wasSet;
  }

  public Date getReportDate()
  {
    return reportDate;
  }

  public String getReportUser()
  {
    return reportUser;
  }

  public int getReportId()
  {
    return reportId;
  }

  public void delete()
  {}

  // line 162 "../../../../../TreePLE.ump"
   public  SurveyReport(Date aReportDate, String aReportUser, int aReportId){
    reportDate = aReportDate;
    reportUser = aReportUser;
    reportId = aReportId;
  }

  // line 168 "../../../../../TreePLE.ump"
   public static  boolean setNextReportId(int minReportId){
    boolean wasSet = false;
    nextReportId = minReportId;
    wasSet = true;
    return wasSet;
  }

  // line 175 "../../../../../TreePLE.ump"
   public static  int getNextReportId(){
    return nextReportId;
  }


  public String toString()
  {
    return super.toString() + "["+
            "reportId" + ":" + getReportId()+ "," +
            "reportUser" + ":" + getReportUser()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "reportDate" + "=" + (getReportDate() != null ? !getReportDate().equals(this)  ? getReportDate().toString().replaceAll("  ","    ") : "this" : "null");
  }
}