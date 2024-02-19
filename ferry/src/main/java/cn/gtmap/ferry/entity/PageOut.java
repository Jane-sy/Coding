package cn.gtmap.ferry.entity;

import lombok.Data;

import java.util.List;

/**
 * @author ShiYou
 * @date 2023年08月21日 10:24
 * Description: 分页查询结果
 */
@Data
public class PageOut<T> {

    /**
     * 每页数量
     */
    int size;

    /**
     * 页码
     */
    int page;

    /**
     * 总量
     */
    int total;

    /**
     * 内容
     */
    List<T> content;

    public PageOut(int size, int page) {
        this.size = size;
        this.page = page;
    }
}
