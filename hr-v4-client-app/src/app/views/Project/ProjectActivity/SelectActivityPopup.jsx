import React, { useEffect, useState } from "react";
import { observer } from "mobx-react";
import { useFormikContext } from "formik";
import { Grid, Button, } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "../../../stores";
import { Radio } from "@material-ui/core";
import SearchIcon from "@material-ui/icons/Search";
import MaterialTable from "material-table";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import { ArrowUpward, ChevronRight } from "@material-ui/icons";
import GlobitsPopup from "app/common/GlobitsPopup";

export default observer(function SelectActivityPopup({ isOpenPopup, setIsOpenPopup }) {

  const { projectStore, } = useStore();

  const { listActivity } = projectStore

  const { t } = useTranslation();
  const { setFieldValue, values } = useFormikContext();

  const [selectedParentActivity, setSelectedParentActivity] = useState(values?.parent);
  const [selectedRow, setSelectedRow] = useState(null);


  const handleSelectParentActivity = (_, activity) => {
    if (selectedParentActivity && selectedParentActivity?.id === activity?.id) {
      setSelectedParentActivity(null);
    } else {
      setSelectedParentActivity(activity);
    }
  };

  useEffect(() => {
    setFieldValue("parent", selectedParentActivity);
  }, [setFieldValue, selectedParentActivity]);

  let columns = [
    {
      title: t("general.popup.select"),
      align: "center",
      render: (rowData) => (
        <Radio
          id={`radio${rowData.id}`}
          name="radSelected"
          value={rowData.id}
          checked={selectedParentActivity?.id === rowData.id}
          onClick={(event) => handleSelectParentActivity(event, rowData)}
        />
      ),
    },
    {
      title: t("project.activity.code"),
      field: "code",
    },
    {
      title: t("project.activity.name"),
      field: "name",
    },
    {
      title: t("project.activity.description"),
      field: "description",
    },
  ];

  return (
    <GlobitsPopup
      open={isOpenPopup}
      title={t("project.activity.select")}
      onClosePopup={() => setIsOpenPopup(false)}
      action={<Button
        variant="contained"
        className="btn btn-secondary d-inline-flex"
        onClick={() => setIsOpenPopup(false)}
      >
        {t("general.button.close")}
      </Button>}
    >
      <Grid item xs={12}>
        <MaterialTable
          style={{ width: "100%" }}
          title="Danh sách hoạt động"
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
          data={listActivity}
          onRowClick={(evt, selectedRow) => {
            setSelectedRow(selectedRow.tableData.id);
          }}
          options={{
            toolbar: true,
            paging: true,
            maxBodyHeight: "300px",
            headerStyle: {
              backgroundColor: "#5390BE",
              color: "#FFF",
              fontSize: "17px",
              textAlign: "center",
              fontWeight: "bold",
            },

            rowStyle: (rowData, index) => ({
              marginLeft: rowData.parent != null ? "15px" : "0px",
              backgroundColor:
                selectedRow === rowData?.tableData?.id ? "#EEE" : "#FFF",
            }),
          }}
          parentChildData={(row, rows) => rows.find((a) => a?.id === row?.parentId)}
          onSelectionChange={(rows) => {
            this.data = rows;
          }}
          localization={{
            body: {
              emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
            },
            header: {
              actions: "Lựa chọn",
            },
            pagination: {
              labelDisplayedRows: "{from}-{to} của {count}",
              labelRowsSelect: "hàng mỗi trang",
              labelRowsPerPage: "Số hàng mỗi trang:",
              firstAriaLabel: "Trang đầu",
              firstTooltip: "Trang đầu",
              previousAriaLabel: "Trang trước",
              previousTooltip: "Trang trước",
              nextAriaLabel: "Trang sau",
              nextTooltip: "Trang sau",
              lastAriaLabel: "Trang cuối",
              lastTooltip: "Trang cuối",
            },
            toolbar: {
              searchPlaceholder: "Tìm kiếm",
            },
          }}
        />
      </Grid>
    </GlobitsPopup>
  );
});