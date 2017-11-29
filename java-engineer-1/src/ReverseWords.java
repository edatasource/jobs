/**
 * Given an input string, reverse the string word by word.  
 * The user should be able to pass the input string as a paramter to the application.
 */
public class ReverseWords {

	public static void main(String[] args) throws Exception{
		
		System.out.println("Hello from ReverseWords.java");
		
		System.out.print("Enter a string:");
		Scanner s = new Scanner(System.in);
		String strings = s.nextLine();
		
		String delim = "[ ]";
		
		String allstrings[] = strings.split(delim);
		
		int number = allstrings.length;
		String newArray[] = new String[allstrings.length];
		for(int i = 0; i < allstrings.length; i++)
		{
			number = number -1;
			newArray[i] = allstrings[number];
		}
		for(int i = 0; i < newArray.length; i++)
		{
			System.out.print(newArray[i] + " ");
		}
		
		

	}

}
