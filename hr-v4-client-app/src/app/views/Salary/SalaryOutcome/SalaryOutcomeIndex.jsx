/* eslint-disable react-hooks/exhaustive-deps */
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { Grid, Button, ButtonGroup } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { useStore } from "app/stores";
import SalaryOutcomeToolbar from "./SalaryOutcomeToolbar";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import SalaryOutcomeBoard from "./SalaryOutcomeBoard/SalaryOutcomeBoard";
import SalaryOutcomeStaffsPopup from "./SalaryOutcomeStaffs/SalaryOutcomeStaffsPopup";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsColorfulThemePopup from "app/common/GlobitsColorfulThemePopup";
import SalaryStaffPayslipForm from "../SalaryStaffPayslip/SalaryStaffPayslipForm";
import SalaryRecalPayslipPopup from "../SalaryStaffPayslip/SalaryRecalPayslip/SalaryRecalPayslipPopup";

function SalaryOutcomeIndex() {
  const { t } = useTranslation();
  const location = useLocation();

  const {
    salaryOutcomeStore,
    hrRoleUtilsStore,
    salaryStaffPayslipStore
  } = useStore();

  const {
    checkAllUserRoles,
  } = hrRoleUtilsStore;

  const {
    searchObject,
    resetStore,
    openChooseStaffsPopup,
    handleOpenConfirmLockPayslipPopup,
    openConfirmLockPayslipPopup,
    handleClose,
    handleConfirmChangeStatusToLock,
    openConfirmDownloadExcelPopup,
    openConfirmDownloadPdfPopup,
    handExportPdfCalculateSalaryStaffs,
    handExportExcelCalculateSalaryStaffs,
    handleCalculateSalaryStaffs
  } = salaryOutcomeStore;


  const {
    openCreateEditPopup,
    openPopupSalary,
    openRecalculatePayslip
  } = salaryStaffPayslipStore;


  useEffect(() => {
    checkAllUserRoles();

    resetStore();

    return resetStore;
  }, []);

  return (
    <div className="content-index pb-48">
      <div className="index-breadcrumb py-6">
        <GlobitsBreadcrumb
          routeSegments={[
            { name: t("navigation.salary") },
            { name: t("navigation.salaryOutcome.title") }]}
        />

      </div>

      <Grid container spacing={2}>
        <Grid item xs={12} className="index-card">
          <SalaryOutcomeToolbar />
        </Grid>

        {
          searchObject?.salaryTemplate && searchObject?.salaryPeriod && (
            <Grid item xs={12} className="index-card mt-8">
              <SalaryOutcomeBoard />
            </Grid>
          )
        }

      </Grid>

      {openCreateEditPopup && <SalaryStaffPayslipForm />}

      {openRecalculatePayslip && (
        <SalaryRecalPayslipPopup
          actionAfterSave={handleCalculateSalaryStaffs}
        />
      )}


      {openChooseStaffsPopup && (
        <SalaryOutcomeStaffsPopup />
      )}

      {
        openConfirmLockPayslipPopup && (
          <GlobitsConfirmationDialog
            open={openConfirmLockPayslipPopup}
            onConfirmDialogClose={handleClose}
            onYesClick={handleConfirmChangeStatusToLock}
            title={"Xác nhận khóa phiếu lương"}
            text={"Bạn có chắc chắn khóa các phiếu lương đã chọn? Hành động này không thể hoàn tác"}
            agree={t("confirm_dialog.delete_list.agree")}
            cancel={t("confirm_dialog.delete_list.cancel")}
          />
        )
      }

      {openConfirmDownloadExcelPopup && (
        <GlobitsColorfulThemePopup
          open={openConfirmDownloadExcelPopup}
          handleClose={handleClose}
          size={"sm"}
          onConfirm={handExportExcelCalculateSalaryStaffs}
        >
          <ExportConfirmWarningContent />
        </GlobitsColorfulThemePopup>
      )}

      {openConfirmDownloadPdfPopup && (
        <GlobitsColorfulThemePopup
          open={openConfirmDownloadPdfPopup}
          handleClose={handleClose}
          size={"sm"}
          onConfirm={handExportPdfCalculateSalaryStaffs}
        >
          <ExportConfirmWarningContent />
        </GlobitsColorfulThemePopup>
      )}
    </div>
  );
};

export default memo(observer(SalaryOutcomeIndex));



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