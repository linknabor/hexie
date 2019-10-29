package com.eshequ.eurekaclient.eureka;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.eshequ.eurekaclient.entity.EurekaApplication;
import com.eshequ.eurekaclient.entity.EurekaApplicationInstance;
import com.eshequ.eurekaclient.entity.EurekaApplications;
import com.eshequ.eurekaclient.exception.EurekaClientException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 同EurekaServer建立连接 负责定时更新 负责获取指定的Service 外部不需要调用这个类 这个类是个单例
 *
 * @author yangzj
 */
public class HexieEurekaClient {

	private static final Logger logger = LoggerFactory.getLogger(HexieEurekaClient.class);

	private volatile static String EUREKA_SERVER_URL;

	private volatile static Hashtable<String, EurekaApplication> applications = new Hashtable<>();

	// 网络访问错误的service地址
	private volatile static Hashtable<String, Long> eurekaError = new Hashtable<>();

	// 保存service的上次刷新时间
	private volatile static Hashtable<String, Long> eurekaRefresh = new Hashtable<>();

	// 网络访问错误重试次数
	private static final int NETWORK_RETRY_TIME = 2;

	private static final int SERVICE_RETRY_TIME = 3;

	// 网络访问错误重试间隔时间
	private static final long RETRY_WAIT_MILLISECONDS = 1000;

	// 刷新远程链接间隔时间
	private static final long REFRESH_INSTANCE_SECONDS = 20;

	private static final long REFRESH_ERROR_INSTANCE_SECONDS = 30;

	private volatile static int FLAG = 0;

	private static ReentrantLock lock = new ReentrantLock();

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	protected HexieEurekaClient() {
		init();
	}

	public Hashtable<String, EurekaApplication> getApplications() {
		return HexieEurekaClient.applications;
	}

	public EurekaApplication getApplicatioin(String serviceName) {
		return HexieEurekaClient.applications.get(serviceName);
	}

	protected void init() {
		ResourceBundle resource = ResourceBundle.getBundle("eureka-client");
		EUREKA_SERVER_URL = resource.getString("eureka.serviceUrl");
		refreshApplication("");
	}

	/**
	 * 刷新eureka的实例
	 * 
	 * @param serviceName
	 */
	private void refreshApplication(String serviceName) {

		String url = EUREKA_SERVER_URL + "/apps";
		if (!StringUtils.isEmpty(serviceName)) {
			url += "/" + serviceName.toUpperCase();
			ResponseEntity<String> respEntity = restTemplate.getForEntity(url, String.class);
			String appsJson = respEntity.getBody();
			analyseApplication(appsJson, serviceName);
		} else {
			ResponseEntity<String> respEntity = restTemplate.getForEntity(url, String.class);
			String appsJson = respEntity.getBody();
			analyseApplication(appsJson);
		}
	}

	/**
	 * 解析Eureka服务传过来的json串进行解析
	 * 
	 * @param appsJson
	 */
	private void analyseApplication(String appsJson) {
		synchronized (HexieEurekaClient.class) {
			if (FLAG == 1) {
				return;
			}
			FLAG = 1;
			lock.lock();
		}
		try {
			EurekaApplications apps = objectMapper.readValue(appsJson, new TypeReference<EurekaApplications>() {});
			List<EurekaApplication> appList = apps.getApplication();

			if (appList != null && appList.size() > 0) {
				applications.clear();
				for (int i = 0; i < appList.size(); i++) {
					EurekaApplication eurekaApplication = appList.get(i);
					applications.put(eurekaApplication.getName(), eurekaApplication);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
		} finally {
			FLAG = 0;
			lock.unlock();
		}
	}

	/**
	 * 解析Eureka服务传过来的json串进行解析
	 * 
	 * @param appsJson
	 * @param serviceName
	 */
	private void analyseApplication(String appsJson, String serviceName) {
		if (StringUtils.isEmpty(serviceName)) {
			return;
		}
		lock.lock();
		try {
			
			EurekaApplication eurekaApplication = objectMapper.readValue(appsJson, new TypeReference<EurekaApplication>() {});
			if (eurekaApplication==null) {
				logger.error("Application instance not exist。[" + serviceName + "]");
			} else {
				refreshErrorEureka();
			}
			applications.put(eurekaApplication.getName(), eurekaApplication);
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
			logger.error("Target Application not exist。[" + serviceName + "]");
		} finally {
			eurekaRefresh.put(serviceName, System.currentTimeMillis());
			lock.unlock();
		}
	}

	/**
	 * 进行RestTemplate请求响应
	 * 
	 * @param serviceName service实例名称
	 * @param url         需要访问的方法url
	 * @param method      http请求模式
	 * @param entity      rest请求的实体
	 * @param returnClaz  请求需要返回的对象类
	 * @param             <T> 泛型
	 * @return
	 */
	public <T> T request(String serviceName, String url, HttpMethod method, HttpEntity<?> entity, Class<T> returnClaz) {
		int serviceCount = 0;
		// 根据指定次数，如果获取的连接地址不可用，可以再次尝试获取新的地址，否则就返回null
		while (serviceCount < SERVICE_RETRY_TIME) {
			String reqUrl = chooseService(serviceName, url);
			int count = 0;
			// 允许多次循环尝试访问
			while (count < NETWORK_RETRY_TIME) {
				try {
					ResponseEntity<T> returnBody = restTemplate.exchange(reqUrl + url, method, entity, returnClaz);
					return returnBody.getBody();
				} catch (ResourceAccessException rae) {
					System.out.println(rae.getMessage());
					if (rae.getMessage().toLowerCase().contains("connection refused")
							|| rae.getMessage().toLowerCase().contains("connection timed out")) {
						count++;
						if (RETRY_WAIT_MILLISECONDS > 0) {
							try {
								Thread.sleep(RETRY_WAIT_MILLISECONDS);
							} catch (Exception e) {
								logger.error("request retry wait error。[" + e.getMessage() + "]", e);
							}
						}
					}
				} catch (Exception e) {
					logger.error("request is error。[" + serviceName + "]", e);
					return null;
				}
			}
			// 如果多次访问失败，则添加到失败列表中
			if (count >= NETWORK_RETRY_TIME) {
				serviceCount++;
				HexieEurekaClient.eurekaError.put(reqUrl, System.currentTimeMillis());
			}
		}
		return null;
	}

	/**
	 * 获取详细的Service地址
	 * 
	 * @param serviceName
	 * @return
	 */
	public String chooseService(String serviceName, String url) {
		if (!applications.containsKey(serviceName) || !eurekaRefresh.containsKey(serviceName)
				|| (System.currentTimeMillis() - eurekaRefresh.get(serviceName) > REFRESH_INSTANCE_SECONDS * 1000)) {
			refreshApplication(serviceName);
		}
		EurekaApplicationInstance instance = randomGetInstanceInfo(serviceName);
		if (instance == null) {
			eurekaError.clear();
			refreshApplication(serviceName);
			instance = randomGetInstanceInfo(serviceName);
		}
		// TODO 如果还是未空， 则可能未启动相应的微服务，给出特殊提醒
		if (instance == null) {
			throw new EurekaClientException("The Eureka Client Serivce[" + serviceName + "] is not exists.");
		}
		return instance.getHomePageUrl();
	}

	/**
	 * 随机获取可以使用的Service的连接对象
	 * 
	 * @param serviceName
	 * @return
	 */
	private EurekaApplicationInstance randomGetInstanceInfo(String serviceName) {
		EurekaApplication eurekaApplication = this.getApplicatioin(serviceName);
		List<EurekaApplicationInstance> instanceInfos = eurekaApplication.getInstance();
		// 如果只有一个Serivce端口，那么直接返回
		if (instanceInfos.size() == 1) {
			return instanceInfos.get(0);
		}
		EurekaApplicationInstance instance;
		int count = 0;
		// 可以循环100次
		while (count < 100) {
			if (instanceInfos == null || instanceInfos.size() == 0) {
				break;
			}
			// 获取随机数，范围为0到连接句柄对象列表的大小
			int randomNum = ThreadLocalRandom.current().nextInt(0, instanceInfos.size());
			instance = instanceInfos.get(randomNum);
			// 如果随机出来的连接句柄已经是错误对象，或者在isAll为false时，与DDL的连接句柄是同一个时，则从列表中移除
			// 然后再次循环随机
			if (isErrorInstanceInfo(instance)) {
				instanceInfos.remove(randomNum);
			} else {
				return instance;
			}
		}
		return null;
	}

	/**
	 * 判断是否已经是失效的连接句柄
	 * 
	 * @param instance
	 * @return
	 */
	private boolean isErrorInstanceInfo(EurekaApplicationInstance instance) {
		if (instance == null) {
			return false;
		}
		return eurekaError.containsKey(instance.getHomePageUrl());
	}

	/**
	 * 根据设置来刷新错误的连接信息数据，以免部分数据恢复
	 */
	private void refreshErrorEureka() {
		Iterator<String> keys = eurekaError.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			if (System.currentTimeMillis() - eurekaError.get(key) > REFRESH_ERROR_INSTANCE_SECONDS * 1000) {
				eurekaError.remove(key);
			}
		}
	}

}