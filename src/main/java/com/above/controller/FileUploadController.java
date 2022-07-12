package com.above.controller;

import com.above.dto.UserDto;
import com.above.utils.CommonResult;
import com.above.utils.FileUtil;
import com.above.utils.MyStringUtils;
import com.above.utils.ResourcesUtil;
import com.above.vo.FileVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 文件上传接口
 * @Author: LZH
 * @Date: 2021/10/29 10:10
 */
@RestController
@RequestMapping("/file")
@Api(tags = "文件接口")
public class FileUploadController {

    /**
     * 接口地址
     */
    private static final String URL = ResourcesUtil.getValue("fileConfig", "url");

    /**
     * @Description: 上传文件到本地
     * @Author: LZH
     * @Date: 2021/10/22 17:06
     */
    @ApiOperation("上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "files",value = "文件",required = true),
            @ApiImplicitParam(name = "fileType",value = "文件类型: 1-用户头像(图片) 2-任务（图片） 3-任务（视频） 4-资源（视频）")
    })
    @PostMapping("/upload")
    public CommonResult<Object> uploadToObs(FileVo vo) throws Exception {

        //获取文件
        List<MultipartFile> files = vo.getFiles();
        if (files == null){
            return CommonResult.error(500,"缺少文件");
        }
        if (files.size() <= 0){
            return CommonResult.error(500,"没有文件");
        }
        MultipartFile multipartFile = files.get(0);
        //保存文件url地址
        List<String> urlList = new ArrayList<>();
        //保存文件
        String url = FileUtil.saveFile(multipartFile, vo);

        urlList.add(url);
        return CommonResult.success(urlList);

    }


    /**
     * @Description: 获取jar包中的文件url
     * @Author: LZH
     * @Date: 2022/2/23 17:35
     */
    @ApiOperation("获取模板文件url")
    @GetMapping("/getTemplateUrl")
    public CommonResult<Object> getTemplateUrl(FileVo vo) {

        //获取参数
        if (vo == null){
            return CommonResult.error(500,"缺少参数");
        }
        if (vo.getTemplateType() == null){
            return CommonResult.error(500,"缺少模板类型");
        }
        String urlName = null;
        String fileName = null;
        switch (vo.getTemplateType()){
            case 1:
                urlName = "downloadTeacherTemplate";
                fileName="教职工导入模板.xlsx";
                break;
            case 2:
                urlName = "downloadSchoolTemplate";
                fileName="院校信息导入模板.xlsx";
                break;
            case 3:
                urlName = "downloadStudentTemplate";
                fileName="学生信息导入模板.xlsx";
                break;
            default:
                break;
        }

        if (urlName == null){
            return CommonResult.error(500,"请检查模板类型");
        }

        String path = URL+"excelTemplate/"+urlName;

        Map<String, Object> map = new HashMap<>(16);
        map.put("url",path);
        map.put("fileName",fileName);
        return CommonResult.success(map);
    }


}


