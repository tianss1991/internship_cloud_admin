package com.above.dao;

import com.above.dto.ClassReviewDto;
import com.above.dto.UserList;
import com.above.po.UserAccount;
import com.above.vo.user.UserVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 用户账号信息表（记录用户关联的手机，邮箱，微信等账号信息） Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-01-16
 */
@Repository
public interface UserAccountMapper extends BaseMapper<UserAccount> {
    /**
     *<p>
     *展示教师列表
     *</p>
     * @since 2022-04-14
     * **/
    List<UserList> getAllTeacherByDepartment(UserVo userVo);
    /**
     *<p>
     *展示游客列表
     *</p>
     * @since 2022-04-14
     * @param userVo**/
    List<UserList> getAllVisitor(UserVo userVo);
    /**
     *<p>
     *获取二级学院教师总数
     *</p>
     * @since 2022-04-15
     * **/
    Integer getTotalTeacherByDepartment(UserVo userVo);
    /**
     *<p>
     *获取游客总数
     *</p>
     * @since 2022-04-15
     * @param userVo**/
    Integer getTotalVisitor(UserVo userVo);
    /**
     *<p>
     *获取具体游客教评集合
     *</p>
     * @since 2022-04-15
     * @param userVo**/
    List<ClassReviewDto> getSingleVisitor(UserVo userVo);
    /**
     *<p>
     *获取游客评价总数
     *</p>
     * @since 2022-04-19
     * @param userVo**/
    Integer getTotalVisitorEvaluate(UserVo userVo);
    /**
     *<p>
     *通过超管id获取邀请游客集合(分页)
     *</p>
     * @since 2022-04-28
     * @param userVo**/
    List<UserList> getAllVisitorByManagerPage(UserVo userVo);
    /**
     *<p>
     *通过超管id获取邀请游客集合(不分页)
     *</p>
     * @since 2022-04-28
     * @param userVo**/
    List<UserList> getAllVisitorByManagerNoPage(UserVo userVo);
    /**
     *<p>
     *通过超管id获取获取游客总数
     *</p>
     * @since 2022-04-28
     * @param userVo**/
    Integer getTotalVisitorByManager(UserVo userVo);



}
