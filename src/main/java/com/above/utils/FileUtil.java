package com.above.utils;

import com.above.config.server.ServerConfig;
import com.above.vo.FileVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @Description: 文件工具类
 * @Author: LZH
 * @Date: 2021/10/14 9:09
 */
public class FileUtil {

    /**
     * @Description: 保存文件的地址
     * @Author: LZH
     * @Date: 2022/1/10 11:47
     */
    private static final String SAVE_URl = ServerConfig.localSaveUrl;
    /**
     * @Description: 下载文件的地址
     * @Author: LZH
     * @Date: 2022/1/10 11:47
     */

    public static String localSave(MultipartFile file, FileVo fileVo) throws IOException {

        //获取文件夹名称
        String folder = fileVo.getFolderName();
        //获取文件名
        String fileName = "";
        if (fileVo.getFileName() != null){
            fileName = generateName(fileVo.getFileName());
        }else {
            fileName = generateName(file.getOriginalFilename());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

        String date =sdf.format(new Date());

        //存储路径可在配置文件中指定
        File pfile = new File(SAVE_URl+folder+date+"/");
        if (!pfile.exists()) {
            pfile.mkdirs();
        }
        //指定好存储路径
        File filename = new File(pfile,fileName);
        try {
            //保存文件
            //使用此方法保存必须要绝对路径且文件夹必须已存在,否则报错
            file.transferTo(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
        return SAVE_URl + folder + date + "/" + fileName;
    }

    /**
     *@Description 删除本地文件
     *@Author  LZH
     *@Date  2021/7/1 15:17
     */
    public static boolean deleteLocalFile(String filePath){

        File delFile = new File(filePath);
        if(delFile.isFile() && delFile.exists()) {
            delFile.delete();
            return true;
        }else {

            return false;
        }
    }
    /**
     * 生成32位的UUID文件名
     * @author imtss
     * @version 1.0
     * @date 2021/2/6 16:21
     * @param  originalFileName-原文件名
     * @return 新文件名
     **/
    private static String generateName(String originalFileName) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        System.out.println(originalFileName);
        //用于切割判断的点
        String pot = ".";
        if (originalFileName.lastIndexOf(pot)<0){
            return uuid + originalFileName+".jpg";
        }
        return uuid + originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    /**
     * @Description: 保存文件到obs
     * @Author: LZH
     * @Date: 2022/5/16 13:41
     */
    public static String saveFile(MultipartFile file,FileVo fileVo) throws IOException {

        //获取文件名
        String fileName = "";
        if (fileVo.getFileName() != null){
            fileName = generateName(fileVo.getFileName());
        }else {
            fileName = generateName(file.getOriginalFilename());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        String date =sdf.format(new Date());
        String folder = null;
        //获取文件夹名称
        if (!StringUtils.isBlank(fileVo.getFolderName())){
            folder = fileVo.getFolderName()+date+"/";
        }else {
            folder = date+"/";
        }

        //存储路径可在配置文件中指定
        File pfile = new File(SAVE_URl+folder);
        if (!pfile.exists()) {
            pfile.mkdirs();
        }
        //指定好存储路径
        File localFile = new File(pfile,fileName);
        try {
            //保存文件
            //使用此方法保存必须要绝对路径且文件夹必须已存在,否则报错
            file.transferTo(localFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
        String url = HuaWeiObs.save(folder+fileName, localFile);
        cn.hutool.core.io.FileUtil.del(SAVE_URl + folder+fileName);
        return url;
    }


}
