/**
 * Write a java code that takes input of a number N, and outputs counting from 1 to N in "N" seconds. Use Threads.
 * The user should be able to pass the number "N" to the application as a parameter.
 *
 * Author: Eduardo Salas
 *      Last Edited: 11/28/2017
 */
public class CountingThreading {

	public static void main(String[] args) throws Exception{
		System.out.println("Hello from CountingThreading.java");

		//check for n if passed in, pass in through commandline argument
        int n = 10;
        try{
            n = Integer.parseInt(args[0]);
        } catch(ArrayIndexOutOfBoundsException exception) {}

        //display num to count to
        System.out.println("Number to count to: " + n);

        //initialize thread with n
        Thread thread = new Thread(new CountingThreading().new Counter(n));

        //start thread
        thread.start();
	}

    class Counter extends Thread {

	    private int num = 0;

        public Counter(int n) {
            this.num = n;
        }

        @Override
        public void run() {
            //go from 1 to n
            for(int i=1; i <= num; i++) {
                //sleep for 1 second
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }

                //print current number
                System.out.println(i);
            }
        }
    }
}
