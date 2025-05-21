import React, { memo } from "react";
import {
  makeStyles,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
} from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useFormikContext } from "formik";
import { formatDate } from "app/LocalFunction";

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

function StaffOverseasWorkHistory() {

  const { t } = useTranslation();
  const classes = useStyles();
  const { values } = useFormikContext();
  const list = values?.overseasWorkHistory;

  return (
    <div className={classes.groupContainer}>
      <div className={classes.tableContainer}>
        {list?.length > 0 ? (
          <Table style={{ tableLayout: "auto" }}>
            <TableHead>
              <TableRow>
                <TableCell colSpan={1}>
                  {t("overseasWorkHistory.startDate")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("overseasWorkHistory.endDate")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("overseasWorkHistory.country")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("overseasWorkHistory.companyName")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("overseasWorkHistory.purpose")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("overseasWorkHistory.decisionNumber")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("overseasWorkHistory.decisionDate")}
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {list?.map((item, index) => (
                <TableRow key={index}>
                  <TableCell colSpan={1}>
                    {formatDate("DD/MM/YYYY", item?.startDate)}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {formatDate("DD/MM/YYYY", item?.endDate)}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.country?.name ? item?.country?.name : ""}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.companyName ? item?.companyName : null}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.purpose ? item?.purpose : ""}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.decisionNumber ? item?.decisionNumber : null}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {formatDate("DD/MM/YYYY", item?.decisionDate)}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : (
          <h5 className="text-primary n-w">
            Không có quá trình công tác nước ngoài nào
          </h5>
        )}
      </div>
    </div>
  );
};

export default memo(StaffOverseasWorkHistory)