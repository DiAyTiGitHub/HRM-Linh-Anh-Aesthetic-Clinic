import React, { memo, useState } from "react";
import { observer } from "mobx-react";
import {
    Grid,
    Icon,
    IconButton,
    makeStyles,
    Tooltip
} from "@material-ui/core";
import { useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import GlobitsTable from "app/common/GlobitsTable";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import { PositionRelationshipType } from "app/LocalConstants";
import { formatDate } from "app/LocalFunction";
import StaffWorkSchedulePopup from "app/views/StaffWorkScheduleV2/StaffWorkSchedulePopup";
import EventAvailableIcon from '@material-ui/icons/EventAvailable';
import {useStore} from "../../../stores";

const useStyles = makeStyles(() => ({
    root: {
        background: "#E4f5fc",
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
    tableContainer: {
        marginTop: "2px",
        overflowX: "auto",
        // overflowY: "hidden",
        "& table": {
            border: "1px solid #ccc",
            borderCollapse: "collapse",
            "& td": {
                border: "1px solid #ccc",
            }
        }
    },
    tableHeader: {
        // width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px"
        },
    },
}));


function ChosenStaffRegularTable(props) {
    const { isDraggableTable = false, listSelectStaffs, setListSelectedStaffs } = props;
    const { t } = useTranslation();
    const classes = useStyles();
    const { values, setFieldValue } = useFormikContext();
    const [openConfirmDelete, setOpenConfirmDelete] = useState(false);
    const [selectedStaffId, setSelectedStaffId] = useState(null);
    const {listExistLeaveRequest} = useStore().staffWorkScheduleStore
    const handleOpenConfirmDelete = (id) => {
        setSelectedStaffId(id);
        setOpenConfirmDelete(true);
    };

    const handleCloseConfirmDelete = () => {
        setOpenConfirmDelete(false);
        setSelectedStaffId(null);
    };

    const handleConfirmDelete = () => {
        setFieldValue(
            "staffs",
            values.staffs.filter((staff) => staff.id !== selectedStaffId)
        );
        handleCloseConfirmDelete();
    };

    const handleSelectionChange = (selectedRows) => {
        setListSelectedStaffs(selectedRows); // Cập nhật danh sách nhân viên được chọn
    };

    const columns = [
        {
            title: t("general.action"),
            align: "center",
            render: (rowData) => (
                <div className="flex flex-middle justify-center px-8">
                    {/* <Tooltip arrow title="Bỏ lựa chọn" placement="top">
                        <IconButton size="small" onClick={() => handleOpenConfirmDelete(rowData.id)}>
                            <Icon fontSize="small" color="secondary">delete</Icon>
                        </IconButton>
                    </Tooltip> */}
                    {(values?.fromDate && values?.toDate && rowData?.id) && (
                        <ButtonOpenStaffWorkSchedulePopup
                            rowData={(rowData)}
                            fromDate={(values?.fromDate)}
                            toDate={(values?.toDate)}
                        />
                    )}
                </div>
            )
        },

        {
            title: t("STT"),
            render: (rowData, index) => (
                <span className="px-8">
                    {rowData?.tableData?.id + 1}
                </span>
            ), // Tăng chỉ số bắt đầu từ 1
            cellStyle: { textAlign: "center" },
            headerStyle: { textAlign: "center" },
        },


        {
            title: "Nhân viên",
            minWidth: "200px",
            //width: "30%",
            render: (rowData) => (
                <>
                    {rowData.displayName && <p className='m-0'><strong>{`${rowData.displayName} - ${rowData.staffCode}`}</strong></p>}
                    {rowData.phoneNumber && <p className='m-0'>SĐT: {rowData.phoneNumber}</p>}
                    {rowData.email && <p className='m-0'>Email: {rowData.email}</p>}
                    {rowData.gender && <p className='m-0'>Giới tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}</p>}
                    {rowData.birthDate && <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", rowData.birthDate)}</p>}
                    {listExistLeaveRequest?.includes(rowData?.id) && <p className='m-0 text-red'>Nhân viên này đã có lịch nghỉ phép trong khoảng thời gian phân ca</p>}
                </>
            ),
        },

        {
            title: "Đơn vị",
            field: "organization.name",
            align: "left",
            minWidth: "120px",
            render: (rowData) => <span className='pr-6'>{rowData?.organization?.name}</span>,
        },
        {
            title: "Phòng ban",
            field: "department.name",
            align: "left",
            minWidth: "120px",
            render: (rowData) => (
                <>
                    {rowData?.department?.name && <p className='m-0'>{rowData?.department?.name}</p>}
                    {rowData?.department?.code && <p className='m-0'>({rowData?.department?.code})</p>}
                </>
            ),
        },

        {
            title: "Chức danh",
            field: "positionTitleName",
            align: "left",
            minWidth: "120px",
            render: (rowData) => <span className='pr-6'>{rowData?.positionTitle?.name}</span>,
        },

        {
            title: "Vị trí",
            field: "currentPosition",
            align: "left",
            minWidth: "120px",
            render: (rowData) => <span className='pr-6'>{rowData?.currentPosition?.name}</span>,
        },

        {
            title: "Trạng thái nhân viên",
            field: "status.name",
            align: "left",
            minWidth: "150px",
            render: (rowData) => <span className='pr-6'>{rowData?.status?.name}</span>,
        },

        {
            title: "Quản lý trực tiếp",
            align: "left",
            minWidth: "150px",
            render: (rowData) => (
                <>
                    {rowData?.currentPosition?.relationships
                        ?.filter(
                            (item) =>
                                item?.supervisor?.name &&
                                item?.relationshipType === PositionRelationshipType.UNDER_DIRECT_MANAGEMENT.value
                        )
                        ?.map((item, index) => (
                            <>
                                {index > 0 && <br />}
                                <span className='pr-6'>
                                    - {item?.supervisor?.name}
                                    {item?.supervisor?.staff?.displayName
                                        ? ` (${item.supervisor.staff.displayName})`
                                        : ""}
                                </span>
                            </>
                        ))}
                </>
            ),
        },
    ];

    return (
        <>
            <GlobitsTable
                selection
                data={values?.staffs}
                columns={columns}
                nonePagination
                handleSelectList={handleSelectionChange}
            />

            {openConfirmDelete && (
                <GlobitsConfirmationDialog
                    open={openConfirmDelete}
                    onConfirmDialogClose={handleCloseConfirmDelete}
                    onYesClick={handleConfirmDelete}
                    title={t("confirm_dialog.delete.title")}
                    text={"Bạn có chắc muốn bỏ lựa chọn nhân viên này?"}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}
        </>
    );
}

const ButtonOpenStaffWorkSchedulePopup = ({ isSubmitting, rowData, fromDate, toDate }) => {
    const [openStaffWorkSchedulePopup, setOpenStaffWorkSchedulePopup] = useState(false);

    return (
        <Grid item xs={6} sm={12}>
            <Tooltip arrow title="Danh sách ca làm việc đã được phân" placement="top">
                <IconButton size="small" onClick={() => setOpenStaffWorkSchedulePopup(true)}>
                    <EventAvailableIcon fontSize="small" color="secondary" />
                </IconButton>
            </Tooltip>
            {
                openStaffWorkSchedulePopup && (
                    <StaffWorkSchedulePopup
                        open={openStaffWorkSchedulePopup}
                        handleClose={() => setOpenStaffWorkSchedulePopup(false)}
                        staff={rowData}
                        fromDate={fromDate}
                        toDate={toDate}
                    />
                )
            }
        </Grid>
    );
};

export default memo(observer(ChosenStaffRegularTable));
