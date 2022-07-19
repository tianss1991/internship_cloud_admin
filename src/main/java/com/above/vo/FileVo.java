package com.above.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FileVo {

    /**
     * @Description: 文件类型 1-用户头像(图片) 2-实习申请（图片） 3-请假申请（图片） 4-学校公告（文件）
     * @Author: LZH
     * @Date: 2022/1/10 11:53
     */
    private static final String DEFAULT_FOLDER = "internship";

    private static final String INTERNSHIP ="/fileInfo/Internship_application/";
    private static final String LEAVE_APPLICATION = "/fileImg/leave_application/";
    private static final String AVA_IMG="/fileImg/ava/img/";
    private static final String SCHOOL_ANNOUNCEMENT ="/fileInfo/school_announcement/";
    private static final String STATION_LETTER ="/fileInfo/station_letter/";

    @ApiModelProperty("文件类型 1-用户头像(图片) 2-实习申请（文件） 3-请假申请（图片） 4-学校公告（文件）5-站内信（文件）")
    private Integer fileType;

    @ApiModelProperty("文件上传参数")
    private List<MultipartFile> files;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("保存文件的文件夹名称")
    private String folderName;

    @ApiModelProperty("文件保存地址")
    private String url;

    @ApiModelProperty("模板类型 1-学生 2- 教师 3-院校信息 4-题库 5-教师分配")
    private Integer templateType;

    public void setFileType(int fileType) {
        this.fileType = fileType;
        if (fileType != 0){
            switch (fileType){
                case 1:
                    this.folderName = DEFAULT_FOLDER + FileVo.AVA_IMG;
                    break;
                case 2:
                    this.folderName = DEFAULT_FOLDER + FileVo.INTERNSHIP;
                    break;
                case 3:
                    this.folderName = DEFAULT_FOLDER + FileVo.LEAVE_APPLICATION;
                    break;
                case 4:
                    this.folderName = DEFAULT_FOLDER + FileVo.SCHOOL_ANNOUNCEMENT;
                    break;
                case 5:
                    this.folderName = DEFAULT_FOLDER + FileVo.STATION_LETTER;
                    break;
                default:
                    this.folderName = null;
                    break;
            }
        }

    }
}
