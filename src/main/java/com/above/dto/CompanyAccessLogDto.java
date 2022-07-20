package com.above.dto;

import com.above.po.CompanyAccessLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author BANGO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors
public class CompanyAccessLogDto extends CompanyAccessLog {

    private String planTitle;

    private String schoolName;

    private String gradeYear;

    private String departmentName;

    private String majorName;

    private String className;

}
