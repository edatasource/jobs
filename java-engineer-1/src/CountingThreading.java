/**
 * Write a java code that takes input of a number N, and outputs counting from 1 to N in "N" seconds. Use Threads.
 * The user should be able to pass the number "N" to the application as a parameter.
 */
import java.util.Scanner;

public class CountingThreading implements Runnable {
	
		
	
	public void run()
	{
		
	}
	
	
	public static void main(String[] args) throws Exception{
		
		System.out.println("Hello from CountingThreading.java");
		
		System.out.print("Enter a number:");
		Scanner s = new Scanner(System.in);
		int numOfThreads = s.nextInt();
		
		for(int i = 0; i < numOfThreads; i++)
		{
			CountingThreading c = new CountingThreading();
			Thread newT  = new Thread(c);
			newT.start();
			Thread.sleep(1000);
			System.out.println(i+1);
		}
		
	}
	

}
