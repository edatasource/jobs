import java.util.Scanner;

/**
 * Write a recursive function that takes a string as input and returns the reversed string.
 */
public class Recursion {

	public static void main(String[] args) throws Exception{
		
		System.out.println("Hello from Recursion.java");
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter a word: ");
		String word = sc.next();
		sc.close();
		String word1 = reverse(word);
		System.out.println("The reverse of '" + word + "' is '" + word1 + "'.");

	}
	public static String reverse(String word){
		if(word.length() <= 1)
		{
			return word;
		}
		return reverse(word.substring(1)) + word.charAt(0);
	}
}

