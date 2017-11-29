/**
 * Given an input string, reverse the string word by word.  
 * The user should be able to pass the input string as a parameter to the application.
 */
public class ReverseWords
{
	/**
	 * Main class, takes command line input and converts it to a string
	 * before calling reverseString
	 * @param args command line input
	 */
	public static void main(String[] args)
	{
		System.out.println("Hello from ReverseWords.java");

		String input = String.join(" ", args);
		System.out.println(reverseString(input));
	}
	/**
	 * revereString reverses an input string word by word
	 * and returns it
	 * @param s input string
	 * @return reversed string
	 */
	static private String reverseString(String s)
	{
		if(s.lastIndexOf(" ") <= 0)
			return s;
		else
			//Grabs last word and concatenates to the front of the remainder of the string
			return s.substring(s.lastIndexOf(" ")).trim() + " " + reverseString(s.substring(0, s.lastIndexOf(" ")));
	}
}
