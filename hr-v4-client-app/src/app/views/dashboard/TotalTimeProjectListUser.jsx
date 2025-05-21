import React from "react";
import GlobitsTableNotPagination from "../../common/GlobitsTableNotPagination";
import { useStore } from "../../stores";
// import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";

export default observer(function TotalTimeList() {
  const { dashboardStore } = useStore();
  // const { t } = useTranslation();

  const { totalProjectTimeReportUserList } =
    dashboardStore;

  let columns = [
    {
      //title: "Số nhân viên",
      render: (rowData) => {
        return (
          <>
            <div style = {{display: 'flex', marginLeft: "16px"}}>
              <div style={{ width: "50%" }}>
                {rowData.project ? (
                  <span>
                    <strong>Dự án: </strong>
                    <strong>{rowData.project}</strong>
                  </span>
                ) : (
                  ""
                )}
              </div>
              <div>
                <div>
                  <span>Số nhân viên: </span>
                  <span>{rowData.numberStaff ? rowData.numberStaff : 0}</span>
                </div>
                <div style = {{marginTop: "6px"}}>
                  <span>Tổng thời gian: </span>
                  <span>{rowData.totalHours ? rowData.totalHours : 0}</span>
                </div>
              </div>
            </div>
          </>
        );
      },
    },
  ];

  return (
    <GlobitsTableNotPagination
      //handleSelectList={handleSelectListTotalHour}
      data={totalProjectTimeReportUserList}
      columns={columns}
    />
  );
});
