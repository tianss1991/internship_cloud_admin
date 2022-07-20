package com.above.dto;

import com.above.po.GradeTemplate;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author BANGO
 */
@Data
@Accessors(chain = true)
public class GradeTemplateDto extends GradeTemplate {


    /**
     * 子类
     */
   private List<GradeTemplateDto> children;

}
