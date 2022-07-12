package com.above.vo;

import com.above.po.SchoolNotice;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SchoolNoticeVo extends SchoolNotice {

    @ApiModelProperty(value = "公告id集合")
    private List<Integer> schoolNoticeIdList;

}
