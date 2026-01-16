package com.kingbo.petserver.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

@Data
@ContentRowHeight(20)  // 内容行高
@HeadRowHeight(25)     // 表头行高
@ColumnWidth(20)       // 默认列宽
public class UserExcelDto {

    @ExcelProperty(value = "用户ID", index = 0)
    private Long id;

    @ExcelProperty(value = "用户名", index = 1)
    private String username;

    @ExcelProperty(value = "姓名", index = 2)
    private String name;

    @ExcelProperty(value = "年龄", index = 3)
    private Integer age;

    @ExcelProperty(value = "邮箱", index = 4)
    @ColumnWidth(30)
    private String email;

    @ExcelProperty(value = "手机号", index = 5)
    private String phone;

    @ExcelProperty(value = "创建时间", index = 6)
    @ColumnWidth(25)
    private Date createTime;

    // 这个字段不会被导出到Excel
    @ExcelIgnore
    private String password;
}

