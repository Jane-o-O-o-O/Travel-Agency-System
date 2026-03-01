package com.shishiji.travel.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 分页响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    
    /**
     * 总数
     */
    private Long total;
    
    /**
     * 当前页码（1-based）
     */
    private Integer pageNo;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 总页数
     */
    private Integer pages;
    
    public PageResult<T> calcPages() {
        this.pages = (int) ((this.total + this.pageSize - 1) / this.pageSize);
        return this;
    }
}
