import React, {memo, useEffect} from "react";
import GlobitsTable from "app/common/GlobitsTable";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import {Checkbox, Tooltip} from "@material-ui/core";
import {observer} from "mobx-react";
import {formatDate} from "app/LocalFunction";
import {useFormikContext} from "formik";

function PopupStaffList() {
    const {values, setFieldValue} = useFormikContext();
    const {t} = useTranslation();
    const {popupStaffStore} = useStore();

    const {
        staffList,
        totalPages,
        handleChangePage,
        setPageSize,
        totalElements,
        rowsPerPage,
        page,
        pagingStaff,
        resetStore,
    } = popupStaffStore;


    useEffect(() => {
        pagingStaff();
        return () => {
            resetStore()
        }
    }, []);

    const handleSelectStaff = (chosenStaff, checked) => {
        if (checked) {
            setFieldValue("selectedListStaffIpKeeping", [...values.selectedListStaffIpKeeping, chosenStaff]);
        } else {
            const newList = values.selectedListStaffIpKeeping.filter(item => String(item.id) !== String(chosenStaff.id));
            setFieldValue("selectedListStaffIpKeeping", newList);
        }
    };

    const handleSelectAll = (checked) => {
        if (checked) {
            setFieldValue("selectedListStaffIpKeeping", [...staffList])
        } else {
            setFieldValue("selectedListStaffIpKeeping", [])
        }
    };

    const isCheckedAll = staffList?.length > 0 && values.selectedListStaffIpKeeping?.length === staffList?.length;


    let columns = [{
        title: (<Tooltip title={isCheckedAll ? "Bỏ chọn tất cả" : "Chọn tất cả"} placement="top">
            <Checkbox
                className="pr-16"
                id="checkbox-all"
                checked={isCheckedAll}
                onChange={(event) => handleSelectAll(event.target.checked)}
            />

        </Tooltip>), sorting: false, align: "center", width: "10%", cellStyle: {
            textAlign: "center",
        }, render: (rowData) => {
            let isChecked = false;
            if (values?.selectedListStaffIpKeeping?.length > 0) {
                isChecked = values?.selectedListStaffIpKeeping?.some(item => item.id === rowData?.id);
            }

            return (<Tooltip
                title={isChecked ? "Bỏ chọn" : "Chọn"}
                placement="top"
            >
                <Checkbox
                    className="pr-16"
                    id={`checkbox${rowData.id}`}
                    checked={isChecked}
                    onChange={(event) => handleSelectStaff(rowData, event.target.checked)}
                />
            </Tooltip>)
        }
    },

        {
            title: "Mã nhân viên",
            field: "staffCode",
            align: "center",
            render: (rowData) => <span className='px-6'>{rowData?.staffCode}</span>,
        }, {
            title: "Nhân viên", minWidth: "200px", render: (rowData) => (<>
                {rowData.displayName && (<p className='m-0'>
                    <strong>{rowData.displayName}</strong>
                </p>)}

                {rowData.birthDate && <p className='m-0'>Ngày sinh: {formatDate("DD/MM/YYYY", rowData.birthDate)}</p>}

                {rowData.gender && <p className='m-0'>Giới
                    tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}</p>}

                {rowData.birthPlace && <p className='m-0'>Nơi sinh: {rowData.birthPlace}</p>}
            </>),
        }, {
            title: "Thông tin liên hệ", field: "info", minWidth: "200px", render: (rowData) => (<>
                {rowData.phoneNumber && <p className='m-0'>SĐT: {rowData.phoneNumber}</p>}

                {rowData.email && <p className='m-0'>Email: {rowData.email}</p>}
            </>),
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
        },];

    return (<GlobitsTable
        data={staffList}
        columns={columns}
        totalPages={totalPages}
        handleChangePage={handleChangePage}
        setRowsPerPage={setPageSize}
        pageSize={rowsPerPage}
        pageSizeOption={[10, 15, 25, 50, 100]}
        totalElements={totalElements}
        page={page}
    />);
}

export default memo(observer(PopupStaffList));