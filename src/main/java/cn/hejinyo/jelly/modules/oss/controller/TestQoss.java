package cn.hejinyo.jelly.modules.oss.controller;

import cn.hejinyo.jelly.common.utils.JsonUtil;
import cn.hejinyo.jelly.common.utils.Result;
import cn.hejinyo.jelly.modules.oss.factory.OSSFactory;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.region.Region;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author : heshuangshuang
 * @date : 2018/7/30 17:35
 */
@RestController
@RequestMapping("/test")
public class TestQoss {


    @GetMapping("/qupload")
    private Result ttst() throws Exception {
        OSSFactory.build().delete("test.jpg");
        OSSFactory.build().list("test.jpg", 100);
        File localFile = new File("C:\\Users\\Administrator\\Desktop\\test.jpg");
        InputStream input = new FileInputStream(localFile);
        byte[] byt = new byte[input.available()];
        input.read(byt);
        String avatarUrl = OSSFactory.build().upload(byt, "test_" + String.valueOf(RandomUtils.nextInt(1000, 9999)) + ".jpg");
        return Result.ok(avatarUrl);
    }

    private static void test() {
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials("AKIDSjmdGfBmW3KOEo4oGUTDj02pTs2x24GP", "2pMcfS0IJ8i38yymNkvsa3II3ZqtlPo1");
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new Region("ap-chengdu"));
        // 3 生成cos客户端
        COSClient cosClient = new COSClient(cred, clientConfig);
        // bucket的命名规则为{name}-{appid} ，此处填写的存储桶名称必须为此格式
        String bucketName = "jelly-1252854556";

        // 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20M以下的文件使用该接口
        // 大文件上传请参照 API 文档高级 API 上传
        File localFile = new File("C:\\Users\\Administrator\\Desktop\\test.jpg");
        // 指定要上传到 COS 上对象键
        // 对象键（Key）是对象在存储桶中的唯一标识。例如，在对象的访问域名 `bucket1-1250000000.cos.ap-guangzhou.myqcloud.com/doc1/pic1.jpg` 中，对象键为 doc1/pic1.jpg, 详情参考 [对象键](https://cloud.tencent.com/document/product/436/13324)
        String key = "test2.jpg";
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, localFile);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        System.out.println(JsonUtil.toJson(putObjectResult));
    }

    public static void main(String[] args) throws Exception {
        OSSFactory.build().delete("test.jpg");
        //test();
    }
}
