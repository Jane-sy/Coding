package demo;

import java.math.BigDecimal;

/**
 * @author shiyou
 * @date 2022年03月04日 16:02
 * @description 数字测试
 */
public class NumTest {
    public static void main(String[] args) {
        test1();
    }

    /**
     *  浮点数double在运行一定次数后会出现数据溢出情况
     *  大数应从字符型转换，从double类型转换也会出现溢出情况（转换后的值，计算不会）
     */
    public static void test1() {
        // 浮点数
        double num = 0.0;
        double a= 0.00222222;
        for (int i = 0; i < 100; i++) {
            num += a;
        }
        System.out.println("浮点数计算结果：" + num);

        // 大数
        BigDecimal numB = new BigDecimal("0.0");
        BigDecimal b = new BigDecimal("0.0022222222");
        for (int i = 0; i < 100; i++) {
            numB = numB.add(b);
        }
        System.out.println("大数计算结果：" + numB);
    }
}
