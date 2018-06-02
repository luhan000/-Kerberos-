import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextArea;

import DES.DES;
import RSA.RSA;

public class Vserverthread implements Runnable {
	private Socket socket;
	String mcpkey="01111";//物理机公钥包
	String vregist="01000";//注册包
	String vconfirm="01001";//服务器认证包
	String vchat="01010";//服务器聊天包
	String voffchat="10000";//离线消息请求包
	String conline="10010";//在线人数请求包
	String vkey="helloser";//秘钥
	private Queue<ObjectOutputStream> cwrite;//在线用户Socket集合
	private Queue<String> chatmess;//离线消息队列
	JTextArea text1;
	public  Vserverthread(Socket socket,Queue<ObjectOutputStream> cwrite,Queue<String> chatmess,JTextArea text1)
	{
		this.socket=socket;
		this.cwrite=cwrite;
		this.chatmess=chatmess;
		this.text1=text1;
	}
    //注册包来自AS
	//认证包来自客户端
	//聊天包来自客户端
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			final Lock lock = new ReentrantLock();//同一时间只能有一个线程占用数据库修改权限
			String adc=socket.getInetAddress().getHostAddress();//socket对应的IP地址
			int adclen=socket.getInetAddress().getHostAddress().length();//IP地址长度
			System.out.println("IP长度："+adclen);
			ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
			String data=(String)read.readObject();//读取来自socket信息
			String clfi=data.substring(0, 5);//读取数据包的头
			System.out.println("数据包头"+clfi);
			String lengh=data.substring(5,13);//长度，长度描述根据数据包首部来确定
			String info=data.substring(13);//读取有效数据包
			System.out.println("有效数据包:"+info);
			if(clfi.equals(mcpkey))//由于clfi的值不会变化 所以可以采用多重IF结构
			{
				//物理机公钥包
				String sendback="";
				String MACid=info.substring(0,8);//MAC地址
				String pukey=info.substring(8);//公钥
				ResultSet rs=null;
				Connection conn=null;  
				lock.lock();
				conn=Handledata.conmyVsql();//连接V服务器数据库
				String sql="select pkey  from mac_key where macid=?;";//查询客户端物理机公钥是否在数据库
			    PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setString(1, MACid);//传入参数
			    rs=pStmt.executeQuery();//接收查询结果
			    if(!rs.next())//物理机序号和公钥不存在数据库
			    {
			    	sql="insert into mac_key values(?,?);";
			        pStmt=(PreparedStatement) conn.prepareStatement(sql);
			        pStmt.setString(1, MACid);//传入参数
			        pStmt.setString(2, pukey);
			        pStmt.executeUpdate();//插入数据库
			        sendback="1111100000000"+"already inserted into database!";
			        write.writeObject(sendback);//反馈消息
			    }
			    else
			    {
			        sendback="1111100000000"+"have existed!";
			        write.writeObject(sendback);//反馈消息
			    }
			    lock.unlock();
			}
            if(clfi.equals(vregist))
            {
            	//注册包
            	String idc=info.substring(0,3);//注册序列号ID
            	String name=info.substring(3);//注册昵称
                String date=Handledata.gettime();
                String regist=idc+" "+name+" 在"+date+"注册";
                System.out.println("注册信息: "+regist);
                text1.setText(regist);//实时显示注册消息
                lock.lock();//集合线程安全操作
				String sendback="";
				ResultSet rs=null;
				Connection conn=null;  
				conn=Handledata.conmyVsql();//连接V服务器数据库
				String sql="insert into new_table values(?,?,?,?);";//插入信息到数据库
			    PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setString(1, idc);//传入参数
			    pStmt.setString(2, name);
			    pStmt.setString(3, "");
			    pStmt.setInt(4, 0);
			    pStmt.executeUpdate();//执行语句
			    lock.unlock();
			    sendback="1111100000000"+"regist succed!";//反馈消息
			    write.writeObject(sendback);
            }
            if(clfi.equals(vconfirm))
            {
            	//认证包
            	DES vdes=new DES(vkey);
            	String enticket=info.substring(0, Handledata.getlenght(lengh));//票据字段
            	System.out.println("V票据："+enticket);
            	String deticket=vdes.decryp(enticket);//解密票据包
            	System.out.println("解密V票据："+deticket);
            	String idc=deticket.substring(8, 11);//用户ID
            	System.out.println("客户端ID："+idc);
            	String cvkey=deticket.substring(0, 8);//服务器客户端临时钥匙
            	System.out.println("服务器客户端临时钥匙:"+cvkey);
            	String adct=deticket.substring(11,11+adclen);//票据客户端IP
            	System.out.println("v 票字段的IP："+adct);
            	String enau=info.substring(Handledata.getlenght(lengh));//认证字段
            	System.out.println("认证字段："+enau);
            	DES audes=new DES(cvkey);
            	String deau=audes.decryp(enau);//解密认证字段
            	System.out.println("解密后的认证字段："+deau);
            	String adcau=deau.substring(3,3+adclen);//认证字段客户端IP
            	System.out.println("认证字段的IP："+adct);
            	String ts5=deau.substring(3+adclen);//同步时间字段
				ResultSet rs=null;
				Connection conn=null;
        		conn=Handledata.conmyVsql();//连接服务器数据库
        		String sql="select state from new_table where id=?;";
			    PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setString(1, idc);
			    rs=pStmt.executeQuery();
			    rs.next();
			    int state=rs.getInt(1);
			    System.out.println(state);
			    if(state==1)
			    {
			    	String sendfinfo="10001"+"00000000"+"重复登录!";//待发送数据包
            		write.writeObject(sendfinfo);//返回认证失败包
			    }
			    else
			    {
            	if(adct.equals(adcau))//认证成功
            	{
            		String back=ts5+"1";
            		System.out.println("返回的加密前的同步字段"+back);
            		String enback=audes.encryp(back);//加密
            		System.out.println("加密的同步字段"+back);
            		String sendfinfo="01011"+"00000000"+enback;//待发送数据包
            		write.writeObject(sendfinfo);
            		//修改数据库该用户在线状态
            		//lock.lock();
    				/*ResultSet rs=null;
    				Connection conn=null;
            		conn=Handledata.conmyVsql();//连接服务器数据库*/
            		sql="update new_table set state=? where id=?;";//修改个人在线状态到数据库
            		pStmt = (PreparedStatement) conn.prepareStatement(sql);
    			    pStmt.setInt(1, 1);
    			    pStmt.setString(2, idc);
    			    pStmt.executeUpdate();//执行语句
    			    sql="select name from new_table where id=?;";
    			    pStmt=(PreparedStatement) conn.prepareStatement(sql);
    			    pStmt.setString(1, idc);
    			    rs=pStmt.executeQuery();
    			    rs.next();
    			    String name=rs.getString(1);
    			    //lock.unlock();
    			    //群发某某加入聊天
    			    ObjectOutputStream bwrite=null;
    			    String mess="";
    			    System.out.println("socket write队列大小:"+cwrite.size());
    			    if(cwrite.size()!=0)
    			    {
    			    int k=cwrite.size();
    			    System.out.println("进程:"+Thread.currentThread().getId());
    			    lock.lock();
                    for(int i=0;i<k;i++)
                    {
                    	bwrite=cwrite.poll();
                    	mess=name+" 加入了聊天室\n";
                    	System.out.println(mess);
                    	bwrite.writeObject(mess);
                    	cwrite.offer(bwrite);
                    }
                    lock.unlock();
    			    }
                    //csocket.offer(socket);//将登陆socket加入在线socket队列
            	}
            	else//认证失败
            	{
            		String sendfinfo="10001"+"00000000"+"服务器认证失败";//待发送数据包
            		write.writeObject(sendfinfo);//返回认证失败包
            	}
			    }
            }
            if(clfi.equals(vchat))
            {
            	//聊天包,数字签名确认身份,持续监听接收消息       群发消息,接收下线数据包  关闭socket、
            	cwrite.offer(write);
            	String macid=info.substring(0,8);//从哪个 物理机发送来的
            	int len=Handledata.getlenght(lengh);//第一次数据中聊天信息的长度
            	String idc=info.substring(8,11);//用户对应的ID
            	String dgsign=info.substring(11+len);//数字签名
            	System.out.println("数字签名 :"+dgsign);
        		lock.lock();
				ResultSet rs=null;
				Connection conn=null;
        		conn=Handledata.conmyVsql();//连接服务器数据库查找公钥
        		String sql="select pkey from mac_key where macid=?;";//查询语句
			    PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setString(1, macid);//传入参数
			    rs=pStmt.executeQuery();
			    rs.next();
			    String pkey=rs.getString(1);//对应的公钥
			    System.out.println("客户端公钥:"+pkey);
			    sql="select name from new_table where id=?;";
			    pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setString(1, idc);//传入参数
			    rs=pStmt.executeQuery();
			    rs.next();
			    String cname=rs.getString(1);
			    System.out.println("客户昵称："+cname);
			    lock.unlock();
			    String deidc=new String(RSA.decrypt(pkey, Base64.getDecoder().decode(dgsign.getBytes())));//解密后的签名
			    System.out.println("解密后的签名"+deidc);
			    if(deidc.equals(idc))//数字签名认证成功
			    {
			      /*if(mess.equals("offline"))
			      {
				        lock.lock();
				        sql="update new_table set state=? where name=?;";
				        pStmt = (PreparedStatement) conn.prepareStatement(sql);
				        pStmt.setInt(1, 0);//传入参数
				        pStmt.setString(2, cname);//传入用户名
				        pStmt.executeUpdate();//执行语句
				        lock.unlock();
				    	for(int i=0;i<csocket.size();i++)//在队列中删除该socket
				    	{
				    		Socket comsocket=csocket.poll();
				    		if(!comsocket.equals(socket))
				    		{
				    			ObjectOutputStream bwrite = new ObjectOutputStream(comsocket.getOutputStream());
				    			bwrite.writeObject(cname+"quit the chatroom!");//向各个在线用户发送某某离线的提示信息
				    			csocket.offer(comsocket);
				    		}
				    	}
			      }
			      else
			      {*/
			       //群发第一次消息
			     //System.out.println("在线用户个数:"+csocket.size());
			     /* for(int i=0;i<count;i++)
			      {
			    		Socket comsocket=csocket.poll();
			    		System.out.println(comsocket.getInetAddress().getHostAddress());
			    		ObjectOutputStream bwrite;
			    		if(!comsocket.equals(socket))
			    		{
                           bwrite = new ObjectOutputStream(comsocket.getOutputStream());
			    		}
			    		else
			    		{
			    			System.out.println("bendi");
			    			bwrite=write;
			    		}
                        System.out.println("first!");
			    	    bwrite.writeObject(mess);//向各个在线用户发送某某离线的提示信息
			    	    System.out.println("send!");
			    	    csocket.offer(comsocket);
			    	    System.out.println(i);
			      }*/
			      int count;
		    	  System.out.println("在线用户个数:"+cwrite.size());
		    	  String chatme="";
			      while(true)//持续接听消息直至接收到离线消息
			      {
			    	 chatme=(String)read.readObject();//第一次认证成功后，后续发来的包除离线消息除外都是只有聊天消息的格式
			    	 System.out.println("进程号:"+Thread.currentThread().getId());
				     count=cwrite.size();
			    	 if(chatme.equals("offline"))//后续发送的离线消息的标识符
			    	 {
					        sql="update new_table set state=? where name=?;";
					        pStmt = (PreparedStatement) conn.prepareStatement(sql);
					        pStmt.setInt(1, 0);//传入参数
					        pStmt.setString(2, cname);//传入用户名
					        pStmt.executeUpdate();//执行语句
					        lock.lock();
					        count=cwrite.size();
					        ObjectOutputStream bwrite=null;
					    	for(int i=0;i<count;i++)//在队列中删除该socket
					    	{
					    		  bwrite=cwrite.poll();
					    		  if(!bwrite.equals(write))
					    		  {
					    			  bwrite.writeObject(cname+" 离开了聊天室!\n");
					    			  cwrite.offer(bwrite);
					    		  }
					    		  else
					    		  {
                                  bwrite.writeObject("0");//返回下线消息
					    		  }
					    	}
					    	lock.unlock();
					    	break;
			    	 }
			    	 else
			    	 {
				    	 System.out.println("接收到的聊天消息:"+chatmess);
			    		  chatmess.offer(chatme);//更新离线消息队列
			    		  System.out.println("群发消息");
			    		  lock.lock();
			    		  count=cwrite.size();
			    		  System.out.println("在线人数"+count);
					      ObjectOutputStream bwrite=null;
					      for(int i=0;i<count;i++)
					      {
					    	    bwrite=cwrite.poll();
					    	    bwrite.writeObject(chatme);//向各个在线用户发送聊天消息
					    	    cwrite.offer(bwrite);
					    	    System.out.println("fasong"+i);
					      }
					      lock.unlock();
			    	 }
			      }
			      }
			    else
			    {
			    	
			    }
            }
            //}
            if(clfi.equals(voffchat))
            {
            	//离线消息请求包
            	String off="";
            	for(String x: chatmess)//遍历所有消息相加
            	{
            		off=off+x;
            	}
            	System.out.println(off);
            	write.writeObject(off);
            }
            if(clfi.equals(conline))
            {
            	//在线人数请求包
            	System.out.println("在线人数请求!");
            	String online="";
				ResultSet rs=null;
				Connection conn=null;  
				lock.lock();
				conn=Handledata.conmyVsql();//连接V服务器数据库
				String sql="select name  from new_table where state=?;";//查询客户端物理机公钥是否在数据库
			    PreparedStatement pStmt = (PreparedStatement) conn.prepareStatement(sql);
			    pStmt.setInt(1, 1);
			    rs=pStmt.executeQuery();
			    while(rs.next())
			    {
			      System.out.println(rs.getString(1));
			      online=online+rs.getString(1)+"\n";//顺序输出名字
			    }
			    write.writeObject(online);
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
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
	}
}
