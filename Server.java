package chrriis;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.SwingUtilities;

import actors.*;

public class Server implements Runnable{
  public static ServerSocket serverSocket;
  public static IdentityGenerator taxiIdentificator; //global scope for id generator for taxi
  public IdentityGenerator passengerIdentificator; //global scope for id generator for passengers
//  private Database database;
    
  @Override
  public void run(){
    try{
      passengerIdentificator = new IdentityGenerator();
      taxiIdentificator=new IdentityGenerator();
      serverSocket = new ServerSocket(ApplicationAdapter.SERVERPORT);
      
      //Initialize database
//      database = new Database();
      SimpleWebBrowserExample.statusField.setText("LISTENING TO SERVERPORT: " + ApplicationAdapter.SERVERPORT);
	}catch (IOException e) {
      System.out.println("FAILED TO LISTEN TO SERVERPORT...");
      e.printStackTrace();
    }
    
    //While server socket is not closed
	while(!serverSocket.isClosed()){
      try{
    	//updates listener for units on operation
    	System.out.println("Status: System is now ready for incoming connection requests.");
    	Socket clientSocket = serverSocket.accept(); //after this line, the system can detect incoming connections
    	new Thread(new ClientThread(clientSocket)).start(); //one thread is allocated for every taxi on process
      }catch (IOException e) {
        System.out.println(e.getCause());
	  }
	}  
	//process continues if the server has been established then generate a server-client connection	
	SimpleWebBrowserExample.statusField.setText("SERVER IS STOPPED!");
  }	
  
  //NOTE: client is a general term as us CLIENT-SERVER connections NOT CLIENT,TAXI,SERVER..
  private class ClientThread implements Runnable {		
    private Socket clientSocket;
		
    public ClientThread(Socket pClientSocket) {
      clientSocket = pClientSocket;
    }
		
    @Override
    public void run(){
      try {
    	//manages objects sent by the client connections
    	ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
    	ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
        
        try{
          Object inputObject = input.readObject(); //must be stored the value here
          
          if(inputObject instanceof MyPassenger){
        	//links to the object data sent by the client connection
            MyPassenger sp = (MyPassenger) inputObject;
            
            //NOTE: No need to create another MyPassenger since the data is already stored in sp
            /*MyPassenger serverPassenger = new MyPassenger(passengerIdentificator.requestId(),
        		                                          sp.getSrcLat(),sp.getSrcLng(),
						                                  sp.getDesLat(),sp.getDesLng(),
						                                  sp.getClientName());*/
            
            sp.setID(passengerIdentificator.requestId());
            
            //NOTE: No need for client IP since we will use the "output" variable to send data back to client 
            //sp.setIp(clientSocket.getInetAddress().toString());
            
            int clientCount = Integer.parseInt(SimpleWebBrowserExample.clientCountField.getText());
            SimpleWebBrowserExample.clientCountField.setText(String.valueOf(++clientCount));
            
            //Find the nearest taxi and send its information to the passenger
//            MyTaxi nearestTaxi = database.calculateShortestTaxiDistance(sp);
//			output.writeObject(nearestTaxi);
//			
//			//Send passenger data to taxi
//			Socket taxiSocket = new Socket(nearestTaxi.ip, ApplicationAdapter.TAXIPORT);
//			ObjectOutputStream taxiOutput = new ObjectOutputStream(taxiSocket.getOutputStream());
//			taxiOutput.writeObject(sp);
//			taxiOutput.close();
//			taxiSocket.close();
          }
          
          else{
        	//links to the object data sent by the client(taxi) connection
            MyTaxi myTaxi = (MyTaxi) inputObject;
            SimpleWebBrowserExample.taxiLat = myTaxi.taxiLat;
            SimpleWebBrowserExample.taxiLng  = myTaxi.taxiLng;
            
            int taxiCount = Integer.parseInt(SimpleWebBrowserExample.taxiCountField.getText());
          	SimpleWebBrowserExample.taxiCountField.setText(String.valueOf(++taxiCount));
          	
          	SwingUtilities.invokeAndWait(new Runnable(){
                public void run() {
    		      SimpleWebBrowserExample.webBrowser.navigate("http://localhost/thesis/multiplemarkers.php?fname=addMarker&arg1="+SimpleWebBrowserExample.taxiLat+"&arg2="+SimpleWebBrowserExample.taxiLng);
                }
          	});
//            	output.writeObject(myTaxi);
//            }
            
//            	database.updateTaxiData(myTaxi);
            	
            //NOTE: No need since the data is already stored in the "st" variable
            /*MyTaxi serverTaxi = new MyTaxi(taxiIdentificator.requestId(),
          		                           st.curLat, st.curLng, 
          		                           st.plateNo, st.bodyNo, 
          		                           st.driverName);
            	serverTaxi.setIp(clientSocket.getInetAddress().toString()); //get the ip of the requesting passenger*/
          }
		    input.close();
            output.close();
        }catch (ClassNotFoundException e) {
				  System.out.println("class not found exception"+e.getMessage());
//		} catch (SQLException e) {
//			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }catch (IOException e) {
        System.out.println("io exception: "+e.getMessage());
	  }finally{
		  try{
			  if(clientSocket!= null)
				clientSocket.close();
          } catch (IOException e) {
  		    System.out.println(e.getMessage());
        }
      }
	}
  }
}