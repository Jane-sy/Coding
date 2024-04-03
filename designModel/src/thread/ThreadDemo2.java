package thread;

/**
 * @author shiyou
 * @date 2022年10月14日 13:51
 * @description 借助对象锁实现线程交互打印（A和B单个和总体都是顺序打印的）
 */
public class ThreadDemo2 {
    private static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        new Thread(new ThreadA()).start();
        Thread.sleep(1000);
        new Thread(new ThreadB()).start();
    }

    static class ThreadA implements Runnable {

        @Override
        public void run() {
            synchronized (lock) {
                for (int i = 0; i < 100; i++) {
                    try {
                        System.out.println("Thread A " + i);
                        lock.notify();
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lock.notify();
            }
        }
    }

    static class ThreadB implements Runnable {

        @Override
        public void run() {
            synchronized (lock) {
                for (int i = 0; i < 100; i++) {
                    try {
                        System.out.println("Thread B " + i);
                        lock.notify();
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lock.notify();
            }
        }
    }
}
