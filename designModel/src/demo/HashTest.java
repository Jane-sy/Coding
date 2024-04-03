package demo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author shiyou
 * @date 2021年12月23日 15:19
 * @description
 */
public class HashTest {
    public static void main(String[] args) {
        Consumer c = System.out::println;
        Person person1 = new Person("Tom", 12);
        Person person2 = new Person("Tom", 12);

        Map<Person, String> map = new HashMap<Person, String>();

        map.put(person1, "hhh");

        String a = "111";
        String b = "111";

        Map cmap = new ConcurrentHashMap();

        c.accept(person1.equals(person2));
        c.accept(a instanceof String);

        c.andThen(c).accept(b instanceof String);

    }
}
