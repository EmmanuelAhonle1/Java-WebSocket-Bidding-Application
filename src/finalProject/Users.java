package finalProject;

import com.google.gson.Gson;

import java.math.BigInteger;
import java.security.*;

public class Users {
	private String user;
	private String pass;
	
	
	
	public Users(String user, String pass) {
		this.user = user;
		this.pass = pass;
		try {
			encryptPassword();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String toString() {
		
		Gson gson = new Gson();
		return gson.toJson(this);
	}
	
	private void encryptPassword() throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		
		byte[] messageDigest = md.digest(this.pass.getBytes());
		
		BigInteger bigInt = new BigInteger(1,messageDigest);
		
		this.pass = bigInt.toString(16);
	}
	
	public String hasher(String pass) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		
		byte[] messageDigest = md.digest(pass.getBytes());
		
		BigInteger bigInt = new BigInteger(1,messageDigest);
		
		return bigInt.toString(16);
	}
	
	public String getUser() {
		return user;
	}
	
	public String getPass() {
		return pass;
	}
}
