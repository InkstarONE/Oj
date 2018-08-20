package com.zh.qq.mapper;

import com.zh.qq.pojo.AdminNotice;
import com.zh.qq.pojo.vo.AdminNoticeVO;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AdminNoticeMapper extends Mapper<AdminNotice> {
    public void saveUser(AdminNotice adminNotice);

    public List<AdminNoticeVO> showNoticeList();
}
