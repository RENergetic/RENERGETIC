package com.renergetic.hdrapi.service;

import com.renergetic.hdrapi.dao.DataDAO;
import com.renergetic.hdrapi.dao.DataWrapperDAO;
import com.renergetic.hdrapi.exception.NotFoundException;
import com.renergetic.hdrapi.model.InformationPanel;
import com.renergetic.hdrapi.model.InformationTile;
import com.renergetic.hdrapi.model.Measurement;
import com.renergetic.hdrapi.repository.InformationPanelRepository;
import com.renergetic.hdrapi.repository.InformationTileMeasurementRepository;
import com.renergetic.hdrapi.repository.InformationTileRepository;
import com.renergetic.hdrapi.repository.MeasurementRepository;
import com.renergetic.hdrapi.service.utils.DummyDataGenerator;
import com.renergetic.hdrapi.service.utils.HttpAPIs;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DataService {
    @Value("${influx.api.url}")
    String influxURL;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    InformationPanelRepository panelRepository;
    @Autowired
    InformationTileRepository tileRepository;
    @Autowired
    MeasurementRepository measurementRepository;

    @Autowired
    InformationTileMeasurementRepository tileMeasurementRepository;

    public DataDAO getByUserId(Long userId, Map<String, String> params) {
        List<Measurement> measurements = measurementRepository.findByUserId(userId);

        DataDAO ret = new DataDAO();
        measurements.forEach(measurement -> {

            params.put("field", measurement.getType().getName());
            HttpResponse<String> responseLast = HttpAPIs.sendRequest(
                    String.format(influxURL + "/api/measurement/%s/last", measurement.getName()),
                    "GET", params, null, null);

            HttpResponse<String> responseMax = HttpAPIs.sendRequest(
                    String.format(influxURL + "/api/measurement/%s/max", measurement.getName()),
                    "GET", params, null, null);

            if (responseLast != null && responseLast.statusCode() < 300) {
                JSONArray array = new JSONArray(responseLast.body());
                if (array.length() > 0)
                    ret.getCurrent().getLast().put(measurement.getId().toString(),
                            Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("last")));
            } else System.err.println(responseLast == null ? "URL not found" : responseLast.body());
            if (responseMax != null && responseMax.statusCode() < 300) {
                JSONArray array = new JSONArray(responseMax.body());
                if (array.length() > 0)
                    ret.getCurrent().getMax().put(measurement.getId().toString(),
                            Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("max")));
            } else System.err.println(responseMax == null ? "URL not found" : responseMax.body());
        });
        return ret;
    }

    //TODO: move panelservice?
    private List<Measurement> getPanelMeasurements(long id) {
        InformationPanel panel = panelRepository.findById(id).orElse(null);

        if (panel != null) {
            List<Measurement> measurements = new ArrayList<>();
            for (InformationTile tile : panel.getTiles()) {
                measurements.addAll(
                        tileMeasurementRepository.findByInformationTileId(tile.getId()).stream().map(obj -> {
                            if (obj.getMeasurement() != null)
                                return obj.getMeasurement();
                            else {
                                Measurement ret = new Measurement();
                                ret.setName(obj.getMeasurementName());
                                ret.setType(obj.getType());
                                ret.setSensorName(obj.getSensorName());
                                ret.setDomain(obj.getDomain());
                                ret.setDirection(obj.getDirection());

                                return ret;
                            }
                        }).collect(Collectors.toList()));
            }
            return measurements;
        }
        throw new NotFoundException("No panel found related with id " + id);

    }

    public DataDAO getByPanel(Long id, Map<String, String> params) {
//        InformationPanel panel = panelRepository.findById(id).orElse(null);
        List<Measurement> measurements = this.getPanelMeasurements(id);
        // GET MEASUREMENTS RELATED TO THE PANEL

        DataDAO ret = new DataDAO();
        for (Measurement measurement : measurements) {
            params.put("field", measurement.getType().getName());
            // Execute request to Measurement API to get max and last values
            HttpResponse<String> responseLast = HttpAPIs.sendRequest(
                    String.format(influxURL + "/api/measurement/%s/last", measurement.getName()), "GET", params,
                    null, null);
            HttpResponse<String> responseMax = HttpAPIs.sendRequest(
                    String.format(influxURL + "/api/measurement/%s/max", measurement.getName()), "GET", params,
                    null, null);
            // If request are successfully executed format data
            if (responseLast.statusCode() < 300) {
                JSONArray array = new JSONArray(responseLast.body());
                if (array.length() > 0)
                    ret.getCurrent().getLast().put(measurement.getId().toString(),
                            Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("last")));
            }
            if (responseMax.statusCode() < 300) {
                JSONArray array = new JSONArray(responseMax.body());
                if (array.length() > 0)
                    ret.getCurrent().getMax().put(measurement.getId().toString(),
                            Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("max")));
            }
        }
        return ret;
    }

    public DataDAO getByTile(Long id, Map<String, String> params) {
        InformationTile tile = tileRepository.findById(id).orElse(null);
        List<Measurement> measurements = new ArrayList<>();

        // GET MEASUREMENTS RELATED TO THE PANEL
        if (tile != null) {
            measurements.addAll(tileMeasurementRepository.findByInformationTileId(tile.getId()).stream().map(obj -> {
                if (obj.getMeasurement() != null)
                    return obj.getMeasurement();
                else {
                    Measurement ret = new Measurement();
                    ret.setName(obj.getMeasurementName());
                    ret.setType(obj.getType());
                    ret.setSensorName(obj.getSensorName());
                    ret.setDomain(obj.getDomain());
                    ret.setDirection(obj.getDirection());

                    return ret;
                }
            }).collect(Collectors.toList()));
            DataDAO ret = new DataDAO();
            for (Measurement measurement : measurements) {
                params.put("field", measurement.getType().getName());
                // Execute request to Measurement API to get max and last values
                HttpResponse<String> responseLast = HttpAPIs.sendRequest(
                        String.format(influxURL + "/api/measurement/%s/last", measurement.getName()), "GET", params,
                        null, null);
                HttpResponse<String> responseMax = HttpAPIs.sendRequest(
                        String.format(influxURL + "/api/measurement/%s/max", measurement.getName()), "GET", params,
                        null, null);
                // If request are successfully executed format data
                if (responseLast.statusCode() < 300) {
                    JSONArray array = new JSONArray(responseLast.body());
                    if (array.length() > 0)
                        ret.getCurrent().getLast().put(measurement.getId().toString(),
                                Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("last")));
                }
                if (responseMax.statusCode() < 300) {
                    JSONArray array = new JSONArray(responseMax.body());
                    if (array.length() > 0)
                        ret.getCurrent().getMax().put(measurement.getId().toString(),
                                Double.parseDouble(array.getJSONObject(0).getJSONObject("fields").getString("max")));
                }
            }
            return ret;
        } else throw new NotFoundException("No tile found related with id " + id);
    }
    public DataWrapperDAO getPanelData(Long id,  Long from, Optional<Long> to) {
        return this.getPanelData(id,null,from,to );
    }

    public DataWrapperDAO getPanelData(Long id, Long assetId, Long from, Optional<Long> to) {
        //TODO: get measurements for assetId
        List<Measurement> measurements = this.getPanelMeasurements(id);

        // GET MEASUREMENTS RELATED TO THE PANEL

//TODO: build influxDB query
        DataDAO res = DummyDataGenerator.getData(measurements);
        return new DataWrapperDAO(res);

    }
}
