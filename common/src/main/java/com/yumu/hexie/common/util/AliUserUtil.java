package com.yumu.hexie.common.util;

import org.apache.commons.lang3.StringUtils;

public class AliUserUtil {

	/**
	 * 传入openid或者userid
	 * @param userid
	 */
	public static boolean isAliUser(String userId) {
		return StringUtils.isNumeric(userId);
	}
}
