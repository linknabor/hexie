package hexie;

import java.util.HashMap;
import java.util.Map;

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




}
