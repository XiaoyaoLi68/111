package com.kingbo.petserver.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.kingbo.petserver.dao.ShopAuditLogDao;
import com.kingbo.petserver.dao.ShopDao;
import com.kingbo.petserver.entity.Employee;
import com.kingbo.petserver.entity.Shop;
import com.kingbo.petserver.entity.ShopAuditLog;
import com.kingbo.petserver.myconst.MyPetHomeConst;
import com.kingbo.petserver.service.EmployeeService;
import com.kingbo.petserver.service.ShopService;
import com.kingbo.petserver.vo.Result;
import com.kingbo.testMessage.TestMessage;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * (Shop)表服务实现类
 *
 * @author makejava
 * @since 2026-01-12 20:18:44
 */
@Service("shopService")
public class ShopServiceImpl implements ShopService {
    @Resource
    private ShopDao shopDao;

    @Resource
    private TestMessage testMessage;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private ShopAuditLogDao shopAuditLogDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Shop queryById(Long id) {
        return this.shopDao.queryById(id);
    }

    /**
     * 分页查询
     *
     * @param shop        筛选条件
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    @Override
    public Page<Shop> queryByPage(Shop shop, PageRequest pageRequest) {
        long total = this.shopDao.count(shop);
        return new PageImpl<>(this.shopDao.queryAllByLimit(shop, pageRequest), pageRequest, total);
    }

    /**
     * 新增数据
     *
     * @param shop 实例对象
     * @return 实例对象
     */
    @Override
    public Shop insert(Shop shop) {
        this.shopDao.insert(shop);
        return shop;
    }

    /**
     * 修改数据
     *
     * @param shop 实例对象
     * @return 实例对象
     */
    @Override
    public Shop update(Shop shop) {
        this.shopDao.update(shop);
        return this.queryById(shop.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.shopDao.deleteById(id) > 0;
    }

    @Override
    public Result<String> sendMessage(String phone) {
        String s = RandomUtil.randomNumbers(6);
        //testMessage.main(phone,s);
        redisTemplate.opsForValue().set(MyPetHomeConst.SMS_PRE + phone, s,5, TimeUnit.MINUTES);
        return Result.success("发送成功");
    }

    @Override
    public Result<String> onboarding(Shop shop) {
        /*
            0: 数据合法性校验 校验手机验证码,密码和确认密码是否一致,....
            1: 先向 shop 表添加店铺本身相关的信息
            2: 获取店铺的 经纬度,并将店铺的名称保证到 redis 中,以 geo类型保存,以便于后续根据用户发布寻主需求的时候,进行周围指定距离的搜索;
            3: 将 店铺的管理员信息添加到 employee 表
            4: 给 店铺审核的 log 表添加记录,用于审核;
         */
        Employee admin = shop.getAdmin();
        Object o = redisTemplate.opsForValue().get(MyPetHomeConst.SMS_PRE + admin.getPhone());
        if(o == null) return Result.failure("验证码无效");
        if ( !admin.getVerifyCode().equals(Integer.parseInt(o.toString()))) return Result.failure("验证码错误");
        redisTemplate.delete(MyPetHomeConst.SMS_PRE + admin.getPhone());
        if( !admin.getPassword().equals(admin.getComfirmPassword())) return Result.failure("请检查所输的密码是否一致");

        shop.setRegisterTime(new Date());
        shop.setState(1);
        shopDao.insert(shop);

        redisTemplate.opsForGeo().add(MyPetHomeConst.SHOP_LOCATIONS,new Point(shop.getLongitude(),shop.getLatitude()),shop.getAddress());

        admin.setShopId(shop.getId());
        String string = UUID.randomUUID().toString();
        admin.setSalt(string);
        admin.setPassword(SecureUtil.md5(admin.getPassword()+string));
        employeeService.insert(admin);
        shop.setAdminId(admin.getId());
        shopDao.update(shop);

        ShopAuditLog log = new ShopAuditLog();
        log.setState(1);
        log.setShopId(shop.getId());
        shopAuditLogDao.insert(log);
        return Result.success("入驻成功");
    }


}
