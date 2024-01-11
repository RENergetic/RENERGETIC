package com.renergetic.hdrapi.dao.details;

import com.renergetic.common.dao.details.DetailsDAO;

public class TagDAO extends DetailsDAO {
    public Long id;

    public TagDAO(String key, String value, Long id) {
        super(key, value);
        this.id = id;
    }
}
