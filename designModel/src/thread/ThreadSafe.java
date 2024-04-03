package thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ShiYou
 * @date 2023年12月29日 9:47
 * Description: 线程安全测试
 */
public class ThreadSafe {

    private static int x = 0, y = 0;
    private static int a = 0, b = 0;

    /**
     * 描述: 两个线程交替执行, 一个线程执行完后, 另一个线程会立即执行, 使得 x,y 交替为1 <br>
     * 实际: 在运行至一定次数后会出现 x,y 全为0或1的情况 <br>
     * 原因: AtomicInteger是线程安全的，可以保证每个for循环内加一；线程内代码是不安全的，可能会被挂起，在一个for循环内无法完成（异步和同步的问题） <br>
     */
    public static void main(String[] args) throws InterruptedException {
        final AtomicInteger count = new AtomicInteger(0);
        for (; ; ) {
            x = 0;
            y = 0;
            a = 0;
            b = 0;
            CountDownLatch latch = new CountDownLatch(1);

            // 线程1
            Thread one = new Thread(() -> {
                try {
                    latch.await();
                } catch (InterruptedException ignored) {
                }
                a = 1;
                x = b;
            });

            // 线程2
            Thread other = new Thread(() -> {
                try {
                    latch.await();
                } catch (InterruptedException ignored) {
                }
                b = 1;
                y = a;
            });

            one.start();
            other.start();
            latch.countDown();
            one.join();
            other.join();

            // 打印单次循环结果
            String result = "第" + count.addAndGet(1) + "次 (" + x + "," + y + "）";
            if (x == 0 && y == 0) {
                // 线程只执行了一个或都没执行
                System.err.println(result);
                break;
            } else {
                System.out.println(result);
            }
        }
    }
}
