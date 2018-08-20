package com.zh.qq.service;

import com.zh.qq.pojo.AdminNotice;
import com.zh.qq.pojo.vo.AdminNoticeVO;

import java.util.List;

public interface AdminService {
    public void saveNotice(AdminNotice adminNotice);

    public List<AdminNoticeVO> showNoticeList();
}
