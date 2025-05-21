/* eslint-disable jsx-a11y/anchor-is-valid */
import React, { memo } from "react";
import GlobitsTable from "app/common/GlobitsTable";
import ConstantList from "app/appConfig";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { useHistory } from "react-router-dom";
import { formatDate } from "app/LocalFunction";

function StaffBirthDayByMonthList() {
    const { dashboardStore } = useStore();
    const history = useHistory();

    const {
        listStaffHasBirthDayInMonth
    } = dashboardStore;

    const stafftName = (rowData) => (
        <a onClick={() => { history.push(ConstantList.ROOT_PATH + "staff/view/" + rowData.id) }} style={{ cursor: "pointer", fontWeight: "450" }}>
            {rowData?.displayName}
        </a>
    );

    const columns = [

        {
            title: "Nhân viên",
            field: "displayName",
            headerStyle: { fontSize: "16px", paddingLeft: "16px" },
            minWidth: "72%",
            render: (rowData) => {
                return (
                    < >
                        {rowData?.displayName && (
                            <div style={{ color: "#8999af" }} className="ml-16">
                                <strong>{stafftName(rowData)}</strong>
                            </div>
                        )}
                    </>
                );
            },
        },
        {
            title: "Ngày sinh",
            headerStyle: { fontSize: "14px" },
            render: (rowData) => {
                return (
                    <>
                        {
                            rowData?.birthDate && (
                                <p className="m-0 px-8">{formatDate("DD/MM", rowData.birthDate)}</p>
                            )
                        }
                    </>
                );
            },

        },
    ];

    return (
        <GlobitsTable
            data={listStaffHasBirthDayInMonth || []}
            columns={columns}
            nonePagination={true}
        />
    );
}

export default memo(observer(StaffBirthDayByMonthList));
