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
//发送自己的信息，请求与TGS通信
static String MACid="19315214";//设定物理机ID
static String asip="192.168.43.237";//AS服务器固定IP
static String tgsip="192.168.43.15";//TGS服务器固定IP
static String vip="192.168.43.161";//V服务器固定IP
static int asport=2222;
static int tgsport=3333;
static  int tovport=4444;//连接服务器，服务器接收端口
static  int fromvport=5555;//服务器连接 客户端接收端口
static String asfail="00100";//用户不存在包首部
static String tgsticket="00011";//TGS票据包首部
static String vticket="00110";//V票据包首部
static String vconfirm="01011";//服务器返回的同步认证包
static String vfail="10001";//服务器返回的认证失败包
static String tgsid="1";
static String clientid="001";//注册序列号
static String pukey="79&518940563";//公钥
static String prkey="282436519&518940563";//私钥  这是已有的
public static void sendpukey()//发送物理机公钥包
{
	String sendinfo="01111"+"00000100"+MACid+pukey;//发送给V服务器的物理机序列号和公钥
	Socket socket=null;
	try {
	socket = new Socket(vip,tovport);
	ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
	ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
	write.writeObject(sendinfo);//发送
	System.out.println(((String)read.readObject()).substring(13));//反馈结果
	}
	catch (Exception e) {
		System.out.println(e);
	}
	finally {
		try {
	        if (socket!=null) {
	        	//Thread.sleep(10000);
	            socket.close();
	        }//关闭SOCKET
	    } catch (Exception e2) {
	        e2.printStackTrace();
	    }
	}
}
public static int getlenght(String lenght)//将有效数据包长度从二进制字符转化为整形
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

public static String getbina(int lenght)//将十进制转化为八位二进制
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
	   String result=sb.append(part).toString();//MD5密文字符串
	   return result;
}
public void registsendToAS (String ClientID,String password) throws NoSuchAlgorithmException{//注册方法
String temp=gethash(password);
String detail=ClientID+temp.substring(0,8);//提取八位hash后的结构作为对称钥加密的秘钥，也可用户认证用户
String send ="00000"+getbina(detail.length())+detail;//报文结构
//System.out.println(send);
Socket socket=null;
try {
socket = new Socket(asip,asport);
ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
//System.out.println("send "+send);
write.writeObject(send);
String recv = ((String) read.readObject()).substring(13);//注册成功即数据库更新成功返回码
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
        }//注册成功后关闭SOCKET
    } catch (Exception e2) {
        e2.printStackTrace();
    }
}
}
public static String[] LogintoAS(String ClientID,String password) throws NoSuchAlgorithmException//登录AS的方法 返回key(C,tgs)和票据或者用户失败码
{
	String []retu=new String[4];//返回的字符串数组,若AS认证成功则将客户端和TGS之间的临时秘钥和票据以及票据明文长度放入数组作为返回值
	String curtime=Handledata.gettime();
	String mess=ClientID+tgsid+curtime;
	String lengh=Handledata.getbina(mess.length());
	String ctoas="00001"+lengh+mess;
	String ckey=MD5.gethash(password).substring(0, 8);//得到对称加密秘钥
	Socket socket=null;
	try {
		socket = new Socket(asip,asport);
		ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
		write.writeObject(ctoas);
		String recv = (String) read.readObject();
		String clfi=recv.substring(0, 5);//数据包首部
		int len=Handledata.getlenght(recv.substring(5, 13));//票据长度
		String eninfo=recv.substring(13);//得到加密的有效数据包
		System.out.println("AS数据包:"+eninfo);
		//解密
		if(clfi.equals(asfail))//失败包
		{
			retu[0]="用户不存在!";
		}
		else
		{
		   DES des=new DES(ckey);
		   String deinfo=des.decryp(eninfo);//第一次解密
		   System.out.println("c对AS发来的数据包进行解密:"+deinfo);
		   String backtgs=deinfo.substring(11, 12);
		   if(!backtgs.equals(tgsid))
		   {
			   retu[0]="密码错误!";
		   }
		   else
		   {
			   String TGSticket=deinfo.substring(34,34+len);//票据
			   System.out.println("TGS票据："+TGSticket);
			   String idc=deinfo.substring(8,11);
			   System.out.println("用户在数据库的序列号:"+idc);
			   String ctgskey=deinfo.substring(0, 8);//临时秘钥
			   System.out.println("c tgs 临时秘钥："+ctgskey);
			   String le=Handledata.getbina(len);//票据明文的长度
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
	String idv="1";//服务器ID
    //String adc=InetAddress.getLocalHost().getHostAddress();//服务器在异地 得到本地网络地址
	String adc=InetAddress.getLocalHost().getHostAddress();
    System.out.println("local ip"+adc);
    String curtime=Handledata.gettime();//得到当前时间的年月日时分秒格式
    String keyctgs=ctgskey;
    System.out.println("客户端，TGS临时秘钥"+keyctgs);
    DES au=new DES(keyctgs);
    System.out.println(keyctgs.length());
    String bfauth=clientid+adc+curtime;
    System.out.println("发送给TGS的加密前的认证字段："+bfauth);
    String auth=au.encryp(bfauth);//认证数据段
    System.out.println("认证字段："+auth);
    String info=idv+TGSticket+auth;//C TO TGS 有效数据包
    String sendinfo="00101"+len+info;//待发送的完整数据包
	Socket socket=null;
	try {
		socket = new Socket(tgsip,tgsport);
		ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
		write.writeObject(sendinfo);//发送给TGS
		String backtgs=(String) read.readObject();//接收返回的数据包
		String clfi=backtgs.substring(0, 5);//数据包首部
		System.out.println(clfi);
		int lenght=Handledata.getlenght(backtgs.substring(5, 13));//票据长度
		System.out.println("长度"+lenght);
		String backinfo=backtgs.substring(13);//有效数据包
		System.out.println("TGS数据包:"+backinfo);
		if(clfi.equals(vticket))
		{
			String debackinfo=au.decryp(backinfo);
			System.out.println("解密后的TGS报文："+debackinfo);
			retu[0]=debackinfo.substring(0,8);//服务器和客户端之间的临时秘钥
			retu[1]=debackinfo.substring(28,28+lenght);//访问服务器的票据
			retu[2]=Handledata.getbina(lenght);//票据长度
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
	            socket.close();//关闭Socket
	        }
	    } catch (Exception e2) {
	        e2.printStackTrace();
	    }
	}
	return retu;
}
public static String logintov(String clientid,String cvkey,String Vticket,String len) throws UnknownHostException//返回成功认证识别码然后进入聊天线程
{
	String retu="";
	DES des=new DES(cvkey);//服务器和客户端临时秘钥
    //String adc=InetAddress.getLocalHost().getHostAddress();//服务器在异地 得到本地网络地址
	String adc=InetAddress.getLocalHost().getHostAddress();
	String ts5=Handledata.gettime();//同步时间段
	String bfauth=clientid+adc+ts5;
	System.out.println("加密前的认证字段:"+bfauth);
	String auth=des.encryp(bfauth);//得到认证字段
	System.out.println("加密后的认证字段:"+auth);
	String sendinfo="01001"+len+Vticket+auth;//待发送的服务器认证包
	Socket socket=null;
	try {
		socket = new Socket(vip,tovport);
		ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
		write.writeObject(sendinfo);//发送信息
		String sendback=(String)read.readObject();//接收反馈消息
		String clfi=sendback.substring(0, 5);//数据包首部
		String lengh=sendback.substring(5,13);//数据包长度
		String info=sendback.substring(13);//有效信息段
		System.out.println("从服务器反馈的消息:"+info);
		if(clfi.equals(vconfirm))//返回的认证成功同步认证包
		{
			System.out.println("接收到v返回的加密字段"+info);
			String deinfo=des.decryp(info);//对有效数据段进行解密
			System.out.println("解密结果"+deinfo);
			if(deinfo.substring(0, 19).equals(ts5))
			{
				retu="1";
			}
		}
		else//返回的认证失败包
		{
			retu=info;//认证失败字段 
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
	            socket.close();//关闭Socket
	        }
	    } catch (Exception e2) {
	        e2.printStackTrace();
	    }
	}
	return retu;
}
public static String getdigsign(String idc)//获取数字签名
{
	String sign="";
	byte[]en=idc.getBytes();
	sign=new String(Base64.getEncoder().encodeToString(RSA.encrypt(prkey, en)));
	return sign;
}
}

