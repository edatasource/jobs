/**
 * Write a recursive function that takes a string as input and returns the reversed string.
 * The user should be able to pass the input string as a paramter to the application.
 *
 */
public class Recursion {

	public static void main(String[] args) throws Exception{
		
		System.out.println("Hello from Recursion.java");
		
		System.out.print("Enter a string to be reversed:");
		Scanner s = new Scanner(System.in);
		String tobeReversed = s.next();
		
		System.out.print(switchitup(tobeReversed));
		
		
		
	}
	
	public static String switchitup(String s)
	{
		char temp;//this will hold the character that we are reversing 
		if(s.length() == 0)//nothing more to reverse
		{
		return s;
		}
		else
		{
			temp = s.charAt(0);
			//every time one more is taken off and added to the overall string
			return switchitup(s.substring(1)) + temp;
			
		}
		
		
	}

}
