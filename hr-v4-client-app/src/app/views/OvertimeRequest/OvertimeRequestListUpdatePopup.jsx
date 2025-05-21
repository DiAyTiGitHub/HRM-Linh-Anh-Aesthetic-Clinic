import { observer } from 'mobx-react';
import React, { memo } from "react";
import {
  Button,
  DialogActions,
  Grid,
  Icon,
  IconButton,
  makeStyles,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Tooltip
} from "@material-ui/core";
import GlobitsColorfulThemePopup from 'app/common/GlobitsColorfulThemePopup';
import { useStore } from 'app/stores';
import { useTranslation } from "react-i18next";
import LocalConstants from 'app/LocalConstants';
import { formatDate, formatNumber, getCheckInAndCheckOutTimeOfShiftWork } from 'app/LocalFunction';
import { Form, Formik } from "formik";
import GlobitsNumberInput from "../../common/form/GlobitsNumberInput";
import BlockIcon from "@material-ui/icons/Block";
import DoneIcon from "@material-ui/icons/Done";
import * as Yup from "yup";
import GlobitsVNDCurrencyInput from "../../common/form/GlobitsVNDCurrencyInput";

const useStyles = makeStyles ({
  root:{
    "& .MuiDialogContent-root":{
      overflow:"auto !important",
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

function OvertimeRequestListUpdatePopup ({readOnly}) {
  const {overtimeRequestStore} = useStore ();
  const {t} = useTranslation ();
  const classes = useStyles ();
  const {
    openConfirmUpdateStatusPopup,
    handleClose,
    handleRemoveActionItem,
    listChosen,
    onUpdateStatus,
    pagingOvertimeRequest,
    handleConfirmUpdateStatus,
  } = overtimeRequestStore;

  function handleCloseConfirmPopup () {
    handleClose ();
    pagingOvertimeRequest ();
  }

  const handleSubmit = async (values) => {
    let list = [];
    values?.items?.map ((item, index) => {
      list.push ({
        id:item?.id,
        requestOTHoursBeforeShift:item?.requestOTHoursBeforeShift,
        requestOTHoursAfterShift:item?.requestOTHoursAfterShift,
        staffWorkSchedule:{
          confirmedOTHoursBeforeShift:item?.confirmedOTHoursBeforeShift,
          confirmedOTHoursAfterShift:item?.confirmedOTHoursAfterShift,
        },
        approvalStatus:onUpdateStatus
      });
    });
    await handleConfirmUpdateStatus (list);
  };
  const validationSchema = Yup.object ({
    items:Yup.array ().of (
        Yup.object ({
          confirmedOTHoursBeforeShift:Yup.number ()
              .nullable ()
              .notRequired ()
              .test (
                  'max-earlyArrival',
                  'Số giờ yêu cầu trước ca phải nhỏ hơn hoặc bằng số giờ đến sớm',
                  function (value) {
                    const {staffWorkSchedule} = this.parent;
                    const maxValue = staffWorkSchedule?.earlyArrivalMinutes? formatNumber (staffWorkSchedule.earlyArrivalMinutes / 60) : 0;
                    return value == null || value <= maxValue;
                  }
              ),
          confirmedOTHoursAfterShift:Yup.number ()
              .nullable ()
              .notRequired ()
              .test (
                  'max-lateExit',
                  'Số giờ yêu cầu sau ca phải nhỏ hơn hoặc bằng số giờ về muộn',
                  function (value) {
                    const {staffWorkSchedule} = this.parent;
                    const maxValue = staffWorkSchedule?.lateExitMinutes? formatNumber (staffWorkSchedule.lateExitMinutes / 60) : 0;
                    return value == null || value <= maxValue;
                  }
              )
        })
    )
  });

  return (
      <GlobitsColorfulThemePopup
          open={openConfirmUpdateStatusPopup}
          handleClose={handleCloseConfirmPopup}
          size="lg"
          hideFooter={true}
      >
        <div className="dialog-body">
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <div className="dialogScrollContent">
                <h6 className="text-red">
                  <strong>
                    {listChosen?.length <= 1? "Thông tin " : "Danh sách "}
                    yêu cầu nghỉ được cập nhật
                    thành {LocalConstants.OvertimeRequestApprovalStatus.getListData ().find (i => i.value == onUpdateStatus)?.name?.toUpperCase ()}
                  </strong>
                </h6>
                <TableContainer component={Paper}>
                  <Formik
                      validationSchema={validationSchema}
                      enableReinitialize
                      initialValues={{
                        items:listChosen?.map (item => ({
                          ... item,
                          // requestOTHoursBeforeShift:item.requestOTHoursBeforeShift ?? formatNumber (item.staffWorkSchedule?.earlyArrivalMinutes / 60),
                          // requestOTHoursAfterShift:item.requestOTHoursAfterShift ?? formatNumber (item.staffWorkSchedule?.lateExitMinutes / 60),
                        }))
                      }}
                      onSubmit={handleSubmit}
                  >
                    {({values, setFieldValue}) => (
                        <Form autoComplete="off">
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
                                  Thao tác
                                </TableCell>
                                <TableCell
                                    align="center"
                                    className="py-4"
                                    style={{width:"10%"}}
                                >
                                </TableCell>
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
                                  Giờ vào quy định
                                </TableCell>

                                <TableCell
                                    align="center"
                                    className="py-4"
                                    style={{width:"15%"}}
                                >
                                  Thời gian chấm công
                                </TableCell>

                                <TableCell
                                    align="center"
                                    className="py-4"
                                    style={{width:"15%"}}
                                >
                                  Số phút sớm/muộn
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
                                    style={{width:"18%"}}
                                >
                                  Số giờ yêu cầu tính làm thêm
                                </TableCell>
                                <TableCell
                                    align="center"
                                    className="py-4"
                                    style={{width:"18%"}}
                                >
                                  Trạng thái
                                </TableCell>
                              </TableRow>
                            </TableHead>

                            <TableBody>
                              {values?.items?.map ((item, index) => {
                                const checkInOut = getCheckInAndCheckOutTimeOfShiftWork (item.staffWorkSchedule?.shiftWork);

                                return (
                                    <React.Fragment key={index}>
                                      <TableRow className={classes.tableBody}>
                                        <TableCell align="center" rowSpan={2}>
                                          <Tooltip title="Xóa">
                                            <IconButton size="small"
                                                        onClick={() => {
                                                          const newValue = values?.items.filter (data => data?.id !== item?.id);
                                                          setFieldValue ("items", newValue);
                                                          handleRemoveActionItem (item?.id);
                                                        }}
                                            >
                                              <Icon fontSize="small" color="secondary">delete</Icon>
                                            </IconButton>
                                          </Tooltip>
                                        </TableCell>
                                        <TableCell align="center" rowSpan={2}>
                                          {item?.staffWorkSchedule?.staff?.displayName}
                                        </TableCell>
                                        <TableCell align="center">Đi sớm</TableCell>
                                        <TableCell
                                            align="center">{formatDate ("HH:mm", checkInOut?.checkInTime)}</TableCell>
                                        <TableCell
                                            align="center">{formatDate ("HH:mm", item.staffWorkSchedule?.firstCheckIn)}</TableCell>
                                        <TableCell
                                            align="center">{formatNumber (item.staffWorkSchedule?.earlyArrivalMinutes)} phút</TableCell>
                                        <TableCell
                                            align="center">{formatNumber (item.staffWorkSchedule?.earlyArrivalMinutes / 60)} giờ</TableCell>
                                        <TableCell align="center">
                                          <GlobitsVNDCurrencyInput
                                              // label={"Xác nhận số làm thêm trước ca"}
                                              name={`items[${index}].confirmedOTHoursAfterShift`}
                                              disabled={item?.approvalStatus === LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value}
                                              readOnly={readOnly || !(formatNumber (item.staffWorkSchedule?.earlyArrivalMinutes / 60) >= 0.5)}
                                          />
                                        </TableCell>
                                        <TableCell
                                            align="center"
                                            rowSpan={2}
                                        >
                                          {LocalConstants.OvertimeRequestApprovalStatus.getListData ().find (i => i.value === item?.approvalStatus)?.name}
                                        </TableCell>
                                      </TableRow>
                                      <TableRow className={classes.tableBody}>
                                        <TableCell align="center">Về muộn</TableCell>
                                        <TableCell
                                            align="center">{formatDate ("HH:mm", checkInOut?.checkOutTime)}</TableCell>
                                        <TableCell
                                            align="center">{formatDate ("HH:mm", item.staffWorkSchedule?.lastCheckout)}</TableCell>
                                        <TableCell
                                            align="center">{formatNumber (item.staffWorkSchedule?.lateExitMinutes)} phút</TableCell>
                                        <TableCell
                                            align="center">{formatNumber (item.staffWorkSchedule?.lateExitMinutes / 60)} giờ</TableCell>
                                        <TableCell align="center">
                                          <GlobitsVNDCurrencyInput
                                              name={`items[${index}].confirmedOTHoursBeforeShift`}
                                              disabled={item?.approvalStatus === LocalConstants.OvertimeRequestApprovalStatus.APPROVED.value}
                                              readOnly={readOnly || !(formatNumber (item.staffWorkSchedule?.lateExitMinutes / 60) >= 0.5)}
                                          />
                                        </TableCell>
                                      </TableRow>
                                    </React.Fragment>
                                );
                              })}
                            </TableBody>
                          </Table>
                          <DialogActions className="confirmDeletePopupFooter">
                            <div className="flex flex-space-between flex-middle">
                              <Button
                                  startIcon={<BlockIcon className="mr-4"/>}
                                  variant="contained"
                                  className="btn  bg-light-gray d-inline-flex mr-12"
                                  onClick={handleClose}
                              >
                                Hủy bỏ
                              </Button>
                              <Button
                                  className="btn btn-success d-inline-flex"
                                  variant="contained"
                                  startIcon={<DoneIcon className="mr-4"/>}
                                  type={"submit"}
                              >
                                Xác nhận
                              </Button>
                            </div>
                          </DialogActions>
                        </Form>
                    )}
                  </Formik>
                </TableContainer>
              </div>
            </Grid>
          </Grid>
        </div>
      </GlobitsColorfulThemePopup>
  );
}

export default memo (observer (OvertimeRequestListUpdatePopup));