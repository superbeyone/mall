package cn.e3mall.fast;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import cn.e3mall.common.utils.FastDFSClient;

public class FastDfsTest {
	@Test
	public void testUpload()throws Exception{
		ClientGlobal.init("E:/Project/e3Project/e3-manager-web/src/main/resources/conf/client.conf");
		TrackerClient trackerClient = new TrackerClient();
		TrackerServer trackerServer = trackerClient.getConnection();
		StorageClient storageClient = new StorageClient(trackerServer,null);
		String[] strings = storageClient.upload_file("D:/Pictures/Saved Pictures/girl/8.jpg", "jpg", null);
		for (String string : strings) {
			System.out.println(string);
		}
		
	}
	
	@Test
	public void testfastDFSClient()throws Exception{
		FastDFSClient fastDFSClient = new FastDFSClient("E:/Project/e3Project/e3-manager-web/src/main/resources/conf/client.conf");
		String uploadFile = fastDFSClient.uploadFile("D:/Pictures/Saved Pictures/girl/9.jpg");
		System.out.println(uploadFile);
	}
}
