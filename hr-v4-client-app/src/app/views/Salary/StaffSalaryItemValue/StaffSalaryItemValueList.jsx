import React, {memo} from "react";
import GlobitsTable from "app/common/GlobitsTable";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import {IconButton, Icon} from "@material-ui/core";
import {observer} from "mobx-react";
import LocalConstants from "../../../LocalConstants";

function MaterialButton(props) {
    const {item} = props;
    return (
        <div>
            <IconButton size="small" onClick={() => props.onSelect(item, 0)}>
                <Icon fontSize="small" color="primary">
                    edit
                </Icon>
            </IconButton>
            <IconButton size="small" onClick={() => props.onSelect(item, 1)}>
                <Icon fontSize="small" color="secondary">
                    delete
                </Icon>
            </IconButton>
        </div>
    );
}

function StaffSalaryItemValueList() {
    const {staffSalaryItemValueStore} = useStore();
    const {t} = useTranslation();

    const {
        listStaffSalaryItemValue,
        totalPages,
        totalElements,
        handleChangePage,
        setPageSize,
        pageIndex,
        pageSize,
    } = staffSalaryItemValueStore;

    const columns = [
        {
            title: "Tên nhân viên",
            field: "staff.displayName",
            align: "left",
        },
        {
            title: "Tên phần tử lương",
            field: "salaryItem.name",
            align: "left",
        },
        {
            title: "Giá trị",
            field: "value",
            align: "left",
        },
        {
            title: "Cách tính",
            field: "calculationType",
            align: "left",
            render: rowData => {
                if (rowData?.calculationType)
                    return LocalConstants.SalaryItemCalculationType.getListData().find(i => i.value == rowData?.calculationType)?.name;
                return "";
            },
        },
    ];

    return (
        <GlobitsTable
            data={listStaffSalaryItemValue || []}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElements}
            page={pageIndex}
        />
    );
}

export default memo(observer(StaffSalaryItemValueList));
