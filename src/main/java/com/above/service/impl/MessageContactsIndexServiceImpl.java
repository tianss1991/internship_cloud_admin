package com.above.service.impl;

import com.above.po.MessageContactsIndex;
import com.above.dao.MessageContactsIndexMapper;
import com.above.service.MessageContactsIndexService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息联系人索引表;本表只存储用户互相沟通的最后一句话，方便消息中联系人列表的内容） 服务实现类
 * </p>
 *
 * @author mp
 * @since 2022-06-21
 */
@Service
public class MessageContactsIndexServiceImpl extends ServiceImpl<MessageContactsIndexMapper, MessageContactsIndex> implements MessageContactsIndexService {

}
