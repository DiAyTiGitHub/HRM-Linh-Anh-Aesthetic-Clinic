import React, { memo } from "react";
import {
  makeStyles,
  TableCell,
  Table,
  TableRow,
  TableBody,
  TableHead,
} from "@material-ui/core";
import { useFormikContext } from "formik";
import { useTranslation } from "react-i18next";
import { formatDate } from "app/LocalFunction";

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

function StaffSalaryHistory() {

  const { t } = useTranslation();
  const classes = useStyles();
  const { values } = useFormikContext();
  const list = values?.salaryHistory;

  return (
    <div className={classes.groupContainer}>
      <div className={classes.tableContainer}>
        {values?.salaryHistory?.length > 0 ? (
          <Table style={{ tableLayout: "auto" }}>
            <TableHead>
              <TableRow>
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
                  <TableCell colSpan={1}>
                    {item?.positionName ? item?.positionName : null}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.staffTypeCode ? item?.staffTypeCode : null}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.coefficientOverLevel ? item?.coefficientOverLevel : ""}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.percentage ? item?.percentage : null}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.decisionCode ? item?.decisionCode : ""}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {formatDate("DD/MM/YYYY", item?.decisionDate)}
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
  );
};

export default memo(StaffSalaryHistory)