/**
 * Given an input string, reverse the string word by word.  
 * The user should be able to pass the input string as a parameter to the application.
 */
public class ReverseWords
{
	public static void main(String[] args) throws Exception
	{
		System.out.println("Hello from ReverseWords.java");

		String input = String.join(" ", args);
		System.out.println(reverseString(input));
	}

	static private String reverseString(String s)
	{
		if(s.lastIndexOf(" ") <= 0)
			return s;
		else
			return s.substring(s.lastIndexOf(" ")).trim() + " " + reverseString(s.substring(0, s.lastIndexOf(" ")));
	}
}
