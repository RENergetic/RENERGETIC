package com.renergetic.common.dao.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.model.details.MeasurementTags;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeasurementTagsDAO extends DetailsDAO {
    @JsonProperty(value = "tag_id", required = true)
    Long tagId;


    public MeasurementTagsDAO(String key, String value, Long measurementId) {
        super(key, value);
        this.tagId = measurementId;
    }


    public static MeasurementTagsDAO create(MeasurementTags tag) {
        MeasurementTagsDAO dao = null;
        if (tag != null) {
            dao = new MeasurementTagsDAO(tag.getKey(), tag.getValue(), tag.getId());
        }
        return dao;
    }

    public   MeasurementTags mapToEntity() {
        MeasurementTags  tag = new MeasurementTags( );

        tag.setId(this.tagId);
        tag.setKey(this.getKey());
        tag.setValue(this.getValue());

        return tag;
    }



}
