/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.27.0.3728.d139ed893 modeling language!*/

package ca.mcgill.ecse321.treeple.model;

// line 148 "../../../../../TreePLE.ump"
public class SustainabilityCalculator
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //SustainabilityCalculator Associations
  private Tree tree;
  private Municipality municipality;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public SustainabilityCalculator(Tree aTree, Municipality aMunicipality)
  {
    if (!setTree(aTree))
    {
      throw new RuntimeException("Unable to create SustainabilityCalculator due to aTree");
    }
    if (!setMunicipality(aMunicipality))
    {
      throw new RuntimeException("Unable to create SustainabilityCalculator due to aMunicipality");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public Tree getTree()
  {
    return tree;
  }

  public Municipality getMunicipality()
  {
    return municipality;
  }

  public boolean setTree(Tree aNewTree)
  {
    boolean wasSet = false;
    if (aNewTree != null)
    {
      tree = aNewTree;
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

  public void delete()
  {
    tree = null;
    municipality = null;
  }

  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------

  // line 152 "../../../../../TreePLE.ump"
  // int getShadeSize(this.tree) ;
  // line 153 "../../../../../TreePLE.ump"
  // int getCO2Reduced(this.tree) ;
  // line 154 "../../../../../TreePLE.ump"
  // int getEnergyConserved(this.tree) ;
  // line 155 "../../../../../TreePLE.ump"
  // int getStormWaterIntercepted(this.tree) ;
  // line 156 "../../../../../TreePLE.ump"
  // int getBioDiversityIndex(this.tree) ;


}