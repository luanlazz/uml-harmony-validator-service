package com.inconsistency.javakafka.kafkajava.entities.dto;

import java.util.Comparator;

import org.apache.commons.lang.builder.CompareToBuilder;

public class InconsistencyErrorDTOComparator implements Comparator<InconsistencyNotificationDTO> {

	@Override
	public int compare(InconsistencyNotificationDTO o1, InconsistencyNotificationDTO o2) {
		return new CompareToBuilder().append(o1.getConcentration(), o2.getConcentration())
				.append(o1.getElId(), o2.getElId()).toComparison();
	}
}
