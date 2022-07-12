package com.above.service.impl;

import com.above.dao.InternshipInfoByStudentMapper;
import com.above.dao.StudentInfoMapper;
import com.above.dto.ReportWithStudentDto;
import com.above.dto.UserDto;
import com.above.po.InternshipInfoByStudent;
import com.above.po.ReportWithStudent;
import com.above.dao.ReportWithStudentMapper;
import com.above.po.StudentInfo;
import com.above.service.ReportWithStudentService;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.ReportWithStudentVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 学生报告表 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Service
public class ReportWithStudentServiceImpl extends ServiceImpl<ReportWithStudentMapper, ReportWithStudent> implements ReportWithStudentService {

    @Autowired
    private StudentInfoMapper studentInfoMapper;
    @Autowired
    private InternshipInfoByStudentMapper internshipInfoByStudentMapper;
    @Autowired
    private ReportWithStudentMapper reportWithStudentMapper;

    //新增日报
    @Override
    public CommonResult<Object> addDailyPaper(UserDto userDto, ReportWithStudentVo vo) {
        //获取参数
        Integer createBy = userDto.getId();
        String number = userDto.getAccountNumber();
        String title = vo.getTitle();
        String content = vo.getContent();
        String imgUrl = vo.getImgUrl();

        //查询学生是否存在
        QueryWrapper<StudentInfo> qw = new QueryWrapper<>();
        qw.eq("student_number", number)
                .eq("deleted", BaseVo.UNDELETE);
        StudentInfo studentInfo = studentInfoMapper.selectOne(qw);
        if (studentInfo == null) {
            return CommonResult.error(500, "学生不存在");
        }
        //获取学生id
        Integer id = studentInfo.getId();

        //获取关联实习id
        QueryWrapper<InternshipInfoByStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("relation_student_id", id)
                .eq("deleted", BaseVo.UNDELETE);
        InternshipInfoByStudent byStudent = internshipInfoByStudentMapper.selectOne(queryWrapper);
        Integer internshipId = byStudent.getId();
        if (internshipId == null) {
            return CommonResult.error(500, "缺少实习id");
        }


        if (StringUtils.isEmpty(title)) {
            return CommonResult.error(500, "请输入标题");
        }
        if (StringUtils.isEmpty(content)) {
            return CommonResult.error(500, "请输入日报内容");
        }
        if (StringUtils.isEmpty(imgUrl)) {
            return CommonResult.error(500, "图片不能为空");
        }
        //创建学生报告表实体类
        ReportWithStudent rws = new ReportWithStudent();
        //关联实习id
        rws.setInternshipId(internshipId);
        //学生id
        rws.setStudentId(id);
        //日报状态
        rws.setStatus(1);
        //报告类型
        rws.setReportType(1);
        //创建人
        rws.setCreateBy(createBy);
        //标题
        rws.setTitle(title);
        //内容
        rws.setContent(content);
        //图片
        rws.setImgUrl(imgUrl);
        //链接
        rws.setUrl(vo.getUrl());
        boolean save = this.save(rws);

        if (save) {
            return CommonResult.success("新增成功", null);
        } else {
            return CommonResult.error(500, "新增失败");
        }
    }

    //查询学生日报(学生端)
    @Override
    public CommonResult<Object> getDailyPaperStudent(UserDto userDto, ReportWithStudentVo vo) {
        String number = userDto.getAccountNumber();
        String userName = userDto.getUserName();
        String userAvatar = userDto.getUserAvatar();

        //查询学生是否存在
        QueryWrapper<StudentInfo> qw = new QueryWrapper<>();
        qw.eq("student_number", number)
                .eq("deleted", BaseVo.UNDELETE);
        StudentInfo studentInfo = studentInfoMapper.selectOne(qw);
        if (studentInfo == null) {
            return CommonResult.error(500, "学生不存在");
        }
        //获取学生id
        Integer id = studentInfo.getId();

        //查询条件
        QueryWrapper<ReportWithStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", id)
                .eq("deleted", BaseVo.UNDELETE);

        HashMap<String, Object> map = new HashMap<>(16);
        List<ReportWithStudent> list = this.list(queryWrapper);

        ArrayList<ReportWithStudentDto> returnList = new ArrayList<>();
        for (ReportWithStudent reportWithStudent : list) {
            ReportWithStudentDto dto = new ReportWithStudentDto();
            BeanUtils.copyProperties(reportWithStudent, dto);
            dto.setUserName(userName);
            dto.setUserAvatar(userAvatar);

            returnList.add(dto);
        }
        map.put(BaseVo.LIST, returnList);
        return CommonResult.success(map);
    }

    //撤回日报
    @Override
    public CommonResult<Object> revocationDailyPaper(UserDto userDto, ReportWithStudentVo vo) {
        //获取参数
        Integer updateBy = userDto.getId();
        String number = userDto.getAccountNumber();
        Integer rwsid = vo.getId();
        Integer status = vo.getStatus();

        //查询学生是否存在
        QueryWrapper<StudentInfo> qw = new QueryWrapper<>();
        qw.eq("student_number", number)
                .eq("deleted", BaseVo.UNDELETE);
        StudentInfo studentInfo = studentInfoMapper.selectOne(qw);
        if (studentInfo == null) {
            return CommonResult.error(500, "学生不存在");
        }
        //获取学生id
        Integer id = studentInfo.getId();

        QueryWrapper<ReportWithStudent> queryWrapper = new QueryWrapper<>();
        if (status == 1) {
            queryWrapper.eq("id", rwsid)
                    .eq("deleted", BaseVo.UNDELETE);
        } else {
            return CommonResult.error(500, "该日报不存在");
        }
        ReportWithStudent reportWithStudent = reportWithStudentMapper.selectOne(queryWrapper);
        reportWithStudent.setStatus(0);
        reportWithStudent.setUpdateBy(updateBy);
        HashMap<String, Object> map = new HashMap<>();
        boolean save = this.update(reportWithStudent, queryWrapper);

        if (save) {
            return CommonResult.success("撤回成功", null);
        } else {
            return CommonResult.error(500, "撤回失败");
        }
    }

    //修改/编辑日报
    @Override
    public CommonResult<Object> updateDailyPaper(UserDto userDto, ReportWithStudentVo vo) {
        {
            Integer updateBy = userDto.getId();
            Integer id = vo.getId();
            Integer status = vo.getStatus();

            if (id == null || id==0) {
                return CommonResult.error(500, "日报id为空");
            }
            if (status == null) {
                return CommonResult.error(500, "学生状态为空");
            }

            if (status == 0) {
                QueryWrapper<ReportWithStudent> qw = new QueryWrapper<>();
                qw.eq("id", id)
                        .eq("deleted", BaseVo.UNDELETE);
                ReportWithStudent rws = reportWithStudentMapper.selectOne(qw);
                rws.setTitle(vo.getTitle());
                rws.setContent(vo.getContent());
                rws.setImgUrl(vo.getImgUrl());
                rws.setUrl(vo.getUrl());
                rws.setUpdateBy(updateBy);
                int update = reportWithStudentMapper.update(rws, qw);
            }
            if (status == 2) {
                QueryWrapper<ReportWithStudent> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", id)
                        .eq("deleted", BaseVo.UNDELETE);
                ReportWithStudent reportWithStudent = reportWithStudentMapper.selectOne(queryWrapper);
                reportWithStudent.setTitle(vo.getTitle());
                reportWithStudent.setContent(vo.getContent());
                reportWithStudent.setImgUrl(vo.getImgUrl());
                reportWithStudent.setUrl(vo.getUrl());
                reportWithStudent.setUpdateBy(updateBy);
                int update = reportWithStudentMapper.update(reportWithStudent, queryWrapper);
            }

            return CommonResult.success("修改成功", null);
        }



    }
}
