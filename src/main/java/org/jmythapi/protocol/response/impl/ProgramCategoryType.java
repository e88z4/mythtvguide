package org.jmythapi.protocol.response.impl;

import org.jmythapi.protocol.ProtocolVersion;
import org.jmythapi.protocol.response.IProgramCategoryType;

/**
 * Created by felix on 20/04/17.
 */
public class ProgramCategoryType extends AVersionableEnumGroup<IProgramCategoryType.CategoryType> implements IProgramCategoryType {
    private static final long serialVersionUID = 1L;

    public ProgramCategoryType(ProtocolVersion protoVersion, long value)
    {
        super(CategoryType.class,protoVersion,value);
    }

    public CategoryType getCategoryType()
    {
        return this.getEnum();
    }

    public int getCategoryTypeValue()
    {
        return (int)this.longValue();
    }
}
