import React, {memo, useState} from "react";
import {observer} from "mobx-react";
import {useTranslation} from "react-i18next";
import {IconButton, Icon, Tooltip} from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import {useStore} from "app/stores";
import {getDate} from "app/LocalFunction";
import {useHistory} from "react-router-dom/cjs/react-router-dom";
import LocalConstants from "app/LocalConstants";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";

function AbsenceRequestList() {
    const history = useHistory();
    const {absenceRequestStore} = useStore();
    const {t} = useTranslation();

    const {
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        listAbsenceRequests,
        handleSelectListDelete,
        handleOpenCreateEdit,
        handleOpenConfirmUpdateStatusPopup,
        listChosen
    } = absenceRequestStore;

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            align: "center",
            render: (rowData) => {
                return (
                    <div className="flex flex-middle justify-center">
                        <Tooltip title="Cập nhật" placement="top">
                            <IconButton size="small" onClick={function () {
                                handleOpenCreateEdit(rowData);
                            }}>
                                <Icon fontSize="small" color="primary">
                                    edit
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Xóa" placement="top">
                            <IconButton className="ml-8" size="small" onClick={() => handleDelete(rowData)}>
                                <Icon fontSize="small" color="secondary">
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Thao tác khác" placement="top">
                            <IconButton
                                className="ml-8"
                                size="small"
                                onClick={(event) => {
                                    setSelectedRow(rowData);
                                    setAnchorEl(event?.currentTarget);
                                }}
                            >
                                <MoreHorizIcon/>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
        {
            title: "Nhân viên yêu cầu",
            field: "staff",
            align: "left",
            render: row => {
                return <span>{row?.workSchedule?.staff?.displayName}</span>
            }
        },
        {
            title: "Ngày làm việc",
            field: "workSchedule",
            align: "left",
            render: row => {
                return <span>{getDate(row?.workSchedule?.workingDate) + ' - ' + row?.workSchedule?.shiftWork?.name}</span>
            }
        },
        {
            title: "Ngày yêu cầu nghỉ",
            field: "requestDate",
            align: "left",
            render: row => {
                return <span>{getDate(row?.requestDate)}</span>
            }
        },
        // {
        //     title: "Lý do nghỉ",
        //     field: "absenceReason",
        //     align: "left"
        // },
        {
            title: "Trạng thái",
            field: "approvalStatus",
            align: "left",
            render: row => {
                return <span>{LocalConstants.AbsenceRequestApprovalStatus.getListData().find(i => i.value == row?.approvalStatus)?.name}</span>
            }
        },
    ];

    const [anchorEl, setAnchorEl] = useState();
    const [selectedRow, setSelectedRow] = useState(null);

    function handleClosePopover() {
        setAnchorEl(null);
        setSelectedRow(null);
    }

// console.log("current list chosen: ", listChosen);

    return (
        <>
            <GlobitsTable
                data={listAbsenceRequests}
                columns={columns}
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject?.pageSize}
                pageSizeOption={[10, 15, 25, 50, 100]}
                totalElements={totalElements}
                page={searchObject?.pageIndex}
                selection
                handleSelectList={handleSelectListDelete}
                selectedRows={listChosen || []}
            />

            {Boolean(anchorEl) && (
                <Menu
                    id={"simple-menu-options"}
                    anchorEl={anchorEl}
                    keepMounted
                    open={Boolean(anchorEl)}
                    onClose={handleClosePopover}
                    className="py-0"
                >

                    {selectedRow?.approvalStatus != LocalConstants.AbsenceRequestApprovalStatus.APPROVED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenConfirmUpdateStatusPopup(LocalConstants.AbsenceRequestApprovalStatus.APPROVED.value);
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color: "green"}}>
                                done_all
                            </Icon>
                            Phê duyệt
                        </MenuItem>
                    )}

                    {selectedRow?.approvalStatus != LocalConstants.AbsenceRequestApprovalStatus.NOT_APPROVED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenConfirmUpdateStatusPopup(LocalConstants.AbsenceRequestApprovalStatus.NOT_APPROVED.value);
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color: "red"}}>
                                thumb_down
                            </Icon>
                            Không duyệt
                        </MenuItem>
                    )}

                    {selectedRow?.approvalStatus != LocalConstants.AbsenceRequestApprovalStatus.NOT_APPROVED_YET.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenConfirmUpdateStatusPopup(LocalConstants.AbsenceRequestApprovalStatus.NOT_APPROVED_YET.value);
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color: "blue"}}>
                                loop
                            </Icon>
                            Chưa duyệt
                        </MenuItem>
                    )}
                </Menu>
            )}
        </>

    );
}

export default memo(observer(AbsenceRequestList));
