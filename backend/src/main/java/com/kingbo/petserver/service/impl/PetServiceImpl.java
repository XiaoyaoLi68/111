package com.kingbo.petserver.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Pet;
import com.kingbo.petserver.dao.PetDao;
import com.kingbo.petserver.service.PetService;
import com.kingbo.petserver.vo.Result;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;


@Service("petService")
public class PetServiceImpl implements PetService {
    @Resource
    private PetDao petDao;

    @Override
    public Pet queryById(Long id) {
        return this.petDao.queryById(id);
    }


    @Override
    public Pet insert(Pet pet) {
        this.petDao.insert(pet);
        return pet;
    }

    @Override
    public Pet update(Pet pet) {
        this.petDao.update(pet);
        return this.queryById(pet.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.petDao.deleteById(id) > 0;
    }

    @Override
    public Result<PageInfo<Pet>> queryPage(PageDto pageDto) {
        PageHelper.startPage(pageDto.getCurrentPage(),pageDto.getPageSize());
        List<Pet> pets = petDao.queryAllByLimit(pageDto.getKeyword());
        return Result.success(new PageInfo<>(pets));
    }


}
