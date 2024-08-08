package com.renergetic.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;


@Entity
@Table(name = "pipeline_definition_property", uniqueConstraints = @UniqueConstraint(columnNames = {"pipeline_id", "property_key"}))
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class PipelineDefinitionProperty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    //unique key on "parameter_key"  and "experiment_id"
    @Column(name = "property_key", nullable = false)
    private String key;
    @Column(name = "property_type", nullable = false)
    private String type;
    @Column(name = "property_description", nullable = true)
    private String description;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "pipeline_id", nullable = false)
    @JsonIgnore()
    private PipelineDefinition pipelineDefinition;


}
