import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Icon, IconButton, Grid, Button, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { formatDate } from 'app/LocalFunction';
import { useTranslation } from "react-i18next";
import GlobitsTable from 'app/common/GlobitsTable';
import { Form, Formik } from 'formik';
import FormikFocusError from 'app/common/FormikFocusError';
import GlobitsTextField from 'app/common/form/GlobitsTextField';
import SaveIcon from "@material-ui/icons/Save";
import DeleteIcon from "@material-ui/icons/Delete";
import * as Yup from "yup";
import GlobitsDateTimePicker from 'app/common/form/GlobitsDateTimePicker';

function PassedCandidateConfirmReceptPopup() {
    const { passedCandidateStore } = useStore();
    const { t } = useTranslation();
    const {
        openReceptPopup,
        handleClose,
        handleConfirmReceptCandidate,
        getReceptionStatusName,
        handleRemoveActionItem,
        listChosen,
        handleSelectListChosen,
        pagingPassedCandidates,
    } = passedCandidateStore;

    const columns = [
        {
            title: "Mã ứng viên",
            field: "candidateCode",
        },
        {
            title: "Họ tên",
            field: "displayName",
        },
        {
            title: "Ngày sinh",
            field: "birthDate",
            render: (rowData) => (
                <span>
                    {rowData?.birthDate && (formatDate("DD/MM/YYYY", rowData?.birthDate))}
                </span>
            ),
        },
        {
            title: "Đợt tuyển dụng",
            field: "recruitment",
            render: rowData => (
                <>
                    {rowData?.recruitment && (
                        <span className="pr-8">
                            {rowData?.recruitment?.name}
                        </span>
                    )}
                </>
            )
        },
        {
            title: "Vị trí ứng tuyển",
            field: "position",
            render: rowData => (
                <>
                    {rowData?.position && (
                        <span className="pr-8">
                            {rowData?.position?.name}
                        </span>
                    )}
                </>
            )
        },
        {
            title: "Trạng thái hiện tại",
            field: "receptionStatus",
            render: function (applicant) {
                return (<span>{getReceptionStatusName(applicant?.receptionStatus)}</span>);
            }
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

    function handleCloseConfirmPopup() {
        handleClose();
        pagingPassedCandidates();
    }

    const validationSchema = Yup.object({
        onboardDate: Yup.date().transform(function transformDate(castValue, originalValue) {         return originalValue ? new Date(originalValue) : castValue;       }).required(t("validation.required")).nullable()
    });

    const initialValues = {
        onboardDate: null
    }

    return (
        <GlobitsColorfulThemePopup
            open={openReceptPopup}
            handleClose={handleCloseConfirmPopup}
            size="lg"
            hideFooter
            onConfirm={handleConfirmReceptCandidate}
        >
            <div className="dialog-body">
                <Grid container spacing={2}>
                    <Grid item xs={12} sm={8} md={9}>
                        <div className="dialogScrollContent">
                            <h6 className="text-red">
                                <strong>
                                    {listChosen?.length <= 1 ? "Thông tin " : "Danh sách "}
                                    ứng viên được chuyển sang CHỜ NHẬN VIỆC
                                </strong>
                            </h6>
                            <GlobitsTable
                                data={listChosen}
                                handleSelectList={handleSelectListChosen}
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
                            onSubmit={handleConfirmReceptCandidate}
                        >
                            {({ isSubmitting, values, setFieldValue, initialValues }) => {

                                return (
                                    <Form autoComplete="off" autocomplete="off">
                                        <FormikFocusError />

                                        <Grid container spacing={2}>
                                            <Grid item xs={12}>
                                                <GlobitsDateTimePicker
                                                    isDateTimePicker
                                                    required
                                                    name="onboardDate"
                                                    label="Ngày tiếp nhận"
                                                />
                                            </Grid>
                                        </Grid>

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
                                                onClick={handleCloseConfirmPopup}
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

export default memo(observer(PassedCandidateConfirmReceptPopup));