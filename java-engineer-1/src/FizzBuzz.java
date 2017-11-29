/**
 * Write a program that prints the numbers from 1 to 100. But for multiples of three print "Fizz" 
 * instead of the number and for the multiples of five print "Buzz". 
 * For numbers which are multiples of both three and five print "FizzBuzz".
 */
public class FizzBuzz
{
	private static int MIN = 1;		//The minimum value parsed
	private static int MAX = 100;	//The maximum value parsed

	/**
	 * Main class of FizzBuzz, prints "Fizz" for multiples of three, "Buzz"
	 * for multiples of five, and finally "FizzBuzz" for multiples of both in
	 * the set between the min and max values, inclusive
	 * @param args default main argument
	 */
	public static void main(String[] args)
	{
		System.out.println("Hello from FizzBuzz.java");

		for(int i = MIN; i < MAX; i++)
		{
			if(i%3 == 0)
			{
				if (i%5 == 0)
					System.out.println("FizzBuzz");
				else
					System.out.println("Fizz");
			}
			else if(i%5 == 0)
				System.out.println("Buzz");
			else
				System.out.println(i);
		}
	}
}
