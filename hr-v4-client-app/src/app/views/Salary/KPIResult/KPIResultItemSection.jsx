import {
    Button,
    ButtonGroup,
    Grid,
    makeStyles
} from "@material-ui/core";
import { Add, Delete } from "@material-ui/icons";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from 'react-i18next';
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";

const useStyles = makeStyles(() => ({
    root: {
        background: "#E4f5fc",
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
    tableContainer: {
        marginTop: "16px",
        "& table": {
            border: "1px solid #ccc",
            borderCollapse: "collapse",
            "& td": {
                border: "1px solid #ccc",
            }
        }
    },
    tableHeader: {
        width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px"
        },
    },
}));

function KPIResultItemSection() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FieldArray name="kpiResultItems">
                    {({ remove }) => (
                        <>
                            <Grid item xs={12}>
                                <Grid container spacing={1}>
                                    <Grid item xs={12} sm={4}>
                                        <strong>Giá trị phần tử KPI</strong>
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item xs={12}>
                                <section className={classes.tableContainer}>
                                    <table className={`w-100 ${classes.table}`}>
                                        <thead>

                                            <tr className={classes.tableHeader}>
                                                <th
                                                    align="center"
                                                    style={{ width: "50%" }}
                                                >
                                                    {t("kpi.kpi-item.name")}
                                                </th>
                                                
                                                <th
                                                    align="center"
                                                    style={{ width: "50%" }}
                                                >
                                                    {t("kpi.kpi-result.kpi-result-item.value")}
                                                </th>
                                            </tr>

                                        </thead>

                                        <tbody>
                                            {values?.kpi?.kpiItems?.length > 0 ? (
                                                values?.kpi?.kpiItems?.map((item, index) => (
                                                    <KpiResultItems
                                                        key={index}
                                                        index={index}
                                                        order={item}
                                                        nameSpace={`kpiResultItems[${index}]`}
                                                        remove={() => remove(index)}
                                                    />
                                                ))
                                            ) : (
                                                <tr className='row-table-body row-table-no_data'>
                                                    <td colSpan={2} align='center' className="py-8">Chưa có phần tử nào</td>
                                                </tr>
                                            )}
                                        </tbody>
                                    </table>
                                </section>
                            </Grid>
                        </>
                    )}
                </FieldArray>
            </Grid>
        </Grid>
    );
}

const KpiResultItems = memo(({ index, nameSpace, order }) => {
    const withNameSpace = (field) => field ? `${nameSpace}.${field}` : nameSpace;

    return (
        <tr className='row-table-body' key={index}>
            <td>
                <span style={{ paddingLeft: 10 }}>{order?.name}</span>
            </td>
            <td style={{ display: "none" }}>
                <GlobitsTextField name={withNameSpace("kpiItem")} value={order} disabled />
            </td>
            <td>
                <GlobitsNumberInput name={withNameSpace("value")} />
            </td>
        </tr>
    );
});

export default memo(observer(KPIResultItemSection));
