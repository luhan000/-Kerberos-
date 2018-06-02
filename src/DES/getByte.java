package DES;
import java.io.UnsupportedEncodingException;

public class getByte {

	public static void main(String[] args) throws UnsupportedEncodingException{
		String tt="helloworld";
		String t="\0";
		tt=tt+t;
		byte[] ms;
		ms = tt.getBytes("UTF-8");
		System.out.println(tt);
	}
}
