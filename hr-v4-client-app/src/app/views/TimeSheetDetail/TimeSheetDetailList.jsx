import { Icon, IconButton, Tooltip } from "@material-ui/core";
import { getDate, getDateTime, getTime } from "app/LocalFunction";
import { observer } from "mobx-react";
import React, { memo, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import localStorageService from "app/services/localStorageService";
import { NavLink } from "react-router-dom";
import ConstantList from "../../appConfig";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import BackspaceIcon from "@material-ui/icons/Backspace";
import CompareArrowsIcon from "@material-ui/icons/CompareArrows";
import InsertInvitationIcon from "@material-ui/icons/InsertInvitation";
import TimerIcon from "@material-ui/icons/Timer";
import { LocalActivity } from "@material-ui/icons";

const ButtonsTable = (props) => {
    const { data } = props;
    const {
        timeSheetDetailStore,
        hrRoleUtilsStore
    } = useStore();

    const {
        isAdmin,
        isManager
    } = hrRoleUtilsStore;

    const {
        handleDelete,
        handleOpenCreateEdit,
        handleOpenView
    } = timeSheetDetailStore;

    const [anchorEl, setAnchorEl] = useState();

    const isLocked = data?.staffWorkSchedule?.isLocked;

    return (
        <div className="flex flex-center">
            <Tooltip
                arrow
                placement="top"
                title={"Thống kê công"}
            >
                <IconButton
                    className="ml-4"
                    size="small"
                    onClick={() => handleOpenView(data?.id)}
                >
                    <Icon fontSize="small" style={{ color: "#13529f" }}>
                        remove_red_eye
                    </Icon>
                </IconButton>

            </Tooltip>

            {(isManager || isAdmin) && !isLocked && (
                <Tooltip
                    arrow
                    title={"Cập nhật thông tin lịch sử chấm công"}
                    placement="top"
                >
                    <IconButton size="small" onClick={() => handleOpenCreateEdit(data?.id)}>
                        <Icon fontSize="small" color="primary">
                            edit
                        </Icon>
                    </IconButton>
                </Tooltip>
            )}

            {(isManager || isAdmin) && !isLocked && (
                <Tooltip
                    arrow
                    title={"Xóa thông tin lịch sử chấm công"}
                    placement="top"
                >
                    <IconButton size="small" onClick={() => handleDelete(data)}>
                        <Icon fontSize="small" color="secondary">
                            delete
                        </Icon>
                    </IconButton>
                </Tooltip>
            )}

            <IconButton size='small' onClick={(event) => setAnchorEl(event.currentTarget)}>
                <Icon
                    style={{ fontSize: "20px" }}
                    className='m-0 '
                >
                    more_vert
                </Icon>
            </IconButton>

            <Menu
                id='simple-menu'
                anchorEl={anchorEl}
                keepMounted
                open={Boolean(anchorEl)}
                onClose={() => setAnchorEl(null)}
            >
                <MenuItem
                    component={NavLink}
                    to={ConstantList.ROOT_PATH + "staff-month-schedule-calendar/" + data?.employee?.id}
                >
                    <InsertInvitationIcon style={{ color: "#13529f" }} />

                    <span className='ml-6'>Lịch làm việc</span>
                </MenuItem>
            </Menu>
        </div>
    );
};

function TimeSheetDetailList() {
    const { timeSheetDetailStore, listChosen, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        timeSheetDetailList,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleSelectListDelete,
    } = timeSheetDetailStore;

    let columns = [
        {
            title: t("general.action"),
            width: "10%",
            align: "center",
            render: (rowData) => <ButtonsTable data={rowData} />,
        },
        {
            title: t("Nhân viên"),
            field: "employee",
            render: (data) => <span>{data?.employee?.displayName}</span>,
            align: "left",
        },
        {
            title: t("Ngày chấm công"),
            field: "workingDate",
            render: (data) => <span>{getDate(data?.timeSheet?.workingDate)}</span>,
            align: "left",
        },
        {
            title: t("Thời gian bắt đầu"),
            field: "startTime",
            render: (data) => <span>{getDateTime(data?.startTime)}</span>,
            align: "left",
        },
        {
            title: t("Thời gian kết thúc"),
            field: "endTime",
            render: (data) => <span>{getDateTime(data?.endTime)}</span>,
            align: "left",
        },
        {
            title: t("Địa chỉ IP checkin"),
            field: "addressIPCheckIn",
            render: (data) => <span>{data?.addressIPCheckIn}</span>,
            align: "left",
        },
        {
            title: t("Địa chỉ IP checkout"),
            field: "addressIPCheckOut",
            render: (data) => <span>{data?.addressIPCheckOut}</span>,
            align: "left",
        },
        {
            title: t("Ca chấm"),
            field: "shiftWork",
            render: (data) => <span>{data?.staffWorkSchedule?.shiftWork?.name}</span>,
            align: "left",
        },
        // {
        //     title:t("Giai đoạn làm việc") ,
        //     field:"shiftWorkTimePeriod" ,
        //     render:(data) =>
        //         !data?.staffWorkSchedule?.allowOneEntryOnly && (
        //             <span>{getTime(data?.shiftWorkTimePeriod?.startTime)} - {getTime(data?.shiftWorkTimePeriod?.endTime)}</span>
        //         ) ,
        //     align:"left" ,
        // }
    ];


    return (
        <GlobitsTable
            selection
            data={timeSheetDetailList}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
            handleSelectList={handleSelectListDelete}
            selectedRows={listChosen || []}
        />
    );
}

export default memo(observer(TimeSheetDetailList));
