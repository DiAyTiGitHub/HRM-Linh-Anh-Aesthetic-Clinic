import React, { memo, useEffect } from "react";
import { observer } from "mobx-react";
import GlobitsPagination from "app/common/GlobitsPagination";
import MaterialTable from "material-table";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import { Radio } from "@material-ui/core";
import Config from "app/common/GlobitsConfigConst";
import { useFormikContext } from "formik";

function SelectOrganizationList(props) {
  const { handleClose } = props;
  const { organizationStore } = useStore();
  const { t } = useTranslation();

  const {
    listOrganizations,
    totalPages,
    totalElements,
    pageSize,
    page,
    handleChangePage,
    setRowsPerPage,
    updatePageData
  } = organizationStore;

  const { setFieldValue, values } = useFormikContext();

  // console.log("current values: ", values);

  function handleSelectItem(_, department) {
    if (values?.parent?.id === department?.id) {
      setFieldValue("parent", null);
    } else {
      setFieldValue("parent", department);
      handleClose();
    }
  };


  const columns = [
    {
      title: t("general.popup.select"),
      render: (rowData) => (
        <Radio
          id={`radio${rowData?.id}`}
          name="radSelected"
          disabled={values?.id == rowData?.id}
          value={rowData?.id}
          checked={values?.parent?.id === rowData?.id}
          onClick={(event) => handleSelectItem(event, rowData)}
        />
      ),
    },
    {
      title: t("department.code"),
      field: "code",
      ...Config.tableCellConfig,
    },
    { title: t("department.name"), field: "name", ...Config.tableCellConfig },
    {
      title: t("department.description"),
      field: "description",
      ...Config.tableCellConfig,
    },
  ];

  useEffect(function () {
    const initializeSearchObject = {
      pageSize: 10,
      pageIndex: 1
    };

    updatePageData(initializeSearchObject);
  }, []);

  return (
    <div className="w-100 pt-8">
      <MaterialTable
        data={listOrganizations}
        columns={columns}
        parentChildData={(row, rows) => {
          var list = rows.find((a) => a?.id === row?.parentId);
          return list;
        }}
        options={{
          selection: false,
          actionsColumnIndex: -1,
          paging: false,
          search: false,
          toolbar: false,
          maxBodyHeight: "300px",
          headerStyle: {
            backgroundColor: "#2f4f4f",
            color: "#fff",
          },
          rowStyle: (rowData, index) => ({
            backgroundColor: index % 2 === 1 ? "rgb(237, 245, 251)" : "#FFF",
          }),
        }}
        onSelectionChange={(rows) => {
          this.data = rows;
        }}
      />
      <GlobitsPagination
        totalPages={totalPages}
        handleChangePage={handleChangePage}
        setRowsPerPage={setRowsPerPage}
        pageSize={pageSize}
        pageSizeOption={[10, 25, 50]}
        totalElements={totalElements}
        page={page}
      />
    </div>
  );
}

export default memo(observer(SelectOrganizationList));
