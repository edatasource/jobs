/**
 * Write a java code that takes input of a number N, and outputs counting from 1 to N in "N" seconds. Use Threads.
 * The user should be able to pass the number "N" to the application as a parameter.
 */
public class CountingThreading
{
	private static int DEFAULT_TIME_STEP = 1000;

	public static void main(String[] args) throws Exception
	{
		System.out.println("Hello from CountingThreading.java");
		int input = Integer.parseInt(args[0]);

		Thread t;
		for(int i = 1; i <= input; i++)
		{
			t = new Thread(new CountingThreads(i));
			t.start();
		}
	}

	private static class CountingThreads implements Runnable
	{
		int count;
		CountingThreads(int count)
		{
			this.count = count;
		}

		public void run()
		{
			try
			{
				Thread.sleep(count*DEFAULT_TIME_STEP);
				System.out.println(count);
			}
			catch (InterruptedException e)
			{
				System.out.println("Thread was interrupted");
				e.printStackTrace();
			}
		}
	}
}
