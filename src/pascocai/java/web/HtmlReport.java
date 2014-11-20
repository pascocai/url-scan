package pascocai.java.web;


import org.apache.commons.lang3.StringEscapeUtils;

import pasco.cai.java.util.FileOperation;

public class HtmlReport {
	
	private String reportStr = null;
	private String[] statusColor = {"green", "red", "black"};	// 0:pass 1:fail 2:normal
	private String stepResultTag = "td";
	private String errorMessageTag = "div";
	private FileOperation fo = null;
	
	public HtmlReport() {
		
	}
	
	public void resetContent() {
		reportStr = "";
	}
	
	public void setContent(String content) {
		reportStr += content;
	}
	
	public void setStepResult(int status, String content) {
		reportStr += "<"+stepResultTag+" style=\"color:" + statusColor[status] + "\">" + StringEscapeUtils.escapeHtml4(content) + "</"+stepResultTag+">";
	}
	
	public void setErrorMessage(int status, String content) {
		reportStr += "<"+errorMessageTag+" style=\"color:" + statusColor[status] + "\">" + StringEscapeUtils.escapeHtml4(content) + "</"+errorMessageTag+">";
	}
	
	public void generateHtmlReport(String fileName) {
		fo = new FileOperation();
		fo.write(fileName, reportStr);
	}
}
