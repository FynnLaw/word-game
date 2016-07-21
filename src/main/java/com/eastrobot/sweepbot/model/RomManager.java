package com.eastrobot.sweepbot.model;

import org.codehaus.jackson.annotate.JsonIgnore;

public class RomManager {
	private String id;
	private String version;
	private String type;
	private String sysRomName;
	private String originalRomName;
	private String comment;
	private String createTime;
	private String modifyTime;
	private String is_delete;
	private byte[] content;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSysRomName() {
		return sysRomName;
	}
	public void setSysRomName(String sysRomName) {
		this.sysRomName = sysRomName;
	}
	public String getOriginalRomName() {
		return originalRomName;
	}
	public void setOriginalRomName(String originalRomName) {
		this.originalRomName = originalRomName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getIs_delete() {
		return is_delete;
	}
	public void setIs_delete(String is_delete) {
		this.is_delete = is_delete;
	}
	
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	@JsonIgnore
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	
	
}
