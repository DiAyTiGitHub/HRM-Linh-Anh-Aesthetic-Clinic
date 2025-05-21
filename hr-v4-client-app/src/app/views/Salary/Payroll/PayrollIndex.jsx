import React, { useEffect, memo } from "react";
import { Grid, Button, makeStyles, ButtonGroup } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { useParams, useLocation } from "react-router-dom/cjs/react-router-dom";
import SalaryStaffPayslipForm from "../SalaryStaffPayslip/SalaryStaffPayslipForm";
import SalaryRecalPayslipPopup from "../SalaryStaffPayslip/SalaryRecalPayslip/SalaryRecalPayslipPopup";
import PayrollBoard from "./PayrollBoard";
import PayrollToolbar from "./PayrollSearch/PayrollToolbar";
import GlobitsColorfulThemePopup from "app/common/GlobitsColorfulThemePopup";
import OrphanedPayslipsPopup from "./OrphanedPayslips/OrphanedPayslipsPopup";
import StaffSalaryTemplateCUForm from "app/views/StaffSalaryTemplate/StaffSalaryTemplateCUForm";
import SalaryValueHistoriesPopup from "app/views/StaffSalaryTemplate/SalaryValueHistories/SalaryValueHistoriesPopup";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { toast } from "react-toastify";
import { useHistory } from "react-router-dom";
import ConstantList from "app/appConfig";

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

function PayrollIndex() {
    const { id: salaryResultId } = useParams();
    const { t } = useTranslation();

    const history = useHistory();

    const {
        payrollStore,
        salaryStaffPayslipStore,
        hrRoleUtilsStore,
        staffSalaryTemplateStore,
        staffSalaryItemValueStore
    } = useStore();

    const {
        openViewPopup,
    } = staffSalaryTemplateStore;

    const {
        checkAllUserRoles
    } = hrRoleUtilsStore;

    const {
        resetStore,
        onViewSalaryResult,
        handleSetSearchObj,
        openConfirmDownloadPdfPopup,
        handleClose,
        handExportPdfCalculateSalaryStaffs,
        openConfirmDownloadExcelPopup,
        handExportExcelCalculateSalaryStaffs,
        getBasicInfoById,
        pagingSalaryResultStaff,
        getListSumSalaryResultStaff,
        hasAnyOrphanedPayslips,
        openViewOrphanedPayslipsPopup,
        openConfirmRecalculationPopup,
        handRecalculationConfirmSalaryBoard,
        openLockPayrollPopup,
        openUnlockPayrollPopup,
        confirmUnLockPayroll,
        confirmLockPayroll,
        openConfirmDeleteListPopup,
        handleConfirmDeleteList,
    } = payrollStore;

    const {
        openValueHitoriesPopup
    } = staffSalaryItemValueStore;

    const {
        openCreateEditPopup,
        openRecalculatePayslip
    } = salaryStaffPayslipStore;


    const classes = useStyles();


    async function fetchScreenData() {
        try {
            if (!salaryResultId) return;

            handleSetSearchObj({ salaryResultId });

            getBasicInfoById(salaryResultId);
            await pagingSalaryResultStaff();
            await getListSumSalaryResultStaff();
            hasAnyOrphanedPayslips(salaryResultId);
        }
        catch (error) {
            console.error(error);
            // toast.error
        }
    }

    useEffect(function () {
        checkAllUserRoles();
        if (salaryResultId) {
            fetchScreenData();
        }
        return resetStore;

    }, [salaryResultId]);


    async function handleConfirmLockPayroll() {
        try {
            const response = await confirmLockPayroll();

            if (response) {
                toast.info("Đang khởi tạo lại bảng lương", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });

                await fetchScreenData();

                toast.dismiss();
                toast.success("Đã khởi tạo lại bảng lương");
            }
        }
        catch (error) {
            console.error(error);
        }
    }

    async function handleConfirmUnlockPayroll() {
        try {
            const response = await confirmUnLockPayroll();

            if (response) {
                toast.info("Đang khởi tạo lại bảng lương", {
                    autoClose: 5000,
                    draggable: false,
                    limit: 5,
                });

                await fetchScreenData();

                toast.dismiss();
                toast.success("Đã khởi tạo lại bảng lương");
            }
        }
        catch (error) {
            console.error(error);
        }
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


                <Grid container spacing={2}>
                    <Grid item xs={12} className="index-card">
                        <PayrollToolbar />
                    </Grid>

                    <Grid item xs={12} className="index-card mt-12">
                        <PayrollBoard />
                    </Grid>
                </Grid>


                {/* {
                    onViewResultStaff && (
                        <SalaryStaffPaySlipPopup />
                    )
                } */}

                {openCreateEditPopup && <SalaryStaffPayslipForm />}

                {openRecalculatePayslip && (
                    <SalaryRecalPayslipPopup
                        actionAfterSave={async () => {
                            try {
                                await pagingSalaryResultStaff();
                                await getListSumSalaryResultStaff();
                            }
                            catch (error) {
                                console.error(error);
                            }
                        }}
                    />
                )}

                {
                    openViewOrphanedPayslipsPopup && (
                        <OrphanedPayslipsPopup />
                    )
                }

                {openConfirmDownloadPdfPopup && (
                    <GlobitsColorfulThemePopup
                        open={openConfirmDownloadPdfPopup}
                        handleClose={handleClose}
                        size={"xs"}
                        onConfirm={handExportPdfCalculateSalaryStaffs}
                    >
                        <ExportConfirmWarningContent />
                    </GlobitsColorfulThemePopup>
                )}

                {openConfirmDownloadExcelPopup && (
                    <GlobitsColorfulThemePopup
                        open={openConfirmDownloadExcelPopup}
                        handleClose={handleClose}
                        size={"xs"}
                        onConfirm={handExportExcelCalculateSalaryStaffs}
                    >
                        <ExportConfirmWarningContent />
                    </GlobitsColorfulThemePopup>
                )}

                {openConfirmRecalculationPopup && (
                    <GlobitsColorfulThemePopup
                        open={openConfirmRecalculationPopup}
                        handleClose={handleClose}
                        size={"xs"}
                        onConfirm={() => handRecalculationConfirmSalaryBoard(salaryResultId)}
                    >
                        <RecalculationConfirmWarningContent />
                    </GlobitsColorfulThemePopup>
                )}


                {openViewPopup && (
                    <StaffSalaryTemplateCUForm
                        readOnly={true}
                    />
                )}

                {openValueHitoriesPopup && (
                    <SalaryValueHistoriesPopup />
                )}

                {openLockPayrollPopup && (
                    <GlobitsConfirmationDialog
                        open={openLockPayrollPopup}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmLockPayroll}
                        title={t("confirm_dialog.delete.title")}
                        text={"Bạn có chắc muốn KHÓA bảng lương hiện tại? Bảng lương và các phiếu lương thuộc bảng lương sẽ không được sửa đổi"}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                )}

                {openUnlockPayrollPopup && (
                    <GlobitsConfirmationDialog
                        open={openUnlockPayrollPopup}
                        onConfirmDialogClose={handleClose}
                        onYesClick={handleConfirmUnlockPayroll}
                        title={t("confirm_dialog.delete.title")}
                        text={"Bạn có chắc muốn HỦY KHÓA bảng lương hiện tại? Bảng lương và các phiếu lương thuộc bảng lương sẽ có thể tiếp tục được sửa đổi"}
                        agree={t("confirm_dialog.delete.agree")}
                        cancel={t("confirm_dialog.delete.cancel")}
                    />
                )}


                {openConfirmDeleteListPopup && (
                    <GlobitsConfirmationDialog
                        open={openConfirmDeleteListPopup}
                        onConfirmDialogClose={handleClose}
                        onYesClick={async () => {
                            await handleConfirmDeleteList()
                            history.push(ConstantList.ROOT_PATH + `salary/salary-result`);

                        }}
                        title={t("confirm_dialog.delete_list.title")}
                        text={t("confirm_dialog.delete_list.text")}
                        agree={t("confirm_dialog.delete_list.agree")}
                        cancel={t("confirm_dialog.delete_list.cancel")}
                    />
                )}


            </div >
        </>
    );
}

export default memo(observer(PayrollIndex));

function ExportConfirmWarningContent() {
    return (
        <div className="dialogScrollContent">
            <h6 className="text-red">
                <strong>
                    {`Lưu ý: `}
                </strong>
                Bạn đang thực hiện hành động xuất danh sách dữ liệu theo bộ lọc, hành động này sẽ lấy dữ liệu của tất
                cả dữ liệu theo bộ lọc và
                <strong>
                    {` có thể cần đến vài phút`}
                </strong>
                <br />
                <strong className='flex pt-6'>BẠN CÓ CHẮC MUỐN THỰC HIỆN HÀNH ĐỘNG NÀY?</strong>
            </h6>
        </div>
    );
}

function RecalculationConfirmWarningContent() {
    return (
        <div className="dialogScrollContent">
            <h6 className="text-red">
                <strong>
                    {`Lưu ý: `}
                </strong>
                Bạn đang thực hiện hành động tính toán lại bảng lương cho tất cả nhân viên trong danh sách
                {/* <strong>
                    {` có thể cần đến vài phút`}
                </strong> */}
                <br />
                <strong className='flex pt-6'>BẠN CÓ CHẮC MUỐN THỰC HIỆN HÀNH ĐỘNG NÀY?</strong>
            </h6>
        </div>
    );
}
