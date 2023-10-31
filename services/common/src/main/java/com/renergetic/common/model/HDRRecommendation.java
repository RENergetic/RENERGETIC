package com.renergetic.common.model;

import com.renergetic.common.model.details.MeasurementDetails;
import com.renergetic.common.model.details.MeasurementTags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hdr_recommendation", uniqueConstraints =
@UniqueConstraint(columnNames = {"timestamp", "tag_id"}))

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class HDRRecommendation {

    //todo: unique key: name-asset-sensor-type-direction
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @OneToOne(cascade = CascadeType.REFRESH, mappedBy = "hdr_recommendation")
    @NotFound(action = NotFoundAction.IGNORE)

    private MeasurementTags tag;
    @Column(name = "label")
    private String label;


}
