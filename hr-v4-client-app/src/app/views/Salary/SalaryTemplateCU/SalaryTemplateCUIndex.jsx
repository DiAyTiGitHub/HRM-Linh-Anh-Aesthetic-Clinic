import { Button, ButtonGroup, Grid, makeStyles } from "@material-ui/core";
import ArrowBackIcon from '@material-ui/icons/ArrowBack';
import DeleteIcon from '@material-ui/icons/Delete';
import RemoveRedEyeIcon from '@material-ui/icons/RemoveRedEye';
import RotateLeftIcon from '@material-ui/icons/RotateLeft';
import SaveOutlinedIcon from '@material-ui/icons/SaveOutlined';
import ConstantList from "app/appConfig";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import { useLocation, useParams } from "react-router-dom/cjs/react-router-dom";
import * as Yup from "yup";
import PreviewSalaryResult from "./SalaryTemplateCUPopup/PreviewSalaryResult";
import SalaryTemplateCUTabContainer from "./SalaryTemplateCUTabContainer";


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

function SalaryTemplateCUIndex() {
    const location = useLocation();
    const queryParams = new URLSearchParams(location?.search); // Parse the query string
    // const isFromExamCandidate = queryParams.get('isFromExamCandidate'); // Get the value of the 'isFromExamCandidate' parameter
    // const isFromPassedCandidate = queryParams.get('isFromPassedCandidate'); // Get the value of the 'isFromPassedCandidate' parameter
    // const isFromWaitingJobCandidate = queryParams.get('isFromWaitingJobCandidate'); // Get the value of the 'isFromWaitingJobCandidate' parameter
    // const isFromNotComeCandidate = queryParams.get('isFromNotComeCandidate'); // Get the value of the 'isFromNotComeCandidate' parameter
    // const isFromOnboardedCandidate = queryParams.get('isFromOnboardedCandidate'); // Get the value of the 'isFromOnboardedCandidate' parameter

    const { id: salaryTemplateId } = useParams();
    const isViewMode = location.pathname.includes('/view/');
    const { t } = useTranslation();

    const {
        salaryTemplateStore,
        salaryResultStore,
        hrRoleUtilsStore,

    } = useStore();

    const {
        handleClose,
        saveSalaryTemplate,
        selectedSalaryTemplate,
        openConfirmDeletePopup,
        handleConfirmDelete,
        resetStore,
        handleGetSalaryTemplateData,
        handleDelete,
        openViewPopup: readOnly,
        handleOpenView
    } = salaryTemplateStore;

    const {
        isAdmin,
        isManager,
        isCompensationBenifit,
        checkAllUserRoles
    } = hrRoleUtilsStore;

    const classes = useStyles();

    const salaryTemplateValidationSchema = Yup.object({
        code: Yup.string().nullable().required(t("validation.required")),
        name: Yup.string().nullable().required(t("validation.required")),
    });

    useEffect(() => {
        if (isViewMode) {
            handleOpenView(salaryTemplateId)
        }
    }, [])


    const history = useHistory();

    async function handleSaveForm(values) {
        try {
            const savedTemplate = await saveSalaryTemplate(values);

            //link to page edit when staff created new
            if (!savedTemplate?.id) throw Error("Error when saving salary template");

            const redirectUrl = ConstantList.ROOT_PATH + `salary-template/` + savedTemplate?.id;
            // if (isFromExamCandidate) {
            //     redirectUrl += "?isFromExamCandidate=true";
            // }
            // else if (isFromPassedCandidate) {
            //     redirectUrl += "?isFromPassedCandidate=true";
            // }
            // else if (isFromWaitingJobCandidate) {
            //     redirectUrl += "?isFromWaitingJobCandidate=true";
            // }
            // else if (isFromNotComeCandidate) {
            //     redirectUrl += "?isFromNotComeCandidate=true";
            // }
            // else if (isFromOnboardedCandidate) {
            //     redirectUrl += "?isFromOnboardedCandidate=true";
            // }

            history.push(redirectUrl);
        } catch (error) {
            console.error(error);
        }

    }

    useEffect(function () {
        checkAllUserRoles();

        async function getCandidateData(salaryTemplateId) {
            await handleGetSalaryTemplateData(salaryTemplateId);
        }

        if (salaryTemplateId == "new-salary-template") {
            getCandidateData(null);
        } else {
            getCandidateData(salaryTemplateId);
        }

        return resetStore;
    }, [salaryTemplateId]);

    const switchToTabWithError = (errors, values) => {
        // const tabFields = [
        //     {
        //         index: 0,
        //         fields: ['firstName', 'lastName']
        //     },
        //     {
        //         index: 1,
        //         fields: ['candidateCode', 'position']
        //     }
        // ];

        // // console.log("errors", errors);

        // // candidate's profile is approved => validate field interviewDate
        // if (values?.approvalStatus === LocalConstants.CandidateApprovalStatus.APPROVED.value) {
        //     tabFields[1].fields.push('interviewDate');
        // }

        // for (let i = 0; i < tabFields.length; i++) {
        //     const hasErrorInTab = tabFields[i].fields.some((field) => errors[field]);
        //     if (hasErrorInTab) {
        //         setTabCU(i); // Redirect to the tab that has error
        //         break;
        //     }
        // } 
    }

    async function handleConfirmDeleteCandidate() {
        try {
            const response = await handleConfirmDelete();
            if (response)
                handleReturn();
        } catch (error) {
            console.error(error);
        }
    }

    function handleReturn() {
        const redirectUrl = ConstantList.ROOT_PATH + `salary/salary-template`;

        history.push(redirectUrl);
    }

    const {
        handleOpenPreview,
        isOpenPreview
    } = salaryResultStore;

    return (
        <>
            <div className="content-index">
                <div className="index-breadcrumb py-6">
                    <GlobitsBreadcrumb
                        routeSegments={[
                            { name: t("navigation.salary") },
                            { name: t("navigation.salaryTemplate.title") },
                            { name: selectedSalaryTemplate?.name || "Mẫu bảng lương mới" }
                        ]}
                    />
                </div>

                <Grid container spacing={2} className="index-card">
                    <Grid item xs={12}>
                        <Formik
                            validationSchema={salaryTemplateValidationSchema}
                            enableReinitialize
                            initialValues={selectedSalaryTemplate}
                            onSubmit={handleSaveForm}
                        >
                            {({
                                isSubmitting,
                                values,
                                setFieldValue,
                                initialValues,
                                resetForm,
                                errors,
                                handleSubmit
                            }) => {
                                // console.log("values", values);

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
                                                            type="button"
                                                            onClick={handleReturn}
                                                            disabled={isSubmitting}
                                                        >
                                                            <ArrowBackIcon className="mr-6" />
                                                            Quay lại
                                                        </Button>

                                                        {selectedSalaryTemplate?.id && (isAdmin || isManager || isCompensationBenifit) && !readOnly && (
                                                            <Button
                                                                type="button"
                                                                disabled={isSubmitting}
                                                                onClick={() => handleDelete(selectedSalaryTemplate)}
                                                            >
                                                                <DeleteIcon className="mr-6" />
                                                                Xóa
                                                            </Button>
                                                        )}
                                                        {!readOnly && (
                                                            <Button
                                                                type="button"
                                                                onClick={() => resetForm()}
                                                                disabled={isSubmitting}

                                                            >
                                                                <RotateLeftIcon className="mr-6" />
                                                                Đặt lại
                                                            </Button>
                                                        )}

                                                        {(isAdmin || isManager || isCompensationBenifit) && !readOnly && (
                                                            <Button
                                                                type="submit"
                                                                disabled={isSubmitting}
                                                                onClick={() => {
                                                                    switchToTabWithError(errors, values);
                                                                    handleSubmit();
                                                                }}
                                                            >
                                                                <SaveOutlinedIcon className="mr-6" />
                                                                Lưu thông tin
                                                            </Button>
                                                        )}
                                                    </ButtonGroup>

                                                    {
                                                        (values?.templateItems?.length > 0) && (
                                                            <div
                                                                className="flex flex-middle hyperLink cursor-pointer"
                                                                onClick={handleOpenPreview}
                                                            >
                                                                <RemoveRedEyeIcon className="mr-6" />
                                                                <span className="">
                                                                    Xem trước bảng lương
                                                                </span>
                                                            </div>
                                                        )
                                                    }
                                                </div>

                                            </Grid>

                                            <Grid item xs={12}>
                                                <SalaryTemplateCUTabContainer />
                                            </Grid>
                                        </Grid>


                                        {
                                            isOpenPreview && (
                                                // <Suspense fallback={<PreviewSalaryResultLazySkeleton />}>
                                                <PreviewSalaryResult />
                                                // </Suspense>
                                            )
                                        }
                                    </Form>
                                );
                            }}
                        </Formik>

                    </Grid>
                </Grid>

                {
                    openConfirmDeletePopup && (
                        <GlobitsConfirmationDialog
                            open={openConfirmDeletePopup}
                            onConfirmDialogClose={handleClose}
                            onYesClick={handleConfirmDeleteCandidate}
                            title={t("confirm_dialog.delete.title")}
                            text={t("confirm_dialog.delete.text")}
                            agree={t("confirm_dialog.delete.agree")}
                            cancel={t("confirm_dialog.delete.cancel")}
                        />
                    )
                }


            </div>
        </>
    );
}

export default memo(observer(SalaryTemplateCUIndex));
