package com.inconsistency.javakafka.kafkajava.entities.dto;

import java.util.Comparator;

public class InconsistencyErrorDTOComparator implements Comparator<InconsistencyErrorDTO> {

	@Override
	public int compare(InconsistencyErrorDTO o1, InconsistencyErrorDTO o2) {
		if (o1 == null && o2 == null) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			return 1;
		} else {
			return o1.getSeverity() - o2.getSeverity();
		}
	}
}
