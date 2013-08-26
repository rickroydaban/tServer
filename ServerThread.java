package chrriis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Hashtable;

import actors.MyTaxi;
import connections.MyConnection;

public class ServerThread implements Runnable{
  public static ServerSocket serverSocket;
  public static Hashtable<String, MyTaxi> taxiList;
  String jsonText;  
  MyConnection conn;
//  private Database database;    
  @Override
  public void run(){
    try{
      conn = new MyConnection();	
      serverSocket = new ServerSocket(conn.getServerPort());
      taxiList=new Hashtable<String, MyTaxi>();
      
      InetAddress ip=InetAddress.getLocalHost();
      conn.setServerIp(ip.getHostAddress());
      String url = "http://transphone.freetzi.com/thesis/dbmanager.php?fname=setServerIP&arg1="+ip.getHostAddress();
      InputStream is = new URL(url).openStream();
      try {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        jsonText = readAll(rd);
      } finally {
        is.close();
      }      
      
      ServerMap.statusField.setText("ONLINE. Listening for Requests... ");
	}catch (IOException e) {
      System.out.println("FAILED TO LISTEN TO SERVERPORT...");
      e.printStackTrace();
    }
    
    //While server socket is not closed
	while(ServerMap.isServerThreadRunning){
      try{
    	//updates listener for units on operation
    	System.out.println("Status: System is now ready for incoming connection requests.");
    	Socket clientSocket = serverSocket.accept(); 
    	
    	new Thread(new ConnectionListenerThread(clientSocket,conn)).start();
      }catch (IOException e) {
        System.out.println(e.getCause());
	  }
	}  
	//process continues if the server has been established then generate a server-client connection	
	ServerMap.statusField.setText("SERVER IS STOPPED!");
  }	
  //NOTE: client is a general term as us CLIENT-SERVER connections NOT CLIENT,TAXI,SERVER..
  
  private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
}