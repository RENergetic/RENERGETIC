package com.renergetic.backdb.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class DemandRequestDAO {
    private Long id;

    private String uuid;

    @JsonProperty("asset_id")
    private Long assetId;

    @JsonProperty("measurement_id")
    private Long measurementId;

    @JsonProperty("measurement_type_id")
    private Long measurementTypeId;

    private String action;

    private String value;

    @JsonProperty("demand_request_start")
    private LocalDateTime start;

    @JsonProperty("demand_request_stop")
    private LocalDateTime stop;

    private String ext;

    @JsonProperty("update_date")
    private LocalDateTime updateDate;
}
