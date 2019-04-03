package com.suizhu.work.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalendarDate {

	/**
	 * 这个月的第几天
	 */
	private Integer day;

	/**
	 * 星期几
	 */
	private Integer weekDay;

	/**
	 * 是否是今天
	 */
	private Boolean isToday;

	/**
	 * 该天的数据
	 */
	private Object info;

}
