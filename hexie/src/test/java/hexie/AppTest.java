package hexie;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.yumu.hexie.common.config.AppConfig;
import com.yumu.hexie.integration.baidu.BaiduMapUtil;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 * 
 * @param <T>
 * @param <T>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppConfig.class)
public class AppTest extends TestCase {


	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Test
	public void testRedisTemplate() {

		Map<String, String> map = new HashMap<String, String>();
		map.put("tom", "cat");
		map.put("jerry", "mouse");
		
		
		Map<String, Map<String, String>> map2 = new HashMap<String, Map<String, String>>();
		
		Map<String, String> map3 = new HashMap<String, String>();
		map3.put("aa", "a");
		map3.put("bb", "b");
		
		map2.put("222", map3);
		
		map.put("tom", "cat");
		map.put("jerry", "mouse");
		
		redisTemplate.opsForHash().putAll("test1", map);
		redisTemplate.opsForHash().putAll("test2", map2);
		
	}
	
	@Autowired
	private BaiduMapUtil baiduMapUtil;
	
	@Test
	public void testBaiduMap() {
		
		String str = baiduMapUtil.findByBaiduGetCity("106.74590655179391,26.671247860481976");
		System.out.println("str:" + str);
		
		String str2 = baiduMapUtil.findByCoordinateGetBaidu("121.4737,31.23037");
		System.out.println("str2:" + str2);
		
		String str3 = baiduMapUtil.findByBaiduGetCity("");
		System.out.println("str3:" + str3);
		
		String str4 = baiduMapUtil.findByCoordinateGetBaidu("");
		System.out.println("str4:" + str4);
		
		String str5 = baiduMapUtil.findByBaiduGetCity("null");
		System.out.println("str5:" + str5);
		
		String str6 = baiduMapUtil.findByCoordinateGetBaidu("null");
		System.out.println("str6:" + str6);
		
		
	}

	@Test
	public void readPropFile () throws IOException{
		
		Properties sysProp = System.getProperties();
		String password = sysProp.getProperty("jasypt.encryptor.password");
		System.out.println("password : " + password);
		
		List<String> fileList = new ArrayList<>();
		fileList.add("d:/tmp/props/application.properties");
		fileList.add("d:/tmp/props/wechat.properties");
		fileList.add("d:/tmp/props/alipay.properties");
		
		for (String filePath : fileList) {
			Properties props = new Properties();
			InputStream in = new FileInputStream(filePath);
			props.load(in);
			
			Iterator<Entry<Object, Object>> it = props.entrySet().iterator();
			while(it.hasNext()) {
				Entry<Object, Object> entry = it.next();
//				System.out.println("key:" + entry.getKey() + ", value : " + entry.getValue());
				
				BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		        textEncryptor.setPassword(password);
		        
		        String value = (String) entry.getValue();
		        String encryptValue = textEncryptor.encrypt(value);
		        System.out.println("key:" + entry.getKey() + ", value : " + encryptValue);
		        
			}

		}
		
	}
	
	@Test
	public void decrypt() throws IOException{
		
		Properties sysProp = System.getProperties();
		String password = sysProp.getProperty("jasypt.encryptor.password");
		System.out.println("password : " + password);
		
		String miniprogramAppId = "7ICVuS7Hk8bvyWGM3zvPfZuMtdrf5YgjMk33M2V1i8E=";
		String miniprogramSecret = "2laD+WaEwQApfh0jv4EjWWU8f8/Kq51GWgbGL0v4d70R9NMX9zcPUBOgngQTmVbW";
		
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPassword(password);
        
        String decryptMiniAppid = textEncryptor.decrypt(miniprogramAppId);
        System.out.println("decryptMiniAppid:" + decryptMiniAppid);
        
        String decryptMiniSecret = textEncryptor.decrypt(miniprogramSecret);
        System.out.println("decryptMiniSecret:" + decryptMiniSecret);
	}



}
