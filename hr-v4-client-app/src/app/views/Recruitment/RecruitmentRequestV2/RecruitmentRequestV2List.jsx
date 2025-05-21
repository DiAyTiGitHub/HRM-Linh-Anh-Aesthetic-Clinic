import {Icon, IconButton, Tooltip} from "@material-ui/core";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import GlobitsTable from "app/common/GlobitsTable";
import LocalConstants from "app/LocalConstants";
import {useStore} from "app/stores";
import {observer} from "mobx-react";
import React, {memo, useState} from "react";
import {useTranslation} from "react-i18next";
import {useHistory} from "react-router-dom/cjs/react-router-dom";
import {formatDate} from "../../../LocalFunction";

function RecruitmentRequestV2List() {
    const history = useHistory();
    const {recruitmentRequestStore, recruitmentPlanStore} = useStore();
    const {t} = useTranslation();

    const {
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        listRecruitmentRequests,
        handleSelectListDelete,
        handleOpenCreateEdit,
        handleOpenConfirmUpdateStatusPopup,
        listChosen,
        handleExportWord,
        handleOpenChoicePersonInCharge,
        handleOpenView
    } = recruitmentRequestStore;

    const {setFieldSelected} = recruitmentPlanStore;
    const columns = [
        {
            title: t("general.action"),
            cellStyle: {
                width: "10%",
            },
            headerStyle: {
                width: "10%",
            },
            align: "center",
            render: (rowData) => {
                return (
                    <div className='flex flex-middle justify-center'>
                        <Tooltip
                            arrow
                            placement="top"
                            title={"Xem chi tiết"}
                        >
                            <IconButton
                                className="ml-4"
                                size="small"
                                onClick={() => handleOpenView(rowData)}
                            >
                                <Icon fontSize="small" style={{color: "green"}}>
                                    remove_red_eye
                                </Icon>
                            </IconButton>

                        </Tooltip>
                        {rowData?.editPermission && (
                            <Tooltip title='Cập nhật' className='ml-4' placement='top'>
                                <IconButton
                                    size='small'
                                    onClick={function () {
                                        handleOpenCreateEdit(rowData);
                                    }}>
                                    <Icon fontSize='small' color='primary'>
                                        edit
                                    </Icon>
                                </IconButton>
                            </Tooltip>
                        )}
                        <Tooltip title='Xóa' placement='top'>
                            <IconButton className='ml-4' size='small' onClick={() => handleDelete(rowData)}>
                                <Icon fontSize='small' color='secondary'>
                                    delete
                                </Icon>
                            </IconButton>
                        </Tooltip>
                        <Tooltip title='Tải xuống phiếu đề xuất tuyển dụng' arrow>
                            <IconButton size='small' className='ml-4' onClick={() => handleExportWord(rowData?.id)}>
                                <Icon fontSize='small' color='blue'>
                                    description
                                </Icon>
                            </IconButton>
                        </Tooltip>
                        <Tooltip title='Thao tác khác' placement='top'>
                            <IconButton
                                className='ml-4'
                                size='small'
                                onClick={(event) => {
                                    setSelectedRow(rowData);
                                    setAnchorEl(event?.currentTarget);
                                }}>
                                <MoreHorizIcon/>
                            </IconButton>
                        </Tooltip>
                    </div>
                );
            },
        },
        {
            title: "Mã",
            field: "code",
            align: "left",
        },
        {
            title: "Tên yêu cầu",
            field: "name",
            align: "left",
        },
        {
            title: "Nhân sự phụ trách",
            field: "personInCharge",
            align: "left",
            render: (row) => {
                return <span>{row?.personInCharge?.displayName}</span>;
            },
        },
        {
            title: "Số lượng",
            field: "recruitmentRequestItems[0].announcementQuantity",
            align: "center",
        },
        {
            title: "Trạng thái",
            field: "status",
            align: "left",
            render: (row) => {
                return (
                    <span>
                        {
                            LocalConstants.RecruitmentRequestStatus.getListData().find((i) => i.value == row?.status)
                                ?.name
                        }
                    </span>
                );
            },
        },
        {
            title: "Ngày bắt đầu tuyển dụng",
            field: "recruitmentStartDate",
            align: "center",
            render: (row) => formatDate("DD/MM/yyyy",row.recruitingStartDate)
        },
        {
            title: "Ngày kết thúc tuyển dụng",
            field: "recruitmentEndDate",
            align: "center",
            render: (row) => formatDate("DD/MM/yyyy",row.recruitingEndDate)
        },
        {
            title: "Người gửi",
            align: "left",
            render: (row) => {
                return (
                    <span>
                        {row?.aPosition?.name}
                    </span>
                );
            },
        },
        {
            title: "Người duyệt",
            align: "left",
            render: (row) => {
                return (
                    <span>
                        {row?.nAPosition?.name}
                    </span>
                );
            },
        },
        {
            title: "Đơn vị",
            field: "organization.name",
            align: "left",
        },
        {
            title: "Phòng ban",
            field: "hrDepartment.name",
            align: "left",
        },

        {
            title: "Chức danh cần tuyển",
            field: "recruitmentRequestItems[0].positionTitle.name",
            align: "left",
        },
        {
            title: "Độ tuổi",
            align: "center",
            render: (row) => {
                return (
                    <span>
                        {row?.recruitmentRequestItems?.[0]?.minimumAge} -{" "}
                        {row?.recruitmentRequestItems?.[0]?.maximumAge}
                    </span>
                );
            },
        },
        {
            title: "Thu nhập đề xuất",
            align: "center",
            render: (row) => {
                return (
                    <span>
                        {row?.recruitmentRequestItems?.[0]?.minimumIncome} VND -{" "}
                        {row?.recruitmentRequestItems?.[0]?.maximumIncome} VND
                    </span>
                );
            },
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
                data={listRecruitmentRequests}
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
                    className='py-0'>
                    {selectedRow?.status != LocalConstants?.RecruitmentRequestStatus?.APPROVED?.value &&
                        selectedRow?.approvePermission && (
                            <MenuItem
                                className='flex items-center justify-center'
                                onClick={function () {
                                    handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                    handleOpenConfirmUpdateStatusPopup(
                                        LocalConstants?.RecruitmentRequestStatus?.APPROVED?.value
                                    );
                                    handleClosePopover();
                                }}>
                                <Icon className='pr-6' fontSize='small' style={{color: "green"}}>
                                    done_all
                                </Icon>
                                Phê duyệt
                            </MenuItem>
                        )}

                    {selectedRow?.status != LocalConstants?.RecruitmentRequestStatus?.REJECTED?.value && (
                        <MenuItem
                            className='flex items-center justify-center'
                            onClick={function () {
                                handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                handleOpenConfirmUpdateStatusPopup(
                                    LocalConstants?.RecruitmentRequestStatus?.REJECTED?.value
                                );
                                handleClosePopover();
                            }}>
                            <Icon className='pr-6' fontSize='small' style={{color: "red"}}>
                                thumb_down
                            </Icon>
                            Từ chối
                        </MenuItem>
                    )}
                    {[LocalConstants.RecruitmentRequestStatus.HR_LEADER.value,LocalConstants.RecruitmentRequestStatus.START_RECRUITING.value].includes(selectedRow?.status) && (
                        <MenuItem
                            className='flex items-center justify-center'
                            onClick={function () {
                                handleSelectListDelete([JSON.parse(JSON.stringify(selectedRow))]);
                                handleOpenChoicePersonInCharge();
                                handleClosePopover();
                            }}>
                            <Icon className='pr-6' fontSize='small'>
                                person_add
                            </Icon>
                            Gán nhân sự phụ trách
                        </MenuItem>
                    )}
                    {selectedRow?.status === LocalConstants?.RecruitmentRequestStatus?.START_RECRUITING?.value && (
                        <MenuItem
                            className='flex items-center justify-center'
                            onClick={function () {
                                setFieldSelected({recruitmentRequest: selectedRow});
                                handleClosePopover();
                            }}>
                            <Icon className='pr-6' fontSize='small'>
                                calendar_month
                            </Icon>
                            Tạo kế hoạch tuyển dụng
                        </MenuItem>
                    )}
                </Menu>
            )}
        </>
    );
}

export default memo(observer(RecruitmentRequestV2List));
