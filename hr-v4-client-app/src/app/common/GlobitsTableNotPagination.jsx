/* eslint-disable no-useless-computed-key */
import React from "react";
import MaterialTable from "material-table";
import { makeStyles } from "@material-ui/core";
import { useTranslation } from "react-i18next";

const useStyles = makeStyles((theme) => ({
  globitsTableWraper: {
    maxHeight: "700px",
    overflowY: "auto",
    borderColor: "none !important",
    ["@media (max-width:420px)"]: {
      maxHeight: "400px",
      overflowY: "auto",
    },
  },
}));

export default function GlobitsTable(props) {
  const classes = useStyles();
  const { t } = useTranslation();
  const { data, columns, selection, handleSelectList, maxWidth, title } = props;

  return (
    <>
      {/* {title ? <h5>Danh sách các dự án hiện tại</h5> : ""} */}
      <div className={classes.globitsTableWraper}>
        <MaterialTable
          data={data}
          columns={columns}
          parentChildData={(row, rows) => {
            if (row.parentId) {
              var list = rows.find((a) => a.id === row.parentId);
              return list;
            }
          }}
          options={{
            selection: selection ? true : false,
            actionsColumnIndex: -1,
            paging: false,
            search: false,
            toolbar: false,
            headerStyle: {
              // display: "none",
              maxHeight: "700px",
              backgroundColor: "#01c0c8",
              color: "#fff",
              paddingLeft: !selection ? "5px" : "unset",
              paddingRight: !selection ? "5px" : "unset",
              position: "sticky",
              maxWidth: maxWidth ? maxWidth : "auto",
              textAlign: "center", 
            },
            rowStyle: (rowData, index) => ({
              backgroundColor: index % 2 === 1 ? "#f8f9fa" : "#ffffff",
            }),
          }}
          onSelectionChange={(rows) => {
            handleSelectList(rows);
          }}
          localization={{
            body: {
              emptyDataSourceMessage: `${t("general.emptyDataMessageTable")}`,
            },
          }}
        />
      </div>
    </>
  );
}
