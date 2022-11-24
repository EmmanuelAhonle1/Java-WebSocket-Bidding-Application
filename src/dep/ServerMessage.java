package dep;

class Message {
  String type;
  String input;
  int number;

  protected Message() {
    this.type = "";
    this.input = "";
    this.number = 0;
    System.out.println("server-side message created");
  }

  protected Message(String type, String input, int number) {
    this.type = type;
    this.input = input;
    this.number = number;
    System.out.println("server-side message created");
  }
}


class LoginMessage{
	String username;
	String password;
	


	protected LoginMessage(String user, String pass) {
		    this.username = user;
		    this.password = pass;
		    System.out.println("server login response created");
		  }
}