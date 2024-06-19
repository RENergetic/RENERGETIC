package com.renergetic.common.model.listeners;

import com.renergetic.common.model.AssetConnection;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.information.AssetDetailsRepository;
import com.renergetic.common.utilities.AssetDetailsAggregation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssetConnectionListener {
    private final AssetDetailsRepository assetDetailsRepository;
    private final AssetRepository assetRepository;

    @Autowired
    public AssetConnectionListener(@Lazy AssetDetailsRepository assetDetailsRepository,
                                   @Lazy AssetRepository assetRepository){
        this.assetDetailsRepository = assetDetailsRepository;
        this.assetRepository = assetRepository;
    }


    @PostRemove
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAggregatedParamsAfterDelete(AssetConnection assetConnection) {
        try {
            updateAggregatedParams(assetConnection, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostPersist
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAggregatedParamsAfterUpdate(AssetConnection assetConnection){
        try {
            updateAggregatedParams(assetConnection, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAggregatedParams(AssetConnection assetConnection, boolean deleting){
        if(assetConnection.getAsset() == null)
            return;

        //Populating elements if missing.
        if(assetConnection.getAsset().getConnections() == null)
            assetConnection.setAsset(assetRepository.findById(assetConnection.getAsset().getId()).orElseThrow());
        if(assetConnection.getConnectedAsset().getConnections() == null)
            assetConnection.setConnectedAsset(assetRepository.findById(assetConnection.getConnectedAsset().getId()).orElseThrow());

        if(assetConnection.getAsset().getType().getName().equals("pv_virtual_asset_group")) {
            List<AssetDetails> existingDef = assetConnection.getAsset().getDetails().stream()
                    .filter(x -> x.getKey().endsWith("_aggregation")).collect(Collectors.toList());
            List<AssetDetails> connectedDetails = assetConnection.getAsset().getConnections() == null ||
                    assetConnection.getAsset().getConnections().isEmpty() ?
                    assetRepository.findById(assetConnection.getAsset().getId()).orElseThrow().getConnections().stream()
                            .filter(x -> !x.getAsset().getId().equals(assetConnection.getAsset().getId()) ||
                                    !x.getConnectedAsset().getId().equals(assetConnection.getConnectedAsset().getId()) ||
                                    !x.getConnectionType().equals(assetConnection.getConnectionType()))
                            .map(ac -> ac.getConnectedAsset().getDetails())
                            .flatMap(List::stream).distinct().collect(Collectors.toList())
                    :
                    assetConnection.getAsset().getConnections().stream()
                            .filter(x -> !x.getAsset().getId().equals(assetConnection.getAsset().getId()) ||
                                    !x.getConnectedAsset().getId().equals(assetConnection.getConnectedAsset().getId()) ||
                                    !x.getConnectionType().equals(assetConnection.getConnectionType()))
                            .map(ac -> ac.getConnectedAsset().getDetails())
                            .flatMap(List::stream).distinct().collect(Collectors.toList());

            if(!deleting)
                connectedDetails.addAll(assetConnection.getConnectedAsset().getDetails());

            List<AssetDetails> detailsToSave = new ArrayList<>();
            existingDef.forEach(def -> {
                String argument = def.getKey().split("_aggregation")[0];
                AssetDetails assetDetails = assetConnection.getAsset().getDetails().stream()
                        .filter(x -> x.getKey().equals(argument)).findFirst().orElse(new AssetDetails());
                if(assetDetails.getKey() == null) {
                    assetDetails.setKey(argument);
                    assetDetails.setAsset(assetConnection.getAsset());
                    //assetConnection.getAsset().getDetails().add(assetDetails);
                }
                assetDetails.setValue(AssetDetailsAggregation.aggregate(def.getValue().toLowerCase(), argument, connectedDetails));
                detailsToSave.add(assetDetails);
            });
            assetDetailsRepository.saveAll(detailsToSave);
        }
    }
}
