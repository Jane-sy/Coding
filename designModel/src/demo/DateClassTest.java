package demo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * @author shiyou
 * @date 2021年12月29日 16:34
 * @description 时间类测试
 */
public class DateClassTest {

    // 定义消费者（打印）
    static Consumer c = System.out::println;

    public static void main(String[] args) {
        test1();
    }

    /**
     * 测试LocalDateTime类
     */
    public static void test1() {
        // 定义时间格式
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // ZoneOffset.of("+8")与ZoneOffset.ofHours(8)效果一致
        long timeStamp = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        c.accept("当前时间戳：" + timeStamp);

        String str = LocalDateTime.ofEpochSecond(timeStamp / 1000, 0, ZoneOffset.ofHours(8)).format(dateTimeFormatter);
        c.accept("时间戳格式化：" + str);

        String now = LocalDateTime.now().format(dateTimeFormatter);
        c.accept("当前时间：" + now);

        String beforeOneDay = LocalDateTime.now().minusDays(1).format(dateTimeFormatter);
        c.accept("前一天：" + beforeOneDay);

        String afterOneDay = LocalDateTime.now().plusDays(1).format(dateTimeFormatter);
        c.accept("后一天：" + afterOneDay);
    }
}
