/**
 * Write a java code that takes input of a number N, and outputs counting from 1 to N in "N" seconds. Use Threads.
 * The user should be able to pass the number "N" to the application as a parameter.
 */
import java.util.*;

public class CountingThreading implements Runnable{
	
	int N;
	CountingThreading(int N){
		this.N = N;
	}

	public static void main(String[] args) throws Exception{
		
		System.out.println("Hello from CountingThreading.java\n");
		System.out.print("Enter N: ");
		Scanner sc = new Scanner(System.in);
		int num = sc.nextInt();
		sc.close();
		CountingThreading ct = new CountingThreading(num);
		new Thread(ct).start();
		
	}

	@Override
	public void run() {
		int i = 1;
		while(i <= N){
			System.out.println(i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		
	}

}
