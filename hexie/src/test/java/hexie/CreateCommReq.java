package hexie;

import java.io.Serializable;
import java.util.Arrays;

public class CreateCommReq implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5865797877545860743L;
	
	private String name;	//用户访问物业缴费查看的小区名字
	private String out_community_id;	//服务商对于当前传入小区记录再内部的唯一编号
	private String alias;	//小区别名，后续检索时候可以使用上
	private String support_type;	//物业社区支持类型,EXTERNAL_BIND_BILL
	private String address;	//小区地址
	private String latitude;	//维度
	private String longitude;	//经度
	private String address_memo;	//小区地址备注
	private String verify_type;	//户号验证方式，后续用户绑定小区呼号是执行的校验方式。
	private String province;	//省级行政编码 310000
	private String city;	//市级行政编码	310100
	private String county;	//区县行政编码，可空
	private String street_adcode;	//街道编码，可空
	private String community_adcode;	//社区行政编码，可空
	private String county_name;	//西湖区
	private String street_adcode_name;	//街道名称
	private String community_adcode_name;	//社区中文名称
	private String hot_line;	//小区热线电话,可空
	private String hot_line_start;	//服务热线每日服务开始时间，精度：分钟。00:00
	private String hot_line_end;	//24:00
	private CommunityPropertyCompany community_property_company;	//物业公司信息
	private CommunityService[] community_service;	//社区服务信息
	
	
	public static class CommunityPropertyCompany {
		
		private String name;	//物业公司名
		private String short_name;	//首次调用不传，插入新物业公司信息，并企鹅接口返回该字段。后续更新对应物业公司时传入该字段，更新对应记录信息
		private String pid;	//物业公司PID
		private String open_id;	//用于标记物业公司在应用下的唯一标识
		private String scale;	//物业小区规模，单位：个。用于后续消息广播场景根据物业负责小区规模进行分发调度，1-100
		private String logo;	//物业公司商标地址,http:/xxxxx
		private String description;	//企业描述,蓉信物业，在20xx年成立于四川成都，隶属xxx公司
		private String memo;	//物业公司备注
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getShort_name() {
			return short_name;
		}
		public void setShort_name(String short_name) {
			this.short_name = short_name;
		}
		public String getPid() {
			return pid;
		}
		public void setPid(String pid) {
			this.pid = pid;
		}
		public String getOpen_id() {
			return open_id;
		}
		public void setOpen_id(String open_id) {
			this.open_id = open_id;
		}
		public String getScale() {
			return scale;
		}
		public void setScale(String scale) {
			this.scale = scale;
		}
		public String getLogo() {
			return logo;
		}
		public void setLogo(String logo) {
			this.logo = logo;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getMemo() {
			return memo;
		}
		public void setMemo(String memo) {
			this.memo = memo;
		}
		@Override
		public String toString() {
			return "CommunityPropertyCompany [name=" + name + ", short_name=" + short_name + ", pid=" + pid
					+ ", open_id=" + open_id + ", scale=" + scale + ", logo=" + logo + ", description=" + description
					+ ", memo=" + memo + "]";
		}
	}

	public static class CommunityService {
		
		private String servce_type;	//当前小区开通办理业务：THIRD_PARTY_COMMUNITY_JIAOFEI
		private String daily_start;	//每日服务开始时间：00:00
		private String daily_end;	//每日服务结束时间：24:00
		private String billkey_url;	//跳转至服务商物业户号查询服务地址,alipays://platformapi/startapp?appId=XXX&page=%2Fpages%2Fcommunity%2Fhouseaccountquery%2Fhouseaccountout_
		private String out_bill_url;	//房服务商侧服务账单的跳转地址,alipays://platformapi/startapp?appId=XXX&page=%2Fpages%2Fcommunity%2Fhouseaccountquery%2Fhouseaccount-
		public String getServce_type() {
			return servce_type;
		}
		public void setServce_type(String servce_type) {
			this.servce_type = servce_type;
		}
		public String getDaily_start() {
			return daily_start;
		}
		public void setDaily_start(String daily_start) {
			this.daily_start = daily_start;
		}
		public String getDaily_end() {
			return daily_end;
		}
		public void setDaily_end(String daily_end) {
			this.daily_end = daily_end;
		}
		public String getBillkey_url() {
			return billkey_url;
		}
		public void setBillkey_url(String billkey_url) {
			this.billkey_url = billkey_url;
		}
		public String getOut_bill_url() {
			return out_bill_url;
		}
		public void setOut_bill_url(String out_bill_url) {
			this.out_bill_url = out_bill_url;
		}
		@Override
		public String toString() {
			return "CommunityService [servce_type=" + servce_type + ", daily_start=" + daily_start + ", daily_end="
					+ daily_end + ", billkey_url=" + billkey_url + ", out_bill_url=" + out_bill_url + "]";
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOut_community_id() {
		return out_community_id;
	}

	public void setOut_community_id(String out_community_id) {
		this.out_community_id = out_community_id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getSupport_type() {
		return support_type;
	}

	public void setSupport_type(String support_type) {
		this.support_type = support_type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAddress_memo() {
		return address_memo;
	}

	public void setAddress_memo(String address_memo) {
		this.address_memo = address_memo;
	}

	public String getVerify_type() {
		return verify_type;
	}

	public void setVerify_type(String verify_type) {
		this.verify_type = verify_type;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getStreet_adcode() {
		return street_adcode;
	}

	public void setStreet_adcode(String street_adcode) {
		this.street_adcode = street_adcode;
	}

	public String getCommunity_adcode() {
		return community_adcode;
	}

	public void setCommunity_adcode(String community_adcode) {
		this.community_adcode = community_adcode;
	}

	public String getCounty_name() {
		return county_name;
	}

	public void setCounty_name(String county_name) {
		this.county_name = county_name;
	}

	public String getStreet_adcode_name() {
		return street_adcode_name;
	}

	public void setStreet_adcode_name(String street_adcode_name) {
		this.street_adcode_name = street_adcode_name;
	}

	public String getCommunity_adcode_name() {
		return community_adcode_name;
	}

	public void setCommunity_adcode_name(String community_adcode_name) {
		this.community_adcode_name = community_adcode_name;
	}

	public String getHot_line() {
		return hot_line;
	}

	public void setHot_line(String hot_line) {
		this.hot_line = hot_line;
	}

	public String getHot_line_start() {
		return hot_line_start;
	}

	public void setHot_line_start(String hot_line_start) {
		this.hot_line_start = hot_line_start;
	}

	public String getHot_line_end() {
		return hot_line_end;
	}

	public void setHot_line_end(String hot_line_end) {
		this.hot_line_end = hot_line_end;
	}

	public CommunityPropertyCompany getCommunity_property_company() {
		return community_property_company;
	}

	public void setCommunity_property_company(CommunityPropertyCompany community_property_company) {
		this.community_property_company = community_property_company;
	}

	public CommunityService[] getCommunity_service() {
		return community_service;
	}

	public void setCommunity_service(CommunityService[] community_service) {
		this.community_service = community_service;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "CreateCommReq [name=" + name + ", out_community_id=" + out_community_id + ", alias=" + alias
				+ ", support_type=" + support_type + ", address=" + address + ", latitude=" + latitude + ", longitude="
				+ longitude + ", address_memo=" + address_memo + ", verify_type=" + verify_type + ", province="
				+ province + ", city=" + city + ", county=" + county + ", street_adcode=" + street_adcode
				+ ", community_adcode=" + community_adcode + ", county_name=" + county_name + ", street_adcode_name="
				+ street_adcode_name + ", community_adcode_name=" + community_adcode_name + ", hot_line=" + hot_line
				+ ", hot_line_start=" + hot_line_start + ", hot_line_end=" + hot_line_end
				+ ", community_property_company=" + community_property_company + ", community_service="
				+ Arrays.toString(community_service) + "]";
	}

	
	
}
