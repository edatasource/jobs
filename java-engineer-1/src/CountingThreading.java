/**
 * Write a java code that takes input of a number N, and outputs counting from 1 to N in "N" seconds. Use Threads.
 * The user should be able to pass the number "N" to the application as a parameter.
 */
public class CountingThreading
{
	private static int DEFAULT_TIME_STEP = 1000; 	//Default time for a thread to wait in mills

	/**
	 * Main method, takes an int from command line and starts that
	 * number of threads
	 * @param args contains command line input, a single number
	 */
	public static void main(String[] args)
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

	/**
	 * Private class used to create threads
	 */
	private static class CountingThreads implements Runnable
	{
		int count;	//The number assigned to this thread

		/**
		 * Constructor initializes a thread with the current count
		 * @param count The number of assigned to this thread
		 */
		CountingThreads(int count)
		{
			this.count = count;
		}

		/**
		 * Required class run(), called when the thread is instantiated
		 * The class waits the specified number of mills based on the
		 * default time step and count of this thread
		 */
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
