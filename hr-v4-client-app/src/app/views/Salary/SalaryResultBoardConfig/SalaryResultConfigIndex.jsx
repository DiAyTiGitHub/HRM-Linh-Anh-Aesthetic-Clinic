import React, { useEffect, memo, useState, Suspense } from "react";
import { Grid, Button, makeStyles, ButtonGroup } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import ArrowBackIcon from '@material-ui/icons/ArrowBack';
import { useHistory } from "react-router-dom";
import { useParams, useLocation } from "react-router-dom/cjs/react-router-dom";
import ConstantList from "app/appConfig";
import RateReviewIcon from '@material-ui/icons/RateReview';
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import DeleteIcon from '@material-ui/icons/Delete';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import SaveOutlinedIcon from '@material-ui/icons/SaveOutlined';
import { Form, Formik } from "formik";
import * as Yup from "yup";
import FormikFocusError from "app/common/FormikFocusError";
import SalaryResultConfigTabContainer from "./SalaryResultConfigTabContainer";
import PreviewSalaryResultLazySkeleton from "../SalaryTemplateCU/SalaryTemplateCUPopup/PreviewSalaryResultLazySkeleton";
import PreviewConfigBoard from "./PreviewConfigBoard";



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

function SalaryResultConfigIndex() {
    const location = useLocation();
    const queryParams = new URLSearchParams(location?.search); // Parse the query string
    const { id: salaryResultId } = useParams();
    const { t } = useTranslation();
    const classes = useStyles();
    const history = useHistory();

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.code")).nullable(),
        name: Yup.string().required(t("validation.name")).nullable(),
        salaryTemplate: Yup.object().required("Chưa chọn mẫu bảng lương").nullable(),
        salaryPeriod: Yup.object().required("Chưa chọn kì lương").nullable(),
    });

    const {
        salaryResultStore,
        salaryResultDetailStore
    } = useStore();

    const {
        openConfirmDeletePopup,
        handleClose,
        handleConfirmDeleteOnly,
        handleDelete,
        saveBoardConfig
    } = salaryResultStore;

    const {
        resetStore,
        getSalaryResultBoard,
        onViewSalaryResult,
        setTabCU,
        tabCU,
        handleConfirmDeleteResultStaff,
        openConfirmDeleteResultStaff,
        handleCloseConfirmDeleteResultStaff,
        selectedResultStaff
    } = salaryResultDetailStore;

    const {
        handleOpenPreview,
        isOpenPreview
    } = salaryResultStore;

    async function handleSaveSalaryResult(values) {
        try {
            const response = await saveBoardConfig(values);

            const redirectUrl = ConstantList.ROOT_PATH + `salary-result-detail-read-only/` + salaryResultId;

            history.push(redirectUrl);
        }
        catch (err) {
            console.error(err);
        }
    }

    function handleReturn() {
        const redirectUrl = ConstantList.ROOT_PATH + `salary-result-detail-read-only/` + salaryResultId;

        history.push(redirectUrl);
    }

    function handleRedirectViewList() {
        const redirectUrl = ConstantList.ROOT_PATH + `salary/salary-result`;

        history.push(redirectUrl);
    }

    async function handleConfirmDeleteAndRedirect() {
        try {
            const response = await handleConfirmDeleteOnly();

            if (response) {
                handleRedirectViewList();
            }

        }
        catch (error) {
            console.error(error);
        }
    }

    useEffect(function () {
        async function getData(salaryResultId) {
            await getSalaryResultBoard(salaryResultId);
        }

        if (salaryResultId) {
            getData(salaryResultId);
        }

        return resetStore;
    }, [salaryResultId]);

    const [initialValues, setInitialValues] = useState(onViewSalaryResult);

    useEffect(function () {
        setInitialValues(onViewSalaryResult);
    }, [onViewSalaryResult, onViewSalaryResult?.id]);


    return (
        <>
            <div className="content-index">
                <div className="index-breadcrumb py-6">
                    <GlobitsBreadcrumb
                        routeSegments={[
                            { name: t("navigation.salary") },
                            { name: t("navigation.salaryResult.title") },
                            { name: "Cấu hình" },
                            { name: onViewSalaryResult?.name || "Chưa đặt tên" }
                        ]}
                    />
                </div>

                <Grid container spacing={2} className="index-card">
                    <Grid item xs={12}>
                        <Formik
                            validationSchema={validationSchema}
                            enableReinitialize
                            initialValues={initialValues}
                            onSubmit={handleSaveSalaryResult}
                        >
                            {({ isSubmitting, resetForm, values }) => {


                                return (
                                    <Form autoComplete="off" autocomplete="off">
                                        <FormikFocusError />

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

                                                        {onViewSalaryResult?.id && (
                                                            <Button
                                                                // className="btn px-8 py-2 btn-danger d-inline-flex mr-12"
                                                                type="button"
                                                                disabled={isSubmitting}
                                                                onClick={() => handleDelete(onViewSalaryResult)}
                                                            >
                                                                <DeleteIcon className="mr-6" />
                                                                Xóa
                                                            </Button>
                                                        )}

                                                        <Button
                                                            // className="btn px-8 py-2 bg-light-gray d-inline-flex mr-12"
                                                            type="reset"
                                                            disabled={isSubmitting || JSON.stringify(values) === JSON.stringify(initialValues)}
                                                        >
                                                            <RotateLeftIcon className="mr-6" />
                                                            Đặt lại
                                                        </Button>

                                                        <Button
                                                            // className="btn px-8 py-2 btn-success d-inline-flex"
                                                            // fullWidth
                                                            type="submit"
                                                            disabled={isSubmitting || JSON.stringify(values) === JSON.stringify(initialValues)}
                                                        >
                                                            <SaveOutlinedIcon className="mr-6" />
                                                            Lưu cấu hình và tạo mới dữ liệu
                                                        </Button>

                                                    </ButtonGroup>
                                                </div>

                                            </Grid>

                                            <Grid item xs={12}>
                                                <SalaryResultConfigTabContainer />
                                            </Grid>
                                        </Grid>

                                        {
                                            isOpenPreview && (
                                                <PreviewConfigBoard />
                                            )
                                        }
                                    </Form>
                                )
                            }}
                        </Formik>

                    </Grid>
                </Grid>
            </div >

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteAndRedirect}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}

            {openConfirmDeleteResultStaff && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeleteResultStaff}
                    onConfirmDialogClose={handleCloseConfirmDeleteResultStaff}
                    onYesClick={handleConfirmDeleteResultStaff}
                    title={t("confirm_dialog.delete.title")}
                    text={`Xác nhận xóa dữ liệu tính lương của nhân viên ${selectedResultStaff?.selectedStaff?.displayName} trong ${onViewSalaryResult?.name}?`}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
        </>
    );
}

export default memo(observer(SalaryResultConfigIndex));
