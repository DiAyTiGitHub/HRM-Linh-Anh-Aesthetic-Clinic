import React, { useState, useEffect, memo } from "react";
import { Formik, Form, useFormikContext } from "formik";
import { Grid, DialogActions, Button, DialogContent, Radio, Tooltip } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import LocalConstants from "app/LocalConstants";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import { convertToConstantFormat } from "app/common/CommonFunctions";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate } from "app/LocalFunction";

function DuplicateStaffsCandidatesPopup(props) {
  const { t } = useTranslation();

  const {
    values,
    setFieldValue
  } = useFormikContext();

  const { candidateStore } = useStore();
  const {
    handleCloseListDuplicate,
    duplicateResponse,
    openListDuplicate
  } = candidateStore;

  function handleSelectItem(rowData) {
    setFieldValue("staff", rowData);
  }

  function handleSelectItem(_, staff) {
    if (values?.staff && values?.staff?.id === staff?.id) {
      setFieldValue("staff", null);
    } else {
      setFieldValue("staff", staff);
      // handleCloseListDuplicate();
    }
  };


  let columns = [
    {
      title: "Chọn",
      align: "center",
      field: "action",
      minWidth: "100px",
      render: (rowData) => {

        return (
          <Tooltip placement="top" title="Xác nhận chính là ứng viên">
            <Radio
              id={`radio${rowData?.id}`}
              name="radSelected"
              value={rowData?.id}
              checked={values?.staff?.id === rowData?.id}
              onClick={(event) => handleSelectItem(event, rowData)}
            />
          </Tooltip>
        );
      },
    },
    {
      title: "Mã nhân viên",
      field: "staffCode",
      align: "center",
      render: (rowData) => (
        <span className="px-6">
          {rowData?.staffCode}
        </span>
      ),
    },
    {
      title: "Nhân viên",
      minWidth: "200px",
      render: (rowData) => (
        <>
          {rowData.displayName && (
            <p className="m-0"><strong>{rowData.displayName}</strong></p>
          )}

          {rowData.birthDate && (
            <p className="m-0">Ngày sinh: {formatDate("DD/MM/YYYY", rowData.birthDate)}</p>
          )}

          {rowData.gender && (
            <p className="m-0">Giới tính: {rowData.gender === "M" ? "Nam" : rowData.gender === "F" ? "Nữ" : ""}</p>
          )}

          {rowData.birthPlace && (
            <p className="m-0">Nơi sinh: {rowData.birthPlace}</p>
          )}
        </>
      ),
    },
    {
      title: "Thông tin liên hệ",
      field: "info",
      minWidth: "200px",
      render: (rowData) => (
        <>
          {rowData.phoneNumber && (
            <p className="m-0">SĐT: {rowData.phoneNumber}</p>
          )}

          {rowData.email && (
            <p className="m-0">Email: {rowData.email}</p>
          )}

        </>
      ),
    },

    {
      title: "Phòng ban",
      field: "department.name",
      align: "left",
      minWidth: "120px",
      render: (rowData) => (
        <span className="pr-6">
          {rowData?.department?.name}
        </span>
      ),
    },

    {
      title: "Nơi ở hiện tại",
      field: "currentResidence",
      align: "left",
      minWidth: "180px",
      render: (rowData) => (
        <span className="pr-6">
          {rowData?.currentResidence}
        </span>
      ),
    },
  ];

  return (
    <GlobitsPopupV2
      size="md"
      scroll={"body"}
      open={openListDuplicate}
      noDialogContent
      title={"Nhân viên có thông tin trùng với ứng viên"}
      onClosePopup={handleCloseListDuplicate}
    >
      <DialogContent className="o-hidden p-12">
        <Grid container spacing={2}>

          <GlobitsTable
            columns={columns}
            data={duplicateResponse?.listStaff || []}
            nonePagination
          />

        </Grid>
      </DialogContent>

      <DialogActions className="dialog-footer px-12">
        <div className="flex flex-space-between flex-middle">
          <Button
            variant="contained"
            className="mr-12 btn btn-secondary d-inline-flex"
            color="secondary"
            // disabled={isSubmitting}
            onClick={handleCloseListDuplicate}
          >
            {t("general.button.close")}
          </Button>
          <Button
            className="mr-0 btn btn-primary d-inline-flex"
            variant="contained"
            color="primary"
            onClick={handleCloseListDuplicate}
            type="submit"
          // disabled={isSubmitting}
          >
            Xác nhận
          </Button>
        </div>
      </DialogActions>
    </GlobitsPopupV2>
  );
}

export default memo(observer(DuplicateStaffsCandidatesPopup));
