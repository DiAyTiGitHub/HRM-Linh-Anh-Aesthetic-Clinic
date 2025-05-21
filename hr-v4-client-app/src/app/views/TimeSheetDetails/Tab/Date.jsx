import React, { useEffect, useState } from "react";
import { observer } from "mobx-react";
import { toast } from "react-toastify";
import { TableCell, Table, TableRow, TableBody, TableHead } from "@material-ui/core";
import { useStore } from "app/stores";
import { ListWeeks } from "app/LocalConstants";
import { formatDate, getDate, getDay, getFullYear, getMinutes } from "app/LocalFunction";
import { RenderButtonNextDate } from "../TimeSheetDetailsIndex";
import { getMonth } from "date-fns";
import { useTranslation } from "react-i18next";

toast.configure({
    autoClose: 2000,
    draggable: false,
    limit: 3,
});

export default observer(function Dates({ renderButtonView, staffId }) {

    const { t } = useTranslation();    
    const { timeSheetDetailsStore } = useStore();
    const { getTimeSheetDay, listTimeSheetDay, handleOpenFormTimeSheet } = timeSheetDetailsStore;

    const [currentDate, setCurrentDate] = useState(new Date());

    useEffect(() => {
        getTimeSheetDay(currentDate, staffId)
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [currentDate, staffId]);

    function renderCalendarBody() {
        let flag = false;

        return (
            <TableBody>
                {Array.from({ length: 24 }).map((item, index) => {
                    const valueHours = (listTimeSheetDay && listTimeSheetDay.length > 0) ? listTimeSheetDay.filter(e => formatDate('HH', e.startTime) === formatDate('HH', new Date().setHours(index))) : [];
                    return (
                        <TableRow>
                            <TableCell className="calendar-hours-title" style={{ verticalAlign: 'center', width: '60px', height: 70 }} key={index} id={index + 'hours'}>
                                <p>{index < 10 ? '0' + index : index}</p>
                            </TableCell>
                            <TableCell style={{ border: '2px solid #dee2e6', position: 'relative' }}>
                                <div style={{ height: '2px', backgroundColor: '#dee2e6' }}></div>
                                {valueHours.length > 0 && (
                                    <>
                                        {valueHours.map((value, num) => {
                                            if (!flag) {
                                                document.getElementById(index + 'hours').scrollIntoView();
                                                flag = true;
                                            }
                                            return (
                                                <div
                                                    key={num}
                                                    style={{
                                                        position: 'absolute',
                                                        width: `${100 / valueHours.length}%`,
                                                        top: `${getMinutes(value.startTime) / 60 * 100}%`,
                                                        left: `${100 / valueHours.length * num}%`,
                                                        height: `${88 * value.duration}px`,
                                                        zIndex: 1,
                                                        padding: '1px 1px 2px',
                                                        cursor: 'pointer'
                                                    }}
                                                    onClick={() => { handleOpenFormTimeSheet(value?.id) }}
                                                >
                                                    <div style={{ background: '#5899ca', width: '100%', height: '100%', fontSize: 15, color: 'white', padding: 5 }}>
                                                        <span>{formatDate('hh:mm', value.startTime)} - {formatDate('HH:mm', value.endTime)}</span>
                                                        <p>{value.activity}</p>
                                                    </div>
                                                </div>
                                            )
                                        })}
                                    </>
                                )}
                            </TableCell>
                        </TableRow>
                    )
                })}
            </TableBody>
        )
    }

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: "5px" }}>
                <RenderButtonNextDate
                    preDate={() => setCurrentDate(new Date(getFullYear(currentDate), getMonth(currentDate), getDate(currentDate) - 1))}
                    nextDate={() => setCurrentDate(new Date(getFullYear(currentDate), getMonth(currentDate), getDate(currentDate) + 1))}
                    setDate={setCurrentDate}
                />
                <p style={{ fontSize: 25, margin: 0 }}>{formatDate("DD-MM-YYYY", currentDate)} </p>
                {renderButtonView()}
            </div>
            <Table className="table-calendar">
                <TableHead className="calendar-header">
                    <TableRow>
                        <TableCell>{t(ListWeeks.find(e => e.index === getDay(currentDate)).name)}</TableCell>
                    </TableRow>
                </TableHead>
            </Table>
            <div className="calendar-body">
                <Table className="table-calendar">
                    {renderCalendarBody()}
                </Table>
            </div>
        </div>
    );
});
