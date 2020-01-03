package com.yumu.hexie.common.util;

public class CardUtil {

	/**
	 * 芝麻转换成积分
	 * @param zhima
	 * @return
	 */
	public static int convertZhima(int zhima) {
		int points = zhima;
		if (points == 0) {
			points = 88;
		}else if (points < 800) {
			points = 800;
		}else if (points > 8800) {
			points = 8800;
		}
		return points;
	}
}
