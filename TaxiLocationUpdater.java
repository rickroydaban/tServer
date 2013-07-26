package chrriis;

public class TaxiLocationUpdater implements Runnable {
   Double taxiLat, taxiLng;

	
   public TaxiLocationUpdater(Double pTaxiLat, Double pTaxiLng){
     taxiLat=pTaxiLat;
     taxiLng=pTaxiLng;
   }
	
	@Override
	public void run() {
		SimpleWebBrowserExample.webBrowser.navigate("http://localhost/thesis/multiplemarkers.php?fname=addMarker&arg1="+taxiLat+"&arg2="+taxiLng);
	}

}
