package com.kingbo.petserver.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONPath;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.kingbo.myossimagecheckstarter.utils.MyContextCheckUtils;
import com.kingbo.myossimagecheckstarter.utils.MyImageCheckUtils;
import com.kingbo.petserver.dao.ShopAuditLogDao;
import com.kingbo.petserver.dao.ShopDao;
import com.kingbo.petserver.dto.PageDto;
import com.kingbo.petserver.entity.Employee;
import com.kingbo.petserver.entity.Shop;
import com.kingbo.petserver.entity.ShopAuditLog;
import com.kingbo.petserver.exception.ShopCheckException;
import com.kingbo.petserver.myconst.MyPetHomeConst;
import com.kingbo.petserver.service.EmployeeService;
import com.kingbo.petserver.service.ShopService;
import com.kingbo.petserver.utils.EmailUtil;
import com.kingbo.petserver.vo.Result;
import com.kingbo.petserver.vo.ShopStateEcharts;
import com.kingbo.testMessage.TestMessage;
import jakarta.annotation.Resource;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    @Resource
    private EmailUtil emailUtil;

    @Resource
    private MyImageCheckUtils imageCheckUtils;

    @Resource
    private MyContextCheckUtils contextCheckUtils;

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

    @Override
    public Result<PageInfo<Shop>> queryByPage(PageDto pageDto) {
        PageHelper.startPage(pageDto.getCurrentPage(),pageDto.getPageSize());
        List<Shop> shops = shopDao.queryAllByLimit(pageDto.getKeyword());
        return Result.success(new PageInfo<>(shops));
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
    @Transactional(rollbackFor = Exception.class)
    public Result<String> update(Shop shop) throws Exception {
        Shop originalShop = shopDao.queryById(shop.getId());
        shop.setState(1);
        if(shop.getAdmin() != null) {
            //把原来管理的店铺管理人设置为 null
            shopDao.updateAdminIdByAdminId(shop.getAdminId());
            //修改员工的shop_id为 null
            employeeService.updateShopIdNullById(shop.getAdminId());
            //修改店铺管理人
            shop.setAdminId(shop.getAdmin().getId());
            //修改员工的shop_id
            employeeService.updateShopIdById(shop.getAdminId(),shop.getId());
        }
        shopDao.update(shop);
        ShopAuditLog log = new ShopAuditLog(null, 1, shop.getId(), null, null, "修改后需重新审核");
        shopAuditLogDao.updateByShopId(log);
        //自动审核
        try {
            check(log);
            if(log.getState() == 4){
                shopDao.update(originalShop);
                return Result.failure("修改失败");
            }
        }catch (Exception e){
            ShopCheckException.throwPetException("自动审核失败....");
        }
        return Result.success("修改成功");
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public Result<String> deleteById(Long id) {
        int i = shopDao.deleteById(id);
        if(i > 0){
            shopAuditLogDao.deleteByShopId(id);
            employeeService.updateShopIdNullByShopId(id);
            return Result.success("删除成功");
        }
        return Result.failure("删除失败");
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
        try {
            check(log);
        }catch (Exception e){
            ShopCheckException.throwPetException("第1次审核失败....");
        }
        return Result.success("入驻申请成功，待审核");
    }

    public Result<String> check(ShopAuditLog shopAuditLog) throws Exception {
        Shop shop = shopDao.queryById(shopAuditLog.getShopId());

        String r1 = imageCheckUtils.checkImage(shop.getLogo());
        String r2 = contextCheckUtils.checkContext(Arrays.asList(shop.getAddress(), shop.getName()));

        JSONArray s1 = (JSONArray) JSONPath.eval(r1, "$.body.Data.Results[0].SubResults.Suggestion");
        JSONArray s2 = (JSONArray) com.alibaba.fastjson2.JSONPath.eval(r2, "$.body.Data.Elements.Results.Suggestion");

        String eval = "pass";
        if (s1 != null) {
            for (Object r : s1) {
                if(r.toString().equals("block")){
                    eval = "block";
                    break;
                }else if(r.toString().equals("review")){
                    eval = "review";
                }
            }
        }
        if(eval.equals("pass")){
            if (s2 != null) {
                for (Object r : s2) {
                    if(r.toString().equals("block")){
                        eval = "block";
                        break;
                    }else if(r.toString().equals("review")){
                        eval = "review";
                    }
                }
            }
        }
        switch (eval){
            case "pass" ->{
                shopAuditLog.setState(2);
                shopAuditLog.setNote("阿里云自动审核成功");
            }
            case "review" ->{
                shopAuditLog.setState(5);
                shopAuditLog.setNote("阿里云自动审核不确定,需人工介入");
            }
            default -> {
                shopAuditLog.setState(4);
                shopAuditLog.setNote("阿里云自动审核拒绝");
            }
        }
        // 在这个方法内修改状态
        int i = updateStatus(shopAuditLog);
        return i == 2 ? Result.success("审核成功") : Result.failure("审核失败");
    }

    @Override
    @Transactional
    public Result<String> pass(ShopAuditLog shopAuditLog) {
        shopAuditLog.setState(2);
        int i = updateStatus(shopAuditLog);
        return Result.success("审核通过");
    }

    @Override
    public Result<String> deleteByIds(List<Long> ids) {
        int i = shopDao.deleteByIds(ids);
        if(i > 0){
            shopAuditLogDao.deleteByShopIds(ids);
            employeeService.updateShopIdNullByShopIds(ids);
            return Result.success("删除成功");
        }
        return Result.failure("删除失败");
    }

    @Override
    public List<Shop> queryData() {
        return shopDao.queryAllByLimit(null);
    }

    // 统计不同状态的店铺数量
    @Override
    public HashMap<String, List<?>> queryShopGroupState() {
        // 调用Mapper分组统计不同状态的店铺数量
        List<ShopStateEcharts> maps = shopDao.selectShopGroupState();
        // 准备返回值
        HashMap<String, List<?>> hashMap = new HashMap<>();
        ArrayList<String> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        for (ShopStateEcharts map : maps) {
            x.add(map.getStateDes());
            y.add(map.getStateNum());
        }

        hashMap.put("states_x", x);
        hashMap.put("num_y", y);
        return hashMap;
    }

    @Override
    public List<Map<String, Object>> queryShopGroupStateForPie() {
        // 1. 复用 DAO 查出数据
        List<ShopStateEcharts> list = shopDao.selectShopGroupState();

        // 2. 转换格式为 ECharts 饼图专用: [{name: "待审核", value: 10}, ...]
        List<Map<String, Object>> pieList = new ArrayList<>();

        for (ShopStateEcharts item : list) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("name", item.getStateDes()); // 名字
            dataMap.put("value", item.getStateNum()); // 数值
            pieList.add(dataMap);
        }
        return pieList;
    }


    @Override
    @Transactional
    public Result<String> reject(ShopAuditLog shopAuditLog) {
        shopAuditLog.setState(4);
        int i = updateStatus(shopAuditLog);
        return Result.success("审核驳回");
    }

    @Transactional
    public int updateStatus(ShopAuditLog shopAuditLog) {
        shopAuditLog.setAuditTime(new Date());
        // 判断,如果是 审核成功,修改状态之后,就发激活的邮件,否则就直接修改状态不发邮件
        //更新shop状态
        int i = shopDao.updateStateById(shopAuditLog.getState(), shopAuditLog.getShopId());
        shopAuditLogDao.updateByShopId(shopAuditLog);
        if(shopAuditLog.getState() == 2){
            /*//发邮件
            String to = employeeService.selectEmailByShopId(shopAuditLog.getShopId());
            int code = RandomUtil.randomInt(6);
            emailUtil.sendHtmlMail(to, "激活店铺", "<a href='http://localhost:8080/org/shop/active?id=" + shopAuditLog.getShopId() + "&code=" + code + "'>点击激活您的店铺</a>");
            */return 2;
        }else if(shopAuditLog.getState() == 4){
            return 4;
        }
        return 0;
    }

}
