

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultCaret;


public class ChatUI extends JFrame implements ActionListener{

	/**
	 * 
	 */
	
	static String MACid="19315214";//�趨�����ID
	private int count=0;//��ʶ������Ϣ�ĵ�һ�η���    ֻ�ڵ�һ�η���ʱ����idc��IDc������ǩ����ע����Ϊ�๲�������
	private static final long serialVersionUID = 1L;
	private final JTextArea text1 = new JTextArea();
	private final JTextArea text2 = new JTextArea();
    private final JButton sendMessageButton = new JButton();
    private final JButton exitChatroomButton = new JButton();
    private final JButton getButton = new JButton();
    private final JButton ShowInfoButton = new JButton();
    private final JLabel tip1=new JLabel();
    private final JLabel tip2=new JLabel();
    private JLabel back=new JLabel();
    private final JScrollPane jsp=new JScrollPane(text1);
    private final JScrollPane jsp2=new JScrollPane(text2);
	private Socket socket=null;//���պͷ���������Ϣ��socket(�̶���)
	private String vip="192.168.43.161";
	private String idc;
	private String name;
	ObjectOutputStream write;
	 ObjectInputStream read ;
	public ChatUI(String idc,String name) throws IOException{
		count=0;//ÿ���������� ����count��ֵ
		this.idc=idc;//���ݸ��û�ID
		this.name=name;//���ݸ��û��ǳ�
		this.setResizable(false); 		//�����޸Ĵ�С
		this.getContentPane().setLayout(null);
		this.setTitle("chat");
		this.setBounds(100,100,500,500);
		
		back=new JLabel();
		ImageIcon icon=new ImageIcon(this.getClass().getResource("mainform.png"));
		back.setIcon(icon);
		back.setBounds(0, 0, 500, 500);
		
		tip1.setBounds(0,0, 80, 30);
		tip1.setFont(new Font("����",Font.PLAIN,16));
		tip1.setForeground(Color.BLACK);
		tip1.setText("������Ϣ:");
		
		tip2.setBounds(0,260, 80, 30);
		tip2.setFont(new Font("����",Font.PLAIN,16));
		tip2.setForeground(Color.BLACK);
		tip2.setText("������Ϣ:");
		
		text1.setForeground(Color.black);
		//text1.setBounds(50,300, 410, 200);
		text1.setFont(new Font("Serif",Font.PLAIN,12));
		text1.setLineWrap(true);
		text1.setOpaque(false);
		text1.setEditable(false);
	    
		
		jsp.setBounds(30,30, 410, 200);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setOpaque(false);
		jsp.getViewport().setOpaque(false);
		
		
		text2.setForeground(Color.black);
		//text2.setBounds(30, 300, 410, 100);
		text2.setFont(new Font("Serif",Font.PLAIN,12));
		text2.setLineWrap(true);
		text2.setOpaque(false);
		
		jsp2.setBounds(30,300, 410, 100);
		jsp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsp2.setOpaque(false);
		jsp2.getViewport().setOpaque(false);
		
		sendMessageButton.setText("������Ϣ");
		sendMessageButton.setFont(new Font("Dialog",0,12));
		sendMessageButton.setBounds(10, 410, 100, 28);
		sendMessageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		sendMessageButton.setBackground(getBackground());
		sendMessageButton.setBackground(Color.white);
		Border b = new LineBorder(Color.white, 2); 
		sendMessageButton.setBorder(b);
		sendMessageButton.setVisible(true);

		
		exitChatroomButton.setText("�˳�");
		exitChatroomButton.setFont(new Font("Dialog",0,12));
		exitChatroomButton.setBounds(130, 410, 100, 28);
		exitChatroomButton.setBackground(Color.WHITE);
		exitChatroomButton.setVisible(true);
		exitChatroomButton.setBorder(b);
		exitChatroomButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		getButton.setText("������Ϣ");
		getButton.setFont(new Font("Dialog",0,12));
		getButton.setBounds(250, 410, 100, 28);
		getButton.setBackground(Color.WHITE);
		getButton.setVisible(true);
		getButton.setBorder(b);
		getButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		ShowInfoButton.setText("�����û�");
		ShowInfoButton.setFont(new Font("Dialog",0,12));
		ShowInfoButton.setBounds(360, 410, 100, 28);
		ShowInfoButton.setBackground(Color.WHITE);
		ShowInfoButton.setVisible(true);
		ShowInfoButton.setBorder(b);
		ShowInfoButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		
		sendMessageButton.setMnemonic(java.awt.event.KeyEvent.VK_D);
		sendMessageButton.addActionListener(this);
		exitChatroomButton.setMnemonic(java.awt.event.KeyEvent.VK_F4);
		exitChatroomButton.addActionListener(this);
		getButton.addActionListener(this);
		ShowInfoButton.addActionListener(this);
	
		this.getContentPane().add(jsp);
		this.getContentPane().add(jsp2);
		this.getContentPane().add(sendMessageButton);	
		this.getContentPane().add(exitChatroomButton);
		this.getContentPane().add(getButton);
		this.getContentPane().add(ShowInfoButton);
		this.getContentPane().add(tip1);
		this.getContentPane().add(tip2);
		this.getContentPane().add(back);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		this.setVisible(true);
	    socket=new Socket(vip,4444);
		write = new ObjectOutputStream(socket.getOutputStream());
		read = new ObjectInputStream(socket.getInputStream());
		//
		String dgsign=Clientbackhandle.getdigsign(idc);
		System.out.println("�ͻ��˵ó����Լ�������ǩ�� ��"+dgsign);
		String sendinfo="01010"+"00000000"+MACid+this.idc+""+dgsign;//�����͵���Ϣ
		System.out.println("�����͵���Ϣ ��"+sendinfo);//������Ϣ��֪����������������Ϣ�����׶�
		write.writeObject(sendinfo);
		//
	    new Thread(new CrecvThread(read,text1)).start();//������Ϣ����
	}
	/*public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		new ChatUI();
	}*/
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==sendMessageButton){
			//����Ϣ
			try {
			String mess=Handledata.gettime()+" "+name+":"+"\n"+text2.getText()+"\n";//�õ�������Ϣ��ʽ
			/*if(count==0){
				String dgsign=Clientbackhandle.getdigsign(idc);
				System.out.println("�ͻ��˵ó����Լ�������ǩ�� ��"+dgsign);
				String len=Handledata.getbina(mess.length());//������Ϣ����
				String sendinfo="01010"+len+MACid+idc+mess+dgsign;//�����͵���Ϣ
				System.out.println("�����͵���Ϣ ��"+sendinfo);
				write.writeObject(sendinfo);
				count++;
			}
			else
			{*/
			System.out.println("�����͵���Ϣ��"+mess);
			write.writeObject(mess);
			//}
			text2.setText("");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			}
		else if(e.getSource()==getButton){
			String off = null;
			try {
				Socket osocket=new Socket(vip,4444);
				ObjectOutputStream write = new ObjectOutputStream(osocket.getOutputStream());
				ObjectInputStream read = new ObjectInputStream(osocket.getInputStream());
				String mess="10000"+"00000000"+"000";//���������ֶ�
				write.writeObject(mess);
			    off=(String)read.readObject();
			    osocket.close();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			new Offline(off);
		}
		else if(e.getSource()==ShowInfoButton){
			String on="";
			try {
				Socket osocket=new Socket(vip,4444);
				ObjectOutputStream write = new ObjectOutputStream(osocket.getOutputStream());
				ObjectInputStream read = new ObjectInputStream(osocket.getInputStream());
				String mess="10010"+"00000000"+"000";//���������ֶ�
				write.writeObject(mess);
			    on=(String)read.readObject();
			    osocket.close();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			new Numbers(on);
		}
		else if(e.getSource()==exitChatroomButton)
		{
	    	 try {//ʹ�÷��ͺͽ�����Ϣ��socket������������Ϣ
				String mess="offline";
				/*if(count==0){
					String dgsign=Clientbackhandle.getdigsign(idc);
					System.out.println("�ͻ��˵ó����Լ�������ǩ�� ��"+dgsign);
					String len=Handledata.getbina(mess.length());//������Ϣ����
					String sendinfo="01010"+len+MACid+idc+mess+dgsign;//�����͵���Ϣ
					System.out.println("�����͵���Ϣ ��"+sendinfo);
					write.writeObject(sendinfo);
					count++;
				}
				else
				{*/	
					write.writeObject(mess);//���������ź�
				//}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.exit(0);;//�˳�
		}
	}
}
