package com.yumu.hexie.integration.baidu;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yumu.hexie.common.util.JacksonJsonUtil;

@Component
public class BaiduMapUtil {

	private static final Logger logger = LoggerFactory.getLogger(BaiduMapUtil.class);

	@Autowired
	private RestTemplate restTemplate;
	
	/**
	 * 坐标转换（百度）
	 */
	public String findByCoordinateGetBaidu(String coordinate) {
		
		LinkedMultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<String, String>();
		paramsMap.add("coords", coordinate);//（经度，纬度）
		paramsMap.add("from", "1");//1：GPS设备获取的角度坐标，WGS84坐标;
		paramsMap.add("to", "5");//5：bd09ll(百度经纬度坐标);
		paramsMap.add("ak", ConstantBaidu.MAPKEY);

		String requestUrl = "http://api.map.baidu.com/geoconv/v1/";
		logger.info("baiduMap util, request url : " + requestUrl + "param : " + paramsMap);
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUrl);
		URI uri = builder.queryParams(paramsMap).build().encode().toUri();
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
		logger.info("baiduMap util, response : " + response);
		Double x = 0d;
		Double y = 0d;
		if (HttpStatus.OK == response.getStatusCode()) {
			try {
				String str = response.getBody();
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				@SuppressWarnings("unchecked")
				Map<String, ArrayList<Map<String, ?>>> map = objectMapper.readValue(str, Map.class);
				ArrayList<Map<String, ?>> list = map.get("result");
				x = (Double) list.get(0).get("x");
				y = (Double) list.get(0).get("y");
				
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		return x+","+y;
	}
	
	/**
	 * 通过坐标获取市
	 */
	public String findByBaiduGetCity(String coordinate) {
		
		if(StringUtils.isEmpty(coordinate)) {
			return "";
		}
		String[] coors = coordinate.split(",");
		String lng = coors[0];
		String lat = coors[1];
		coordinate = lat+","+lng;
		LinkedMultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<String, String>();
		paramsMap.add("location", coordinate);//lat<纬度>,lng<经度>
		paramsMap.add("latest_admin", "1");
		paramsMap.add("pois", "1");
		paramsMap.add("output", ConstantBaidu.OUTPUT);
		paramsMap.add("ak", ConstantBaidu.MAPKEY);
		
		String requestUrl = "http://api.map.baidu.com/geocoder/v2/";
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(requestUrl);
		URI uri = builder.queryParams(paramsMap).build().encode().toUri();
		logger.info("baiduMap util, request url : " + requestUrl + "param : " + paramsMap);
		ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
		logger.info("baiduMap util, response : " + response);
		
		String city = "";
		if (HttpStatus.OK == response.getStatusCode()) {
			try {
				String str = response.getBody();
				ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
				@SuppressWarnings("unchecked")
				LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, ?>>> map = objectMapper.readValue(str, LinkedHashMap.class);
				LinkedHashMap<String, LinkedHashMap<String, ?>> mapResult = map.get("result");
				city = (String) mapResult.get("addressComponent").get("province");
				
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		return city;
	}
	
	public static void main(String args[]) throws JsonParseException, JsonMappingException, IOException {
//		System.out.println(findByBaiduGetCity("106.74590655179391,26.671247860481976"));
		
		String str = "{\"status\":0,\"result\":{\"location\":{\"lng\":106.7459065517939,\"lat\":26.67124783924754},\"formatted_address\":\"贵州省贵阳市乌当区\",\"business\":\"\",\"addressComponent\":{\"country\":\"中国\",\"country_code\":0,\"country_code_iso\":\"CHN\",\"country_code_iso2\":\"CN\",\"province\":\"贵州省\",\"city\":\"贵阳市\",\"city_level\":2,\"district\":\"乌当区\",\"town\":\"\",\"town_code\":\"\",\"adcode\":\"520112\",\"street\":\"\",\"street_number\":\"\",\"direction\":\"\",\"distance\":\"\"},\"pois\":[{\"addr\":\"贵阳市乌当区一三八县道\",\"cp\":\" \",\"direction\":\"西南\",\"distance\":\"257\",\"name\":\"培席\",\"poiType\":\"行政地标\",\"point\":{\"x\":106.74721575315269,\"y\":26.672964210655825},\"tag\":\"行政地标;村庄\",\"tel\":\"\",\"uid\":\"4cf8d24e17afd8e59cf7795f\",\"zip\":\"\",\"parent_poi\":{\"name\":\"\",\"tag\":\"\",\"addr\":\"\",\"point\":{\"x\":0.0,\"y\":0.0},\"direction\":\"\",\"distance\":\"\",\"uid\":\"\"}},{\"addr\":\"贵阳市乌当区\",\"cp\":\" \",\"direction\":\"东南\",\"distance\":\"479\",\"name\":\"后头山\",\"poiType\":\"行政地标\",\"point\":{\"x\":106.74397287026274,\"y\":26.674707487378055},\"tag\":\"行政地标;村庄\",\"tel\":\"\",\"uid\":\"49d95938cf961440c61e5170\",\"zip\":\"\",\"parent_poi\":{\"name\":\"\",\"tag\":\"\",\"addr\":\"\",\"point\":{\"x\":0.0,\"y\":0.0},\"direction\":\"\",\"distance\":\"\",\"uid\":\"\"}},{\"addr\":\"贵州省贵阳市乌当区贵阳绕城高速公路贵阳火车东站附近\",\"cp\":\" \",\"direction\":\"西\",\"distance\":\"501\",\"name\":\"贵阳火车东-出站口\",\"poiType\":\"出入口\",\"point\":{\"x\":106.75040473771206,\"y\":26.671019134003815},\"tag\":\"出入口;车站出口\",\"tel\":\"\",\"uid\":\"8af0e77875908831d8131844\",\"zip\":\"\",\"parent_poi\":{\"name\":\"贵阳东站\",\"tag\":\"交通设施;火车站\",\"addr\":\"贵州省贵阳市乌当区138县道附近\",\"point\":{\"x\":106.75107846684432,\"y\":26.67069629646045},\"direction\":\"西\",\"distance\":\"579\",\"uid\":\"84d164461b848e7738221c8c\"}},{\"addr\":\"贵州省贵阳市乌当区138县道附近\",\"cp\":\" \",\"direction\":\"西\",\"distance\":\"579\",\"name\":\"贵阳东站\",\"poiType\":\"交通设施\",\"point\":{\"x\":106.75107846684432,\"y\":26.67069629646045},\"tag\":\"交通设施;火车站\",\"tel\":\"\",\"uid\":\"84d164461b848e7738221c8c\",\"zip\":\"\",\"parent_poi\":{\"name\":\"\",\"tag\":\"\",\"addr\":\"\",\"point\":{\"x\":0.0,\"y\":0.0},\"direction\":\"\",\"distance\":\"\",\"uid\":\"\"}},{\"addr\":\"贵州省贵阳市乌当区贵阳绕城高速公路贵阳火车东站\",\"cp\":\" \",\"direction\":\"西\",\"distance\":\"617\",\"name\":\"贵阳火车东-进站口\",\"poiType\":\"出入口\",\"point\":{\"x\":106.751419822938,\"y\":26.670680154559034},\"tag\":\"出入口;车站入口\",\"tel\":\"\",\"uid\":\"fb253422249f43d6b11e363a\",\"zip\":\"\",\"parent_poi\":{\"name\":\"贵阳东站\",\"tag\":\"交通设施;火车站\",\"addr\":\"贵州省贵阳市乌当区138县道附近\",\"point\":{\"x\":106.75107846684432,\"y\":26.67069629646045},\"direction\":\"西\",\"distance\":\"579\",\"uid\":\"84d164461b848e7738221c8c\"}},{\"addr\":\"贵州省贵阳市乌当区奶牛场片区\",\"cp\":\" \",\"direction\":\"西\",\"distance\":\"604\",\"name\":\"贵阳东站(贵阳市乌当区奶牛场片区)\",\"poiType\":\"交通设施\",\"point\":{\"x\":106.75132100933193,\"y\":26.670849644408709},\"tag\":\"交通设施;长途汽车站\",\"tel\":\"\",\"uid\":\"3440c5653614f6b91f389488\",\"zip\":\"\",\"parent_poi\":{\"name\":\"\",\"tag\":\"\",\"addr\":\"\",\"point\":{\"x\":0.0,\"y\":0.0},\"direction\":\"\",\"distance\":\"\",\"uid\":\"\"}}],\"roads\":[],\"poiRegions\":[],\"sematic_description\":\"培席西南257米\",\"cityCode\":146}}";
//		String str = "{\"status\":0,\"result\":[{\"x\":121.4847865110217,\"y\":31.234284571920424}]}";
		ObjectMapper objectMapper = JacksonJsonUtil.getMapperInstance(false);
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, ?>>> map = objectMapper.readValue(str, LinkedHashMap.class);
		LinkedHashMap<String, LinkedHashMap<String, ?>> mapResult = map.get("result");
		System.out.println(mapResult.get("addressComponent").get("province"));
		
	}
}
