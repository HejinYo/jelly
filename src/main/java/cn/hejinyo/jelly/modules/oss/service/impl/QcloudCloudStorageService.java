package cn.hejinyo.jelly.modules.oss.service.impl;


import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.common.utils.JsonUtil;
import cn.hejinyo.jelly.modules.oss.model.CloudStorageDTO;
import cn.hejinyo.jelly.modules.oss.model.OssFileInfo;
import cn.hejinyo.jelly.modules.oss.service.CloudStorageService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 腾讯云存储
 */
public class QcloudCloudStorageService extends CloudStorageService {
    private COSClient client;

    public QcloudCloudStorageService(CloudStorageDTO config) {
        this.config = config;
        //初始化
        init();
    }

    private void init() {
        COSCredentials credentials = new BasicCOSCredentials(config.getAccessKey(), config.getSecretKey());
        //初始化客户端配置
        ClientConfig clientConfig = new ClientConfig(new Region(config.getRegion()));
        client = new COSClient(credentials, clientConfig);
    }

    @Override
    public String upload(byte[] data, String path) {
        //上传到腾讯云
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(data.length);
            PutObjectRequest putObjectRequest = new PutObjectRequest(config.getBucketName(), path, new ByteArrayInputStream(data), objectMetadata);
            client.putObject(putObjectRequest);
        } catch (CosClientException e) {
            throw new InfoException("文件上传失败，" + e.getMessage());
        }
        return config.getDomain() + "/" + path;
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            byte[] data = IOUtils.toByteArray(inputStream);
            return this.upload(data, path);
        } catch (IOException e) {
            throw new InfoException("上传文件失败", e);
        }
    }

    /**
     * 删除文件
     */
    @Override
    public void delete(String key) {
        try { // 指定要删除的 bucket 和对象键
            client.deleteObject(config.getBucketName(), key);
        } catch (CosClientException e) {
            throw new InfoException("删除文件失败，" + e.getMessage());
        }
    }

    /**
     * 查询获取文件列表。
     */
    @Override
    public List<OssFileInfo> list(String key, int limit) {
        // 获取 bucket 下成员（设置 delimiter）
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        listObjectsRequest.setBucketName(config.getBucketName());
        // 设置 list 的 prefix, 表示 list 出来的文件 key 都是以这个 prefix 开始
        listObjectsRequest.setPrefix(key);
        // 设置 delimiter 为/, 即获取的是直接成员，不包含目录下的递归子成员
        listObjectsRequest.setDelimiter("/");
        // 设置 marker, (marker 由上一次 list 获取到, 或者第一次 list marker 为空)
        listObjectsRequest.setMarker("");
        // 设置最多 list 100 个成员,（如果不设置, 默认为 1000 个，最大允许一次 list 1000 个 key）
        listObjectsRequest.setMaxKeys(limit);

        ObjectListing objectListing = client.listObjects(listObjectsRequest);
        // 获取下次 list 的 marker
        String nextMarker = objectListing.getNextMarker();
        // 判断是否已经 list 完, 如果 list 结束, 则 isTruncated 为 false, 否则为 true
        boolean isTruncated = objectListing.isTruncated();
        List<COSObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        List<OssFileInfo> fileList = new ArrayList<>();
        for (COSObjectSummary cos : objectSummaries) {
            OssFileInfo ossFileInfo = new OssFileInfo();
            ossFileInfo.setKey(cos.getKey());
            ossFileInfo.setHash(cos.getETag());
            ossFileInfo.setPutTime(cos.getLastModified());
            ossFileInfo.setSize(cos.getSize());
            fileList.add(ossFileInfo);
        }
        return fileList;
    }
}
