package thread;

/**
 * @author shiyou
 * @date 2022年10月14日 14:08
 * @description 借助信号量实现线程通信
 */
public class ThreadDemo3 {
    private static volatile int signal = 0;

    public static void main(String[] args) throws InterruptedException {
        new Thread(new ThreadDemo3.ThreadA()).start();
        Thread.sleep(1000);
        new Thread(new ThreadDemo3.ThreadB()).start();
    }


    static class ThreadA implements Runnable {

        @Override
        public void run() {
            // 当信号小于15时执行以下代码块
            while (signal < 15) {
                // 如果信号是偶数
                if (signal % 2 == 0) {
                    // 打印输出"threadA: "和信号的值
                    System.out.println("threadA: " + signal);
                    // 同步代码块
                    synchronized (this) {
                        // 信号自增1
                        signal++;
                    }
                }
            }
        }
    }

    static class ThreadB implements Runnable {

        @Override
        public void run() {
            // 当信号小于15时执行以下代码块
            while (signal < 15) {
                // 如果信号是奇数（即信号除以2的余数为1），执行以下代码块
                if (signal % 2 == 1) {
                    // 打印线程名称和信号值
                    System.out.println("threadB: " + signal);

                    // 加锁当前对象
                    synchronized (this) {
                        // 信号值增加1
                        signal = signal + 1;
                    }
                }
            }
        }
    }
}
