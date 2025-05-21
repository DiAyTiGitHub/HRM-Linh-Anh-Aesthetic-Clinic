import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import moment from "moment";
import {
  IconButton,
  Typography,
  withStyles,
  Icon,
  Dialog,
  DialogContent,
  Grid,
} from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import Paper from "@material-ui/core/Paper";
import { useStore } from "../../../stores";
import MuiDialogTitle from "@material-ui/core/DialogTitle";
import CloseIcon from "@material-ui/icons/Close";
import Draggable from "react-draggable";
import MaterialTable from "material-table";
import SearchIcon from "@material-ui/icons/Search";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import { ArrowUpward, ChevronRight } from "@material-ui/icons";
import GlobitsPopup from "app/common/GlobitsPopup";

const useStyles = makeStyles({
  table: {
    "& .MuiPaper-root": {
      maxHeight: "70vh",
      overflow: "auto",
    },
  },
});

const styles = (theme) => ({
  root: {
    margin: 0,
    padding: theme.spacing(2),
  },
  closeButton: {
    position: "absolute",
    right: theme.spacing(1),
    top: theme.spacing(1),
    color: theme.palette.grey[500],
  },
});

function PaperComponent(props) {
  return (
    <div className="paper-container">
      <Draggable
        handle="#draggable-dialog-title"
        cancel={'[class*="MuiDialogContent-root"]'}
      >
        <Paper {...props} />
      </Draggable>
    </div>
  );
}

const DialogTitle = withStyles(styles)((props) => {
  const { children, classes, onClose, ...other } = props;
  return (
    <MuiDialogTitle disableTypography className={classes.root} {...other}>
      <Typography variant="h6">{children}</Typography>
      {onClose ? (
        <IconButton
          aria-label="close"
          className={classes.closeButton}
          onClick={onClose}
        >
          <CloseIcon />
        </IconButton>
      ) : null}
    </MuiDialogTitle>
  );
});

export default observer(function ListTaskByActivity() {
  const { t } = useTranslation();
  const classes = useStyles();

  const { projectStore } = useStore();

  const { selectedTaskList, handleClose, shouldOpenTaskListByActivity } =
    projectStore;
  const [selectedRow, setSelectedRow] = useState(null);

  return (
    <>
      {/* <Dialog
        className="dialog-container"
        open={shouldOpenTaskListByActivity}
        PaperComponent={PaperComponent}
        fullWidth
        maxWidth="md"
      >
        <DialogTitle
          className="dialog-header"
          style={{ cursor: "move" }}
          id="draggable-dialog-title"
        >
          <span className="mb-20">Danh sách Công việc</span>
        </DialogTitle>
        <IconButton
          style={{ position: "absolute", right: "8px", top: "8px" }}
          onClick={() => {
            handleClose();
          }}
        >
          <Icon color="disabled" title={t("general.close")}>
            close
          </Icon>
        </IconButton>
        <DialogContent className={classes.table}>
          <MaterialTable
            title="Danh sách thời gian biểu"
            icons={{
              Filter: React.forwardRef((props, ref) => (
                <SearchIcon ref={ref} />
              )),
              Search: React.forwardRef((props, ref) => (
                <SearchIcon ref={ref} />
              )),
              ResetSearch: React.forwardRef((props, ref) => (
                <RotateLeftIcon ref={ref} />
              )),
              SortArrow: ArrowUpward,
              DetailPanel: ChevronRight,
            }}
            columns={[
              {
                field: "code",
                title: "#",
                cellStyle: {
                  padding: "0 10px",
                },
                render: (rowData) => (
                  <> {`${rowData.projectCode}#${rowData.code}`}</>
                ),
              },
              {
                field: "name",
                title: "Tên",
                cellStyle: {
                  padding: "0 10px",
                },
              },
              {
                field: "priority",
                title: "Mức độ ưu tiên",
                render: (rowData) => (
                  <>
                    {rowData.priority === 1 ? (
                      <span>Thấp</span>
                    ) : rowData.priority === 2 ? (
                      <span
                        style={{
                          padding: "10px",
                          borderRadius: "10px",
                          color: "#fff",
                          backgroundColor: "#0069D9",
                        }}
                      >
                        Trung bình
                      </span>
                    ) : rowData.priority === 3 ? (
                      <span
                        style={{
                          padding: "10px",
                          borderRadius: "10px",
                          color: "#fff",
                          backgroundColor: "#E0A800",
                        }}
                      >
                        Cao
                      </span>
                    ) : (
                      <span
                        style={{
                          padding: "10px",
                          borderRadius: "10px",
                          color: "#fff",
                          backgroundColor: "#C82333",
                        }}
                      >
                        Cấp bách
                      </span>
                    )}
                  </>
                ),
                cellStyle: {
                  textAlign: "center",
                },
              },
              {
                field: "assignee",
                title: "Người thực hiện",
                cellStyle: {
                  textAlign: "center",
                },
              },
              {
                field: "statusName",
                title: "Trạng thái",
                cellStyle: {
                  textAlign: "center",
                },
              },
            ]}
            data={selectedTaskList}
            onRowClick={(evt, selectedRow) => {
              setSelectedRow(selectedRow.tableData.id);
            }}
            options={{
              showTitle: false,
              paging: false,
              headerStyle: {
                backgroundColor: "#01C0C8",
                color: "#FFF",
                fontSize: "17px",
                textAlign: "center",
                fontWeight: "bold",
              },
              actionsColumnIndex: -1,
              rowStyle: (rowData, index) => ({
                marginLeft: rowData.parent != null ? "15px" : "0px",
                backgroundColor:
                  selectedRow === rowData.tableData.id ? "#EEE" : "#FFF",
              }),
            }}
            localization={{
              body: {
                emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
              },
              header: {
                actions: "Lựa chọn",
              },
              toolbar: {
                searchPlaceholder: "Tìm kiếm",
              },
            }}
          />
        </DialogContent>
      </Dialog> */}


      <GlobitsPopup
        scroll={"body"}
        size="lg"
        open={shouldOpenTaskListByActivity}
        noDialogContent
        title={"Danh sách Công việc thuộc hoạt động"}
        onClosePopup={handleClose}
      >
        <div className="dialog-body">
          <DialogContent className="p-12">
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <MaterialTable
                  title="Danh sách thời gian biểu"
                  icons={{
                    Filter: React.forwardRef((props, ref) => (
                      <SearchIcon ref={ref} />
                    )),
                    Search: React.forwardRef((props, ref) => (
                      <SearchIcon ref={ref} />
                    )),
                    ResetSearch: React.forwardRef((props, ref) => (
                      <RotateLeftIcon ref={ref} />
                    )),
                    SortArrow: ArrowUpward,
                    DetailPanel: ChevronRight,
                  }}
                  columns={[
                    {
                      field: "code",
                      title: "#",
                      cellStyle: {
                        padding: "2px 8px",
                        textAlign: "center",
                      },
                      render: (rowData) => (
                        <> {`${rowData.projectCode}#${rowData.code}`}</>
                      ),
                    },
                    {
                      field: "name",
                      title: "Tên",
                      cellStyle: {
                        padding: "4px 8px",
                      },
                    },
                    {
                      field: "priority",
                      title: "Mức độ ưu tiên",
                      render: (rowData) => (
                        <>
                          {rowData.priority === 1 ? (
                            <span>Thấp</span>
                          ) : rowData.priority === 2 ? (
                            <span
                              style={{
                                padding: "4px 8px",
                                borderRadius: "4px",
                                color: "#fff",
                                backgroundColor: "#0069D9",
                              }}
                            >
                              Trung bình
                            </span>
                          ) : rowData.priority === 3 ? (
                            <span
                              style={{
                                padding: "4px 8px",
                                borderRadius: "4px",
                                color: "#fff",
                                backgroundColor: "#E0A800",
                              }}
                            >
                              Cao
                            </span>
                          ) : (
                            <span
                              style={{
                                padding: "4px 8px",
                                borderRadius: "4px",
                                color: "#fff",
                                backgroundColor: "#C82333",
                              }}
                            >
                              Cấp bách
                            </span>
                          )}
                        </>
                      ),
                      cellStyle: {
                        textAlign: "center",
                      },
                    },
                    {
                      field: "assignee",
                      title: "Người thực hiện",
                      cellStyle: {
                        textAlign: "center",
                      },
                    },
                    {
                      field: "statusName",
                      title: "Trạng thái",
                      cellStyle: {
                        textAlign: "center",
                      },
                    },
                  ]}
                  data={selectedTaskList}
                  onRowClick={(evt, selectedRow) => {
                    setSelectedRow(selectedRow.tableData.id);
                  }}
                  options={{
                    showTitle: false,
                    paging: false,
                    headerStyle: {
                      backgroundColor: "rgb(246 246 246)",
                      color: "#717276",
                      position: "sticky",
                      maxWidth: "auto",
                      fontSize: "14px",
                      textAlign: "center",
                    },
                    actionsColumnIndex: -1,
                    rowStyle: (rowData, index) => ({
                      marginLeft: rowData.parent != null ? "15px" : "0px",
                      backgroundColor:
                        selectedRow === rowData.tableData.id ? "#EEE" : "#FFF",
                    }),
                  }}
                  localization={{
                    body: {
                      emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
                    },
                    header: {
                      actions: "Lựa chọn",
                    },
                    toolbar: {
                      searchPlaceholder: "Tìm kiếm",
                    },
                  }}
                />
              </Grid>

            </Grid>

          </DialogContent>
        </div>
      </GlobitsPopup >
    </>
  );
});
