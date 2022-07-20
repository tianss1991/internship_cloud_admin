package com.above.service.impl;

import com.above.dao.InternshipPlanInfoMapper;
import com.above.dto.CompanyAccessLogDto;
import com.above.dto.PlanWithOther;
import com.above.dto.UserDto;
import com.above.po.CompanyAccessLog;
import com.above.dao.CompanyAccessLogMapper;
import com.above.po.InternshipPlanInfo;
import com.above.po.TeacherInfo;
import com.above.service.CompanyAccessLogService;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.CompanyAccessVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.ehcache.sizeof.SizeOf;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 企业寻访记录表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-07-15
 */
@Service
public class CompanyAccessLogServiceImpl extends ServiceImpl<CompanyAccessLogMapper, CompanyAccessLog> implements CompanyAccessLogService {

    @Autowired
    private InternshipPlanInfoMapper internshipPlanInfoMapper;

    @Override
    public CommonResult<Object> addCompanyAccessLog(UserDto userDto, CompanyAccessVo vo) {

        TeacherInfo teacherInfo = userDto.getTeacherInfo();

        if (vo.getPlanId() == null){
            return CommonResult.error(500,"缺少实习计划id");
        }

        CompanyAccessLog companyAccessLog = new CompanyAccessLog();

        BeanUtils.copyProperties(vo,companyAccessLog);
        //设置用户关联
        companyAccessLog.setTeacherId(teacherInfo.getId());
        companyAccessLog.setRoleCode(userDto.getUserRoleDto().getRoleCode());
        companyAccessLog.setCreateBy(userDto.getId());
        //插入数据库
        boolean save = super.save(companyAccessLog);

        return save ? CommonResult.success("添加成功") : CommonResult.error(500,"添加失败");
    }

    /**
     * 修改记录
     *
     * @param userDto 操作用户
     * @param vo      前端参数
     * @return 通过返回
     */
    @Override
    public CommonResult<Object> editCompanyAccessLog(UserDto userDto, CompanyAccessVo vo) {

        CompanyAccessLog companyAccessLog = super.getById(vo.getId());
        //判断该记录是否已删除
        if (companyAccessLog == null || companyAccessLog.getDeleted().equals(BaseVo.DELETE)){
            return CommonResult.error(500,"该记录不存在或已删除");
        }
        //用于判断是否更新成功
        boolean flag = false;
        if (vo.getCompanyName() != null && !vo.getCompanyName().equals(companyAccessLog.getCompanyName())){
            companyAccessLog.setCompanyName(vo.getCompanyName());
            flag = true;
        }
        if (vo.getAddress() != null && !vo.getAddress().equals(companyAccessLog.getAddress())){
            companyAccessLog.setAddress(vo.getAddress());
            flag = true;
        }
        if (vo.getTitle() != null && !vo.getTitle().equals(companyAccessLog.getTitle())){
            companyAccessLog.setTitle(vo.getTitle());
            flag = true;
        }
        if (vo.getContent() != null && !vo.getContent().equals(companyAccessLog.getContent())){
            companyAccessLog.setContent(vo.getContent());
            flag = true;
        }
        if (vo.getImgUrl() != null && !vo.getImgUrl().equals(companyAccessLog.getImgUrl())){
            companyAccessLog.setImgUrl(vo.getImgUrl());
            flag = true;
        }
        //判断是否执行更新
        if (flag){

            boolean update = super.updateById(companyAccessLog);

            return update ? CommonResult.success("修改成功") : CommonResult.error(500,"修改失败");

        }else {
            return CommonResult.success("无需更新");
        }

    }

    /**
     * 小程序获取自己的寻坊列表
     *
     * @param userDto 操作用户
     * @param vo      前端参数
     * @return 通过返回
     */
    @Override
    public CommonResult<Object> getMyCompanyAccess(UserDto userDto, CompanyAccessVo vo) {
        //查询条件
        LambdaQueryWrapper<CompanyAccessLog> logLambdaQueryWrapper = new LambdaQueryWrapper<>();
        logLambdaQueryWrapper.eq(CompanyAccessLog::getCreateBy,userDto.getId()).eq(CompanyAccessLog::getDeleted,BaseVo.UNDELETE);

        List<CompanyAccessLog> list ;
        //返回对象
        HashMap<String, Object> returnMap = new HashMap<>(16);
        //判断是否分页
        if (vo.getPage() == 0){
            /*不分页查询*/
            list = super.list(logLambdaQueryWrapper);
        }else {
            /*分页查询*/
            Page<CompanyAccessLog> page = new Page<>(vo.getPage(), vo.getSize());
            IPage<CompanyAccessLog> iPage = super.page(page, logLambdaQueryWrapper);
            list = iPage.getRecords();
            returnMap.put(BaseVo.PAGE,iPage.getPages());
            returnMap.put(BaseVo.TOTAL,iPage.getTotal());
        }
        //返回列表
        returnMap.put(BaseVo.LIST,list);

        return CommonResult.success(returnMap);
    }

    /**
     * 管理端获取全部的寻坊列表
     *
     * @param userDto 操作用户
     * @param vo      前端参数
     * @return 通过返回
     */
    @Override
    public CommonResult<Object> getAllCompanyAccess(UserDto userDto, CompanyAccessVo vo) {

        List<CompanyAccessLogDto> list = super.baseMapper.getListForAdmin(vo);
        Integer totalCount = super.baseMapper.getListForAdminCount(vo);

        //返回对象
        HashMap<String, Object> returnMap = new HashMap<>(16);
        //返回列表
        returnMap.put(BaseVo.LIST,list);
        returnMap.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),totalCount));
        returnMap.put(BaseVo.TOTAL,totalCount);

        return CommonResult.success(returnMap);
    }

    /**
     * 设置寻坊次数
     *
     * @param userDto 操作用户
     * @param vo      前端参数
     * @return 通过返回
     */
    @Override
    public CommonResult<Object> setSize(UserDto userDto, CompanyAccessVo vo) {

        InternshipPlanInfo internshipPlanInfo = internshipPlanInfoMapper.selectById(vo.getPlanId());
        //判断状态
        if (internshipPlanInfo == null || internshipPlanInfo.getDeleted().equals(BaseVo.DELETE)){
            return CommonResult.error(500,"该实习计划不存在或者已删除");
        }
        //设置次数
        internshipPlanInfo.setInstructorSize(vo.getInstructorSize());
        internshipPlanInfo.setAdviserSize(vo.getAdviserSize());
        internshipPlanInfo.setUpdateBy(userDto.getId());
        //更新数据库
        boolean b = this.internshipPlanInfoMapper.updateById(internshipPlanInfo) > 0;

        return b ? CommonResult.success("设置成功") : CommonResult.error(500,"设置失败");
    }

    /**
     * 获取寻坊次数列表
     *
     * @param userDto 操作用户
     * @param vo      前端参数
     * @return 通过返回
     */
    @Override
    public CommonResult<Object> getSizeWithPlan(UserDto userDto, CompanyAccessVo vo) {

        List<PlanWithOther> list = super.baseMapper.getPlanWithOther(vo);
        Integer totalCount = super.baseMapper.getPlanWithOtherCount(vo);

        //返回对象
        HashMap<String, Object> returnMap = new HashMap<>(16);
        //返回列表
        returnMap.put(BaseVo.LIST,list);
        returnMap.put(BaseVo.PAGE,BaseVo.calculationPages(vo.getSize(),totalCount));
        returnMap.put(BaseVo.TOTAL,totalCount);

        return CommonResult.success(returnMap);
    }
}
