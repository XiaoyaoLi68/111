package com.kingbo.petserver.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.kingbo.myalyossstarter.utils.MyFileUploadUtils;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Shop;
import com.kingbo.petserver.entity.ShopAuditLog;
import com.kingbo.petserver.exception.PethomeException;
import com.kingbo.petserver.listener.ImportShopListener;
import com.kingbo.petserver.service.EmployeeService;
import com.kingbo.petserver.service.ShopService;
import com.kingbo.petserver.vo.Result;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * (Shop)表控制层
 *
 * @author makejava
 * @since 2026-01-12 20:18:44
 */
@RestController
@RequestMapping("org/shop")
public class ShopController {
    /**
     * 服务对象
     */
    @Resource
    private ShopService shopService;

    @Resource
    private MyFileUploadUtils myFileUploadUtils;

    @Resource
    private EmployeeService employeeService;



    @PostMapping("queryPage")
    public Result<PageInfo<Shop>> queryByPage(@RequestBody PageDto pageDto) {
        return shopService.queryByPage(pageDto);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<Shop> queryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.shopService.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param shop 实体
     * @return 新增结果
     */
    @PostMapping
    public ResponseEntity<Shop> add(Shop shop) {
        return ResponseEntity.ok(this.shopService.insert(shop));
    }

    /**
     * 编辑数据
     *
     * @param shop 实体
     * @return 编辑结果
     */
    @PutMapping
    public Result<String> edit(@RequestBody Shop shop) throws Exception {
        return shopService.update(shop);
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping("{id}")
    public Result<String> deleteById(@PathVariable Long id) {
        return shopService.deleteById(id);
    }

    @PostMapping("fileUpload")
    public Result<String> uploadFile(MultipartFile file) throws Exception {
        String upload = myFileUploadUtils.upload(file);
        return Result.success(upload);
    }

    @PostMapping("sendVerifyCode")
    public Result<String> sendMessage(@RequestBody Map<String,String> map){
        return shopService.sendMessage(map.get("phone"));
    }

    @PostMapping("onboarding")
    public Result<String> onboarding(@RequestBody Shop shop) throws Exception {
        return shopService.onboarding(shop);
    }

    @PostMapping("pass")
    public Result<String> pass(@RequestBody ShopAuditLog shopAuditLog){
        return shopService.pass(shopAuditLog);
    }

    @PostMapping("reject")
    public Result<String> reject(@RequestBody ShopAuditLog shopAuditLog){
        return shopService.reject(shopAuditLog);
    }

    @PatchMapping
    public Result<String> deleteByIds(@RequestBody List<Long> ids){
        return shopService.deleteByIds(ids);
    }

    // 店铺的导出
    @GetMapping("exportData")
    public void exportData(HttpServletResponse response) {
        try {
            // 数据的导出
            // Service只是做数据的查询
            List<Shop> shopList = shopService.queryData();
            // 配置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-disposition", "attachment;filename=shop_data.xlsx");
            // 写出数据
            EasyExcel.write(response.getOutputStream(), Shop.class)
                    .sheet("店铺列表")
                    .doWrite(shopList);
        } catch (IOException e) {
            PethomeException.throwPetException("店铺数据导出失败");
        }
    }

    // 店铺数据的导入 mportData
    @PostMapping("importData")
    public Result importData(@RequestParam MultipartFile file) {
        // 参数校验
        if (ObjectUtil.isNull(file)) {
            PethomeException.throwPetException("导入的文件不能为空");
        }
        // 判断文件的格式是否为 Excel表格
        if (!ObjectUtil.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", file.getContentType())) {
            PethomeException.throwPetException("导入的文件格式不正确");
        }

        try {
            // 这个监听器必须是new出来的 不能放到容器中进行管理
            ImportShopListener listener = new ImportShopListener();
            EasyExcel.read(file.getInputStream(), Shop.class, listener)
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            throw new RuntimeException("Excel导入失败: " + e.getMessage());
        }

        // 返回导入成功
        return Result.success(null);
    }

    // Echarts的接口
// 返回的是 不同状态的店铺数量
// 比如: 待审核的状态 10 审核通过的 100 审核拒绝的 5
    @GetMapping("chartColumn")
    public Result chartColumn() {
        Map<String,List<?>> map = shopService.queryShopGroupState();
        return Result.success(map);
    }
    // 2. 条形图接口 (Bar Chart)
    // 逻辑：数据和柱状图一样，前端交换 X/Y 轴即可
    @GetMapping("chartBar")
    public Result chartBar() {
        Map<String, List<?>> map = shopService.queryShopGroupState();
        return Result.success(map);
    }

    // 3. 折线图接口 (Line Chart)
    // 逻辑：为了演示，这里依然使用“状态统计”数据。
    // 在实际业务中，折线图通常用于展示“时间趋势”（如：每月注册量），逻辑同理。
    @GetMapping("chartLine")
    public Result chartLine() {
        Map<String, List<?>> map = shopService.queryShopGroupState();
        return Result.success(map);
    }

    // 4. 饼图接口 (Pie Chart)
    // 逻辑：饼图需要 {name: "xx", value: 10} 的格式，需要专门处理
    @GetMapping("chartPie")
    public Result chartPie() {
        List<Map<String, Object>> list = shopService.queryShopGroupStateForPie();
        return Result.success(list);
    }


}

