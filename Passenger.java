package chrriis;

public class Passenger{
	int id;
	double srcLat, srcLng, desLat, desLng;
	String name;
	PassengerStatus ps;
	
	public Passenger(int pId, double pSrcLat, double pSrcLng, double pDesLat, double pDesLng, PassengerStatus pPs, String pName){
		id= pId;
		srcLat=pSrcLat;
		srcLng=pSrcLng;
		desLat=pDesLat;
		desLng=pDesLng;
		ps=pPs;
		name=pName;
	}
	
}
