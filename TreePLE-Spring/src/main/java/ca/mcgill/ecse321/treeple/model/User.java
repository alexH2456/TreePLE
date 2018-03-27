/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse321.treeple.model;
import java.util.*;

// line 72 "../../../../../TreePLE.ump"
public class User
{

  //------------------------
  // ENUMERATIONS
  //------------------------

  public enum UserRole { Resident, Scientist }

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static Map<String, User> usersByUsername = new HashMap<String, User>();

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //User Attributes
  private String username;
  private String password;
  private UserRole role;
  private List<String> myAddresses;
  private List<Integer> myTrees;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public User(String aUsername, String aPassword, UserRole aRole)
  {
    password = aPassword;
    role = aRole;
    myAddresses = new ArrayList<String>();
    myTrees = new ArrayList<Integer>();
    if (!setUsername(aUsername))
    {
      throw new RuntimeException("Cannot create due to duplicate username");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setUsername(String aUsername)
  {
    boolean wasSet = false;
    String anOldUsername = getUsername();
    if (hasWithUsername(aUsername)) {
      return wasSet;
    }
    username = aUsername;
    wasSet = true;
    if (anOldUsername != null) {
      usersByUsername.remove(anOldUsername);
    }
    usersByUsername.put(aUsername, this);
    return wasSet;
  }

  public boolean setPassword(String aPassword)
  {
    boolean wasSet = false;
    password = aPassword;
    wasSet = true;
    return wasSet;
  }

  public boolean setRole(UserRole aRole)
  {
    boolean wasSet = false;
    role = aRole;
    wasSet = true;
    return wasSet;
  }

  public boolean addMyAddress(String aMyAddress)
  {
    boolean wasAdded = false;
    wasAdded = myAddresses.add(aMyAddress);
    return wasAdded;
  }

  public boolean removeMyAddress(String aMyAddress)
  {
    boolean wasRemoved = false;
    wasRemoved = myAddresses.remove(aMyAddress);
    return wasRemoved;
  }

  public boolean addMyTree(Integer aMyTree)
  {
    boolean wasAdded = false;
    wasAdded = myTrees.add(aMyTree);
    return wasAdded;
  }

  public boolean removeMyTree(Integer aMyTree)
  {
    boolean wasRemoved = false;
    wasRemoved = myTrees.remove(aMyTree);
    return wasRemoved;
  }

  public String getUsername()
  {
    return username;
  }

  public static User getWithUsername(String aUsername)
  {
    return usersByUsername.get(aUsername);
  }

  public static boolean hasWithUsername(String aUsername)
  {
    return getWithUsername(aUsername) != null;
  }

  public String getPassword()
  {
    return password;
  }

  public UserRole getRole()
  {
    return role;
  }

  public String getMyAddress(int index)
  {
    String aMyAddress = myAddresses.get(index);
    return aMyAddress;
  }

  public String[] getMyAddresses()
  {
    String[] newMyAddresses = myAddresses.toArray(new String[myAddresses.size()]);
    return newMyAddresses;
  }

  public int numberOfMyAddresses()
  {
    int number = myAddresses.size();
    return number;
  }

  public boolean hasMyAddresses()
  {
    boolean has = myAddresses.size() > 0;
    return has;
  }

  public int indexOfMyAddress(String aMyAddress)
  {
    int index = myAddresses.indexOf(aMyAddress);
    return index;
  }

  public Integer getMyTree(int index)
  {
    Integer aMyTree = myTrees.get(index);
    return aMyTree;
  }

  public Integer[] getMyTrees()
  {
    Integer[] newMyTrees = myTrees.toArray(new Integer[myTrees.size()]);
    return newMyTrees;
  }

  public int numberOfMyTrees()
  {
    int number = myTrees.size();
    return number;
  }

  public boolean hasMyTrees()
  {
    boolean has = myTrees.size() > 0;
    return has;
  }

  public int indexOfMyTree(Integer aMyTree)
  {
    int index = myTrees.indexOf(aMyTree);
    return index;
  }

  public void delete()
  {
    usersByUsername.remove(getUsername());
  }

  // line 86 "../../../../../TreePLE.ump"
   public static  boolean clearUsers(){
    usersByUsername.clear();
    return usersByUsername.isEmpty();
  }


  public String toString()
  {
    return super.toString() + "["+
            "username" + ":" + getUsername()+ "," +
            "password" + ":" + getPassword()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "role" + "=" + (getRole() != null ? !getRole().equals(this)  ? getRole().toString().replaceAll("  ","    ") : "this" : "null");
  }
}