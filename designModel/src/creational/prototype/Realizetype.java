package creational.prototype;

/**
 * @author shiyou
 * @date 2021年11月04日 14:19
 * @description 原型模式-具体原型类
 */
public class Realizetype implements Cloneable {
    Realizetype() {
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        System.out.println("具体原型复制成功");
        return (Realizetype) super.clone();
    }
}
