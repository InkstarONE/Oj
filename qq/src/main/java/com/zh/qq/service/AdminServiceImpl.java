package com.zh.qq.service;

import com.zh.qq.mapper.AdminNoticeMapper;
import com.zh.qq.pojo.AdminNotice;
import com.zh.qq.pojo.vo.AdminNoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminNoticeMapper adminNoticeMapper;


    @Override
    public void saveNotice(AdminNotice adminNotice) {
         adminNoticeMapper.saveUser(adminNotice);
    }

    @Override
    public List<AdminNoticeVO> showNoticeList() {
        return adminNoticeMapper.showNoticeList();
    }
}
