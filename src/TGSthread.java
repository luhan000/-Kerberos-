import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import DES.DES;

public class TGSthread implements Runnable{
	
	String con="00101";//用户发送的服务器 票据请求包
	String v_key="helloser";//TGS和服务器之间的对称加密秘钥
	String tgs_key="hellotgs";//TGS和AS之间对称秘钥
	private Socket socket;
	public  TGSthread(Socket socket)
	{
		this.socket=socket;
	}
	@Override
	public void run(){
		try {
			System.out.println(socket.getInetAddress().getHostAddress());
			int adclen=socket.getInetAddress().getHostAddress().length();//得到发送方的IP地址的长度   修改
			ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
			String data=(String)read.readObject();//TGS从客户端接收到的数据包
		    String clfi=data.substring(0, 5);//截取数据包首部
		    int  lenght=Handledata.getlenght(data.substring(5, 13));//截取票据长度字符串
		    String ydata=data.substring(13);
		    String idv=ydata.substring(0,1);//获取要访问的服务器ID
		    int delen=adclen+33;//ticket明文长度
			String entgsticket=ydata.substring(1, 1+lenght);//获取加密后的ticket
			System.out.println("TGS票据:"+entgsticket);
			DES desastgs=new DES(tgs_key);
			String detgsticket=desastgs.decryp(entgsticket);
			System.out.println("解密TGS票据:"+detgsticket);
			//解密的票据长度
			String tgsticket=detgsticket.substring(0, delen);//获取ticket明文
			System.out.println("tgs票据:"+tgsticket);
			String keyctgs=tgsticket.substring(0,8);//TGS和客户端之间的临时秘钥
			System.out.println("c tgs之间的临时秘钥:"+keyctgs+"-"+keyctgs.length());
			String idc=tgsticket.substring(8, 11);//获取客户端用户ID
			String Adct=tgsticket.substring(11, 11+adclen);//获取票据中的ADC
			System.out.println("票据IP"+Adct);
			DES desctgs=new DES(keyctgs);
			String enAuth=ydata.substring(1+lenght);//获取加密后的认证包
			String deAuth=desctgs.decryp(enAuth);//解密
			String Adca=deAuth.substring(3, 3+adclen);//获取认证中的客户端Ip进行对比
			System.out.println("认证包IP"+Adca);
			if(Adct.equals(Adca))
			{
			  String keycv=Handledata.random8();//随机生成服务器和客户端的临时秘钥
			  System.out.println("c v之间的临时秘钥:"+keycv);
			  String curtime=Handledata.gettime();
			  String lifetime="600";
			  DES desv=new DES(v_key);
			  String bfticketv=keycv+idc+Adct+idv+curtime+lifetime;//加密前的ticketv明文
			  System.out.println("加密前的ticketv明文:"+bfticketv);
			  String ticketv=desv.encryp(bfticketv);//加密后的ticketv
			  int len=ticketv.length();
			  System.out.println("加密后的ticket:"+ticketv);
			  String bfinfo=keycv+idv+curtime+ticketv;
              DES desctgs1=new DES(keyctgs);
              String info=desctgs1.encryp(bfinfo);
			  System.out.println("加密报文段:"+info+"-"+info.length());
			  System.out.println(desctgs1.decryp(info));
			  String sendinfo="00110"+Handledata.getbina(len)+info;//待发送的报文段
			  write.writeObject(sendinfo);//发送
			}
			else
			{
				String wrong="00111"+"00000000"+"fail";
				write.writeObject(wrong);//发送失败码
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
