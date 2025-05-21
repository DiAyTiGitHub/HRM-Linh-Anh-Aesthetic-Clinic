import React, { memo, useState } from "react";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { Grid, Button, DialogContent, Tooltip } from "@material-ui/core";
import { pagingStaffWorkSchedule } from "./StaffWorkScheduleService";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import TouchAppIcon from '@material-ui/icons/TouchApp';
import StaffWorkScheduleSummary from "./StaffWorkScheduleSummary";
import AvTimerIcon from '@material-ui/icons/AvTimer';
import { useStore } from "app/stores";

function ViewStatisticPopup() {

    const {
        values
    } = useFormikContext();

    const {
        staffWorkScheduleStore
    } = useStore();

    const {
        handleOpenViewStatistic,
        openViewStatistic,
        handleCloseViewStatisticPopup
    } = staffWorkScheduleStore;

    return (
        <React.Fragment>

            <Tooltip
                arrow
                title="Xem thống kê ca làm việc"
                placement="top"
            >
                <Button
                    fullWidth
                    variant="contained"
                    className="btn bgc-lighter-dark-blue text-white d-inline-flex"
                    onClick={() => handleOpenViewStatistic(values?.staffWorkSchedule?.id)}
                >
                    <AvTimerIcon className="text-white" />
                </Button>
            </Tooltip>

            {openViewStatistic && (
                <StatisticPopup
                    openViewPopup={openViewStatistic}
                    handleClose={handleCloseViewStatisticPopup}
                />
            )}
        </React.Fragment>
    );
}


export default memo(observer(ViewStatisticPopup));


function StatisticPopup(props) {
    const {
        openViewPopup,
        handleClose,
    } = props;

    return (
        <GlobitsPopupV2
            popupId={"StatisticPopup"}
            scroll={"body"}
            size='sm'
            open={openViewPopup}
            title={"Thống kê kết quả ca làm việc"}
            noDialogContent
            onClosePopup={handleClose}
        >
            <DialogContent className='o-hidden dialog-body p-12'>

                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <StaffWorkScheduleSummary
                        />
                    </Grid>
                </Grid>

            </DialogContent>

        </GlobitsPopupV2>
    );
}

