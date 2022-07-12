package com.above.service.impl;

import com.above.dao.*;
import com.above.dto.PayrollDto;
import com.above.dto.UserDto;
import com.above.po.*;
import com.above.service.PayrollService;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.PayrollVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * <p>
 * 工资单 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Slf4j
@Service
public class PayrollServiceImpl extends ServiceImpl<PayrollMapper, Payroll> implements PayrollService {

    @Autowired
    private InternshipInfoByStudentMapper infoByStudentMapper;
    @Autowired
    private StudentInfoMapper studentInfoMapper;
    @Autowired
    private PayrollMapper payrollMapper;
    @Autowired
    private InternshipWithTeacherMapper internshipWithTeacherMapper;
    @Autowired
    private UserAccountMapper userAccountMapper;
    @Autowired
    private TeacherInfoMapper teacherInfoMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private InternshipInfoByStudentMapper internshipInfoByStudentMapper;


    //新增工资单
    @Override
    public CommonResult<Object> addPayroll(UserDto userDto, PayrollVo vo) {
        //获取参数
        Integer createBy = userDto.getId();
        Date dateTime = vo.getDateTime();
        String imgUrl = vo.getImgUrl();
        String number = userDto.getAccountNumber();

        //查询学生是否存在
        QueryWrapper<StudentInfo> qw = new QueryWrapper<>();
        qw.eq("student_number",number);
        StudentInfo studentInfo = studentInfoMapper.selectOne(qw);
        if (studentInfo==null){
            return CommonResult.error(500,"学生不存在");
        }
        //获取学生id
        Integer id = studentInfo.getId();

        QueryWrapper<InternshipInfoByStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("relation_student_id",id);
        InternshipInfoByStudent infoByStudent = infoByStudentMapper.selectOne(queryWrapper);
        //获取学生关联实习id
        Integer infoByStudentId = infoByStudent.getId();
        if (infoByStudentId==null){
            return CommonResult.error(500,"缺少实习id");
        }

        //创建工资单实体类
        Payroll payroll = new Payroll();
        //工资
        payroll.setSalary(vo.getSalary());
        //时间
        payroll.setDateTime(dateTime);
        //图片地址
        payroll.setImgUrl(imgUrl);
        //学生id
        payroll.setStudentId(id);
        //创建人
        payroll.setCreateBy(createBy);
        //学生实习id
        payroll.setInternshipId(infoByStudentId);
        boolean save = this.save(payroll);

        if (save){
            return CommonResult.success("添加成功",null);
        }else {
            return CommonResult.error(500,"添加失败");
        }

    }

    //查询工资单列表-学生端
    @Override
    public CommonResult<Object> getStudentPayrollList(UserDto userDto, PayrollVo vo) {
        Integer userType=userDto.getUserType();
        String number = userDto.getAccountNumber();

        //查询学生是否存在
        QueryWrapper<StudentInfo> qw = new QueryWrapper<>();
        qw.eq("student_number",number)
          .eq("deleted",BaseVo.UNDELETE);
        StudentInfo studentInfo = studentInfoMapper.selectOne(qw);
        if (studentInfo==null){
            return CommonResult.error(500,"学生不存在");
        }
        //获取学生id
        Integer id = studentInfo.getId();
        Payroll studentId = vo.setStudentId(id);

        List<PayrollDto> studentPayrollList = payrollMapper.getStudentPayrollList(vo);

        Map<String, Object> returnMap = new HashMap<>(16);
        returnMap.put(BaseVo.LIST,studentPayrollList);
        return CommonResult.success(returnMap);
    }

    //查询工资单列表-辅导员端
    @Override
    public CommonResult<Object> getInstructorPayrollList(UserDto userDto, PayrollVo vo) {
        Integer userType = userDto.getUserType();
        String number = userDto.getAccountNumber();
        //获取辅导员id
        QueryWrapper<TeacherInfo> qw = new QueryWrapper<>();
        qw.eq("work_number",number);
        TeacherInfo teacherInfo = teacherInfoMapper.selectOne(qw);
        if (teacherInfo==null){
            return CommonResult.error(500,"该辅导员不存在");
        }
        Integer teacherId = teacherInfo.getUserId();
        vo.setTeacherId(teacherId);

        HashMap<String, Object> hashMap = new HashMap<>(16);
        List<PayrollDto> instructorPayrollList = payrollMapper.getInstructorPayrollList(vo);
        hashMap.put(BaseVo.LIST,instructorPayrollList);

        return CommonResult.success(hashMap);
    }

    //查询工资单详情
    @Override
    public CommonResult<Object> getPayrollDeta(UserDto userDto, PayrollVo vo) {
        Integer studentId = vo.getStudentId();

        if (studentId==null || studentId==0){
            return CommonResult.error(500,"缺少学生id");
        }
        //获取学生编号
        QueryWrapper<StudentInfo> qw = new QueryWrapper<>();
        qw.eq("id",studentId).eq("deleted",BaseVo.UNDELETE);
        StudentInfo studentInfo = studentInfoMapper.selectOne(qw);
        Integer userId = studentInfo.getUserId();

        //获取学生名字和头像
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id",userId).eq("deleted",BaseVo.UNDELETE);
        User user = userMapper.selectOne(wrapper);
        String userName = user.getUserName();
        String userAvatar = user.getUserAvatar();
        //获取学生实习工资、实习时间、图片
        QueryWrapper<Payroll> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", BaseVo.UNDELETE)
                    .eq("student_id",studentId);
        Payroll payroll = payrollMapper.selectOne(queryWrapper);
        //学生实习工资
        BigDecimal salary = payroll.getSalary();
        //实习时间
        Date dateTime = payroll.getDateTime();
        //工资单创建时间
        Date createTime = payroll.getCreateTime();
        //图片
        String imgUrl = payroll.getImgUrl();
        //获取学生单位岗位
        QueryWrapper<InternshipInfoByStudent> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("relation_student_id",studentId).eq("deleted", BaseVo.UNDELETE);
        InternshipInfoByStudent iibs = internshipInfoByStudentMapper.selectOne(wrapper1);
        String companyName = iibs.getCompanyName();
        String jobName = iibs.getJobName();

        PayrollDto dto = new PayrollDto();
        dto.setCreateTime(createTime);
        dto.setCompanyName(companyName);
        dto.setJobName(jobName);
        dto.setDateTime(dateTime);
        dto.setUserAvatar(userAvatar);
        dto.setUserName(userName);
        dto.setSalary(salary);
        dto.setImgUrl(imgUrl);

        return CommonResult.success(dto);
    }

    //pc端工资单列表管理(带筛选)
    @Override
    public CommonResult<Object> Payroll(UserDto userDto, PayrollVo vo) {

        String studentName = vo.getStudentName();
        String gradeYear = vo.getGradeYear();
        String majorName = vo.getMajorName();
        Date dateTime = vo.getDateTime();
        if (StringUtils.isEmpty(studentName)){
            vo.setStudentName(studentName);
        }
        if (StringUtils.isEmpty(gradeYear)){
            vo.setGradeYear(gradeYear);
        }
        if (StringUtils.isEmpty(majorName)){
            vo.setMajorName(majorName);
        }
        if (dateTime!=null){
            vo.setDateTime(dateTime);
        }

        Long page = vo.getPage();
        if (vo.getPage() != null && vo.getPage() != 0) {
            vo.setPage((vo.getPage() - 1) * vo.getSize());
        }

        List<PayrollDto> payrollList = payrollMapper.getPayroll(vo);

        int total = this.payrollMapper.countPayroll(vo);

        Map<String, Object> returnMap = new HashMap<>(16);

        returnMap.put("total", total);

        returnMap.put("list", payrollList.size() == 0 ? new ArrayList<>() : payrollList);

        returnMap.put("pages", (page == null || page == 0) ? 1 : (total + vo.getSize() - 1) / vo.getSize());

        return CommonResult.success(returnMap);
    }


}
