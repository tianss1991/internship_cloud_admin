package com.above.config.easyExcel;

import com.above.bean.excel.MajorInfoExcelData;
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
public class MajorInfoExcelListener extends AnalysisEventListener<MajorInfoExcelData>  {
    //可以通过实例获取该值
    private List<Object> datas = new ArrayList<Object>();

    @Override
    public void invoke(MajorInfoExcelData o, AnalysisContext analysisContext) {
        System.out.println(o);
        datas.add(o);//数据存储到list，供批量处理，或后续自己业务逻辑处理。
        doSomething(o);//根据自己业务做处理
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JSON.toJSONString(headMap));
        if (headMap.size()!= 2) {
            throw new RuntimeException("模板信息不匹配，请下载模板进行编辑");
        }
        if (!headMap.get(0).equals("学校名称")){
            throw new RuntimeException("模板信息不匹配，请下载模板进行编辑");
        }
        if (!headMap.get(1).equals("课程名称")){
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