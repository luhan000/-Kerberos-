package DES;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.util.Base64;
import java.util.Scanner;


public class test {
/*
 加密和解密后的结果都为String
 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		Scanner sc=new Scanner(System.in);
		String password;
		String text;
		System.out.println("输入明文：");
		//text=sc.nextLine();
		System.out.println("输入密码：");
		password="helloser";
		
		DES des = new DES(password);
		// 加密
		System.out.println("加密");
		String clipher="6n8Gl8devbx9wQULP+WrUznWine5FrEe0TU2FTF6wGSQQDSkJxzP0V5i+z07dsGBENIKmR3rbi+Pc1bSNY6OZ7R0xblRI/MKl7gHcvvbyj5pCLRZd7zcFFu518FgC7FD";
		// 解密
		password.substring(10);
		String plain = des.decryp(clipher);
		System.out.println("解密后的结果为：" + plain);
	}
	
}

