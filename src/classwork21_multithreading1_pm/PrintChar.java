package classwork21_multithreading1_pm;

public class PrintChar implements Runnable {
	private char c;
	private int n;
	
	public PrintChar (int n, char c) {
		this.n = n;
		this.c = c;
	}
	
	public synchronized void printMeTwice(char c) {
		System.out.print(c);
		System.out.print((char)(c - ('a' - 'A')));
	}

	@Override
	public void run() {
		for (int i = 0; i < n; i++) {
			//System.out.print(c);
			//Thread.yield();
			printMeTwice(c);
		}
	}
	
}
