import React, { memo } from "react";
import { useTranslation } from "react-i18next";
import { useFormikContext } from "formik";
import {
  makeStyles,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
} from "@material-ui/core";
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

function StaffRewardHistory() {

  const { t } = useTranslation();
  const classes = useStyles();
  const { values } = useFormikContext();
  const list = values?.rewardHistory;

  return (
    <div className={classes.groupContainer}>
      <div className={classes.tableContainer}>
        {list?.length > 0 ? (
          <Table style={{ tableLayout: "auto" }}>
            <TableHead>
              <TableRow>
                <TableCell colSpan={1}>
                  {t("rewardHistory.organizationName")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("rewardHistory.rewardDate")}
                </TableCell>
                <TableCell colSpan={1}>
                  {t("rewardHistory.rewardType")}
                </TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {list?.map((item, index) => (
                <TableRow key={index}>
                  <TableCell colSpan={1}>
                    {item?.organizationName ? item?.organizationName : ""}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {formatDate("DD/MM/YYYY", item?.rewardDate)}
                  </TableCell>
                  <TableCell colSpan={1}>
                    {item?.rewardType?.name
                      ? item?.rewardType?.name
                      : null}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        ) : (
          <h5 className="text-primary n-w">
            Không có quá trình khen thưởng nào
          </h5>
        )}
      </div>
    </div>
  );
};

export default memo(StaffRewardHistory)