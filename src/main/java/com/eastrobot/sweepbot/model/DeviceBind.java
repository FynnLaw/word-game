package com.eastrobot.sweepbot.model;

public class DeviceBind {
	private String id;

	private String userId;

	private String deviceIdW;

	private String characterType;

	private String nickName;

	private String createTime;

	private Integer status;

	private Integer sweepStatus;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDeviceIdW() {
		return deviceIdW;
	}

	public void setDeviceIdW(String deviceIdW) {
		this.deviceIdW = deviceIdW;
	}

	public String getCharacterType() {
		return characterType;
	}

	public void setCharacterType(String characterType) {
		this.characterType = characterType;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSweepStatus() {
		return sweepStatus;
	}

	public void setSweepStatus(Integer sweepStatus) {
		this.sweepStatus = sweepStatus;
	}

}
