package it.imposimato.tablut;

public class TablutTimeoutManager extends Thread {


	private int timeout;
	private boolean overTime = false;
	
	public TablutTimeoutManager (int timeout) {
		this.timeout = timeout; 
	}

	public void run() {
		try {
			overTime = false;
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		overTime = true;
	}

	public boolean isOverTime() {
		return overTime;
	}
	
	
}
