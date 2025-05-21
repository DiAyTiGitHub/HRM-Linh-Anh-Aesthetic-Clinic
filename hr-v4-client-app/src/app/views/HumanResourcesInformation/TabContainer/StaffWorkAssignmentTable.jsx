import React, { memo } from "react";
import GlobitsTable from "../../../common/GlobitsTable";
import { useStore } from "../../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import { observer } from "mobx-react";
import { getDate } from "app/LocalFunction";

function StaffWorkAssignmentTable() {
    const { t } = useTranslation();

    const { staffWorkScheduleStore } = useStore();

    const {
        listStaffWorkSchedules,
        totalPages,
        totalElements,
        searchObject,
        handleChangePage,
        setPageSize,
        handleDelete,
        handleSelectListDelete,
        handleOpenCreateEdit,
      } = staffWorkScheduleStore;

    let columns = [
        // {
        //     title: t("general.action"),
        //     width: "10%",
        //     align: "center",
        //     render: (rowData) => (
        //         <>
        //             <IconButton size="small" onClick={() => onOpenStaffWorkScheduleEdit(rowData.id)}>
        //                 <Icon fontSize="small" color="primary">
        //                     edit
        //                 </Icon>
        //             </IconButton>

        //             <IconButton size="small" onClick={() => {
        //                 console.log(rowData?.id)
        //                 setSelectStaffWorkScheduleDeleted(rowData.id)
        //             }}>
        //                 <Icon fontSize="small" color="secondary">
        //                     delete
        //                 </Icon>
        //             </IconButton>
        //         </>
        //     ),
        // },

        {
            title: "Ngày làm việc",
            field: "workingDate",
            width: "10%",
            align: "center",
            render: row => <span>{getDate(row?.workingDate)}</span>
        },

        {
            title: "Ca làm việc",
            width: "20%",
            align: "center",
            field: "shiftWork.name",
            render: row => <span>{`${row?.shiftWork?.name}`}</span>
        },

    ];

    return (
       <GlobitsTable
             selection
             data={listStaffWorkSchedules}
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
    );
};

export default memo(observer(StaffWorkAssignmentTable));
