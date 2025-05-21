import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
// import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { useHistory } from "react-router-dom";
import ConstantList from "../../appConfig";
import { Link } from "react-router-dom";

function TotalTimeProjectList() {
  const history = useHistory();
  const { dashboardStore } = useStore();
  // const { t } = useTranslation();

  const {
    totalProjectTimeReport,
    roundHour
  } = dashboardStore;

  const columns = [
    {
      title: "Dự án",
      field: "project",
      minWidth: "30%",
      render: (rowData) => {
        return (
          <>
            <Link onClick={() => { history.push(`${ConstantList.ROOT_PATH}timesheet/list/${rowData.projectId}`); }}>
              {rowData.project ? (
                <div>
                  <strong>{rowData.project}</strong>
                </div>
              ) : (
                ""
              )}
              <div style={{ width: "50%" }}>
                <span>Số nhân viên: </span>
                <span>{rowData.numberStaff ? rowData.numberStaff : 0}</span>
              </div>
            </Link>
          </>
        );
      },
    },
    {
      title: "Tổng thời gian",
      field: "totalHours",
      render: (rowData) => {
        return (
          <div>
            <span>
              {rowData.totalHours ? roundHour(rowData.totalHours) : "0.0"}
            </span>
            <span> h</span>
          </div>
        );
      },
    },
  ];

  return (
    <GlobitsTable
      //handleSelectList={handleSelectListTotalHour}
      data={totalProjectTimeReport}
      columns={columns}
      title={true}
      nonePagination
      maxHeight="500px"
    />
  );
}

export default memo(observer(TotalTimeProjectList));
