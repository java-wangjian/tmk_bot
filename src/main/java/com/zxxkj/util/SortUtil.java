package com.zxxkj.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class SortUtil {

	/**
	 * 根据群成员数量排序
	 * @param groupMemberDataArr
	 * @return
	 */
	public static JSONObject[] DichotomySort(JSONArray groupMemberDataArr) {
		JSONObject[] array = new JSONObject[groupMemberDataArr.size()];
		
		for (int i = 0; i < groupMemberDataArr.size(); i++) {

			array[i] = groupMemberDataArr.getJSONObject(i);
		}
		
		for (int i = 0; i < array.length; i++) {
			int start, end, mid;
			start = 0;
			end = i - 1;
			mid = 0;
			JSONObject temp = array[i];
			while (start <= end) {
				mid = (start + end) / 2;
				if (array[mid].getIntValue("grade") > temp.getIntValue("grade"))// 要排序元素在已经排过序的数组左边
				{
					end = mid - 1;
				} else {
					start = mid + 1;
				}
			}
			for (int j = i - 1; j > end; j--)// 找到了要插入的位置，然后将这个位置以后的所有元素向后移动

			{
				array[j + 1] = array[j];
			}
			array[end + 1] = temp;

		}
		
		return array;
	}
	
	/**
	 * 根据托管情况排序
	 * @param groupMemberDataArr
	 * @return
	 */
	public static JSONArray PortSort(JSONArray portOnArr) {
		JSONObject[] array = new JSONObject[portOnArr.size()];
		
		for (int i = 0; i < portOnArr.size(); i++) {

			array[i] = portOnArr.getJSONObject(i);
		}
		
		for (int i = 0; i < array.length; i++) {
			int start, end, mid;
			start = 0;
			end = i - 1;
			mid = 0;
			JSONObject temp = array[i];
			while (start <= end) {
				mid = (start + end) / 2;
				if (array[mid].getIntValue("port") > temp.getIntValue("port"))// 要排序元素在已经排过序的数组左边
				{
					end = mid - 1;
				} else {
					start = mid + 1;
				}
			}
			for (int j = i - 1; j > end; j--)// 找到了要插入的位置，然后将这个位置以后的所有元素向后移动

			{
				array[j + 1] = array[j];
			}
			array[end + 1] = temp;

		}
		
		JSONArray result = new JSONArray();
		
		for (int i = 0; i < array.length; i++) {
			result.add(array[i]);
		}
		
		return result;
	}
}
