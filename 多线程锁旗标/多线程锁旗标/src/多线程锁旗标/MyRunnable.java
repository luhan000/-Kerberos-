package 多线程锁旗标;

public class MyRunnable implements Runnable{

    private int num=5;    //总共票数设定为5
    @Override
    public void run() {
        for(int i=0; i<10; i++){
            if(this.num>0){   //打印买票信息
            	if(this.num==2)
            	{
            	   break;
            	}
                System.out.println(Thread.currentThread().getName() + "@买票: " + this.num--);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        MyRunnable myRunnable = new MyRunnable();

        Thread th1 = new Thread(myRunnable,"售票口一");    //线程一
        Thread th2 = new Thread(myRunnable,"售票口二");    //线程二             

        th1.start();
        th2.start();
        Thread.sleep(4000);
        if(!th1.isAlive())
        {
        	System.out.println(1);
        }

    }
}