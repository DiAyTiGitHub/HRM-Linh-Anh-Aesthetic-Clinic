import React, { memo } from "react";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import { Grid, makeStyles, CardContent, Card, Collapse, Button } from "@material-ui/core";
import { Formik, Form } from "formik";
import TotalTimeProjectList from "./TotalTimeProjectList";
import TotalTimeChartAdmin from "./TotalTimeChartAdmin";
import TotalTimeList from "./TotalTimeList";
import DashboardFilter from './DashboardFilter';
import ExpandLessIcon from "@material-ui/icons/ExpandLess";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import StaffBirthDayByMonthFilter from "./StaffBirthDayByMonth/StaffBirthDayByMonthFilter";
import StaffBirthDayByMonthList from "./StaffBirthDayByMonth/StaffBirthDayByMonthList";

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
    dashboardFilter: {
        padding: "16px 0 0 16px",
        [theme.breakpoints.down("xs")]: {
            paddingLeft: "unset",
        }
    },
    maxHeightCard: {
        maxHeight: "58px"
    }
}));

function AdminView() {
    const { dashboardStore } = useStore();
    const [visible, setVisible] = React.useState(true);
    const classes = useStyles();

    const { searObj } = dashboardStore;

    return (
        <Grid container spacing={2}>
            <Grid item xs={12} sm={8} lg={9}>
                <Card className={`${classes.cardTotal}`}
                    style={{ boxShadow: "rgb(0 0 0 / 6%) -1px -1px 4px 0px, rgb(0 0 0 / 6%) 4px 4px 4px 2px, rgb(0 0 0 / 6%) 6px 6px 6px 2px" }}
                >
                    <Grid container spacing={2} className="p-8" style={{ borderBottom: "1px solid #e1f2f8" }}>
                        <Grid item lg={3} sm={4} xs={12}
                            className="flex align-center"
                        >
                            <span
                                className="m-0 font-size-16 "
                                style={{ color: "#478fcc !important" }}
                            >
                                Thống kê theo công việc
                            </span>
                        </Grid>
                        <Grid item lg={9} sm={8} xs={12}>
                            <Formik initialValues={searObj} >
                                <Form autoComplete="off">
                                    <DashboardFilter />
                                </Form>
                            </Formik>
                        </Grid>
                        {/* <Grid item lg={1} sm={1} xs={12} className="self-end text-end">
                            <Button
                                onClick={() => setVisible(!visible)}
                                className="rounded-0"
                                style={{ borderLeft: "1px solid rgba(0, 0, 0, 0.1)" }}
                            >
                                {visible === true ? <ExpandLessIcon fontSize="large" /> : <ExpandMoreIcon fontSize="large" />}
                            </Button>
                        </Grid> */}
                    </Grid>

                    <CardContent
                        className="p-8"
                        component={Collapse}
                        in={visible}
                    >
                        <div className="py-8">
                            <TotalTimeList />
                        </div>
                    </CardContent>
                </Card>
            </Grid>

            <Grid item xs={12} sm={4} lg={3}>
                <Card className={`${classes.cardTotal}`}
                    style={{ boxShadow: "rgb(0 0 0 / 6%) -1px -1px 4px 0px, rgb(0 0 0 / 6%) 4px 4px 4px 2px, rgb(0 0 0 / 6%) 6px 6px 6px 2px" }}
                >
                    <Grid container spacing={2} className="px-8" style={{ borderBottom: "1px solid #e1f2f8" }}>
                        <Grid item sm={6} xs={12} className="flex align-center">
                            <span className="m-0 font-size-16 " style={{ color: "#478fcc !important" }}>
                                Có sinh nhật trong
                            </span>
                        </Grid>
                        <Grid item sm={6} xs={12}>
                            <StaffBirthDayByMonthFilter />
                        </Grid>
                    </Grid>

                    <CardContent
                        className="p-8"
                        component={Collapse}
                        in={visible}
                    >
                        <div className={`${classes.maxHeightCard} py-8`}>
                            <StaffBirthDayByMonthList />
                        </div>
                    </CardContent>
                </Card>
            </Grid>

            <Grid item xs={12}>
                <Card className={`${classes.cardTotal}`} style={{ boxShadow: "rgb(0 0 0 / 6%) -1px -1px 4px 0px, rgb(0 0 0 / 6%) 4px 4px 4px 2px, rgb(0 0 0 / 6%) 6px 6px 6px 2px" }}>
                    <CardContent>
                        <TotalTimeChartAdmin />
                    </CardContent>
                </Card>
            </Grid>

            <Grid item xs={12}>
                <Card className={`${classes.cardTotal}`} style={{ boxShadow: "rgb(0 0 0 / 6%) -1px -1px 4px 0px, rgb(0 0 0 / 6%) 4px 4px 4px 2px, rgb(0 0 0 / 6%) 6px 6px 6px 2px" }}>
                    <CardContent>
                        <TotalTimeProjectList />
                    </CardContent>
                </Card>
            </Grid>
        </Grid>
    );
}

export default memo(observer(AdminView));
