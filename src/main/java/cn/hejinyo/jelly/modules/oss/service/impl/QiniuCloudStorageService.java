package cn.hejinyo.jelly.modules.oss.service.impl;

import cn.hejinyo.jelly.common.exception.InfoException;
import cn.hejinyo.jelly.modules.oss.model.CloudStorageDTO;
import cn.hejinyo.jelly.modules.oss.model.OssFileInfo;
import cn.hejinyo.jelly.modules.oss.service.CloudStorageService;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 七牛云存储
 *
 * @author hejinyo
 * @date 2017/9/26 11:00
 */
public class QiniuCloudStorageService extends CloudStorageService {
    private UploadManager uploadManager;
    private BucketManager bucketManager;
    private String token;

    public QiniuCloudStorageService(CloudStorageDTO config) {
        this.config = config;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        token = auth.uploadToken(config.getBucketName());
        uploadManager = new UploadManager(new Configuration(Zone.zone0()));
        bucketManager = new BucketManager(auth, new Configuration(Zone.zone0()));
    }

    @Override
    public String upload(byte[] data, String path) {
        try {
            Response res = uploadManager.put(data, path, token);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(res.bodyString(), DefaultPutRet.class);
            //解析上传成功的结果
            if (!res.isOK()) {
                throw new RuntimeException("上传七牛出错：" + res.toString());
            }
            return config.getDomain() + "/" + putRet.key;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InfoException("上传文件失败，请核对七牛配置信息", e);
        }
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
        try {
            bucketManager.delete(config.getBucketName(), key);
        } catch (QiniuException ex) {
            //如果遇到异常，说明删除失败
            throw new InfoException("删除文件失败:" + ex.response.toString());
        }

    }

    /**
     * 查询获取文件列表。
     */
    @Override
    public List<OssFileInfo> list(String key, int limit) {
        List<OssFileInfo> list = new ArrayList<>();
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";
        //列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(config.getBucketName(), key, limit, delimiter);
        while (fileListIterator.hasNext()) {
            //处理获取的file list结果
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                OssFileInfo ossFileInfo = new OssFileInfo();
                ossFileInfo.setSize(item.fsize);
                ossFileInfo.setKey(item.key);
                ossFileInfo.setPutTime(new Date(item.putTime));
                ossFileInfo.setHash(item.hash);
                list.add(ossFileInfo);
            }
        }
        return list;
    }
}
