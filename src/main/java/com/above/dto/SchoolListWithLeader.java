package com.above.dto;

import com.above.po.SchoolInfo;
import lombok.Data;

import java.util.List;

/**
 * @Description: 获取学校的领导
 * @Author: LZH
 * @Date: 2022/1/10 19:49
 */
@Data
public class SchoolListWithLeader extends SchoolInfo {
    List<LeaderList> list;
}
