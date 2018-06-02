package 多线程聊天框架;

import java.util.Queue; 
import java.util.concurrent.LinkedBlockingQueue; 

/** 
* 队列的遍历 
* 
* @author leizhimin 2009-7-22 15:05:14 
*/ 
public class TestQueue { 
        public static void main(String[] args) { 
                Queue<Integer> q = new LinkedBlockingQueue<Integer>(); 
                String j="";
                //初始化队列 
                for (int i = 0; i < 5; i++) { 
                        q.offer(i); 
                } 
                System.out.println("-------1-----" ); 
                //集合方式遍历，元素不会被移除 
                for (Integer x : q) { 
                	j=j+"\n"+x.toString();//换行
                    System.out.println(x); 
                } 
                System.out.println("j=:"+j); 
                System.out.println("-------2-----" ); 
                //队列方式遍历，元素逐个被移除 
                while (q.peek() != null ) { 
                        System.out.println(q.poll()); 
                } 
        } 
}