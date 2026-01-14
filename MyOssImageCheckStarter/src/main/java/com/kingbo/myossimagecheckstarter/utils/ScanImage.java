package com.kingbo.myossimagecheckstarter.utils;/*
引入依赖包
<!-- https://mvnrepository.com/artifact/com.aliyun/imageaudit20191230 -->
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>imageaudit20191230</artifactId>
    <version>${aliyun.imageaudit.version}</version>
</dependency>
*/

import com.aliyun.imageaudit20191230.models.ScanImageAdvanceRequest;
import com.aliyun.imageaudit20191230.models.ScanImageResponse;
import com.aliyun.tea.TeaModel;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScanImage {
    public static com.aliyun.imageaudit20191230.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        /*
          初始化配置对象com.aliyun.teaopenapi.models.Config
          Config对象存放AccessKeyId、AccessKeySecret、endpoint等配置
         */
         com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "imageaudit.cn-shanghai.aliyuncs.com";
        return new com.aliyun.imageaudit20191230.Client(config);
    }

    public static void main(String[] args) throws Exception {
        // 创建AccessKey ID和AccessKey Secret，请参考https://help.aliyun.com/document_detail/175144.html
        // 如果您使用的是RAM用户的AccessKey，还需要为子账号授予权限AliyunVIAPIFullAccess，请参考https://help.aliyun.com/document_detail/145025.html
        // 从环境变量读取配置的AccessKey ID和AccessKey Secret。运行代码示例前必须先配置环境变量。
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");
        com.aliyun.imageaudit20191230.Client client = ScanImage.createClient(accessKeyId, accessKeySecret);
        // 场景一，使用本地文件
        // InputStream inputStream1 = new FileInputStream(new File("/tmp/ScanImage1.png"));
        // InputStream inputStream2 = new FileInputStream(new File("/tmp/ScanImage2.png"));
        // 场景二，使用任意可访问的url
        URL url = new URL("https://ts1.tc.mm.bing.net/th/id/R-C.fff49df5dde2397e0007b688d1fcab47?rik=tEof0nSTyZ%2bdUw&riu=http%3a%2f%2fhimg2.huanqiu.com%2fattachment2010%2f2017%2f0410%2f20170410020644555.jpg&ehk=0hrX3Ms6JU4LsmHzjaLW12DdA2zibqSc2uTmpSX8hgw%3d&risl=&pid=ImgRaw&r=0");
        InputStream inputStream1 = url.openConnection().getInputStream();
        //URL url2 = new URL("http://viapi-test.oss-cn-shanghai.aliyuncs.com/viapi-3.0domepic/facebody/DetectFace/DetectFace2.png");
        //InputStream inputStream2 = url2.openConnection().getInputStream();
        ScanImageAdvanceRequest.ScanImageAdvanceRequestTask task0 = new ScanImageAdvanceRequest.ScanImageAdvanceRequestTask();
        task0.setDataId(UUID.randomUUID().toString());
        task0.setImageTimeMillisecond(3000L);
        task0.setInterval(1);
        task0.setMaxFrames(1);
        task0.setImageURLObject(inputStream1);
        /*ScanImageAdvanceRequest.ScanImageAdvanceRequestTask task1 = new ScanImageAdvanceRequest.ScanImageAdvanceRequestTask();
        task1.setDataId("uuid-xxxx-xxxx-1234567");
        task1.setImageTimeMillisecond(1L);
        task1.setInterval(1);
        task1.setMaxFrames(1);
        task1.setImageURLObject(inputStream2);*/
        List<ScanImageAdvanceRequest.ScanImageAdvanceRequestTask> taskList = new ArrayList<>();
        taskList.add(task0);
        //taskList.add(task1);
        List<String> sceneList = new ArrayList<>();
        // 审核的场景  https://help.aliyun.com/zh/viapi/developer-reference/api-f1y6cy?spm=a2c4g.11186623.help-menu-142958.d_4_3_5_2.37e45f9beRyZa0&scm=20140722.H_151941._.OR_help-T_cn~zh-V_1
        sceneList.add("terrorism");
        //sceneList.add("porn");
        com.aliyun.imageaudit20191230.models.ScanImageAdvanceRequest scanImageAdvanceRequest = new com.aliyun.imageaudit20191230.models.ScanImageAdvanceRequest()
                .setTask(taskList)
                .setScene(sceneList);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            ScanImageResponse scanImageResponse = client.scanImageAdvance(scanImageAdvanceRequest, runtime);
            // 获取整体结果
            System.err.println(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(scanImageResponse)));
            // 获取单个字段
            System.err.println(scanImageResponse.getBody().getData());
        } catch (com.aliyun.tea.TeaException teaException) {
            // 获取整体报错信息
            System.out.println(com.aliyun.teautil.Common.toJSONString(teaException));
            // 获取单个字段
            System.out.println(teaException.getCode());
        }
    }
}