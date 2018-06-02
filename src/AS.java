import java.net.ServerSocket;
import java.net.Socket;


public class AS {
	private int port=2222;
	private ServerSocket server;
	private Socket socket;
	public AS() throws Exception
	{
		init();
	}
    private void init() throws Exception {  
        server = new ServerSocket(port);  
        System.out.println("AS socket is start, port is: " + port);  
         while(true) {  
            socket = server.accept();  
            System.out.println(socket.getInetAddress().getHostAddress()+socket.getInetAddress().getHostName());
            handle(socket); 
       }  
    }  
    private void handle(Socket socket) throws Exception {  
        String key = socket.getInetAddress().getHostAddress()+":"+socket.getPort();  
        System.out.println("AS accept a socket: " + key);  
        Thread thread = new Thread(new Asthread(socket));//�����ݹ����߳�
        thread.start();//���̴߳������Կͻ��˵�����
    }  
    public static void main(String []args) throws Exception
    {
    	AS as= new AS();
    }
}
