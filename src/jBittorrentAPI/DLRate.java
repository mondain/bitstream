package jBittorrentAPI;

public class DLRate {
	private double speed;
	private long lastUpdate;
	public DLRate(double s) {
		speed = s;
		this.lastUpdate = System.currentTimeMillis();
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
		this.lastUpdate = System.currentTimeMillis();
	}

	public void setLastUpdate() {
		this.lastUpdate = System.currentTimeMillis();
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public long getInterval() {
		return System.currentTimeMillis() - this.lastUpdate;
	}
}

