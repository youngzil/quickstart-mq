/**
 * 项目名称：msgframe-console 
 * 文件名：Test.java
 * 版本信息：
 * 日期：2017年1月10日
 * Copyright youngzil Corporation 2017
 * 版权所有 *
 */
package test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.DeflaterOutputStream;

/**
 * Test
 * 
 * @author：yangzl
 * @2017年1月10日 上午11:21:48
 * @version 1.0
 */
public class Test {

	public static void main(String[] args) throws IOException {
		
		
		String ff2 = "  ";
		System.out.println("".equals(ff2));
		System.out.println(ff2.length());
		
		
		System.out.println(System.currentTimeMillis());
		
		byte[] ff =  "Hello MetaQ r".getBytes();
		
		
		System.out.println(ff);
		
		System.out.println(new String(ff));
		
		byte[] compressed = compress(ff, 5);
		
		System.out.println(compressed.length);
		System.out.println(new String(compressed));

		List<String> a = new ArrayList<String>();
		a.add("1");
		a.add("2");
		for (String temp : a) {
			if ("2".equals(temp)) {
				a.remove(temp);
			}
		}
		
		ConcurrentHashMap<String/* clientId */, String> factoryTable = new ConcurrentHashMap<String, String>();

		System.out.println(factoryTable.putIfAbsent("aaa", "bbb"));
		System.out.println(factoryTable.putIfAbsent("aaa", "ccc"));

		String response = null;
		// assert response != null:"hahahah";

		int sysFlag = 8;
		sysFlag |= 4;

		System.out.println(sysFlag);

		int communicationMode = 2;
		switch (communicationMode) {
		case 1:
			System.out.println("匹配1");
			break;
		case 2:
		case 3:
			System.out.println("匹配2 || 3");
			break;
		default:
			break;
		}

	}

	public static byte[] compress(final byte[] src, final int level) throws IOException {
		byte[] result = src;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(src.length);
		java.util.zip.Deflater deflater = new java.util.zip.Deflater(level);
		DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, deflater);
		try {
			deflaterOutputStream.write(src);
			deflaterOutputStream.finish();
			deflaterOutputStream.close();
			result = byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			deflater.end();
			throw e;
		} finally {
			try {
				byteArrayOutputStream.close();
			} catch (IOException e) {
			}

			deflater.end();
		}

		return result;
	}

}
