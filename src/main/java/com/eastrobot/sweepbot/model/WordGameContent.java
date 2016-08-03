package com.eastrobot.sweepbot.model;

public class WordGameContent {
	private String id;
	private String serialNo;
	private String title;
	private String content;
	private String option0;
	private String option1;
	private String option2;
	private String option3;
	private String option4;
	private boolean end;
	private String endMessage;
	private String parentSerialNo;
	private String gameId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOption0() {
		return option0;
	}

	public void setOption0(String option0) {
		this.option0 = option0;
	}

	public String getOption1() {
		return option1;
	}

	public void setOption1(String option1) {
		this.option1 = option1;
	}

	public String getOption2() {
		return option2;
	}

	public void setOption2(String option2) {
		this.option2 = option2;
	}

	public String getOption3() {
		return option3;
	}

	public void setOption3(String option3) {
		this.option3 = option3;
	}

	public String getOption4() {
		return option4;
	}

	public void setOption4(String option4) {
		this.option4 = option4;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public String getEndMessage() {
		return endMessage;
	}

	public void setEndMessage(String endMessage) {
		this.endMessage = endMessage;
	}

	public String getParentSerialNo() {
		return parentSerialNo;
	}

	public void setParentSerialNo(String parentSerialNo) {
		this.parentSerialNo = parentSerialNo;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getOption(int i){
		switch (i) {
		case 0:
			return option0;
		case 1:
			return option1;
		case 2:
			return option2;
		case 3:
			return option4;
		case 4:
			return option4;
		default:
			return null;
		}
	}
}
