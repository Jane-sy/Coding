package thread;

/**
 * @author shiyou
 * @date 2022年10月14日 15:37
 * @description 借助对象锁实现线程顺序打印（这里A和B单个看是按顺序的，总体看依旧乱序）
 */
public class ThreadDemo1 {
//    private static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        new Thread(new ThreadDemo1.ThreadA()).start();
//        Thread.sleep(1000);
        new Thread(new ThreadDemo1.ThreadB()).start();
    }

    static class ThreadA implements Runnable {

        @Override
        public void run() {
            synchronized (this) {
                for (int i = 0; i < 100; i++) {
                    System.out.println("Thread A " + i);
                }
            }
        }
    }

    static class ThreadB implements Runnable {

        @Override
        public void run() {
            synchronized (this) {
                for (int i = 0; i < 100; i++) {
                    System.out.println("Thread B " + i);
                }
            }
        }
    }
}
