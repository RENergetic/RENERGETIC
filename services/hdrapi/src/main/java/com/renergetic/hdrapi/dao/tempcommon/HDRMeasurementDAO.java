package com.renergetic.hdrapi.dao.tempcommon;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.dao.MeasurementDAOResponse;
import com.renergetic.common.utilities.DateConverter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class HDRMeasurementDAO {

    //todo: unique key: name-asset-sensor-type-direction
    @JsonProperty(required = false)
    private Long id;
    @JsonProperty(required = true)
    private Long timestamp;
    @JsonProperty(required = true)
    private MeasurementDAOResponse measurement;


    public static HDRMeasurementDAO create(HDRMeasurement mHDRMeasurement) {

        HDRMeasurementDAO dao = null;

        if (mHDRMeasurement != null) {
            dao = new HDRMeasurementDAO();
            dao.setTimestamp(DateConverter.toEpoch(mHDRMeasurement.getTimestamp()));

            dao.setMeasurement(MeasurementDAOResponse.create(mHDRMeasurement.getMeasurement(), null, null));
            dao.setId(mHDRMeasurement.getId());
        }
        return dao;
    }

    public HDRMeasurement mapToEntity() {
        HDRMeasurement mHDRMeasurement = new HDRMeasurement();
        if (this.id != null)
            mHDRMeasurement.setId(this.id);
        mHDRMeasurement.setTimestamp(DateConverter.toLocalDateTime(this.getTimestamp()));
        mHDRMeasurement.setMeasurement(this.measurement.mapToEntity());
        return mHDRMeasurement;
    }

}
