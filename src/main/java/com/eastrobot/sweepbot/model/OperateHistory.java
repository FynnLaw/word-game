package com.eastrobot.sweepbot.model;

public class OperateHistory {
	private String id;
	private String operateUserId;
	private String opereteTypeId;
	private String createTime;
	private String operateUserIp;
	private String operateUserMac;
	private String localServer;
	private String operateUserCity;
	private String operateSummary;
	private Integer isSuccess;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOperateUserId() {
		return operateUserId;
	}
	public void setOperateUserId(String operateUserId) {
		this.operateUserId = operateUserId;
	}
	public String getOpereteTypeId() {
		return opereteTypeId;
	}
	public void setOpereteTypeId(String opereteTypeId) {
		this.opereteTypeId = opereteTypeId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getOperateUserIp() {
		return operateUserIp;
	}
	public void setOperateUserIp(String operateUserIp) {
		this.operateUserIp = operateUserIp;
	}
	public String getOperateUserMac() {
		return operateUserMac;
	}
	public void setOperateUserMac(String operateUserMac) {
		this.operateUserMac = operateUserMac;
	}
	public String getLocalServer() {
		return localServer;
	}
	public void setLocalServer(String localServer) {
		this.localServer = localServer;
	}
	public String getOperateUserCity() {
		return operateUserCity;
	}
	public void setOperateUserCity(String operateUserCity) {
		this.operateUserCity = operateUserCity;
	}
	public String getOperateSummary() {
		return operateSummary;
	}
	public void setOperateSummary(String operateSummary) {
		this.operateSummary = operateSummary;
	}
	public Integer getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(Integer isSuccess) {
		this.isSuccess = isSuccess;
	}
}
