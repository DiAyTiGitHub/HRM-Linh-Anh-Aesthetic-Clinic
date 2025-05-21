import React from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon } from "@material-ui/core";
import Config from "../../common/GlobitsConfigConst";
import { observer } from "mobx-react";

function MaterialButton (props) {
  const {item} = props;
  return (
      <div>
        <IconButton size="small" onClick={() => props.onSelect (item, 0)}>
          <Icon fontSize="small" color="primary">
            edit
          </Icon>
        </IconButton>
        <IconButton size="small" onClick={() => props.onSelect (item, 1)}>
          <Icon fontSize="small" color="secondary">
            delete
          </Icon>
        </IconButton>
      </div>
  );
}

export default observer (function CertificateList () {
  const {certificateStore} = useStore ();
  const {t} = useTranslation ();

  const {
    certificateList,
    totalPages,
    totalElements,
    rowsPerPage,
    page,
    handleChangePage,
    setRowsPerPage,
    handleDelete,
    handleEditCertificate,
    getCertificateType,
    handleSelectListCertificate,
  } = certificateStore;

  let columns = [
    {
      title:t ("general.action"),
      width:"30%",
      ... Config.tableCellConfig,
      render:(rowData) => (
          <MaterialButton
              item={rowData}
              onSelect={(rowData, method) => {
                if (method === 0) {
                  handleEditCertificate (rowData.id);
                } else if (method === 1) {
                  handleDelete (rowData.id);
                } else {
                  alert ("Call Selected Here:" + rowData.id);
                }
              }}
          />
      ),
    },
    {
      title:t ("certificate.code"),
      minWidth:"300px",
      field:"code",
      ... Config.tableCellConfig,
    },
    {
      title:t ("Mô tả"),
      width:"30%",
      field:"name",
      ... Config.tableCellConfig
    },
  ];

  return (
      <GlobitsTable
          selection
          handleSelectList={handleSelectListCertificate}
          data={certificateList}
          columns={columns}
          totalPages={totalPages}
          handleChangePage={handleChangePage}
          setRowsPerPage={setRowsPerPage}
          pageSize={rowsPerPage}
          pageSizeOption={[10, 25, 50]}
          totalElements={totalElements}
          page={page}
      />
  );
});
