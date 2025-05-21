import React, { memo, useEffect } from "react";
import { useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { formatDate, formatNumber, getCheckInAndCheckOutTimeOfShiftWork } from "app/LocalFunction";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import {
  Grid,
  makeStyles,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow
} from "@material-ui/core";


const useStyles = makeStyles ({
  root:{
    "& .MuiDialogContent-root":{
      // overflow: "auto !important",
      overflow:"unset !important",
    },
  },
  marginAuto:{
    display:"flex",
    "& label":{
      margin:"auto",
      marginRight:"10px",
      fontWeight:"500",
      fontSize:"16px",
    },
  },
  table:{
    border:"1.2px solid #4276a4 !important",
    borderCollapse:"collapse",

    "& .MuiTableCell-root":{
      border:"none",
    },

    "& .MuiTableRow-head":{
      backgroundColor:"#4276a4",
      border:"1px solid #4276a4",
    },

    "& .MuiTableCell-head":{
      border:"1px solid #4276a4",
      color:"#fff",
    },

    "& .MuiTableCell-body":{
      border:"1px solid #4276a4",
    },

    "& .MuiFormGroup-root":{
      display:"flex",
      justifyContent:"center",
      alignItems:"center",
    },
  },
  tableBody:{
    "& .MuiCheckbox-root":{
      margin:"auto",
    },
    "& .MuiTextField-root":{
      padding:"5px",
    },
  },
  headerDate:{
    fontSize:"22px",
    fontWeight:"700",
  },
  displayFlex:{
    display:"flex",
    justifyContent:"center",
    alignItems:"center",
  },
});

function ConfirmOTTableSection (props) {
  const {t} = useTranslation ();
  const classes = useStyles ();
  const {readOnly} = props;

  const {
    hrRoleUtilsStore
  } = useStore ();

  const {
    isAdmin,
    isManager
  } = hrRoleUtilsStore;

  const {
    values,
    setFieldValue
  } = useFormikContext ();

  useEffect (function () {
    let convertedHours = 0;
    if (values?.earlyArrivalMinutes > 0) {
      convertedHours = formatNumber (values?.earlyArrivalMinutes / 60);
    }

    setFieldValue ("earlyArrivalHours", convertedHours);
  }, [values?.earlyArrivalMinutes]);

  useEffect (function () {
    let convertedHours = 0;
    if (values?.lateExitMinutes > 0) {
      convertedHours = formatNumber (values?.lateExitMinutes / 60);
    }

    setFieldValue ("lateExitHours", convertedHours);
  }, [values?.lateExitMinutes]);


  const checkInOutObject = getCheckInAndCheckOutTimeOfShiftWork (values?.shiftWork);

  return (
      <Grid container spacing={2}>
        <Grid item xs={12}>

          {/* <strong>
    Giai đoạn làm việc trong ca
</strong> */}

          <TableContainer component={Paper}>
            <Table
                className={`${classes.table} mb-12`}
                aria-label="simple table"
            >
              <TableHead>
                <TableRow>
                  <TableCell
                      align="center"
                      className="py-4"
                      style={{width:"10%"}}
                  >

                  </TableCell>

                  <TableCell
                      align="center"
                      className="py-4"
                      style={{width:"15%"}}
                  >
                    Thời gian quy định
                  </TableCell>

                  <TableCell
                      align="center"
                      className="py-4"
                      style={{width:"15%"}}
                  >
                    Thời gian NV vào/ra
                  </TableCell>

                  <TableCell
                      align="center"
                      className="py-4"
                      style={{width:"15%"}}
                  >
                    Số phút đi sớm/về muộn
                  </TableCell>

                  <TableCell
                      align="center"
                      className="py-4"
                      style={{width:"15%"}}
                  >
                    Số giờ quy đổi
                  </TableCell>

                  <TableCell
                      align="center"
                      className="py-4"
                      style={{width:"20%"}}
                  >
                    Xác nhận số giờ OT
                  </TableCell>
                </TableRow>
              </TableHead>

              <TableBody>
                <TableRow className={classes.tableBody}>
                  <TableCell align="center" className="p-0">
                    Đi sớm
                  </TableCell>

                  <TableCell align="center" className="p-0">
                    {formatDate ("HH:mm", checkInOutObject?.checkInTime)}
                  </TableCell>

                  <TableCell align="center" className="p-0">
                    {values?.firstCheckIn && (
                        <>
                          {formatDate ("HH:mm", values?.firstCheckIn)}
                        </>
                    )}

                    {!values?.firstCheckIn && (
                        <span className="w-100 flex flex-center">
                                            Chưa chấm công
                                        </span>
                    )}
                  </TableCell>

                  <TableCell align="center" className="p-0">
                    {`${formatNumber (values?.earlyArrivalMinutes)} phút`}
                  </TableCell>

                  <TableCell align="center" className="p-0">
                    {`${formatNumber (values?.earlyArrivalHours)} giờ`}
                  </TableCell>

                  <TableCell align="center" className="p-0">
                    <GlobitsVNDCurrencyInput
                        // label={"Xác nhận số làm thêm trước ca"}
                        name='confirmedOTHoursBeforeShift'
                        disabled={!isAdmin && !isManager}
                        readOnly={readOnly || !(formatNumber (values?.earlyArrivalHours) >= 0.5)}
                    />
                  </TableCell>
                </TableRow>


                <TableRow className={classes.tableBody}>
                  <TableCell align="center" className="p-0">
                    Về muộn
                  </TableCell>

                  <TableCell align="center" className="p-0">
                    {formatDate ("HH:mm", checkInOutObject?.checkOutTime)}
                  </TableCell>

                  <TableCell align="center" className="p-0">
                    {
                        values?.lastCheckout && (
                            <>
                              {formatDate ("HH:mm", values?.lastCheckout)}
                            </>
                        )
                    }

                    {!values?.lastCheckout && (
                        <span className="w-100 flex flex-center">
                                            Chưa chấm công
                                        </span>
                    )}
                  </TableCell>

                  <TableCell align="center" className="p-0">
                    {`${formatNumber (values?.lateExitMinutes)} phút`}
                  </TableCell>

                  <TableCell align="center" className="p-0">
                    {`${formatNumber (values?.lateExitHours)} giờ`}
                  </TableCell>

                  <TableCell align="center" className="p-0">
                    <GlobitsVNDCurrencyInput
                        // label={"Xác nhận số làm thêm trước ca"}
                        name='confirmedOTHoursAfterShift'
                        disabled={!isAdmin && !isManager}
                        readOnly={readOnly || !(formatNumber (values?.lateExitHours) >= 0.5)}
                    />
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </TableContainer>
        </Grid>
      </Grid>

  );
}

export default memo (observer (ConfirmOTTableSection));