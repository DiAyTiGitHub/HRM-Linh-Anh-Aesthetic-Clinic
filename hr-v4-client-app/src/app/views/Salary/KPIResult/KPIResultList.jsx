import React, {useEffect} from "react";
import GlobitsTable from "../../../common/GlobitsTable";
import {useStore} from "../../../stores";
import {useTranslation} from "react-i18next";
import {IconButton, Icon} from "@material-ui/core";
import {observer} from "mobx-react";

function MaterialButton(props) {
    const {item} = props;
    return (
        <div>
            <IconButton size='small' onClick={() => props.onSelect(item, 0)}>
                <Icon fontSize='small' color='primary'>
                    edit
                </Icon>
            </IconButton>
            <IconButton size='small' onClick={() => props.onSelect(item, 1)}>
                <Icon fontSize='small' color='error'>
                    delete
                </Icon>
            </IconButton>
        </div>
    );
}

export default observer(function KPIResultList() {
    const {KPIResultStore} = useStore();
    const {t} = useTranslation();

    const {
        kpiResultList,
        totalPages,
        totalElements,
        rowsPerPage,
        page,
        handleChangePage,
        setRowsPerPage,
        handleDelete,
        handleEditKPIResult,
    } = KPIResultStore;

    let columns = [
        {
            title: t("STT"),
            width: "80",
            render: (rowData, index) => rowData?.tableData?.id + 1, // Tăng chỉ số bắt đầu từ 1
            cellStyle: {textAlign: "center"},
            headerStyle: {textAlign: "center"},
        },
        {
            title: t("general.action"),
            cellStyle: {textAlign: "center"},
            headerStyle: {textAlign: "center"},
            render: (rowData) => (
                <MaterialButton
                    item={rowData}
                    onSelect={(rowData, method) => {
                        if (method === 0) {
                            handleEditKPIResult(rowData.id);
                        } else if (method === 1) {
                            handleDelete(rowData.id);
                        } else {
                            alert(t("general.alert.callSelected") + rowData.id); // Dịch thông báo
                        }
                    }}
                />
            ),
        },
        {
            title: t("kpi.kpi-result.name-staff"),
            field: "staff.displayName",
            width: "100",
        },
        {
            title: t("kpi.kpi-result.name-kpi"),
            field: "kpi.name",
            width: "150",
        },
        {
            title: t("kpi.code"),
            field: "kpi.code",
            width: "150",
        },

    ];

    return (
        <GlobitsTable
            data={kpiResultList}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setRowsPerPage}
            pageSize={rowsPerPage}
            pageSizeOption={[10, 25, 50]}
            totalElements={totalElements}
            page={page}
        />
    );
});
