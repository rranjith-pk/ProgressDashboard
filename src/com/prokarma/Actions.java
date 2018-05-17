package com.prokarma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Actions {

	private  String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public  JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	
	public void performactionfor(JSONObject actionJson, WebDriver driver) throws JSONException,InterruptedException {
		String waitfor = actionJson.getString("waitfor");
		String navigateTo = actionJson.getString("navigateTo");
		String findelementby = actionJson.getString("findelementby");
		String findelementvalue = actionJson.getString("findelementvalue");
		String action = actionJson.getString("action");
		String value = actionJson.getString("value");
		if(waitfor!=null && !waitfor.isEmpty()){
			Thread.sleep(Long.parseLong(waitfor));
		}
		if(navigateTo!=null && !navigateTo.isEmpty()){
			driver.navigate().to(navigateTo);
		}
		if(findelementby!=null && !findelementby.isEmpty() && findelementvalue!=null && !findelementvalue.isEmpty() ){
			By byWhat = getByWhat(findelementby, findelementvalue);
			if(byWhat!=null){
			WebElement element = driver.findElement(byWhat);
			perfomAction(element,action,value);
			}
		}
	}
	
	private By getByWhat(String by, String value){
		By byWhat = null;
		switch(by){
		case "className":
			byWhat = By.className(value);
			break;
		case "cssSelector":
			byWhat = By.cssSelector(value);
			break;
		case "id":
			byWhat = By.id(value);
			break;
		case "linkText":
			byWhat = By.linkText(value);
			break;
		case "name":
			byWhat = By.name(value);
			break;
		case "partialLinkText":
			byWhat = By.partialLinkText(value);
			break;
		case "tagName":
			byWhat = By.tagName(value);
			break;
		case "xpath":
			byWhat = By.xpath(value);
			break;
		default:
			byWhat = null;
			break;
		}
		return byWhat;
	}
	
	private void perfomAction(WebElement element, String action, String value){
		switch(action){
		case "clear":
			element.clear();
			break;
		case "click":
			element.click();
			break;
		case "sendKeys":
			if(value!=null && !value.isEmpty())
			element.sendKeys(value);
			break;
		case "submit":
			element.submit();
			break;
		default:
			break;
		}
	}

}
