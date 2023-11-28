package com.renergetic.common.dao.aggregation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.details.MeasurementDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MuVeCoTypeDAO {
    @JsonProperty(value = "type", required = true)
    private String type;
    @JsonProperty(value = "domainA", required = true)
    private String domainA;
    @JsonProperty(value = "domainB", required = true)
    private String domainB;

    public static MuVeCoTypeDAO create(Measurement measurement){
        MuVeCoTypeDAO muVeCoTypeDAO = new MuVeCoTypeDAO();
        muVeCoTypeDAO.setType(measurement.getDetails().stream().filter(x -> x.getKey().equals("optimizer_type"))
                .findFirst().orElse(new MeasurementDetails()).getValue());
        muVeCoTypeDAO.setType(measurement.getDetails().stream().filter(x -> x.getKey().equals("domain_a"))
                .findFirst().orElse(new MeasurementDetails()).getValue());
        muVeCoTypeDAO.setType(measurement.getDetails().stream().filter(x -> x.getKey().equals("domain_b"))
                .findFirst().orElse(new MeasurementDetails()).getValue());
        return muVeCoTypeDAO;
    }
}
