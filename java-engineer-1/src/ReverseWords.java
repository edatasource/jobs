/**
 * Write a recursive function that takes a string as input and returns the reversed string.
 */
import java.util.*;

public class ReverseWords {

	public static void main(String[] args) throws Exception{
		
		System.out.println("Hello from ReverseWords.java\n");
		System.out.print("Enter a string: ");
		Scanner sc = new Scanner(System.in);
		String word = sc.nextLine();
		String rword = reverse(word);
		System.out.println("The reverse of '" + word + "' is '" + rword + "'.");
		sc.close();
	}
	
	public static String reverse(String word){
		int numWords = 0;
		String res = "";
		for(int i = 0; i < word.length(); i++){
			if(word.charAt(i) == ' '){
				numWords++;
			}
		}
		String words[] = new String[numWords + 1];
		words = word.split(" ");
		String ans[] = new String[numWords + 1];
		for(int i = 0; i < words.length; i++){
			ans[i] = words[words.length - 1 -i];
		}
		for(int i = 0; i < words.length; i++){
			res += ans[i] + " ";
		}
		return res.substring(0, word.length());
	}

}
