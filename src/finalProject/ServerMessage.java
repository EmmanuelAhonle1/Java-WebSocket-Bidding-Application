package finalProject;


class LoginMessage{
	String username;
	String password;
	


	protected LoginMessage(String user, String pass) {
		    this.username = user;
		    this.password = pass;
		    System.out.println("server login response created");
		  }
}