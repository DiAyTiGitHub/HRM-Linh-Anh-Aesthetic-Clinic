/* eslint-disable react-hooks/exhaustive-deps */
import React , { memo , useEffect , useState } from "react";
import { observer } from "mobx-react";
import { Grid } from "@material-ui/core";
import { toast } from "react-toastify";
import "moment/locale/vi";
import "../TimeSheetDetails/time-sheet-styles.scss";
import { useStore } from "../../stores";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import { formatDate , getFullYear , getMonth , } from "app/LocalFunction";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import StaffMonthScheduleCalendar from "./StaffMonthScheduleCalendar";
import MonthCalendarTicket from "./MonthCalendarTicket";
import StaffWorkScheduleV2CUForm from "../StaffWorkScheduleV2/StaffWorkScheduleV2CUForm";
import localStorageService from "app/services/localStorageService";
import TimeSheetDetailCheckFormV2 from "../TimeSheetDetail/TimeSheetDetailCheckFormV2";
import ShiftChangeRequestForm from "./ShiftChangeRequestForm";
import ShiftRegistrationFormPopup from "./ShiftRegistrationFormPopup";
import StaffWorkScheduleStatisticPopup from "../StaffWorkScheduleV2/StaffWorkScheduleStatisticPopup";

toast.configure({
    autoClose:2000 ,
    draggable:false ,
    limit:3 ,
});

function StaffMonthScheduleIndex(props) {
    const {t} = useTranslation();
    const {staffId} = useParams();

    const {
        staffWorkScheduleCalendarStore ,
        staffWorkScheduleStore ,
        timeSheetDetailStore ,
        ShiftRegistrationStore ,
        hrRoleUtilsStore ,
        shiftChangeRequestStore
    } = useStore();

    const {
        getWorkCalendarOfStaff ,
        handleSetSearchObject ,
        searchObject ,
        resetStore ,
        scheduledStaffCalendar ,
    } = staffWorkScheduleCalendarStore;
    const {openCreateEditPopup:shouldEditShiftChangeRequest} = shiftChangeRequestStore;
    const {
        checkAllUserRoles ,
        checkHasShiftAssignmentPermission
    } = hrRoleUtilsStore;
    const {
        openViewStatistic ,
    } = staffWorkScheduleStore;

    const {
        handleOpenFormTimeSheetDetailCheck ,
        openFormTimeSheetDetailCheck ,
        handleSaveTimeSheet
    } = timeSheetDetailStore;

    const {
        openFormShiftRegristration
    } = ShiftRegistrationStore;

    const [currentMonth , setCurrentMonth] = useState(new Date());
    const [dateValue , setDateValue] = useState(currentMonth);
    const [valueCurrentDate , setValueCurrentDate] = useState(null);
    const [shiftWorkList , setShiftWorkList] = useState([]);

    useEffect(function () {
        checkAllUserRoles();
        checkHasShiftAssignmentPermission();

        return resetStore();
    } , []);

    const {
        openCreateEditPopup ,
        openViewPopup ,
    } = staffWorkScheduleStore;

    const [id , setId] = useState(staffId);
    useEffect(() => {
        const loginUser = localStorageService.getLoginUser()
        setId(staffId ? staffId : loginUser?.id)
    } , [staffId]);
    useEffect(function () {
        const newSearchObject = {
            ... searchObject ,
            staffId:id ,
            staff:{
                id:id
            } ,
            chosenMonth:getMonth(currentMonth) + 1 ,
            chosenYear:getFullYear(currentMonth) ,
        };

        handleSetSearchObject(newSearchObject);

        getWorkCalendarOfStaff();
    } , [id , currentMonth]);


    useEffect(() => {
        if (formatDate('YYYY MM' , dateValue) === formatDate('YYYY MM' , currentMonth))
            setValueCurrentDate(scheduledStaffCalendar?.workingSchedules?.find(e => formatDate('YYYY MM DD' , dateValue) === formatDate('YYYY MM DD' , e?.workingDate)));
    } , [scheduledStaffCalendar?.workingSchedules]);

    function renderDate(date) {
        // console.log("date", date);
        // console.log("formatDate('YYYY MM DD', date)", formatDate('YYYY MM DD', date));

        const displayItem = scheduledStaffCalendar?.workingSchedules?.find(e => formatDate('YYYY MM DD' , date) === formatDate('YYYY MM DD' , e?.workingDate));

        if (!displayItem) return;

        const {
            workingDate ,
            shiftWorks ,
        } = displayItem;
        return (
            <div className="flex flex-column justify-center text-center">
                {
                    shiftWorks?.map(function (shiftWork , index) {
                        return (
                            <MonthCalendarTicket
                                key={shiftWork?.id}
                                workingDate={workingDate}
                                shiftWork={shiftWork}
                                readOnly={openViewPopup}
                                staffId={id}
                            />
                        );
                    })
                }

            </div>
        );
    }

    function onClickDate(date) {
        setDateValue(date);
        setValueCurrentDate(scheduledStaffCalendar?.workingSchedules?.find(e => formatDate('YYYY MM DD' , date) === formatDate('YYYY MM DD' , e?.workingDate)));
    }

    return (
        <div className="content-index">
            <div className="index-breadcrumb py-6">
                <GlobitsBreadcrumb
                    routeSegments={[{name:`Lịch làm việc - ${scheduledStaffCalendar?.displayName ? scheduledStaffCalendar?.displayName + " - " + scheduledStaffCalendar?.staffCode : "Admin"}`}]}/>
            </div>

            <Grid
                container
                spacing={2}
                className="index-card"
            >
                {/* <Grid item xs={12} md={3} lg={2} className='value-date-group' style={{ height: "fit-content" }}>
          <p className="text-to-date">{t(ListWeeks.find(e => e.index === getDay(dateValue))?.name)} - {formatDate('DD/MM/YYYY', dateValue)}</p>

          <div style={{ justifyItems: "center", display: "grid" }}>
            <Button
              className={`btn btn-info`}
              variant="contained"
              onClick={() => {
                handleOpenFormTimeKeepV2({
                  workingDate: dateValue,
                  staff: {
                    id: staffId
                  }
                })
              }}
            >
              {equalComparisonDate(new Date(), dateValue) ? "Điểm danh" : !valueCurrentDate ? "Điểm danh bù" : 'Chỉnh sửa'}
            </Button>
          </div>
        </Grid> */}

                {/* <Grid item xs={12} md={9} lg={10}> */}
                <Grid item xs={12}>
                    <StaffMonthScheduleCalendar
                        renderDate={renderDate}
                        onChangeDate={(date) => setCurrentMonth(date)}
                        onClickDate={onClickDate}
                        staffId={id}
                    />
                </Grid>
            </Grid>

            {openFormTimeSheetDetailCheck && (
                <TimeSheetDetailCheckFormV2
                    handleSumbit={async (values) => {
                        await handleSaveTimeSheet(values)
                        await getWorkCalendarOfStaff()
                    }}
                    staffId={staffId}
                />
            )}

            {openFormShiftRegristration && (
                <ShiftRegistrationFormPopup
                    staffId={staffId}
                />
            )}

            {openCreateEditPopup && (
                <StaffWorkScheduleV2CUForm
                    pagingAfterEdit={getWorkCalendarOfStaff}
                />
            )}

            {openViewPopup && (
                <StaffWorkScheduleV2CUForm
                    readOnly={openViewPopup}
                />
            )}

            {openViewStatistic && (<StaffWorkScheduleStatisticPopup/>)}

            {shouldEditShiftChangeRequest && (<ShiftChangeRequestForm/>)}
        </div>
    );
}

export default memo(observer(StaffMonthScheduleIndex));
