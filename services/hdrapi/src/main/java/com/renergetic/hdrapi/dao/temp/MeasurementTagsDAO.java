package com.renergetic.hdrapi.dao.temp;

import com.fasterxml.jackson.annotation.JsonProperty;


public class MeasurementTagsDAO extends DetailsDAO {
    @JsonProperty(value = "tag_id", required = true)
    Long tagId;


    public MeasurementTagsDAO(String key, String value, Long measurementId) {
        super(key, value);
        this.tagId = measurementId;
    }


    public static MeasurementTagsDAO create(MeasurementTagss tag) {
        MeasurementTagsDAO dao = null;
        if (tag != null) {
            dao = new MeasurementTagsDAO(tag.getKey(), tag.getValue(), tag.getId());
        }
        return dao;
    }

    public   MeasurementTagss mapToEntity() {
        MeasurementTagss  tag = new MeasurementTagss( );

        tag.setId(this.tagId);
        tag.setKey(this.getKey());
        tag.setValue(this.getValue());

        return tag;
    }



}
