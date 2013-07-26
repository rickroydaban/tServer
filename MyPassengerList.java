package chrriis;

import java.util.ArrayList;
import java.util.List;

public class MyPassengerList {
  List<MyPassengerNode> myPassengerList;
  
  public MyPassengerList(){
    myPassengerList = new ArrayList<MyPassengerNode>();
  }

  public void addNode(MyPassengerNode pPassengerNode){
	myPassengerList.set(pPassengerNode.id, pPassengerNode);
  }
    
  public void removeNode(int id){
    myPassengerList.remove(id);
  }
}
