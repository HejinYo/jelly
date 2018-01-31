package cn.hejinyo.jelly.modules.oss.cloud;

import cn.hejinyo.jelly.common.exception.InfoException;
import com.google.gson.Gson;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 七牛云存储
 *
 * @author hejinyo
 * @date 2017/9/26 11:00
 */
public class QiniuCloudStorageService extends AbstractCloudStorage {
    private UploadManager uploadManager;
    private String token;

    public QiniuCloudStorageService(CloudStorage config) {
        this.config = config;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        uploadManager = new UploadManager(new Configuration(Zone.zone0()));
        if (null != config.getKey()) {
            token = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey()).uploadToken(config.getQiniuBucketName(), config.getKey());
        } else {
            token = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey()).uploadToken(config.getQiniuBucketName());
        }
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
            return config.getQiniuDomain() + "/" + putRet.key;
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

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getQiniuPrefix(), suffix));
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getQiniuPrefix(), suffix));
    }
}
