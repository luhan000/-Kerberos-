package 多线程锁旗标;
class MyTask implements Runnable {
	    private int taskNum;
	     
	    public MyTask(int num) {
	        this.taskNum = num;
	    }
	     
	    @SuppressWarnings("static-access")
		@Override
	    public void run() {
	        System.out.println("正在执行task "+taskNum);
	        try {
	            Thread.currentThread().sleep(4000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        System.out.println("task "+taskNum+"执行完毕");
	    }
	}


