package com.renergetic.ruleevaluationservice.service;

import org.springframework.stereotype.Service;

/*
    This service generates output measurement based on configuration
 */
@Service
public class MultiVectorOptimizerService {
    /*@Autowired
    private DataService dataService;
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private AssetRepository assetRepository;

    @Transactional
    public void process(List<Asset> virtualAssets){
        for(Asset asset : virtualAssets){
            Optional<AssetDetails> assetDetails = asset.getDetails().stream().filter(x -> x.getKey().equals("muveco_config")).findFirst();
            if(assetDetails.isPresent() && true ){{
                //TODO: Deserialize the config.
                MuVeCoConfiguration config = new MuVeCoConfiguration();
                fillOutAssetValues(asset, config);

                //TODO: Overwrite asset connection of the asset.
                createOrUpdateAssetConnection(asset, config.getMvoComponentType());

                aggregateParams(asset, config.getParameterAggregationConfigurations());
                assetRepository.save(asset);

                List<Measurement> existingMeasurements = measurementRepository.findByAsset(asset);
                for (MeasurementAggregation measurementAggregation : config.getMeasurementAggregations()){
                    for(MeasurementAggregationOutput measurementAggregationOutput : measurementAggregation.getOutputs()){
                        generateOutputMeasurementsIfNotExisting(asset, measurementAggregationOutput,
                                measurementAggregation.getMeasurements(), existingMeasurements);
                    }
                }

                existingMeasurements.forEach(measurementRepository::delete);
            }}
        }
    }

    private void fillOutAssetValues(Asset asset, MuVeCoConfiguration config){
        Map<String, String> assetParams = new HashMap<>();
        asset.getDetails().forEach(d -> assetParams.put(d.getKey(), d.getValue()));

        Map<String, ParamaterAggregationConfiguration> params = new HashMap<>();
        //TODO: Only keep global optimizer related details ?
        asset.getConnections().forEach(c -> {
            //TODO: Careful here to not mismatch connected asset and the virtual asset.
            Asset connected = c.getConnectedAsset();
            connected.getDetails().forEach(d -> {
                ParamaterAggregationConfiguration curr;
                if(!params.containsKey(d.getKey())){
                    curr = new ParamaterAggregationConfiguration();
                    curr.setAssetsValues(new HashMap<>());
                    //TODO: Find a way to determine if required.
                    //curr.setRequired();
                    if(assetParams.containsKey(d.getKey()+"_aggregation"))
                        curr.setAggregation(assetParams.get(d.getKey()));
                    params.put(d.getKey(), curr);
                }
                else {
                    curr = params.get(d.getKey());
                }
                curr.getAssetsValues().put(connected.getId(), Double.valueOf(d.getValue()));
            });
        });
    }*/
    /* TODO: Use this later.
    private void aggregateParams(Asset asset, Map<String, ParamaterAggregationConfiguration> config){
        Map<String, AssetDetails> assetDetailsMap = new HashMap<>();
        List<AssetDetails> existingAD = asset.getDetails();
        if(existingAD != null)
            existingAD.forEach(ad -> assetDetailsMap.put(ad.getKey(), ad));

        config.forEach((k, v) -> {
            AssetDetails assetDetails = new AssetDetails();
            assetDetails.setKey(k);
            switch (v.getAggregation().toLowerCase()){
                case "sum":
                    assetDetails.setValue(v.getAssetsValues().values().stream().reduce(0.0, Double::sum).toString());
                    break;
                case "min":
                    assetDetails.setValue(String.valueOf(v.getAssetsValues().values().stream()
                            .mapToDouble(Double::doubleValue).min().orElse(0.0)));
                    break;
                case "max":
                    assetDetails.setValue(String.valueOf(v.getAssetsValues().values().stream()
                            .mapToDouble(Double::doubleValue).max().orElse(0.0)));
                    break;
                case "mean":
                    assetDetails.setValue(String.valueOf(v.getAssetsValues().values().stream()
                            .mapToDouble(Double::doubleValue).average().orElse(0.0)));
                    break;
            }
            assetDetailsMap.put(k, assetDetails);
        });

        asset.setDetails(new ArrayList<>(assetDetailsMap.values()));
    }*/

    /*private void createOrUpdateAssetConnection(Asset asset, MuVeCoType config){
        //TODO later
    }

    private List<Measurement> generateOutputMeasurementsIfNotExisting(Asset asset, MeasurementAggregationOutput measurementAggregationOutput,
                                                         List<Long> measurements, List<Measurement> existingMeasurements){
        List<Measurement> measurementsEntities = measurementRepository.findByIds(measurements);
        if(measurementsEntities.isEmpty())
            return existingMeasurements;
        Measurement ref = measurementsEntities.get(0);

        Measurement measurement = new Measurement();
        measurement.setAsset(asset);
        measurement.setAssetCategory(null);
        measurement.setDirection(ref.getDirection());
        measurement.setDomain(ref.getDomain());
        measurement.setIsland(ref.getIsland());
        //TODO: Define a nice label here.
        measurement.setLabel("TODO: Define a nice label here");
        measurement.setName(measurement.getName());
        measurement.setSensorId(ref.getSensorName());
        measurement.setSensorName(ref.getSensorName());
        measurement.setType(ref.getType());
        measurement.setUuid(ref.getUuid());
        measurement.setDetails(ref.getDetails().stream().map(x ->
                new MeasurementDetails(x.getKey(), x.getValue(), null)).collect(Collectors.toList()));

        //Attempt to recover and cleanup measurement from existing.
        for(Measurement cmp : existingMeasurements){
            //TODO: Careful about null here !
            if(cmp.getDirection().equals(measurement.getDirection())
            && cmp.getDomain().equals(measurement.getDomain())
            && cmp.getIsland().equals(measurement.getIsland())
            && cmp.getName().equals(measurement.getName())
            && cmp.getSensorId().equals(measurement.getSensorId())
            && cmp.getSensorName().equals(measurement.getSensorName())
            && cmp.getType().equals(measurement.getType())){
                Map<String, String> details = new HashMap<>();
                cmp.getDetails().forEach(d -> {
                    details.put(d.getKey(), d.getValue());
                });

                boolean diff = false;
                for(MeasurementDetails md : measurement.getDetails()){
                    if(!details.containsKey(md.getKey()) || !details.get(md.getKey()).equals(md.getValue())){
                        diff = true;
                        break;
                    }
                }

                if(!diff){
                    existingMeasurements.remove(cmp);
                    measurement = cmp;
                    break;
                }
            }
        }

        measurement.getDetails().add(new MeasurementDetails("aggregation_ids",
                measurements.stream().map(String::valueOf).collect(Collectors.joining(",")), measurement.getId()));
        measurement.getDetails().add(new MeasurementDetails("aggregation_type",
                measurementAggregationOutput.getAggregationType(), measurement.getId()));
        measurement.getDetails().add(new MeasurementDetails("time_min",
                measurementAggregationOutput.getTimeMin(), measurement.getId()));
        measurement.getDetails().add(new MeasurementDetails("time_max",
                measurementAggregationOutput.getTimeMax(), measurement.getId()));
        measurement.getDetails().add(new MeasurementDetails("time_range",
                measurementAggregationOutput.getTimeRange(), measurement.getId()));

        measurementRepository.save(measurement);
        return existingMeasurements;
    }*/
}
