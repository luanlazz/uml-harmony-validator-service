package com.inconsistency.javakafka.kafkajava.entities;

import java.util.Comparator;

public class InconsistencyConcentrationComparator implements Comparator<InconsistencyConcentration> {

	@Override
	public int compare(InconsistencyConcentration o1, InconsistencyConcentration o2) {
		if (o1 == null && o2 == null) {
			return 0;
		} else if (o1 == null) {
			return -1;
		} else if (o2 == null) {
			return 1;
		} else {
			return Double.compare(o1.getConcentration(), o2.getConcentration());
		}
	}
}
