import React , { memo , useState } from "react";
import { useFormikContext } from "formik";
import { observer } from "mobx-react";
import { Grid , Button , Tooltip } from "@material-ui/core";
import { pagingStaffWorkSchedule } from "../StaffWorkScheduleV2/StaffWorkScheduleService";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import AvTimerIcon from '@material-ui/icons/AvTimer';
import StaffWorkScheduleStatisticPopup from "./StaffWorkScheduleStatisticPopup";

function StaffWorkScheduleViewStatisticPopup() {

    const {
        values
    } = useFormikContext();

    const [openStatisticPopup , setOpenStatisticPopup] = useState(false);

    function handleOpenStatisticPopup() {
        setOpenStatisticPopup(true);
    }

    function handleCloseStatisticPopup() {
        setOpenStatisticPopup(false);
    }

    return (
        <React.Fragment>
            <Grid container spacing={1}>
                <Grid item xs={9}>
                    <GlobitsPagingAutocomplete
                        label={"Ca làm việc "}
                        name='staffWorkSchedule'
                        searchObject={{
                            fromDate:values?.workingDate ,
                            toDate:values?.workingDate ,
                            staffId:values?.employee ? values?.employee?.id : null ,
                        }}
                        getOptionLabel={(option) => {
                            return `${option?.shiftWork?.name || ""} - ${option?.shiftWork?.code || ""}`;
                        }}
                        api={pagingStaffWorkSchedule}
                        displayData='shiftWork.name'
                        required
                        disabled
                    />
                </Grid>

                <Grid item xs={3} className="flex align-end">
                    <Tooltip
                        arrow
                        title="Xem thống kê ca làm việc"
                        placement="top"
                    >
                        <Button
                            fullWidth
                            variant="contained"
                            className="btn bgc-lighter-dark-blue text-white d-inline-flex "
                            // style={{ marginTop: "25px", }}
                            onClick={() => handleOpenStatisticPopup(true)}
                            // disabled={disabled}
                        >
                            <AvTimerIcon className="text-white"/>
                        </Button>
                    </Tooltip>
                </Grid>
            </Grid>

            {openStatisticPopup && (
                <StaffWorkScheduleStatisticPopup/>
            )}
        </React.Fragment>
    );
}


export default memo(observer(StaffWorkScheduleViewStatisticPopup));


