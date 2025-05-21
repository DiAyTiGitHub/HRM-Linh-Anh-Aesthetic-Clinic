import {
  Button,
  ButtonGroup,
  Grid,
  Icon,
  IconButton,
  makeStyles,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow
} from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import { formatDate, formatMoney, formatVNDMoney } from "app/LocalFunction";
import { FieldArray, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import "react-toastify/dist/ReactToastify.css";
import StaffInsuranceHistoryPopupAdd from "./Popup/StaffSocialInsuranceV2CUForm";

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
    overflowY: "hidden",
    "& .MuiTableCell-body": {
      border: "1px solid #e9ecef",
      padding: 0,
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

export default observer(function StaffInsuranceHistory() {
  const { t } = useTranslation();
  const classes = useStyles();
  const { values, setFieldValue } = useFormikContext();
  const [editable, setEditable] = useState(true);
  const [openPopup, setOpenPopup] = useState(false);
  const [editableIndex, setEditableIndex] = useState(null);
  const [item, setItem] = useState(null);

  let list = values?.staffSocialInsurance;

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
    setFieldValue("staffSocialInsurance", newList);
    setOpenPopup(false);
    setEditable(true);
  };

  // console.log("values", values);

  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <FieldArray
          name="staffSocialInsurance"
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
                        <TableCell colSpan={1} style={{ height: "36px" }}>
                          {t("general.action")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {/* {t("humanResourcesInformation.salaryPeriod")} */}
                          Ngày bắt đầu
                        </TableCell>
                        <TableCell colSpan={1}>
                          {/* {t("humanResourcesInformation.salaryResult")} */}
                          Ngày kết thúc
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.insuranceSalary")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.staffPercentage")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.orgPercentage")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.staffInsuranceAmount")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.orgInsuranceAmount")}
                        </TableCell>
                        <TableCell colSpan={1}>
                          {t("humanResourcesInformation.totalInsuranceAmount")}
                        </TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {list?.map((item, index) => (
                        <TableRow key={index} className={classes.tableRow_Body}>
                          <TableCell size="small" style={{ height: "36px" }}>
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
                            {formatDate("DD/MM/YYYY", item?.startDate)}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {formatDate("DD/MM/YYYY", item?.endDate)}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {formatMoney(item?.insuranceSalary)}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {`${formatVNDMoney(item?.staffPercentage)}%`}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {`${formatVNDMoney(item?.orgPercentage)}%`}
                          </TableCell>

                          <TableCell colSpan={1}>
                            {formatMoney(item?.staffInsuranceAmount)}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {formatMoney(item?.orgInsuranceAmount)}
                          </TableCell>
                          <TableCell colSpan={1}>
                            {formatMoney(item?.totalInsuranceAmount)}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                ) : (
                  <h5 className="text-primary n-w">
                    Không có quá trình đóng BHXH nào
                  </h5>
                )}
              </div>
            </div>

          )}
        />
      </Grid>

      {openPopup && (
        <StaffInsuranceHistoryPopupAdd
          item={item}
          handleSubmit={handleSubmitItem}
          handleClose={() => {
            setOpenPopup(false);
            setEditable(true);
          }}
          open={openPopup}
          editable={editable}
        />
      )}
    </Grid>
  );
});
