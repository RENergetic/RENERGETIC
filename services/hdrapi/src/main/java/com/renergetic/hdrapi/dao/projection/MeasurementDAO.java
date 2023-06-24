package com.renergetic.hdrapi.dao.projection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.hdrapi.dao.SimpleAssetDAO;
import com.renergetic.hdrapi.model.*;
import com.renergetic.hdrapi.model.details.MeasurementDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.repository.Query;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public interface MeasurementDAO {
    Long getId();

    String getName();

    String getLabel();

    Direction getDirection();

    Domain getDomain();

    String getSensorName();

    String getPhysicalName();

    String getSensorId();

    Long getTypeId();

    String getTypeName();

    String getTypeLabel();

    String getUnit();

    Long getAssetId();

    String getAssetName();

    String getAssetLabel();

    int getPanelCount();


}