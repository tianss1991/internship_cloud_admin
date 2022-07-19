package com.above.service;

import com.above.dto.UserDto;
import com.above.po.InternshipInfoByStudent;
import com.above.utils.CommonResult;
import com.above.vo.InternshipApplicationVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 学生实习信息表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface InternshipInfoByStudentService extends IService<InternshipInfoByStudent> {
    /*----------------------学生------------------------*/
    /**------------实习------------*/
    /**
    * 提交及修改实习申请
    * */
    CommonResult<Object> internshipApplySubmitOrUpdate(InternshipApplicationVo vo, UserDto userDto);
    /**
     * 删除实习申请
     * */
//    CommonResult<Object> internshipApplyDelete(InternshipApplicationVo vo, UserDto userDto);
    /**
     * 显示实习申请
     * */
    CommonResult<Object> internshipApplyDisplaySingle(InternshipApplicationVo vo, UserDto userDto);
    /**
     * 显示实习岗位列表
     * */
    CommonResult<Object> internshipApplyDisplayList(InternshipApplicationVo vo, UserDto userDto);
    /**
     * 实习岗位修改与企业变更申请
     * */
    CommonResult<Object> jobModifyAndCompanyModify(InternshipApplicationVo vo, UserDto userDto);

    /**
     *@author: GG
     *@data: 2022/7/19 10:24
     *@function:获取实习岗位已填报学生列表
     */
    CommonResult<Object> getInternshipApplyInfoFilled(InternshipApplicationVo vo, UserDto userDto);


    /**
     *@author: GG
     *@data: 2022/7/19 10:24
     *@function:获取实习岗位未填报学生列表
     */
    CommonResult<Object> getInternshipApplyInfoUnFill(InternshipApplicationVo vo, UserDto userDto);

    /**------------免实习------------*/
    /**
     * 提交及修改免实习申请
     * */
    CommonResult<Object> dismissInternshipApplySubmit(InternshipApplicationVo vo, UserDto userDto);
    /**
     * 删除免实习申请
     * */
//    CommonResult<Object> dismissInternshipApplyDelete(InternshipApplicationVo vo, UserDto userDto);
    /**
     * 显示免实习申请
     * */
    CommonResult<Object> dismissInternshipApplyDisplaySingle(InternshipApplicationVo vo, UserDto userDto);
    /**
     * 显示免实习岗位列表
     * */
//    CommonResult<Object> dismissInternshipApplyDisplayList(InternshipApplicationVo vo, UserDto userDto);


    /**------------就业上报------------*/
    /**
     * 提交就业上报
     * */
    CommonResult<Object> employmentReportedSubmitOrUpdate(InternshipApplicationVo vo, UserDto userDto);
    /**
     * 展示就业上报内容
     * */
    CommonResult<Object> employmentReportedDisplay(InternshipApplicationVo vo, UserDto userDto);
    /**
     * 展示就业上报列表
     * */
    CommonResult<Object> employmentReportedDisplayList(InternshipApplicationVo vo, UserDto userDto);


    /*----------------------教师------------------------*/
    /**
     * 实习申请、实习岗位修改与企业变更申请、免实习申请、就业上报审核列表（教师）
     * */
    CommonResult<Object> internshipInfoCheckedList(InternshipApplicationVo vo, UserDto userDto);
    /**
     * 实习审核列表
     * */
    CommonResult<Object> internshipInfoCheck(InternshipApplicationVo vo, UserDto userDto);
    /**
    *@author: GG
    *@data: 2022/7/5 11:30
    *@function:
    */
    CommonResult<Object> countInternshipInfoCheck(InternshipApplicationVo vo, UserDto userDto);
    /**
    *@author: GG
    *@data: 2022/7/12 9:22
    *@function:拿实习申请中实习审核、企业修改审核、岗位修改审核的数量
    */
    CommonResult<Object> countCheck(InternshipApplicationVo vo, UserDto userDto);



    /**------------通用------------*/
    /**
     * 学生撤回申请
     * */
    CommonResult<Object> withdrawApply(InternshipApplicationVo vo, UserDto userDto);




}
