/* eslint-disable jsx-a11y/anchor-is-valid */
import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import ConstantList from "../../appConfig";
import { useStore } from "../../stores";
import { observer } from "mobx-react";
import { useHistory } from "react-router-dom";

function TotalTimeList() {
    const { dashboardStore } = useStore();
    const history = useHistory();

    const {
        totalTimeReportList,
        totalPages,
        totalElements,
        rowsPerPage,
        page,
        handleChangePage, 
        setRowsPerPage,
        roundHour
    } = dashboardStore;

    const stafftName = (rowData) => (
        <a onClick={() => { history.push(ConstantList.ROOT_PATH + "staff/edit/" + rowData.staffId) }} style={{ cursor: "pointer", fontWeight: "450" }}>
            {rowData.staffName}
        </a>
    );

    const columns = [

        {
            title: "Nhân viên",
            field: "staffName",
            headerStyle: { fontSize: "16px", paddingLeft: "16px" },
            minWidth: "50%",
            render: (rowData) => {
                return (
                    < >
                        {rowData.staffName ? (
                            <div style={{ marginLeft: "16px", color: "#8999af" }}>
                                <strong>{stafftName(rowData)}</strong>
                            </div>
                        ) : (
                            ""
                        )}
                    </>
                );
            },
        },
        {
            title: "Phòng",
            headerStyle: { fontSize: "16px" },
            render: (rowData) => {
                return (
                    < >
                        <div style={{ color: "#888a8d", marginLeft: "5px" }}>
                            {rowData.department}
                        </div>
                    </>
                );
            },
        },
        {
            title: "Loại nhân viên",
            headerStyle: { fontSize: "16px" },
            render: (rowData) => {
                return (
                    < >
                        {rowData.civilServant}
                    </>
                );
            },
        },
        {
            title: "Thời gian",
            headerStyle: { fontSize: "16px" },
            field: "totalTime",
            render: (rowData) => {
                return <><div style={{ color: "#888a8d", marginLeft: "5px" }}>{rowData.totalTime ? roundHour(rowData.totalTime) : "0.0"}</div></>;
            },
        },
    ];

    return (
        <GlobitsTable
            //selection
            //handleSelectList={handleSelectListTotalHour}
            data={totalTimeReportList}
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
}

export default memo(observer(TotalTimeList));
