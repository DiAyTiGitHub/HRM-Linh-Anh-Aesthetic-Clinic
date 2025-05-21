import React from "react";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import { Grid, makeStyles, CardContent, Card } from "@material-ui/core";
import { Formik, Form } from "formik";
import TotalTimeProjectListUser from "./TotalTimeProjectListUser";
import TotalTimeChartUser from "./TotalTimeChartUser";
import DashboardFilter from './DashboardFilter';

const useStyles = makeStyles((theme) => ({
    root: {
        top: "10px",
    },
    card: {
        background: "#ffffff",
        padding: "1.25rem",
    },
    cardBody: {
        display: "flex",
        minHeight: "1px",
    },
    cardInfo: {
        marginLeft: "auto",
        fontSize: "30px",
    },
    cardDevider: {
        height: "6px",
        borderRadius: "0.25rem",
    },
    cardTotal: {
        height: "100%",
        border: "none",
        borderRadius: "0px",
    },
}));

export default observer(function UserView() {
    const { dashboardStore } = useStore();

    const classes = useStyles();


    const { searObj } = dashboardStore;

    return (
        <Grid container spacing={1}>
            <Grid item md={12} xs={12} className="mb-16">
                <Card className={classes.cardTotal} style={{ boxShadow: "rgb(0 0 0 / 6%) -1px -1px 4px 0px, rgb(0 0 0 / 6%) 4px 4px 4px 2px, rgb(0 0 0 / 6%) 6px 6px 6px 2px" }}>
                    <CardContent>
                        <Grid
                            item
                            md={12}
                            xs={12}
                            className="mb-16"
                            style={{ padding: "16px 0 0 16px" }}
                        >
                            <Formik
                                initialValues={searObj}
                            >
                                {({ isSubmitting, values, setFieldValue }) => (
                                    <Form autoComplete="off">
                                        <DashboardFilter />
                                    </Form>
                                )}
                            </Formik>
                        </Grid>

                        <Grid item md={12} xs={12} className="mb-16">
                            <TotalTimeProjectListUser />
                        </Grid>

                        <Grid item md={12} xs={12} className="mb-16">
                            <TotalTimeChartUser />
                        </Grid>
                    </CardContent>
                </Card>
            </Grid>
        </Grid>

    );
});
