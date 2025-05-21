import { observer } from 'mobx-react';
import React, { memo } from "react";
import { Icon, IconButton, Grid, Button, Tooltip } from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { Form, Formik } from 'formik';
import FormikFocusError from 'app/common/FormikFocusError';
import { formatDate, formatMoney } from 'app/LocalFunction';
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import SaveIcon from "@material-ui/icons/Save";
import DeleteIcon from "@material-ui/icons/Delete";
import GlobitsTable from 'app/common/GlobitsTable';


function SalaryStaffPayslipChangeStatusPopup() {
    const { salaryStaffPayslipStore } = useStore();
    const { t } = useTranslation();
    const {
        openConfirmChangeStatus,
        onChooseStatus,
        handleClose,
        handleRemoveActionItem,
        getPaidStatusName,
        handleSelectListDelete,
        listOnDelete,
        handleConfirmChangeStatus,
        pagingSalaryStaffPayslip
    } = salaryStaffPayslipStore;

    const columns = [
        {
            align: "center",
            title: "Mã nhân viên",
            field: "staff.staffCode",
        },
        {
            align: "center",
            title: "Họ tên",
            field: "staff.displayName",
        },
        {
            title: "Kỳ lương",
            field: "salaryPeriod.name",
            render: (rowData) => (
                <span>
                    {
                        rowData?.salaryPeriod && (
                            <>
                                {`${rowData?.salaryPeriod?.name}`}
                                <br />
                                {`(${formatDate("DD/MM/YYYY", rowData?.salaryPeriod?.fromDate)} - ${formatDate("DD/MM/YYYY", rowData?.salaryPeriod?.toDate)})`}
                            </>
                        )
                    }
                </span>
            ),
        },
        {
            title: "Mẫu bảng lương",
            field: "salaryTemplate.name",
        },
        {
            align: "center",
            title: "Trạng thái hiện tại",
            field: "paidStatus",
            render: (value) => (
                <span className="pr-4">
                    {getPaidStatusName(value?.paidStatus)}
                </span>
            )
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
    });

    const initialValues = {
    }

    function handleCloseConfirmPopup() {
        handleClose();
        pagingSalaryStaffPayslip();
    }

    return (
        <GlobitsColorfulThemePopup
            open={openConfirmChangeStatus}
            handleClose={handleCloseConfirmPopup}
            hideFooter
            size="lg"
            onConfirm={handleConfirmChangeStatus}
        >
            <div className="dialog-body">
                <Grid container spacing={2}>
                    <Grid item xs={12} sm={8} md={9}>
                        <div className="dialogScrollContent">
                            <h6 className="text-red">
                                <strong className='pt-4 flex'>
                                    Danh sách chọn cập nhật thành {
                                        getPaidStatusName(onChooseStatus)
                                    }
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
                            onSubmit={handleConfirmChangeStatus}
                        >
                            {({ isSubmitting, values, setFieldValue, initialValues }) => {

                                return (
                                    <Form autoComplete="off" autocomplete="off">
                                        <FormikFocusError />

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

export default memo(observer(SalaryStaffPayslipChangeStatusPopup));