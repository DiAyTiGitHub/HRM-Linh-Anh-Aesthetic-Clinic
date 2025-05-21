import React, { memo, useEffect } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Tooltip, Checkbox } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate } from "app/LocalFunction";
import { useFormikContext } from "formik";
import { toast } from "react-toastify";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";

function PopupStaffList() {
    const { t } = useTranslation();
    const { popupStaffStore } = useStore();
    const { values, setFieldValue } = useFormikContext();

    const {
        staffList,
        totalPages,
        handleChangePage,
        setPageSize,
        totalElements,
        searchObject,
        pagingStaff: searchByPageStaff,
        resetStore
    } = popupStaffStore;


    useEffect(() => {
        searchByPageStaff();
        return () => {
            resetStore()
        }
    }, []);

    const handleSelectStaff = (chosenStaff) => {
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
    };

    // const handleSelectAll = async (checked) => {
    //     if (checked) {
    //         // Nếu đã chọn tất cả, bỏ chọn tất cả
    //         setFieldValue("staffs", []);
    //     } else {
    //         try {
    //             const allStaffs = [...values.staffs]; // Giữ lại các nhân viên đã chọn trước đó
    //             let currentPage = searchObject.pageIndex || 0; // Bắt đầu từ trang hiện tại
    //             const pageSize = searchObject.pageSize || 10;

    //             while (currentPage < totalPages) {
    //                 const { data } = await pagingStaff({ ...searchObject, pageIndex: currentPage, pageSize });
    //                 //const { data } = response;

    //                 if (data?.content) {
    //                     allStaffs.push(...data.content);
    //                 }

    //                 currentPage++;
    //             }

    //             // Loại bỏ các nhân viên trùng lặp
    //             const uniqueStaffs = allStaffs.reduce((acc, staff) => {
    //                 if (!acc.some((item) => item.id === staff.id)) {
    //                     acc.push(staff);
    //                 }
    //                 return acc;
    //             }, []);

    //             // Cập nhật danh sách staffs trong Formik
    //             setFieldValue("staffs", uniqueStaffs);
    //         } catch (error) {
    //             console.error("Error fetching all staff:", error);
    //             toast.error("Có lỗi xảy ra khi lấy danh sách nhân viên, vui lòng thử lại.");
    //         }
    //     }
    // };

    const handleSelectAll = async (checked) => {
        if (checked) {
            // Nếu đã chọn tất cả, bỏ chọn tất cả
            setFieldValue("staffs", []);
        } else {
            try {
                const allStaffs = []; // Mảng để lưu tất cả nhân viên

                const response = await pagingStaff({ ...searchObject, isExportExcel: true });
                const { data } = response;
                if (data?.content) {
                    allStaffs.push(...data.content);
                }

                const uniqueStaffs = allStaffs.reduce((acc, staff) => {
                    if (!acc.some((item) => item.id === staff.id)) {
                        acc.push(staff);
                    }
                    return acc;
                }, []);

                // Cập nhật danh sách staffs trong Formik
                setFieldValue("staffs", uniqueStaffs);
            } catch (error) {
                console.error("Error fetching all staff:", error);
                toast.error("Có lỗi xảy ra khi lấy danh sách nhân viên, vui lòng thử lại.");
            }
        }
    };

    const isCheckedAll = values?.staffs?.length === totalElements;

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
                    <Tooltip title={isChecked ? "Bỏ chọn" : "Chọn sử dụng"} placement="top">
                        <Checkbox
                            className="pr-16"
                            id={`radio${rowData?.id}`}
                            name="radSelected"
                            value={rowData.id}
                            checked={isChecked}
                            onClick={(event) => handleSelectStaff(rowData)}
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

                    {rowData.birthDate &&
                        <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", rowData.birthDate)}</p>}

                    {rowData.gender && <p className='m-0'>Giới
                        tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}</p>}

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
            data={staffList}
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

export default memo(observer(PopupStaffList));