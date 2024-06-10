package com.renergetic.common.dao.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.details.MeasurementTags;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MeasurementTagsDAO extends DetailsDAO {
    @JsonProperty(value = "tag_id", required = true)
    Long tagId;
    @JsonProperty(value = "measurement_id", required = false)
    Long measurementId;


    public MeasurementTagsDAO(String key, String value, Long measurementId) {
        super(key, value);
        this.measurementId = measurementId;
    }

    public MeasurementTagsDAO(Long tagId, String key, String value, Long measurementId) {
        super(key, value);
        this.tagId = tagId;
        this.measurementId = measurementId;
    }

    public static MeasurementTagsDAO create(MeasurementTags tag) {
        return MeasurementTagsDAO.create(tag, null);
    }

    public static MeasurementTagsDAO create(MeasurementTags tag, Long measurementId) {
        MeasurementTagsDAO dao = null;
        if (tag != null) {
            dao = new MeasurementTagsDAO(tag.getId(), tag.getKey(), tag.getValue(), measurementId);
        }
        return dao;
    }

    public MeasurementTags mapToEntity() {
        MeasurementTags tag = new MeasurementTags();

        tag.setId(this.tagId);
        if (this.measurementId != null) {
            var m = new Measurement();
            m.setId(this.measurementId);
            tag.setMeasurements(List.of(m));
        }
        tag.setKey(this.getKey());
        tag.setValue(this.getValue());

        return tag;
    }


}
