package com.renergetic.ruleevaluationservice.service;

import com.renergetic.common.model.Asset;
import com.renergetic.common.model.details.AssetDetails;
import com.renergetic.common.repository.AssetRepository;
import com.renergetic.common.repository.MeasurementRepository;
import com.renergetic.ruleevaluationservice.dao.muveco.MuVeCoConfigDAO;
import com.renergetic.ruleevaluationservice.dao.muveco.MuVeCoParameterConfigDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/*
    This service generates output measurement based on configuration
 */
@Service
public class MultiVectorOptimizerService {
    @Autowired
    private DataService dataService;
    @Autowired
    private MeasurementRepository measurementRepository;
    @Autowired
    private AssetRepository assetRepository;

    public void process(List<Asset> virtualAssets){
        for(Asset asset : virtualAssets){
            Optional<AssetDetails> assetDetails = asset.getDetails().stream().filter(x -> x.getKey().equals("muveco_config")).findFirst();
            if(assetDetails.isPresent() && true/*TODO: Deserialize*/ ){{
                MuVeCoConfigDAO muVeCoConfig = new MuVeCoConfigDAO();
                aggregateParams(asset, muVeCoConfig.getParameterConfig());
            }}
        }
    }

    private void aggregateParams(Asset asset, List<MuVeCoParameterConfigDAO> config){
        //TODO later
    }

    private void generateOutputMeasurementsIfNotExisting(Asset asset){
        /*
            1. Iterate outputs
            2. Generate a local measurement
            3. Generate a local measurement detail (with list of measurement ids to aggregate + aggregate function, time, ...)
            4. Check if such measurement + details exists (or maybe just name and update ?)
            5. If not, save it
         */
    }

    public void test(MuVeCoConfigDAO muVeCoConfig){
        /*
            TODO:
                1. For each config group
                    2. Query all the measurements
                    3. Get the data
                    4. Store the data in influx ?
                    5. Return the data if call manually (web)
         */

        List<MuVeCoParameterConfigDAO> params = muVeCoConfig.getParameterConfig();


        /*for(AggregationConfigDAO config : configs){
            for(AggregationOutputConfigDAO output : config.getOutputs()){
                dataService.getData(measurementRepository.findByIds(config.getMeasurements()),
                        output.getAggregationType(),
                        output.getTimeRange(),
                        output.getTimeMin(),
                        output.getTimeMax());
            }
        }*/
    }
}
