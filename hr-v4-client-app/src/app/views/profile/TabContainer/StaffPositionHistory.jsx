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

function StaffPositionHistory() {

  const { t } = useTranslation();
  const classes = useStyles();
  const { values } = useFormikContext();
  const list = values?.positions;

  return (
    <div className={classes.groupContainer}>
      <div className={classes.tableContainer}>
        {list?.length > 0 ? (
          <Table style={{ tableLayout: "auto" }}>
            <TableHead>
              <TableRow>
                <TableCell colSpan={1}>
                  {t("positionHistory.decisionCode")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("positionHistory.fromDate")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("positionHistory.toDate")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("positionHistory.position")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("positionHistory.department")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("positionHistory.allowanceCoefficient")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("positionHistory.note")}
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {list?.map((item, index) => (
                <TableRow key={index}>
                  <TableCell colSpan={1}>
                    {item?.decisionCode ? item?.decisionCode : null}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {formatDate("DD/MM/YYYY", item?.fromDate)}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {formatDate("DD/MM/YYYY", item?.toDate)}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.position?.name ? item?.position?.name : null}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.department?.name
                      ? item?.department?.name
                      : null}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.allowanceCoefficient
                      ? item?.allowanceCoefficient
                      : null}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.note ? item?.note : ""}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : (
          <h5 className="text-primary n-w">
            Không có quá trình chức vụ nào
          </h5>
        )}
      </div>
    </div>
  );
};

export default memo(StaffPositionHistory)