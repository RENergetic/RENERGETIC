package com.renergetic.common.dao.details;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TagDAO extends DetailsDAO {
    private Long id;

    public TagDAO(String key, String value, Long id) {
        super(key, value);
        this.id = id;
    }
}
