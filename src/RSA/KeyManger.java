package RSA;

import java.math.BigInteger;
import java.security.SecureRandom;


public class KeyManger {
	
	private BigInteger e;
	
	private BigInteger n;
	
	private BigInteger d;
	
	/**
	 * Ĭ�ϵĹ��캯������������2048λ����Կ
	 */
	public KeyManger(){
		this(1024);
	}
	
	/**
	 * �������Ĺ��캯��������2*nbitλ����Կ
	 * @param nbit ����p��q��λ��
	 */
	public KeyManger(int nbit){
		BigInteger p = new BigInteger(nbit,99,new SecureRandom());
		BigInteger q = new BigInteger(nbit,99,new SecureRandom());
		n = q.multiply(p);
		BigInteger fai = q.subtract(new BigInteger("1")).multiply(p.subtract(new BigInteger("1")));
		e = new BigInteger("79");
		d = e.modInverse(fai);
	}
	
	/**
	 * ������e��nƴ�ӳɹ�Կ����
	 * @return ��Կ�ַ���
	 */
	public String getPublicKey(){
		return e.toString()+"&"+n.toString();
	}
	
	/**
	 * ������d��nƴ�ӳ�˽Կ����
	 * @return ˽Կ�ַ���
	 */
	public String getPrivateKey(){
		return d.toString()+"&"+n.toString();
	}
	
	/**
	 * ��ȡ��Կ���е���������e��d
	 * @param keyString ��Կ��˽Կ�ַ���
	 * @return ����������
	 */
	public static String getKeyMain(String keyString){
		String[] result = keyString.split("&");
		if(result.length != 2)
			return null;
		else
			return result[0];
	}
	
	/**
	 * @param keyString ��Կ��˽Կ�ַ���
	 * @return ����N
	 */
	public static String getKeyN(String keyString){
		String[] result = keyString.split("&");
		if(result.length != 2)
			return null;
		else
			return result[1];
	}
}
