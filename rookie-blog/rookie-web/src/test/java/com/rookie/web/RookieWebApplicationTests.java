package com.rookie.web;

import com.rookie.common.domain.dos.UserDO;
import com.rookie.common.domain.mapper.UserMapper;
import com.rookie.web.model.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.SecureRandom;
import java.util.*;

@SpringBootTest
@Slf4j
class RookieWebApplicationTests {
	@Autowired
	private UserMapper userMapper;
	@Test
	void contextLoads() {
		try{
			int i = 1/0;
		}catch (Exception e){
			log.error("发生异常",e);
		}
		log.info("这是一行 Info 级别日志");
		log.warn("这是一行 Warn 级别日志");
		log.error("这是一行 Error 级别日志");

		// 占位符
		String author = "xxx";
		log.info("这是一行带有占位符日志，作者：{}", author);
	}


	@Test
	void insertTest() {
		// 下载地址列表
//		String[] urls = {
//				"https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/2025/05/17/a6ccae2c3001479a9146ba6d3290d891@HFINS应付发票行导入-追创1100人民币部分RMB4789.07.xlsx",
//		};
		Map<String, String> map = new HashMap() {{

			put(    	"P1000230702162"	,	"https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/aa54b819f2e24f528dfd27d2fd8e0c47@image.jpg"	);
			put(    	"P1000230806007-1"	,	"https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/09ba6b6405074511a6bf5ccde4511db1@image.jpg"	);
			put(    	"P1000230806007-2"	,	"https://fcm-file.dreame.tech:9000/dreame-hssp/elec/0/DEFAULT/702dc84f35c247b19315303a22a20cdd@a477c49487ab4ff8be05f7effe18e6b0.pdf"	);
			put(    	"P1000231000343-1"	,	"https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/e0fe8fd1fd9f4f5389ad3dcc88956b87@image.jpg"	);
			put(    	"P1000231000343-2"	,	"https://fcm-file.dreame.tech:9000/dreame-hssp/elec/0/DEFAULT/08295f7e3e644a9e86b0de926a329998@d994112cacae41cb89fb743fae916e1e.pdf"	);
			put(    	"P1000231100887-1"	,	"https://fcm-file.dreame.tech:9000/dreame-hssp/doc/0/DEFAULT/f11b0f92d6d5486489d5e0ecec6b590d@image.jpg"	);
			put(    	"P1000231100887-2"	,	"https://fcm-file.dreame.tech:9000/dreame-hssp/elec/0/DEFAULT/6b3473589a254707b2f65571a588ecf2@746eeb72ee654a8c97bfdc6074326f99.pdf"	);



		}};

		// 目标文件夹路径
		String targetFolder = "downloads";

		// 调用下载方法
//		for (String url : urls) {
//			downloadFile(url, targetFolder);
//		}
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String filename = entry.getKey();
			String url = entry.getValue();
			downloadFile(url, targetFolder,filename);
		}
	}
	public static void downloadFile(String fileURL, String savePath,String filename1) {
		try {
			// 创建目标文件夹
			File folder = new File(savePath);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			// 获取文件名
			String fileName = filename1+"_"+fileURL.substring(fileURL.lastIndexOf('/') + 1);
			File outputFile = new File(folder, fileName);

			// 打开连接
			URL url = new URL(fileURL);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

			// 设置请求方式和超时时间
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setReadTimeout(5000);

			// 检查响应码
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				// 读取输入流
				InputStream inputStream = httpURLConnection.getInputStream();
				FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

				// 下载文件
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					fileOutputStream.write(buffer, 0, bytesRead);
				}

				// 关闭流
				fileOutputStream.close();
				inputStream.close();

				System.out.println("文件已成功下载到: " + outputFile.getAbsolutePath());
			} else {
				System.out.println("服务器响应码: " + responseCode+"--"+fileName+"----"+url);
			}

			// 断开连接
			httpURLConnection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
