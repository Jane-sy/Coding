package creational.singleton;

/**
 * @author shiyou
 * @date 2021年11月04日 13:44
 * @description 懒汉式单例
 */
public class LazySingleton {
    private static volatile LazySingleton instance = null;

    private LazySingleton() {
        System.out.println("一个懒汉式单例正在创建");
    }

    public static synchronized LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        System.out.println("懒汉式单例生成了");
        return instance;
    }
}
