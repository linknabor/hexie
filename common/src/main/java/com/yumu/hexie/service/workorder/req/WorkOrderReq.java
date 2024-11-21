package com.yumu.hexie.service.workorder.req;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class WorkOrderReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1282969012458023843L;
	
	private String acceptType;	//接派单类型
	private String distType;	//维修区域类型，0公共，1入室
	private String address;	//维修地址,公共部位填写
	private String addressId;	//维修地址id,入室
	private String content;	//维修内容
	//小程序用
	private String imagesStr;	//逗号分隔
	
	//公众号用
	private MultipartFile[]fileList;	//上传的图片流
	@JsonIgnore
	private List<String> images;	//七牛图片链接
	
	public String getDistType() {
		return distType;
	}
	public void setDistType(String distType) {
		this.distType = distType;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddressId() {
		return addressId;
	}
	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public MultipartFile[] getFileList() {
		return fileList;
	}
	public void setFileList(MultipartFile[] fileList) {
		this.fileList = fileList;
	}
	public List<String> getImages() {
		return images;
	}
	public void setImages(List<String> images) {
		this.images = images;
	}
	public String getAcceptType() {
		return acceptType;
	}
	public void setAcceptType(String acceptType) {
		this.acceptType = acceptType;
	}
	public String getImagesStr() {
		return imagesStr;
	}
	public void setImagesStr(String imagesStr) {
		this.imagesStr = imagesStr;
	}
	@Override
	public String toString() {
		return "WorkOrderReq [acceptType=" + acceptType + ", distType=" + distType + ", address=" + address
				+ ", addressId=" + addressId + ", content=" + content + ", imagesStr=" + imagesStr + ", fileList="
				+ Arrays.toString(fileList) + ", images=" + images + "]";
	}
	
}
