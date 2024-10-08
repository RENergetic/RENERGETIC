package com.renergetic.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Table(name = "pipeline_parameter")

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class PipelineParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long parameterId;
    //unique key on "parameter_key"  and "experiment_id"
    @Column(name = "parameter_key", nullable = false)
    private String key;
    @Column(name = "parameter_default_value", nullable = true)
    private String defaultValue;
    @Column(name = "parameter_type", nullable = false)
    private String type;
    @Column(name = "parameter_description", nullable = true)
    private String parameterDescription;
    @Column(name = "parameter_visible", nullable = false)
    private Boolean visible = false;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "pipeline_id", nullable = false)
    @JsonIgnore()
    private PipelineDefinition pipelineDefinition;


}
