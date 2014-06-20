package de.se;

import java.util.Observable;

public class SimpleCoordSender extends Observable implements Runnable {

	int rotation = 0;
	private int seconds = 0;
	
	@Override
	public void run() {
		try {
			while(seconds < 160) {
				if(Math.random() > 0.8) {
					rotation += 10;
					System.out.println("sending steer");
					this.setChanged();
					this.notifyObservers(new String("STEER10"));
				}
				System.out.println("sending forward");
				this.setChanged();
				this.notifyObservers(new String("FWD"));
				Thread.sleep(1000);
				seconds ++;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

}
