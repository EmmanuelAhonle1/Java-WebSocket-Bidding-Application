package finalProject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class GsonItem {
	private String name;
	private String description;
	private double currBid;
	private double buyNow;
	private int timer;
	private String bidHistory;
		
	public GsonItem(Item g) {
		this.name = g.name;
		this.description = g.description;
		this.currBid = g.currBid;
		this.buyNow = g.buyNow;
		this.timer = g.timer;
		this.bidHistory = g.bidHistory.getText();
		
		
	}
	
	public GsonItem(String name, String description, double currBid, double buyNow, int timer, String bidHistory) {
		this.name = name;
		this.description = description;
		this.currBid = currBid;
		this.buyNow = buyNow;
		this.timer = timer;
		this.bidHistory = bidHistory;
	}
	

	public void setBidHistory(String string) {
		this.bidHistory = string;
	}

	public void setCurrBid(double currBid2) {
		this.currBid = currBid2;
	}

	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public double getCurrBid() {
		return this.currBid;
	}
	
	public double getBuyNow() {
		return this.buyNow;
	}
	
	public int getTimer() {
		return this.timer;
	}
	
	public String getBidHistory() {
		return this.bidHistory;
	}
	
	public String toString() {
		
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		
		return gson.toJson(this);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	

}
