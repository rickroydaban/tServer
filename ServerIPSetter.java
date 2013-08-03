package chrriis;

public class ServerIPSetter implements Runnable {
   Double taxiLat, taxiLng;

	
   public ServerIPSetter(Double pTaxiLat, Double pTaxiLng){
     taxiLat=pTaxiLat;
     taxiLng=pTaxiLng;
   }
	
	@Override
	public void run() {
		SimpleWebBrowserExample.webBrowser.navigate("http://localhost/thesis/multiplemarkers.php?fname=addMarker&arg1="+taxiLat+"&arg2="+taxiLng);
	}

}
