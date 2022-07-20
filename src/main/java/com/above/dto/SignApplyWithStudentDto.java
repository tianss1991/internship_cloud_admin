package com.above.dto;

import com.above.po.SignApplyInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author BANGO
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class SignApplyWithStudentDto extends SignApplyInfo {

    private String studentName;

}
