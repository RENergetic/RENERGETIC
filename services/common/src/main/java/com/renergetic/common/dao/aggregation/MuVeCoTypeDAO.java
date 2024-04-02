package com.renergetic.common.dao.aggregation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.renergetic.common.model.Asset;
import com.renergetic.common.model.Measurement;
import com.renergetic.common.model.OptimizerType;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.common.model.details.MeasurementDetails;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MuVeCoTypeDAO {
    @JsonProperty(value = "type", required = true)
    private String type;
    @JsonProperty(value = "domainA", required = true)
    private Long domainA;
    @JsonProperty(value = "domainB", required = true)
    private Long domainB;
    @JsonProperty(required = false)
    private Integer domainsQuantity;
    @JsonProperty(value = "domainAReadableConnection", required = true)
    private String domainAReadableConnection;
    @JsonProperty(value = "domainBReadableConnection", required = true)
    private String domainBReadableConnection;

    public static MuVeCoTypeDAO create(Asset asset, OptimizerType optimizerType){
        MuVeCoTypeDAO muVeCoTypeDAO = new MuVeCoTypeDAO();

        if(asset != null){
            muVeCoTypeDAO.setType(asset.getDetails().stream().filter(x -> x.getKey().equals("optimizer_type"))
                    .findFirst().orElse(new AssetDetails()).getValue());

            try {
                muVeCoTypeDAO.setDomainA(Long.valueOf(asset.getDetails().stream().filter(x -> x.getKey().equals("domain_a"))
                        .findFirst().orElse(new AssetDetails()).getValue()));
            } catch (Exception exception){
                exception.printStackTrace();
                muVeCoTypeDAO.setDomainA(null);
            }

            try{
                muVeCoTypeDAO.setDomainB(Long.valueOf(asset.getDetails().stream().filter(x -> x.getKey().equals("domain_b"))
                        .findFirst().orElse(new AssetDetails()).getValue()));
            } catch (Exception exception){
                exception.printStackTrace();
                muVeCoTypeDAO.setDomainB(null);
            }
        }

        if(optimizerType != null){
            muVeCoTypeDAO.domainsQuantity = optimizerType.getDomainsQuantity();
            muVeCoTypeDAO.domainAReadableConnection = optimizerType.getConnectionTypeAReadable();
            muVeCoTypeDAO.domainBReadableConnection = optimizerType.getConnectionTypeBReadable();
        }

        return muVeCoTypeDAO;
    }
}
