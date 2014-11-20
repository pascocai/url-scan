package pascocai.java.web;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import pasco.cai.java.util.HTTPRequestPoster;

import org.dom4j.*;
import org.dom4j.io.*;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class UrlScan {
	
	private static Element root = null;
	private static Workbook book = null;
	private static Sheet sheet = null;
	private static Cell cell = null;
	private int totalUrls = 1;
	private int maxParams = 99;
	private int isPost = 0;
	private int firstValidRow = 99;
	private int urlBeginCol = 99;
	private int paramBeginCol = 99;
	private int valueBeginCol = 99;
	private String xssKeyword = "xss";
	private String outputFileName = "report.html";
	private String domain = "https://www.google.com.hk";
	private String strCookie = "";
	
	public boolean checkConfig() {
		try {
			File f = new File("config.xml");
			SAXReader reader = new SAXReader();
			Document doc = reader.read(f);
			root = doc.getRootElement();
			
			String temp = root.element("VAR").element("totalUrls").getTextTrim();
			if(!temp.equals(""))
				totalUrls = Integer.parseInt(temp);
			
			temp = root.element("VAR").element("maxParams").getTextTrim();
			if(!temp.equals(""))
				maxParams = Integer.parseInt(temp);
			
			temp = root.element("VAR").element("firstValidRow").getTextTrim();
			if(!temp.equals(""))
				firstValidRow = Integer.parseInt(temp);
			
			temp = root.element("VAR").element("urlBeginCol").getTextTrim();
			if(!temp.equals(""))
				urlBeginCol = Integer.parseInt(temp);
			
			temp = root.element("VAR").element("paramBeginCol").getTextTrim();
			if(!temp.equals(""))
				paramBeginCol = Integer.parseInt(temp);
			
			temp = root.element("VAR").element("valueBeginCol").getTextTrim();
			if(!temp.equals(""))
				valueBeginCol = Integer.parseInt(temp);
			
			temp = root.element("VAR").element("isPost").getTextTrim();
			if(!temp.equals(""))
				isPost = Integer.parseInt(temp);
			
			temp = root.element("VAR").element("xssKeyword").getTextTrim();
			if(!temp.equals(""))
				xssKeyword = temp;
			
			temp = root.element("VAR").element("outputFileName").getTextTrim();
			if(!temp.equals(""))
				outputFileName = temp;
			
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");     
			outputFileName = outputFileName+"_"+sDateFormat.format(new java.util.Date())+".html";
			
			temp = root.element("VAR").element("domain").getTextTrim();
			if(!temp.equals(""))
				domain = temp;
			
			temp = root.element("VAR").element("strCookie").getTextTrim();
			if(!temp.equals(""))
				strCookie = temp;
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkImportFile(String importFileName) {
		try {
			book = Workbook.getWorkbook(new File(importFileName));
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		sheet = book.getSheet(0);
		return true;
	}
	public boolean runScan() {
		
		String urls[] = new String[totalUrls];
		String postData[] = new String[totalUrls];
		String paramName[] = new String[maxParams];
		String valueName[] = new String[maxParams];
		String params[][] = new String[totalUrls][];
		String values[][] = new String[totalUrls][];
		
		int valueCount = 0;
		for (int i = 0; i < totalUrls; i++) {
			valueCount = 0;
			urls[i] = getCell(urlBeginCol, i+firstValidRow);
			postData[i] = "";
			for (int j = 0; j < maxParams; j++) {
				paramName[j] = getCell(paramBeginCol+j, i+firstValidRow);
				valueName[j] = getCell(valueBeginCol+j, i+firstValidRow);
				if(paramName[j]!="")
					valueCount++;
			}
			params[i] = new String[valueCount];
			values[i] = new String[valueCount];
			for (int j = 0; j < valueCount; j++) {
				params[i][j] = paramName[j];
				values[i][j] = valueName[j];
			}
		}
		
		HtmlReport report = new HtmlReport();
		report.resetContent();
		report.setContent("<html><head></head><body>");
		for (int i = 0; i < totalUrls; i++) {
			if (params[i] != null && params[i].length > 0) {
				for (int j = 0; j < params[i].length; j++) {
					if (j == 0){
						postData[i] += params[i][j] + "=" + values[i][j];
					} else{
						postData[i] += "&" + params[i][j] + "=" + values[i][j];
					}
				}
			}

			HTTPRequestPoster hrp = new HTTPRequestPoster();
			String response = "";
			if(isPost==0){
				// Run get method
				response = hrp.sendGetRequest(domain + urls[i] + "?" , strCookie);
			} else {
				// Run post method
				response = hrp.sendPostRequest(domain + urls[i], strCookie, postData[i]);
			}
			System.out.println(response);
			if(null!=response) {
				if (response.indexOf(xssKeyword) == -1) {
					report.setErrorMessage(0, (i + 1) + " passed url: " + domain + urls[i]);
				} else {
					report.setErrorMessage(1, (i + 1) + " failed [keyword found] url: " + domain + urls[i]);
				}
	
				if (params[i] != null && params[i].length > 0) {
					for (int j = 0; j < params[i].length; j++) {
						if (response.indexOf(values[i][j]) == -1) {
							report.setErrorMessage(0, "  passed params: " + params[i][j] + "=" + values[i][j]);
						} else {
							report.setErrorMessage(1, "  failed [keyword found] params: " + params[i][j] + "=" + values[i][j]);
						}
					}
				}
				report.setContent("<br />");
			} else{
				report.setErrorMessage(1, (i + 1) + " failed [404 Not Found] url: " + domain + urls[i]);
			}
		}
		report.setContent("</body></html>");
		report.generateHtmlReport(outputFileName);
		
		book.close();
		return true;
	}
	
	private String getCell(int col, int row) {
		try {
			cell = sheet.getCell(col-1, row-1);
			String text = cell.getContents().toString().trim();
			return text;
		} catch (Exception e) {
			return null;
		}
	}
}
