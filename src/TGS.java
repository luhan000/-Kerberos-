import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

public class TGS {
	private int port=3333;
	private ServerSocket server;
	private Socket socket;
	public TGS() throws Exception
	{
		init();
	}
    private void init() throws Exception {  
        server = new ServerSocket(port);  
        System.out.println("TGS socket is start, port is: " + port);  
         while(true) {  
            socket = server.accept();  
            System.out.println(socket.getInetAddress().getHostAddress());
            handle(socket); 
       }  
    }  
    private void handle(Socket socket) throws Exception {  
        String key = socket.getInetAddress().getHostAddress()+":"+socket.getPort();  
        System.out.println("TGS accept a socket: " + key);  
        Thread thread = new Thread(new TGSthread(socket));//非数据共享线程
        thread.start();//多线程处理来自客户端的请求
    }  
    public static void main(String []args) throws Exception
    {
    	TGS tgs=new TGS();
    }

}
