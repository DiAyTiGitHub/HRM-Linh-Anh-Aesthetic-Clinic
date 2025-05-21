import { Button, ButtonGroup, Grid, useMediaQuery, useTheme } from "@material-ui/core";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import SaveOutlinedIcon from "@material-ui/icons/SaveOutlined";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import DeleteIcon from "@material-ui/icons/Delete";
import ArrowBackIcon from "@material-ui/icons/ArrowBack";
import { useHistory, useParams } from "react-router-dom/cjs/react-router-dom";
import { Form, Formik } from "formik";
import RecruitmentCUTabContainer from "./RecruitmentCUTabContainer";
import FormikFocusError from "app/common/FormikFocusError";
import FormatListNumberedIcon from "@material-ui/icons/FormatListNumbered";
import EvaluationCandiateRoundPopup from "./EvaluationCandidateRound/EvaluationCandiateRoundPopup";

function RecruitmentCUIndex() {
    const { t } = useTranslation();
    const history = useHistory();
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
    const { recruitmentStore } = useStore();
    const { evaluationCandidateRoundStore } = useStore();
    const { openFormEvaluationCandidateRound } = evaluationCandidateRoundStore;

    const { resetStore, selectedRecruitment, saveRecruitment, handleOpenCreateEdit, handleDelete } = recruitmentStore;

    const { id: recruitmentId } = useParams();

    useEffect(
        function () {
            async function getRecruitmentData(recruitmentId) {
                await handleOpenCreateEdit(recruitmentId);
            }

            if (recruitmentId == "new-recruitment") {
                getRecruitmentData(null);
            } else {
                getRecruitmentData(recruitmentId);
            }

            return resetStore;
        },
        [recruitmentId]
    );

    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.code")).nullable(),
        name: Yup.string().required(t("validation.name")).nullable(),
    });

    async function handleSaveForm(values) {
        saveRecruitment(values).then(function (data) {
            if (data?.id) {
                history.push("/recruitment/" + data.id);
            }
        });
    }

    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: t("navigation.recruitment.title") },
                        {
                            name: selectedRecruitment?.id
                                ? "Đợt tuyển dụng " + selectedRecruitment?.code
                                : "Đợt tuyển dụng mới",
                        },
                    ]}
                />
            </div>
          
            <Grid container spacing={2} className='index-card'>
                <Grid item xs={12} className=' mb-6'>
                    <Formik
                        validationSchema={validationSchema}
                        enableReinitialize
                        initialValues={JSON.parse(JSON.stringify(selectedRecruitment))}
                        onSubmit={handleSaveForm}>
                        {({ setFieldValue, values, isSubmitting, errors, touched, resetForm }) => {
                            return (
                                <Form autoComplete='off'>
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <ButtonGroup color='container' aria-label='outlined primary button group'>
                                                <Button
                                                    type='button'
                                                    onClick={() => history.push("/recruitment")}
                                                    disabled={isSubmitting}>
                                                    <ArrowBackIcon className='mr-4' />
                                                    Quay lại
                                                </Button>

                                                {selectedRecruitment?.id && (
                                                    <Button
                                                        type='button'
                                                        disabled={isSubmitting}
                                                        onClick={() => handleDelete(selectedRecruitment)}>
                                                        <DeleteIcon className='mr-4' />
                                                        Xóa
                                                    </Button>
                                                )}

                                                <Button
                                                    type='button'
                                                    onClick={() => resetForm()}
                                                    disabled={isSubmitting}>
                                                    <RotateLeftIcon className='mr-4' />
                                                    Đặt lại
                                                </Button>

                                                <Button type='submit' disabled={isSubmitting}>
                                                    <SaveOutlinedIcon className='mr-4' />
                                                    Lưu thông tin
                                                </Button>

                                                {selectedRecruitment?.id && (
                                                    <Button
                                                        type='button'
                                                        disabled={isSubmitting}
                                                        onClick={() => {
                                                            history.push(
                                                                "/candidates-in-recruitment/" + selectedRecruitment?.id
                                                            );
                                                        }}>
                                                        <FormatListNumberedIcon className='mr-4' />
                                                        Danh sách ứng tuyển
                                                    </Button>
                                                )}
                                            </ButtonGroup>
                                        </Grid>

                                        <Grid item xs={12}>
                                            <RecruitmentCUTabContainer />
                                        </Grid>
                                    </Grid>
                                </Form>
                            );
                        }}
                    </Formik>
                </Grid>
            </Grid>
        </div>
    );
}

export default memo(observer(RecruitmentCUIndex));
