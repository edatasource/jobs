/**
 * Write a recursive function that takes a string as input and returns the reversed string.
 * The user should be able to pass the input string as a parameter to the application.
 *
 */
public class Recursion
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Hello from Recursion.java");

		String input = String.join(" ", args);
		System.out.println(reverseString(input));
	}

	static private String reverseString(String s)
	{
		if(s.length() == 1)
			return s;
		else
			return s.substring(s.length()-1, s.length()) + reverseString(s.substring(0, s.length()-1));
	}
}
