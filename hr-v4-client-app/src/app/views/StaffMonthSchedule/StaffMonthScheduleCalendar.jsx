import React, { memo, useState } from 'react'
import { TableCell, Table, TableRow, TableBody, TableHead, Button, ButtonGroup } from "@material-ui/core";
import { formatDate, getDate, getDay, getFullYear, getMonth } from 'app/LocalFunction';
import { LIST_MONTH, LIST_YEAR, ListWeeks } from 'app/LocalConstants';
import { Form, Formik } from 'formik';
import KeyboardArrowLeftIcon from "@material-ui/icons/KeyboardArrowLeft";
import KeyboardArrowRightIcon from "@material-ui/icons/KeyboardArrowRight";
import i18n from 'i18n';
import GlobitsSelectInput from 'app/common/form/GlobitsSelectInput';
import { observer } from 'mobx-react';
import AlarmOnIcon from '@material-ui/icons/AlarmOn';
import Web from '@material-ui/icons/Web';
import { useStore } from 'app/stores';

function StaffMonthScheduleCalendar(props) {
    const {
        defaultDate,
        onClickDate,
        renderDate,
        renderButtonView,
        onChangeDate,
        staffId
    } = props;

    const {
        timeSheetDetailStore,
        hrRoleUtilsStore
    } = useStore();

    const {
        isAdmin,
        isManager,
        isUser
    } = hrRoleUtilsStore;

    const {
        handleOpenFormTimeSheetDetailCheck,
    } = timeSheetDetailStore;

    const {
        handleOpenFormShiftRegristration
    } = useStore().ShiftRegistrationStore;

    const [currentMonth, setCurrentMonth] = useState(defaultDate ? defaultDate : new Date());

    const firstDayOfMonth = new Date(getFullYear(currentMonth), getMonth(currentMonth));

    function handleChangeDateOffRange(date, classDateOffRange) {
        if (onClickDate) {
            onClickDate(date);
        }
        if (classDateOffRange) {
            onChangeDate(date);
            setCurrentMonth(date)
        }
    }

    return (
        <div className="calendar-month">
            <Formik
                enableReinitialize
                initialValues={{
                    month: getMonth(currentMonth) + 1,
                    year: getFullYear(currentMonth),
                }}
            >
                {({ values, setValues }) => {
                    const handleChangeMonth = (date, isChangeToday) => {

                        setCurrentMonth(date);
                        setValues({
                            month: getMonth(date) + 1,
                            year: getFullYear(date)
                        });

                        if (onChangeDate) {
                            onChangeDate(date);
                        }
                        if (isChangeToday) {
                            onClickDate(date);
                        }
                    }

                    return (
                        <Form className='calendar-form flex space-between'>
                            <div className="flex justify-between align-center w-100">
                                <div className="btn-group">
                                    <button onClick={() => handleChangeMonth(new Date(getFullYear(currentMonth), values.month - 2))} className='btn-calendar'>
                                        <KeyboardArrowLeftIcon />
                                    </button>
                                    <button onClick={() => handleChangeMonth(new Date(getFullYear(currentMonth), values.month))} className='btn-calendar'>
                                        <KeyboardArrowRightIcon />
                                    </button>

                                    <button onClick={() => handleChangeMonth(new Date(), true)} className='btn-calendar ml-8 px-4'>
                                        Hôm nay
                                    </button>


                                </div>

                                {staffId && (
                                    <ButtonGroup
                                        // className='ml-8'
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            startIcon={<AlarmOnIcon />}
                                            onClick={() => {
                                                handleOpenFormTimeSheetDetailCheck();
                                            }}
                                        >
                                            Chấm công
                                        </Button>

                                        {/* <Button
                                            startIcon={<Web />}
                                            onClick={() => {
                                                handleOpenFormShiftRegristration();
                                            }}
                                        >
                                            Đăng ký ca làm
                                        </Button> */}
                                    </ButtonGroup>
                                )}


                                <div className='flex items-center gap-1'>
                                    <div style={{ width: 120 }}>
                                        <GlobitsSelectInput
                                            name='month'
                                            options={LIST_MONTH}
                                            handleChange={({ target }) => handleChangeMonth(new Date(getFullYear(currentMonth), target.value - 1))}
                                        />
                                    </div>

                                    <div style={{ width: 130 }}>
                                        <GlobitsSelectInput
                                            name='year'
                                            options={LIST_YEAR}
                                            handleChange={({ target }) => handleChangeMonth(new Date(currentMonth).setFullYear(target.value))}
                                        />
                                    </div>
                                </div>
                            </div>

                            {renderButtonView ? renderButtonView() : (<span></span>)}
                        </Form>
                    )
                }}
            </Formik>

            <Table className='mt-10'>
                <TableHead>
                    <TableRow>
                        {ListWeeks.map((item, index) => (
                            <TableCell className="weeks-header" key={index}>{i18n.t(item.name)}</TableCell>
                        ))}
                    </TableRow>
                </TableHead>

                <TableBody>
                    {Array.from({ length: 6 }).map((_, index) => {
                        const day = getDay(firstDayOfMonth)
                        const startDate = new Date(firstDayOfMonth).setDate(2 - (day === 0 ? 7 : day) + index * 7);

                        if (index >= 5 && getMonth(startDate) !== getMonth(currentMonth)) {
                            return <React.Fragment key={index}></React.Fragment>
                        }

                        return (
                            <TableRow key={index}>
                                {[...new Array(7)].map((_, index) => {
                                    // const date = new Date(startDate).setDate(getDate(startDate) + index);
                                    const date = new Date(startDate).setDate(new Date(startDate).getDate() + index);

                                    const classDateToDay = new Date().toDateString() === new Date(date).toDateString() ? 'today-date' : '';
                                    const classDateOffRange = getMonth(currentMonth) !== getMonth(date) ? 'date-off-range' : '';

                                    return (
                                        <TableCell
                                            key={index}
                                            className={["date", classDateToDay, classDateOffRange, "p-2"].join(" ")}
                                            onClick={() => handleChangeDateOffRange(date, classDateOffRange)}
                                        >
                                            <p
                                                // style={{ fontSize: "10px" }}
                                                className='font-size-13'
                                            >
                                                {formatDate('DD', date)}
                                            </p>
                                            {(!classDateOffRange && renderDate) ? renderDate(date) : <></>}
                                        </TableCell>
                                    )
                                })}
                            </TableRow>
                        )
                    })}
                </TableBody>
            </Table>


        </div>
    )
}

export default memo(observer(StaffMonthScheduleCalendar));