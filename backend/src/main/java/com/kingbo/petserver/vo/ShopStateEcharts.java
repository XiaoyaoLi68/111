package com.kingbo.petserver.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 店铺状态统计 VO
 * 用于接收 SQL: SELECT state as auditState, COUNT(*) as stateNum ... 的结果
 */
@Data
public class ShopStateEcharts implements Serializable {

    /**
     * 审核状态 (数据库里的 0, 1, 2, 3)
     * 注意：虽然数据库是 int，这里定义为 String 也没问题，MyBatis 会自动转换
     */
    private String auditState;

    /**
     * 该状态下的店铺数量
     */
    private Integer stateNum;

    /**
     * 辅助方法：获取状态的中文描述
     * 前端 ECharts 显示图表时，通常需要中文名称而不是数字
     */
    public String getStateDes() {
        if (auditState == null) {
            return "未知状态";
        }
        // 根据你的业务定义状态码，这里是个示例：
        switch (auditState) {
            case "0": return "待审核";
            case "1": return "待启用";
            case "2": return "审核拒绝";
            case "3": return "已启用";
            default: return "其他";
        }
    }
}
