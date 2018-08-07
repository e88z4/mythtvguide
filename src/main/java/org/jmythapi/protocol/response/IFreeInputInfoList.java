package org.jmythapi.protocol.response;

import org.jmythapi.IVersionable;
import org.jmythapi.protocol.annotation.MythProtoVersionAnnotation;

import java.util.List;

import static org.jmythapi.protocol.ProtocolVersion.PROTO_VERSION_87;

@MythProtoVersionAnnotation(from=PROTO_VERSION_87)
public 	interface IFreeInputInfoList extends Iterable<IFreeInputInfo>, IVersionable {

    /**
     * Gets the free-inputs as list
     *
     * @return
     * 		the input devices as list
     */
    public List<IFreeInputInfo> asList();

    /**
     * Gets the input at the given position.
     *
     * @param idx
     * 		the input index
     * @return
     * 		the input
     */
    public IFreeInputInfo get(int idx);

    /**
     * The size of this list
     * @return
     * 		the size of this list
     */
    public int size();

    /**
     * Checks if this list is empty.
     *
     * @return
     * 		{@code true} if the list is empty.
     */
    public boolean isEmpty();
}
