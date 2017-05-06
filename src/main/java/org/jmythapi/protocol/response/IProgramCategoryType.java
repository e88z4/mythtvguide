package org.jmythapi.protocol.response;

import static org.jmythapi.database.DatabaseVersion.DB_VERSION_1244;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_79;
import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_LATEST;
import static org.jmythapi.protocol.ProtocolVersionInfo.GIT_COMMIT;
import static org.jmythapi.protocol.ProtocolVersionInfo.SVN_COMMIT;

import org.jmythapi.IVersionable;
import org.jmythapi.database.annotation.MythDatabaseVersionAnnotation;
import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;
import org.jmythapi.protocol.annotation.MythProtoVersionMetadata;
import org.jmythapi.protocol.utils.EnumUtils;

/**
 * Created by felix on 20/04/17.
 */
public interface IProgramCategoryType extends IEnumGroup<IProgramCategoryType.CategoryType>, IVersionable {

    public static enum CategoryType implements IVersionableValue {
        @MythDatabaseVersionAnnotation(from = DB_VERSION_1244)
        NONE(0),
        @MythDatabaseVersionAnnotation(from = DB_VERSION_1244)
        MOVIE(1),
        @MythDatabaseVersionAnnotation(from = DB_VERSION_1244)
        SERIES(2),
        @MythDatabaseVersionAnnotation(from = DB_VERSION_1244)
        SPORTS(3),
        @MythDatabaseVersionAnnotation(from = DB_VERSION_1244)
        TVSHOW(4);

        private VersionablePair[] values;

        private CategoryType(int value) {
            this(VersionablePair.valueOf(value));
        }

        private CategoryType(VersionablePair... values) {
            this.values = values;
        }

        public VersionablePair[] getValues() {
            return this.values;
        }

        public Long getFlagValue() {
            return this.getValue(PROTO_VERSION_LATEST);
        }

        public Long getValue(ProtocolVersion protoVersion) {
            return EnumUtils.getVersionableValue(protoVersion, this);
        }

    }

    public int getCategoryTypeValue();
    public CategoryType getCategoryType();
}
