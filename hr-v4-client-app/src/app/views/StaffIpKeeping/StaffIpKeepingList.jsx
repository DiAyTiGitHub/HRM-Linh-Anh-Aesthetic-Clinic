import React, {memo} from "react";
import GlobitsTable from "../../common/GlobitsTable";
import {useStore} from "../../stores";
import {useTranslation} from "react-i18next";
import {IconButton, Icon} from "@material-ui/core";
import {observer} from "mobx-react";

function MaterialButton(props) {
    const {item} = props;
    return (
        <div>
            <IconButton size="small" onClick={() => props.onSelect(item, 1)}>
                <Icon fontSize="small" color="secondary">
                    delete
                </Icon>
            </IconButton>
        </div>
    );
}

function StaffIpKeepingList() {
    const {staffIpKeepingStore} = useStore();
    const {t} = useTranslation();

    const {
        listStaffIpKeeping,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectedList,
    } = staffIpKeepingStore;

    const columns = [
        {
            title: t("general.action"),
            width: "10%",
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 1) {
                            handleDelete(rowData);
                        } else {
                            alert("Call Selected Here:" + rowData?.id);
                        }
                    }}
                />
            ),
        },
        {
            title: "Tổ chức",
            field: "organization.name",
            align: "left",

        },
        {
            title: "Phòng ban sử dụng",
            field: "department.name",
            align: "left",
        },
        {
            title: "Nhân viên",
            field: "displayName",
            align: "left",
        },
    ];

    return (
        <GlobitsTable
            selection
            data={listStaffIpKeeping}
            handleSelectList={handleSelectedList}
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

export default memo(observer(StaffIpKeepingList));
