package 多线程聊天框架;


import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.locks.*;

public class ThreadSocket implements Runnable{
	private Socket socket;
	public  ThreadSocket(Socket socket)
	{
		this.socket=socket;
	}
	@Override
	public void run() {
		final Lock lock = new ReentrantLock();
		try {
			ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
			while(true)//持续监听消息
			{
			   String info=(String)read.readObject();
			   if(info!=null)
			   {
				   //解析认证包   
				   //if是认证包则用当前SOCEKT返回消息  若发送下线码break;  break 客户端下线则结束进程
				   //if是聊天包
				   lock.lock();
				   //查找所有在线 用户Ip 建立SOCKET发送消息
				   //发送完所有消息
				   lock.unlock();
			   }
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
