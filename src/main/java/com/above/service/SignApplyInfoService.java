package com.above.service;

import com.above.dto.UserDto;
import com.above.exception.OptionDateBaseException;
import com.above.po.SignApplyInfo;
import com.above.utils.CommonResult;
import com.above.vo.SignApplyInfoVo;
import com.above.vo.SignInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 签到申请管理表 服务类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface SignApplyInfoService extends IService<SignApplyInfo> {

    /**
     *  学生提交补签申请
     * @param userDto 用户
     * @param vo 前端参数
     * @return 通用返回
     */
    CommonResult<Object> submitSignApply(UserDto userDto,SignInfoVo vo);

    /**
     *  学生修改补签申请
     * @param userDto 用户
     * @param vo 前端参数
     * @return 通用返回
     */
    CommonResult<Object> modifySignApply(UserDto userDto,SignInfoVo vo);

    /**
     *  学生撤回补签申请
     * @param userDto 用户
     * @param vo 前端参数
     * @return 通用返回
     */
    CommonResult<Object> withdrawSignApply(UserDto userDto,SignInfoVo vo);

    /**
     *  教师审核补签申请
     * @param userDto 用户
     * @param vo 前端参数
     * @return 通用返回
     */
    CommonResult<Object> auditSignApply(UserDto userDto,SignInfoVo vo) throws OptionDateBaseException;

    /**
     *  根据applyId获取审核详情
     * @param userDto 用户
     * @param vo 前端参数
     * @return 通用返回
     */
    CommonResult<Object> getSignApplyInfo(UserDto userDto,SignInfoVo vo);

    /**
     *  获取补签申请列表
     * @param userDto 用户
     * @param vo 前端参数
     * @return 通用返回
     */
    CommonResult<Object> getSignApplyList(UserDto userDto,SignInfoVo vo);
}
