package com.above.service.impl;

import com.above.dao.DepartmentInfoMapper;
import com.above.dao.SchoolInfoMapper;
import com.above.dto.UserDto;
import com.above.po.*;
import com.above.dao.MajorInfoMapper;
import com.above.service.MajorInfoService;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.MajorInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 专业 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Slf4j
@Service
public class MajorInfoServiceImpl extends ServiceImpl<MajorInfoMapper, MajorInfo> implements MajorInfoService {

    @Autowired
    private SchoolInfoMapper schoolInfoMapper;
    @Autowired
    private DepartmentInfoMapper departmentInfoMapper;
    @Autowired
    private MajorInfoMapper majorInfoMapper;



    /**
     *添加专业
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public CommonResult<Object> addMajor(UserDto userDto, MajorInfoVo majorInfoVo) {

        Integer createBy = userDto.getId();
        Integer schoolId = majorInfoVo.getSchoolId();
        Integer departmentId = majorInfoVo.getDepartmentId();

        //判断学校是否存在
        SchoolInfo schoolInfo = schoolInfoMapper.selectById(schoolId);
        if (schoolInfo == null){
            return CommonResult.error(500,"学校不存在");
        }
        //判断二级学院是否存在
        DepartmentInfo departmentInfo = departmentInfoMapper.selectById(departmentId);
        if (departmentInfo == null) {
            return CommonResult.error(500, "二级学院不存在");
        }

        //判断专业名称是否重复
        QueryWrapper<MajorInfo>  majorInfoQueryWrapper = new QueryWrapper<>();
        majorInfoQueryWrapper.eq("department_id",departmentId)
                             .eq("major_name",majorInfoVo.getMajorName())
                             .eq("school_id",schoolId).eq("deleted", BaseVo.UNDELETE);

        int count = super.count(majorInfoQueryWrapper);
        if (count > 0) {
            return CommonResult.error(500, "名称重复");
        }
        //新建专业实体类
        MajorInfo major = new MajorInfo();
        //专业名称
        major.setMajorName(majorInfoVo.getMajorName());
        //院内专业代码
        major.setMajorCode(majorInfoVo.getMajorCode());
        //创建人
        major.setCreateBy(createBy);
        //关联学校id
        major.setSchoolId(schoolId);
        major.setSchoolName(schoolInfo.getSchoolName());
        //关联二级学院
        major.setDepartmentId(departmentId);
        major.setSchoolName(departmentInfo.getDepartmentName());

        boolean save = this.save(major);

         if (save) {
                //添加成功返回
                return CommonResult.success("添加成功", null);
         } else {
                return CommonResult.error(500, "添加专业失败");
                }

    }


    /**
     * 修改专业
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public CommonResult<Object> modifyMajInfo(UserDto userDto, MajorInfoVo majorInfoVo) {
        //获取参数
        boolean flag = false;
        Integer updateBy = userDto.getId();
        Integer majorId = majorInfoVo.getId();
        String majorName = majorInfoVo.getMajorName();

        MajorInfo majorInfo = super.getById(majorId);
        //判断二级学院是否存在
        if (majorInfo == null) {
            return CommonResult.error(500, "该专业不存在");
        }
        if (majorInfo.getDeleted() == 1) {
            return CommonResult.error(500, "该专业已删除");
        }
        //若专业名称与原名称不一致则修改
        if (!StringUtils.isBlank(majorName)){
            if (!majorInfo.getMajorName().equals(majorName)) {
                //设置新班级名称
                majorInfo.setMajorName(majorName);
                //修改flag状态
                flag = true;
            }
        }
        //判断是否有执行更新
        if (flag) {
            majorInfo.setUpdateBy(updateBy);
            //更新数据库
            boolean b = super.updateById(majorInfo);
            if (b) {
                return CommonResult.success("修改成功", null);
            } else {
                throw new RuntimeException("修改失败,请重试");
            }

        } else {
            //没操作更新直接返回
            return CommonResult.success("无需修改", null);
        }

    }

    /**
     * 删除专业
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public CommonResult<Object> deleteMajor(UserDto userDto, MajorInfoVo majorInfoVo) {
        //获取参数
        List<Integer> majorIdList = majorInfoVo.getMajorIdList();
        Integer updateby = userDto.getId();

        if (majorIdList.isEmpty()) {
            return CommonResult.error(500, "缺少二级学院id");
        }
        //批量查询
        List<MajorInfo> majorInfos = this.getBaseMapper().selectBatchIds(majorIdList);

        //循环设置删除状态和更新人
        for (MajorInfo majorInfo : majorInfos) {
           if (majorInfo.getDeleted().equals(BaseVo.DELETE)){
               return CommonResult.error(500, "请勿重复删除");
           }
           majorInfo.setUpdateBy(updateby);
           majorInfo.setDeleted(1);
        }
        //批量删除
        boolean b = this.updateBatchById(majorInfos);

        try {
            //删除成功后，删除相关信息
            if (b) {
                log.info("删除成功");
                return CommonResult.success("删除成功", null);
            } else {
                throw new RuntimeException("删除失败");
            }
        } catch (RuntimeException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info("异常打印-->"+e.getMessage());
            return CommonResult.error(500, e.getMessage());
        }
    }

    /**
     * 获取专业列表（分页）
     */
    @Override
    public CommonResult<Object> getmajorWithoutPage(UserDto userDto, MajorInfoVo majorInfoVo) {
        Integer userType=userDto.getUserType();
        Integer departmentId=0;
        String roleCode= userDto.getUserRoleDto().getRoleCode();

        //设置分页参数
        Page<MajorInfo> page = new Page<>(majorInfoVo.getPage(), majorInfoVo.getSize());
        //设置查找状态正常的学校
        QueryWrapper<MajorInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE);

        if (majorInfoVo.getSchoolId() != null && majorInfoVo.getSchoolId() > 0){
            queryWrapper.eq("school_id", majorInfoVo.getSchoolId());
        }
        if (majorInfoVo.getDepartmentId() != null && majorInfoVo.getDepartmentId() > 0){
            queryWrapper.eq("department_id",majorInfoVo.getDepartmentId());
        }

        //若有传key则筛选
        if (!StringUtils.isEmpty(majorInfoVo.getKey())) {
            queryWrapper.like("major_name", majorInfoVo.getKey());
        }
        IPage<MajorInfo> iPage = super.page(page, queryWrapper);
        //获取分页集合
        List<MajorInfo> departmentInfos = iPage.getRecords();

        //新建返回集合
        Map<String, Object> returnMap = new HashMap<>(16);

        //总页数
        returnMap.put(BaseVo.PAGE, iPage.getPages());
        //总数
        returnMap.put(BaseVo.TOTAL, iPage.getTotal());
        //返回数据
        returnMap.put(BaseVo.LIST,departmentInfos);

        return CommonResult.success(returnMap);
    }

    /**
     * 获取专业列表（不分页）
     */
    @Override
    public CommonResult<Object> getmajorPageList(UserDto userDto, MajorInfoVo majorInfoVo) {
        Integer userType = userDto.getUserType();
        Integer schoolId=0;
        Integer departmentId=0;
        //设置查找状态正常的学校
        QueryWrapper<MajorInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE);
        if (majorInfoVo.getSchoolId() != null && majorInfoVo.getSchoolId() >0 ){
            queryWrapper.eq("school_id",majorInfoVo.getSchoolId());
        }
        if (majorInfoVo.getDepartmentId() != null && majorInfoVo.getDepartmentId() >0){
            queryWrapper.eq("department_id",majorInfoVo.getDepartmentId());
        }

        //若有传key则筛选
        if (!StringUtils.isEmpty(majorInfoVo.getKey())) {
            queryWrapper.like("major_name", majorInfoVo.getKey());
        }
        List<MajorInfo> list=this.list(queryWrapper);

        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap.put(BaseVo.LIST,list);

        return CommonResult.success(returnMap);
    }


}



