package com.renergetic.hdrapi.dao.temp;

import com.renergetic.common.model.details.MeasurementTags;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @ManyToOne(cascade = CascadeType.REFRESH, optional = false)
    @JoinColumn(name = "tag_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MeasurementTagss tag;
    @Column(name = "label")
    private String label;

}
