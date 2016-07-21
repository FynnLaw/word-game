package com.eastrobot.sweepbot.model;

import java.util.List;
public class CategoryTreeBean {
	private String id;
	private String parent_id;
	private String href;
	private String text;
	private boolean leaf;
	private List<CategoryTreeBean> children;
	
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getParent_id() {
		return parent_id;
	}
	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	public List<CategoryTreeBean> getChildren() {
		return children;
	}
	public void setChildren(List<CategoryTreeBean> children) {
		this.children = children;
	}
	
}
