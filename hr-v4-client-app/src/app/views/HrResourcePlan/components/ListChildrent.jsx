import { Grid, makeStyles } from "@material-ui/core";
import GlobitsNumberInput from "app/common/form/GlobitsNumberInput";
import { useStore } from "app/stores";
import { getResourceItemById } from "app/views/Position/PositionService";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";

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
            },
        },
    },
    tableHeader: {
        width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px",
        },
    },
}));

function ResourcePlanItemSection() {
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();
    const { hrResourcePlanStore } = useStore();
    const { selectedHrResourcePlan } = hrResourcePlanStore;
    useEffect(() => {
        if (values?.department != null && selectedHrResourcePlan?.id === null) {
            getResourceItemById(values?.department?.id)
                .then(({ data }) => {
                    console.log(data);
                    setFieldValue("resourcePlanItems", data);
                })
                .catch((err) => {
                    console.log(err);
                });
        }
    }, [values?.department]);

    return (
        <Grid container spacing={2}>
            <Grid item xs={12}>
                <FieldArray name='resourcePlanItems'>
                    {({ push, remove }) => (
                        <>
                            <Grid item xs={12}>
                                <Grid container spacing={1}>
                                    <Grid item xs={12} sm={4}>
                                        <strong>Các vị trí định biên trong phòng ban</strong>
                                    </Grid>
                                    {/* <Grid item xs={12}>
                                        <Button
                                            className='btn btn-info d-inline-flex'
                                            startIcon={<AddIcon />}
                                            variant='contained'
                                            onClick={() => push({ ...new HrResourcePlanItemAttribute() })}>
                                            Thêm định biên
                                        </Button>
                                    </Grid> */}
                                </Grid>
                            </Grid>

                            <Grid item xs={12}>
                                <section className={classes.tableContainer}>
                                    <table className={`w-100 ${classes.table}`}>
                                        <thead>
                                            <tr className={classes.tableHeader}>
                                                <th align='center' style={{ width: "40%" }}>
                                                    {t("Chức danh")}
                                                </th>

                                                <th align='center' style={{ width: "30%" }}>
                                                    {t("Số lượng định biên")}
                                                </th>

                                                <th align='center' style={{ width: "30%" }}>
                                                    {t("Số lượng thực tế")}
                                                </th>
                                            </tr>
                                        </thead>

                                        <tbody>
                                            {values?.resourcePlanItems?.length > 0 ? (
                                                values?.resourcePlanItems?.map((item, index) => (
                                                    <ResourcePlanItem
                                                        key={index}
                                                        index={index}
                                                        order={item}
                                                        nameSpace={`resourcePlanItems[${index}]`}
                                                        remove={() => remove(index)}
                                                    />
                                                ))
                                            ) : (
                                                <tr className='row-table-body row-table-no_data'>
                                                    <td colSpan={3} align='center' className='py-8'>
                                                        Chưa có phần tử nào
                                                    </td>
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

const ResourcePlanItem = memo(({ index, nameSpace, order }) => {
    const withNameSpace = (field) => (field ? `${nameSpace}.${field}` : nameSpace);

    return (
        <tr className='row-table-body' key={index}>
            <td style={{ paddingLeft: 10 }}>{order?.positionTitle?.name}</td>
            <td>
                <GlobitsNumberInput name={withNameSpace("planNumber")} />
            </td>
            <td>
                <GlobitsNumberInput name={withNameSpace("currentNumber")} disabled />
            </td>
        </tr>
    );
});

export default memo(observer(ResourcePlanItemSection));
