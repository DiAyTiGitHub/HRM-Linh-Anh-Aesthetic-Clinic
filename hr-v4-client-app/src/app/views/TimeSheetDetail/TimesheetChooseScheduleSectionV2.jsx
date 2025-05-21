import React, { memo, useEffect, useState } from "react";
import { observer } from "mobx-react";
import { Grid } from "@material-ui/core";
import { pagingStaffWorkSchedule } from "../StaffWorkScheduleV2/StaffWorkScheduleService";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { useFormikContext } from "formik";
import { toast } from "react-toastify";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingShiftWork } from "../ShiftWork/ShiftWorkService";

function TimesheetChooseScheduleSectionV2() {
    const {
        values,
        setFieldValue
    } = useFormikContext();

    const [isChooseShiftMode, setIsChooseShiftMode] = useState(false);

    async function autoFillOneStaffWorkSchedule() {
        const searchObject = {
            fromDate: values?.workingDate,
            toDate: values?.workingDate,
            staffId: values?.staff?.id
        }

        try {
            const { data } = await pagingStaffWorkSchedule(searchObject);
            const listWorkSchedules = data?.content;

            if (listWorkSchedules?.length == 1 && values?.staffWorkSchedule == null) {
                setFieldValue("staffWorkSchedule", listWorkSchedules[0]);
                setIsChooseShiftMode(false);
            }
            else if (listWorkSchedules == null || listWorkSchedules?.length == 0) {
                setIsChooseShiftMode(true);
                toast.info("Hiện chưa có ca làm việc nào được phân trong ngày, vui lòng chọn ca làm việc để phân công và đồng thời chấm công cho nhân viên", {
                    autoClose: 5555,
                });
            }
        }
        catch (error) {
            console.error(error);
        }
    }


    useEffect(function () {
        if (values?.workingDate && values?.staff?.id) {

            autoFillOneStaffWorkSchedule();
        }
    }, [values?.workingDate, values?.staff?.id]);

    return (
        <Grid item xs={12} sm={8}>
            {
                !isChooseShiftMode && (
                    <GlobitsPagingAutocomplete
                        label={"Ca làm việc"}
                        name="staffWorkSchedule"
                        disabled={!values?.staff?.id}
                        searchObject={{
                            fromDate: values?.workingDate,
                            toDate: values?.workingDate,
                            staffId: values?.staff?.id
                        }}
                        api={pagingStaffWorkSchedule}
                        required
                        displayData="shiftWork.name"
                    />
                )
            }

            {
                isChooseShiftMode && (
                    <GlobitsPagingAutocompleteV2
                        name='staffWorkSchedule.shiftWork'
                        label={"Ca làm việc"}
                        api={pagingShiftWork}
                        // required
                        disabled={!values?.staff?.id || !values.workingDate}
                        searchObject={{
                            staffId: values?.staff?.id
                        }}
                        getOptionLabel={(option) =>
                            option?.name && option?.code
                                ? `${option.name} - ${option.code}`
                                : option?.name || option?.code || ""
                        }
                        // readOnly={readOnly || values.staffWorkSchedule?.id}
                    />
                )
            }
        </Grid>
    );
}

export default TimesheetChooseScheduleSectionV2;