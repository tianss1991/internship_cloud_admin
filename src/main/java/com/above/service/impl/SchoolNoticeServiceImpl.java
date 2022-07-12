package com.above.service.impl;

import com.above.dto.UserDto;
import com.above.po.DepartmentInfo;
import com.above.po.SchoolInfo;
import com.above.po.SchoolNotice;
import com.above.dao.SchoolNoticeMapper;
import com.above.service.SchoolNoticeService;
import com.above.utils.CommonResult;
import com.above.vo.BaseVo;
import com.above.vo.SchoolNoticeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 学校公告 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Slf4j
@Service
public class SchoolNoticeServiceImpl extends ServiceImpl<SchoolNoticeMapper, SchoolNotice> implements SchoolNoticeService {

    //发布学校公告
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public CommonResult<Object> addNotice(UserDto userDto, SchoolNoticeVo vo) {
        //获取参数
        Integer createBy = userDto.getId();
        String userName = userDto.getUserName();
        String title = vo.getTitle();
        String content = vo.getContent();
        Date periodOfValidity = vo.getPeriodOfValidity();
        Integer status = vo.getStatus();

        //创建学校公告实体类
        SchoolNotice schoolNotice = new SchoolNotice();
        //标题
        schoolNotice.setTitle(title);
        //内容
        schoolNotice.setContent(content);
        //公告类型
        schoolNotice.setType(1);
        //有效期
        schoolNotice.setPeriodOfValidity(periodOfValidity);
        //公告状态
        schoolNotice.setStatus(status);
        //创建人
        schoolNotice.setCreateBy(createBy);
        schoolNotice.setIssuer(userName);
        boolean save = this.save(schoolNotice);

            if (save) {
                //添加成功返回
                return CommonResult.success("发布成功", null);
            } else {
                return CommonResult.error(500, "发布失败");
            }

    }

    //查询学校公告
    @Override
    public CommonResult<Object> getNoticeList(UserDto userDto, SchoolNoticeVo vo) {

        QueryWrapper<SchoolNotice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", BaseVo.UNDELETE);
        List<SchoolNotice> list = this.list(queryWrapper);

        HashMap<String, Object> returnMap = new HashMap<>(16);
        returnMap.put(BaseVo.LIST,list);

        return CommonResult.success(returnMap);
    }

    //公告详情
    @Override
    public CommonResult<Object> getNotice(UserDto userDto, SchoolNoticeVo vo) {
        //获取参数
        Integer id = vo.getId();

        QueryWrapper<SchoolNotice> queryWrapper = new QueryWrapper<>();
        if (id!=null && id>0){
            queryWrapper.eq("id",id);
        }
        if (vo.getDeleted()!=null){
            queryWrapper.eq("deleted", BaseVo.UNDELETE);
        }

        List<SchoolNotice> list = this.list(queryWrapper);
        HashMap<String, Object> returnMap = new HashMap<>(16);
        returnMap.put(BaseVo.LIST,list);
        return CommonResult.success(returnMap);
    }

}
