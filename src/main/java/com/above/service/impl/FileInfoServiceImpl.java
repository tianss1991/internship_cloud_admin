package com.above.service.impl;

import com.above.dto.UserDto;
import com.above.po.FileInfo;
import com.above.dao.FileInfoMapper;
import com.above.service.FileInfoService;
import com.above.vo.BaseVo;
import com.above.vo.FileVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-07-07
 */
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {

    /**
     * 保存上传的文件信息
     *
     * @param vo      前端参数
     * @return 返回true成功
     */
    @Override
    public boolean saveFileInfo(FileVo vo) {
        FileInfo fileInfo = new FileInfo();
        //文件名
        String fileName = vo.getFileName();
        if (!StringUtils.isBlank(fileName)){
            //后缀
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            fileInfo.setFileName(fileName);
            fileInfo.setFileType(suffix);
        }

        fileInfo.setUrl(vo.getUrl());
        fileInfo.setType(vo.getFileType().toString());

        fileInfo.setFileStatus(BaseVo.UNDELETE);

        return super.save(fileInfo);
    }
}
