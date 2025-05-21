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

function CandidateConfirmApprovePopup() {
    const { candidateStore } = useStore();
    const { t } = useTranslation();
    const {
        openApprovePopup,
        handleClose,
        handleConfirmApproveCandidate,
        getApprovalStatus,
        handleRemoveActionItem,
        listOnDelete,
        handleSelectListDelete
    } = candidateStore;

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
            field: "status",
            render: function (applicant) {
                return (<span>{getApprovalStatus(applicant?.status)}</span>);
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

    const validationSchema = Yup.object({
        // interviewDate: Yup.date().transform(function transformDate(castValue, originalValue) {         return originalValue ? new Date(originalValue) : castValue;       }).required(t("validation.required")).nullable()
    });

    const initialValues = {
        interviewDate: null
    }

    return (
        <GlobitsColorfulThemePopup
            open={openApprovePopup}
            handleClose={handleClose}
            hideFooter
            size="lg"
            onConfirm={handleConfirmApproveCandidate}
        >
            <div className="dialog-body">
                <Grid container spacing={2}>
                    <Grid item xs={12} sm={8} md={9}>
                        <div className="dialogScrollContent">
                            <h6 className="text-red">
                                <strong>
                                    {listOnDelete?.length <= 1 ? "Thông tin " : "Danh sách "}
                                    ứng viên được chọn PHÊ DUYỆT
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
                            onSubmit={handleConfirmApproveCandidate}
                        >
                            {({ isSubmitting, values, setFieldValue, initialValues }) => {

                                return (
                                    <Form autoComplete="off" autocomplete="off">
                                        <FormikFocusError /> 

                                        {/* <Grid container spacing={2}>
                                            <Grid item xs={12}>
                                                <GlobitsDateTimePicker
                                                    isDateTimePicker
                                                    required
                                                    name="interviewDate"
                                                    label="Ngày phỏng vấn/thi tuyển"
                                                />
                                            </Grid>

                                            <Grid item xs={12}>
                                                <GlobitsTextField
                                                    label="Vị trí dự thi"
                                                    name="examPosition"
                                                />
                                            </Grid>
                                        </Grid> */}

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

export default memo(observer(CandidateConfirmApprovePopup));