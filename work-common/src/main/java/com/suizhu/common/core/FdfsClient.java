package com.suizhu.common.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.domain.upload.FastFile;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.suizhu.common.exception.MyException;

import lombok.AllArgsConstructor;

/**
 * fdfs客户端
 * 
 * @author gaochao
 * @date Feb 18, 2019
 */
@Component
@AllArgsConstructor
public class FdfsClient {

	private final FastFileStorageClient storageClient;

	private static final String[] IEBrowserSignals = { "MSIE", "Trident", "Edge" };

	private static final Map<String, String> EXT_MAPS = new HashMap<>(19);

	private static void initExt() {
		// image
		EXT_MAPS.put("png", "image/png");
		EXT_MAPS.put("gif", "image/gif");
		EXT_MAPS.put("bmp", "image/bmp");
		EXT_MAPS.put("ico", "image/x-ico");
		EXT_MAPS.put("jpeg", "image/jpeg");
		EXT_MAPS.put("jpg", "image/jpeg");
		// 压缩文件
		EXT_MAPS.put("zip", "application/zip");
		EXT_MAPS.put("rar", "application/x-rar");
		// doc
		EXT_MAPS.put("pdf", "application/pdf");
		EXT_MAPS.put("ppt", "application/vnd.ms-powerpoint");
		EXT_MAPS.put("xls", "application/vnd.ms-excel");
		EXT_MAPS.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		EXT_MAPS.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
		EXT_MAPS.put("doc", "application/msword");
		EXT_MAPS.put("doc", "application/wps-office.doc");
		EXT_MAPS.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
		EXT_MAPS.put("txt", "text/plain");
		// 音频
		EXT_MAPS.put("mp4", "video/mp4");
		EXT_MAPS.put("flv", "video/x-flv");
	}

	/**
	 * @dec 上传文件
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param inputStream
	 * @param filename
	 * @param fileSize
	 * @return
	 */
	public String upload(InputStream inputStream, String filename, long fileSize) {
		String suffix = FilenameUtils.getExtension(filename);

		Set<MetaData> metaDataSet = new HashSet<>(0);
		metaDataSet.add(new MetaData("filename", filename));
		metaDataSet.add(new MetaData("suffix", suffix));

		FastFile fastFile = new FastFile(inputStream, fileSize, suffix, metaDataSet);

		return storageClient.uploadFile(fastFile).getFullPath();
	}

	/**
	 * @dec 上传文件
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param file
	 * @return
	 */
	public String upload(MultipartFile file) {
		String filename = file.getOriginalFilename();
		String suffix = FilenameUtils.getExtension(filename);
		InputStream inputStream = null;
		try {
			inputStream = file.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Set<MetaData> metaDataSet = new HashSet<>(0);
		metaDataSet.add(new MetaData("filename", filename));
		metaDataSet.add(new MetaData("suffix", suffix));

		FastFile fastFile = new FastFile(inputStream, file.getSize(), suffix, metaDataSet);

		return storageClient.uploadFile(fastFile).getFullPath();
	}

	/**
	 * @dec 更新文件
	 * @date Feb 21, 2019
	 * @author gaochao
	 * @param file
	 * @param fileId
	 * @return
	 */
	public String updateFile(MultipartFile file, String fileId) {
		storageClient.deleteFile(fileId);
		String filename = file.getOriginalFilename();
		String suffix = FilenameUtils.getExtension(filename);
		InputStream inputStream = null;
		try {
			inputStream = file.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Set<MetaData> metaDataSet = new HashSet<>(0);
		metaDataSet.add(new MetaData("filename", filename));
		metaDataSet.add(new MetaData("suffix", suffix));

		FastFile fastFile = new FastFile(inputStream, file.getSize(), suffix, metaDataSet);

		return storageClient.uploadFile(fastFile).getFullPath();
	}

	/**
	 * @dec 下载文件
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param fileId
	 * @return
	 */
	public byte[] download(String fileId) {
		StorePath storePath = StorePath.parseFromUrl(fileId);
		DownloadByteArray downloadByteArray = new DownloadByteArray();
		return storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), downloadByteArray);
	}

	/**
	 * @dec 下载文件
	 * @date Feb 18, 2019
	 * @author gaochao
	 * @param fileId
	 * @param response
	 * @throws Exception
	 */
	public void download(String fileId, HttpServletResponse response) {
		StorePath storePath = StorePath.parseFromUrl(fileId);
		Set<MetaData> metadataSet = storageClient.getMetadata(storePath.getGroup(), storePath.getPath());
		Map<String, String> data = new HashMap<>(0);
		metadataSet.forEach(meta -> {
			data.put(meta.getName(), meta.getValue());
		});

		DownloadByteArray downloadByteArray = new DownloadByteArray();
		byte[] bs = storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), downloadByteArray);

		initExt();
		String filename = data.get("filename");
		String suffix = data.get("suffix");
		String contentType = EXT_MAPS.get(suffix);
		try {
			String encoderName = URLEncoder.encode(filename, "UTF-8").replace("+", "%20").replace("%2B", "+");
			response.setHeader("Content-Disposition", "attachment;filename=\"" + encoderName + "\"");
			response.setContentType(contentType + ";charset=UTF-8");
			response.setHeader("Accept-Ranges", "bytes");
			IOUtils.write(bs, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @dec 下载文件
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param fileId
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void download(String fileId, HttpServletRequest request, HttpServletResponse response) {
		StorePath storePath = StorePath.parseFromUrl(fileId);
		Set<MetaData> metadataSet = storageClient.getMetadata(storePath.getGroup(), storePath.getPath());
		Map<String, String> data = new HashMap<>(0);
		metadataSet.forEach(meta -> {
			data.put(meta.getName(), meta.getValue());
		});

		DownloadByteArray downloadByteArray = new DownloadByteArray();
		byte[] bs = storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), downloadByteArray);

		initExt();
		String filename = data.get("filename");
		String suffix = data.get("suffix");
		String contentType = EXT_MAPS.get(suffix);
		String encoderName;
		try {
			if (isMSBrowser(request)) {
				encoderName = URLEncoder.encode(filename, "UTF-8").replace("+", "%20").replace("%2B", "+");
			} else {
				encoderName = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
			}

			response.setHeader("Content-Disposition", "attachment;filename=\"" + encoderName + "\"");
			response.setContentType(contentType + ";charset=UTF-8");
			response.setHeader("Accept-Ranges", "bytes");
			IOUtils.write(bs, response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean isMSBrowser(HttpServletRequest request) {
		String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
		for (String signal : IEBrowserSignals) {
			if (userAgent.contains(signal))
				return true;
		}
		return false;
	}

	/**
	 * @dec 获取文件信息
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param fileId
	 * @return
	 */
	public Map<String, String> getFileInfo(String fileId) {
		StorePath storePath = StorePath.parseFromUrl(fileId);
		Set<MetaData> metadataSet = storageClient.getMetadata(storePath.getGroup(), storePath.getPath());
		Map<String, String> data = new HashMap<>(0);
		metadataSet.forEach(meta -> {
			data.put(meta.getName(), meta.getValue());
		});

		return data;
	}

	/**
	 * @dec 删除文件
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param fileId
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String fileId) {
		try {
			storageClient.deleteFile(fileId);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @dec 删除文件
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param fileIds
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String[] fileIds) {
		boolean b = false;
		for (String fileId : fileIds) {
			b = delete(fileId);
			if (!b) {
				throw new MyException("文件删除失败！");
			}
		}
		return b;
	}

	/**
	 * @dec 删除文件
	 * @date Feb 19, 2019
	 * @author gaochao
	 * @param fileIds
	 * @return
	 * @throws Exception
	 */
	public boolean delete(List<String> fileIds) {
		boolean b = false;
		for (String fileId : fileIds) {
			b = delete(fileId);
			if (!b) {
				throw new MyException("文件删除失败！");
			}
		}
		return b;
	}

}
