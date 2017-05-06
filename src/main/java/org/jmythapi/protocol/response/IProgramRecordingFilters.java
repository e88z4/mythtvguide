package org.jmythapi.protocol.response;

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1309;
import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1310;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_77;

import org.jmythapi.IVersionable;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.protocol.ProtocolVersionInfo;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;

public interface IProgramRecordingFilters extends IFlagGroup<IProgramRecordingFilters.Filters>, IVersionable {
	/**
	 * All possible recording filters.
	 * <p>
	 * 
	 * {@mythProtoVersionMatrix}
	 */
	public static enum Filters implements IFlag {
		/**
		 * New episode.
		 */
		NEW_EPISODE(0),
		
		/**
		 * Identifiable episodes.
		 */
		IDENTIFIABLE_EPISODE(1),
		
		/**
		 * First showing.
		 */
		FIRST_SHOWING(2),
		
		/**
		 * Prime time.
		 */
		PRIME_TIME(3),
		
		/**
		 * Commercial free.
		 */
		COMMERCIAL_FREE(4),
		
		/**
		 * High definition.
		 */
		HIGH_DEFINITION(5),
		
		/**
		 * This episode.
		 */
		THIS_EPISODE(6),
		
		/**
		 * This series.
		 */
		THIS_SERIES(7),
		
		/**
		 * This time.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_77,fromInfo={
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.GIT_COMMIT,value="a1f979393d4897d91b338581a14a2e245d76fa16")
		})
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1309)
		THIS_TIME(8),
		
		/**
		 * This day and time.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_77,fromInfo={
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.GIT_COMMIT,value="a1f979393d4897d91b338581a14a2e245d76fa16")
		})
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1309)
		THIS_DAY_AND_TIME(9),
		
		/**
		 * This channel.
		 */
		@MythProtoVersionAnnotation(from=PROTO_VERSION_77,fromInfo={
			@MythProtoVersionMetadata(key=ProtocolVersionInfo.GIT_COMMIT,value="9497ba1b63a5a6a91b06dbb85beea37444ef3ccc")
		})
		@MythDatabaseVersionAnnotation(from=DB_VERSION_1310)
		THIS_CHANNEL(10);
		
		private final int bitPosition;
		Filters(int bitPosition) {
			this.bitPosition = bitPosition;
		}
		
		public Long getFlagValue() {
			return Long.valueOf(1<<this.bitPosition);
		}
	}
}
