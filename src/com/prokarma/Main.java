package com.prokarma;


import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Main {
	public static JSONObject fullObject;
	public static JSONArray baseActionsArry;
	public static JSONObject dashboardObject;
	public static String dashboradViewRepeat;
	public static String shutdownsystem;
	public static JSONArray dashboradActionsArry;
    public static WebDriver driver;
    public static Actions actions;
	public static void main(String[] args) {
		actions = new Actions();
		try {
			getDashboardRespose();
			System.out.println("******* Dashboard config respone received *******");
			System.out.println(fullObject.toString());
			String browser = fullObject.getString("browser");
			if(browser.equalsIgnoreCase("firefox"))
			{
				driver  = new FirefoxDriver();
			}else{
				System.setProperty("webdriver.chrome.driver", "libs/chromedriver");
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--kiosk");
				driver  = new ChromeDriver(options);
			}
			parseFullJsonObject(fullObject);
	
			for (int i = 0; i < baseActionsArry.length(); i++) {
			    JSONObject jsonobject = baseActionsArry.getJSONObject(i);
			    JSONObject actionObject = jsonobject.getJSONObject("action");
			    actions.performactionfor(actionObject, driver);
			}
			Boolean isDashboardRepeat = false;
			if(dashboradViewRepeat!=null && !dashboradViewRepeat.isEmpty()){
				isDashboardRepeat = Boolean.parseBoolean(dashboradViewRepeat);
			}
			if(isDashboardRepeat)
			{
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {
					
					@Override
					public void run() {
						try {
							getDashboardRespose();
							parseFullJsonObject(fullObject);
							
							System.out.println("Timer started");
						    int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY); 
						    int minute = Calendar.getInstance().get(Calendar.MINUTE);
						    
						    Boolean shutdown = false;
							if(shutdownsystem!=null && !shutdownsystem.isEmpty()){
								shutdown = Boolean.parseBoolean(shutdownsystem);
							}

						    if((hour==20 && minute==30) || shutdown ){
		                     Process p = Runtime.getRuntime().exec("sudo shutdown -h now");
		                     p.waitFor();
						    }
						    
							System.out.println("Got Dashboard respose");
							performDashboardAction();
						} catch (JSONException e1) {
							System.out.println("Json Exception :"+e1.toString());
						} catch (InterruptedException e1) {
							System.out.println("Loop runner interupt Exception :"+e1.toString());
						} catch (IOException ioException)
						{
							System.out.println("Network call IOException :"+ioException.toString());
						}
						
					}
				},50, 50);
				
			}else{
				performDashboardAction();
				driver.quit();
			}
		} catch (Exception ex) {
			System.out.println("Error getting actions: " + ex.toString());
			driver.quit();
		}
		
		

	}
	
	private static void parseFullJsonObject(JSONObject fullObject) throws JSONException 
	{
		baseActionsArry = fullObject.getJSONArray("actions");
		shutdownsystem = fullObject.getString("shutdown");
		dashboardObject = fullObject.getJSONObject("dashboard");
		dashboradViewRepeat = dashboardObject.getString("viewrepeat");
		dashboradActionsArry = dashboardObject.getJSONArray("actions");
	}
	
	private static void getDashboardRespose() throws IOException, JSONException
	{
		fullObject = actions.readJsonFromUrl(
				"http://mdw.prokarma.com/mockservice/serviceresponse.php?serviceurl=c59b27f41f2d29fc2852f0e332d9b36d");
	}
	
	private static void performDashboardAction() throws JSONException, InterruptedException {
		System.out.println("******* Performing actions started *******");
		
		for (int i = 0; i < dashboradActionsArry.length(); i++) {
		    JSONObject jsonobject = dashboradActionsArry.getJSONObject(i);
		    System.out.println("Dashboard Array:"+ jsonobject.toString());
		    JSONObject actionObject = jsonobject.getJSONObject("action");
		    System.out.println("Action to perform:"+ actionObject.toString());
		    actions.performactionfor(actionObject, driver);
		    System.out.println("******* Action completed *******");
		}
	}
}
