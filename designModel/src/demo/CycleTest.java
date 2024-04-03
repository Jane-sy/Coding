package demo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shiyou
 * @date 2021年12月24日 11:20
 * @description 循环测试
 */
public class CycleTest {
    public static void main(String[] args) {
        test1();
        test2();
    }

    /**
     * 测试for循环和forEach循环效率
     * 总结：差异不大
     */
    public static void test1() {
        List<Map> maps = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Map map = new HashMap();
            maps.add(map);
        }

        // for循环测试
        long start = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        for (Map map : maps) {
            map.put("num", "hash");
        }
        long end = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        System.out.println(end - start);

        // forEach循环测试
        start = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        maps.stream().forEach(map ->
                map.put("num", "hash")
        );
        end = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        System.out.println(end - start);
    }

    /**
     * parallelStream()与stream()方法测试
     * parallelStream() 乱序
     * stream() 顺序
     */
    public static void test2() {

        Map<Integer, String> map = new HashMap();
        map.put(1, "Tom");
        map.put(2, "Jerry");
        map.put(3, "Rose");
        map.put(4, "Jack");
        map.put(5, "Monica");

        /*次序不一致*/
        map.entrySet().parallelStream().forEach((entry) -> {
            System.out.println(entry.getKey());
        });

        System.out.println("==========================");

        /*次序一致*/
        /*filter方法保存满足条件的数据*/
        map.entrySet().stream().filter(m -> 1 == m.getKey()).forEach((entry) -> {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        });
    }

}
