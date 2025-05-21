import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Icon, IconButton, Grid, Button, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { Form, Formik } from 'formik';
import FormikFocusError from 'app/common/FormikFocusError';
import GlobitsTextField from 'app/common/form/GlobitsTextField';
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';
import { formatDate } from 'app/LocalFunction';
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import SaveIcon from "@material-ui/icons/Save";
import DeleteIcon from "@material-ui/icons/Delete";
import GlobitsTable from 'app/common/GlobitsTable';
import LocalConstants from 'app/LocalConstants';

function CRRMoveToNextRoundPopup() {
    const { t } = useTranslation();

    const {
        candidateRecruitmentRoundStore,
        recruitmentStore
    } = useStore();

    const {
        selectedRecruitment
    } = recruitmentStore;

    const {
        openMoveToNextRoundPopup,
        handleClose,
        handleConfirmMoveToNextRound,
        handleRemoveActionItem,
        listOnDelete,
        handleSelectListDelete,
        isLastRecruitmentRound
    } = candidateRecruitmentRoundStore;

    const columns = [
        {
            title: "Mã ứng viên",
            field: "candidate.candidateCode",
        },
        {
            title: "Họ tên",
            field: "candidate.displayName",
        },
        {
            title: "Ngày sinh",
            field: "candidate.birthDate",
            render: (rowData) => (
                <span>
                    {rowData?.candidate?.birthDate && (formatDate("DD/MM/YYYY", rowData?.candidate.birthDate))}
                </span>
            ),
        },
        {
            title: "Giới tính",
            field: "candidate.gender",
            render: rowData => (
                <span>
                    {rowData?.candidate.gender === "M" ? "Nam" : rowData?.candidate.gender === "F" ? "Nữ" : ""}
                </span>
            )
        },
        {
            title: "SĐT",
            field: "candidate.phoneNumber",
            render: rowData => (
                <>
                    {rowData?.phoneNumber && (
                        <span className="pr-8">
                            {rowData?.candidate.phoneNumber}
                        </span>
                    )}
                </>
            )
        },
        {
            title: "Ngày nộp hồ sơ",
            field: "candidate.submissionDate",
            render: (rowData) => (
                <span>
                    {rowData?.candidate.submissionDate && (formatDate("DD/MM/YYYY", rowData?.candidate.submissionDate))}
                </span>
            ),
        },

        {
            title: "Vị trí dự thi",
            field: "examPosition",
            render: (rowData) => (
                <span>
                    {rowData?.examPosition}
                </span>
            ),
        },
        {
            title: "Thời gian dự thi",
            field: "actualTakePlaceDate",
            render: (rowData) => (
                <span>
                    {rowData?.actualTakePlaceDate && (formatDate("HH:mm DD/MM/YYYY", rowData?.actualTakePlaceDate))}
                </span>
            ),
        },
        {
            title: "Kết quả hiện tại",
            field: "result",
            render: (rowData) => (
                <span>
                    {rowData?.result && (LocalConstants.CandidateExamStatus.getValueByKey(rowData?.result))}
                </span>
            ),
        },
        {
            title: t("general.action"),
            width: "6%",
            align: "center",
            render: (rowData) => {
                return (
                    <div className="flex flex-middle w-100 justify-center">
                        <Tooltip title="Loại bỏ" placement="top">
                            <IconButton className="" size="small" onClick={() => handleRemoveActionItem(rowData?.id)}>
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    </div >
                );
            },
        },
    ];

    let validationSchema = isLastRecruitmentRound(selectedRecruitment) ? Yup.object({}) : Yup.object({
        actualTakePlaceDate: Yup.date().transform(function transformDate(castValue, originalValue) {         return originalValue ? new Date(originalValue) : castValue;       }).required(t("validation.required")).nullable(),
        examPosition: Yup.string().nullable()
    });



    const initialValues = {
        actualTakePlaceDate: null,
        examPosition: null,
    }

    return (
        <GlobitsColorfulThemePopup
            open={openMoveToNextRoundPopup}
            handleClose={handleClose}
            hideFooter
            size="lg"
            onConfirm={handleConfirmMoveToNextRound}
        >
            <div className="dialog-body">
                <Grid container spacing={2}>
                    <Grid item xs={12} sm={8} md={9}>
                        <div className="dialogScrollContent">
                            <h6 className="text-red">
                                <strong>
                                    {listOnDelete?.length <= 1 ? "Thông tin " : "Danh sách "}

                                    {isLastRecruitmentRound(selectedRecruitment) ? "ứng viên được đánh dấu đã ĐẠT kì thi tuyển" : "ứng viên được chuyển đến vòng thi tiếp theo"}

                                </strong>
                            </h6>
                            <GlobitsTable
                                data={listOnDelete}
                                handleSelectList={handleSelectListDelete}
                                columns={columns}
                                nonePagination
                            />
                        </div>
                    </Grid>

                    <Grid item xs={12} sm={4} md={3}>
                        <Formik
                            validationSchema={validationSchema}
                            enableReinitialize
                            initialValues={initialValues}
                            onSubmit={handleConfirmMoveToNextRound}
                        >
                            {({ isSubmitting, values, setFieldValue, initialValues }) => {

                                return (
                                    <Form autoComplete="off" autocomplete="off">
                                        <FormikFocusError />

                                        {!isLastRecruitmentRound(selectedRecruitment) && (
                                            <Grid container spacing={2}>
                                                <Grid item xs={12}>
                                                    <GlobitsDateTimePicker
                                                        isDateTimePicker
                                                        required
                                                        name="actualTakePlaceDate"
                                                        label="Ngày phỏng vấn/thi tuyển"
                                                    />
                                                </Grid>

                                                <Grid item xs={12}>
                                                    <GlobitsTextField
                                                        label="Vị trí dự thi"
                                                        name="examPosition"
                                                    />
                                                </Grid>
                                            </Grid>
                                        )}


                                        <div className="pt-12" style={{ color: "#5e6c84" }}>
                                            {t("task.action")}
                                        </div>

                                        <div className="listButton">
                                            <Button
                                                variant="contained"
                                                className="btn-green"
                                                startIcon={<SaveIcon />}
                                                type="submit"
                                                disabled={isSubmitting}
                                            >
                                                Xác nhận
                                            </Button>

                                            <Button
                                                startIcon={<DeleteIcon />}
                                                variant="contained"
                                                onClick={handleClose}
                                                className="btn-danger"
                                                disabled={isSubmitting}
                                            >
                                                Hủy bỏ
                                            </Button>
                                        </div>
                                    </Form>
                                );
                            }
                            }
                        </Formik>
                    </Grid>
                </Grid>
            </div>
        </GlobitsColorfulThemePopup>
    );
}

export default memo(observer(CRRMoveToNextRoundPopup));