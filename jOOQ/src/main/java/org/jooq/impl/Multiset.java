/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.impl;

import static java.lang.Boolean.TRUE;
// ...
// ...
// ...
import static org.jooq.SQLDialect.POSTGRES;
import static org.jooq.impl.DSL.jsonArray;
import static org.jooq.impl.DSL.jsonArrayAgg;
import static org.jooq.impl.DSL.jsonEntry;
import static org.jooq.impl.DSL.jsonObject;
import static org.jooq.impl.DSL.jsonbArray;
import static org.jooq.impl.DSL.jsonbArrayAgg;
import static org.jooq.impl.DSL.jsonbObject;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.selectFrom;
import static org.jooq.impl.DSL.xmlagg;
import static org.jooq.impl.DSL.xmlelement;
import static org.jooq.impl.DSL.xmlserializeContent;
import static org.jooq.impl.JSONArrayAgg.patchOracleArrayAggBug;
import static org.jooq.impl.Keywords.K_MULTISET;
import static org.jooq.impl.Names.N_MULTISET;
import static org.jooq.impl.Names.N_RECORD;
import static org.jooq.impl.Names.N_RESULT;
import static org.jooq.impl.SQLDataType.CLOB;
import static org.jooq.impl.SQLDataType.VARCHAR;
import static org.jooq.impl.Tools.emulateMultiset;
import static org.jooq.impl.Tools.fieldName;
import static org.jooq.impl.Tools.map;
import static org.jooq.impl.Tools.visitSubquery;
import static org.jooq.impl.Tools.BooleanDataKey.DATA_MULTISET_CONDITION;

import java.util.Set;

import org.jooq.AggregateFilterStep;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Fields;
import org.jooq.JSON;
import org.jooq.JSONArrayAggOrderByStep;
import org.jooq.JSONArrayAggReturningStep;
import org.jooq.JSONArrayReturningStep;
import org.jooq.JSONB;
import org.jooq.JSONObjectReturningStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Scope;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.XML;
import org.jooq.XMLAggOrderByStep;

/**
 * @author Lukas Eder
 */
final class Multiset<R extends Record> extends AbstractField<Result<R>> {

    static final Set<SQLDialect> NO_SUPPORT_JSON_COMPARE = SQLDialect.supportedBy(POSTGRES);
    static final Set<SQLDialect> NO_SUPPORT_XML_COMPARE  = SQLDialect.supportedBy(POSTGRES);

    final Select<R>              select;

    @SuppressWarnings("unchecked")
    Multiset(Select<R> select) {
        super(N_MULTISET, new MultisetDataType<>((AbstractRow<R>) select.fieldsRow(), select.getRecordType()));

        this.select = select;
    }

    @Override
    public final void accept(Context<?> ctx) {











        if (TRUE.equals(ctx.data(DATA_MULTISET_CONDITION))) {
            ctx.data().remove(DATA_MULTISET_CONDITION);
            accept0(ctx, true);
            ctx.data(DATA_MULTISET_CONDITION, true);
        }
        else
            accept0(ctx, false);
    }

    private final void accept0(Context<?> ctx, boolean multisetCondition) {
        switch (emulateMultiset(ctx.configuration())) {
            case JSON: {
                Table<?> t = select.asTable("t");

                switch (ctx.family()) {









                    default: {
                        JSONArrayAggOrderByStep<JSON> order;
                        JSONArrayAggReturningStep<JSON> returning;

                        returning = order = jsonArrayaggEmulation(ctx, t, multisetCondition);

                        // TODO: Re-apply derived table's ORDER BY clause as aggregate ORDER BY
                        if (multisetCondition)
                            returning = order.orderBy(t.fields());

                        Select<Record1<JSON>> s = patchOracleArrayAggBug(
                            ctx.configuration(),
                            select(DSL.coalesce(
                                returningClob(ctx, returning),
                                returningClob(ctx, jsonArray())
                            )).from(t)
                        );

                        if (multisetCondition && NO_SUPPORT_JSON_COMPARE.contains(ctx.dialect()))
                            ctx.visit(DSL.field(s).cast(VARCHAR));
                        else
                            visitSubquery(ctx, s, true);

                        break;
                    }
                }

                break;
            }

            case JSONB: {
                Table<?> t = select.asTable("t");

                switch (ctx.family()) {









                    default: {
                        JSONArrayAggOrderByStep<JSONB> order;
                        JSONArrayAggReturningStep<JSONB> returning;

                        returning = order = jsonbArrayaggEmulation(ctx, t, multisetCondition);

                        // TODO: Re-apply derived table's ORDER BY clause as aggregate ORDER BY
                        if (multisetCondition)
                            returning = order.orderBy(t.fields());

                        Select<Record1<JSONB>> s = patchOracleArrayAggBug(
                            ctx.configuration(),
                            select(DSL.coalesce(
                                returningClob(ctx, returning),
                                returningClob(ctx, jsonbArray())
                            )).from(t)
                        );

                        visitSubquery(ctx, s, true);
                        break;
                    }
                }

                break;
            }

            case XML: {
                Table<?> t = select.asTable("t");

                switch (ctx.family()) {














                    default: {
                        XMLAggOrderByStep<XML> order;
                        AggregateFilterStep<XML> filter;

                        filter = order = xmlaggEmulation(t, multisetCondition);

                        // TODO: Re-apply derived table's ORDER BY clause as aggregate ORDER BY
                        if (multisetCondition)
                            filter = order.orderBy(t.fields());

                        Select<Record1<XML>> s = select(xmlelement(N_RESULT, filter)).from(t);

                        if (multisetCondition && NO_SUPPORT_XML_COMPARE.contains(ctx.dialect()))
                            ctx.visit(xmlserializeContent(DSL.field(s), VARCHAR));
                        else
                            visitSubquery(ctx, s, true);

                        break;
                    }
                }

                break;
            }

            case NATIVE:
                visitSubquery(ctx.visit(K_MULTISET), select, true);
                break;
        }
    }

    static final <J> Field<J> returningClob(Scope ctx, JSONObjectReturningStep<J> jsonObject) {
        switch (ctx.family()) {





            default:
                return jsonObject;
        }
    }

    static final <J> Field<J> returningClob(Scope ctx, JSONArrayReturningStep<J> jsonArray) {
        switch (ctx.family()) {





            default:
                return jsonArray;
        }
    }

    static final <J> Field<J> returningClob(Scope ctx, JSONArrayAggReturningStep<J> jsonArrayagg) {
        switch (ctx.family()) {





            default:
                return jsonArrayagg;
        }
    }

    static final JSONArrayAggOrderByStep<JSON> jsonArrayaggEmulation(Scope ctx, Fields fields, boolean multisetCondition) {
        return jsonArrayAgg(
            returningClob(ctx, jsonObject(
                map(fields.fields(), (f, i) -> jsonEntry(multisetCondition ? Tools.fieldNameString(i) : f.getName(), f))
            ))
        );
    }

    static final JSONArrayAggOrderByStep<JSONB> jsonbArrayaggEmulation(Scope ctx, Fields fields, boolean multisetCondition) {
        return jsonbArrayAgg(
            returningClob(ctx, jsonbObject(
                map(fields.fields(), (f, i) -> jsonEntry(multisetCondition ? Tools.fieldNameString(i) : f.getName(), f))
            ))
        );
    }

    static final XMLAggOrderByStep<XML> xmlaggEmulation(Fields fields, boolean multisetCondition) {
        return xmlagg(xmlelement(N_RECORD, map(fields.fields(), (f, i) -> xmlelement(multisetCondition ? fieldName(i) : f.getUnqualifiedName(), f))));
    }

}