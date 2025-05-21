import { Icon, IconButton, Tooltip } from "@material-ui/core";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import MoreHorizIcon from "@material-ui/icons/MoreHoriz";
import ConstantList from "app/appConfig";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { memo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import SalarySelectPrintRows from "./SalarySelectPrintRows";
// import {SALARY_STAFF_PAYSLIP_APPROVAL_STATUS} from ""
import LocalConstants from "app/LocalConstants";
import CheckIcon from '@material-ui/icons/Check';

function SalaryResultList() {
    const {
        salaryResultStore,
        hrRoleUtilsStore

    } = useStore();

    const { t } = useTranslation();

    const history = useHistory();

    const {
        openFormSelectRows,
        setOpenFormSelectRows,
        listSalaryResults,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenCreateEdit,
        handleExportExcelStaff,
        getSalaryResultBoard
    } = salaryResultStore;

    const {
        isCompensationBenifit,
        isStaffView
    } = hrRoleUtilsStore;

    const [anchorEl, setAnchorEl] = useState();
    const [selectedRow, setSelectedRow] = useState(null);

    function handleClosePopover() {
        setAnchorEl(null);
        setSelectedRow(null);
    }

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            render: (rowData) => {
                function handleSwitchReadOnlyMode() {
                    history.push(ConstantList.ROOT_PATH + `payroll/` + rowData?.id);
                }

                return (
                    <div className='flex justify-center'>
                        {
                            (isStaffView || isCompensationBenifit) && (
                                <Tooltip title='Xem bảng lương' placement='top'>
                                    <IconButton size='small' onClick={() => {
                                        if (rowData?.approvalStatus === LocalConstants.SalaryStaffPayslipApprovalStatus.LOCKED.value) {
                                            history.push(ConstantList.ROOT_PATH + `salary-result-detail-read-only/` + rowData?.id);
                                        } else {
                                            handleSwitchReadOnlyMode()
                                        }
                                    }}>
                                        <Icon fontSize='small' style={{ color: "blue" }}>
                                            visibility
                                        </Icon>
                                    </IconButton>
                                </Tooltip>
                            )
                        }


                        {
                            (isCompensationBenifit && !rowData?.isLocked) && (
                                <Tooltip title='Xóa bảng lương' placement='top'>
                                    <IconButton size='small' onClick={() => handleDelete(rowData)}>
                                        <Icon fontSize='small' color='secondary'>
                                            delete
                                        </Icon>
                                    </IconButton>
                                </Tooltip>
                            )
                        }

                        {
                            (isCompensationBenifit && !rowData?.isLocked) && (
                                <Tooltip title='Thao tác khác' placement='top'>
                                    <IconButton
                                        className=''
                                        size='small'
                                        onClick={(event) => {
                                            setSelectedRow(rowData);
                                            setAnchorEl(event?.currentTarget);
                                        }}>
                                        <MoreHorizIcon />
                                    </IconButton>
                                </Tooltip>
                            )
                        }

                    </div>
                );
            },
        },

        {
            title: "Tên bảng lương",
            field: "name",
            align: "left",
            width: "30%",
            render: data => (
                <span className="px-6">
                    {data?.name}
                </span>
            )
        },

        {
            title: "Mã bảng lương",
            field: "code",
            align: "center",
            width: "20%",
            render: data => (
                <Tooltip
                    arrow
                    placement="top"
                    title={data?.code}
                >
                    <span className="px-6 multiline-ellipsis">
                        {data?.code}
                    </span>
                </Tooltip>
            )
        },

        {
            title: "Kỳ tính lương",
            field: "salaryPeriod.name",
            align: "center",
            width: "20%",

        },

        {
            title: "Mẫu tính lương",
            field: "salaryTemplate.name",
            align: "left",
            width: "20%",

        },

        {
            title: "Đã khóa",
            field: "isLocked",
            width: "10%",
            render: data => {

                return (
                    <div className="flex justify-center w-100">
                        {data?.isLocked && (
                            <CheckIcon fontSize="small" style={{ color: "green" }} />
                        )}
                    </div>
                );

            }
        }
        // {
        //     title: "Mô tả",
        //     field: "description",
        //     align: "left",
        //     minWidth: "150px",
        // },
    ];

    function handleRedirectConfigMode() {
        history.push(ConstantList.ROOT_PATH + `salary-result-board-config/` + selectedRow?.id);
    }

    function handleExportExcel() {
        handleExportExcelStaff(selectedRow?.id, selectedRow?.name);
    }

    const componentRef = useRef();

    const handlePrintPointer = () => {
        console.log(selectedRow?.id);
        setOpenFormSelectRows(true);
    };

    return (
        <>
            <GlobitsTable
                selection
                data={listSalaryResults}
                handleSelectList={handleSelectListDelete}
                columns={columns}
                totalPages={totalPages}
                handleChangePage={handleChangePage}
                setRowsPerPage={setPageSize}
                pageSize={searchObject?.pageSize}
                pageSizeOption={[10, 15, 25, 50, 100]}
                totalElements={totalElements}
                page={searchObject?.pageIndex}
            />

            {Boolean(anchorEl) && (
                <Menu id={"simple-menu-options"} anchorEl={anchorEl} keepMounted open={Boolean(anchorEl)}
                    onClose={handleClosePopover} className='py-0'>
                    <MenuItem className='flex items-center justify-center'
                        onClick={() => handleExportExcel(selectedRow?.id, selectedRow?.name)}>
                        <Icon fontSize='small' style={{ color: "green" }}>
                            border_all
                        </Icon>
                        <span className='pl-8'>Xuất Excel</span>
                    </MenuItem>

                    <MenuItem className='flex items-center justify-center'
                        onClick={() => handlePrintPointer(selectedRow?.id)}>
                        <Icon fontSize='small' style={{ color: "blue" }}>
                            print
                        </Icon>
                        <span className='pl-8'>In bảng lương</span>
                    </MenuItem>
                </Menu>
            )}

            {openFormSelectRows && <SalarySelectPrintRows selectedRow={selectedRow} setSelectedRow={setSelectedRow} />}
        </>
    );
}

export default memo(observer(SalaryResultList));
