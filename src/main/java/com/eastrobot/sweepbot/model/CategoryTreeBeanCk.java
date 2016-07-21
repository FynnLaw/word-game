package com.eastrobot.sweepbot.model;

import java.util.List;
public class CategoryTreeBeanCk {
	private String id;
	private String parent_id;
//	private String href;
	private String text;
	private boolean leaf;
	private boolean checked;
	private List<CategoryTreeBeanCk> children;
	
//	public String getHref() {
//		return href;
//	}
//	public void setHref(String href) {
//		this.href = href;
//	}
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
	public List<CategoryTreeBeanCk> getChildren() {
		return children;
	}
	public void setChildren(List<CategoryTreeBeanCk> children) {
		this.children = children;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	
	
}
