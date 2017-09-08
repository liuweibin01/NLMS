package com.cibfintech.blacklist.policeblacklist.getter;

import java.util.Date;

import resource.bean.blacklist.NsPoliceBLOperateLog;

import com.cibfintech.blacklist.policeblacklist.service.PoliceBlackListOperateLogService;
import com.cibfintech.blacklist.policeblacklist.service.PoliceBlackListService;
import com.cibfintech.blacklist.util.GenerateID;
import com.huateng.common.err.Module;
import com.huateng.common.err.Rescode;
import com.huateng.commquery.result.Result;
import com.huateng.commquery.result.ResultMng;
import com.huateng.ebank.business.common.GlobalInfo;
import com.huateng.ebank.business.common.PageQueryResult;
import com.huateng.ebank.business.common.SystemConstant;
import com.huateng.ebank.framework.exceptions.CommonException;
import com.huateng.ebank.framework.report.common.ReportConstant;
import com.huateng.ebank.framework.web.commQuery.BaseGetter;
import com.huateng.exception.AppException;

@SuppressWarnings("unchecked")
public class PoliceBlackListGetter extends BaseGetter {
	/*
	 * 获取公安部黑名单
	 * 
	 * @author huangcheng
	 */
	@Override
	public Result call() throws AppException {
		try {
			this.setValue2DataBus(ReportConstant.QUERY_LOG_BUSI_NAME, "公安部黑名单管理查询");
			PageQueryResult pageResult = getData();
			ResultMng.fillResultByList(getCommonQueryBean(), getCommQueryServletRequest(), pageResult.getQueryResult(), getResult());
			result.setContent(pageResult.getQueryResult());
			result.getPage().setTotalPage(pageResult.getPageCount(getResult().getPage().getEveryPage()));
			result.init();
			return result;
		} catch (AppException appEx) {
			throw appEx;
		} catch (Exception ex) {
			throw new AppException(Module.SYSTEM_MODULE, Rescode.DEFAULT_RESCODE, ex.getMessage(), ex);
		}
	}

	protected PageQueryResult getData() throws Exception {
		String qPartyId = getCommQueryServletRequest().getParameter("qPartyId");
		String qCertificateType = getCommQueryServletRequest().getParameter("qCertificateType");
		String qCertificateNumber = getCommQueryServletRequest().getParameter("qCertificateNumber");
		String qOperateState = getCommQueryServletRequest().getParameter("qOperateState");
		int pageSize = this.getResult().getPage().getEveryPage();
		int pageIndex = this.getResult().getPage().getCurrentPage();
		PageQueryResult pqr = PoliceBlackListService.getInstance().pageQueryByHql(pageIndex, pageSize, qPartyId, qCertificateType, qCertificateNumber,
				qOperateState);

		String message = "公安部黑名单的查询:partyId=" + qPartyId + "certificateType=" + qCertificateType + "certificateNumber=" + qCertificateNumber;
		recordOperateLog(GlobalInfo.getCurrentInstance(), pqr, message);
		return pqr;
	}

	// 记录查询日志
	private void recordOperateLog(GlobalInfo globalinfo, PageQueryResult pqr, String message) {
		PoliceBlackListOperateLogService service = PoliceBlackListOperateLogService.getInstance();
		NsPoliceBLOperateLog bean = new NsPoliceBLOperateLog();
		bean.setBrNo(globalinfo.getBrno());
		bean.setId(String.valueOf(GenerateID.getId()));
		bean.setQueryType("");
		bean.setQueryRecordNumber(String.valueOf(null == pqr ? "0" : pqr.getTotalCount()));
		bean.setTlrIP(globalinfo.getIp());
		bean.setTlrNo(globalinfo.getTlrno());
		bean.setOperateType(SystemConstant.LOG_QUERY);
		bean.setMessage(message);
		bean.setCreateDate(new Date());
		try {
			service.addEntity(bean);
		} catch (CommonException e) {
			e.printStackTrace();
		}
	}
}
