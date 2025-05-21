import React, { memo, useEffect, useMemo, useState } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip, Checkbox, Menu, MenuItem, Button, Grid } from "@material-ui/core";
import { observer } from "mobx-react";
import CheckIcon from '@material-ui/icons/Check';
import { Radio } from "@material-ui/core";
import { formatDate, formatVNDMoney, getCheckInAndCheckOutTimeOfShiftWork, getDate } from "app/LocalFunction";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import LocalConstants from "app/LocalConstants";
import { CloseOutlined, PauseCircleOutline } from "@material-ui/icons";
import { useHistory } from "react-router-dom";
import ConstantList from "app/appConfig";
import InsertInvitationIcon from "@material-ui/icons/InsertInvitation";
import AvTimerIcon from '@material-ui/icons/AvTimer';
import { NavLink } from "react-router-dom/cjs/react-router-dom";


function SchedulesInDayList(props) {
    const {
        isOnChoosingMode,
        selectedItem,
        handleChooseItem
    } = props;

    const { t } = useTranslation();

    const {
        hrRoleUtilsStore,
        timekeepingReportStore,
        staffWorkScheduleStore

    } = useStore();

    const {
        schedulesInDayList,
    } = timekeepingReportStore;

    const {
        handleOpenViewStatistic,
        handleDelete
    } = staffWorkScheduleStore;

    const {
        isAdmin,
        isManager,

    } = hrRoleUtilsStore;


    let columns = [
        {
            title: t("general.action"),
            width: "10%",
            align: "center",
            render: (rowData) => (
                <div className="flex align-center justify-center">
                    <Tooltip
                        arrow
                        placement="top"
                        title={"Thống kê kết quả làm việc"}
                    >
                        <IconButton
                            className="ml-4"
                            size="small"
                            onClick={
                                function () {
                                    handleOpenViewStatistic(rowData?.id)
                                }
                            }
                        >
                            <Icon fontSize="small" style={{ color: "13529f" }}>
                                av_timer
                            </Icon>
                        </IconButton>
                    </Tooltip>


                    {(isAdmin || isManager) && (
                        <Tooltip
                            arrow
                            placement="top"
                            title={"Xóa thông tin lần phân ca"}
                        >
                            <IconButton
                                className="ml-4"
                                size="small"
                                onClick={
                                    function () {
                                        handleDelete(rowData);
                                    }
                                }
                            >
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                    )}
                </div >
            ),
        },
        {
            title: "Mã nhân viên",
            field: "staff.staffCode",
            align: "left",
        },

        {
            title: "Nhân viên",
            field: "staff",
            align: "left",
            render: (row) => (
                <span>
                    {row?.staff?.displayName}
                    {row?.staff?.staffCode && (
                        <>
                            <br />
                            ({row.staff.staffCode})
                        </>
                    )}
                </span>
            ),
            minWidth: "120px",
        },

        {
            title: "Ngày làm việc",
            field: "workingDate",
            align: "left",
            render: row => <span className="px-2">{getDate(row?.workingDate)}</span>
        },

        {
            title: "Ca làm việc",
            align: "left",
            field: "shiftWork.name",
            // render: row => <span className="px-2">{`${row?.shiftWork?.name} `}</span>
            render: (rowData) => {
                const { shiftWork } = rowData;
                const {
                    name,
                    code,
                    totalHours,
                    timePeriods
                } = shiftWork;

                const {
                    checkInTime,
                    checkOutTime
                } = getCheckInAndCheckOutTimeOfShiftWork(shiftWork);

                return (
                    <div className="px-4">
                        {name && (
                            <p className='m-0 pb-4'>
                                <span>
                                    <strong>{`${name} - ${code}`} </strong>
                                </span>
                            </p>
                        )}

                        {/* {totalHours && <p className='m-0 no-wrap-text'>Quy định: {formatVNDMoney(totalHours)} giờ</p>} */}

                        {(checkInTime && checkOutTime) && <p className='m-0 no-wrap-text'>
                            {/* Thời gian:  */}
                            {`${formatDate("HH:mm", checkInTime)} - ${formatDate("HH:mm", checkOutTime)}`}</p>}

                    </div>
                );
            },
            minWidth: "200px",
        },
    ];

    return (
        <Grid container spacing={2}>
            <Grid item xs={12} className="w-100">
                <GlobitsTable
                    columns={columns}
                    data={schedulesInDayList || []}
                    noPagination
                />
            </Grid>
        </Grid>
    );
}

export default memo(observer(SchedulesInDayList));
