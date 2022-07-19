package com.above.utils;

import cn.hutool.core.util.IdUtil;
import com.above.config.obs.HuaWeiObsConfig;
import com.above.config.server.ServerConfig;
import com.obs.services.ObsClient;
import com.obs.services.model.ObsObject;

import java.io.*;

/**
 * @author: imtss
 * @Description:
 * @Date: 2022/7/1 16:49
 * @Version 2.0
 */

public  class HuaWeiObs {

    //保存文件的地址
    private static final String defaultURl = ServerConfig.localSaveUrl;
    // 创建ObsClient实例
    private static ObsClient obsClient ;

    public static String save(String objectName, File file){
        if(obsClient == null){
            obsClient =  new ObsClient(HuaWeiObsConfig.apiKey, HuaWeiObsConfig.secretKey, HuaWeiObsConfig.endPoint);
        }
        // file为待上传的本地文件路径，需要指定到具体的文件名
        obsClient.putObject(HuaWeiObsConfig.bucketName, objectName, file);

        return String.format("https://%s.obs.cn-south-1.myhuaweicloud.com/%s", HuaWeiObsConfig.bucketName, objectName);
    }

    public static void deleteFile(String bucketName, String objectKey) throws Exception {
        if(obsClient == null){
            obsClient =  new ObsClient(HuaWeiObsConfig.apiKey, HuaWeiObsConfig.secretKey, HuaWeiObsConfig.endPoint);
        }
        String substring = objectKey.substring(objectKey.lastIndexOf("myhuaweicloud.com/"));
        obsClient.deleteObject(bucketName, substring);
//            obsClient.close(); //ObsClient调用close方法后导致部分属性为null
    }

    public static String getFileByUrl(String obsUrl) throws IOException {
        if(obsClient == null){
            obsClient =  new ObsClient(HuaWeiObsConfig.apiKey, HuaWeiObsConfig.secretKey, HuaWeiObsConfig.endPoint);
        }
        String substring = obsUrl.substring(obsUrl.lastIndexOf("myhuaweicloud.com/"));
        ObsObject obsObject = obsClient.getObject("inspection", substring);
        // 读取对象内容
        InputStream input = obsObject.getObjectContent();
        byte[] b = new byte[1024];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        while ((len=input.read(b)) != -1){
            bos.write(b, 0, len);
        }
        //转换为二进制
        byte[] bytes = bos.toByteArray();
        String randomCode = IdUtil.fastUUID().substring(0,6);
        try {
            String newFileUrl =  defaultURl.concat(randomCode).concat(substring);
            File file = new File(newFileUrl);
            //判断文件是否存在，若不存在就创建
            if (!file.exists()) {
                String folderUrl = newFileUrl.substring(0,newFileUrl.lastIndexOf("/")+1);
                File folder = new File(folderUrl);
                //判断文件夹是否存在，若不存在就创建
                if (!folder.exists() && !folder.isDirectory()) {
                    folder.mkdirs();
                }
                file.createNewFile();
            }
            FileOutputStream output = new FileOutputStream(file);
            output.write(bytes);
            output.close();
            bos.close();
            input.close();
//            obsClient.close(); //ObsClient调用close方法后导致部分属性为null
            return newFileUrl;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
