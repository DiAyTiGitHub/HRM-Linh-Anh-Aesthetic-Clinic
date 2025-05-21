import {ButtonGroup, Grid} from "@material-ui/core";
import React from "react";
import { observer } from "mobx-react";
import "react-toastify/dist/ReactToastify.css";
import {
  Button,
  IconButton,
  Icon,
  Table,
  makeStyles,
  TableCell,
  TableRow,
  TableBody,
  TableHead,
} from "@material-ui/core";
import { useFormikContext, FieldArray } from "formik";
import { useTranslation } from "react-i18next";
import AddIcon from "@material-ui/icons/Add";
import moment from "moment";
import { useState } from "react";
import StaffSalaryHistoryPopupAdd from "./Popup/StaffSalaryHistoryPopupAdd";

const useStyles = makeStyles(() => ({
  root: {
    background: "#E4f5fc",
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
      width: "calc(100vw / 7)",
    },
  },
}));

export default observer(function StaffSalaryHistory() {

  const { t } = useTranslation();
  const classes = useStyles();
  const [item, setItem] = useState(null);
  const [openPopup, setOpenPopup] = useState(false);
  const [editable, setEditable] = useState(true);
  const [editableIndex, setEditableIndex] = useState(null);
  const { values, setFieldValue } = useFormikContext();
  const list = values?.salaryHistory;

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

    setFieldValue("salaryHistory", newList);
    setOpenPopup(false);
    setEditable(true);
  };

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <FieldArray
          name="salaryHistory"
          render={(arrayHelpers) => (
            <div className={classes.groupContainer}>
              <ButtonGroup
                  color="container"
                  aria-label="outlined primary button group"
              >
                <Button
                    startIcon={<AddIcon/>}
                    type="button"
                    onClick={() => {
                      setOpenPopup(true);
                      setItem(null);
                    }}
                >
                  {t("general.button.add")}
                </Button>
              </ButtonGroup>
              <div className={classes.tableContainer}>
                {list?.length > 0 ? (
                  <Table style={{ tableLayout: "auto" }}>
                    <TableHead>
                      <TableRow>
                        <TableCell style={{ minWidth: "70px" }}>
                          {t("general.action")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.positionName")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.staffTypeCode")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.coefficientOverLevel")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.percentage")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.decisionCode")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.decisionDate")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.salaryIncrementType")}
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
                            {item?.positionName
                              ? item?.positionName
                              : null}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {item?.staffTypeCode
                              ? item?.staffTypeCode
                              : null}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {item?.coefficientOverLevel ? item?.coefficientOverLevel : ""}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {item?.percentage
                              ? item?.percentage
                              : null}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {item?.decisionCode ? item?.decisionCode : ""}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {item?.decisionDate
                              ? moment(item?.decisionDate).format("DD/MM/YYYY")
                              : null}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {item?.salaryIncrementType ? item?.salaryIncrementType : ""}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : (
                  <h5 className="text-primary n-w">Không có quá trình lương nào</h5>
                )}
              </div>
            </div>
          )}
        />
      </Grid>

      {openPopup && (
        <StaffSalaryHistoryPopupAdd
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
  );
});
