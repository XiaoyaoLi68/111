package com.kingbo.myossimagecheckstarter.utils;

import com.aliyun.imageaudit20191230.models.ScanImageAdvanceRequest;
import com.aliyun.imageaudit20191230.models.ScanImageResponse;
import com.aliyun.tea.TeaModel;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyImageCheckUtils {

    @Value("${aly.sceneList}")
    private List<String> sceneList;

    private com.aliyun.imageaudit20191230.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
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

    public String checkImage(String urlPath) throws Exception {
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");
        com.aliyun.imageaudit20191230.Client client = createClient(accessKeyId, accessKeySecret);
        // 场景二，使用任意可访问的url
        URL url = new URL(urlPath);
        InputStream inputStream1 = url.openConnection().getInputStream();
        ScanImageAdvanceRequest.ScanImageAdvanceRequestTask task0 = new ScanImageAdvanceRequest.ScanImageAdvanceRequestTask();
        task0.setDataId(UUID.randomUUID().toString());
        task0.setImageTimeMillisecond(3000L);
        task0.setInterval(1);
        task0.setMaxFrames(1);
        task0.setImageURLObject(inputStream1);
        List<ScanImageAdvanceRequest.ScanImageAdvanceRequestTask> taskList = new ArrayList<>();
        taskList.add(task0);

        // 审核的场景 将来在yml中配置
        ScanImageAdvanceRequest scanImageAdvanceRequest = new ScanImageAdvanceRequest()
                .setTask(taskList)
                .setScene(sceneList);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            ScanImageResponse scanImageResponse = client.scanImageAdvance(scanImageAdvanceRequest, runtime);
            // 获取整体结果
            //System.err.println(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(scanImageResponse)));
            return com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(scanImageResponse));
            // 获取单个字段
            //System.err.println(scanImageResponse.getBody().getData());
        } catch (com.aliyun.tea.TeaException teaException) {
           teaException.printStackTrace();
        }
        return "阿里云服务调用失败";
    }
}