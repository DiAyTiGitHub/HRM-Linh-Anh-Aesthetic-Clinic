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
import { formatDate, getDateTime } from "app/LocalFunction";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { InputOutlined, PlayCircleOutline } from "@material-ui/icons";
import { pagingStaffWorkSchedule } from "../StaffWorkScheduleV2/StaffWorkScheduleService";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { useParams } from "react-router";

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

export default observer(function TimeKeepingFormV2() {
  const { timeSheetStore, timeKeepStore } = useStore();
  const { t } = useTranslation();
  const { id } = useParams();
  const {
    shouldOpenPopupListStaffNotTimeKeepingIn,
  } = timeSheetStore;

  const { dataEditTimeKeep, openFormTimeKeep,
    handleClosePopup, createTimeKeeping, isUser,
    handleSaveTimeSheet, selectedTimeSheet } = timeKeepStore;

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
  const formatDuration = (duration) => {
    if (!duration) return "0";

    const [hours, minutes] = duration.toString().split(".");
    const formattedMinutes = minutes ? parseInt(minutes, 10) : 0;

    return `${hours} tiếng ${formattedMinutes} phút`;
  };

  return (
    <GlobitsPopup
      size='sm'
      open={openFormTimeKeep}
      onClosePopup={handleClosePopup}
      noDialogContent
      title={t("timeKeeping.title")}
    >
      <Formik
        enableReinitialize
        initialValues={selectedTimeSheet}
        onSubmit={(values) => handleSaveTimeSheet(values)}
      >
        {({ values, setFieldValue, submitForm }) => {
          return (
            <Form autoComplete="off" className=" mt-12">
              <div className={`dialog-body ${classes.root} px-12`}>
                <Grid container spacing={2}>
                  <Grid item xs={12} md={12}>
                    <GlobitsDateTimePicker
                      name="workingDate"
                      label="Ngày:"
                    />
                  </Grid>
                  <Grid item xs={12} md={12}>
                    <GlobitsPagingAutocomplete
                      label={"Ca làm việc"}
                      name="staffWorkSchedule"
                      searchObject={{
                        fromDate: values?.workingDate,
                        toDate: values?.workingDate,
                        staffId: id ? id : null
                      }}
                      api={pagingStaffWorkSchedule}
                      displayData="shiftWork.name"
                    />
                  </Grid>
                  <Grid item xs={12} md={12}>
                    <GlobitsSelectInput
                      label={"Giai đoạn làm việc trong ca"}
                      name="shiftWorkTimePeriod"
                      options={values?.staffWorkSchedule?.shiftWork?.timePeriods?.map(item => ({
                        name: item.displayTime,
                        value: item
                      })) || []}
                      hideNullOption
                    />
                  </Grid>
                  <Grid item xs={12} md={12}>
                    <GlobitsSelectInput
                      label={"Loại chấm công"}
                      name="typeTimeSheetDetail"
                      hideNullOption
                      options={[
                        { value: 1, name: "Bắt đầu" },
                        { value: 2, name: "Kết thúc" },
                      ]}
                    />
                  </Grid>

                  <StaffPopupNotTimeKeepingIn
                    open={shouldOpenPopupListStaffNotTimeKeepingIn}
                    timeKeeping={values}
                  />

                  {/* <Grid item xs={12}>
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
                              {t("Thời gian bắt đầu")}
                            </TableCell>
                            <TableCell align="center" style={{ width: "15%" }}>
                              {t("Thời gian kết thúc")}
                            </TableCell>
                            <TableCell align="center" style={{ width: "15%" }}>
                              {t("Tổng thời gian làm việc")}
                            </TableCell>
                            <TableCell align="center" style={{ width: "15%" }}>
                              {t("Địa chỉ IP")}
                            </TableCell>
                          </TableRow>
                        </TableHead>

                        <TableBody>
                          {Array.isArray(values?.details) > 0
                            ? values?.details?.map(
                              (item, index) => {
                                return (
                                  <TableRow key={index} className={classes.tableBody}>
                                    <TableCell
                                      component="th"
                                      scope="row"
                                      align="center"
                                      style={{
                                        textTransform: "uppercase",
                                        fontWeight: "500",
                                      }}
                                    >
                                      {item?.staffWorkSchedule?.shiftWork?.name}
                                    </TableCell>

                                    <TableCell align="center">
                                      {item?.startTime ?
                                        getDateTime(item?.startTime) :
                                        <Button
                                          startIcon={<InputOutlined />}
                                          variant={"contained"}
                                          color="primary"
                                          onClick={() => {
                                            setFieldValue(`details.[${index}].startTime`, new Date())
                                            submitForm()
                                          }}
                                        >
                                          Check In
                                        </Button>
                                      }
                                    </TableCell>
                                    <TableCell align="center">
                                      {item?.endTime ?
                                        getDateTime(item?.endTime) :
                                        <Button
                                          startIcon={<PlayCircleOutline />}
                                          variant={"contained"}
                                          color="secondary"
                                          style={{ marginLeft: 8 }}
                                          onClick={() => {
                                            setFieldValue(`details.[${index}].endTime`, new Date())
                                            submitForm()
                                          }}
                                        >
                                          Check Out
                                        </Button>
                                      }
                                    </TableCell>
                                    <TableCell
                                      scope="row"
                                      align="center"
                                      
                                    >
                                      {formatDuration(item?.duration)}
                                    </TableCell>
                                    <TableCell
                                      scope="row"
                                      align="center"
                                      
                                    >
                                      {item?.addressIP}
                                    </TableCell>
                                  </TableRow>
                                );
                              }
                            )
                            : "Chưa có dữ liệu !"}
                        </TableBody>
                      </Table>
                    </TableContainer>
                  </Grid> */}
                </Grid>
              </div>

              <div className="dialog-footer px-12 mt-5">
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
    </GlobitsPopup>
  );
});
