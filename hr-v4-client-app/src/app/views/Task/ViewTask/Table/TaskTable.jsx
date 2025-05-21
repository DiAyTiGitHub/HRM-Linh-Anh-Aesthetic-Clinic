import GlobitsTable from "app/common/GlobitsTable";
import React from "react";
import { useStore } from "../../../../stores";
import DescriptionIcon from "@material-ui/icons/Description";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import { useHistory } from "react-router-dom";
import moment from "moment";
import { Tooltip } from "@material-ui/core";
import BallotIcon from '@material-ui/icons/Ballot';

function TaskTable() {
  const { t } = useTranslation();
  const history = useHistory();
  const { taskStore } = useStore();
  const {
    pageIndex,
    totalPage,
    pageSize,
    totalElements,
    listTaskPaging,
    setRowsPerPage,
    handleChangePage,
    handleOpenTaskPopup,
    getPagingTaskProject,
    currentProject,
    canExportExcel,
    handleExportExcel,
    handleChangeTablePageSize,
    handleChangeTablePageIndex
  } = taskStore;

  // useEffect(() => {
  //   if (currentProject?.id) {
  //     getPagingTaskProject();
  //   }
  // }, [currentProject, getPagingTaskProject]);

  const columns = [
    {
      title: "Mã",
      field: "code",
      align: "center",
      headerStyle: { fontSize: "15px" },
      render: (rowData) =>
        rowData?.code && (
          <span className="pr-4">
            {rowData?.projectCode}
            {rowData?.code && "#" + rowData?.code}
          </span>
        ),
    },
    {
      title: t("task.list.task"),
      field: "name",
      align: "center",
      headerStyle: { fontSize: "15px" },
      render: (rowData) => (
        <Tooltip title="Xem chi tiết" placement="top">
          <div
            className="flex pr-4 align-center taskTableRowInfo"
            onClick={() => handleOpenTaskPopup(rowData?.id)}
          >
            <BallotIcon fontSize="small" />
            <div
              className="name pl-6"
            >
              {rowData?.name || ""}
            </div>
          </div>
        </Tooltip>
      ),
    },

    {
      title: "Hoạt động",
      field: "activity",
      align: "center",
      headerStyle: { fontSize: "15px" },
      render: (rowData) => <span className="px-4">{rowData?.activity}</span>,
    },
    {
      title: t("task.list.status"),
      field: "statusName",
      align: "center",
      headerStyle: { fontSize: "15px" },
      render: (rowData) => {
        return (
          <div className="flex align-center pr-4">
            {rowData?.statusName && (
              <span className="tagInTable bgc-primary">{rowData?.statusName}</span>
            )}
          </div>
        )
      },
    },

    {
      title: t("task.list.staff"),
      field: "assignee",
      align: "center",
      headerStyle: { fontSize: "15px" },
      render: (rowData) => <span className="pr-4 w-100 text-center flex">{rowData?.assignee}</span>,
    },
    {
      title: "Ngày tạo",
      field: "createDate",
      align: "center",
      headerStyle: { fontSize: "15px" },
      render: (rowData) => <span className="pr-4">{rowData?.createDate && moment(rowData?.createDate).format("hh:mm DD-MM-YYYY")}</span>,

    },
    {
      title: "Người tạo",
      field: "createDate",
      align: "center",
      headerStyle: { fontSize: "15px" },
      render: (rowData) => <span className="pr-4">{rowData?.creatorName}</span>,
    },
    {
      title: "Cập nhật",
      field: "lastModifyDate",
      align: "center",
      headerStyle: { fontSize: "15px" },
      render: (rowData) =>
        <span className="pr-4">{moment(rowData?.lastModifyDate).format("hh:mm DD-MM-YYYY")}</span>
    },
    {
      title: t("task.list.priority"),
      field: "priority",
      align: "center",
      headerStyle: { fontSize: "15px" },
      render: (rowData) => (
        <div>
          {rowData?.priority && (
            <span className="tagInTable">
              {" "}
              {rowData.priority === 1 ? (
                <span
                  style={{
                    backgroundColor: "rgb(10 255 15)",
                    padding: "2px 4px",
                    borderRadius: "3px",
                  }}
                >
                  Thấp
                </span>
              ) : rowData.priority === 2 ? (
                <span className="tagInTable bgc-primary">Trung bình</span>
              ) : rowData.priority === 3 ? (
                <span className="tagInTable bgc-warning-d1">Cao</span>
              ) : rowData.priority === 4 ? (
                <span className="tagInTable bgc-danger-tp1">Cấp bách</span>
              ) : (
                <></>
              )}
            </span>
          )}
        </div>
      ),
    },
  ];

  function handleOpenFormCreateNewTask() {
    handleOpenTaskPopup(null, null, 2);
  }

  return (
    <GlobitsTable
      data={listTaskPaging}
      columns={columns}
      totalPages={totalPage}
      handleChangePage={handleChangeTablePageIndex}
      setRowsPerPage={handleChangeTablePageSize}
      pageSize={pageSize}
      pageSizeOption={[10, 25, 50]}
      totalElements={totalElements}
      page={pageIndex}
    />
  );
}

export default observer(TaskTable);
