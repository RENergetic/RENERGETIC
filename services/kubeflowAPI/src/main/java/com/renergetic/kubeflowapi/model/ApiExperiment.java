//package com.renergetic.kubeflowapi.model;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.OneToOne;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "experiment")
//public class ApiExperiment {
//
//    @Id
//    @Column(name = "experiment_id")
//    String experiment_id;
//
//    @Column(name = "name")
//    String name;
//
//    @Column(name = "description")
//    String description;
//
//    @Column(name = "created_at")
//    String createdAt;
//
//    @OneToOne
//    @JoinColumn(name = "key_id", nullable = false )
//    ApiResourceReference resource_reference;
//
//    @Column(name = "storage_state")
//    String storage_state;
//}
