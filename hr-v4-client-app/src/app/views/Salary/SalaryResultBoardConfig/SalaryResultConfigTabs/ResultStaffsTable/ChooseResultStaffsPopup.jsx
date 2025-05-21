import React, { memo, useEffect } from "react";
import { Checkbox, Grid, Tooltip, } from "@material-ui/core";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { useFormikContext } from "formik";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate } from "app/LocalFunction";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { pagingStaff as pagingStaffService, pagingLowerLevelStaff } from "app/views/HumanResourcesInformation/StaffService";
import { toast } from "react-toastify";
import StaffToolbar from "./StaffToolbar";
import { PositionRelationshipType } from "app/LocalConstants";

/*
*   Select multiple staffs
*   used: phân ca nhiều nhân viên
*/
function ChooseResultStaffsPopup(props) {
    const {
        open,
        handleClose,
        searchObject,
        isDisableFilter
    } = props;

    const { t } = useTranslation();

    const { userStore } = useStore();
    const {
        pagingLowerStaff,
        listUsingStaff,
        resetUsingStaffSection,
        setRowsPerPageSelectMultipleStaffs,
        usingStaffSO,
        totalStaffElements,
        totalStaffPages,
        handleChangePageSelectMultipleStaffs,
        handleSetUsingStaffSO

    } = userStore;

    const { values, setFieldValue } = useFormikContext();

    useEffect(function () {
        if (open) {
            resetUsingStaffSection();

            if (searchObject) {
                const newSearchObject = {
                    ...usingStaffSO,
                    ...searchObject
                };

                handleSetUsingStaffSO(newSearchObject);
            }

            pagingLowerStaff();
        }

        return resetUsingStaffSection;
    }, [open]);

    function handleSelectUsingStaff(chosenStaff) {
        // Check if the chosenStaff is already in the values.staffs list
        const isAlreadySelected = values.staffs.some((staff) => staff.id === chosenStaff.id);

        if (isAlreadySelected) {
            // Remove the staff from the list
            const updatedStaffs = values.staffs.filter((staff) => staff.id !== chosenStaff.id);
            setFieldValue("staffs", updatedStaffs);
        } else {
            // Add the staff to the list
            setFieldValue("staffs", [...values.staffs, chosenStaff]);
        }
    }

    const handleSelectAll = (checked) => {
        if (checked) {
            setFieldValue("staffs", []);
        } else {
            pagingLowerLevelStaff({ ...usingStaffSO, isExportExcel: true })
                .then(({ data }) => {
                    setFieldValue("staffs", data?.content || []);
                }).catch((err) => {
                    console.error(err);
                    toast.error("Có lỗi xảy ra, vui lòng thử lại")
                })
        }
    }

    const isCheckedAll = values?.staffs?.length === totalStaffElements;

    const columns = [
        {
            title: (
                <Tooltip title={isCheckedAll ? "Bỏ chọn tất cả" : "Chọn tất cả"} placement="top">
                    <Checkbox
                        className="pr-16"
                        id="checkbox-all"
                        checked={isCheckedAll}
                        onClick={(event) => handleSelectAll(isCheckedAll)}
                    />
                </Tooltip>
            ),
            sorting: false,
            align: "center",
            width: "10%",
            cellStyle: {
                textAlign: "center",
            },
            render: (rowData) => {
                // Check if the current staff is selected
                const isChecked = values?.staffs?.some((staff) => staff?.id === rowData?.id);

                return (
                    <Tooltip title={isChecked ? "Bỏ chọn" : "Chọn sử dụng"} placement="top">
                        <Checkbox
                            className="pr-16"
                            id={`radio${rowData?.id}`}
                            name="radSelected"
                            value={rowData.id}
                            checked={isChecked}
                            onClick={(event) => handleSelectUsingStaff(rowData)}
                        />
                    </Tooltip>
                )
            }
        },
        // {
        //     title: "Mã nhân viên",
        //     field: "staffCode",
        //     align: "left",
        //     cellStyle: {
        //         textAlign: "left",
        //     },
        // },
        // {
        //     title: "Tên nhân viên",
        //     field: "displayName",
        //     align: "left",
        //     width: "30%",
        //     cellStyle: {
        //         textAlign: "left",
        //     },
        // },
        // {
        //     title: "Ngày sinh",
        //     field: "birthDate",
        //     render: (value) => value?.birthDate && (<span>{formatDate("DD/MM/YYYY", value?.birthDate)}</span>),
        // },
        // {
        //     title: "Giới tính",
        //     field: "gender",
        //     render: (value) => { return value.gender === "M" ? "Nam" : value.gender === "F" ? "Nữ" : "" },
        // },
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
        <GlobitsPopupV2
            noDialogContent
            popupId={"chooseChooseResultStaffsPopup"}
            open={open}
            title='Danh sách nhân viên'
            size="md"
            scroll={"body"}
            onClosePopup={handleClose}
        >
            <Grid container className="p-12">
                <Grid item xs={12}>
                    <StaffToolbar
                        isDisableFilter={isDisableFilter}
                    />
                </Grid>

                <Grid item xs={12} className="pt-12">
                    <GlobitsTable
                        data={listUsingStaff}
                        columns={columns}
                        totalPages={totalStaffPages}
                        handleChangePage={handleChangePageSelectMultipleStaffs}
                        setRowsPerPage={setRowsPerPageSelectMultipleStaffs}
                        pageSize={usingStaffSO?.pageSize}
                        pageSizeOption={[3, 5, 10, 20, 25]}
                        totalElements={totalStaffElements}
                        page={usingStaffSO?.pageIndex}
                    />
                </Grid>
            </Grid>
        </GlobitsPopupV2>
    );
}

export default memo(observer(ChooseResultStaffsPopup)); 