package creational.builder;

/**
 * @author shiyou
 * @date 2021年11月04日 15:42
 * @description
 */
abstract class Builder {
    protected Product product = new Product();

    public abstract void buildPartA();

    public abstract void buildPartB();

    public abstract void buildPartC();

    public Product getResult() {
        return product;
    }
}
