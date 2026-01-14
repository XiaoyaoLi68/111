package com.kingbo.myalyossstarter.utils;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

public class MyFileUploadUtils {

    @Value("${aly.endPoint}")
    private String endpoint;
    @Value("${aly.bucket}")
    private String bucketName;

    public String upload(MultipartFile image) throws Exception {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        // 填写Bucket名称，例如examplebucket。
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        // 为了防止文件相互同名覆盖,应该使用 UUID 替代  ,必须先知道原始文件的名字???
        String string = UUID.randomUUID().toString();
        int i = image.getOriginalFilename().lastIndexOf(".");
        String objectName ;
        if(i == -1){
            // 说明没有后缀,直接让 objectName = string;
            objectName= string;
        }else {
            // 有后缀,让 objectName = string+切后缀
            objectName = string+image.getOriginalFilename().substring(i);
        }
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
        // 填写Bucket所在地域。以华东1（杭州）为例，Region填写为cn-hangzhou。

        String region = endpoint.substring(endpoint.indexOf("-")+1,endpoint.indexOf("."));

        // 创建OSSClient实例。
        // 当OSSClient实例不再使用时，调用shutdown方法以释放资源。
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();

        try {
            //InputStream inputStream = new FileInputStream(filePath);
            // 将文件上传的流对象放在这里
            InputStream inputStream = image.getInputStream();
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
            // 创建PutObject请求。
            PutObjectResult result = ossClient.putObject(putObjectRequest);
            // 根据阿里云图片上传之后的路径规律,自己拼接出上传后的路径
            StringBuilder sb = new StringBuilder("https://");
            sb.append(bucketName).append(".").append(endpoint.substring(8))
                    .append("/").append(objectName);
            return sb.toString();
        } catch (Exception ce) {
            ce.printStackTrace();
            return "文件上传失败.......";
        }

    }
}
