// Copy-paste this file at the top of every file you turn in.
/*
* EE422C Final Project submission by
* Replace <...> with your actual data.
* <Emmanuel Ahonle>
* <eva278>
* <17610>
* Fall 2022
*/

package finalProject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class Item {
	String name;
	String description;
	double currBid;
	double buyNow;
	int timer;
	TextField bidHistory;
	
	
	public Item(String name, String description, double currBid, double buyNow, int timer, String bidHistory) {
		this.name = name;
		this.description = description;
		this.currBid = currBid;
		this.buyNow = buyNow;
		this.timer = timer;
		this.bidHistory = new TextField(bidHistory);
		this.bidHistory.setEditable(true);
	}
	
	public Item(GsonItem g) {
		this.name = g.getName();
		this.description = g.getDescription();
		this.currBid = g.getCurrBid();
		this.buyNow = g.getBuyNow();
		this.timer = Integer.parseInt(g.getTimer());
		this.bidHistory = new TextField(g.getBidHistory());
		this.bidHistory.setEditable(true);
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public double getCurrBid() {
		return currBid;
	}
	
	public double getBuyNow() {
		return buyNow;
	}
	
	public int getTimer() {
		return timer;
	}
	
	public String getBidHistory() {
		return bidHistory.getText();
	}
	
	public void setBidHistory(String bh) {
		this.bidHistory.setText(bh);
	}
	
	public void setCurrBid(double b) {
		this.currBid = b;
	}
}

