package creational.singleton;

/**
 * @author shiyou
 * @date 2021年11月04日 13:53
 * @description
 */
public class SingletonTest {
    public static void main(String[] args) {
        HungrySingleton hungrySingleton = HungrySingleton.getInstance();
        System.out.println("==================");

        LazySingleton lazySingleton = LazySingleton.getInstance();
        System.out.println("==================");

        try {
            HungrySingleton hungrySingleton1 = HungrySingleton.getInstance();
            if (hungrySingleton1 == hungrySingleton) {
                System.out.println("这是同一个饿汉式单例！！");
            }
        } catch (Exception e) {
            System.out.println("单例创建失败：" + e);
        }
    }
}
