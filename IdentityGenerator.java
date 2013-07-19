package chrriis;


import java.util.ArrayList;

public class IdentityGenerator {
  ArrayList<Integer> idList = new ArrayList<Integer>();
  ArrayList<Integer> avList = new ArrayList<Integer>();
  int incrementID=0;
 
  public IdentityGenerator(){
	  avList.add(0);//available list will be initialized to have an available index at its first index
  }
    
  public boolean avListWillBeEmpty(){
	  if(avList.size()<2)
		  return true;
	  else 
		  return false;
  }
  
  public int requestId(){
	  int availableID=avList.get(0);
	  
	  if(avListWillBeEmpty()){
		incrementID = incrementID+1;
	    avList.add(incrementID); //an increment of the last available value will be added
	  }
	  System.out.println("adding "+ incrementID + "because its gonna be soon to be empty");
	  
	  idList.add(availableID);//store the soon to be used available id into the used id list
	  avList.remove(avList.get(0));//remove the soon to be used available id from the available list
	  return availableID;//returns id of the first available index
  }
  
  public void recycleId(int id){
	  avList.add(id); //add the soon to be available id to the available list
	  idList.remove(idList.indexOf(id));//remove the soon to be available id from the used id list
  }
}
