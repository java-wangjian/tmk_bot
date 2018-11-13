package com.zxxkj.service.impl;

import com.zxxkj.dao.MailMapper;
import com.zxxkj.model.Mail;
import com.zxxkj.service.MailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("mailService")
public class MailServiceImpl implements MailService {
	
	@Resource
	private MailMapper mailDao;

	@Override
	public Mail selectRole0Info(Map<String, Object> map) {
		return mailDao.selectRole0Info(map);
	}

	@Override
	public List<Mail> selectSalesInfoList(Map<String, Object> map) {
		return mailDao.selectSalesInfoList(map);
	}

}
