package org.jeecg.modules.demo.empInfo.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.empInfo.entity.EmpInfo;
import org.jeecg.modules.demo.empInfo.service.IEmpInfoService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 员工信息
 * @Author: jeecg-boot
 * @Date:   2025-04-23
 * @Version: V1.0
 */
@Api(tags="员工信息")
@RestController
@RequestMapping("/empInfo")
@Slf4j
public class EmpInfoController extends JeecgController<EmpInfo, IEmpInfoService> {
	@Autowired
	private IEmpInfoService empInfoService;
	
	/**
	 * 分页列表查询
	 *
	 * @param empInfo
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "员工信息-分页列表查询")
	@ApiOperation(value="员工信息-分页列表查询", notes="员工信息-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<EmpInfo>> queryPageList(EmpInfo empInfo,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		System.out.println("当前登录用户信息："+ JSON.toJSONString(user));
        QueryWrapper<EmpInfo> queryWrapper = QueryGenerator.initQueryWrapper(empInfo, req.getParameterMap());
		Page<EmpInfo> page = new Page<EmpInfo>(pageNo, pageSize);
		IPage<EmpInfo> pageList = empInfoService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param empInfo
	 * @return
	 */
	@AutoLog(value = "员工信息-添加")
	@ApiOperation(value="员工信息-添加", notes="员工信息-添加")
	//@RequiresPermissions("empInfo:emp_info:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody EmpInfo empInfo) {
		empInfoService.save(empInfo);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param empInfo
	 * @return
	 */
	@AutoLog(value = "员工信息-编辑")
	@ApiOperation(value="员工信息-编辑", notes="员工信息-编辑")
	//@RequiresPermissions("empInfo:emp_info:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody EmpInfo empInfo) {
		empInfoService.updateById(empInfo);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "员工信息-通过id删除")
	@ApiOperation(value="员工信息-通过id删除", notes="员工信息-通过id删除")
	//@RequiresPermissions("empInfo:emp_info:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		empInfoService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "员工信息-批量删除")
	@ApiOperation(value="员工信息-批量删除", notes="员工信息-批量删除")
	//@RequiresPermissions("empInfo:emp_info:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.empInfoService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "员工信息-通过id查询")
	@ApiOperation(value="员工信息-通过id查询", notes="员工信息-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<EmpInfo> queryById(@RequestParam(name="id",required=true) String id) {
		EmpInfo empInfo = empInfoService.getById(id);
		if(empInfo==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(empInfo);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param empInfo
    */
    //@RequiresPermissions("empInfo:emp_info:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, EmpInfo empInfo) {
        return super.exportXls(request, empInfo, EmpInfo.class, "员工信息");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("empInfo:emp_info:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, EmpInfo.class);
    }

}
