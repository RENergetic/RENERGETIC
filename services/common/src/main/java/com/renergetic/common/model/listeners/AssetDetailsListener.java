package com.renergetic.common.model.listeners;

import com.renergetic.common.model.Asset;
import com.renergetic.common.model.AssetConnection;
import com.renergetic.common.model.ConnectionType;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.common.repository.AssetConnectionRepository;
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
import javax.persistence.PostUpdate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AssetDetailsListener {
    private final AssetDetailsRepository assetDetailsRepository;
    private final AssetRepository assetRepository;
    private final AssetConnectionRepository assetConnectionRepository;

    @Autowired
    public AssetDetailsListener(@Lazy AssetDetailsRepository assetDetailsRepository,
                                @Lazy AssetRepository assetRepository,
                                @Lazy AssetConnectionRepository assetConnectionRepository){
        this.assetDetailsRepository = assetDetailsRepository;
        this.assetRepository = assetRepository;
        this.assetConnectionRepository = assetConnectionRepository;
    }

    @PostRemove
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAggregatedParamsAfterDelete(AssetDetails assetDetails) {
        assetDetails.setValue(null);
        updateAggregatedParams(assetDetails, true);
    }

    @PostPersist
    @PostUpdate
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateAggregatedParamsAfterUpdate(AssetDetails assetDetails){
        updateAggregatedParams(assetDetails, false);
    }

    public void updateAggregatedParams(AssetDetails assetDetails, boolean deleting){
        if(assetDetails.getAsset() == null)
            return;

        //Populating elements if missing.
        if(assetDetails.getAsset().getConnections() == null)
            assetDetails.setAsset(assetRepository.findById(assetDetails.getAsset().getId()).orElseThrow());

        if(assetDetails.getAsset().getType().getName().equals("pv_virtual_asset_group")) {
            if(!assetDetails.getKey().endsWith("_aggregation"))
                return;

            if(deleting) {
                //TODO: Maybe delete the non "_aggregation" too ?
                AssetDetails toDelete = assetDetails.getAsset().getDetails().stream()
                        .filter(x -> x.getKey().equals(assetDetails.getKey().split("_aggregation")[0])).findFirst().orElse(null);
                if(toDelete != null)
                    assetDetailsRepository.delete(toDelete);
            } else {
                AssetDetails toSave = aggregateDetailsForGivenKey(assetDetails.getAsset(),
                        assetDetails.getAsset().getConnections().stream().map(ac -> ac.getConnectedAsset().getDetails())
                                .flatMap(List::stream).distinct().collect(Collectors.toList()), assetDetails,
                        assetDetails.getKey().split("_aggregation")[0]);
                if(toSave != null)
                    assetDetailsRepository.save(toSave);
            }

        } else {
            List<Asset> connectedAggr = !assetDetails.getAsset().getConnections().isEmpty() ? assetDetails.getAsset().getConnections().stream()
                    .filter(x -> x.getConnectionType().equals(ConnectionType.va_grouping) &&
                            x.getAsset().getType().getName().equals("pv_virtual_asset_group"))
                    .map(AssetConnection::getAsset).collect(Collectors.toList()) :
                    assetConnectionRepository.findByConnectedAssetIdAndConnectionType(assetDetails.getAsset().getId(),
                            ConnectionType.va_grouping).stream().filter(x -> x.getConnectionType().equals(ConnectionType.va_grouping))
                            .map(AssetConnection::getAsset).collect(Collectors.toList());

            if (!connectedAggr.isEmpty()) {
                assetDetailsRepository.saveAll(connectedAggr.stream().map(ca -> {
                    List<AssetDetails> connectedDetails = ca.getConnections().stream()
                            .map(ac -> ac.getConnectedAsset().getDetails())
                            .flatMap(List::stream).distinct().collect(Collectors.toList());

                    AssetDetails ad = connectedDetails.stream()
                            .filter(x -> x.getKey().equals(assetDetails.getKey()) &&
                                    x.getAsset().getId().equals(assetDetails.getAsset().getId()))
                                    .findFirst().orElse(null);

                    if(ad != null){
                        connectedDetails.remove(ad);
                            if(!deleting)
                                connectedDetails.add(assetDetails);
                    }
                    else
                        connectedDetails.add(assetDetails);

                    return aggregateDetailsForGivenKey(ca, connectedDetails,
                            ca.getDetails().stream().filter(d -> d.getKey().equals(assetDetails.getKey() + "_aggregation"))
                                    .findFirst().orElse(null), assetDetails.getKey());
                }).filter(Objects::nonNull).collect(Collectors.toList()));
            }
        }
    }

    private AssetDetails aggregateDetailsForGivenKey(Asset asset, List<AssetDetails> connectedDetails,
                                                     AssetDetails definition, String key) {
        if(definition != null){
            AssetDetails aggrDetail = asset.getDetails().stream()
                    .filter(x -> x.getKey().equals(key)).findFirst().orElse(new AssetDetails());
            if(aggrDetail.getKey() == null) {
                aggrDetail.setKey(key);
                aggrDetail.setAsset(asset);
                //ca.getDetails().add(aggrDetail);
            }
            aggrDetail.setValue(AssetDetailsAggregation.aggregate(definition.getValue().toLowerCase(), key, connectedDetails));
            return aggrDetail;
        }
        return null;
    }
}
