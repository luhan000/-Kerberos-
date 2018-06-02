import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


public class RegistUI extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private JLabel back;
	private JLabel usr=new JLabel();
	private JLabel pwd=new JLabel();
	private JLabel jl=new JLabel();
	private JTextField jt = new JTextField("�����û���");		//�������г�ʼ���ı����ı������
	private JPasswordField jp1=new JPasswordField(20);
	private JPasswordField jp2=new JPasswordField(20);
	private JButton x=new JButton();
	
	public RegistUI(){
		this.setResizable(false); 		//�����޸Ĵ�С
		this.getContentPane().setLayout(null);
		this.setTitle("ע��");
		this.setSize(450,350);
		
		//��������λ�ã��ǶԻ������
				Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
				this.setLocation((int)(screenSize.width-350)/2,
						(int)(screenSize.height-600)/2+45);
		
				back=new JLabel();
				ImageIcon icon=new ImageIcon(this.getClass().getResource("regist.png"));
				back.setIcon(icon);
				back.setBounds(0, 0, 450, 350);		
		
		usr.setBounds(95, 40, 80, 50);
		usr.setFont(new Font("����",Font.PLAIN,14));
		usr.setForeground(Color.BLACK);
		usr.setText("�û���:");		
		jt.setForeground(Color.gray);
		jt.setBounds(150, 50, 150, 30);
		jt.setFont(new Font("Serif",Font.PLAIN,12));
		jt.setOpaque(false);
		
		pwd.setBounds(95, 85, 80, 50);
		pwd.setFont(new Font("����",Font.PLAIN,14));
		pwd.setForeground(Color.BLACK);
		pwd.setText("���룺 ");		
		//���������
		jp1.setFont(new Font("Serif",Font.PLAIN,12));
		jp1.setBounds(150, 95, 150, 30);
		jp1.setVisible(true);
		jp1.setOpaque(false);
		
		jl.setBounds(85, 130, 80, 60);
		jl.setFont(new Font("����",Font.PLAIN,14));
		jl.setForeground(Color.BLACK);
		jl.setText("����ȷ�ϣ� ");		
		jp2.setFont(new Font("Serif",Font.PLAIN,12));
		jp2.setBounds(150, 140, 150, 30);
		jp2.setVisible(true);
		jp2.setOpaque(false);

		
		x.setText("����ע��");
		x.setFont(new Font("Dialog",0,12));
		x.setBounds(180,200, 90, 30);
		x.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		x.setBackground(getBackground());
		x.setBackground(Color.white);
		Border b = new LineBorder(Color.white, 2); 
		x.setBorder(b);
		x.setVisible(true);
		
		x.addActionListener(this);
		this.getContentPane().add(jt);
		this.getContentPane().add(usr);
		this.getContentPane().add(pwd);
		this.getContentPane().add(jl);
		this.getContentPane().add(jp1);	
		this.getContentPane().add(jp2);	
		this.getContentPane().add(x);

		this.getContentPane().add(back);
		this.setVisible(true);
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {					
		String usr=jt.getText().toString();	//��ȡ�ı�������					
		String password1 =String.valueOf(jp1.getPassword());	//��ȡ���������			
		String password2 =String.valueOf(jp2.getPassword());	//��ȡ���������					
		String Content=usr+password1+password2;
					
		if(usr.equals("")||password1.equals("")||password2.equals("")){
			//System.out.println("������������Ϣ!");
			JOptionPane.showMessageDialog(null, "������������Ϣ!");
			jp1.setText(null);
	        jp2.setText(null);
			}
		else if(password1.equals(password2)){
			Clientbackhandle newregist=new Clientbackhandle();
			try {
				newregist.registsendToAS(usr, password1);//��̨������
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "ע��ɹ�");
			setVisible(false);			
			}	
		else{
			 JOptionPane.showMessageDialog(null, "������������벻һ�£����������룡");
	         jp1.setText(null);
	         jp2.setText(null);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new RegistUI();
	}
}