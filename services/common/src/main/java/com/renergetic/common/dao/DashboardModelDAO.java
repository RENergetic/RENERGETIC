package com.renergetic.common.dao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class DashboardModelDAO {
    String name;
    String label;

    public DashboardModelDAO(String name, String label) {
        this.name = name;
        this.label = label;
    }
}
