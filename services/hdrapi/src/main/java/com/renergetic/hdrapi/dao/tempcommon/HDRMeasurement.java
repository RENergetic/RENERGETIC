//package com.renergetic.hdrapi.dao.tempcommon;
//
//import com.renergetic.common.model.Measurement;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.Setter;
//import lombok.ToString;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "hdr_measurement", uniqueConstraints =
//@UniqueConstraint(columnNames = {"timestamp", "measurement_id"}))
//
//@RequiredArgsConstructor
//@Getter
//@Setter
//@ToString
//public class HDRMeasurement {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @Column(nullable = false)
//    private LocalDateTime timestamp;
//    @ManyToOne(cascade = CascadeType.REFRESH, optional = false)
//    @JoinColumn(name = "measurement_id", nullable = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private Measurement measurement;
//
//
//
//}
