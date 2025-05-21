import React, { memo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip, Checkbox } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate, getDate } from "app/LocalFunction";
import { useFormikContext } from "formik";
import { pagingStaff as pagingStaffService } from "app/views/HumanResourcesInformation/StaffService";

function SalaryStaffsList() {
    const { t } = useTranslation();
    const {
        salaryOutcomeStore
    } = useStore();

    const {
        listAvailableStaffs,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        pagingAvailableCalculateStaffs,
        handleSetSearchObject,
        totalStaffElements
    } = salaryOutcomeStore;

    const {
        values,
        setFieldValue
    } = useFormikContext();


    function handleSelectStaffs(chosenStaff) {
        const updatedStaffs = values.staffs.some(staff => staff.id === chosenStaff.id)
            ? values.staffs.filter(staff => staff.id !== chosenStaff.id) // Remove if already selected
            : [...values.staffs, chosenStaff]; // Add if not selected
    
        setFieldValue("staffs", updatedStaffs);
        handleSetSearchObject({ ...searchObject, staffs: updatedStaffs });
    }
    


    const handleSelectAll = async (checked) => {
        try {
            if (checked) {
                setFieldValue("staffs", []);
                return;
            }

            const payload = {
                ...searchObject,
                isExportExcel: true
            };

            const { data } = await pagingStaffService(payload)
            setFieldValue("staffs", data?.content || []);

            const newSO = {
                ...searchObject,
                staffs: data?.content || []
            };
            handleSetSearchObject(newSO);
        }
        catch (error) {
            console.error(error);
        }

    }

    const isCheckedAll = values?.staffs?.length === totalStaffElements;


    let columns = [
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
                    <Tooltip
                        title={isChecked ? "Bỏ chọn" : "Chọn tính lương"}
                        placement="top"
                    >
                        <Checkbox
                            className="pr-16"
                            id={`radio${rowData?.id}`}
                            name="radSelected"
                            value={rowData.id}
                            checked={isChecked}
                            onClick={(event) => handleSelectStaffs(rowData)}
                        />
                    </Tooltip>
                )
            }
        },

        {
            title: "Mã nhân viên",
            field: "staffCode",
            align: "center",
            render: (rowData) => <span className='px-6'>{rowData?.staffCode}</span>,
        },
        {
            title: "Nhân viên",
            minWidth: "200px",
            render: (rowData) => (
                <>
                    {rowData.displayName && (
                        <p className='m-0'>
                            <strong>{rowData.displayName}</strong>
                        </p>
                    )}

                    {rowData.birthDate && <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", rowData.birthDate)}</p>}

                    {rowData.gender && <p className='m-0'>Giới tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}</p>}

                    {rowData.birthPlace && <p className='m-0'>Nơi sinh: {rowData.birthPlace}</p>}
                </>
            ),
        },
        {
            title: "Thông tin liên hệ",
            field: "info",
            minWidth: "200px",
            render: (rowData) => (
                <>
                    {rowData.phoneNumber && <p className='m-0'>SĐT: {rowData.phoneNumber}</p>}

                    {rowData.email && <p className='m-0'>Email: {rowData.email}</p>}

                    {/* {rowData.currentResidence && (
              <p className="m-0">Nơi ở hiện tại: {rowData.currentResidence}</p>
            )} */}
                </>
            ),
        },

        {
            title: "Đơn vị đang công tác",
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
            render: (rowData) => <span className='pr-6'>{rowData?.department?.name}</span>,
        },

        {
            title: "Vị trí",
            field: "currentPosition.name",
            align: "left",
            minWidth: "120px",
            render: (rowData) => <span className='pr-6'>{rowData?.currentPosition?.name}</span>,
        },

        {
            title: "Nơi ở hiện tại",
            field: "currentResidence",
            align: "left",
            minWidth: "180px",
            render: (rowData) => <span className='pr-6'>{rowData?.currentResidence}</span>,
        },
    ];

    return (
        <GlobitsTable
            data={listAvailableStaffs}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    );
}
export default memo(observer(SalaryStaffsList));