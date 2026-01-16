package com.kingbo.myossimagecheckstarter.utils;

import com.aliyun.imageaudit20191230.models.ScanTextRequest;
import com.aliyun.imageaudit20191230.models.ScanTextResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class MyContextCheckUtils {

    @Value("${aly.labelList}")
    private List<String> targetLabels;

    private static com.aliyun.imageaudit20191230.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "imageaudit.cn-shanghai.aliyuncs.com";
        return new com.aliyun.imageaudit20191230.Client(config);
    }


    public String checkContext(List<String> contents) throws Exception {
        String accessKeyId = System.getenv("OSS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("OSS_ACCESS_KEY_SECRET");
        com.aliyun.imageaudit20191230.Client client = createClient(accessKeyId, accessKeySecret);
        List<ScanTextRequest.ScanTextRequestTasks> taskList = new ArrayList<>();
        for (String content : contents) {
            ScanTextRequest.ScanTextRequestTasks task = new ScanTextRequest.ScanTextRequestTasks()
                    .setContent(content);
            taskList.add(task);
        }

        List<ScanTextRequest.ScanTextRequestLabels> labelList = new ArrayList<>();
        for (String label : targetLabels) {
            ScanTextRequest.ScanTextRequestLabels labelObj = new ScanTextRequest.ScanTextRequestLabels()
                    .setLabel(label);
            labelList.add(labelObj);
        }

        ScanTextRequest scanTextRequest = new ScanTextRequest()
                .setLabels(labelList)
                .setTasks(taskList);

        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        try {
            ScanTextResponse response = client.scanTextWithOptions(scanTextRequest, runtime);

            return com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(response));

        } catch (TeaException error) {
            System.out.println(com.aliyun.teautil.Common.toJSONString(error));
            System.out.println(error.getCode());
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            System.out.println(error.getMessage());
        }
        return "阿里云文本审核失败";
    }
}
