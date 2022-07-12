package com.above.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author BANGO
 */
@Accessors(chain = true)
@Data
public class BaseVo {
    /**
     * 总数
     */
    public static final transient String TOTAL = "totalCount";
    /**
     * 总页数
     */
    public static final transient String PAGE = "pages";
    /**
     * 集合
     */
    public static final transient String LIST = "list";
    /**
     * 逻辑删除：0=未删除，1=删除
     */
    public static final transient Integer UNDELETE = 0;

    public static final transient Integer DELETE = 1;
    /**
     * 督导类型判断：1=校级督导，2=系级督导
     */
    public static final transient Integer SC_SUPERVISOR = 1;

    public static final transient Integer DEP_SUPERVISOR = 2;

    /**
     * 授权判断：4=校级督导，5=系级督导
     */
    public static final transient Integer SCHOOL_SUPERVISOR = 4;

    public static final transient Integer DEPARTMENT_SUPERVISOR = 5;

    public static final transient String ROLECODE_DEP = "instructorDepartment";

    public static final transient String ROLECODE_SCH = "instructorSchool";

    @ApiModelProperty(value = "页数")
    private Integer page = 1;

    @ApiModelProperty(value = "每页个数")
    private Integer size = 10;

    private Integer start;

    @ApiModelProperty(value = "学校id")
    private Integer schoolId;

    @ApiModelProperty(value = "用户id")
    private Integer userId;

    @ApiModelProperty(value = "二级学院id")
    private Integer departmentId;

    @ApiModelProperty(value = "班级id")
    private Integer classId;

    @ApiModelProperty(value = "班级idList")
    private List<Integer> classIdList;

    @ApiModelProperty(value = "年级id")
    private Integer gradeId;

    @ApiModelProperty(value = "实习计划id")
    private Integer planId;

    @ApiModelProperty(value = "专业id")
    private Integer majorId;

    @ApiModelProperty(value = "学校idList")
    private List<Integer> schoolIdList;

    @ApiModelProperty(value = "二级学院idList")
    private List<Integer> departmentIdList;

    @ApiModelProperty(value = "教师id")
    private Integer teacherId;

    @ApiModelProperty(value = "教师ids")
    private List<Integer> teacherIdList;

    @ApiModelProperty(value = "通用筛选开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    @ApiModelProperty(value = "通用筛选结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "用户类型 0-管理员 1-学生 2-教师")
    private Integer userType;

    @ApiModelProperty(value = "搜索关键字")
    private String key;

    @ApiModelProperty(value = "状态")
//   请假单： value = "申请状态 1-待审核 2-驳回 3-通过（修改打卡状态）
    private Integer status;


    @ApiModelProperty("排序字段0-倒叙（DESC） 1-正序（ASC）")
    private Integer order;

    @ApiModelProperty("排序字段2 0-倒叙（DESC） 1-正序（ASC）")
    private Integer orderSecond;



    public Integer getStart() {
        if (page != null && size != null && page != 0) {
            start = (this.page - 1) * size;
        }
        return start;
    }


    public void setStart(Integer start) {
        this.start = start;
    }

    public static Integer calculationPages(Integer size, Integer totalCount) {
        return (totalCount + size - 1) / size;
    }
}
