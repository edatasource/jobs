/**
 * Write a recursive function that takes a string as input and returns the reversed string.
 * The user should be able to pass the input string as a paramter to the application.
 *
 * Author: Eduardo Salas
 *      Last Edited: 11/28/2017
 */
public class Recursion {

	public static void main(String[] args) throws Exception{
        System.out.println("Hello from Recursion.java");

        //check for n if passed in, pass in through commandline argument
	    String strToReverse = "dnuoF retemaraP oN";
	    try{
	        strToReverse = args[0];
        } catch(ArrayIndexOutOfBoundsException exception) {}

        //show original string
        System.out.println("Normal String: ");
        System.out.println(strToReverse + "\n");

        //show result of reversing
        System.out.println("Reversed String: ");
        System.out.println(reverseString(strToReverse));
	}

	private static String reverseString(String str){
	    //check for null, empty, or single character string; return itself
        if ((str.length() <= 1) || (null == str)) {
            return str;
        }

        //recursively sent in the string without its first character, add the character to the end of the string and then return
        return reverseString(str.substring(1)) + str.charAt(0);
	}
}
