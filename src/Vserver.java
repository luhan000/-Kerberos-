import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JTextArea;


public class Vserver {
	private int port=4444;//���������ŵĶ˿�
	private ServerSocket server;
	private Socket socket;
    private Queue<ObjectOutputStream> cwrite=new LinkedList<ObjectOutputStream>();//�����û�Socket ���� 
    private Queue<String> chatmess=new LinkedList<String>();//������Ϣ����
	public Vserver(JTextArea text1) throws Exception
	{
		cwrite.clear();
		chatmess.clear();
		init(text1);
	}
    private void init(JTextArea text1) throws Exception {  
        server = new ServerSocket(port);  
        System.out.println("Vserver socket is start, port is: " + port);  
         while(true) {  
            socket = server.accept();  
            System.out.println(socket.getInetAddress().getHostAddress()+socket.getInetAddress().getHostName());
            handle(socket,text1); 
       }  
    }  
    private void handle(Socket socket,JTextArea text) throws Exception {  
        String key = socket.getInetAddress().getHostAddress()+":"+socket.getPort();  
        System.out.println("Vserver accept a socket: " + key);  
        Thread thread = new Thread( new Vserverthread(socket,cwrite,chatmess,text));//�����ݹ����߳�
        thread.start();//���̴߳������Կͻ��˵�����
    }  
}