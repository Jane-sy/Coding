import cn.hutool.core.util.StrUtil;

/**
 * @author shiyou
 * @date 2021年11月18日 19:02
 * @description 测试模块
 */
public class Test {
    public static void main(String[] args) {
//        Integer n = 10;
//        Integer m = n;
//        n--;
//        System.out.println(n);
//        System.out.println(m);
        int i = "1.xxxx.pdf".lastIndexOf(".");
//        int i = StrUtil.lastIndexOf("1.xxxx.pdf", ".", -1, true);
        String substring = "1.xxxx.pdf".substring(0, i);
        System.out.println(substring);

    }

}
