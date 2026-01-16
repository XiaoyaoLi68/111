package com.kingbo.petserver.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ContentRowHeight(20)  // 内容行高
@HeadRowHeight(25)     // 表头行高
@ColumnWidth(20)       // 默认列宽
public class Shop implements Serializable {
    @ExcelIgnore
    private Long id; // 店铺ID
    @ExcelProperty(value = "店铺名称", index = 0)
    private String name; // 店铺名称
    @ExcelProperty(value = "店铺地址", index = 1)
    private String address; // 店铺地址
    @ExcelProperty(value = "店铺电话", index = 2)
    private String tel; // 店铺电话
    @ExcelProperty(value = "注册时间", index = 3)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date registerTime; // 注册日期
    @ExcelProperty(value = "店铺审核状态", index = 4)
    private Integer state; // 店铺状态（审核状态）
    @ExcelProperty(value = "Logo地址", index = 5)
    private String logo; // 店铺logo网络地址
    @ExcelIgnore
    private Long adminId; // 店铺管理员ID
    @ExcelProperty(value = "店铺管理员名称", index = 6)
    private String adminName; // 店铺管理员名称

    // 一对一 用对象
    @ExcelIgnore
    private Employee admin; // 店铺管理员

    // 商铺的经纬度信息
    @ExcelIgnore
    private Double longitude;
    @ExcelIgnore
    private Double latitude;
}


