import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import SalaryValueHistoriesList from "./SalaryValueHistoriesList";

function SalaryValueHistoriesPopup(props) {
    const {
        hasStaff = false,
        readOnly = false

    } = props;

    const {
        staffSalaryItemValueStore,

    } = useStore();

    const { t } = useTranslation();

    const {
        handleClose,
        openValueHitoriesPopup,
        getValueHistoryStaffName,
        getValueHistorySalaryItemName
    } = staffSalaryItemValueStore;

    const staffName = getValueHistoryStaffName();
    const salaryItemName = getValueHistorySalaryItemName();

    return (
        <GlobitsPopupV2
            size="sm"
            scroll={"body"}
            open={openValueHitoriesPopup}
            noDialogContent
            title={"Lịch sử giá trị lương nhân viên"}
            onClosePopup={handleClose}
        >
            <DialogContent className="o-hidden p-12">
                <Grid container spacing={2}>

                    {salaryItemName && staffName && (
                        <Grid item xs={12} className="pb-0">
                            <p className="m-0 p-0 borderThrough2">
                                Lịch sử giá trị lương
                                <strong>
                                    {` ${salaryItemName} `}
                                </strong>
                                của nhân viên
                                <strong>
                                    {` ${staffName}`}
                                </strong>
                            </p>
                        </Grid>
                    )}


                    <Grid item xs={12}>
                        <SalaryValueHistoriesList />
                    </Grid>
                </Grid>
            </DialogContent>

            <DialogActions className="dialog-footer px-12">
                <div className="flex flex-space-between flex-middle">
                    <Button
                        variant="contained"
                        className={`${!readOnly} btn-secondary d-inline-flex`}
                        startIcon={<BlockIcon />}
                        color="secondary"
                        onClick={handleClose}
                    >
                        {t("general.button.close")}
                    </Button>
                </div>
            </DialogActions>
        </GlobitsPopupV2>);
}

export default memo(observer(SalaryValueHistoriesPopup));
