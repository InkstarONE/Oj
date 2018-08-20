package com.zh.qq.controller;

import com.zh.qq.mapper.AdminNoticeMapper;
import com.zh.qq.pojo.AdminNotice;
import com.zh.qq.pojo.Users;
import com.zh.qq.service.AdminService;
import com.zh.qq.utils.AdminJsonToContentUtil;
import com.zh.qq.utils.JSONResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private Sid sid;

    @PostMapping("/addNotice")
    public JSONResult addNotice(@RequestBody String content) throws Exception {
        AdminNotice adminNotice = new AdminNotice();
        String id = sid.nextShort();
        adminNotice.setId(id);
        String c = AdminJsonToContentUtil.adminJsonToContent(content);
        System.out.println(c);
        adminNotice.setContent(c);

        adminService.saveNotice(adminNotice);
        return JSONResult.ok();
    }



    @PostMapping("/showNotice")
    public JSONResult showNotice() throws Exception{
        System.out.println(adminService.showNoticeList());
        return JSONResult.ok(adminService.showNoticeList());
    }

}
