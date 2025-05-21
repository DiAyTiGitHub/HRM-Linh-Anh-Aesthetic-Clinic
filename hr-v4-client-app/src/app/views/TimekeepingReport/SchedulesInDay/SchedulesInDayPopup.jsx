import {
    Button,
    DialogActions,
    DialogContent,
    Tooltip
} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import SchedulesInDayList from "./SchedulesInDayList";

function SchedulesInDayPopup(props) {
    const { t } = useTranslation();

    const {
        pagingAfterEdit,
        readOnly = false

    } = props;

    const {
        timekeepingReportStore

    } = useStore();

    const {
        handleClose,
        openScheduleInDayPopup
    } = timekeepingReportStore;


    return (
        <GlobitsPopupV2
            scroll={"body"}
            size='md'
            open={openScheduleInDayPopup}
            title={"Thông tin ca làm việc được phân"}
            noDialogContent
            onClosePopup={handleClose}
        >

            <DialogContent className='o-hidden dialog-body p-12'>
                <SchedulesInDayList />
            </DialogContent>

            <DialogActions className='dialog-footer flex flex-end flex-middle px-12'>
                <Tooltip arrow title='Đóng' placement='bottom'>
                    <Button
                        startIcon={<BlockIcon />}
                        className='btn btn-secondary d-inline-flex'
                        onClick={handleClose}
                    >
                        {t("general.button.cancel")}
                    </Button>
                </Tooltip>
            </DialogActions>
        </GlobitsPopupV2>
    );
}

export default memo(observer(SchedulesInDayPopup));

