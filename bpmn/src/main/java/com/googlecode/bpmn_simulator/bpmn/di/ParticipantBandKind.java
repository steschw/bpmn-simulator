package com.googlecode.bpmn_simulator.bpmn.di;

public enum ParticipantBandKind {
	TOP_INITIATING,
	MIDDLE_INITIATING,
	BOTTOM_INITIATING,
	TOP_NON_INITIATING,
	MIDDLE_NON_INITIATING,
	BOTTOM_NON_INITIATING;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	public static ParticipantBandKind fromString(final String value) {
		for (final ParticipantBandKind kind : values()) {
			if (kind.toString().equals(value)) {
				return kind;
			}
		}
		return null;
	}

}
