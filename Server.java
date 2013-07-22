package chrriis;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{
  public ServerSocket serverSocket;
  public IdentityGenerator passengerIdentificator; //global scope for id generator for passengers
  public IdentityGenerator taxiIdentificator; //global scope for id generator for taxi
    
  @Override
  public void run(){
    try {
      passengerIdentificator = new IdentityGenerator();
      taxiIdentificator=new IdentityGenerator();
      
      serverSocket = new ServerSocket(ApplicationAdapter.PORT);
      SimpleWebBrowserExample.statusField.setText("LISTENING TO PORT: " + ApplicationAdapter.PORT);
      ApplicationAdapter.isThreadRunning=true;			
	}catch (IOException e) {
      System.out.println("FAILED TO LISTEN TO PORT...");
      ApplicationAdapter.isThreadRunning=false;
      e.printStackTrace();
    }
    
	while(ApplicationAdapter.isThreadRunning){
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
      this.clientSocket = pClientSocket;
    }
		
    @Override
    public void run(){
      try {
    	//manages objects sent by the client connections
        ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
        ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
				
        try{
          Object inputObject = input.readObject(); //must be stored the value here
          
          if(inputObject instanceof TRANSPHONE_Actors.MyPassenger){
        	//links to the object data sent by the client connection
            TRANSPHONE_Actors.MyPassenger sp = (TRANSPHONE_Actors.MyPassenger) inputObject;
            //copy the data inside the object sent by the client connection
            TRANSPHONE_Actors.MyPassenger serverPassenger = new TRANSPHONE_Actors.MyPassenger( 
        		                                                       passengerIdentificator.requestId(),
        		                                                       sp.getSrcLat(),sp.getSrcLng(),
						                                               sp.getDesLat(),sp.getDesLng(),
						                                               sp.getClientName());
            serverPassenger.setIp(clientSocket.getInetAddress().toString()); //get the ip of the requesting passenger
            int clientCount = Integer.parseInt(SimpleWebBrowserExample.clientCountField.getText());
            SimpleWebBrowserExample.clientCountField.setText(String.valueOf(++clientCount));		
          }else{
        	//links to the object data sent by the client connection
            TRANSPHONE_Actors.MyTaxi st = (TRANSPHONE_Actors.MyTaxi) inputObject;
            //copy the data inside the object sent by the client connection
            TRANSPHONE_Actors.MyTaxi serverTaxi = new TRANSPHONE_Actors.MyTaxi( 
          		                                                       taxiIdentificator.requestId(),
          		                                                       st.getCurLat(), st.getCurLng(), 
          		                                                       st.getPlateNumber(), st.getBodyNumber(), 
          		                                                       st.getDriverName());
            serverTaxi.setIp(clientSocket.getInetAddress().toString()); //get the ip of the requesting passenger
            int taxiCount = Integer.parseInt(SimpleWebBrowserExample.taxiCountField.getText());
  	 	    SimpleWebBrowserExample.clientCountField.setText(String.valueOf(++taxiCount));		     	  
          }
		    input.close();
            output.close();
        }catch (ClassNotFoundException e) {
				  System.out.println(e.getMessage());
		}
	  }catch (IOException e) {
        System.out.println(e.getMessage());
	  }finally{
	    if(clientSocket!= null){
          try{
            clientSocket.close();
          }catch (IOException e) {
		    System.out.println(e.getMessage());
          }
        }
      }
	}
  }
}