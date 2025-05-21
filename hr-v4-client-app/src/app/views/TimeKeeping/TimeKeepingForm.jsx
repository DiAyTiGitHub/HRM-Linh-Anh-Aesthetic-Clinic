import React from "react";
import { Formik, Form } from "formik";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import { Grid, DialogActions, Button } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsRadioGroup from "app/common/form/GlobitsRadioGroup";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import LocalConstants from "app/LocalConstants";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import StaffPopupNotTimeKeepingIn from "./StaffNotTimeKeepingIn/StaffPopupNotTimeKeepingIn";
import moment from "moment";
import GlobitsPopup from "app/common/GlobitsPopup";
import { formatDate } from "app/LocalFunction";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

const useStyles = makeStyles({
  root: {
    "& .MuiDialogContent-root": {
      overflow: "auto !important",
    },
  },
  marginAuto: {
    display: "flex",
    "& label": {
      margin: "auto",
      marginRight: "10px",
      fontWeight: "500",
      fontSize: "16px",
    },
  },
  table: {
    minWidth: 650,
    border: "3px solid #2a80c8 !important",
    borderCollapse: "collapse",

    "& .MuiTableCell-root": {
      border: "none",
    },

    "& .MuiTableRow-head": {
      backgroundColor: "#2a80c8",
      border: "1px solid #2a80c8",
    },

    "& .MuiTableCell-head": {
      border: "1px solid #2a80c8",
      color: "#fff",
    },

    "& .MuiTableCell-body": {
      border: "1px solid #2a80c8",
    },

    "& .MuiFormGroup-root": {
      display: "flex",
      justifyContent: "center",
      alignItems: "center",
    },
  },
  tableBody: {
    "& .MuiCheckbox-root": {
      margin: "auto",
    },
    "& .MuiTextField-root": {
      padding: "5px",
    },
  },
  headerDate: {
    fontSize: "22px",
    fontWeight: "700",
  },
  displayFlex: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
  },
});

export default observer(function TimeKeepingForm() {
  const { timeSheetStore, timeKeepStore } = useStore();
  const { t } = useTranslation();
  const {
    shouldOpenPopupListStaffNotTimeKeepingIn,
  } = timeSheetStore;

  const { dataEditTimeKeep, openFormTimeKeep, handleClosePopup, createTimeKeeping, isUser } = timeKeepStore;

  const classes = useStyles();

  function hanledFormSubmit(values) {
    const timeKeepingList = values.timeSheetShiftWorkPeriods.filter((item) => {
      return (
        item.workingFormat !== "" &&
        item.workingFormat !== null &&
        item.workingFormat !== undefined
      );
    });

    createTimeKeeping({
      ...values,
      timeSheetShiftWorkPeriods: timeKeepingList.map((item) => {
        return { ...item, workingFormat: Number(item.workingFormat) }
      }),
    });
  }

  return (
    <GlobitsPopupV2
      open={openFormTimeKeep}
      onClosePopup={handleClosePopup}
      noDialogContent
      title={t("timeKeeping.title")}
    >
      <Formik
        enableReinitialize
        initialValues={dataEditTimeKeep}
        onSubmit={(values) => hanledFormSubmit(values)}
      >
        {({ values, setFieldValue }) => {
          return (
            <Form autoComplete="off" className=" mt-12">
              <div className={`dialog-body ${classes.root} px-12`}>
                <Grid container spacing={2}>
                  <Grid item xs={12} md={4}>
                    <GlobitsDateTimePicker
                      name="workingDate"
                      label="Ngày:"
                    />
                  </Grid>

                  {/* <Grid item xs={12} md={4}>
                    {!isUser && (
                      <Button
                        className={`btn btn-warning ${!isMobile ? "d-inline-flex " : "d-block w-100 "
                          }`}
                        startIcon={<PersonIcon />}
                        variant="contained"
                        style={{ marginTop: "25px" }}
                        onClick={() => {
                          handleOpenPopupListStaffNotTimeKeepingIn();
                        }}
                      >
                        {!isMobileXS && "Danh sách nhân viên"}
                      </Button>
                    )}
                  </Grid> */}

                  <StaffPopupNotTimeKeepingIn
                    open={shouldOpenPopupListStaffNotTimeKeepingIn}
                    timeKeeping={values}
                  />

                  <Grid item xs={12}>
                    <TableContainer component={Paper}>
                      <Table
                        className={`${classes.table} mb-12`}
                        aria-label="simple table"
                      >
                        <TableHead>
                          <TableRow>
                            <TableCell align="center" style={{ width: "15%" }}>
                              {t("timeKeeping.shiftWork")}
                            </TableCell>
                            <TableCell align="center" style={{ width: "15%" }}>
                              {t("timeKeeping.startTime")}
                            </TableCell>
                            <TableCell align="center" style={{ width: "15%" }}>
                              {t("timeKeeping.endTime")}
                            </TableCell>
                            <TableCell align="center" style={{ width: "35%" }}>
                              {t("timeKeeping.workingFormat")}
                            </TableCell>
                            <TableCell align="center" style={{ width: "20%" }}>
                              {t("timeKeeping.note")}
                            </TableCell>
                          </TableRow>
                        </TableHead>

                        <TableBody>
                          {Array.isArray(values?.timeSheetShiftWorkPeriods) > 0
                            ? values?.timeSheetShiftWorkPeriods?.map(
                              (item, index) => {

                                return (
                                  <TableRow key={index} className={classes.tableBody}>
                                    <TableCell
                                      component="th"
                                      scope="row"
                                      align="center"
                                      // rowSpan={rowSpan[index]}
                                      style={{
                                        textTransform: "uppercase",
                                        fontWeight: "500",
                                      }}
                                    >
                                      {item.shiftWorkTimePeriod.shiftWorkDto?.name}
                                    </TableCell>

                                    <TableCell align="center">
                                      {formatDate("HH:mm", item.shiftWorkTimePeriod.startTime)}
                                    </TableCell>
                                    <TableCell align="center">
                                      {formatDate("HH:mm", item.shiftWorkTimePeriod.endTime)}
                                    </TableCell>
                                    <TableCell align="center">
                                      <GlobitsRadioGroup
                                        name={`timeSheetShiftWorkPeriods[${index}].workingFormat`}
                                        keyValue="value"
                                        options={LocalConstants.ListStatus}
                                        disabled={isUser === true && moment(new Date()).format('YYYY MM DD') !== moment(values.workingDate).format('YYYY MM DD')}
                                      />
                                    </TableCell>
                                    <TableCell align="center">
                                      <GlobitsTextField
                                        name={`timeSheetShiftWorkPeriods[${index}].note`}
                                      />
                                    </TableCell>
                                  </TableRow>
                                );
                              }
                            )
                            : "Chưa có dữ liệu !"}
                        </TableBody>
                      </Table>
                    </TableContainer>
                  </Grid>
                </Grid>
              </div>
              
              <div className="dialog-footer px-12">
                <DialogActions className="p-0">
                  <div className="flex flex-space-between flex-middle">
                    <Button
                      startIcon={<BlockIcon />}
                      variant="contained"
                      className="mr-12 btn btn-secondary d-inline-flex"
                      color="secondary"
                      onClick={handleClosePopup}
                    >
                      {t("general.button.cancel")}
                    </Button>
                    <Button
                      startIcon={<SaveIcon />}
                      className="mr-0 btn btn-primary d-inline-flex"
                      variant="contained"
                      color="primary"
                      type="submit"
                    >
                      {t("general.button.save")}
                    </Button>
                  </div>
                </DialogActions>
              </div>
            </Form>
          );
        }}
      </Formik>
    </GlobitsPopupV2>
  );
});
