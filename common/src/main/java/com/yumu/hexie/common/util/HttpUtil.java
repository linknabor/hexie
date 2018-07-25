package com.yumu.hexie.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.yumu.hexie.service.exception.OvertimeException;



public class HttpUtil {
	
	private static final Log logger = LogFactory.getLog(HttpUtil.class);
	
	private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 50000;	//50秒
    
    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        requestConfig = configBuilder.build();
    }
    
    /**
     * 发送 GET 请求（HTTP），不带输入数据
     * @param url
     * @return
     */
    public static String doGet(String url) {
        return doGet(url, new HashMap<String, String>(),"utf-8");
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     * @param url
     * @param params
     * @return
     */
    public static String doGet(String url, Map<String, String> params, String codePage) {
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        if (params != null) {
	        int i = 0;
	        for (String key : params.keySet()) {
	            if (i == 0)
	                param.append("?");
	            else
	                param.append("&");
	            param.append(key).append("=").append(params.get(key));
	            i++;
	        }
	        apiUrl += param;
        }
        String result = null;
        HttpClient httpclient = new HttpClient();
        try {
            GetMethod httpGet = new GetMethod(apiUrl);
            int statusCode = httpclient.executeMethod(httpGet);

            System.out.println("执行状态码 : " + statusCode);

            InputStream inputStream = httpGet.getResponseBodyAsStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, codePage));
			StringBuffer stringBuffer = new StringBuffer();
			String str= "";
			while((str = br.readLine()) != null){
				stringBuffer.append(str );
			}
			result=stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送POST K-V形式请求，根据URL区分http或https
     * @param apiUrl
     * @param params
     * @param codePage
     * @return
     */
	public static String doPostKandVArr(String apiUrl, Map<String,String> params, String codePage)
    {
    	logger.debug(">>>正在通过http client 方式请求中......");
		logger.debug(">>>>>>通过http方式请求中......");
		logger.debug(">>>>>>请求地址："+apiUrl);
		return doPostMap(apiUrl, params, codePage);
    }
    
    /**
     * 发送POST JSON形式请求，根据URL区分http或https
     * @param apiUrl
     * @param params
     * @param codePage
     * @return
     * @throws Exception 
     */
	public static String doPostJsonArr(String apiUrl, JSONObject json, String codePage) throws Exception
    {
    	
		return doPostJson(apiUrl, json, codePage);
    }
    
    /**
     * 发送 POST 请求（HTTP），K-V形式
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPostMap(String apiUrl, Map<String, String> params, String codePage) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            if(params!=null)
            {
            	 httpPost.setEntity(new UrlEncodedFormEntity(assembleRequestParams(params), Charset.forName(codePage)));
            }
            response = httpClient.execute(httpPost);
            logger.debug("response statusCode is "+response.getStatusLine().getStatusCode());
            
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, codePage);
        } catch (IOException e) {
            throw new OvertimeException();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     * @param apiUrl
     * @param json json对象
     * @return
     * @throws Exception 
     */
    public static String doPostJson(String apiUrl, JSONObject json, String codePage) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(),codePage);//解决中文乱码问题
            stringEntity.setContentEncoding(codePage);
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            httpPost.setHeader("Accept", "application/json");
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            logger.info("response statusCode "+response.getStatusLine().getStatusCode());
            
            httpStr = EntityUtils.toString(entity, codePage);
        } catch (IOException e) {
        	throw new Exception(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                	throw new Exception(e);
                }
            }
        }
        return httpStr;
    }
    
    /**
     * 发送 POST 请求（HTTP），JSON形式
     * @param apiUrl
     * @param json json对象
     * @return
     * @throws Exception 
     */
    public static String doPostJsonStr(String apiUrl, String json, String codePage) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(),codePage);//解决中文乱码问题
            stringEntity.setContentEncoding(codePage);
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            logger.info("response statusCode "+response.getStatusLine().getStatusCode());
            
            httpStr = EntityUtils.toString(entity, codePage);
        } catch (IOException e) {
        	throw new Exception(e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                	throw new Exception(e);
                }
            }
        }
        return httpStr;
    }

    public static String doPostXml(String url, String xmlString, String codePage)
    {
    	CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(xmlString,codePage);//解决中文乱码问题
            stringEntity.setContentEncoding(codePage);
            stringEntity.setContentType("text/xml");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            logger.debug("response statusCode "+response.getStatusLine().getStatusCode());
            
            httpStr = EntityUtils.toString(entity, codePage);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return httpStr;
    }
    
    
	
	/**
	 * 组装http请求参数
	 * 
	 * @param params
	 * @param menthod
	 * @return
	 */
	private synchronized static List<org.apache.http.NameValuePair> assembleRequestParams(Map<String, String> params) {
		
		List<org.apache.http.NameValuePair> nameValueList = new ArrayList<org.apache.http.NameValuePair>(params.size());
        for (Map.Entry<String, String> entry : params.entrySet()) {
            org.apache.http.NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
            nameValueList.add(pair);
        }
		return nameValueList;
	}
	
	/**
	 * 发送接送数组
	 * @param apiUrl
	 * @param jsonarray
	 * @param codePage
	 * @return
	 * @throws Exception 
	 */
	public static String doPostJsonArray(String apiUrl, JSONArray jsonarray, String codePage) throws Exception{
		
		 CloseableHttpClient httpClient = HttpClients.createDefault();
	        String httpStr = null;
	        HttpPost httpPost = new HttpPost(apiUrl);
	        CloseableHttpResponse response = null;

	        try {
	            httpPost.setConfig(requestConfig);
	            StringEntity stringEntity = new StringEntity(jsonarray.toString(),codePage);//解决中文乱码问题
	            stringEntity.setContentEncoding(codePage);
	            stringEntity.setContentType("application/json");
	            httpPost.setEntity(stringEntity);
	            response = httpClient.execute(httpPost);
	            HttpEntity entity = response.getEntity();
	            logger.info("resposne is : " + response );
	            logger.info("response statusCode "+response.getStatusLine().getStatusCode());
	            httpStr = EntityUtils.toString(entity, codePage);
	            
	        } catch (IOException e) {
	        	throw new Exception(e);
	        } finally {
	            if (response != null) {
	                try {
	                    EntityUtils.consume(response.getEntity());
	                } catch (IOException e) {
	                	throw new Exception(e);
	                }
	            }
	        }
	        return httpStr;
		
		
	}
	
}
