/**
 * Given an input string, reverse the string word by word.  
 * The user should be able to pass the input string as a paramter to the application.
 *
 * Author: Eduardo Salas
 *      Last Edited: 11/28/2017
 */
public class ReverseWords {

	public static void main(String[] args) throws Exception{
        System.out.println("Hello from ReverseWords.java");

        //check for n if passed in, pass in through commandline argument
        String strToReverse = "Found Parameter No";
        try{
            strToReverse = args[0];
        } catch(ArrayIndexOutOfBoundsException exception) {}

        //show normal string
        System.out.println("Normal String: ");
        System.out.println(strToReverse + "\n");

        //show the result of reverseWords
        System.out.println("Reversed Words: ");
        System.out.println(reverseWords(strToReverse));
	}

	private static String reverseWords(String str) {
        //check for null, empty, or single character string; return itself
        if ((null == str) || (str.length() <= 1)) {
            return str;
        }

        //split string into an array of words
        String[] word = str.split(" ");

        //construct string from going backwards in the array
        StringBuilder builder = new StringBuilder();
        for (int i = word.length - 1; i >= 0; i--) {
            if (!word[i].equals("")) {
                //add word to string builder with an additional space
                builder.append(word[i]).append(" ");
            }
        }

        //get rid of the extra space at the end
        if(builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }

        //return the resulting string
        return builder.toString();
    }
}
