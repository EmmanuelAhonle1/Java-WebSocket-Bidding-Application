package classwork21_multithreading1_pm;

public class PrintNum implements Runnable {

	private int max;
	
	public PrintNum(int max) {
		this.max = max;
	}
	public void run() {
		for (int i = 0; i < max; i++) {
			System.out.print(i);
			//Thread.yield();

			/*
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
			
		}
	}
}
