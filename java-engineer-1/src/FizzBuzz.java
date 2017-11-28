/**
 * Write a program that prints the numbers from 1 to 100. But for multiples of three print "Fizz" 
 * instead of the number and for the multiples of five print "Buzz". 
 * For numbers which are multiples of both three and five print "FizzBuzz".
 *
 * Author: Eduardo Salas
 *      Last Edited: 11/28/2017
 */
public class FizzBuzz {

	private static int n = 100;

	public static void main(String[] args) throws Exception{
        System.out.println("Hello from FizzBuzz.java");

		for(int i = 1; i <= n; i++){
		    //add current number to result
			String testResult = i + " ";

			//add Fizz if divisible by 3
			testResult += (i % 3 == 0) ? "Fizz" : "";

			//add Buzz if divisible by 5
            testResult += (i % 5 == 0) ? "Buzz" : "";

            //show current number in loop with result
            System.out.print(testResult + "\n");
		}
	}
}
