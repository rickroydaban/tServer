package chrriis;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import actors.MyTaxi;

public class ServerThread implements Runnable{
  public static ServerSocket serverSocket;
  public static Hashtable<String, MyTaxi> taxiList;
  
//  private Database database;    
  @Override
  public void run(){
    try{
      serverSocket = new ServerSocket(connections.MyConnection.serverPort);
      taxiList=new Hashtable<String, MyTaxi>();
      //Initialize database
//      database = new Database();
      SimpleWebBrowserExample.statusField.setText("ONLINE. Listening for Requests... ");
	}catch (IOException e) {
      System.out.println("FAILED TO LISTEN TO SERVERPORT...");
      e.printStackTrace();
    }
    
    //While server socket is not closed
	while(!serverSocket.isClosed()){
      try{
    	//updates listener for units on operation
    	System.out.println("Status: System is now ready for incoming connection requests.");
    	Socket clientSocket = serverSocket.accept(); 
    	
    	new Thread(new ConnectionListenerThread(clientSocket)).start(); //one thread is allocated for every taxi on process
      }catch (IOException e) {
        System.out.println(e.getCause());
	  }
	}  
	//process continues if the server has been established then generate a server-client connection	
	SimpleWebBrowserExample.statusField.setText("SERVER IS STOPPED!");
  }	
  
  //NOTE: client is a general term as us CLIENT-SERVER connections NOT CLIENT,TAXI,SERVER..
}