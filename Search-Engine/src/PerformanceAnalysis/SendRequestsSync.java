package PerformanceAnalysis;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class SendRequestsSync implements Runnable {
	public String ConnectUrl;
	public AtomicInteger counter;
	public boolean sync;
	

	public SendRequestsSync(String connectUrl, AtomicInteger counter,boolean sync) {
		super();
		ConnectUrl = connectUrl;
		this.counter = counter;
		this.sync=sync;
		
	}


	@Override
	public void run() {
		URL obj;
		try {
			obj = new URL(ConnectUrl);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			//con.setConnectTimeout(5);
			if(sync) {
				con.setReadTimeout(7000);
				
			}
			
			int responseCode = con.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) { // success
				counter.incrementAndGet();
				
			}
			
		} catch (Exception e) {
			
			return;
		}
		
		
	}
}


