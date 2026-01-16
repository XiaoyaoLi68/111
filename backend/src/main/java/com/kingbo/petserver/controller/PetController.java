package com.kingbo.petserver.controller;

import com.github.pagehelper.PageInfo;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Pet;
import com.kingbo.petserver.service.PetService;
import com.kingbo.petserver.vo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * (Pet)表控制层
 *
 * @author makejava
 * @since 2026-01-16 10:44:16
 */
@RestController
@RequestMapping("pet")
public class PetController {
    /**
     * 服务对象
     */
    @Resource
    private PetService petService;

    @PostMapping("pet/queryPage")
    public Result<PageInfo<Pet>> queryPage(@RequestBody PageDto pageDto){
        return petService.queryPage(pageDto);
    }


}

