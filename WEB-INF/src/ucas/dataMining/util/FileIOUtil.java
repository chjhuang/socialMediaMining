package ucas.dataMining.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class FileIOUtil {
	
	/**
	 * 读取文件
	 * @throws IOException 
	 * 
	 */
	public static String readFile(File file) throws IOException {
		String result = "";
		BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
		String s = null;
		while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
			result = result + "\n" + s;
		}
		br.close();
		return result;
	}
	
	/**
	 * 写入文件
	 * @param content文件内容
	 * @param savePath保存完整路径
	 * @throws IOException
	 */

	public static void writeToFile(String content, String savePath) throws IOException {
		FileWriter fw = new FileWriter(savePath);
		PrintWriter out = new PrintWriter(fw);
		out.write(content);
		out.println();
		fw.close();
		out.close();

	}
	
	/**
	 * 保存前台上传文件
	 * @throws IOException 
	 */
	public static void saveToLocal(String filePath, InputStream data) throws IOException {
		File saveFile = new File(filePath);
		FileOutputStream fos = new FileOutputStream(saveFile);
		
		byte b[] = new byte[1024];
        int n;
        while ((n = data.read(b)) != -1) {
        	fos.write(b, 0, n);
        }
        data.close();
        fos.close();
	}
}
