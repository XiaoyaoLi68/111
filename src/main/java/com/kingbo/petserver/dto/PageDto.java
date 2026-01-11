package com.kingbo.petserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    专门封装分页查询参数的对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageDto {

    private Integer currentPage;
    private Integer pageSize;
    private String keyword;

}