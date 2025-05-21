import React, { memo } from "react";
import GlobitsTable from "../../common/GlobitsTable";
import { useStore } from "../../stores";
import { useTranslation } from "react-i18next";
import { IconButton, Icon, Tooltip } from "@material-ui/core";
import { observer } from "mobx-react";
import { formatDate, getDate, getMonth } from "app/LocalFunction";
import CheckIcon from "@material-ui/icons/Check";
import PreviewFile from "./PreviewFile";

function StaffDocumentItemList () {
  const {staffDocumentItemStore} = useStore ();
  const {t} = useTranslation ();

  const {
    staffDocumentItemList,
    totalPages,
    totalElements,
    searchObject,
    handleChangePage,
    setPageSize,
    handleDelete,
    handleSelectListDelete,
    handleOpenCreateEdit,
  } = staffDocumentItemStore;

  let columns = [
    {
      title:t ("general.action"),
      width:"10%",
      align:"center",
      render:(rowData) => {
        return (
            <div className='flex flex-middle justify-center'>
              <Tooltip title='Cập nhật thông tin' placement='top'>
                <IconButton
                    size='small'
                    onClick={function () {
                      handleOpenCreateEdit (rowData?.id);
                    }}>
                  <Icon fontSize='small' color='primary'>
                    edit
                  </Icon>
                </IconButton>
              </Tooltip>

              <Tooltip title='Xóa' placement='top'>
                <IconButton size='small' className='ml-4' onClick={() => handleDelete (rowData)}>
                  <Icon fontSize='small' color='secondary'>
                    delete
                  </Icon>
                </IconButton>
              </Tooltip>
            </div>
        );
      },
    },
    {
      title:"Nhân viên",
      field:"staff.displayName",
      render:(row) => <span>{row?.staff?.displayName || ""}</span>,
      align:"left",
      minWidth:"150px"
    },
    {
      title:"Thuộc bộ hồ sơ",
      field:"staff.documentTemplate.name",
      render:(row) => <span>{row?.staff?.documentTemplate?.name || ""}</span>,
      align:"left",
      minWidth:"150px"
    },
    {
      title:"Tài liệu/Hồ sơ",
      field:"documentItem.name",
      render:(row) => <span>{row?.documentItem?.name || ""}</span>,
      align:"left",
      minWidth:"150px"
    },
    {
      title:"Ngày nộp",
      field:"submissionDate",
      render:(row) => <span>{formatDate ("DD/MM/YYYY", row?.submissionDate)}</span>,
      align:"center",
      minWidth:"150px",
    },
    {
      title:"Đã nộp",
      field:"isSubmitted",
      minWidth:"150px",
      align:"center",
      render:(data) => {
        if (data?.isSubmitted) return <CheckIcon fontSize='small' style={{color:"green"}}/>;
        return "";
      },
    },
    {
      title:"Bắt buộc",
      field:"documentItem.isRequired",
      minWidth:"150px",
      align:"center",
      render:(data) => {
        if (data?.documentItem?.isRequired) return <CheckIcon fontSize='small' style={{color:"green"}}/>;
        return "";
      },
    },
  ];

  return (
      <GlobitsTable
          selection
          data={staffDocumentItemList}
          handleSelectList={handleSelectListDelete}
          columns={columns}
          totalPages={totalPages}
          handleChangePage={handleChangePage}
          setRowsPerPage={setPageSize}
          pageSize={searchObject?.pageSize}
          pageSizeOption={[10, 15, 25, 50, 100]}
          totalElements={totalElements}
          page={searchObject?.pageIndex}
      />
  );
}

export default memo (observer (StaffDocumentItemList));
