import React, { memo } from "react";
import { observer } from "mobx-react";
import "react-toastify/dist/ReactToastify.css";
import { Grid, Button, IconButton, Icon, makeStyles, Table, TableBody, TableCell, TableHead, TableRow, Tooltip } from "@material-ui/core";
import { useFormikContext, FieldArray } from "formik";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import { formatDate } from "app/LocalFunction";
import { useState } from "react";
import StaffAgreementPopupAdd from "./Popup/StaffAgreementPopupAdd";
import { useStore } from "app/stores";
import { LocalAtm } from "@material-ui/icons";
import StaffSalaryItemValuePopup from "./Popup/StaffSalaryItemValuePopup";
import { toast } from "react-toastify";

const useStyles = makeStyles(() => ({
  root: {
    padding: "10px 15px",
    borderRadius: "5px",
  },
  groupContainer: {
    width: "100%",
    "& .MuiOutlinedInput-root": {
      borderRadius: "0!important",
    },
  },
  tableContainer: {
    marginTop: "16px",
    overflowX: "auto",
    overflowY: "auto",
    maxHeight: "60vh",
    "& .MuiTableCell-body": {
      border: "1px solid #e9ecef",
      textAlign: "center",
    },
    "& .MuiTableCell-head": {
      padding: "10px",
      minWidth: "150px",
      border: "1px solid #e9ecef",
      textAlign: "center",
    },
  },
  tableHeader: {
    width: "100%",
    borderBottom: "1px solid #E3F2FD",
    marginBottom: "8px",
    "& th": {
      width: "calc(100vw / 4)",
    },
  },
}));

function StaffAgreements() {
  const { t } = useTranslation();
  const classes = useStyles();
  const [openPopup, setOpenPopup] = useState(false);
  const [openStaffSalaryItemValuePopup, setOpenStaffSalaryItemValuePopup] = useState(false);
  const [editable, setEditable] = useState(true);
  const [editableIndex, setEditableIndex] = useState(null);
  const [item, setItem] = useState(null);

  const { values, setFieldValue } = useFormikContext();
  const list = values?.agreements;

  const handleSubmitItem = (values) => {
    let newList = list ? list : [];
    if (!item) {
      newList.push(values);
    } else {
      newList = newList?.map((el, index) => {
        if (values?.index === index) {
          return values;
        }
        return el;
      });
    }

    setFieldValue("agreements", newList);
    setOpenPopup(false);
    setEditable(true);
  };

  const { staffLabourAgreementStore } = useStore();

  const { handleExportHDLD } = staffLabourAgreementStore;

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <FieldArray
          name='agreements'
          render={(arrayHelpers) => (
            <div className={classes.groupContainer}>
              <Button
                className='btn btn-primary d-inline-flex'
                startIcon={<AddIcon />}
                variant='contained'
                onClick={() => {
                  setOpenPopup(true);
                  setItem(null);
                }}>
                {t("general.button.add")}
              </Button>
              <div className={classes.tableContainer}>
                {list?.length > 0 ? (
                  <Table style={{ tableLayout: "auto" }}>
                    <TableHead>
                      <TableRow>
                        <TableCell style={{ minWidth: "90px" }}>{t("general.action")}</TableCell>
                        <TableCell colSpan={1}>{t("agreements.labourAgreementNumber")}</TableCell>
                        <TableCell colSpan={1}>{t("agreements.signedDate")}</TableCell>
                        <TableCell colSpan={1}>{t("agreements.startDate")}</TableCell>
                        <TableCell colSpan={1}>{t("agreements.endDate")}</TableCell>
                        {/* <TableCell colSpan={1} style={{ minWidth: "80px" }}>
                          {t("agreements.agreementStatus")}
                        </TableCell> */}
                        <TableCell colSpan={1}>{t("agreements.contractType")}</TableCell>
                        <TableCell colSpan={1}>Mẫu bảng lương</TableCell>
                      </TableRow>
                    </TableHead>

                    <TableBody>
                      {list?.map((item, index) => (
                        <TableRow key={index}>
                          <TableCell>
                            {editable === false && editableIndex === index ? (
                              <IconButton size='small'>
                                <Icon fontSize='small' color='primary'>
                                  visibility
                                </Icon>
                              </IconButton>
                            ) : (
                              <>
                                {/* <IconButton
                                  size='small'
                                  onClick={() => {
                                    setItem({ ...item, index });
                                    setOpenPopup(true);
                                    setEditable(false);
                                    setEditableIndex(index);
                                  }}>
                                  <Icon fontSize='small' color='primary'>
                                    edit
                                  </Icon>
                                </IconButton>
                                <IconButton size='small' onClick={() => arrayHelpers.remove(index)}>
                                  <Icon fontSize='small' color='secondary'>
                                    delete
                                  </Icon>
                                </IconButton> */}

                                <Tooltip title='Cập nhật thông tin' placement='top'>
                                  <IconButton
                                    size='small'
                                    onClick={function () {
                                      setItem({ ...item, index });
                                      setOpenPopup(true);
                                      setEditable(false);
                                      setEditableIndex(index);
                                    }}>
                                    <Icon fontSize='small' color='primary'>
                                      edit
                                    </Icon>
                                  </IconButton>
                                </Tooltip>

                                <Tooltip title='Xóa' placement='top'>
                                  <IconButton size='small' className='ml-4' onClick={() => arrayHelpers.remove(index)}>
                                    <Icon fontSize='small' color='secondary'>
                                      delete
                                    </Icon>
                                  </IconButton>
                                </Tooltip>
                                <Tooltip title='Tải xuống hợp đồng lao động' arrow>
                                  <IconButton size='small' className='ml-4' onClick={() => handleExportHDLD(item?.id)}>
                                    <Icon fontSize='small' color='blue'>
                                      description
                                    </Icon>
                                  </IconButton>
                                </Tooltip>
                                {values?.id && item?.salaryTemplate &&
                                  <Tooltip title='Cấu hình lương cố định' arrow>
                                    <IconButton size='small' onClick={() => {
                                      setItem({ ...item, index });
                                      setOpenStaffSalaryItemValuePopup(true)
                                    }}>
                                      <LocalAtm fontSize="small" />
                                    </IconButton>
                                  </Tooltip>
                                }
                              </>
                            )}
                          </TableCell>

                          <TableCell colSpan={1}>{item?.labourAgreementNumber}</TableCell>
                          <TableCell colSpan={1}>{formatDate("DD/MM/YYYY", item?.signedDate)}</TableCell>
                          <TableCell colSpan={1}>{formatDate("DD/MM/YYYY", item?.startDate)}</TableCell>
                          <TableCell colSpan={1}>{formatDate("DD/MM/YYYY", item?.endDate)}</TableCell>
                          {/* <TableCell colSpan={1}>
                            {item?.agreementStatus ? item?.agreementStatus : null}
                          </TableCell> */}
                          <TableCell colSpan={1}>{item?.contractType?.name ? item?.contractType?.name : ""}</TableCell>
                          <TableCell colSpan={1}>{item?.salaryTemplate?.name ? item?.salaryTemplate?.name : ""}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : (
                  <h5 className='text-primary n-w'>Không có hợp đồng nào</h5>
                )}
              </div>
            </div>
          )}
        />

        {openPopup && (
          <StaffAgreementPopupAdd
            item={item}
            handleSubmit={handleSubmitItem}
            open={openPopup}
            handleClose={() => {
              setOpenPopup(false);
              setEditable(true);
            }}
            editable={editable}
          />
        )}
        {openStaffSalaryItemValuePopup && (
          <StaffSalaryItemValuePopup
            item={{
              staff: {
                id: values?.id,
                displayName: values?.displayName
              },
              salaryTemplate: item?.salaryTemplate
            }}
            handleSubmit={(values) => {
              data ? toast.success("Thông tin đã được lưu") : toast.warning("Lưu thông tin có lỗi");
              setOpenStaffSalaryItemValuePopup(false);
            }}
            open={openStaffSalaryItemValuePopup}
            handleClose={() => {
              setOpenStaffSalaryItemValuePopup(false);
              setEditable(true);
            }}
            editable={editable}
          />
        )}
      </Grid>
    </Grid>
  );
}


export default memo(observer(StaffAgreements));
