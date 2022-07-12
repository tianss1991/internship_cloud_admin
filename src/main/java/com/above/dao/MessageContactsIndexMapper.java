package com.above.dao;

import com.above.po.MessageContactsIndex;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 消息联系人索引表;本表只存储用户互相沟通的最后一句话，方便消息中联系人列表的内容） Mapper 接口
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
public interface MessageContactsIndexMapper extends BaseMapper<MessageContactsIndex> {

}
