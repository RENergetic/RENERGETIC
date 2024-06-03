package com.renergetic.common.dao;

import com.renergetic.common.exception.NotFoundException;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.RuleDefinition;
import com.renergetic.common.model.RuleDefinitionMeasurement;
import com.renergetic.common.repository.MeasurementRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class RuleDefinitionDAO {
    private Long id;
    private RuleDefinitionMeasurementDAO measurement1;
    private RuleDefinitionMeasurementDAO measurement2;
    private String manualThreshold;
    private String comparator;

    public RuleDefinition mapToEntity(){
        RuleDefinition entity = new RuleDefinition();
        entity.setId(getId());
        entity.setMeasurement1(getMeasurement1().mapToEntity(false));
        entity.setMeasurement2(getMeasurement2().mapToEntity(false));
        entity.setManualThreshold(getManualThreshold());
        entity.setComparator(getComparator());
        return entity;
    }

    public static RuleDefinitionDAO fromEntity(RuleDefinition entity){
        RuleDefinitionDAO dao = new RuleDefinitionDAO();
        dao.setId(entity.getId());
        dao.setMeasurement1(RuleDefinitionMeasurementDAO.fromEntity(entity.getMeasurement1()));
        dao.setMeasurement2(RuleDefinitionMeasurementDAO.fromEntity(entity.getMeasurement2()));
        dao.setManualThreshold(entity.getManualThreshold());
        dao.setComparator(entity.getComparator());
        return dao;
    }
}
