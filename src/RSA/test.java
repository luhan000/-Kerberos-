package RSA;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Base64;

public class test {
	public  static void main(String []args) throws UnsupportedEncodingException, UnknownHostException, SocketException
	{
		String pukey="79&518940563";
		System.out.println("pukey:"+pukey);
		String prkey="282436519&518940563";
		System.out.println("prkey:"+prkey);
		String id="002";
		byte[]en=id.getBytes();
		byte[]lm;
        en=RSA.encrypt(prkey, en);
        lm=Base64.getEncoder().encode(en);
		String len=new String(lm);
		System.out.println(len+len.length());
		System.out.println(en[0]+","+en[1]+","+en[2]+","+en[3]);
		en=Base64.getDecoder().decode(len.getBytes());
		byte[]de=RSA.decrypt(pukey,en);
		System.out.println(new String(de));
		/*InetAddress ip = InetAddress.getLocalHost();
		NetworkInterface network = NetworkInterface.getByInetAddress(ip);
		byte[] mac = network.getHardwareAddress();
		System.out.print("Current MAC address : ");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++) {
		    sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
		}
		System.out.println(sb.toString());*/
		
	}
}

