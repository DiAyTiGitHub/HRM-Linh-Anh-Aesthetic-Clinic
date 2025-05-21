import React, { useState } from "react";
import { observer } from "mobx-react";
import { toast } from "react-toastify";
import { TableCell, Table, TableRow, TableBody, TableHead, } from "@material-ui/core";
import { Form, Formik } from "formik";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput"; 
import LocalConstants, { ListWeeks } from "app/LocalConstants";
import { useEffect } from "react";
import { useStore } from "../../../stores";
import { formatDate, getDate, getDay, getFullYear, getMonth } from "app/LocalFunction";
import { RenderButtonNextDate } from "../TimeSheetDetailsIndex";
import { useTranslation } from "react-i18next";

toast.configure({
    autoClose: 2000, 
    draggable: false,
    limit: 3,
});

const ListYear = Array.from({ length: 7 }).map((item, index) => ({
    name: (new Date().getFullYear() - index) + '',
    value: new Date().getFullYear() - index,
}))

export default observer(function Weeks({ renderButtonView, renderButtonNextDate, staffId }) {
    const { t } = useTranslation();    
    const { timeSheetDetailsStore } = useStore();
    const { getTimeSheetWeeks, listTimeSheetWeeks, handleOpenFormTimeSheet } = timeSheetDetailsStore;
    const [currentWeeks, setCurrentWeeks] = useState(new Date());

    const startDate = new Date(getFullYear(currentWeeks), getMonth(currentWeeks), getDate(currentWeeks) - ListWeeks.find(e => e.index === getDay(currentWeeks)).valueWeeks);

    function handleChangeMonth(month, setValues) {
        const date = new Date(getFullYear(currentWeeks), month - 1);

        setCurrentWeeks(new Date(date));
        setValues({
            month: getMonth(date) + 1,
            year: getFullYear(date)
        })
    }

    function handleChangeWeeks(date, setValues) {
        const newDate = new Date(getFullYear(currentWeeks), getMonth(currentWeeks), getDate(currentWeeks) + date);;
        setCurrentWeeks(newDate);
        setValues({
            month: getMonth(newDate) + 1,
            year: getFullYear(newDate)
        })
    }

    useEffect(() => {
        getTimeSheetWeeks(startDate, new Date(getFullYear(startDate), getMonth(startDate), getDate(startDate) + 6), staffId)
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [currentWeeks, staffId]);


    function renderCalendarBody() {
        let flag = false;
        return (
            <TableRow>
                <TableCell className="calendar-table-cell" style={{ width: '60px' }}>
                    {Array.from({ length: 24 }).map((item, index) => {
                        return (
                            <div className="calendar-hours-title" key={index}>{index}</div>
                        )
                    })}
                </TableCell>

                {[...new Array(7)].map((e, index) => {
                    const valueDate = Array.isArray(listTimeSheetWeeks) ? listTimeSheetWeeks.filter(e => formatDate('YYYY MM DD', new Date(startDate).setDate(getDate(startDate) + index)) === formatDate('YYYY MM DD', e.workingDate)) : [];

                    return (
                        <TableCell key={index} className="calendar-table-cell">
                            {Array.from({ length: 24 }).map((_, number) => {
                                const valueHours = valueDate.length > 0 ? valueDate.filter(e => formatDate('HH', e.startTime) === formatDate('HH', new Date().setHours(number))) : [];

                                return (
                                    <div className="calendar-item-parent" key={number} id={number + 'hours'}>
                                        <div></div>
                                        {valueHours.length > 0 && valueHours.map((value, num) => {
                                            if (!flag) {
                                                var curElement = document.getElementById(number + 'hours');
                                                if (curElement != null) curElement.scrollIntoView();
                                                flag = true;
                                            }
                                            return (
                                                <div
                                                    key={num}
                                                    className="calendar-item-children"
                                                    style={{
                                                        width: `${100 / valueHours.length}%`,
                                                        top: `${new Date(value.startTime).getMinutes() / 60 * 100}%`,
                                                        left: `${100 / valueHours.length * num}%`,
                                                        height: `${70 * value.duration}px`,
                                                        cursor: 'pointer'
                                                    }}
                                                    onClick={() => { handleOpenFormTimeSheet(value.id) }}
                                                >
                                                    <div >
                                                        <span>{formatDate('HH:mm', value.startTime)} - {formatDate('HH:mm', value.endTime)}</span>
                                                        <p>{value.activity}</p>
                                                    </div>
                                                </div>
                                            )
                                        })}
                                    </div>
                                )
                            })}
                        </TableCell>
                    )
                })}
            </TableRow>
        )
    }

    return (
        <div id="calendar-weeks">
            <Formik
                initialValues={{
                    month: getMonth(currentWeeks) + 1,
                    year: getFullYear(currentWeeks),
                }}
            >
                {({ values, setFieldValue, setValues }) => {
                    return (
                        <Form className="form-calendar">
                            <RenderButtonNextDate
                                preDate={() => handleChangeWeeks(-7, setValues)}
                                nextDate={() => handleChangeWeeks(7, setValues)}
                                setDate={setCurrentWeeks}
                            />
                            <div style={{ fontSize: 25, margin: 0, display: 'flex' }}>
                                <span style={{ margin: "0px 5px" }}> {t("general.month")}</span>
                                <div style={{ width: '120px' }}>
                                    <GlobitsSelectInput
                                        name={'month'}
                                        options={LocalConstants.ListMonth}
                                        handleChange={({ target }) => handleChangeMonth(target.value, setValues)}
                                    />
                                </div>
                                <span style={{ margin: "0px 5px" }}> {t("general.year")}</span>
                                <div style={{ width: '120px' }}>
                                    <GlobitsSelectInput
                                        name={'year'}
                                        options={ListYear}
                                        handleChange={({ target }) => {
                                            setFieldValue('year', target.value)
                                            setCurrentWeeks(new Date(currentWeeks.setFullYear(target.value)))
                                        }}
                                    />
                                </div>
                            </div>
                            {renderButtonView()}
                        </Form>
                    )
                }}
            </Formik>
            <p className="text-to-date ">
                {formatDate("DD/MM/YYYY", startDate)} - {formatDate("DD/MM/YYYY", new Date(getFullYear(startDate), getMonth(startDate), getDate(startDate) + 6))}
            </p>

            <Table className="table-calendar">
                <TableHead className="calendar-header">
                    <TableRow>
                        <TableCell style={{ width: '60px' }}></TableCell>
                        {Array.from({ length: 7 }).map((item, index) => {
                            const date = new Date(getFullYear(startDate), getMonth(startDate), getDate(startDate) + index);
                            return (
                                <TableCell key={index}>
                                    {formatDate('DD/MM', date)} - {t(ListWeeks[index].name)}
                                </TableCell>
                            )
                        })}
                    </TableRow>
                </TableHead>
            </Table>
            <div className="calendar-body">
                <Table className="table-calendar">
                    <TableBody>
                        {renderCalendarBody()}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
});
