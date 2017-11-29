/**
 * Write a recursive function that takes a string as input and returns the reversed string.
 * The user should be able to pass the input string as a parameter to the application.
 *
 */
public class Recursion
{
	/**
	 * Main class, takes input from command line and creates a
	 * string before applying reverseString
	 * @param args command line input
	 */
	public static void main(String[] args)
	{
		System.out.println("Hello from Recursion.java");

		String input = String.join(" ", args);
		System.out.println(reverseString(input));
	}

	/**
	 * revereString reverses an input string character by character
	 * and returns it
	 * @param s input string
	 * @return reversed string
	 */
	static private String reverseString(String s)
	{
		if(s.length() == 1)
			return s;
		else
			//Creates a substring of the last character and concatenates it with the rest of
			//the string minus the last character
			return s.substring(s.length()-1, s.length()) + reverseString(s.substring(0, s.length()-1));
	}
}
