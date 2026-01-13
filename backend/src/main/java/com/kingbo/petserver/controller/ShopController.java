package com.kingbo.petserver.controller;

import com.kingbo.myalyossstarter.utils.MyFileUploadUtils;
import com.kingbo.petserver.entity.Shop;
import com.kingbo.petserver.service.ShopService;
import com.kingbo.petserver.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 分页查询
     *
     * @param shop        筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<Page<Shop>> queryByPage(Shop shop, PageRequest pageRequest) {
        return ResponseEntity.ok(this.shopService.queryByPage(shop, pageRequest));
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
    public ResponseEntity<Shop> edit(Shop shop) {
        return ResponseEntity.ok(this.shopService.update(shop));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Long id) {
        return ResponseEntity.ok(this.shopService.deleteById(id));
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
}

