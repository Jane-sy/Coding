package creational.builder;

/**
 * @author shiyou
 * @date 2021年11月05日 16:46
 * @description
 */
public class Client {
    public static void main(String[] args) {
        Builder builder = new ConcreteBuilder();
        Director director = new Director(builder);
        Product product = director.construct();
        product.show();
    }
}
