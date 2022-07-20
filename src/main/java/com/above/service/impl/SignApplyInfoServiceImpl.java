package com.above.service.impl;

import com.above.dao.SignInfoByStudentMapper;
import com.above.dto.SignApplyWithStudentDto;
import com.above.dto.UserDto;
import com.above.exception.OptionDateBaseException;
import com.above.po.SignApplyInfo;
import com.above.dao.SignApplyInfoMapper;
import com.above.po.SignInfoByStudent;
import com.above.service.SignApplyInfoService;
import com.above.service.SignInfoByStudentService;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.SignApplyInfoVo;
import com.above.vo.SignInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 签到申请管理表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Service
public class SignApplyInfoServiceImpl extends ServiceImpl<SignApplyInfoMapper, SignApplyInfo> implements SignApplyInfoService {

    @Autowired
    private SignInfoByStudentMapper signInfoByStudentMapper;

    /**
     * 学生提交补签申请
     *
     * @param userDto 用户
     * @param vo      前端参数
     * @return 通用返回
     */
    @Override
    public CommonResult<Object> submitSignApply(UserDto userDto, SignInfoVo vo) {

        //获取打卡记录
        SignInfoByStudent signInfoByStudent = this.signInfoByStudentMapper.selectById(vo.getSignId());
        if (signInfoByStudent == null){
            return CommonResult.error(500,"当前状态无法申请打卡记录");
        }
        if (signInfoByStudent.getSignStatus().equals(SignInfoByStudent.EXCEPTION)){
            return CommonResult.error(500,"当前状态无法申请打卡记录");
        }

        //查询该学生是否对该打卡提交过记录
        LambdaQueryWrapper<SignApplyInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SignApplyInfo::getSignId,vo.getSignId())
                .eq(SignApplyInfo::getStudentId,userDto.getStudentInfo().getId())
                .eq(SignApplyInfo::getDeleted, BaseVo.UNDELETE);
        int count = super.count(queryWrapper);
        if (count > 0){
            return CommonResult.error(500,"您已提交过补卡申请");
        }

        SignApplyInfo signApplyInfo = new SignApplyInfo();
        signApplyInfo.setApplyTime(vo.getSignDateTime()).setApplyType(1).setSignId(vo.getSignId()).setStudentId(userDto.getStudentInfo().getId())
                    .setInternshipPlanId(userDto.getInternshipPlanInfo().getId()).setInternshipId(userDto.getInternshipInfo().getId())
                    .setReason(vo.getReason()).setCreateBy(userDto.getId()).setStatus(1);
        if (vo.getImgUrl() != null){
            signApplyInfo.setImgUrl(vo.getImgUrl());
        }
        //查看是否是草稿
        if (vo.getIsDraft() != null && vo.getIsDraft() == 1){
            signApplyInfo.setStatus(0);
        }
        //存入数据库
        boolean save = super.save(signApplyInfo);

        if (!save){
            return CommonResult.error(500,"提交失败");
        }

        return CommonResult.success("提交成功");
    }

    /**
     * 学生修改补签申请
     *
     * @param userDto 用户
     * @param vo      前端参数
     * @return 通用返回
     */
    @Override
    public CommonResult<Object> modifySignApply(UserDto userDto, SignInfoVo vo) {

        SignApplyInfo applyInfo = super.getById(vo.getApplyId());
        //判断是否为审核中的状态
        if (!applyInfo.getStatus().equals(SignApplyInfo.SUCCESS)){
            return CommonResult.error(500,"补卡申请审核通过无法修改");
        }
        applyInfo.setStatus(SignApplyInfo.AUDITING).setUpdateBy(userDto.getId());
        boolean falg = false;
        //判断参数不为空则更新
        if (!StringUtils.isBlank(vo.getImgUrl())){
            applyInfo.setImgUrl(vo.getImgUrl());
            falg = true;
        }
        if (!StringUtils.isBlank(vo.getReason())){
            applyInfo.setReason(vo.getReason());
            falg = true;
        }
        if (vo.getSignDateTime() != null){
            applyInfo.setApplyTime(vo.getSignDateTime());
            falg = true;
        }
        //更新数据库
        if (falg){
            boolean update = super.updateById(applyInfo);
            return update ? CommonResult.success("修改成功") : CommonResult.error(500,"修改失败");
        }

        return CommonResult.success("无需更新");
    }

    /**
     * 学生撤回补签申请
     *
     * @param userDto 用户
     * @param vo      前端参数
     * @return 通用返回
     */
    @Override
    public CommonResult<Object> withdrawSignApply(UserDto userDto, SignInfoVo vo) {
        SignApplyInfo applyInfo = super.getById(vo.getApplyId());
        //判断是否为审核中的状态
        if (!applyInfo.getStatus().equals(SignApplyInfo.AUDITING)){
            return CommonResult.error(500,"只有审核中的申请可以撤回");
        }
        applyInfo.setStatus(SignApplyInfo.DRAFT).setUpdateBy(userDto.getId());

        boolean update = super.updateById(applyInfo);
        //判断是否成功
        if (update){
            return CommonResult.success("撤回成功");
        }else {
            return CommonResult.error(500,"撤回失败");
        }

    }

    /**
     * 教师审核补签申请
     *
     * @param userDto 用户
     * @param vo      前端参数
     * @return 通用返回
     */
    @Override
    @Transactional(rollbackFor = OptionDateBaseException.class,propagation = Propagation.REQUIRED)
    public CommonResult<Object> auditSignApply(UserDto userDto, SignInfoVo vo) throws OptionDateBaseException {

        SignApplyInfo applyInfo = super.getById(vo.getApplyId());
        //判断是否为审核中的状态
        if (!applyInfo.getStatus().equals(SignApplyInfo.AUDITING)){
            return CommonResult.error(500,"当前申请不在审核中，无法操作");
        }

        //获取操作的审核状态
        boolean success = vo.getAuditType() != null && vo.getAuditType() == 1;

        //true审核通过
        if (!success){
            /*审核失败执行*/
            if (StringUtils.isBlank(vo.getFailReason())){
                return CommonResult.error(500,"缺少失败理由");
            }
            //修改状态
            applyInfo.setStatus(SignApplyInfo.FAIL).setFailReason(vo.getFailReason()).setUpdateBy(userDto.getId());

            boolean update = super.updateById(applyInfo);
            //判断是否成功
            if (update){
                return CommonResult.success("操作成功");
            }else {
                return CommonResult.error(500,"操作失败");
            }

        }else {
            /*审核通过执行--更新打卡状态*/
            applyInfo.setStatus(SignApplyInfo.SUCCESS).setUpdateBy(userDto.getId());
            boolean update = super.updateById(applyInfo);
            //判断是否成功
            if (update){
                //成功后修改签到状态
                SignInfoByStudent signInfoByStudent = signInfoByStudentMapper.selectById(applyInfo.getSignId());

                signInfoByStudent.setSignTime(applyInfo.getApplyTime()).setSignStatus(SignInfoByStudent.NORMAL).setIsSign(1);

                int i = signInfoByStudentMapper.updateById(signInfoByStudent);

                if (i < 0){
                    throw new OptionDateBaseException("签到更新失败");
                }

                return CommonResult.success("操作成功");
            }else {
                return CommonResult.error(500,"操作失败");
            }
        }
    }

    /**
     * 根据applyId获取审核详情
     *
     * @param userDto 用户
     * @param vo      前端参数
     * @return 通用返回
     */
    @Override
    public CommonResult<Object> getSignApplyInfo(UserDto userDto, SignInfoVo vo) {

        SignApplyInfo applyInfo = super.getById(vo.getApplyId());
        //判断是否为审核中的状态
        if (applyInfo == null || applyInfo.getDeleted().equals(BaseVo.DELETE)){
            return CommonResult.error(500,"该申请不存在或已删除");
        }
        HashMap<String, Object> returnMap = new HashMap<>(7);

        returnMap.put("applyInfo",applyInfo);

        return CommonResult.success(returnMap);
    }

    /**
     * 获取补签申请列表
     *
     * @param userDto 用户
     * @param vo      前端参数
     * @return 通用返回
     */
    @Override
    public CommonResult<Object> getSignApplyList(UserDto userDto, SignInfoVo vo) {

        List<SignApplyWithStudentDto> list = super.baseMapper.getSignApplyList(vo);
        Integer totalCount = super.baseMapper.getSignApplyListTotalCount(vo);

        //返回对象
        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap.put(BaseVo.LIST,list);
        returnMap.put(BaseVo.TOTAL,totalCount);
        returnMap.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),totalCount));

        return CommonResult.success(returnMap);
    }


}
