package com.renergetic.kpiapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@Tag(name = "KPI Controller", description = "Allows see the KPIs timeseries and group it")
@RequestMapping("/api/kpi")
public class KPIController {
}
