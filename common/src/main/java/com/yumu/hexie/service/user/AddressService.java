package com.yumu.hexie.service.user;

import java.util.List;

import com.yumu.hexie.integration.eshop.resp.RgroupRegionsVO;
import com.yumu.hexie.integration.wuye.vo.HexieAddress;
import com.yumu.hexie.model.distribution.RgroupAreaItem;
import com.yumu.hexie.model.distribution.region.AmapAddress;
import com.yumu.hexie.model.distribution.region.City;
import com.yumu.hexie.model.distribution.region.County;
import com.yumu.hexie.model.distribution.region.Province;
import com.yumu.hexie.model.distribution.region.Region;
import com.yumu.hexie.model.user.Address;
import com.yumu.hexie.model.user.User;
import com.yumu.hexie.service.user.req.AddressReq;
import com.yumu.hexie.vo.RgroupAddressVO;


/**
 * 用户服务
 */
public interface AddressService {

	//添加地址
	 Address addAddress(AddressReq address);

	 void deleteAddress(long id,long userId);
    //设置默认地址
     Address configDefaultAddress(User user, long addressId);

     Address queryDefaultAddress(User user);
    //根据id查询地址
     Address queryAddressById(long id);

	 List<Address> queryAddressByUser(long userId);

	 List<Region> queryRegions(int type,long regionId);

	 List<AmapAddress> queryAroundByCoordinate(double longitude, double latitude);

	 List<Address> getAddressByuserIdAndAddress(long id, String cell_addr);

	 List<Address> getAddressByMain(long id,boolean main);

	void updateDefaultAddress(User user, HexieAddress addr);

	List<Address> queryBindedAddressByUser(long userId);

	List<Province> queryProvince();

	List<City> queryCity(long provinceId);

	List<County> queryCounty(long cityId);

	List<Address> queryBindedAddressByUserAndRegion(User user);

	List<RgroupRegionsVO> querySectInfo(User user, List<RgroupAreaItem> rgroupAreas) throws Exception;

	Address addAddress4Rgroup(User user, AddressReq addressReq);

	List<Address> queryRgroupAddressByUser(long userId, String ruleId, String regionId);

	RgroupAddressVO queryRgroupDefaultAddress(long userId, String ruleId);

}
