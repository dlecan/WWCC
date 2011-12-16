package com.dlecan.sqli.wwcc;

import java.util.Comparator;

import org.joda.time.Interval;

public class OlderFirstIntervalComparator implements Comparator<Interval> {

	public int compare(Interval interval1, Interval interval2) {
		int result;
		
		if (interval1.equals(interval2)) {
			result = 0;
		} else {
			if (interval1.getStart().isBefore(interval2.getStart())) {
				result = -1;
			} else {
				result = 1;
			}
		}
		
		return result;
	}


}
