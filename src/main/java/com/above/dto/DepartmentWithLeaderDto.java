package com.above.dto;

import com.above.po.DepartmentInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BANGO
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DepartmentWithLeaderDto extends DepartmentInfo {

    List<LeaderList> list = new ArrayList<>();


}
