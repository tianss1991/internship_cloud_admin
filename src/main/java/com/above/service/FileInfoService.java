package com.above.service;

import com.above.dto.UserDto;
import com.above.po.FileInfo;
import com.above.vo.FileVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mp
 * @since 2022-07-07
 */
public interface FileInfoService extends IService<FileInfo> {

    /**
     *  保存上传的文件信息
     * @param vo 前端参数
     * @return 返回true成功
     */
    boolean saveFileInfo(FileVo vo);

}
