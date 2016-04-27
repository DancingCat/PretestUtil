package com.chinapost.pretest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * 自动化测试辅助jar类
 * @author ken
 * @date 2016-4-23
 */
public class PretestUtil {
	public static void main(String[] args) {
		try {
			 for(int i=0;i<args.length;i++){
				if(args[i].endsWith("--downloadapk")){
					downLoadAPK();					
				}
				if(args[i].endsWith("--startnox")){
					startNox();
					Thread.sleep(120000);
				}
				if(args[i].endsWith("--stopnox")){
					stopNox();
				}
				if(args[i].endsWith("--getreport")){
					getReport(args[i+1]);
				}

			}
//			System.out.println(getNewestDirectoryName("E:\\adt-bundle-windows-x86_64-20140702\\adt-bundle-windows-x86_64-20140702\\sdk\\android-cts\\repository\\results\\"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void startNox() throws IOException {
		Runtime.getRuntime().exec("E:\\Users\\kk\\AppData\\Roaming\\Nox\\bin\\Nox.exe");	
	}

	private static void stopNox() throws IOException {
		Runtime.getRuntime().exec("taskkill /f /t /im spx.exe");	
	}

	/**
	 * 下载最新apk
	 * @throws Exception
	 */
	public static void downLoadAPK() throws Exception {
		// 1.删除本地APK
		// 2.请求网页
		// 3.下载当前最新版本
		// p:nth-child(2) 选择属于其父元素的第二个子元素的每个 <p> 元素。
		String userdir = System.getProperty("user.dir");
		String filePath = userdir + "\\car.apk";
		File apk = new File(filePath);
		if (apk.exists()) {
			apk.delete();
		}
		Document document = Jsoup.connect(
				"http://app2.dg11185.com/appm/f/more?projectId=122&appType=2")
				.get();
		// 获取tbody下第一个tr
		String id = document.select(
				"tbody tr:nth-child(1) button[class*=\"e_show\"]").attr("id");
		String detailUrl = "http://app2.dg11185.com/appm/f/" + id + "/show";
		String downloadUrl = Jsoup.connect(detailUrl).get()
				.select("a[href*=\"appm/f/d/\"]").attr("href");
		saveToFile(downloadUrl, filePath);
	}
	/**
	 * 保存文件
	 * @param destUrl   下载地址
	 * @param fileName  文件路径+文件名
	 * @throws IOException
	 */
	public static void saveToFile(String destUrl, String fileName)
			throws IOException {
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		HttpURLConnection httpUrl = null;
		URL url = null;
		byte[] buf = new byte[1024];
		int size = 0;

		// 建立链接
		url = new URL(destUrl);
		httpUrl = (HttpURLConnection) url.openConnection();
		// 连接指定的资源
		httpUrl.connect();
		// 获取网络输入流
		bis = new BufferedInputStream(httpUrl.getInputStream());
		// 建立文件
		File file = new File(fileName);
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
		fos = new FileOutputStream(fileName);

		// 保存文件
		while ((size = bis.read(buf)) != -1)
			fos.write(buf, 0, size);

		fos.close();
		bis.close();
		httpUrl.disconnect();
	}
	
	/**
	 * 读取文件夹下最新文件夹
	 * @throws IOException 
	 */
	public static void getReport(String path) throws IOException{
		File file = new File(path);
		File[] listFiles = file.listFiles();
		List<String> directorys = new ArrayList<String>();
		for (File f : listFiles) {
			if(f.isDirectory()){
				System.out.println("文件名："+f.getName()+",最后编辑时间："+f.lastModified());
				directorys.add(f.getName());
				
			}
		}
		String  latestDirecoryName = directorys.get(directorys.size()-1);
		//1.删除本地旧的报告
		//2.拷贝最新测试结果到jenkins/workspace/185爱车/car_android下
//		String cmd1 = "cmd.exe /C cd car_android";
		String cmd2  = "cmd.exe /C rd result /s/q";
		String cmd3  = "cmd.exe /C mkdir result";
		String cmd4 = "cmd.exe /C xcopy /y  "+path+latestDirecoryName+" result /s/e/h";
//		Runtime.getRuntime().exec(cmd1);
		Runtime.getRuntime().exec(cmd2);
		Runtime.getRuntime().exec(cmd3);
		Runtime.getRuntime().exec(cmd4);
		
		
	}
}
