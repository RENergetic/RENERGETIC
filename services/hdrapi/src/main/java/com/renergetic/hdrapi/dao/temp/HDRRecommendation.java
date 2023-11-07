package com.renergetic.hdrapi.dao.temp;

import com.renergetic.common.model.details.MeasurementTags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hdr_recommendation", uniqueConstraints =
@UniqueConstraint(columnNames = {"timestamp", "tag_id"}))

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class HDRRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @OneToOne(cascade = CascadeType.REFRESH, mappedBy = "hdr_recommendation")
    @NotFound(action = NotFoundAction.IGNORE)

    private MeasurementTags tag;
    @Column(name = "label")
    private String label;


}
