package com.zxxkj.util;

public class PlanStatusUtil {

	public static boolean planStatus(int planStatus) {
		switch (planStatus) {
		case 0://未执行
			
			return true;
		case 2://计划完成
			
			return true;
		case 3://取消计划
			
			return true;
		case 4://关闭计划
			
			return true;

		default:
			return false;
		}
	}
}
