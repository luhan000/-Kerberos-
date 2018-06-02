package ���߳�������;


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
			while(true)//����������Ϣ
			{
			   String info=(String)read.readObject();
			   if(info!=null)
			   {
				   //������֤��   
				   //if����֤�����õ�ǰSOCEKT������Ϣ  ������������break;  break �ͻ����������������
				   //if�������
				   lock.lock();
				   //������������ �û�Ip ����SOCKET������Ϣ
				   //������������Ϣ
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
