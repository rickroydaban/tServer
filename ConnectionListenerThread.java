package chrriis;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Set;

import javax.swing.SwingUtilities;

import actors.MyPassenger;
import actors.MyTaxi;


class ConnectionListenerThread implements Runnable {		
    private Socket clientSocket;
		
    public ConnectionListenerThread(Socket pClientSocket) {
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
            MyPassenger passenger = (MyPassenger) inputObject;
            
            //NOTE: No need for client IP since we will use the "output" variable to send data back to client 
            //sp.setIp(clientSocket.getInetAddress().toString());
            
            int clientCount = Integer.parseInt(SimpleWebBrowserExample.clientCountField.getText());
            SimpleWebBrowserExample.clientCountField.setText(String.valueOf(++clientCount));
            
            //Find the nearest taxi and send its information to the passenger
            double shortestDistance = 0, //the shortest distance record within the loop
            	   curDistance = 0;      //the distance estimated by comparing the current taxi's location to the client's source location
            
            String nearestTaxiKey = ""; //the key of the resulting nearest taxi estimation
            
            Set<String> keys = ServerThread.taxiList.keySet(); //get all the keys of the taxi record
            
            //loop to find the nearest taxi
            for(String curKey:keys){
              MyTaxi curTaxi = ServerThread.taxiList.get(curKey);
              if(shortestDistance==0){
                shortestDistance= distance( passenger.getSrcLat(),passenger.getSrcLng(), //calculate the distance of the passenger to the current taxi's location
                						    curTaxi.getCurLat(),curTaxi.getCurLng());
              }else{
                curDistance = distance( passenger.getSrcLat(),passenger.getSrcLng(),
					                    curTaxi.getCurLat(),curTaxi.getCurLng());
              }
              
              if(curDistance==0 || curDistance<shortestDistance)
              	nearestTaxiKey=curKey;  

                if(curDistance!=0)
            	  shortestDistance=curDistance; //the current distance is the shortest distance
              
            }
            
            MyTaxi nearestTaxi = ServerThread.taxiList.get(nearestTaxiKey);
            output.writeObject(nearestTaxi);
			
			//Send passenger data to taxi
			Socket taxiSocket = new Socket(nearestTaxi.getIP(), connections.MyConnection.serverPort);
			ObjectOutputStream taxiOutput = new ObjectOutputStream(taxiSocket.getOutputStream());
			taxiOutput.writeObject(passenger);
			taxiOutput.close();
			taxiSocket.close();
          }
          
          else{
        	//links to the object data sent by the client(taxi) connection
            MyTaxi myTaxi = (MyTaxi) inputObject;
            
            //update the taxi coordinates in the view
            SimpleWebBrowserExample.taxiLat = myTaxi.getCurLat();
            SimpleWebBrowserExample.taxiLng  = myTaxi.getCurLng();
            
            //update number of taxi count in the view
            int taxiCount = Integer.parseInt(SimpleWebBrowserExample.taxiCountField.getText());
          	SimpleWebBrowserExample.taxiCountField.setText(String.valueOf(++taxiCount));
          	SwingUtilities.invokeAndWait(new Runnable(){
                public void run() {
    		      SimpleWebBrowserExample.webBrowser.navigate("http://localhost/thesis/multiplemarkers.php?fname=addMarker&arg1="+SimpleWebBrowserExample.taxiLat+"&arg2="+SimpleWebBrowserExample.taxiLng);
                }
          	});

          	//automatically updates myTaxi object pair using its plate number
          	//if the plate number does not exist in the list, it will be automatically inserted
          	System.out.println(myTaxi.getBodyNumber());
          	System.out.println(myTaxi.getCompanyName());
          	ServerThread.taxiList.put(myTaxi.getPlateNumber(), myTaxi);          	
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
    
    private double distance(double lat1, double lon1, double lat2, double lon2) {
	double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;
		return (dist);
	}

	//This function converts decimal degrees to radians
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	//This function converts radians to decimal degrees
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
  }