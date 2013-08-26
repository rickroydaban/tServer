package chrriis;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Set;

import javax.swing.SwingUtilities;

import actors.*;
import connections.MyConnection;
import data.TaxiStatus;


class ConnectionListenerThread implements Runnable {		
    private Socket clientSocket;
    private MyConnection conn;
		
    public ConnectionListenerThread(Socket pClientSocket, MyConnection pConn) {
      clientSocket = pClientSocket;
      conn = pConn;
    }
		
    @Override
    public void run(){
      try {
    	//manages objects sent by the client connections
        //NOTE: No need for client IP since we will use the "output" variable to send data back to client 
    	ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
    	ObjectOutputStream passengerOutSocket = new ObjectOutputStream(clientSocket.getOutputStream());
        
        try{
          Object inputObject = input.readObject(); //must be stored the value here
          
          if(inputObject instanceof MyPassenger){
        	//links to the object data sent by the client connection
            MyPassenger passenger = (MyPassenger) inputObject;
            
            int clientCount = Integer.parseInt(ServerMap.clientCountField.getText());
            ServerMap.clientCountField.setText(String.valueOf(++clientCount));
            
            //Find the nearest taxi and send its information to the passenger
            double shortestDistance = 0, //the shortest distance record within the loop
            	   curDistance = 0;      //the distance estimated by comparing the current taxi's location to the client's source location
            
            MyTaxi nearestTaxi = null; //the data of the resulting nearest taxi estimation
            
            Set<String> keys = ServerThread.taxiList.keySet(); //get all the keys of the taxi record
            
            //loop to find the nearest taxi
            for(String curKey:keys){
              MyTaxi curTaxi = ServerThread.taxiList.get(curKey);
              if(curTaxi.getStatus() == TaxiStatus.vacant)
          	  {
            	  if(shortestDistance==0){
            		  shortestDistance= distance( passenger.getSrcLat(),passenger.getSrcLng(), //calculate the distance of the passenger to the current taxi's location
                						    curTaxi.getCurLat(),curTaxi.getCurLng());
            	  }else{
            		  curDistance = distance( passenger.getSrcLat(),passenger.getSrcLng(),
					                    curTaxi.getCurLat(),curTaxi.getCurLng());
            	  }
              
            	  if(curDistance==0 || curDistance<shortestDistance)
            	  {
            		  nearestTaxi=curTaxi;  
            		  if(curDistance!=0)
            			  shortestDistance=curDistance; //the current distance is the shortest distance
            	  }
              }
            }
            //send taxi data to the passenger
            passengerOutSocket.writeObject(nearestTaxi);
            passengerOutSocket.flush();
			
			//Send passenger data to taxi
			Socket taxiSocket = new Socket(nearestTaxi.getIP(), conn.getTaxiPort());
			ObjectOutputStream taxiOutSocket = new ObjectOutputStream(taxiSocket.getOutputStream());
			taxiOutSocket.writeObject(passenger);
			taxiOutSocket.flush();
			taxiOutSocket.close();
			taxiOutSocket.close();
          }
          
          else{
        	//links to the object data sent by the client(taxi) connection
            MyTaxi myTaxi = (MyTaxi) inputObject;
            boolean keyFound = ServerThread.taxiList.containsKey(myTaxi.getPlateNumber());
            
            if(keyFound)
            {
            	MyTaxi curTaxi = ServerThread.taxiList.get(myTaxi.getPlateNumber());
            	
            	if(curTaxi.getCurLat() != 0 && curTaxi.getCurLng() != 0)
            	{
            		double curDistance = distance(curTaxi.getCurLat(), curTaxi.getCurLng(), myTaxi.getCurLat(), myTaxi.getCurLng());
            		double distanceTraveled = curTaxi.getDistance() + curDistance;
            		myTaxi.setDistance(distanceTraveled);
            	}
            }
            
            else
            {
            	//update number of taxi count
            	int taxiCount = Integer.parseInt(ServerMap.taxiCountField.getText());
            	ServerMap.taxiCountField.setText(String.valueOf(++taxiCount));
            }
            
            //update the taxi coordinates in the view
            ServerMap.taxiLat = myTaxi.getCurLat();
            ServerMap.taxiLng  = myTaxi.getCurLng();
            
            //update number of taxi count in the view
          	SwingUtilities.invokeAndWait(new Runnable(){
                public void run() {
    		      ServerMap.webBrowser.navigate("http://localhost/thesis/multiplemarkers.php?fname=addMarker&arg1="+ServerMap.taxiLat+"&arg2="+ServerMap.taxiLng);
                }
          	});

          	//automatically updates myTaxi object pair using its plate number
          	//if the plate number does not exist in the list, it will be automatically inserted
          	System.out.println(myTaxi.getBodyNumber());
          	System.out.println(myTaxi.getCompanyName());
          	ServerThread.taxiList.put(myTaxi.getPlateNumber(), myTaxi);          	
          }
          
		  input.close();
          passengerOutSocket.close();
        }catch (ClassNotFoundException e) {
        	System.out.println("server class not found exception"+e.getMessage());
		} catch (InterruptedException e) {
        	System.out.println("server interrupted exception"+e.getMessage());
		} catch (InvocationTargetException e) {
        	System.out.println("server invocation targer exception"+e.getMessage());
		}
	  }catch (IOException e) {
        System.out.println("server io exception: "+e.getMessage());
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