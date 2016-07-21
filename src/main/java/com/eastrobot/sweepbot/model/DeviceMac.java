package com.eastrobot.sweepbot.model;

import java.util.Date;

public class DeviceMac {

	private String deviceIdW;

	private String qrticket;

	private String deviceId;

	private Date edittime; // 修改时间

	private String rowNum;

	private String sweepType;

	private Integer sweepStatus;
	
	private Integer autoUpdate;

	public Integer getAutoUpdate() {
		return autoUpdate;
	}

	public void setAutoUpdate(Integer autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	public String getRowNum() {
		return rowNum;
	}

	public void setRowNum(String rowNum) {
		this.rowNum = rowNum;
	}

	public DeviceMac() {
	}

	public DeviceMac(String deviceIdW, String qrticket) {
		this.deviceIdW = deviceIdW;
		this.qrticket = qrticket;
	}

	public DeviceMac(String deviceIdW, String qrticket, String deviceId) {
		this.deviceIdW = deviceIdW;
		this.qrticket = qrticket;
		this.deviceId = deviceId;
	}

	public DeviceMac(String deviceIdW, String qrticket, String deviceId, String rowNum, String sweepType) {
		this.deviceIdW = deviceIdW;
		this.qrticket = qrticket;
		this.deviceId = deviceId;
		this.rowNum = rowNum;
		this.sweepType = sweepType;
	}

	public String getDeviceIdW() {
		return deviceIdW;
	}

	public void setDeviceIdW(String deviceIdW) {
		this.deviceIdW = deviceIdW;
	}

	public String getQrticket() {
		return qrticket;
	}

	public void setQrticket(String qrticket) {
		this.qrticket = qrticket;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public Date getEdittime() {
		return edittime;
	}

	public void setEdittime(Date edittime) {
		this.edittime = edittime;
	}

	public String getSweepType() {
		return sweepType;
	}

	public void setSweepType(String sweepType) {
		this.sweepType = sweepType;
	}

	public Integer getSweepStatus() {
		return sweepStatus;
	}

	public void setSweepStatus(Integer sweepStatus) {
		this.sweepStatus = sweepStatus;
	}

	@Override
	public String toString() {
		return "deviceIdW:" + deviceIdW + " qrticket:" + qrticket + " deviceId:" + deviceId + "\n";
	}

}
