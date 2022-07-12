package com.above.config.easyExcel;

import com.above.bean.excel.TeacherInfoExcelData;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: excel监听器
 * @Author: LZH
 * @Date: 2021/10/19 10:13
 */
@Slf4j
public class TeacherInfoExcellListener extends AnalysisEventListener<TeacherInfoExcelData>  {
    //可以通过实例获取该值
    private List<Object> datas = new ArrayList<Object>();

    @Override
    public void invoke(TeacherInfoExcelData o, AnalysisContext analysisContext) {
        System.out.println(o);
        datas.add(o);//数据存储到list，供批量处理，或后续自己业务逻辑处理。
        doSomething(o);//根据自己业务做处理
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JSON.toJSONString(headMap));
        if (headMap.size()!= 7) {
            throw new RuntimeException("模板信息不匹配，请下载模板进行编辑");
        }
        if (!headMap.get(0).equals("学校名称")){
            throw new RuntimeException("模板信息不匹配，请下载模板进行编辑");
        }
        if (!headMap.get(1).equals("部门名称")){
            throw new RuntimeException("模板信息不匹配，请下载模板进行编辑");
        }
        if (!headMap.get(2).equals("姓名")){
            throw new RuntimeException("模板信息不匹配，请下载模板进行编辑");
        }
        if (!headMap.get(3).equals("性别")){
            throw new RuntimeException("模板信息不匹配，请下载模板进行编辑");
        }
        if (!headMap.get(4).equals("工号")){
            throw new RuntimeException("模板信息不匹配，请下载模板进行编辑");
        }
        if (!headMap.get(5).equals("移动电话")){
            throw new RuntimeException("模板信息不匹配，请下载模板进行编辑");
        }
        if (!headMap.get(6).equals("邮箱")){
            throw new RuntimeException("模板信息不匹配，请下载模板进行编辑");
        }


    }

    private void doSomething(Object object) {
        //1、入库调用接口
    }

    public List<Object> getDatas() {
        return datas;
    }

    public void setDatas(List<Object> datas) {
        this.datas = datas;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // datas.clear();//解析结束销毁不用的资源
    }
}