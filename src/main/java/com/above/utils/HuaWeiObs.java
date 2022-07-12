package com.above.utils;

import cn.hutool.core.util.IdUtil;
import com.obs.services.ObsClient;
import com.obs.services.model.ObsObject;

import java.io.*;

/**
 * @author: zph
 * @Description:
 * @Date: 2021/4/16 19:49
 * @Version 1.0
 */

public  class HuaWeiObs {

    private static String endPoint = ResourcesUtil.getValue("HuaWeiObsConfig","url");
    private static String ak = ResourcesUtil.getValue("HuaWeiObsConfig","apiKey");
    private static String sk = ResourcesUtil.getValue("HuaWeiObsConfig","secretKey");
    //保存文件的地址
    private static final String defaultURl = ResourcesUtil.getValue("fileConfig", "saveUrl");
    //下载文件的地址
    private static final String url = ResourcesUtil.getValue("fileConfig", "url");
    // 创建ObsClient实例
    private static ObsClient obsClient ;

    public static String save(String objectname, File file){
        if(obsClient == null){
            obsClient =  new ObsClient(ak, sk, endPoint);
        }
        return save(objectname, file, false);
    }

    public static String save(String objectName, File file, boolean isHttps){
        if(obsClient == null){
            obsClient =  new ObsClient(ak, sk, endPoint);
        }
        // file为待上传的本地文件路径，需要指定到具体的文件名
        obsClient.putObject("inspection", objectName,file);

        return String.format("https://inspection.obs.cn-south-1.myhuaweicloud.com/%s", objectName);
    }

    public static void deleteFile(String bucketName,String objectKey) throws Exception {
        if(obsClient == null){
            obsClient =  new ObsClient(ak, sk, endPoint);
        }
        String substring = objectKey.substring(objectKey.lastIndexOf("myhuaweicloud.com")+18);
        obsClient.deleteObject(bucketName, substring);
//            obsClient.close(); //ObsClient调用close方法后导致部分属性为null
    }

    public static String getFileByUrl(String obsUrl) throws IOException {
        if(obsClient == null){
            obsClient =  new ObsClient(ak, sk, endPoint);
        }
        String substring = obsUrl.substring(obsUrl.lastIndexOf("myhuaweicloud.com")+18);
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
