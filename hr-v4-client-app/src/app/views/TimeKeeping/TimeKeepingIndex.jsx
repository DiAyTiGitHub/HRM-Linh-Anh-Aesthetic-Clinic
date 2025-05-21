/* eslint-disable react-hooks/exhaustive-deps */
import React, { memo, useState } from "react";
import { observer } from "mobx-react";
import { Grid, Button } from "@material-ui/core";
import { toast } from "react-toastify";
import "moment/locale/vi";
import "../TimeSheetDetails/time-sheet-styles.scss";
import { useStore } from "../../stores";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import StaffPopupNotTimeKeeping from "./StaffNotTimeKeeping/StaffPopupNotTimeKeeping";
import { useEffect } from "react";
import { ListWeeks } from "app/LocalConstants";
import { formatDate, getDay, } from "app/LocalFunction";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import CalendarMonth from "app/common/Calendar/CalendarMonth";
import TimeKeepingForm from "./TimeKeepingForm";
import { equalComparisonDate } from '../../LocalFunction'
import { pagingStaffWorkSchedule } from "../StaffWorkScheduleV2/StaffWorkScheduleService";
import TimeKeepingFormV2 from "./TimeKeepingFormV2";

toast.configure({
  autoClose: 2000,
  draggable: false,
  limit: 3,
});

function TimeKeepingIndex(props) {

  const { t } = useTranslation();
  const { id } = useParams();
  const { shiftWorkStore, timeKeepStore, staffWorkScheduleStore } = useStore();

  const {
    getTimeKeepByMonth,
    handleOpenFormTimeKeepV2,
    dataTimeKeep,
    isReload,
    openFormTimeKeep,
    shouldOpenImportDialog,
    setShouldOpenImportDialog,
    shouldOpenImportDialogV2,
    setShouldOpenImportDialogV2,
    getTimeSheetByDate
  } = timeKeepStore;
  const {
    handleChangeFormSearch,
    staffWorkScheduleList
  } = staffWorkScheduleStore;

  const { updatePageData } = shiftWorkStore;
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [dateValue, setDateValue] = useState(currentMonth);
  const [valueCurrentDate, setValueCurrentDate] = useState(null);
  const [shiftWorkList, setShiftWorkList] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        await getTimeSheetByDate({
          workingDate: dateValue,
          staff: {
            id: id
          }
        });
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };
    fetchData();
  }, []);

  // useEffect(() => {
  //   const newShiftWorkList = [];
  //   updatePageData().then((response) => {
  //     response.map((item) => {
  //       return item.timePeriods.map((detail) => {
  //         return newShiftWorkList.push({
  //           shiftWorkTimePeriod: {
  //             ...detail,
  //             shiftWorkDto: {
  //               ...item,
  //             },
  //           },
  //           timeSheet: null,
  //           workingFormat: null,
  //           note: null,
  //         })
  //       });
  //     })
  //   });
  //   setShiftWorkList(newShiftWorkList)
  // }, [updatePageData]);

  useEffect(() => {
    getTimeKeepByMonth(currentMonth, id);
  }, [currentMonth, isReload])

  useEffect(() => {
    if (formatDate('YYYY MM', dateValue) === formatDate('YYYY MM', currentMonth))
      setValueCurrentDate(dataTimeKeep.items.find(e => formatDate('YYYY MM DD', dateValue) === formatDate('YYYY MM DD', e.workingDate)));
  }, [dataTimeKeep]);

  function renderDate(date) {
    const valueDate = dataTimeKeep.items.find(e => formatDate('YYYY MM DD', date) === formatDate('YYYY MM DD', e.workingDate));

    if (valueDate && Array.isArray(valueDate.timeSheetShiftWorkPeriods) && valueDate.timeSheetShiftWorkPeriods.length > 0) {
      return (
        valueDate.timeSheetShiftWorkPeriods.slice().sort((item1, item2) => item1.shiftWorkTimePeriod.startTime - item2.shiftWorkTimePeriod.startTime).map((item, index) => (
          <div key={index}>
            {item.workingFormat === 0 ? (
              <div className="bgc-blue text-white sm-bagde" >
                <span>{'Đi làm(' + formatDate('HH:mm', item?.shiftWorkTimePeriod?.startTime)}-{formatDate('HH:mm', item?.shiftWorkTimePeriod?.endTime) + ')'}</span>
              </div>
            ) : item.workingFormat === 1 ? (
              <div className="bgc-green-d1 text-white sm-bagde">
                <p >{'Làm online(' + formatDate('HH:mm', item?.shiftWorkTimePeriod?.startTime)}-{formatDate('HH:mm', item?.shiftWorkTimePeriod?.endTime) + ')'}</p>
              </div>
            ) : item.workingFormat === 2 ? (
              <div className="bgc-brown text-white sm-bagde">
                <p >{'Đi công tác(' + formatDate('HH:mm', item?.shiftWorkTimePeriod?.startTime)}-{formatDate('HH:mm', item?.shiftWorkTimePeriod?.endTime) + ')'}</p>
              </div>
            ) : (
              <div className="bgc-danger-tp1 text-white sm-bagde" >
                <p>{'Nghỉ(' + formatDate('HH:mm', item?.shiftWorkTimePeriod?.startTime)}-{formatDate('HH:mm', item?.shiftWorkTimePeriod?.endTime) + ')'}</p>
              </div>
            )}
          </div>
        )))
    }
    if (formatDate('YYYY MM DD', new Date()) > formatDate('YYYY MM DD', date)) {
      return (
        <div className="bgc-danger-tp1 text-white sm-bagde"  >
          <p>Nghỉ</p>
        </div>
      )
    }
    return;
  }

  function onClickDate(date) {
    setDateValue(date);
    setValueCurrentDate(dataTimeKeep.items.find(e => formatDate('YYYY MM DD', date) === formatDate('YYYY MM DD', e.workingDate)));
  }

  return (
    <div className="content-index">
      <div className="index-breadcrumb">
        <GlobitsBreadcrumb routeSegments={[{ name: `Điểm danh - ${dataTimeKeep?.staffName ? dataTimeKeep?.staffName : "Admin"}` }]} />
      </div>

      <Grid container spacing={2}>
        <Grid item xs={12} md={3} lg={2} className='value-date-group' style={{ height: "fit-content" }}>
          <p className="text-to-date">{t(ListWeeks.find(e => e.index === getDay(dateValue))?.name)} - {formatDate('DD/MM/YYYY', dateValue)}</p>
          {/* {(valueCurrentDate?.timeSheetShiftWorkPeriods && valueCurrentDate?.timeSheetShiftWorkPeriods?.length > 0) ? (
            valueCurrentDate?.timeSheetShiftWorkPeriods.slice().sort((item1, item2) => item1.shiftWorkTimePeriod.startTime - item2.shiftWorkTimePeriod.startTime).map((item, index) => (
              <div key={index}>
                <div className='item-timesheet' onClick={() => handleOpenFormTimeKeepV2(dateValue)}>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <p className="name-project">{item?.shiftWorkTimePeriod?.shiftWorkDto?.name}</p>
                    {item?.workingFormat === 0 ? (
                      <p className="name-fomart bgc-blue text-white" >Đi làm</p>
                    ) : item?.workingFormat === 1 ? (
                      <p className="name-fomart bgc-green-d1 text-white" >Làm online</p>
                    ) : item?.workingFormat === 2 ? (
                      <p className="name-fomart bgc-brown text-white" >Đi công tác</p>
                    ) : (<p className="name-fomart bgc-danger-tp1 text-white" >Nghỉ</p>)
                    }
                    <p className="time-project">{formatDate('HH:mm', item?.shiftWorkTimePeriod?.startTime)} - {formatDate('HH:mm', item?.shiftWorkTimePeriod?.endTime)}</p>
                  </div>
                </div>
              </div>
            ))
          ) : (
            <div className="text-center">Chưa điểm danh</div>
          )} */}
          <div style={{ justifyItems: "center", display: "grid" }}>
            <Button
              className={`btn btn-info`}
              variant="contained"
              onClick={() => {
                handleOpenFormTimeKeepV2({
                  workingDate: dateValue,
                  staff: {
                    id: id
                  }
                })
              }}
            >
              {equalComparisonDate(new Date(), dateValue) ? "Điểm danh" : !valueCurrentDate ? "Điểm danh bù" : 'Chỉnh sửa'}
            </Button>
          </div>
        </Grid>
        
        <Grid item xs={12} md={9} lg={10}>
          <CalendarMonth
            renderDate={renderDate}
            onChangeDate={(date) => setCurrentMonth(date)}
            onClickDate={onClickDate}
          />
        </Grid>

        {openFormTimeKeep && (
          <TimeKeepingFormV2 />
        )}

        <StaffPopupNotTimeKeeping />
      </Grid>


    </div>
  );
}

export default memo(observer(TimeKeepingIndex));
