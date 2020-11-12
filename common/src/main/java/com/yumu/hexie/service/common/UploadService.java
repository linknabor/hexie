/**
 * Yumu.com Inc.
 * Copyright (c) 2014-2016 All Rights Reserved.
 */
package com.yumu.hexie.service.common;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.yumu.hexie.model.localservice.repair.RepairOrder;

/**
 * <pre>
 * 
 * </pre>
 *
 * @author tongqian.ni
 * @version $Id: UploadService.java, v 0.1 2016年1月7日 上午2:03:52  Exp $
 */
public interface UploadService {

    public void updateRepairImg(RepairOrder order);

    Map<String, String> uploadImages(String appId, List<String> imgUrlList);

	File downloadFromWechat(String accessToken, String mediaId);
}
