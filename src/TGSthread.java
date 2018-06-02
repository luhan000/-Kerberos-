import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import DES.DES;

public class TGSthread implements Runnable{
	
	String con="00101";//�û����͵ķ����� Ʊ�������
	String v_key="helloser";//TGS�ͷ�����֮��ĶԳƼ�����Կ
	String tgs_key="hellotgs";//TGS��AS֮��Գ���Կ
	private Socket socket;
	public  TGSthread(Socket socket)
	{
		this.socket=socket;
	}
	@Override
	public void run(){
		try {
			System.out.println(socket.getInetAddress().getHostAddress());
			int adclen=socket.getInetAddress().getHostAddress().length();//�õ����ͷ���IP��ַ�ĳ���   �޸�
			ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream read = new ObjectInputStream(socket.getInputStream());
			String data=(String)read.readObject();//TGS�ӿͻ��˽��յ������ݰ�
		    String clfi=data.substring(0, 5);//��ȡ���ݰ��ײ�
		    int  lenght=Handledata.getlenght(data.substring(5, 13));//��ȡƱ�ݳ����ַ���
		    String ydata=data.substring(13);
		    String idv=ydata.substring(0,1);//��ȡҪ���ʵķ�����ID
		    int delen=adclen+33;//ticket���ĳ���
			String entgsticket=ydata.substring(1, 1+lenght);//��ȡ���ܺ��ticket
			System.out.println("TGSƱ��:"+entgsticket);
			DES desastgs=new DES(tgs_key);
			String detgsticket=desastgs.decryp(entgsticket);
			System.out.println("����TGSƱ��:"+detgsticket);
			//���ܵ�Ʊ�ݳ���
			String tgsticket=detgsticket.substring(0, delen);//��ȡticket����
			System.out.println("tgsƱ��:"+tgsticket);
			String keyctgs=tgsticket.substring(0,8);//TGS�Ϳͻ���֮�����ʱ��Կ
			System.out.println("c tgs֮�����ʱ��Կ:"+keyctgs+"-"+keyctgs.length());
			String idc=tgsticket.substring(8, 11);//��ȡ�ͻ����û�ID
			String Adct=tgsticket.substring(11, 11+adclen);//��ȡƱ���е�ADC
			System.out.println("Ʊ��IP"+Adct);
			DES desctgs=new DES(keyctgs);
			String enAuth=ydata.substring(1+lenght);//��ȡ���ܺ����֤��
			String deAuth=desctgs.decryp(enAuth);//����
			String Adca=deAuth.substring(3, 3+adclen);//��ȡ��֤�еĿͻ���Ip���жԱ�
			System.out.println("��֤��IP"+Adca);
			if(Adct.equals(Adca))
			{
			  String keycv=Handledata.random8();//������ɷ������Ϳͻ��˵���ʱ��Կ
			  System.out.println("c v֮�����ʱ��Կ:"+keycv);
			  String curtime=Handledata.gettime();
			  String lifetime="600";
			  DES desv=new DES(v_key);
			  String bfticketv=keycv+idc+Adct+idv+curtime+lifetime;//����ǰ��ticketv����
			  System.out.println("����ǰ��ticketv����:"+bfticketv);
			  String ticketv=desv.encryp(bfticketv);//���ܺ��ticketv
			  int len=ticketv.length();
			  System.out.println("���ܺ��ticket:"+ticketv);
			  String bfinfo=keycv+idv+curtime+ticketv;
              DES desctgs1=new DES(keyctgs);
              String info=desctgs1.encryp(bfinfo);
			  System.out.println("���ܱ��Ķ�:"+info+"-"+info.length());
			  System.out.println(desctgs1.decryp(info));
			  String sendinfo="00110"+Handledata.getbina(len)+info;//�����͵ı��Ķ�
			  write.writeObject(sendinfo);//����
			}
			else
			{
				String wrong="00111"+"00000000"+"fail";
				write.writeObject(wrong);//����ʧ����
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
