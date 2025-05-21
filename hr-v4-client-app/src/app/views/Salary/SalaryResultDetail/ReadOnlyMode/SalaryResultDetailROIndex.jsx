import React, { useEffect, memo } from "react";
import { Grid, Button, makeStyles, ButtonGroup } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import SaveOutlinedIcon from '@material-ui/icons/SaveOutlined';
import ArrowBackIcon from '@material-ui/icons/ArrowBack';
import { useHistory } from "react-router-dom";
import { useParams, useLocation } from "react-router-dom/cjs/react-router-dom";
import ConstantList from "app/appConfig";
import RateReviewIcon from '@material-ui/icons/RateReview';
import SalaryBoardViewMode from "./SalaryBoardViewMode";
import SalaryStaffPayslipForm from "../../SalaryStaffPayslip/SalaryStaffPayslipForm";
import SalaryRecalPayslipPopup from "../../SalaryStaffPayslip/SalaryRecalPayslip/SalaryRecalPayslipPopup";
// import SalaryStaffPaySlipPopup from "./SalaryStaffPaySlipPopup";

const useStyles = makeStyles((theme) => ({
    root: {
        "& .MuiAccordion-rounded": {
            borderRadius: "5px",
        },

        "& .MuiPaper-root": {
            borderRadius: "5px",
        },

        "& .MuiAccordionSummary-root": {
            borderRadius: "5px",
            // backgroundColor: "#EBF3F9",
            color: "#5899d1 ",
            fontWeight: "400",

            "& .MuiTypography-root": {
                fontSize: "1rem",
            },
        },

        "& .Mui-expanded": {
            "& .MuiAccordionSummary-root": {
                backgroundColor: "#EBF3F9",
                color: "#5899d1 ",
                // borderLeft: "3px solid rgba(88, 153, 202, 0.84) !important",
                fontWeight: "700",
                maxHeight: "50px !important",
                minHeight: "50px !important",
            },
            "& .MuiTypography-root": {
                fontWeight: 700,
            },
        },

        "& .MuiButton-root": {
            borderRadius: "0.125rem !important",
        },
    },
}));

function SalaryResultDetailROIndex() {
    const location = useLocation();
    const queryParams = new URLSearchParams(location?.search); // Parse the query string
    const { id: salaryResultId } = useParams();
    const { t } = useTranslation();

    const {
        salaryResultStore,
        salaryResultDetailStore,
        salaryStaffPayslipStore
    } = useStore();

    const {
        resetStore,
        getSalaryResultBoard,
        onViewSalaryResult,
        setTabCU,
        tabCU,
        onViewResultStaff
    } = salaryResultDetailStore;

    const {
        openCreateEditPopup,
        openPopupSalary,
        openRecalculatePayslip
    } = salaryStaffPayslipStore;


    const classes = useStyles();

    const history = useHistory();

    useEffect(function () {
        async function getData(salaryResultId) {
            await getSalaryResultBoard(salaryResultId);
        }

        if (salaryResultId) {
            getData(salaryResultId);
        }

        return resetStore;
    }, [salaryResultId]);

    function handleReturn() {
        const redirectUrl = ConstantList.ROOT_PATH + `salary/salary-result`;

        history.push(redirectUrl);
    }

    function handleSwitchUpdateMode() {
        history.push(ConstantList.ROOT_PATH + `salary-result-detail/` + salaryResultId);
    }

    function handleGoConfig() {
        history.push(ConstantList.ROOT_PATH + `salary-result-board-config/` + onViewSalaryResult?.id);
    }



    return (
        <>
            <div className="content-index">
                <div className="index-breadcrumb py-6">
                    <GlobitsBreadcrumb
                        routeSegments={[
                            { name: t("navigation.salary") },
                            { name: t("navigation.salaryResult.title") },
                            { name: onViewSalaryResult?.name || "Chưa đặt tên" }
                        ]}
                    />
                </div>

                <Grid container spacing={2} className="index-card">
                    <Grid item xs={12}>
                        <Grid container spacing={2} className={classes.root}>
                            <Grid item xs={12}>
                                <div className="flex justify-between">
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            // className="btn px-8 py-2 btn-info d-inline-flex mr-12"
                                            type="button"
                                            onClick={handleReturn}
                                        >
                                            <ArrowBackIcon className="mr-6" />
                                            Quay lại
                                        </Button>

                                        {/* <Button
                                            type="button"
                                            onClick={() => {
                                                handleSwitchUpdateMode();
                                            }}
                                        >
                                            <RateReviewIcon className="mr-6" />
                                            Cập nhật lương nhân viên
                                        </Button>

                                        {onViewSalaryResult?.id && (
                                            <Button
                                                type="button"
                                                onClick={() => handleGoConfig()}
                                            >
                                                <SettingsApplicationsIcon className="mr-6" />
                                                Cấu hình bảng lương
                                            </Button>
                                        )} */}
                                    </ButtonGroup>
                                </div>

                            </Grid>

                            <Grid item xs={12}>
                                <SalaryBoardViewMode />
                            </Grid>
                        </Grid>
                    </Grid>
                </Grid>

                {/* {
                    onViewResultStaff && (
                        <SalaryStaffPaySlipPopup />
                    )
                } */}

                {openCreateEditPopup && <SalaryStaffPayslipForm />}

                {openRecalculatePayslip && <SalaryRecalPayslipPopup />}

            </div >
        </>
    );
}

export default memo(observer(SalaryResultDetailROIndex));
