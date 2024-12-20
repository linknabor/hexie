package com.yumu.hexie.web.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.yumu.hexie.integration.qiniu.util.QiniuUtil;
import com.yumu.hexie.web.BaseResult;

/**
 * 描述:
 *
 * @author jackie
 * @create 2021-05-10 16:36
 */
@RestController
public class UpLoadController {

    private static final Logger log = LoggerFactory.getLogger(UpLoadController.class);

    @Autowired
    private QiniuUtil qiniuUtil;

    @Value(value = "${qiniu.domain}")
    private String domain;

    /**
     * 上传图片到七牛
     * @param multiFile
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<String> upload(@RequestParam(value = "file", required = false) MultipartFile multiFile) throws Exception {
        String imgUrl = "";
        log.error(multiFile.getOriginalFilename());
        String fileName = multiFile.getOriginalFilename();
        if(StringUtils.isNoneBlank(fileName)) {
            long timestamp = System.currentTimeMillis();
            String kzm = fileName.substring(0, fileName.lastIndexOf("."));
            Random random = new Random();
            int r = random.nextInt();
            String key = timestamp + "_" + r + "_" + kzm;

            log.error("key:" + key);
            log.error("kzm:" + kzm);

            String uptoken = qiniuUtil.getUpToken();    //获取qiniu上传文件的token
            PutExtra extra = new PutExtra();
            PutRet putRet = IoApi.Put(uptoken, key, multiFile.getInputStream(), extra);
            log.error("exception:" + putRet.getException());
            log.error("response:" + putRet.getResponse());
            if (putRet.getException() == null) {
                imgUrl = domain + key;
            }
        }
        return BaseResult.successResult(imgUrl);
    }

    /**
     * 上传图片到七牛
     * @param multiFiles
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/batchUpload", method = RequestMethod.POST)
    @ResponseBody
    public BaseResult<List<String>> batchUpload(@RequestParam(value = "file", required = false) MultipartFile[] multiFiles) throws Exception {
        List<String> list = new ArrayList<>();
        for(MultipartFile multiFile : multiFiles ) {
            String fileName = multiFile.getOriginalFilename();
            if(StringUtils.isNoneBlank(fileName)) {
            	long timestamp = System.currentTimeMillis();
                String kzm = fileName.substring(0, fileName.lastIndexOf("."));

                Random random = new Random();
                int r = random.nextInt();
                String key = timestamp + "_" + r + "_" + kzm;
                String uptoken = qiniuUtil.getUpToken();    //获取qiniu上传文件的token
                PutExtra extra = new PutExtra();
                PutRet putRet = IoApi.Put(uptoken, key, multiFile.getInputStream(), extra);
                log.debug("upload:" + fileName + " putRet :" + putRet.getException());
                if (putRet.getException() == null) {
                    list.add(domain + key);
                }
            }
        }
        return BaseResult.successResult(list);
    }
}
