//package chrriis;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//import actors.*; 
//
//public class Database {
//	
//	private Connection connection = null;
//	private Statement statement = null;
//	private ResultSet resultSet = null;
//	
//	private String dbDriver = "com.mysql.jdbc.Driver";
//	private String dbUrl = "jdbc:mysql://localhost/transphone?user=root&password=''";
//	
//	public Database() {
//		try {
//			// This will load the MySQL driver
//			Class.forName(dbDriver);
//			
//			// Setup the connection with the DB
//			connection = DriverManager.getConnection(dbUrl);
//			
//			// Statements allow to issue SQL queries to the database
//			statement = connection.createStatement();
//			
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	//Insert passenger data into database
//	/*public void insertClientValues(ClientStruct client) throws SQLException {
//		statement.execute("INSERT INTO CLIENT VALUES (default, "+client.srcLatitude+", "+client.srcLongitude+", "
//														+client.destLatitude+", "+client.destLongitude+")");
//	}*/
//	
//	//LOGIN TAXI DATA
//	public MyTaxi loginTaxiData(MyTaxi pMyTaxi) throws SQLException {
//		int result = 0;
//		
//		//Check if driver exists
//		resultSet = statement.executeQuery("SELECT * FROM DRIVER WHERE Driver_License LIKE '"+pMyTaxi.driverLicense+"'");
//		
//		//If driver exists
//		if(resultSet.first())
//			result = statement.executeUpdate("UPDATE TAXI SET Taxi_IP='"+pMyTaxi.ip+"', Driver_License='"+pMyTaxi.driverLicense+"' WHERE Plate_No LIKE '"+pMyTaxi.plateNo+"'");
//		
//		//If there is a result
//		if(result > 0)
//		{
//			resultSet = statement.executeQuery("SELECT * FROM TAXI WHERE Plate_No LIKE '"+pMyTaxi.plateNo+"'");
//			//Let's the Result Set point to the first result since it's initially pointing to its head not the first index
//			resultSet.next();
//			pMyTaxi = getTaxiResult(resultSet);
//			//Set id to taxi
//			pMyTaxi.id = ServerThread.taxiIdentificator.requestId();
//		}
//		return pMyTaxi;
//	}
//	
//	//UPDATE TAXI DATA
//	public void updateTaxiData(MyTaxi pMyTaxi) throws SQLException {
//		//Get latitude, longitude, and distance of taxi from database
//		resultSet = statement.executeQuery("SELECT Taxi_Latitude, Taxi_Longitude, Distance_Traveled FROM TAXI WHERE Plate_No LIKE '"+pMyTaxi.plateNo+"'");
//		resultSet.next();
//		double prevLatitude = resultSet.getDouble("Taxi_Latitude");
//		double prevLongitude = resultSet.getDouble("Taxi_Longitude");
//		double distanceTraveled = 0;
//		
//		//If the latitude and longitude is 0, it means that the taxi has not set its location in the database yet
//		if(prevLatitude != 0 && prevLongitude != 0)
//		{
//			//Get the distance between the previous location and current location
//			double curDistance = distance(prevLatitude, prevLongitude, pMyTaxi.taxiLat, pMyTaxi.taxiLng);
//			distanceTraveled = resultSet.getDouble("Distance_Traveled");
//			//Add the total distance traveled by the taxi 
//			distanceTraveled += curDistance;
//		}
//		//Update the latitude, longtiude, distance, and status of the taxi
//		statement.executeUpdate("UPDATE TAXI SET Taxi_Latitude="+pMyTaxi.taxiLat+", Taxi_Longitude="+pMyTaxi.taxiLng+", Distance_Traveled="+distanceTraveled+", Vacancy_Status='"+pMyTaxi.status+"' WHERE Plate_No LIKE '"+pMyTaxi.plateNo+"'");
//	}
//	
//	
//	public void clearTaxiData() throws SQLException {
//		statement.executeUpdate("UPDATE TAXI SET Taxi_IP = '', Taxi_Latitude = 0, Taxi_Longitude = 0, Vacancy_Status = '', Driver_License = ''");
//	}
//	
//	
//	public void displayTaxi(String plateNo, String bodyNo) throws SQLException {
//		if(!plateNo.equals("") && !bodyNo.equals(""))
//			resultSet = statement.executeQuery("SELECT * FROM TAXI WHERE Plate_No LIKE '"+plateNo+"' AND Body_No LIKE '"+bodyNo+"'");
//		
//		else if(!plateNo.equals(""))
//			resultSet = statement.executeQuery("SELECT * FROM TAXI WHERE Plate_No LIKE '"+plateNo+"'");
//		
//		else 
//			resultSet = statement.executeQuery("SELECT * FROM TAXI WHERE Body_No LIKE '"+bodyNo+"'");
//		
//		resultSet.next();
//		MyTaxi taxi = getTaxiResult(resultSet);
//		displayResultSet(taxi);
//	}
//	
//	
//	public void displayAllTaxi() throws SQLException {
//		resultSet = statement.executeQuery("SELECT * FROM TAXI");
//		
//		while(resultSet.next())
//		{
//			MyTaxi taxi = getTaxiResult(resultSet);
//			displayResultSet(taxi);
//		}
//	}
//	
//	//GET TAXI DATA BASED ON THE RESULT SET
//	private MyTaxi getTaxiResult(ResultSet resultSet) throws SQLException {
//		MyTaxi myTaxi = new MyTaxi();
//		myTaxi.plateNo = resultSet.getString("Plate_No");
//		myTaxi.bodyNo = resultSet.getString("Body_No");
//		myTaxi.companyName = resultSet.getString("Taxi_Company");
//		myTaxi.description = resultSet.getString("Taxi_Description");
//		myTaxi.ip = resultSet.getString("Taxi_IP");
//		myTaxi.taxiLat = resultSet.getDouble("Taxi_Latitude");
//		myTaxi.taxiLng = resultSet.getDouble("Taxi_Longitude");
//		myTaxi.disTraveled = resultSet.getDouble("Distance_Traveled");
////		myTaxi.status = resultSet.getString("Vacancy_Status");
//		myTaxi.driverLicense = resultSet.getString("Driver_License");
//		return myTaxi;
//	}
//	
//	//DISPLAY DATA OF TAXI
//	private void displayResultSet(MyTaxi taxi) {
//		System.out.println("Plate No: " + taxi.plateNo);
//		System.out.println("Body No: " + taxi.bodyNo);
//		System.out.println("Taxi Company: " + taxi.companyName);
//		System.out.println("Taxi Description: " + taxi.description);
//		System.out.println("Taxi IP: " + taxi.ip);
//		System.out.println("Taxi Latitude: " + taxi.taxiLat);
//		System.out.println("Taxi Longitude: " + taxi.taxiLng);
//		System.out.println("Taxi Vacancy: " + taxi.status);
//		System.out.println("Driver License: " + taxi.driverLicense);
//		System.out.println("\n");
//	}
//	
//	//CALCULATE SHORTEST TAXI DISTANCE
//	public MyTaxi calculateShortestTaxiDistance(MyPassenger passenger) throws SQLException {
//		MyTaxi taxi = new MyTaxi();
//		double shortestDistance = 0, curDistance = 0;
//		Statement driverStatement = connection.createStatement();
//		
//		resultSet = statement.executeQuery("SELECT * FROM TAXI WHERE Vacancy_Status LIKE 'Vacant'");
//		
//		while(resultSet.next())
//		{
//			if(shortestDistance == 0)
//				shortestDistance = distance(passenger.srcLat, passenger.srcLng, resultSet.getDouble("Taxi_Latitude"), resultSet.getDouble("Taxi_Longitude"));
//			else
//				curDistance = distance(passenger.srcLat, passenger.srcLng, resultSet.getDouble("Taxi_Latitude"), resultSet.getDouble("Taxi_Longitude"));
//			
//			if(curDistance == 0 || curDistance < shortestDistance)
//			{
//				/*taxi.plateNo = resultSet.getString("Plate_No");
//				taxi.bodyNo = resultSet.getString("Body_No");
//				taxi.companyName = resultSet.getString("Taxi_Company");
//				taxi.description= resultSet.getString("Taxi_Description");
//				taxi.ip = resultSet.getString("Taxi_IP");*/
//				
//				taxi = getTaxiResult(resultSet);
//				
//				ResultSet driverResultSet = driverStatement.executeQuery("SELECT * FROM DRIVER WHERE Driver_License LIKE '"+resultSet.getString("Driver_License")+"'");
//				driverResultSet.next();
//				taxi.driverName = driverResultSet.getString("Driver_Name");
//				taxi.driverNo = driverResultSet.getDouble("Driver_No");
//				
//				if(curDistance != 0)
//					shortestDistance = curDistance;
//			}
//		}
//		return taxi;
//	}
//	
//	
//	private double distance(double lat1, double lon1, double lat2, double lon2) {
//		double theta = lon1 - lon2;
//		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
//		dist = Math.acos(dist);
//		dist = rad2deg(dist);
//		dist = dist * 60 * 1.1515;
//		dist = dist * 1.609344;
//		return (dist);
//	}
//
//	//This function converts decimal degrees to radians
//	private double deg2rad(double deg) {
//		return (deg * Math.PI / 180.0);
//	}
//
//	//This function converts radians to decimal degrees
//	private double rad2deg(double rad) {
//		return (rad * 180.0 / Math.PI);
//	}
//}