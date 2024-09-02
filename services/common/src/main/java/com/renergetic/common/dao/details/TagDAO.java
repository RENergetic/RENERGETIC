package com.renergetic.common.dao.details;

import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.details.MeasurementTags;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class TagDAO extends DetailsDAO {
    private Long id;

    public TagDAO(String key, String value, Long id) {
        super(key, value);
        this.id = id;
    }

    public static TagDAO create(MeasurementTags tag ) {
        TagDAO dao = null;
        if (tag != null) {
            dao = new TagDAO( tag.getKey(), tag.getValue(),tag.getId() );
        }
        return dao;
    }

    public MeasurementTags mapToEntity() {
        MeasurementTags tag = new MeasurementTags();

        tag.setId(this.id);
//        if (this.measurementId != null) {
//            var m = new Measurement();
//            m.setId(this.measurementId);
//            tag.setMeasurements(List.of(m));
//        }
        tag.setKey(this.getKey());
        tag.setValue(this.getValue());

        return tag;
    }

}
