package com.above.service.impl;

import com.above.dto.UserDto;
import com.above.po.GradeInfo;
import com.above.dao.GradeInfoMapper;
import com.above.po.SchoolInfo;
import com.above.service.GradeInfoService;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.GradeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 年级 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Service
public class GradeInfoServiceImpl extends ServiceImpl<GradeInfoMapper, GradeInfo> implements GradeInfoService {
    /**
     * 新建年级
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> addGradeInfo(GradeVo vo, UserDto userDto) {

        Integer schoolId = vo.getSchoolId();
        String gradeYear = vo.getGradeYear();
        Integer createBy = userDto.getId();

        GradeInfo gradeInfo = new GradeInfo();
        gradeInfo.setCreateBy(createBy);
        gradeInfo.setGradeYear(gradeYear);
        gradeInfo.setSchoolId(schoolId);

        //查询同一个学校下是否已经有存在同样的学期
        QueryWrapper<GradeInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolId).eq("grade_year",gradeYear);
        List<GradeInfo> list = this.list(queryWrapper);
        if(list.size()>0){
            return CommonResult.error(500,"该学校已存在同名年级");
        }
        boolean save = this.save(gradeInfo);
        if(!save){
            return CommonResult.error(500,"添加年级失败");
        }
        return CommonResult.success("添加年级成功");
    }

    /**
     * 删除年级
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> deleteGradeInfo(GradeVo vo, UserDto userDto) {
        Integer gradeId = vo.getGradeId();
        GradeInfo gradeInfo = this.getById(gradeId);
        if(gradeInfo!=null){
            //如果被删除，则返回
            if(gradeInfo.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该年级已被删除，无法再次进行删除操作");
            }
            gradeInfo.setDeleted(BaseVo.DELETE);
        }else{
            return CommonResult.error(500,"该年级不存在");
        }
        boolean save = this.updateById(gradeInfo);
        if(!save){
            return CommonResult.error(500,"删除失败");
        }
        return CommonResult.success("删除成功");
    }

    /**
     * 修改年级
     * */
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = RuntimeException.class)
    public CommonResult<Object> modifyGradeInfo(GradeVo vo, UserDto userDto) {
        Integer gradeId = vo.getGradeId();
        GradeInfo gradeInfo = this.getById(gradeId);
        if(gradeInfo!=null){
            //如果被删除，则返回
            if(gradeInfo.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该年级已被删除，无法再次进行删除操作");
            }
            if(vo.getGradeYear()!=null){
                String gradeYear = vo.getGradeYear();
                if(!gradeInfo.getGradeYear().equals(gradeYear)){
                    gradeInfo.setGradeYear(gradeYear);
                }else{
                    return CommonResult.error(500,"本次操作无改动");
                }
            }
        }else{
            return CommonResult.error(500,"该年级不存在");
        }
        boolean save = this.updateById(gradeInfo);
        if(!save){
            return CommonResult.error(500,"修改年级失败");
        }
        return CommonResult.success("修改年级成功");
    }

    /**
     * 查询年级
     * */
    @Override
    public CommonResult<Object> queryGradeInfo(GradeVo vo, UserDto userDto) {
        Integer gradeId = vo.getGradeId();
        GradeInfo gradeInfo = this.getById(gradeId);
        Map<String,Object> map = new HashMap<>();
        if(gradeInfo!=null){
            //如果被删除，则返回
            if(gradeInfo.getDeleted().equals(BaseVo.DELETE)){
                return CommonResult.error(500,"该年级已被删除，无法再次进行删除操作");
            }
            map.put("gradeInfo",gradeInfo);
        }else{
            return CommonResult.error(500,"该年级不存在");
        }
        return CommonResult.success(map);
    }

    /**
     * 通过学校查询所有年级
     * */
    @Override
    public CommonResult<Object> queryGradeList(GradeVo vo, UserDto userDto) {
        Integer schoolId = 0;
        if(userDto.getUserRoleDto().getRoleCode().equals("admin")){
            schoolId = vo.getSchoolId();
        }else if(userDto.getUserRoleDto().getRoleCode().equals("schoolAdmin") || userDto.getUserRoleDto().getRoleCode().equals("departmentAdmin")){
            schoolId = userDto.getTeacherInfo().getSchoolId();
        }

        Map<String,Object> map = new HashMap<>();
        QueryWrapper<GradeInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted",BaseVo.UNDELETE).eq("school_id",schoolId);
        List<GradeInfo> list = this.list(queryWrapper);
        if(list.size()>0){
            map.put("list",list);
        }
        return CommonResult.success(map);
    }
}
