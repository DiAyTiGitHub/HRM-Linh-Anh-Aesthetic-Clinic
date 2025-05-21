import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { makeStyles, Grid, Button } from "@material-ui/core";
import { observer } from "mobx-react";
import moment from "moment";
import { useStore } from "app/stores";
import { useState } from "react";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsTable from "app/common/GlobitsTable";
import { Form, Formik } from "formik";
import PostAddIcon from "@material-ui/icons/PostAdd";
import { toast } from "react-toastify";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import { saveAs } from "file-saver";
import GlobitsSearchInput from "./GlobitsSearchInput";
import { exportToExcelStaffNotTimeSheet } from "app/views/TimeSheet/TimeSheetService";

const useStyles = makeStyles((theme) => ({
  root: {
    "& .MuiTable-root": {
      overflow: "auto !important",
    },

    "& .MuiTableCell-head": {
      textAlign: "center",
    },

    "& .MuiPaper-root": {
      height: "40vh",
      overflow: "auto !important",
    },

    "& .MuiGrid-grid-xs-4": {
      display: "flex",

      "& label": {
        margin: "auto 10px auto 0",
        fontWeight: "500",
      },
    },
  },
}));

export default observer(function StaffPopupNotDetailList() {
  const { timeSheetStore } = useStore();
  const { t } = useTranslation();
  const classes = useStyles();

  const {
    selectedListStaffNotDetail,
    totalPages,
    totalElements,
    rowsPerPage,
    page,
    handleChangePage_StaffNotTimeSheet,
    setRowsPerPage_StaffNotTimeSheet,
    updatePageData_StaffNotTimeSheet,
  } = timeSheetStore;

  const [initialValues, setInitialValues] = useState({
    date: Date.now(),
  });

  const [staffList, setStaffList] = useState([]);

  useEffect(() => {
    if (selectedListStaffNotDetail) setStaffList(selectedListStaffNotDetail);
  }, [selectedListStaffNotDetail]);

  useEffect(() => {
    if (updatePageData_StaffNotTimeSheet)
      updatePageData_StaffNotTimeSheet({ workingDate: initialValues?.date });
  }, [updatePageData_StaffNotTimeSheet, initialValues, setInitialValues]);

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const isMobileXS = useMediaQuery(theme.breakpoints.down("xs"));

  const handleExportExcel = () => {
    if (
      selectedListStaffNotDetail != null &&
      selectedListStaffNotDetail.length > 0
    ) {
      exportToExcelStaffNotTimeSheet({ workingDate: initialValues?.date })
        .then((res) => {
          toast.success(t("general.successExport"));
          let blob = new Blob([res.data], {
            type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
          });
          saveAs(blob, "NhanVien.xlsx");
        })
        .catch((err) => {
          console.log(err);
        });
    } else {
      toast.warning(t("general.noData"));
    }
  };

  let columns = [
    {
      title: "Nhân viên",
      minWidth: "200px",
      render: (rowData) => {
        return (
          <div style={{ textAlign: "center" }}>
            {rowData.displayName ? (
              <div>
                <strong>{rowData.displayName}</strong>
              </div>
            ) : (
              ""
            )}
            {rowData.birthDate ? (
              <div>
                <span>
                  Ngày sinh: {moment(rowData.birthDate).format("DD/MM/YYYY")}
                </span>
              </div>
            ) : (
              ""
            )}
            {rowData.gender ? (
              <span>
                Giới tính:
                {rowData.gender === "M" ? (
                  <span> Nam</span>
                ) : rowData.gender === "F" ? (
                  <span> Nữ</span>
                ) : (
                  ""
                )}
              </span>
            ) : (
              ""
            )}
            {rowData.birthPlace ? (
              <div>
                <span>Nơi sinh: {rowData.birthPlace}</span>
              </div>
            ) : (
              ""
            )}
          </div>
        );
      },
    },
    {
      title: "Thông tin liên hệ",
      minWidth: "250px",
      // field: "activity.name",
      render: (rowData) => {
        return (
          <div style={{ textAlign: "center" }}>
            {rowData.phoneNumber ? (
              <div>
                <span>SĐT: {rowData.phoneNumber}</span>
              </div>
            ) : (
              ""
            )}
            {rowData.email ? (
              <div>
                <span>Email: {rowData.email}</span>
              </div>
            ) : (
              ""
            )}
          </div>
        );
      },
    },
  ];

  return (
    <Formik initialValues={initialValues} enableReinitialize>
      {() => {
        return (
          <Form autoComplete="off">
            <div className={classes.root}>
              <Grid spacing={1} container xs={12}>
                <Grid item lg={6} md={6} sm={4} xs={4}></Grid>
                <Grid item lg={6} md={6} sm={8} xs={8}>
                  <GlobitsSearchInput
                    search={updatePageData_StaffNotTimeSheet}
                    initialValues={initialValues}
                  />
                </Grid>
                <Grid item xs={4}>
                  <GlobitsDateTimePicker
                    label="Ngày:"
                    name="date"
                    // disableFuture={true}
                    onChange={(value) => {
                      setInitialValues({ date: value });
                    }}
                  />
                </Grid>
                <Grid item xs={8}>
                  <Button
                    className={`btn btn-secondary ${
                      !isMobile ? "d-inline-flex" : "d-block w-100"
                    } mr-16 `}
                    startIcon={<PostAddIcon />}
                    variant="contained"
                    onClick={handleExportExcel}
                  >
                    {!isMobileXS && t("general.button.exportExcel")}
                  </Button>
                </Grid>
                <Grid item xs={12}>
                  <GlobitsTable
                    selected
                    data={staffList}
                    // handleSelectList={handleSelectListTimeSheetDetail}
                    columns={columns}
                    totalPages={totalPages}
                    handleChangePage={handleChangePage_StaffNotTimeSheet}
                    setRowsPerPage={setRowsPerPage_StaffNotTimeSheet}
                    pageSize={rowsPerPage}
                    pageSizeOption={[10, 25, 50]}
                    totalElements={totalElements}
                    page={page}
                  />
                </Grid>
              </Grid>
            </div>
          </Form>
        );
      }}
    </Formik>
  );
});
