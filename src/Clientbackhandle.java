import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import DES.DES;
import MD5.MD5;
import RSA.RSA;



public class Clientbackhandle{ 
//�����Լ�����Ϣ��������TGSͨ��
static String MACid="19315214";//�趨�����ID
static String asip="192.168.43.237";//AS�������̶�IP
static String tgsip="192.168.43.15";//TGS�������̶�IP
static String vip="192.168.43.161";//V�������̶�IP
static int asport=2222;
static int tgsport=3333;
static  int tovport=4444;//���ӷ����������������ն˿�
static  int fromvport=5555;//���������� �ͻ��˽��ն˿�
static String asfail="00100";//�û������ڰ��ײ�
static String tgsticket="00011";//TGSƱ�ݰ��ײ�
static String vticket="00110";//VƱ�ݰ��ײ�
static String vconfirm="01011";//���������ص�ͬ����֤��
static String vfail="10001";//���������ص���֤ʧ�ܰ�
static String tgsid="1";
static String clientid="001";//ע�����к�
static String pukey="79&518940563";//��Կ
static String prkey="282436519&518940563";//˽Կ  �������е�
public static void sendpukey()//�����������Կ��
{
	String sendinfo="01111"+"00000100"+MACid+pukey;//���͸�V����������������кź͹�Կ
	Socket socket=null;
	try {
	socket = new Socket(vip,tovport);
	ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
	ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
	write.writeObject(sendinfo);//����
	System.out.println(((String)read.readObject()).substring(13));//�������
	}
	catch (Exception e) {
		System.out.println(e);
	}
	finally {
		try {
	        if (socket!=null) {
	        	//Thread.sleep(10000);
	            socket.close();
	        }//�ر�SOCKET
	    } catch (Exception e2) {
	        e2.printStackTrace();
	    }
	}
}
public static int getlenght(String lenght)//����Ч���ݰ����ȴӶ������ַ�ת��Ϊ����
{
   int len=0;
   String str=lenght;
   int temp;
   for(int i=0;i<8;i++)
   {
	   temp=str.charAt(7-i)-48;
	   len=(int) (len+temp*Math.pow(2,i));
   }
   return len;
}

public static String getbina(int lenght)//��ʮ����ת��Ϊ��λ������
{
	int temp=lenght;
	String com="0";
	String fina="";
	String trans=java.lang.Integer.toBinaryString(temp);
	for(int i=0;i<8-trans.length();i++)
	{
		fina+=com;
	}
	fina+=trans;
	return fina;
}

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
	   String result=sb.append(part).toString();//MD5�����ַ���
	   return result;
}
public void registsendToAS (String ClientID,String password) throws NoSuchAlgorithmException{//ע�᷽��
String temp=gethash(password);
String detail=ClientID+temp.substring(0,8);//��ȡ��λhash��Ľṹ��Ϊ�Գ�Կ���ܵ���Կ��Ҳ���û���֤�û�
String send ="00000"+getbina(detail.length())+detail;//���Ľṹ
//System.out.println(send);
Socket socket=null;
try {
socket = new Socket(asip,asport);
ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
//System.out.println("send "+send);
write.writeObject(send);
String recv = ((String) read.readObject()).substring(13);//ע��ɹ������ݿ���³ɹ�������
if (recv!=null)
{
	clientid=recv;
	System.out.println("Client have recesived :"+recv);
}
}
catch (Exception e) {
	System.out.println(e);
}
finally {
	try {
        if (socket!=null) {
        	//Thread.sleep(10000);
            socket.close();
        }//ע��ɹ���ر�SOCKET
    } catch (Exception e2) {
        e2.printStackTrace();
    }
}
}
public static String[] LogintoAS(String ClientID,String password) throws NoSuchAlgorithmException//��¼AS�ķ��� ����key(C,tgs)��Ʊ�ݻ����û�ʧ����
{
	String []retu=new String[4];//���ص��ַ�������,��AS��֤�ɹ��򽫿ͻ��˺�TGS֮�����ʱ��Կ��Ʊ���Լ�Ʊ�����ĳ��ȷ���������Ϊ����ֵ
	String curtime=Handledata.gettime();
	String mess=ClientID+tgsid+curtime;
	String lengh=Handledata.getbina(mess.length());
	String ctoas="00001"+lengh+mess;
	String ckey=MD5.gethash(password).substring(0, 8);//�õ��ԳƼ�����Կ
	Socket socket=null;
	try {
		socket = new Socket(asip,asport);
		ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
		write.writeObject(ctoas);
		String recv = (String) read.readObject();
		String clfi=recv.substring(0, 5);//���ݰ��ײ�
		int len=Handledata.getlenght(recv.substring(5, 13));//Ʊ�ݳ���
		String eninfo=recv.substring(13);//�õ����ܵ���Ч���ݰ�
		System.out.println("AS���ݰ�:"+eninfo);
		//����
		if(clfi.equals(asfail))//ʧ�ܰ�
		{
			retu[0]="�û�������!";
		}
		else
		{
		   DES des=new DES(ckey);
		   String deinfo=des.decryp(eninfo);//��һ�ν���
		   System.out.println("c��AS���������ݰ����н���:"+deinfo);
		   String backtgs=deinfo.substring(11, 12);
		   if(!backtgs.equals(tgsid))
		   {
			   retu[0]="�������!";
		   }
		   else
		   {
			   String TGSticket=deinfo.substring(34,34+len);//Ʊ��
			   System.out.println("TGSƱ�ݣ�"+TGSticket);
			   String idc=deinfo.substring(8,11);
			   System.out.println("�û������ݿ�����к�:"+idc);
			   String ctgskey=deinfo.substring(0, 8);//��ʱ��Կ
			   System.out.println("c tgs ��ʱ��Կ��"+ctgskey);
			   String le=Handledata.getbina(len);//Ʊ�����ĵĳ���
			   retu[0]=ctgskey;
			   retu[1]=TGSticket;
			   retu[2]=le;
			   retu[3]=idc;
		   }
		}
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	finally {
		try {
	        if (socket!=null) {
	        	//Thread.sleep(10000);
	            socket.close();
	        }
	    } catch (Exception e2) {
	        e2.printStackTrace();
	    }
	}
	return retu;
}
public static String[] logintotgs(String clientid,String ctgskey,String TGSticket,String len) throws UnknownHostException
{
	String []retu=new String[3];
	String idv="1";//������ID
    //String adc=InetAddress.getLocalHost().getHostAddress();//����������� �õ����������ַ
	String adc=InetAddress.getLocalHost().getHostAddress();
    System.out.println("local ip"+adc);
    String curtime=Handledata.gettime();//�õ���ǰʱ���������ʱ�����ʽ
    String keyctgs=ctgskey;
    System.out.println("�ͻ��ˣ�TGS��ʱ��Կ"+keyctgs);
    DES au=new DES(keyctgs);
    System.out.println(keyctgs.length());
    String bfauth=clientid+adc+curtime;
    System.out.println("���͸�TGS�ļ���ǰ����֤�ֶΣ�"+bfauth);
    String auth=au.encryp(bfauth);//��֤���ݶ�
    System.out.println("��֤�ֶΣ�"+auth);
    String info=idv+TGSticket+auth;//C TO TGS ��Ч���ݰ�
    String sendinfo="00101"+len+info;//�����͵��������ݰ�
	Socket socket=null;
	try {
		socket = new Socket(tgsip,tgsport);
		ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
		write.writeObject(sendinfo);//���͸�TGS
		String backtgs=(String) read.readObject();//���շ��ص����ݰ�
		String clfi=backtgs.substring(0, 5);//���ݰ��ײ�
		System.out.println(clfi);
		int lenght=Handledata.getlenght(backtgs.substring(5, 13));//Ʊ�ݳ���
		System.out.println("����"+lenght);
		String backinfo=backtgs.substring(13);//��Ч���ݰ�
		System.out.println("TGS���ݰ�:"+backinfo);
		if(clfi.equals(vticket))
		{
			String debackinfo=au.decryp(backinfo);
			System.out.println("���ܺ��TGS���ģ�"+debackinfo);
			retu[0]=debackinfo.substring(0,8);//�������Ϳͻ���֮�����ʱ��Կ
			retu[1]=debackinfo.substring(28,28+lenght);//���ʷ�������Ʊ��
			retu[2]=Handledata.getbina(lenght);//Ʊ�ݳ���
		}
		else
		{  
			System.out.println("else");
			retu[0]=backinfo;
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	finally {
		try {
	        if (socket!=null) {
	        	//Thread.sleep(10000);
	            socket.close();//�ر�Socket
	        }
	    } catch (Exception e2) {
	        e2.printStackTrace();
	    }
	}
	return retu;
}
public static String logintov(String clientid,String cvkey,String Vticket,String len) throws UnknownHostException//���سɹ���֤ʶ����Ȼ����������߳�
{
	String retu="";
	DES des=new DES(cvkey);//�������Ϳͻ�����ʱ��Կ
    //String adc=InetAddress.getLocalHost().getHostAddress();//����������� �õ����������ַ
	String adc=InetAddress.getLocalHost().getHostAddress();
	String ts5=Handledata.gettime();//ͬ��ʱ���
	String bfauth=clientid+adc+ts5;
	System.out.println("����ǰ����֤�ֶ�:"+bfauth);
	String auth=des.encryp(bfauth);//�õ���֤�ֶ�
	System.out.println("���ܺ����֤�ֶ�:"+auth);
	String sendinfo="01001"+len+Vticket+auth;//�����͵ķ�������֤��
	Socket socket=null;
	try {
		socket = new Socket(vip,tovport);
		ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
		write.writeObject(sendinfo);//������Ϣ
		String sendback=(String)read.readObject();//���շ�����Ϣ
		String clfi=sendback.substring(0, 5);//���ݰ��ײ�
		String lengh=sendback.substring(5,13);//���ݰ�����
		String info=sendback.substring(13);//��Ч��Ϣ��
		System.out.println("�ӷ�������������Ϣ:"+info);
		if(clfi.equals(vconfirm))//���ص���֤�ɹ�ͬ����֤��
		{
			System.out.println("���յ�v���صļ����ֶ�"+info);
			String deinfo=des.decryp(info);//����Ч���ݶν��н���
			System.out.println("���ܽ��"+deinfo);
			if(deinfo.substring(0, 19).equals(ts5))
			{
				retu="1";
			}
		}
		else//���ص���֤ʧ�ܰ�
		{
			retu=info;//��֤ʧ���ֶ� 
		}
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	finally {
		try {
	        if (socket!=null) {
	        	//Thread.sleep(10000);
	            socket.close();//�ر�Socket
	        }
	    } catch (Exception e2) {
	        e2.printStackTrace();
	    }
	}
	return retu;
}
public static String getdigsign(String idc)//��ȡ����ǩ��
{
	String sign="";
	byte[]en=idc.getBytes();
	sign=new String(Base64.getEncoder().encodeToString(RSA.encrypt(prkey, en)));
	return sign;
}
}

