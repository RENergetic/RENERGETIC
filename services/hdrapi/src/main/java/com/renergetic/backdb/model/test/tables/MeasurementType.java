/*
 * This file is generated by jOOQ.
 */
package com.renergetic.backdb.model.test.tables;


import com.renergetic.backdb.model.test.Indexes;
import com.renergetic.backdb.model.test.Public;

import java.util.Arrays;
import java.util.List;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MeasurementType extends TableImpl<Record> {

    private static final long serialVersionUID = 387861553;

    /**
     * The reference instance of <code>public.measurement_type</code>
     */
    public static final MeasurementType MEASUREMENT_TYPE = new MeasurementType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<Record> getRecordType() {
        return Record.class;
    }

    /**
     * The column <code>public.measurement_type.id</code>.
     */
    public static final TableField<Record, Long> ID = createField(DSL.name("id"), org.jooq.impl.SQLDataType.BIGINT.nullable(false), MEASUREMENT_TYPE, "");

    /**
     * The column <code>public.measurement_type.base_unit</code>.
     */
    public static final TableField<Record, String> BASE_UNIT = createField(DSL.name("base_unit"), org.jooq.impl.SQLDataType.VARCHAR(255), MEASUREMENT_TYPE, "");

    /**
     * The column <code>public.measurement_type.color</code>.
     */
    public static final TableField<Record, String> COLOR = createField(DSL.name("color"), org.jooq.impl.SQLDataType.VARCHAR(255), MEASUREMENT_TYPE, "");

    /**
     * The column <code>public.measurement_type.factor</code>.
     */
    public static final TableField<Record, Double> FACTOR = createField(DSL.name("factor"), org.jooq.impl.SQLDataType.DOUBLE, MEASUREMENT_TYPE, "");

    /**
     * The column <code>public.measurement_type.label</code>.
     */
    public static final TableField<Record, String> LABEL = createField(DSL.name("label"), org.jooq.impl.SQLDataType.VARCHAR(255), MEASUREMENT_TYPE, "");

    /**
     * The column <code>public.measurement_type.metric_type</code>.
     */
    public static final TableField<Record, String> METRIC_TYPE = createField(DSL.name("metric_type"), org.jooq.impl.SQLDataType.VARCHAR(255), MEASUREMENT_TYPE, "");

    /**
     * The column <code>public.measurement_type.name</code>.
     */
    public static final TableField<Record, String> NAME = createField(DSL.name("name"), org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), MEASUREMENT_TYPE, "");

    /**
     * The column <code>public.measurement_type.unit</code>.
     */
    public static final TableField<Record, String> UNIT = createField(DSL.name("unit"), org.jooq.impl.SQLDataType.VARCHAR(255), MEASUREMENT_TYPE, "");

    /**
     * No further instances allowed
     */
    private MeasurementType() {
        this(DSL.name("measurement_type"), null);
    }

    private MeasurementType(Name alias, Table<Record> aliased) {
        this(alias, aliased, null);
    }

    private MeasurementType(Name alias, Table<Record> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.MEASUREMENT_TYPE_PKEY, Indexes.UK_2IOYT5KGC8XYN1RC4QDPEFJGG);
    }

    @Override
    public UniqueKey<Record> getPrimaryKey() {
        return Internal.createUniqueKey(com.renergetic.backdb.model.test.tables.MeasurementType.MEASUREMENT_TYPE, "measurement_type_pkey", com.renergetic.backdb.model.test.tables.MeasurementType.ID);
    }

    @Override
    public List<UniqueKey<Record>> getKeys() {
        return Arrays.<UniqueKey<Record>>asList(
              Internal.createUniqueKey(com.renergetic.backdb.model.test.tables.MeasurementType.MEASUREMENT_TYPE, "measurement_type_pkey", com.renergetic.backdb.model.test.tables.MeasurementType.ID)
            , Internal.createUniqueKey(com.renergetic.backdb.model.test.tables.MeasurementType.MEASUREMENT_TYPE, "uk_2ioyt5kgc8xyn1rc4qdpefjgg", com.renergetic.backdb.model.test.tables.MeasurementType.NAME)
        );
    }
}
