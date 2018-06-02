package MD5;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	
	public static String gethash(String str) throws NoSuchAlgorithmException
	{
		  String MD5=str;
		   MessageDigest md = MessageDigest.getInstance("MD5");
		   byte[] md5 = md.digest(MD5.getBytes());
		   StringBuffer sb = new StringBuffer();
		   String part = null;
		   for (int i = 0; i < md5.length; i++) {
		    part = Integer.toHexString(md5[i] & 0xFF);
		    if (part.length() == 1) {
		     part = "0" + part;
		    }
		    sb.append(part);
		   }
		   String result=sb.append(part).toString();//MD5ÃÜÎÄ×Ö·û´®
		   return result;
	}
	public static void main(String[]args) throws NoSuchAlgorithmException
	{
		String str="++++";
		System.out.println(gethash(str));
	}
}
