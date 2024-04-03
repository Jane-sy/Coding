package creational.singleton;

/**
 * @author shiyou
 * @date 2021年11月04日 13:48
 * @description 饿汉式单例
 */
public class HungrySingleton {
    private static final HungrySingleton instance = new HungrySingleton();
    private String name;

    private HungrySingleton() {
        System.out.println("一个饿汉式单例正在创建");
    }

    public static HungrySingleton getInstance() {
        System.out.println("饿汉式单例生成了");
        return instance;
    }

    public void getName() {
        System.out.println("Tom");
    }
}
