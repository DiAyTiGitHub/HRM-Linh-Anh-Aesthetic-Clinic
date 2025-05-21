import React, { useState, memo } from "react";
import { useFormikContext } from "formik";
import { Button, Tooltip } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { useHistory } from "react-router-dom";
import GroupWorkIcon from '@material-ui/icons/GroupWork';
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";

function MergeOrphanedPayslipButton() {
    const {
        payrollStore,
        salaryStaffPayslipStore,

    } = useStore();

    const history = useHistory();

    const { t } = useTranslation();

    const {
        openViewMergeOrphanedPayslipButton,
        handleCloseViewOrphanedPayslip,
        onViewSalaryResult,
        getAllOrphanedPayslips,
        pagingSalaryResultStaff,
        handleMergeOrphansToSalaryBoard
    } = payrollStore;

    const {
        openCreateEditPopup,
        openRecalculatePayslip,
        openConfirmDeletePopup,
        handleClose,
        handleConfirmDelete,
        listOnDelete,
        getSelectedIds
    } = salaryStaffPayslipStore;

    async function handleConfirmMergeToSalaryBoard() {
        try {
            const selectedIds = getSelectedIds();

            const response = await handleMergeOrphansToSalaryBoard(selectedIds);

            await pagingSalaryResultStaff();

            handleCloseViewOrphanedPayslip();
        } catch (err) {
            console.error("Error merge data:", err);
        }
    }

    const [openConfirmMerge, setOpenConfirmMerge] = useState(false);

    function handleOpenConfirmMergePopup() {
        setOpenConfirmMerge(true);
    }

    function handlCloseConfirmMergePopup() {
        setOpenConfirmMerge(false);
    }

    return (
        <>
            {
                listOnDelete && listOnDelete.length > 0 && (
                    <Tooltip placement="top"
                        arrow
                        title="Các bản ghi được chọn sẽ được tổng hợp vào bảng lương hiện tại"
                    >
                        <Button
                            startIcon={<GroupWorkIcon />}
                            className="ml-8 btn bgc-lighter-dark-blue d-inline-flex"
                            variant="contained"
                            color="primary"
                            onClick={handleOpenConfirmMergePopup}
                        >
                            Tổng hợp
                        </Button>
                    </Tooltip>
                )
            }

            {openConfirmMerge && (
                <GlobitsConfirmationDialog
                    open={openConfirmMerge}
                    onConfirmDialogClose={handlCloseConfirmMergePopup}
                    onYesClick={handleConfirmMergeToSalaryBoard}
                    title={t("confirm_dialog.delete.title")}
                    text={t("Bạn có chắc muốn tổng hợp các phiếu lương vào bảng lương? Hành động này không thể hoàn tác.")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
        </>
    )
}
export default memo(observer(MergeOrphanedPayslipButton));