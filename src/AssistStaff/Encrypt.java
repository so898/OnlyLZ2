package AssistStaff;

public class Encrypt {
	public static String Encode(char [] s){
		char[]   a   =   s;
		for (int i = 0;i < a.length;i++)   {
			a[i] = (char) (a[i] ^ 't');
		}
		String secret = new String(a);
		return secret;
	}
	
	public static String Decode(String s){
		char [] a = s.toCharArray();
		for   (int   i   =   0;   i   <   a.length;   i++)   {
	        a[i]   =   (char)   (a[i]^ 't');
	    }
	    String   secret   =   new   String(a);
		return (secret);	
	}
}
