
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


public class ClientmainUI extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel back=new JLabel();
	private JLabel jt1=new JLabel();
	private JLabel jt2=new JLabel();
	private JTextField jt = new JTextField("输入用户名");		//创建带有初始化文本的文本框对象
	private JPasswordField jp=new JPasswordField(20);
	private JButton xa=new JButton();
	private JButton xb=new JButton();
	private static int stime;
	public ClientmainUI(){
		this.setResizable(false); 		//不能修改大小
		this.getContentPane().setLayout(null);
		this.setTitle("登陆");
		this.setSize(450,350);
		//设置运行位置，是对话框居中
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((int)(screenSize.width-350)/2,(int)(screenSize.height-600)/2+45);
		
		back=new JLabel();
		ImageIcon icon=new ImageIcon(this.getClass().getResource("login.png"));
		back.setIcon(icon);
		back.setBounds(0, 0, 450, 350);
		
		jt.setForeground(Color.black);
		jt.setBounds(95, 100, 150, 30);
		jt.setFont(new Font("Serif",Font.PLAIN,12));
		jt.setOpaque(false);
		
		
		jt1.setBounds(40, 90, 80, 50);
		jt1.setFont(new Font("黑体",Font.PLAIN,16));
		jt1.setForeground(Color.BLACK);
		jt1.setText("用户名:");
		
		//创建密码框
		jp.setFont(new Font("Serif",Font.PLAIN,12));
		jp.setBounds(95, 150, 150, 30);
		jp.setVisible(true);
		jp.setOpaque(false);
		
		jt2.setBounds(40, 140, 80, 50);
		jt2.setFont(new Font("黑体",Font.PLAIN,16));
		jt2.setForeground(Color.BLACK);
		jt2.setText("密码： ");
		
		xa.setText("登陆");
		xa.setFont(new Font("Dialog",0,12));
		xa.setBounds(80, 200, 100, 30);
		xa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		xa.setBackground(getBackground());
		xa.setBackground(Color.white);
		Border b = new LineBorder(Color.white, 2); 
		xa.setBorder(b);
		xa.setVisible(true);

		
		xb.setText("注册");
		xb.setFont(new Font("Dialog",0,12));
		xb.setBounds(185, 200, 100, 30);
		xb.setBackground(Color.WHITE);
		xb.setVisible(true);
		xb.setBorder(b);
		xb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		xa.registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),JComponent.WHEN_IN_FOCUSED_WINDOW);
		xa.addActionListener(this);//添加事件监听
		xb.addActionListener(this);
		
	
		this.getContentPane().add(jt);
		this.getContentPane().add(jt1);
		this.getContentPane().add(jt2);
		this.getContentPane().add(jp);	
		this.getContentPane().add(xa);
		this.getContentPane().add(xb);
		
		this.getContentPane().add(back);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	    if(stime!=1)
	    {
		Clientbackhandle.sendpukey();//发送物理机公钥
		stime=1;
	    }
		new ClientmainUI();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {//时间触发
		// TODO Auto-generated method stub
		if(e.getSource()==xa){//点击的按钮是登录	
			
			String usr=jt.getText().toString();	//获取文本框内容			
			char[] passwords = jp.getPassword();			
			String password =String.valueOf(passwords);	//获取密码框内容
			
			String Content=usr+password;
			
			if(usr.equals("")||password.equals("")){
				//System.out.println("请输入完整信息!");
				JOptionPane.showMessageDialog(null, "请输入完整信息!"
						+ "");
			}
			else{
				xb.setVisible(false);
				xa.setText("正在登录...");
				String[] asretu;
				try {
				asretu = Clientbackhandle.LogintoAS(usr, password);
				if(asretu[1]==null)
				{
					JOptionPane.showMessageDialog(null, asretu[0]);
					xb.setVisible(true);
					xa.setText("登录");
				}
				else
				{
					String []tgsretu=Clientbackhandle.logintotgs(asretu[3],asretu[0], asretu[1],asretu[2]);//TGS认证结果
					String idc=asretu[3];//保存用户在数据库中的ID             
					if(tgsretu[1]==null)
					{
						JOptionPane.showMessageDialog(null, tgsretu[0]);
						xb.setVisible(true);
						xa.setText("登录");
					}
					else
					{
						String retu=Clientbackhandle.logintov(idc, tgsretu[0],tgsretu[1], tgsretu[2]);
						if(retu.equals("1"))//认证成功开始进入聊天UI
						{
							xb.setVisible(true);
							xa.setText("登录");
							//this.setVisible(false);
							new ChatUI(idc,usr);
						}
						else
						{
							JOptionPane.showMessageDialog(null, retu);//认证失败重新登录
							xb.setVisible(true);
							xa.setText("登录");
						}
					}
				}
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				xa.setBounds(95, 200, 150, 30);
				this.setVisible(false);
				/*long usrId = Long.parseLong(jt.getText());
				boolean goon = false;*/	
				}
			}
		else if(e.getSource()==xb){
			new RegistUI();
		}
	}
}
