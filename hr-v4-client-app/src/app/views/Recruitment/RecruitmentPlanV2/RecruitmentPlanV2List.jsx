import React, {memo, useState} from "react";
import {observer} from "mobx-react";
import {useTranslation} from "react-i18next";
import {Icon, IconButton, Tooltip} from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import {useStore} from "app/stores";
import {formatDate} from "app/LocalFunction";
import {useHistory} from "react-router-dom/cjs/react-router-dom";
import LocalConstants from "app/LocalConstants";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";

function RecruitmentPlanV2List() {
    const history = useHistory();
    const {recruitmentPlanStore} = useStore();
    const {t} = useTranslation();

    const {
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        listRecruitmentPlans,
        handleSelectListDelete,
        handleOpenCreateEdit,
        handleOpenConfirmUpdateStatusPopup
    } = recruitmentPlanStore;

    const columns = [
        {
            title: t("general.action"),
            cellStyle: {
                width: "8%",
            },
            headerStyle: {
                width: "8%",
            },
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

                        {/* <Tooltip title="Thao tác khác" placement="top">
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
                        </Tooltip> */}
                    </div>
                );
            },
        },
        {
            title: "Mã kế hoạch",
            field: "code",
            align: "center",
        },
        {
            title: "Tên kế hoạch",
            field: "name",
            align: "left",
        },
        {
            title: "Yêu cầu tuyển dụng",
            field: "recruitmentRequest.name",
            align: "left",
        },
        // {
        //     title: "Số lượng",
        //     field: "quantity",
        //     align: "left",
        // },
        // {
        //     title: "Trạng thái",
        //     field: "status",
        //     align: "left",
        //     render: row =>
        //         <span>{LocalConstants.RecruitmentPlanStatus.getListData().find(i => i.value === row?.status)?.name}</span>
        // },
        {
            title: "Dự kiến từ",
            field: "estimatedTimeFrom",
            render: (rowData) => (
                <span>
          {rowData?.estimatedTimeFrom && (formatDate("DD/MM/YYYY", rowData?.estimatedTimeFrom))}
        </span>
            ),
        },
        {
            title: "Dự kiến đến",
            field: "estimatedTimeTo",
            render: (rowData) => (
                <span>
          {rowData?.estimatedTimeTo && (formatDate("DD/MM/YYYY", rowData?.estimatedTimeTo))}
        </span>
            ),
        },
    ];

    const [anchorEl, setAnchorEl] = useState();
    const [selectedRow, setSelectedRow] = useState(null);

    function handleClosePopover() {
        setAnchorEl(null);
        setSelectedRow(null);
    }

    return (
        <>
            <GlobitsTable
                data={listRecruitmentPlans}
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

                    {selectedRow?.status !== LocalConstants.RecruitmentPlanStatus.APPROVED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenConfirmUpdateStatusPopup(LocalConstants.RecruitmentPlanStatus.APPROVED.value);
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color: "green"}}>
                                done_all
                            </Icon>
                            Phê duyệt
                        </MenuItem>
                    )}

                    {selectedRow?.status !== LocalConstants.RecruitmentPlanStatus.REJECTED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenConfirmUpdateStatusPopup(LocalConstants.RecruitmentPlanStatus.REJECTED.value);
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color: "red"}}>
                                thumb_down
                            </Icon>
                            Từ chối
                        </MenuItem>
                    )}

                    {selectedRow?.status !== LocalConstants.RecruitmentPlanStatus.COMPLETED.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenConfirmUpdateStatusPopup(LocalConstants.RecruitmentPlanStatus.COMPLETED.value);
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6 text-limegreen" fontSize="small">
                                check_box
                            </Icon>
                            Hoàn thành
                        </MenuItem>
                    )}

                    {selectedRow?.status !== LocalConstants.RecruitmentPlanStatus.NOT_APPROVED_YET.value && (
                        <MenuItem className="flex items-center justify-center" onClick={function () {
                            handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                            handleOpenConfirmUpdateStatusPopup(LocalConstants.RecruitmentPlanStatus.NOT_APPROVED_YET.value);
                            handleClosePopover();
                        }}>
                            <Icon className="pr-6" fontSize="small" style={{color: "blue"}}>
                                loop
                            </Icon>
                            Cài lại
                        </MenuItem>
                    )}
                </Menu>
            )}
        </>

    );
}

export default memo(observer(RecruitmentPlanV2List));
