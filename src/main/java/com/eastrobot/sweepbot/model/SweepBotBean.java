package com.eastrobot.sweepbot.model;

public class SweepBotBean {
	private String id;
	private String deviceId;
	private String wxDeviceId;
	private String qrTicket;
	private String model;
	private String version;
	private String status;
	private String autoUpdate;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getWxDeviceId() {
		return wxDeviceId;
	}
	public void setWxDeviceId(String wxDeviceId) {
		this.wxDeviceId = wxDeviceId;
	}
	public String getQrTicket() {
		return qrTicket;
	}
	public void setQrTicket(String qrTicket) {
		this.qrTicket = qrTicket;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAutoUpdate() {
		return autoUpdate;
	}
	public void setAutoUpdate(String autoUpdate) {
		this.autoUpdate = autoUpdate;
	}
}
