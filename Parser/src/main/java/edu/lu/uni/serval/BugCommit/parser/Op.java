package edu.lu.uni.serval.BugCommit.parser;

public class Op {
	private int level; // parent, child
	private String op; // UPD, MOV ...
	private String stmtType = null;
	
	private String opName = null;
	private String parentOpName = null;
	private String childOpName = null;
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public String getStmtType() {
		return stmtType;
	}
	public void setStmtType(String stmtType) {
		this.stmtType = stmtType;
	}
	public String getOpName() {
		return opName;
	}
	public void setOpName(String opName) {
		this.opName = opName;
	}
	public String getParentOpName() {
		return parentOpName;
	}
	public void setParentOpName(String parentOpName) {
		this.parentOpName = parentOpName;
	}
	public String getChildOpName() {
		return childOpName;
	}
	public void setChildOpName(String childOpName) {
		this.childOpName = childOpName;
	}
	
	public String toString(){
		String blank = "";
		int tmpLevel = level;
		while (tmpLevel - 1 > 0){
			blank += "   ";
			tmpLevel --; 
		}
		return opName + ":" + blank +"(" + op + ", " 
				+ stmtType + ", "
				+ parentOpName + ", "
				+ childOpName + ")\n";
	} 
}
