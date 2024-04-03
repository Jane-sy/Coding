package demo;

import java.util.Arrays;
import java.util.List;

/**
 * @author shiyou
 * @date 2022年11月25日 14:48
 * @description 数组测试
 */
public class ArrayTest {
    public static void main(String[] args) {
        test1();
        test2();
    }

    /**
     * contains精确度测试
     * 总结：contains能满足数据包含精度
     */
    public static void test1() {
        List<String> list = Arrays.asList("1101", "101", "1102");
        System.out.println(list.contains("102"));   // false
        System.out.println(list.contains("101"));   // true
    }

    /**
     * split方法测试
     * 总结：split默认会忽略空值，需将limit设置为-1
     */
    public static void test2() {
        String text = "" + "," + "";
        String[] value = text.split(",", -1);
        System.out.println(value.length);   // 2
    }
}
