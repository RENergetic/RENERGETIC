package com.renergetic.common.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.renergetic.common.model.DashboardUnit;
import com.renergetic.common.model.MeasurementType;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

@Getter
@Setter

public class DashboardMetaKeys {

//    private static final DashboardMetaKeys instance;

    public static final class DashboardUnitSerializer extends JsonSerializer<List<DashboardUnit>> {


        @Override
        public void serialize(List<DashboardUnit> dul, JsonGenerator jgen,
                              SerializerProvider serializerProvider) throws IOException {
            jgen.writeStartArray();
            for (DashboardUnit du : dul) {
                jgen.writeStartObject();
                jgen.writeStringField("name", du.NAME);
                jgen.writeStringField("symbol", du.SYMBOL);
                jgen.writeStringField("type", du.TYPE);
                jgen.writeStringField("physical_type", du.PHYSICAL_TYPE);
                jgen.writeEndObject();
            }
            jgen.writeEndArray();
        }

    }

//    public static final class MeasurementTypeSerializer extends JsonSerializer<List<MeasurementType>> {
//
//
//        @Override
//        public void serialize(List<MeasurementType> mtl, JsonGenerator jgen,
//                              SerializerProvider serializerProvider) throws IOException {
//            jgen.writeStartArray();
//            for (MeasurementType mt : mtl) {
//                jgen.writeStartObject();
//                jgen.writeStringField("name", mt.);
//                jgen.writeStringField("symbol", du.SYMBOL);
//                jgen.writeStringField("type", du.TYPE);
//                jgen.writeStringField("physical_type", du.PHYSICAL_TYPE);
//                jgen.writeEndObject();
//            }
//            jgen.writeEndArray();
//        }
//
//    }

    //TODO: make it configurable - load those from file or db
    @JsonProperty(required = false, value = "models")
    private List<DashboardModelDAO> models = List.of(
            new DashboardModelDAO("model_1", "Model 1"), new DashboardModelDAO("model_2", "Model 2"),
            new DashboardModelDAO("model_4", "Model 4"), new DashboardModelDAO("model_3", "Model 3")
    );
//    @JsonProperty(required = false, value = "units2")
//    @JsonSerialize(using = DashboardMetaKeys.DashboardUnitSerializer2.class)
//    private List<DashboardUnit> units2 = Arrays.asList(DashboardUnit.values());
@JsonProperty(required = false, value = "units")
//    @JsonSerialize(using = DashboardMetaKeys.DashboardUnitSerializer.class)
private List<MeasurementType> units;
@JsonProperty(required = false, value = "measurement_types")
//    @JsonSerialize(using = DashboardMetaKeys.DashboardUnitSerializer.class)
    private List<MeasurementType> measurementTypes;


//    static {
//        instance = new DashboardMetaKeys();
//    }


    public DashboardMetaKeys(List<MeasurementType> units) {
        this.units = units;
        this.measurementTypes=units;
        //TODO: write some initialization here
    }

//    public static DashboardMetaKeys getInstance() {
//        return new DashboardMetaKeys();
//    }
}
