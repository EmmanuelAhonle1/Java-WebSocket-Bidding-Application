package classwork21_multithreading1_pm;

public class PrintStar extends Thread {

	private int num;
	
	public PrintStar(int num) {
		this.num = num;
	}
	
	public void run() {
		for (int i = 0; i < num; i++) {
			System.out.print('*');
		}
	}
}
