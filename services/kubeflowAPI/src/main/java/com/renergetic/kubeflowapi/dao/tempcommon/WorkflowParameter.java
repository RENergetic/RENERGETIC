package com.renergetic.kubeflowapi.dao.tempcommon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.renergetic.common.model.Measurement;
import javax.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Table(name = "workflow_property")

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class WorkflowParameter {
    @Id
    @Column(name = "parameter_id")
    private String parameterId;

    @Column(name = "parameter_key",nullable = false)
    private String key;
    @Column(name = "parameter_default_value",nullable = true)
    private String defaultValue;
    @Column(name = "parameter_type",nullable = false)
    private String type;
    @Column(name = "parameter_description",nullable = true)
    private String parameterDescription;


    @ManyToOne(cascade = CascadeType.REFRESH)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "experiment_id", nullable = false)
    @JsonIgnore()
    private WorkflowDefinition workflowDefinition;


}
