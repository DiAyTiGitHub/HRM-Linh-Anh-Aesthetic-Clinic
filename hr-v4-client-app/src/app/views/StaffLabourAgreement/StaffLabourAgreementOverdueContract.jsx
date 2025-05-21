import React, { useEffect, memo } from "react";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import LocalConstants from "../../LocalConstants";
import GlobitsTable from "app/common/GlobitsTable";
import { IconButton, Icon, Tooltip, DialogContent, Grid } from "@material-ui/core";
import { formatDate } from "app/LocalFunction";
import { Form } from "formik";

function StaffLabourAgreementOverdueContract() {
  const { staffLabourAgreementStore } = useStore();
  const { t } = useTranslation();
  const {
    openCreateEditPopupOverdueContract,
    handleClosePopupOverdueContract,
    listOverdueContract,
    totalPagesOfOverdueContract,
    totalElementsOfOverdueContract,
    searchObjectOfOverdueContract,
    handleChangePageOfOverdueContract,
    setPageSizeOfOverdueContract,

    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
    handleExportHDLD
  } = staffLabourAgreementStore;

  const columns = [
    {
      title: t("general.action"),
      minWidth: "48px",
      align: "center",
      render: (rowData) => {
        return (
          <div className="flex flex-middle justify-center">
            <Tooltip title="Cập nhật thông tin" placement="top">
              <IconButton size="small" onClick={function () {
                handleOpenCreateEdit(rowData?.id);
              }}>
                <Icon fontSize="small" color="primary">
                  edit
                </Icon>
              </IconButton>
            </Tooltip>

            {/* <Tooltip title="Xóa" placement="top">
              <IconButton
                size="small"
                className="ml-4"
                onClick={() => handleDelete(rowData)
                }
              >
                <Icon fontSize="small" color="secondary">
                  delete
                </Icon>
              </IconButton>
            </Tooltip>

            <Tooltip title="Tải xuống hợp đồng lao động" arrow>
              <IconButton
                size="small"
                className="ml-4"
                onClick={() => handleExportHDLD(rowData?.id)}
              >
                <Icon fontSize="small" color="blue">
                  description
                </Icon>
              </IconButton>
            </Tooltip> */}
          </div>
        );
      },
    },
    {
      title: t("agreements.labourAgreementNumber"),
      field: "labourAgreementNumber",
      align: "left",
    },
    {
      title: "Nhân viên áp dụng",
      field: "staff",
      align: "left",
      render: data => <span>{data?.staff?.displayName}</span>
    },
    {
      title: t("agreements.signedDate"),
      field: "signedDate",
      align: "left",
      render: data => data?.signedDate && (<span>{formatDate("DD/MM/YYYY", data?.signedDate)}</span>)
    },
    {
      title: t("agreements.startDate"),
      field: "startDate",
      align: "left",
      render: data => data?.startDate && (<span>{formatDate("DD/MM/YYYY", data?.startDate)}</span>)
    },
    {
      title: t("agreements.endDate"),
      field: "endDate",
      align: "left",
      render: data => data?.endDate && (<span>{formatDate("DD/MM/YYYY", data?.endDate)}</span>)
    },
    {
      title: t("agreements.contractType"),
      field: "contractType",
      align: "left",
      render: data => <span>{data?.contractType?.name}</span>
    },
    {
      title: "Trạng thái hợp đồng",
      field: "agreementStatus",
      align: "left",
      render: row => {
        return <span>{LocalConstants.AgreementStatus.getListData().find(i => i.value == row?.agreementStatus)?.name}</span>
      }
    }
  ];

  // useEffect(function () {
  // }, []);

  return (

    <GlobitsPopupV2
      scroll={"body"}
      size="md"
      open={openCreateEditPopupOverdueContract}
      noDialogContent
      title={"Danh sách hợp đồng sắp hết hạn"}
      onClosePopup={handleClosePopupOverdueContract}
    >
      <DialogContent className="o-hidden p-12 pt-10" style={{ maxHeight: "80vh" }}>
        <Grid container spacing={2}>
            <GlobitsTable
              selection={false}
              data={listOverdueContract}
              handleSelectList={handleSelectListDelete}
              columns={columns}
              totalPages={totalPagesOfOverdueContract}
              handleChangePage={handleChangePageOfOverdueContract}
              setRowsPerPage={setPageSizeOfOverdueContract}
              pageSize={searchObjectOfOverdueContract?.pageSize}
              pageSizeOption={[10, 15, 25, 50, 100]}
              totalElements={totalElementsOfOverdueContract}
              page={searchObjectOfOverdueContract?.pageIndex}

            />
          </Grid>
      </DialogContent>

    </GlobitsPopupV2>

  );
}

export default memo(observer(StaffLabourAgreementOverdueContract));






