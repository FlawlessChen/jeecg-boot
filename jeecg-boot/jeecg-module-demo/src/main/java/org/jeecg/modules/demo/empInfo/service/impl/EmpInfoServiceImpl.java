package org.jeecg.modules.demo.empInfo.service.impl;

import org.jeecg.modules.demo.empInfo.entity.EmpInfo;
import org.jeecg.modules.demo.empInfo.mapper.EmpInfoMapper;
import org.jeecg.modules.demo.empInfo.service.IEmpInfoService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 员工信息
 * @Author: jeecg-boot
 * @Date:   2025-04-23
 * @Version: V1.0
 */
@Service
public class EmpInfoServiceImpl extends ServiceImpl<EmpInfoMapper, EmpInfo> implements IEmpInfoService {

}
