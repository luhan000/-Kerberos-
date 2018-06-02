

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import DES.DES;

public class Asthread implements Runnable{
	String regist="00000";//注册包首部
	String login="00001";//登录认证包首部
	String ctwo="00";
	String cone="0";
	String tgs_key="hellotgs";//AS和TGS之间的对称秘钥
	String tgsid="1";
	String vip="172.20.10.11";
	int vport=4444;
	private Socket socket;
	public  Asthread(Socket socket)
	{
		this.socket=socket;
	}
	@Override
	public void run() {
		final Lock lock = new ReentrantLock();//同一时间只能有一个线程占用数据库修改权限
		try {
			ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
			String data=(String)read.readObject();//AS从客户端接收到的数据包
		    String clfi=data.substring(0, 5);//截取数据包首部
		    String lenght=data.substring(5, 13);//截取有效数据包长度字符串
		    int len=Handledata.getlenght(lenght);
			ResultSet rs=null;
			Connection conn=null;
		    String info=data.substring(13);//截取有效数据段
		    if(clfi.equals(regist))//AS注册处理
		    {
		      String name=info.substring(0,info.length()-8);//得到注册昵称
		      String key=info.substring(name.length());//得到对称加密钥匙
		      lock.lock();
		      conn=Handledata.conmyASsql();
		      String sql="select count(name)  from c_key;";//用户的ID是根据注册顺序得来的第一个注册就位001
		      PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
		      rs=pStmt.executeQuery();
		      rs.next();
		      int id=Integer.parseInt(rs.getString(1))+1;//计算出待注册用户在数据库的ID
		      String idc=String.valueOf(id);//转化为String
		      if(idc.length()==1)
		      {
		    	  idc=ctwo+idc;
		      }
		      else if (idc.length()==2)
		      {
		    	  idc=cone+idc;
		      }
		      sql="insert into c_key values(?,?,?);";
		      pStmt = (PreparedStatement) conn.prepareStatement(sql);
		      pStmt.setString(1, idc);
		      pStmt.setString(2, name);
		      pStmt.setString(3, key);
		      pStmt.executeUpdate();//插入用户信息，注册成功
		      lock.unlock();//数据库操作完成释放权限
		      String sendtov="0100000000001"+idc+name;//发送给V服务器的注册信息
	          Socket vsocket=new Socket(vip,vport);
	          ObjectOutputStream vwrite = new ObjectOutputStream(vsocket.getOutputStream());
	          ObjectInputStream vread = new ObjectInputStream(vsocket.getInputStream());
	          vwrite.writeObject(sendtov);//发送注册信息
	          System.out.println(((String)vread.readObject()).substring(13));//控制台输出注册反馈消息
	          vsocket.close();//操作完成关闭socket
		      String sucback="0001000000001"+idc;
		      write.writeObject(sucback);//注册成功返回序列号
		      socket.close();//操作完成关闭socket
		    } 
		    //返回带有TGS票据的包共76位   其中访问TGS的票据用的是AS和TGS之间的对称钥加密的，由于本次系统只设置了一个TGS所以提取
		    if(clfi.equals(login))//AS登录处理
		    {
		       int lengh=0;
		       String name=info.substring(0,len-20);//登录ID
		       System.out.println(name);
		       lock.lock();
		       conn=Handledata.conmyASsql();//连接数据库
		       String sql="select ckey from c_key where name=?;";//用户的ID是根据注册顺序得来的第一个注册就位001
			   PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			   pStmt.setString(1, name);
			   rs=pStmt.executeQuery();
			   lock.unlock();
			   if(!rs.next())
			   {
				   //发送用户名不存在失败包
				   String fail="00100"+"00000001"+"0";//用户不存在数据包
				   write.writeObject(fail);//发送失败码
			   }
			   else
			   {
			   String ckey=rs.getString(1);//获取对应的对称秘钥
			   sql="select id from c_key where name=?;";
			   pStmt = (PreparedStatement) conn.prepareStatement(sql);
			   pStmt.setString(1, name);
			   lock.lock();
			   rs=pStmt.executeQuery();
			   rs.next();
			   String idc=rs.getString(1);
			   System.out.println("idc："+idc);
			   String ctgskey=Handledata.random8();//随机生成八位临时tgs和C之间的对称秘钥
			   System.out.println("C,TGS之间临时秘钥:"+ctgskey);
			   String curtime=Handledata.gettime();//当前时间
			   String lifetime="600";
			   String ADC=socket.getInetAddress().getHostAddress();//客户端IP  修改
			   System.out.println(ADC);
			   System.out.println("客户端IP"+ADC);
			   String bftgs=ctgskey+idc+ADC+tgsid+curtime+lifetime;
			   System.out.println("TGS票据明文："+bftgs);
			   lengh=bftgs.length();
			   DES destgs=new DES(tgs_key);
			   String tgsticket=destgs.encryp(bftgs);//TGS票据
			   System.out.println("TGS票据："+tgsticket);
			   String mess=ctgskey+idc+tgsid+curtime+lifetime;//返回信息中多加一个用户昵称对应的ID
			   lengh=tgsticket.length();
			   DES desc=new DES(ckey);
			   System.out.println("AS发送给C的数据:"+mess+tgsticket);
			   String message=desc.encryp(mess+tgsticket);//加密后要发送的有效信息
			   System.out.println("AS发送给C的加密数据:"+message);
			   String astoc="00011"+Handledata.getbina(lengh)+message;
			   write.writeObject(astoc);//向客户端发送TGS票据包
			   socket.close();
			   }
		    }
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
