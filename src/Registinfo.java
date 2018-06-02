import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;




public class Registinfo extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JTextArea text1 = new JTextArea();		//创建带有初始化文本的文本框对象
	private JButton exitButton=new JButton();
	private JScrollPane jsp=new JScrollPane(text1);
	private JLabel back=new JLabel();
	
	public Registinfo() throws Exception{
		Timer t;
		this.setResizable(false); 		//不能修改大小
		this.getContentPane().setLayout(null);
		this.setTitle("注册消息");
		this.setBounds(100, 100, 400, 400);
		
		back=new JLabel();
		ImageIcon icon=new ImageIcon(this.getClass().getResource("info1.png"));
		back.setIcon(icon);
		back.setBounds(0, 0, 400, 400);

		text1.setForeground(Color.gray);
		//text1.setBounds(30,30, 390,390);
		text1.setFont(new Font("Serif",Font.PLAIN,12));
        text1.setText("");
        text1.setLineWrap(true);
        text1.setOpaque(false);
        
        
        jsp.setBounds(30,30, 300,300);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setOpaque(false);
        jsp.getViewport().setOpaque(false);
        
		exitButton.setText("退出");
		exitButton.setFont(new Font("Dialog",0,12));
		exitButton.setBounds(350,320,40,40);
		exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		exitButton.setBackground(getBackground());
		exitButton.setBackground(Color.white);
		Border b = new LineBorder(Color.white, 2);
		exitButton.setBorder(b);
		exitButton.setVisible(true);
		
		exitButton.addActionListener(this);
		this.getContentPane().add(jsp);
		this.getContentPane().add(exitButton);
		this.getContentPane().add(back);
		
		this.setVisible(true);
		Vserver vserver=new Vserver(text1);//开启服务器
	   /* ActionListener timeActionListener = new ActionListener(){  
	        public void actionPerformed(ActionEvent e) {  
	            // TODO Auto-generated method stub   
	            text1.append(vserver.getregist());  //间隔一秒显示vserver的注册信息队列信息   
	        }     
	    };  
        t = new Timer(1000,timeActionListener);  
        t.start();  */
	}
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		new Registinfo();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==exitButton)
		{
			System.exit(0);//退出
		}
	}
}