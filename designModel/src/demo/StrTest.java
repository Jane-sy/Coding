package demo;

import java.util.function.Consumer;

/**
 * @author shiyou
 * @date 2021年12月29日 10:12
 * @description 字符串测试类
 */
public class StrTest {
    static Consumer c = System.out::println;

    public static void main(String[] args) {
        test1();
        test2();
    }

    /**
     * 验证String.inter()使用同一字符串空间（equals比较的是字符串内容, ==比较的是字符串地址）
     */
    public static void test1() {
        String s1 = new String("test").intern();
        String s2 = new String("test").intern();
        c.accept(s1 == s2);

        String s3 = new String("test");
        String s4 = new String("test");
        c.accept(s3 == s4);
    }

    /**
     * 验证StringBuilder和String拼接效率
     */
    public static void test2() {
        // 借助StringBuilder实现字符串拼接,打印拼接效率
        long d1 = System.currentTimeMillis();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            stringBuilder.append("你好啊");
        }
        long d2 = System.currentTimeMillis();
        c.accept(d2 - d1);    // StringBuilder拼接效率高

        // 普通常见的字符串拼接
        long d3 = System.currentTimeMillis();
        String str = "";
        for (int i = 0; i < 1000; i++) {
            str += "你好啊";
        }
        long d4 = System.currentTimeMillis();
        c.accept(d4 - d3);  // 普通字符串拼接效率低
    }
}
