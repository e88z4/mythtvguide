package org.jmythapi.protocol.response.impl;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IProgramRecordingFilters;

public class ProgramRecordingFilters extends AEnumFlagGroup<IProgramRecordingFilters.Filters> implements IProgramRecordingFilters {
	private static final long serialVersionUID = 1L;

	public ProgramRecordingFilters(ProtocolVersion protoVersion, long programFlags) {
		super(IProgramRecordingFilters.Filters.class,protoVersion, programFlags);
	}

	public static ProgramRecordingFilters valueOf(ProtocolVersion protoVersion, String flagString) {
		return valueOf(ProgramRecordingFilters.class, protoVersion, flagString);
	}
	
	public static ProgramRecordingFilters valueOf(ProtocolVersion protoVersion, Filters... filters) {
		return valueOf(ProgramRecordingFilters.class,protoVersion,filters);
	}
}
