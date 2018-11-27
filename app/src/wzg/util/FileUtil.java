package wzg.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

/**Created by wuzhengu on 2018/9/13 */
public class FileUtil
{
	/**
	 MD5
	 */
	public static String hash(byte[] data){
		String result=null;
		try{
			MessageDigest md=MessageDigest.getInstance("MD5");
			md.update(data);
			data=md.digest();
			StringBuilder hex=new StringBuilder();
			for(int b: data){
				b&=0xFF;
				if(b<16) hex.append("0");
				hex.append(Integer.toHexString(b));
			}
			result=hex.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 文件MD5
	 */
	public static String hash(File file){
		String result=null;
		try{
			MessageDigest md=MessageDigest.getInstance("MD5");
			InputStream fis=new FileInputStream(file);
			byte[] data=new byte[10240];
			int len;
			while((len=fis.read(data))!=-1) md.update(data, 0, len);
			fis.close();
			data=md.digest();
			StringBuilder hex=new StringBuilder();
			for(int b: data){
				b&=0xFF;
				if(b<16) hex.append("0");
				hex.append(Integer.toHexString(b));
			}
			result=hex.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 删除文件或文件夹
	 @return 操作失败的文件
	 */
	public static File delete(File root){
		if(root==null) return null;
		if(!root.exists()) return null;
		File[] files=root.listFiles();
		File result=null;
		if(files!=null) for(File child: files){
			child=delete(child);
			if(result==null) result=child;
		}
		if(result==null) if(!root.delete()) result=root;
		return result;
	}
	
	/**
	 移动文件或文件夹
	 @return 操作失败的文件
	 */
	public static File move(File file1, File file2){
		if(file1==null) return null;
		if(!file1.exists()) return null;
		if(file2==null) return file1;
		if(!file2.exists() || file2.delete()){
			File dir=file2.getParentFile();
			if(dir!=null) dir.mkdirs();
			return file1.renameTo(file2)? null: file1;
		}
		String path1=file1.getPath();
		String path2=file2.getPath();
		File result=null;
		List<File> files=listFiles(file1);
		if(files!=null){
			Collections.sort(files, new Comparator<File>()
			{
				@Override
				public int compare(File f1, File f2){
					int result=countParents(f1)-countParents(f2);
					return -result;
				}
			});
			for(File child: files){
				String p1=child.getPath();
				String p2=p1.replace(path1, path2);
				File child2=new File(p2);
				child2.delete();
				File dir=child2.getParentFile();
				if(dir!=null) dir.mkdirs();
				child.renameTo(child2);
				if(child.isDirectory()) child.delete();
				boolean ok=!child.exists();
				System.out.println((ok? " ": "*")+p1+" | "+p2);
				if(result==null) result=ok? null: child;
			}
		}
		return result;
	}
	
	/**
	 列举文件及其子文件
	 */
	public static List<File> listFiles(File file){
		if(file==null) return null;
		if(!file.exists()) return null;
		List<File> list=new ArrayList<>();
		list.add(file);
		File[] files=file.listFiles();
		if(files!=null) for(File child: files) list.addAll(listFiles(child));
		return list;
	}
	
	/**
	 计算父目录数
	 */
	public static int countParents(File file){
		int count=0;
		int index=-1;
		String path=file.getAbsolutePath();
		while((index=path.indexOf(File.separatorChar, index+1))!=-1) count++;
		return count;
	}
	
	/**
	 计算文件或文件夹大小
	 */
	public static long length(File root){
		if(root==null) return 0;
		if(!root.exists()) return 0;
		File[] files=root.listFiles();
		long result=0;
		if(files==null) result+=root.length();
		else for(File child : files) result+=length(child);
		return result;
	}
}
