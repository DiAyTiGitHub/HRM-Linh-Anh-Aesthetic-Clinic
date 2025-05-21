import React from "react";
import { observer } from "mobx-react";
import { useStore } from "../../../stores";
import { useEffect } from "react";
import { Button, Grid, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import { equalComparisonDate, formatDate, getDay } from "app/LocalFunction";
import DeleteIcon from '@material-ui/icons/Delete';
import { useTranslation } from "react-i18next";
import { withTranslation } from 'react-i18next';
import CalendarMonth from "app/common/Calendar/CalendarMonth";
import { ListWeeks } from "app/LocalConstants";

export default withTranslation()(observer(function Month({ renderButtonView, staffId }) {

    const { t } = useTranslation();
    const { timeSheetDetailsStore, } = useStore();
    const {
        listTimeSheetMonth,
        valueCurrentMonth,
        currentMonth,
        dateValueMonth,
        getTimeSheetMonth,
        handleOpenFormTimeSheet,
        handleDeleteTimeSheet,
        setCurrentMonth,
        setValueCurrentMonth,
        setDateValueMonth
    } = timeSheetDetailsStore;

    useEffect(() => {
        getTimeSheetMonth(staffId);
    }, [currentMonth, getTimeSheetMonth, staffId]);

    useEffect(() => {
        if (Array.isArray(listTimeSheetMonth) && listTimeSheetMonth.length > 0 && equalComparisonDate(currentMonth, dateValueMonth, 'month')) {
            const valueDate = listTimeSheetMonth.filter(e => equalComparisonDate(dateValueMonth, e.workingDate));
            setValueCurrentMonth(valueDate);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [listTimeSheetMonth]);

    function onClickDate(date) {
        const valueDate = Array.isArray(listTimeSheetMonth) ? listTimeSheetMonth.filter(e => equalComparisonDate(date, e.workingDate)) : [];
        setValueCurrentMonth(valueDate);
        setDateValueMonth(date)
    }

    
    // console.log("listTimeSheetMonth", listTimeSheetMonth)

    // function renderDate(date) {
    //     const valueDate = Array.isArray(listTimeSheetMonth) ? listTimeSheetMonth.filter(e => equalComparisonDate(date, e.workingDate)) : [];
    //     console.log("on render valueDate: ", valueDate);

    //     return (Array.isArray(valueDate) && valueDate.slice().sort((item1, item2) => item1.startTime - item2.startTime).map((value, index) => (
    //         <p style={{ backgroundColor: "lavender", width: "max-content", padding: "4px", margin: "5px", borderRadius: "7px" }} key={index}>
    //             <span>{formatDate('HH:mm', value.startTime)} - {formatDate('HH:mm', value.endTime)}</span>
    //         </p>
    //     )))
    // }

    function renderDate(date) {
        if (!Array.isArray(listTimeSheetMonth)) {
            // console.log("listTimeSheetMonth is not an array:", listTimeSheetMonth);
            return null;
        }

        const valueDate = listTimeSheetMonth.filter(e => {
            const isEqual = equalComparisonDate(date, e.workingDate);
            // console.log("Comparing dates:", date, e.workingDate, "Result:", isEqual);
            return isEqual;
        });

        // console.log("Filtered valueDate:", valueDate);

        return valueDate.slice().sort((item1, item2) => item1.startTime - item2.startTime).map((value, index) => (
            <p
                style={{
                    backgroundColor: "lavender",
                    width: "max-content",
                    padding: "4px",
                    margin: "5px",
                    borderRadius: "7px"
                }}
                key={index}
            >
                <span>{formatDate('HH:mm', value.startTime)} - {formatDate('HH:mm', value.endTime)}</span>
            </p>
        ));
    }

    return (
        <Grid className="index-card" container spacing={2}>
            <Grid item xs={12} lg={3} className='value-date-group'>
                <p className="text-to-date">{t(ListWeeks.find(e => e.index === getDay(dateValueMonth)).name)} - {formatDate('DD-MM-YYYY', dateValueMonth)}</p>
                {Array.isArray(valueCurrentMonth) ? (valueCurrentMonth?.map((item, index) => (
                    <div key={index} className='item-timesheet' onClick={() => handleOpenFormTimeSheet(item?.id)}>
                        <div className="flex align-center justify-between">
                            <p className="time-project py-4">{formatDate('HH:mm', item?.startTime)} - {formatDate('HH:mm', item?.endTime)}</p>
                            {/* <p className="name-project ">{item?.project}</p> */}

                            <div>
                                <Tooltip placement="top" title="Xóa nhật kí">
                                    <DeleteIcon className="text-red font-size-20" onClick={(event) => { handleDeleteTimeSheet(item); event.stopPropagation() }} />
                                </Tooltip>
                            </div>
                        </div>

                        {/* <p className="activity-project" style={{ textAlign: "center", fontSize: "20px" }}><strong>{item?.activity}</strong></p> */}

                        <p dangerouslySetInnerHTML={{ __html: item?.description }}></p>

                    </div>
                ))) : (
                    <div style={{ textAlign: "center" }}>{t("timeSheet.noTimeSheet")}</div>
                )}
                <div style={{ justifyItems: "center", display: "grid" }}>
                    <Button
                        className="btn btn-info d-inline-flex"
                        startIcon={<AddIcon />}
                        onClick={() => handleOpenFormTimeSheet()}
                    >
                        {t("timeSheet.addTimeSheet")}
                    </Button>
                </div>
            </Grid>
            <Grid item xs={12} lg={9} >
                <CalendarMonth
                    renderDate={renderDate}
                    onChangeDate={(date) => setCurrentMonth(date)}
                    renderButtonView={renderButtonView}
                    onClickDate={onClickDate}
                />
            </Grid>
        </Grid >
    );
}))