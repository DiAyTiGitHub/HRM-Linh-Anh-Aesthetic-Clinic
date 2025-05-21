import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { observer } from "mobx-react";
import MaterialTable from "material-table";
import { useStore } from "../../../stores";
import SearchIcon from "@material-ui/icons/Search";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import { ArrowUpward, ChevronRight } from "@material-ui/icons";
import { Radio } from "@material-ui/core";
export default observer(function ProjectActivityList(props) {
  const { selectedItem, handleSelectItem } = props;

  const { t } = useTranslation();

  const { timeSheetStore } = useStore();

  const { selectedActivityList } = timeSheetStore;

  const [selectedRow, setSelectedRow] = useState(null);

  const getMembers = (members) => {
    let children = [];

    return members.map((m) => {
      if (m.children && m.children.length) {
        children = [...children, ...m.children];
      }
      return m;
    }).concat(children.length ? getMembers(children) : children);
  };

  var activityList = getMembers(selectedActivityList);

  let columns = [
    {
      title: t("general.popup.select"),
      align: "center",
      cellStyle: {
        textAlign: "center",
      },
      render: (rowData) => (
        <Radio
          id={`radio${rowData?.id}`}
          name="radSelected"
          value={rowData?.id}
          checked={selectedItem?.id === rowData?.id}
          onClick={(event) => handleSelectItem(event, rowData)}
        />
      ),
    },
    {
      title: t("activity.code"),
      field: "code",
      cellStyle: {
        textAlign: "center",
      },
    },
    { title: t("activity.name"), field: "name" },
    {
      title: t("activity.description"),
      field: "description",
    },
  ];

  return (
    <MaterialTable
      icons={{
        Filter: React.forwardRef((props, ref) => <SearchIcon ref={ref} />),
        Search: React.forwardRef((props, ref) => <SearchIcon ref={ref} />),
        ResetSearch: React.forwardRef((props, ref) => (
          <RotateLeftIcon ref={ref} />
        )),
        SortArrow: ArrowUpward,
        DetailPanel: ChevronRight,
      }}
      columns={columns}
      data={activityList}
      parentChildData={(row, rows) => rows.find((a) => a?.id === row?.parentId)}
      onRowClick={(evt, selectedRow) => {
        setSelectedRow(selectedRow.tableData.id);
      }}
      options={{
        paging: false,
        search: false,
        toolbar: false,
        showTitle: false,
        headerStyle: {
          backgroundColor: "#e0e0e0",
          color: "#FFF",
          fontSize: "17px",
          textAlign: "center",
          fontWeight: "bold",
        },
        actionsColumnIndex: -1,
        rowStyle: (rowData, index) => ({
          backgroundColor:
            selectedRow === rowData?.tableData?.id ? "#EEE" : "#FFF",
          fontWeight: rowData?.parent ? "normal" : "bold",
        }),
      }}
      localization={{
        body: {
          emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
        },

        toolbar: {
          searchPlaceholder: "Tìm kiếm",
        },
      }}
    />
  );
});
