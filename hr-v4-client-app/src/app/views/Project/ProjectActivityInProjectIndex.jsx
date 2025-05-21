import React, { useEffect, useState, memo } from "react";
import { useParams } from "react-router";
import { observer } from "mobx-react";
import DescriptionIcon from "@material-ui/icons/Description";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { Grid, Button } from "@material-ui/core";
import moment from "moment";
import { useStore } from "../../stores";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import GlobitsTable from "app/common/GlobitsTable";
import { makeStyles } from "@material-ui/core/styles";
import ActivityCreateEditPopup from "./ProjectActivity/ActivityCreateEditPopup";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsSearchInput from "app/common/GlobitsSearchInput";
import MaterialTable, { MTableBodyRow, MTableEditRow } from "material-table";
import { Radio } from "@material-ui/core";
import SearchIcon from "@material-ui/icons/Search";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import { ArrowUpward, ChevronRight } from "@material-ui/icons";
import ProjectActivityFilter from "./ProjectActivityFilter";

const useStyles = makeStyles({
  actions: {
    border: "1px solid #000",
  },
  tableRow: {
    "& td,& th": {
      border: "1px solid #000",
    },
    // "& td:last-child": {
    //   width: "10% !important",
    // },
    // "& td:first-child": {
    //   width: "3% !important",
    // },
    "& div": {
      justifyContent: "center",
      "& .MuiIconButton-root": {
        padding: "4px !important",
      },
    },
  },
});

function ProjectActivityInProjectIndex() {
  const { id } = useParams();
  const { t } = useTranslation();
  const { projectStore, projectActivityStore } = useStore();

  const {
    pageIndex,
    pageSize,
    totalElements,
    totalPages,
    handleChangePage,
    setRowsPerPage,
    resetStore,
    handleOpenActivityPopup,
    handleDeleteActivity,
    getListActivityById,
    listActivities,
    setProjectId,
    openCreateEditPopup,
    openConfirmDeletePopup,
    handleClosePopup,
    handleConfirmDeleteActivity,
    handleSearching,

  } = projectActivityStore;

  const {
    dataEditProject,
    // listActivity,
    getActivityByProject,
    handleOpenPopupFormActivity,
    canCreateUpdateDeleteProject,
    handleOpenTaskByActivity,
  } = projectStore;

  const [selectedRow, setSelectedRow] = useState(null);
  const classes = useStyles();

  useEffect(function () {
    setProjectId(id);
    // pagingProjectActivity({ projectId: id, includeAll: true });
    getListActivityById({ projectId: id, includeAll: true });
    return resetStore;
  }, []);

  let columns = [
    {
      title: "Mã hoạt động",
      field: "code",
      headerStyle: { fontSize: "15px" },
      render: (rowData) => rowData?.code,
    },
    {
      title: "Tên hoạt động",
      field: "name",
      headerStyle: { fontSize: "15px" },
      render: (rowData) => (
        <div className="flex align-center">
          <Tooltip title="Timesheet" placement="top">
            <IconButton
              aria-label="description"
              onClick={() => handleOpenTaskByActivity(rowData?.id)}
            >
              <DescriptionIcon />
            </IconButton>
          </Tooltip>

          {rowData?.name}
        </div>
      ),
    },
    // {
    //     title: "Thời lượng (giờ)",
    //     field: "duration",
    //     headerStyle: { fontSize: "15px" },
    //     render: rowData => rowData?.duration,
    // },
    {
      title: "Thời gian bắt đầu",
      field: "startTime",
      headerStyle: { fontSize: "15px" },
      render: (rowData) =>
        rowData?.startTime && moment(rowData?.startTime).format("DD/MM/YYYY"),
    },
    {
      title: "Thời gian kết thúc",
      field: "endTime",
      headerStyle: { fontSize: "15px" },
      render: (rowData) =>
        rowData?.endTime && moment(rowData?.endTime).format("DD/MM/YYYY"),
    },
    {
      title: t("general.action"),
      headerStyle: { textAlign: "center" },
      render: (rowData) => (
        <div className="flex flex-center">
          <IconButton
            size="small"
            onClick={() => handleOpenActivityPopup(rowData, id)}
          >
            <Icon fontSize="small" color="primary">
              edit
            </Icon>
          </IconButton>
          <IconButton
            size="small"
            onClick={() => handleDeleteActivity(rowData)}
          >
            <Icon fontSize="small" color="secondary">
              delete
            </Icon>
          </IconButton>
        </div>
      ),
    },
  ];

  function handleSearchActivity(values) {
    const searchObject = {
      ...values,
      projectId: id,
      includeAll: true,
    };

    handleSearching(searchObject);
    // pagingProjectActivity(searchObject); 
  }

  return (
    <>
      <Grid container spacing={2}>
        <Grid item lg={6} md={6} sm={4} xs={4}>
          {canCreateUpdateDeleteProject() && (
            <Button
              className="btn btn-primary d-inline-flex"
              startIcon={<AddIcon />}
              variant="contained"
              onClick={handleOpenActivityPopup}
            >
              {t("general.button.add")}
            </Button>
          )}
        </Grid>

        <Grid item lg={6} md={6} sm={8} xs={8}>
          {/* <GlobitsSearchInput search={handleSearchActivity} t={t} /> */}

          {/* New filter writen by DiAyTi */}
          <ProjectActivityFilter />
        </Grid>

        <Grid item xs={12}>
          <MaterialTable
            classes={{ actions: classes.actions }}
            title="Danh sách hoạt động"
            actions={!canCreateUpdateDeleteProject() ? [] : [
              {
                icon: "add",
                tooltip: "Thêm hoạt động",
                onClick: (event, rowData) =>
                  getActivityByProject(dataEditProject?.id, rowData?.id).then(
                    () => handleOpenActivityPopup(rowData, true, id)
                  ),
              },
              (rowData) => ({
                icon: "edit",
                tooltip: "Sửa hoạt động",
                onClick: (event, rowData) => handleOpenActivityPopup(rowData),
              }),
              (rowData) => ({
                icon: "delete",
                tooltip: "Xóa hoạt động",
                onClick: (event, rowData) =>
                  handleDeleteActivity({ ...rowData, project: { id } }),
              }),
            ]}
            columns={[
              {
                field: "code",
                title: "Mã",
                cellStyle: {
                  width: "8%",
                  textAlign: "center",
                },
              },
              {
                field: "name",
                title: "Tên hoạt động",
                cellStyle: {
                  textAlign: "left",
                },
                render: (rowData) => (
                  <>
                    <div className="flex flex-start flex-middle">
                      <Tooltip placement="top" title="Danh sách phần việc của hoạt động">
                        <IconButton
                          className="px-6"
                          aria-label="description"
                          onClick={() => handleOpenTaskByActivity(rowData?.id)}
                        >
                          <DescriptionIcon />
                        </IconButton>
                      </Tooltip>
                      <p className="p-0 m-0">{rowData?.name}</p>
                    </div>
                  </>
                ),
              },
              // {
              //   field: "duration",
              //   title: "Ước lượng",
              //   emptyValue: "0",
              //   cellStyle: {
              //     textAlign: "center",
              //   },
              // },
              {
                field: "startTime",
                title: "Thời gian bắt đầu",
                emptyValue: " ",
                cellStyle: {
                  width: "12%",
                  textAlign: "center",
                },
                render: (rowData) => (
                  <span className="px-8">
                    {moment(rowData?.startTime).format("DD/MM/YYYY hh:mm A")}{" "}
                  </span>
                ),
              },
              {
                field: "endTime",
                title: "Thời gian kết thúc",
                emptyValue: " ",
                cellStyle: {
                  width: "12%",
                  textAlign: "center",
                },
                render: (rowData) => (
                  <span className="px-8">
                    {moment(rowData?.endTime).format("DD/MM/YYYY hh:mm A")}
                  </span>
                ),
              },
              {
                field: "description",
                title: "Mô tả",
                emptyValue: " ",
                cellStyle: {
                  textAlign: "left",
                  width: "32%",
                  padding: "2px 8px"
                },
                // render: (rowData) => (
                //   <span>
                //     {moment(rowData?.endTime).format("DD/MM/YYYY hh:mm A")}
                //   </span>
                // ),
              },
            ]}
            data={listActivities}
            parentChildData={(row, rows) =>
              rows.find((a) => a?.id === row?.parentId)
            }
            onRowClick={(evt, selectedRow) =>
              setSelectedRow(selectedRow?.tableData?.id)
            }
            options={{
              toolbar: false,
              showTitle: false,
              search: false,
              paging: false,
              defaultExpanded: true,
              headerStyle: {
                backgroundColor: "rgb(246 246 246)",
                color: "#717276",
                fontSize: "13px",
                textAlign: "center",
                fontWeight: "bold",
                border: "1px solid #000 ",
              },
              actionsColumnIndex: -1,
              rowStyle: (rowData, index) => ({
                backgroundColor:
                  selectedRow === rowData?.tableData?.id ? "#EEE" : "#FFF",
                fontWeight: rowData?.parent ? "normal" : "bold",
              }),
            }}
            components={{
              Row: (props) => (
                <MTableBodyRow {...props} className={classes.tableRow} />
              ),
              EditRow: (props) => (
                <MTableEditRow {...props} className={classes.tableRow} />
              ),
            }}
            localization={{
              body: {
                emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
              },
            }}
          />
        </Grid>
      </Grid>

      {openCreateEditPopup && (
        <ActivityCreateEditPopup dataEditProject={dataEditProject} />
      )}

      {openConfirmDeletePopup && (
        <GlobitsConfirmationDialog
          open={openConfirmDeletePopup}
          onConfirmDialogClose={handleClosePopup}
          onYesClick={() => handleConfirmDeleteActivity(dataEditProject)}
          title={t("confirm_dialog.delete.title")}
          text={t("confirm_dialog.delete.text")}
          agree={t("confirm_dialog.delete.agree")}
          cancel={t("confirm_dialog.delete.cancel")}
        />
      )}
    </>
  );
}

export default memo(observer(ProjectActivityInProjectIndex));
