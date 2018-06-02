package 多线程聊天框架;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	private int port=9999;
	private ServerSocket server;
	private Socket socket;
	public Server() throws Exception
	{
		init();
	}
    private void init() throws Exception {  
        server = new ServerSocket(port);  
        System.out.println("server socket is start, port is: " + port);  
         while(true) {  
            socket = server.accept();  
            System.out.println(socket.getInetAddress().getHostAddress());
            socket.close();
            handle(socket); 
       }  
    }  
    private void handle(Socket socket) throws Exception {  
        String key = socket.getInetAddress().getHostAddress()+":"+socket.getPort();  
        System.out.println("accept a socket: " + key);  
        Thread thread = new Thread(new ThreadSocket(socket));//非数据共享线程
        thread.start();  
    }  
    public static void main(String[] args) throws Exception { 
    	Server server=new Server();
    }  

}
