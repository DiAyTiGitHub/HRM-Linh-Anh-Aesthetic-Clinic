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
import LocalConstants from 'app/LocalConstants';


function StaffSocialInsuranceChangePaidStatusPopup() {
    const { staffSocialInsuranceStore } = useStore();
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
        pagingStaffSocialInsurance
    } = staffSocialInsuranceStore;

    function renderInsuranceAmount({ value, percentageKey }) {
        const insuranceSalary = value?.insuranceSalary || 0;
        const percentage = value?.[percentageKey] || 0;
        const currency = value?.id ? '' : ' VNĐ';
        const calculatedAmount = (insuranceSalary * percentage) / 100;

        // Nếu là dòng tổng (không có id) và không có phần trăm thì không hiển thị gì
        if (!value?.id && percentage === 0) return null;

        return (
            <span style={{ whiteSpace: 'pre-line' }}>
                {calculatedAmount.toLocaleString()} {currency}
                {/* {percentage ? `\n(${percentage}%)` : ''} */}
            </span>
        );
    }

    const columns = [
        {
            align: "center",
            title: "Mã nhân viên",
            field: "staff.staffCode",
            minWidth: "150px",
        },
        {
            align: "center",
            title: "Tên nhân viên",
            field: "staff.displayName",
            minWidth: "150px",
        },
        {
            title: "Kỳ lương",
            field: "salaryPeriod.name",
            minWidth: "150px",
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
        // {
        //     title: "Ngày sinh",
        //     field: "birthDate",
        //     render: (rowData) => (
        //         <span>
        //             {rowData?.staff?.birthDate && (formatDate("DD/MM/YYYY", rowData?.staff?.birthDate))}
        //         </span>
        //     ),
        // },
        // {
        //     title: "Kỳ lương",
        //     field: "salaryPeriod.name",
        //     render: (rowData) => (
        //         <span>
        //             {
        //                 rowData?.salaryPeriod && (
        //                     <>
        //                         {`${rowData?.salaryPeriod?.name}`}
        //                         <br />
        //                         {`(${formatDate("DD/MM/YYYY", rowData?.salaryPeriod?.fromDate)} - ${formatDate("DD/MM/YYYY", rowData?.salaryPeriod?.toDate)}`}
        //                     </>
        //                 )
        //             }
        //         </span>
        //     ),
        // },
        // {
        //     title: "Dữ liệu từ",
        //     field: "salaryResult.name",
        // },
        {
            align: "center",
            title: "Mức lương đóng BHXH",
            field: "insuranceSalary",
            minWidth: "150px",
            render: (value) => formatMoney(value?.insuranceSalary)
        },
        {
            align: "center",
            title: "Số tiền BHXH của nhân viên đóng",
            field: "socialInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'staffSocialInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Số tiền BHYT nhân viên đóng",
            field: "healthInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'staffHealthInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Số tiền BHTN nhân viên đóng",
            field: "unemploymentInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'staffUnemploymentInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Tổng tiền bảo hiểm nhân viên đóng",
            field: "staffTotalInsuranceAmount",
            minWidth: "150px",
            render: function (value) {
                const res = formatMoney(value?.staffTotalInsuranceAmount || 0);
                if (!res) return "";
                if (value?.id) {
                    return res;
                }
                return res + " VNĐ"
            }
        },
        {
            align: "center",
            title: "Số tiền BHXH của công ty đóng",
            field: "socialInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'orgSocialInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Số tiền BHYT công ty đóng",
            field: "healthInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'orgHealthInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Số tiền BHTN công ty đóng",
            field: "unemploymentInsurance",
            minWidth: "150px",
            render: function (value) {
                return renderInsuranceAmount({
                    value,
                    percentageKey: 'orgUnemploymentInsurancePercentage',
                });
            }
        },
        {
            align: "center",
            title: "Tổng tiền bảo hiểm công ty đóng",
            field: "orgTotalInsuranceAmount",
            minWidth: "150px",
            render: function (value) {
                const res = formatMoney(value?.orgTotalInsuranceAmount || 0);
                if (!res) return "";
                if (value?.id) {
                    return res;
                }
                return res + " VNĐ"
            }
        },
        {
            align: "center",
            title: "Tổng tiền",
            minWidth: "150px",
            field: "totalInsuranceAmount",
            render: function (value) {
                const res = formatMoney(value?.totalInsuranceAmount);
                if (value?.id) {
                    return res;
                }
                return res + " VNĐ"
            }
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
        pagingStaffSocialInsurance();
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
                                {
                                    onChooseStatus == LocalConstants.StaffSocialInsurancePaidStatus.PAID.value && (
                                        <span>
                                            <strong>CẢNH BÁO:</strong> Bạn đang thực hiện thao tác chuyển XÁC NHẬN CHI TRẢ.
                                            <br />
                                            Vui lòng Kiểm tra lại các thông tin.
                                            {/* Hành động này không thể hoàn tác. */}
                                            <br />
                                        </span>
                                    )
                                }

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

export default memo(observer(StaffSocialInsuranceChangePaidStatusPopup));