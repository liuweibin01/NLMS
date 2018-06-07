package com.huateng.report.system.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.beanutils.PropertyUtils;

import resource.bean.pub.Bctl;
import resource.bean.pub.RoleFuncRel;
import resource.bean.pub.RoleInfo;
import resource.bean.pub.TlrBctlRel;
import resource.bean.pub.TlrInfo;
import resource.bean.pub.TlrRoleRel;
import resource.bean.report.BiDayexchangerate;
import resource.bean.report.BiMonthexchangerate;
import resource.bean.report.BiNationregion;
import resource.bean.report.BiWorkdateConf;
import resource.bean.report.SysCurrency;
import resource.bean.report.SysParams;
import resource.bean.report.SysParamsPK;
import resource.bean.report.SysTaskConfig;
import resource.bean.report.SysTaskInfo;
import resource.bean.report.SysTaskLog;
import resource.dao.pub.RoleFuncRelDAO;
import resource.report.dao.ROOTDAO;
import resource.report.dao.ROOTDAOUtils;

import com.huateng.common.DateUtil;
import com.huateng.common.log.HtLog;
import com.huateng.common.log.HtLogFactory;
import com.huateng.ebank.business.common.GlobalInfo;
import com.huateng.ebank.business.common.SystemConstant;
import com.huateng.ebank.business.common.service.CommonService;
import com.huateng.ebank.business.management.common.DAOUtils;
import com.huateng.ebank.entity.data.mng.PfSysParam;
import com.huateng.ebank.entity.data.mng.PfSysParamPK;
import com.huateng.ebank.framework.exceptions.CommonException;
import com.huateng.ebank.framework.util.ApplicationContextUtils;
import com.huateng.ebank.framework.util.DataFormat;
import com.huateng.ebank.framework.util.ExceptionUtil;
import com.huateng.report.system.bean.TaskListBean;
import com.huateng.report.system.bean.TlrInfoAuditBean;
import com.huateng.report.utils.RepList;
import com.huateng.report.utils.ReportEnum.REPORT_TASK_FUNCID;
import com.huateng.report.utils.ReportEnum.REPORT_TASK_TRANS_CD;
import com.huateng.report.utils.ReportEnum.REPORT__FH_ST;
import com.huateng.report.utils.ReportObjectSerializerUtil;
import com.huateng.report.utils.ReportTaskUtil;
import com.huateng.service.pub.UserMgrService;

public class TaskListService {
	/**
	 * @author jianxue.zhang 审批的service;
	 */
	private static final HtLog htlog = HtLogFactory.getLogger(TaskListService.class);

	public synchronized static TaskListService getInstance() {
		return (TaskListService) ApplicationContextUtils.getBean("TaskListService");
	}

	/**
	 * 依据待办任务主键遍历加载待审批记录详细,查询专用--zjx
	 */
	public List getApproveListByTaskIds(String taskIds, String type) throws CommonException {
		List list = new ArrayList();
		UndoConfirmService ucs = UndoConfirmService.getInstance();
		ReportTaskUtil rt = new ReportTaskUtil();
		if (!DataFormat.isEmpty(taskIds)) {
			System.out.println(taskIds);
			String[] ids = taskIds.split("@");

			for (int i = 0; i < ids.length; i++) {
				String tid = ids[i].trim();
				if (!DataFormat.isEmpty(tid)) {
					// --这里实现根据id将taskinfo读出来,并将相应的值赋给taskListbean
					SysTaskInfo systask = ucs.load(tid);
					TaskListBean taskbean = new TaskListBean();
					taskbean.setId(systask.getId());
					taskbean.setSysTaskInfo(systask);
					try {
						Object ob;
						// 从序列化中取出相应对象
						ob = rt.getObjctBySysTaskInfo(systask);
						if (type.equals(REPORT_TASK_FUNCID.TASK_100199.value)) {
							taskbean.setBctl((Bctl) ob);
						} else if (type.equals(REPORT_TASK_FUNCID.TASK_100899.value)) {
							taskbean.setPfSysparam((PfSysParam) ob);
						} else if (type.equals(REPORT_TASK_FUNCID.TASK_100299.value)) {
							taskbean.setRoleInfo((RoleInfo) ob);
						} else if (type.equals(REPORT_TASK_FUNCID.TASK_100399.value)) {
							taskbean.setTlrInfo((TlrInfoAuditBean) ob);
						}

						else if (type.equals(REPORT_TASK_FUNCID.TASK_100599.value)) {
							taskbean.setBiWorkdate((BiWorkdateConf) ob);
						}

						else if (type.equals(REPORT_TASK_FUNCID.TASK_100799.value)) {
							taskbean.setSysParams((SysParams) ob);
						} else if (type.equals(REPORT_TASK_FUNCID.TASK_110199.value)) {
							taskbean.setCurrency((SysCurrency) ob);
						} else if (type.equals(REPORT_TASK_FUNCID.TASK_110499.value)) {
							taskbean.setBiNationregion((BiNationregion) ob);
						} else if (type.equals(REPORT_TASK_FUNCID.TASK_110599.value)) {
							taskbean.setBiMonthexchangerate((BiMonthexchangerate) ob);
						} else if (type.equals(REPORT_TASK_FUNCID.TASK_110699.value)) {
							taskbean.setBiDayexchangerate((BiDayexchangerate) ob);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						ExceptionUtil.throwCommonException(e.getMessage());
					}

					list.add(taskbean);
				}
			}
		}
		return list;
	}

	// 获取审核模块是否需要审核

	public boolean isNeedApprove(String taskNo) throws CommonException {
		ROOTDAO rootdao = ROOTDAOUtils.getROOTDAO();
		SysTaskConfig sytc = rootdao.query(SysTaskConfig.class, taskNo);
		if (sytc != null && sytc.getFlag().equals("0")) {
			return false;
		} else {
			return true;
		}
	}

	// 复核后将taskInfo写入tasklog表
	// TODO
	private boolean LogTask(SysTaskInfo st, String pl, String inscd, String result, String remark) throws CommonException {
		try {
			ROOTDAO rootdao = ROOTDAOUtils.getROOTDAO();
			SysTaskLog stlog = new SysTaskLog();
			stlog.setApproveInsCd(inscd);
			stlog.setApproveOperId(pl);
			stlog.setApproveRemark(remark);
			stlog.setApproveResult(result);
			stlog.setApproveTm(DateUtil.get14Date());
			stlog.setAdtRcdPk(st.getAdtRcdPk());
			stlog.setCrtDt(st.getCrtDt());
			stlog.setId(st.getId());
			stlog.setInsCd(st.getInsCd());
			stlog.setIntInsId(st.getIntInsId());
			stlog.setIntOperId(st.getIntOperId());
			stlog.setLastUpdOper(st.getLastUpdOper());
			stlog.setLastUpdTms(st.getLastUpdTms());
			stlog.setUpdTransCd(st.getUpdTransCd());
			String flag = st.getUpdTransCd();
			String insflag = st.getIntInsId();
			String key = st.getAdtRcdPk();
			if (flag.equals(REPORT_TASK_TRANS_CD.EDIT.value)) {
				stlog.setNewVal1(st.getNewVal1());
				stlog.setNewVal2(st.getNewVal2());
				if (insflag.equals(REPORT_TASK_FUNCID.TASK_100299.value)) {
					// 角色
					List<String> rolelist = rootdao.queryByQL2List("select funcid from RoleFuncRel where roleId = '" + key + "'");
					StringBuffer roleListString = new StringBuffer("");
					for (String func : rolelist) {
						roleListString.append(func.trim()).append(",");
					}
					if (roleListString.length() > 0) {
						roleListString = roleListString.deleteCharAt(roleListString.length() - 1);
					}
					RoleInfo roleInfo = (RoleInfo) getObjectByOldKey(key, insflag);
					roleInfo.setRoleList(roleListString.toString());
					String[] jsons;
					jsons = ReportTaskUtil.getStrToArray(ReportObjectSerializerUtil.serialize(roleInfo));
					stlog.setOldVal1(jsons[0]);
					stlog.setOldVal2(jsons[1]);
				} else if (insflag.equals(REPORT_TASK_FUNCID.TASK_100399.value)) {
					// 用户
					TlrInfo tlrInfo = (TlrInfo) getObjectByOldKey(key, insflag);
					List<TlrBctlRel> bctlRellist = rootdao.queryByQL2List("from TlrBctlRel where tlrNo = '" + key + "'");
					List<TlrRoleRel> roleRellist = rootdao.queryByQL2List("from TlrRoleRel where tlrno = '" + key + "'");
					RepList<TlrBctlRel> repBctlList = new RepList<TlrBctlRel>();
					for (TlrBctlRel tlrBctlRel : bctlRellist) {
						repBctlList.add(tlrBctlRel);
					}
					// 角色岗位
					RepList<TlrRoleRel> repRoleList = new RepList<TlrRoleRel>();
					for (TlrRoleRel tlrRoleRel : roleRellist) {
						repRoleList.add(tlrRoleRel);
					}
					TlrInfoAuditBean tlrInfoAu = new TlrInfoAuditBean();
					tlrInfoAu.setBctlRellist(repBctlList);
					tlrInfoAu.setRoleRellist(repRoleList);
					tlrInfoAu.setTlrInfo(tlrInfo);
					String[] jsons;
					jsons = ReportTaskUtil.getStrToArray(ReportObjectSerializerUtil.serialize(tlrInfoAu));
					stlog.setOldVal1(jsons[0]);
					stlog.setOldVal2(jsons[1]);

				} else if (insflag.equals(REPORT_TASK_FUNCID.TASK_100599.value)) {
					// 工作日期
					BiWorkdateConf biWorkdateConf = (BiWorkdateConf) getObjectByOldKey(key, insflag);
					String hql = "select id from BiWorkdate  where id like '" + key + "%'";
					List<String> dbWorkDates = rootdao.queryByQL2List(hql);
					biWorkdateConf.setWorkDateList(dbWorkDates);
					String[] jsons;
					jsons = ReportTaskUtil.getStrToArray(ReportObjectSerializerUtil.serialize(biWorkdateConf));
					stlog.setOldVal1(jsons[0]);
					stlog.setOldVal2(jsons[1]);
				} else {
					Object ob = new Object();
					ob = getObjectByOldKey(key, insflag);
					String[] jsons;
					jsons = ReportTaskUtil.getStrToArray(ReportObjectSerializerUtil.serialize(ob));
					stlog.setOldVal1(jsons[0]);
					stlog.setOldVal2(jsons[1]);
				}
			}
			if (st.getUpdTransCd().equals(REPORT_TASK_TRANS_CD.NEW.value)) {
				// 只有新值
				stlog.setNewVal1(st.getNewVal1());
				stlog.setNewVal2(st.getNewVal2());
			}
			if (st.getUpdTransCd().equals(REPORT_TASK_TRANS_CD.DEL.value)) {
				// 只有旧值,将new的赋给old,
				stlog.setOldVal1(st.getNewVal1());
				stlog.setOldVal2(st.getNewVal2());
			}
			rootdao.saveOrUpdate(stlog);
			return true;
		} catch (Exception e) {
			ExceptionUtil.throwCommonException(e.getMessage());
			return false;
		}

	}

	// 根据原纪录主键获取Object
	private Object getObjectByOldKey(String key, String insflag) throws CommonException {
		Object ob = new Object();
		ROOTDAO rootdao = ROOTDAOUtils.getROOTDAO();
		try {
			if (insflag.equals(REPORT_TASK_FUNCID.TASK_100199.value)) {
				ob = rootdao.query(Bctl.class, key);
			} else if (insflag.equals(REPORT_TASK_FUNCID.TASK_100299.value)) {
				ob = rootdao.query(RoleInfo.class, Integer.valueOf(key));
			} else if (insflag.equals(REPORT_TASK_FUNCID.TASK_100399.value)) {
				ob = rootdao.query(TlrInfo.class, key);
			} else if (insflag.equals(REPORT_TASK_FUNCID.TASK_100599.value)) {
				ob = rootdao.query(BiWorkdateConf.class, key);
			} else if (insflag.equals(REPORT_TASK_FUNCID.TASK_100799.value)) {
				String[] sss = key.split("#");
				SysParamsPK spk = new SysParamsPK(sss[1], sss[0]);
				ob = rootdao.query(SysParams.class, spk);
			} else if (insflag.equals(REPORT_TASK_FUNCID.TASK_110199.value)) {
				ob = rootdao.query(SysCurrency.class, key);
			} else if (insflag.equals(REPORT_TASK_FUNCID.TASK_110499.value)) {
				ob = rootdao.query(BiNationregion.class, key);
			} else if (insflag.equals(REPORT_TASK_FUNCID.TASK_110599.value)) {
				ob = rootdao.query(BiMonthexchangerate.class, key);
			} else if (insflag.equals(REPORT_TASK_FUNCID.TASK_110699.value)) {
				ob = rootdao.query(BiDayexchangerate.class, key);
			} else if (insflag.equals(REPORT_TASK_FUNCID.TASK_100899.value)) {
				String[] sss = key.split("#");
				PfSysParamPK spk = new PfSysParamPK(sss[1], sss[0]);
				ob = rootdao.query(PfSysParam.class, spk);
			}
		} catch (Exception e) {
			ExceptionUtil.throwCommonException(e.getMessage());
		}
		return ob;
	}

	// 审批通用方法函数,不处理:角色,日期,用户
	private void getObjectAndApprove(SysTaskInfo taskbean, String st, Boolean isLock, Boolean isDel, boolean oldflag, boolean newDel, String approvePeople,
			String approveInsCd, String approveResult, String approveRemark, String intInsId) throws CommonException {
		ROOTDAO rootdao = ROOTDAOUtils.getROOTDAO();
		ReportTaskUtil rt = new ReportTaskUtil();
		String oldkey = taskbean.getAdtRcdPk();
		Object obj = null;
		try {
			// 写入历史表
			Boolean bl = LogTask(taskbean, approvePeople, approveInsCd, approveResult, approveRemark);
			if (oldflag) {
				// 根据原纪录主键获取object
				obj = getObjectByOldKey(oldkey, intInsId);
			} else {// 通过反序列化获取object
				obj = rt.getObjctBySysTaskInfo(taskbean);
			}
			if (st != null) {
				PropertyUtils.setNestedProperty(obj, "st", st);
			}
			if (isLock != null) {
				PropertyUtils.setNestedProperty(obj, "lock", isLock);
			}
			if (isDel != null) {
				PropertyUtils.setNestedProperty(obj, "del", isDel);
			}

			if (newDel == true) {
				// 新增并且拒绝
				rootdao.delete(obj);
			} else {
				rootdao.saveOrUpdate(obj);
			}

			// 删除旧表
			if (bl) {
				rootdao.delete(taskbean);
			}

		} catch (Exception xe) {
			ExceptionUtil.throwCommonException(xe.getMessage());
		}
	}

	/**
	 * @desc 更新角色的权限,用,隔开的
	 * @author fubo
	 * @param rid
	 * @param funcs
	 * @return
	 */
	public int updateRoleFunc(Integer rid, String funcs) {

		Hashtable oldfuncs = new Hashtable();
		Hashtable newfuncs = new Hashtable();

		RoleFuncRelDAO rfrd = DAOUtils.getRoleFuncRelDAO();
		List rfuncs = rfrd.findByRoleid(rid);

		Iterator it = rfuncs.iterator();
		while (it.hasNext()) {
			RoleFuncRel rfr = (RoleFuncRel) it.next();
			oldfuncs.put(rfr.getFuncid().toString(), rfr);
		}

		StringTokenizer st = new StringTokenizer(funcs, ",");
		while (st.hasMoreTokens()) {
			String fid = st.nextToken();
			if (newfuncs.containsKey(fid) == false)
				newfuncs.put(fid, fid);
		}

		Iterator itnew = newfuncs.keySet().iterator();
		while (itnew.hasNext()) {
			String newfid = (String) itnew.next();
			if (oldfuncs.containsKey(newfid)) {
				oldfuncs.remove(newfid);
			} else {
				RoleFuncRel newrfr = new RoleFuncRel();
				// added by xuhong 2015-3-30 id赋值 begin
				Iterator iterator;
				try {
					iterator = DAOUtils.getHQLDAO().queryByQL("select max(id) from RoleFuncRel");
					long id = 100;
					if (iterator.hasNext()) {
						Number num = (Number) iterator.next();
						id = num.intValue() + 1;
					}
					newrfr.setId(String.valueOf(id));
				} catch (CommonException e) {
					e.printStackTrace();
				}
				// added by xuhong 2015-3-30 id赋值 end
				newrfr.setFuncid(newfid);
				newrfr.setRoleId(rid);
				rfrd.save(newrfr);
			}
		}
		Enumeration en = oldfuncs.keys();
		while (en.hasMoreElements()) {
			Object key_num = en.nextElement();
			RoleFuncRel oldrfr = (RoleFuncRel) oldfuncs.get(key_num);
			rfrd.delete(oldrfr);
		}

		return 0;
	}

	// 删除角色-菜单关联用,隔开的
	public void deleRoleFunc(Integer rid) {
		RoleFuncRelDAO rfrd = DAOUtils.getRoleFuncRelDAO();
		List dfuncs = rfrd.findByRoleid(rid);
		Iterator it = dfuncs.iterator();
		while (it.hasNext()) {
			rfrd.delete((RoleFuncRel) it.next());
		}
	}

	// 对角色\日期\用户进行特殊处理
	// TODO
	private void getObjectAndApproveExtra(SysTaskInfo taskbean, String st, boolean extraFlag, String approvePeople, String approveInsCd, String approveResult,
			String approveRemark, String intInsId) throws CommonException {
		ROOTDAO rootdao = ROOTDAOUtils.getROOTDAO();
		ReportTaskUtil rt = new ReportTaskUtil();
		String oldkey = taskbean.getAdtRcdPk();
		try {
			// 写入历史表
			Boolean bl = LogTask(taskbean, approvePeople, approveInsCd, approveResult, approveRemark);
			// 如果是通过
			if (approveResult.equals("01")) {

				// 如果是新增操作:根据原纪录主键获取object
				// 如果是日期:
				if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100599.value)) {
					if (extraFlag) {// 新增
						BiWorkdateConf biWorkdateConf = null;
						biWorkdateConf = (BiWorkdateConf) rt.getObjctBySysTaskInfo(taskbean);
						biWorkdateConf.setSt(REPORT__FH_ST.YES.value);
						biWorkdateConf.setLock(false);
						rootdao.saveOrUpdate(biWorkdateConf);
					} else {// 修改
						BiWorkdateConf biWorkdateConf = null;
						biWorkdateConf = (BiWorkdateConf) rt.getObjctBySysTaskInfo(taskbean);
						biWorkdateConf.setSt(REPORT__FH_ST.YES.value);
						biWorkdateConf.setLock(false);
						rootdao.saveOrUpdate(biWorkdateConf);
						List workList = biWorkdateConf.getWorkDateList();
						String year = biWorkdateConf.getId();
						// 存储日期列表
						BiWorkDateService.getInstance().updateWorkDate(year, workList);
					}
				}
				// 如果是角色-通过
				if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100299.value)) {
					if (extraFlag) {
						// 新增:
						RoleInfo obj = (RoleInfo) getObjectByOldKey(oldkey, intInsId);
						obj.setLock(SystemConstant.FALSE);
						obj.setSt(REPORT__FH_ST.YES.value);
						rootdao.saveOrUpdate(obj);
					} else {
						// 修改:
						RoleInfo roleInfo = null;
						roleInfo = (RoleInfo) rt.getObjctBySysTaskInfo(taskbean);
						roleInfo.setLock(SystemConstant.FALSE);
						roleInfo.setSt(REPORT__FH_ST.YES.value);
						rootdao.saveOrUpdate(roleInfo);
						String roleListNew = roleInfo.getRoleList();
						// 获取菜单列表
						if (DataFormat.isEmpty(roleListNew)) {
							roleListNew = "";
						}
						updateRoleFunc(roleInfo.getId(), roleListNew);
					}
				}

				// 用户(包括重置密码和其它所有修改)
				if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100399.value)) {
					// 新增
					if (extraFlag) {
						TlrInfo obj = (TlrInfo) getObjectByOldKey(oldkey, intInsId);
						obj.setSt(REPORT__FH_ST.YES.value);
						rootdao.saveOrUpdate(obj);

					}
					// 修改
					else {
						TlrInfoAuditBean tlrInfoAuditBean = null;
						tlrInfoAuditBean = (TlrInfoAuditBean) rt.getObjctBySysTaskInfo(taskbean);
						// 获取bean
						TlrInfo tlrInfo = tlrInfoAuditBean.getTlrInfo();
						// 1说明重置密码123456
						if (!DataFormat.isEmpty((tlrInfo.getRestFlg()))) {
							if (tlrInfo.getRestFlg().equals("reset")) {
								// 这儿重置密码//
								UserMgrService userMgrService = new UserMgrService();

								String sysDefaultPwd = CommonService.getInstance().getSysParamDef("PSWD", "DEFAULT_PWD", SystemConstant.DEFAULT_PASSWORD);

								userMgrService.updatePassword(tlrInfo.getTlrno(), sysDefaultPwd);

								TlrInfo newinfo = userMgrService.getUserInfo(tlrInfo.getTlrno());

								// 说明是其它修改
								newinfo.setSt(REPORT__FH_ST.YES.value);
								// 有效/无效
								rootdao.saveOrUpdate(newinfo);

							}
						} else {
							// 说明是其它修改
							tlrInfo.setSt(REPORT__FH_ST.YES.value);
							// 有效/无效
							rootdao.saveOrUpdate(tlrInfo);
							String key = tlrInfo.getTlrno();
							// 先删除用户的角色表和机构关联
							List<TlrBctlRel> bctlRellist = rootdao.queryByQL2List("from TlrBctlRel where tlrNo = '" + key + "'");
							for (TlrBctlRel trlbctreldel : bctlRellist) {
								rootdao.delete(trlbctreldel);
							}
							List<TlrRoleRel> roleRellist = rootdao.queryByQL2List("from TlrRoleRel where tlrno = '" + key + "'");
							for (TlrRoleRel trlrolereldel : roleRellist) {
								rootdao.delete(trlrolereldel);
							}
							// 给用户分配角色表：
							List<TlrRoleRel> roleList = tlrInfoAuditBean.getRoleRellist();
							for (TlrRoleRel trlrolerel : roleList) {
								rootdao.save(trlrolerel);
							}
							// 给用户分机构
							List<TlrBctlRel> bctls = tlrInfoAuditBean.getBctlRellist();
							for (TlrBctlRel trlbctrel : bctls) {
								rootdao.save(trlbctrel);
							}
						}
					}

				}
			}
			// 拒绝操作
			else {
				if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100399.value)) {
					// 用户-新增-拒绝
					if (extraFlag) {
						TlrInfoAuditBean obj = (TlrInfoAuditBean) rt.getObjctBySysTaskInfo(taskbean);
						TlrInfo tlrinfo = rootdao.query(TlrInfo.class, obj.getTlrInfo().getTlrno());
						rootdao.delete(tlrinfo);
						// 删除角色引用
						List<TlrRoleRel> roleList = obj.getRoleRellist();
						for (TlrRoleRel tlrrolerel : roleList) {
							rootdao.delete(tlrrolerel);

						}
						// 删除机构引用
						List<TlrBctlRel> funcList = obj.getBctlRellist();
						for (TlrBctlRel tlrbctlrel : funcList) {
							rootdao.delete(tlrbctlrel);

						}
					}
					// 修改
					else {
						TlrInfo obj = (TlrInfo) getObjectByOldKey(oldkey, intInsId);
						obj.setSt(REPORT__FH_ST.YES.value);
						rootdao.saveOrUpdate(obj);
					}

				}
				// 如果是角色-拒绝
				if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100299.value)) {
					if (extraFlag) {// 新增拒绝
						RoleInfo obj = (RoleInfo) rt.getObjctBySysTaskInfo(taskbean);
						Integer id = obj.getId();
						rootdao.delete(obj);
						// 删除角色-菜单表
						deleRoleFunc(id);
					} else {// 角色修该拒绝
						RoleInfo obj = (RoleInfo) getObjectByOldKey(oldkey, intInsId);
						obj.setLock(SystemConstant.FALSE);
						obj.setSt(REPORT__FH_ST.YES.value);
						rootdao.saveOrUpdate(obj);

					}

				}
				// 日期-拒绝
				if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100599.value)) {
					if (extraFlag) {// 新增拒绝
						BiWorkdateConf biWorkdateConf = null;
						biWorkdateConf = (BiWorkdateConf) rt.getObjctBySysTaskInfo(taskbean);
						rootdao.delete(biWorkdateConf);
						// 删除日期表
						String year = biWorkdateConf.getId();
						StringBuffer sql = new StringBuffer();
						sql.append("delete from Bi_Workdate  where work_date like '" + year + "%'");
						rootdao.executeSql(sql.toString());
					} else {// 修改拒绝
						BiWorkdateConf biWorkdateConf = null;
						biWorkdateConf = (BiWorkdateConf) rt.getObjctBySysTaskInfo(taskbean);
						biWorkdateConf.setSt(REPORT__FH_ST.YES.value);
						biWorkdateConf.setLock(false);
						rootdao.saveOrUpdate(biWorkdateConf);
					}
				}

			}

			// 删除旧表
			if (bl) {
				rootdao.delete(taskbean);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			ExceptionUtil.throwCommonException(e.getMessage());
		}
	}

	// 审批操作--jianxue.zhang--新版本
	public void approveList(List insert, List update, List del, String approveResult, String approveRemark, String intInsId) throws CommonException {
		// 公用数据:
		GlobalInfo GI = GlobalInfo.getCurrentInstance();
		String approvePeople = GI.getTlrno();
		String approveInsCd = GI.getBrcode();
		SysTaskInfo taskbean = new SysTaskInfo();
		// 下边这些是每次遍历的时候的信号标志变量,每一次遍历前都得重新刷新这些数据,否则会被上一次干扰
		// ---------------------FLAG START-----------------------
		// 初始化这些参数的默认值
		// 1,2,3,4,5
		String st = null;
		// 0-1或者true-false
		Boolean isLock = null;
		// true-false
		Boolean isDel = null;
		// true-false
		boolean oldflag = false;
		// 是否新增删除标志表明是:新增拒绝
		boolean newDel = false;
		// 对getObjectAndApproveExtra的特殊flag:代表是新增还是修改
		boolean extraFlag = false;
		// ---------------------FLAG END---------------------------
		if (approveResult.equals("01")) {
			// ---复核通过---
			// 新增操作 :st:有效4,lock:解锁,del:null,oldflag:false;
			if (!insert.isEmpty()) {
				Iterator it = insert.iterator();
				while (it.hasNext()) {
					taskbean = (SysTaskInfo) it.next();
					// --start--
					st = null;
					isLock = null;
					isDel = null;
					oldflag = false;
					newDel = false;
					extraFlag = false;
					// end
					st = REPORT__FH_ST.YES.value;
					isLock = new Boolean(false);
					extraFlag = true;
					if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100299.value) || intInsId.equals(REPORT_TASK_FUNCID.TASK_100599.value)
							|| intInsId.equals(REPORT_TASK_FUNCID.TASK_100399.value)) {
						getObjectAndApproveExtra(taskbean, st, extraFlag, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					} else {
						getObjectAndApprove(taskbean, st, isLock, isDel, oldflag, newDel, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					}
				}

			}
			// 更新操作:st:有效4,lock:解锁,del:null,oldflag:false;
			if (!update.isEmpty()) {
				Iterator it = update.iterator();
				while (it.hasNext()) {
					taskbean = (SysTaskInfo) it.next();
					// --start--
					st = null;
					isLock = null;
					isDel = null;
					oldflag = false;
					newDel = false;
					extraFlag = false;
					// end
					st = REPORT__FH_ST.YES.value;
					isLock = new Boolean(false);
					if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100299.value) || intInsId.equals(REPORT_TASK_FUNCID.TASK_100599.value)
							|| intInsId.equals(REPORT_TASK_FUNCID.TASK_100399.value)) {
						getObjectAndApproveExtra(taskbean, st, extraFlag, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					} else {
						getObjectAndApprove(taskbean, st, isLock, isDel, oldflag, newDel, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					}
				}
			}
			// 删除操作:st:有效4,lock:解锁,del:true,oldflag:true;
			if (!del.isEmpty()) {
				Iterator it = del.iterator();
				while (it.hasNext()) {
					taskbean = (SysTaskInfo) it.next();
					// --start--
					st = null;
					isLock = null;
					isDel = null;
					oldflag = false;
					newDel = false;
					extraFlag = false;
					// end
					st = REPORT__FH_ST.YES.value;
					isLock = new Boolean(false);
					isDel = new Boolean(true);
					oldflag = true;
					if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100299.value) || intInsId.equals(REPORT_TASK_FUNCID.TASK_100599.value)
							|| intInsId.equals(REPORT_TASK_FUNCID.TASK_100399.value)) {
						getObjectAndApproveExtra(taskbean, st, extraFlag, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					} else {
						getObjectAndApprove(taskbean, st, isLock, isDel, oldflag, newDel, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					}
				}
			}
			GI.addBizLog("Updater.log", new String[] { GI.getTlrno(), GI.getBrcode(), "主管确认-复核通过-业务类型【" + intInsId + "】" });
			htlog.info("Updater.log", new String[] { GI.getTlrno(), GI.getBrcode(), "主管确认-复核通过-业务类型【" + intInsId + "】" });
		} else {
			// ---复核拒绝---
			// 新增操作:st:无效5,lock:解锁,del:null,oldflag:false;
			if (!insert.isEmpty()) {
				Iterator it = insert.iterator();
				while (it.hasNext()) {
					taskbean = (SysTaskInfo) it.next();
					// --start--
					st = null;
					isLock = null;
					isDel = null;
					oldflag = false;
					newDel = false;
					extraFlag = false;
					// end
					st = REPORT__FH_ST.NO.value;
					isLock = new Boolean(false);
					isDel = new Boolean(true);
					extraFlag = true;
					newDel = true;
					if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100299.value) || intInsId.equals(REPORT_TASK_FUNCID.TASK_100599.value)
							|| intInsId.equals(REPORT_TASK_FUNCID.TASK_100399.value)) {
						getObjectAndApproveExtra(taskbean, st, extraFlag, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					} else {
						getObjectAndApprove(taskbean, st, isLock, isDel, oldflag, newDel, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					}
				}

			}
			// 更新操作:st:有效4,lock:解锁,del:null,oldflag:true;
			if (!update.isEmpty()) {
				Iterator it = update.iterator();
				while (it.hasNext()) {
					taskbean = (SysTaskInfo) it.next();
					// --start--
					st = null;
					isLock = null;
					isDel = null;
					oldflag = false;
					newDel = false;
					extraFlag = false;
					// end
					st = REPORT__FH_ST.YES.value;
					isLock = new Boolean(false);
					oldflag = true;
					if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100299.value) || intInsId.equals(REPORT_TASK_FUNCID.TASK_100599.value)
							|| intInsId.equals(REPORT_TASK_FUNCID.TASK_100399.value)) {
						getObjectAndApproveExtra(taskbean, st, extraFlag, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					} else {
						getObjectAndApprove(taskbean, st, isLock, isDel, oldflag, newDel, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					}
				}
			}
			// 删除操作:st:有效4,lock:解锁,del:false,oldflag:false;
			if (!del.isEmpty()) {
				Iterator it = del.iterator();
				while (it.hasNext()) {
					taskbean = (SysTaskInfo) it.next();
					// --start--
					st = null;
					isLock = null;
					isDel = null;
					oldflag = false;
					newDel = false;
					extraFlag = false;
					// end
					st = REPORT__FH_ST.YES.value;
					isLock = new Boolean(false);
					isDel = new Boolean(false);
					oldflag = true;
					if (intInsId.equals(REPORT_TASK_FUNCID.TASK_100299.value) || intInsId.equals(REPORT_TASK_FUNCID.TASK_100599.value)
							|| intInsId.equals(REPORT_TASK_FUNCID.TASK_100399.value)) {
						getObjectAndApproveExtra(taskbean, st, extraFlag, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					} else {
						getObjectAndApprove(taskbean, st, isLock, isDel, oldflag, newDel, approvePeople, approveInsCd, approveResult, approveRemark, intInsId);
					}
				}

			}
			GI.addBizLog("Updater.log", new String[] { GI.getTlrno(), GI.getBrcode(), "主管确认-复核拒绝-业务类型【" + intInsId + "】" });
			htlog.info("Updater.log", new String[] { GI.getTlrno(), GI.getBrcode(), "主管确认-复核拒绝-业务类型【" + intInsId + "】" });
		}

	}

}