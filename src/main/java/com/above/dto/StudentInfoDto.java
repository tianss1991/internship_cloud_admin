package com.above.dto;

import com.above.po.StudentInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author BANGO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class StudentInfoDto extends StudentInfo {

    private String schoolName;

    private String gradeName;

    private String departmentName;

    private String majorName;

    private String className;


}
