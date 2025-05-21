import React, { useState } from "react";
import {
  Grid,
  Button,
  IconButton,
  Icon,
  makeStyles,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
} from "@material-ui/core";
import { useFormikContext, FieldArray } from "formik";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import moment from "moment";
import StaffAllowanceHistoryAddPopup from "./Popup/StaffAllowanceHistoryAddPopup";

const useStyles = makeStyles((theme) => ({
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

export default function StaffAllowanceHistory() {
  const { t } = useTranslation()
  const classes = useStyles();
  const [openPopup, setOpenPopup] = useState(false);
  const [editable, setEditable] = useState(true);
  const [editableIndex, setEditableIndex] = useState(null);
  const [item, setItem] = useState(null);

  const { values, setFieldValue } = useFormikContext();
  const list = values?.allowanceHistory;

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

    setFieldValue("allowanceHistory", newList);
    setOpenPopup(false);
    setEditable(true);
  };

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <FieldArray
          name="allowanceHistory"
          render={(arrayHelpers) => (
            <div className={classes.groupContainer}>
              <Button
                className="btn btn-primary d-inline-flex"
                startIcon={<AddIcon />}
                variant="contained"
                onClick={() => {
                  setOpenPopup(true);
                  setItem(null);
                }}
              >
                {t("general.button.add")}
              </Button>
              <div className={classes.tableContainer}>
                {list?.length > 0 ? (
                  <Table style={{ tableLayout: "auto" }}>
                    <TableHead>
                      <TableRow>
                        <TableCell style={{ minWidth: "60px" }}>
                          {t("general.action")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("allowanceHistory.startDate")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("allowanceHistory.endDate")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("allowanceHistory.allowanceType")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("allowanceHistory.coefficient")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("allowanceHistory.note")}
                        </TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {list?.map((item, index) => (
                        <TableRow key={index}>
                          <TableCell>
                            {editable === false && editableIndex === index ? (
                              <IconButton size="small">
                                <Icon fontSize="small" color="primary">
                                  visibility
                                </Icon>
                              </IconButton>
                            ) : (
                              <>
                                <IconButton
                                  size="small"
                                  onClick={() => {
                                    setItem({ ...item, index });
                                    setOpenPopup(true);
                                    setEditable(false);
                                    setEditableIndex(index);
                                  }}
                                >
                                  <Icon fontSize="small" color="primary">
                                    edit
                                  </Icon>
                                </IconButton>
                                <IconButton
                                  size="small"
                                  onClick={() => arrayHelpers.remove(index)}
                                >
                                  <Icon fontSize="small" color="secondary">
                                    delete
                                  </Icon>
                                </IconButton>
                              </>
                            )}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {item?.startDate
                              ? moment(item?.startDate).format("DD/MM/YYYY")
                              : null}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {item?.endDate
                              ? moment(item?.endDate).format("DD/MM/YYYY")
                              : null}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {item?.allowanceType?.name
                              ? item?.allowanceType?.name
                              : ""}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {item?.coefficient ? item?.coefficient : null}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {item?.note ? item?.note : ""}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : (
                  <h5 className="text-primary n-w">Không có phụ cấp nào</h5>
                )}
              </div>
            </div>
          )}
        />
        {openPopup && (
          <StaffAllowanceHistoryAddPopup
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
      </Grid>
    </Grid>
  );
}
