import {
  Grid,
  makeStyles,
  DialogContent,
  DialogActions,
  ButtonGroup,
  IconButton,
  Icon,
  Tooltip,
} from "@material-ui/core";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate, getDate } from "app/LocalFunction";
import { FieldArray, useFormikContext, Formik, Form } from "formik";
import React, { memo, useMemo, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import "react-toastify/dist/ReactToastify.css";
import { Button } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import localStorageService from "app/services/localStorageService";
import { useParams } from "react-router";

// pop up
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import CheckIcon from "@material-ui/icons/Check";
import { pagingStaffDocumentItem } from "app/views/StaffDocumentItem/StaffDocumentItemService";
import StaffDocumentItemCUForm from "app/views/StaffDocumentItem/StaffDocumentItemCUForm";
import PreviewFile from "app/views/StaffDocumentItem/PreviewFile";

const useStyles = makeStyles ((theme) => ({
  root:{
    "& .MuiAccordion-rounded":{borderRadius:"5px"},
    "& .MuiPaper-root":{borderRadius:"5px"},
    "& .MuiAccordionSummary-root":{
      borderRadius:"5px",
      color:"#5899d1",
      fontWeight:"400",
      "& .MuiTypography-root":{fontSize:"1rem"},
    },
    "& .Mui-expanded":{
      "& .MuiAccordionSummary-root":{
        backgroundColor:"#EBF3F9",
        color:"#5899d1",
        fontWeight:"700",
        maxHeight:"50px !important",
        minHeight:"50px !important",
      },
      "& .MuiTypography-root":{fontWeight:700},
    },
    "& .MuiButton-root":{borderRadius:"0.125rem !important"},
  },
  noAllowance:{
    textAlign:"center",
    marginTop:theme.spacing (4),
    fontStyle:"italic",
    color:"#999",
  },
  buttonGroupSpacing:{
    marginBottom:"10px",
  },
}));

function StaffDocumentItems () {
  const {t} = useTranslation ();
  const classes = useStyles ();
  const {values} = useFormikContext ();
  const {staffDocumentItemStore} = useStore ();
  const {id} = useParams ();

  const {
    openCreateEditPopup,
    setOpenCreateEditPopup,
    setSelectedStaffDocumentItem,
    searchObject,
    handleSetSearchObject,
    openConfirmDeletePopup,
    handleConfirmDeleteByStaff:handleConfirmDeleteItem,
    handleClose,
    handleOpenCreateEdit,
    handleDelete,
  } = staffDocumentItemStore;

  const [data, setData] = useState (null);
  const [isLoading, setIsLoading] = useState (true);

  useEffect (() => {
    fetchStaffDocumentItem ();
  }, [id]);

  const fetchStaffDocumentItem = async () => {
    if (!id) return;
    setIsLoading (true);

    const newSearchObject = {... searchObject, staffId:id, pageIndex:1, pageSize:9999};

    try {
      handleSetSearchObject (newSearchObject);
      const {data} = await pagingStaffDocumentItem (newSearchObject);
      setData (data?.content);
    } catch (error) {
      console.error ("Lỗi khi tải danh sách tài liệu", error);
    } finally {
      setIsLoading (false);
    }
  };

  async function handleOpenHrIntroduceCostCUForm () {
    setSelectedStaffDocumentItem ({staff:{id:id}});
    setOpenCreateEditPopup (true);
  }

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
      title:"Tài liệu/Hồ sơ",
      field:"documentItem.name",
      align:"center",
    },

    {
      title:"Tệp đính kèm",
      field:"documentItem.name",
      align:"left",
      render:(data) => {
        return <PreviewFile fileProp={data?.file} showPreview={true} showDowload={true} showDelete={false}/>;
      },
    },
    {
      title:"Ngày nộp",
      field:"submissionDate",
      render:(row) => <span>{formatDate ("DD/MM/YYYY", row?.submissionDate)}</span>,
      align:"center",
    },

    {
      title:"Đã nộp",
      field:"isSubmitted",
      width:"10%",
      align:"center",
      render:(data) => {
        if (data?.isSubmitted) return <CheckIcon fontSize='small' style={{color:"green"}}/>;
        return "";
      },
    },
  ];

  const isAdmin = useMemo (() => {
    let roles = localStorageService.getLoginUser ()?.user?.roles?.map ((item) => item.authority) || [];
    let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
    return roles.some ((role) => auth.indexOf (role) !== -1);
  }, []);

  async function handleConfirmDelete () {
    try {
      const response = await handleConfirmDeleteItem ();

      if (!response) throw new Error ();

      await fetchStaffDocumentItem ();
    } catch (error) {
      console.error (error);
    }
  }

  return (
      <>
        <Grid container spacing={2} className={classes.root}>
          {isAdmin && values?.id && (
              <Grid item xs={12} className='pb-0'>
                <ButtonGroup
                    color='container'
                    aria-label='outlined primary button group'
                    className={classes.buttonGroupSpacing}>
                  <Tooltip title='Thêm mới tài liệu/hồ sơ của nhân viên' placement='top' arrow>
                    <Button startIcon={<AddIcon/>} type='button' onClick={handleOpenHrIntroduceCostCUForm}>
                      Thêm mới
                    </Button>
                  </Tooltip>
                </ButtonGroup>
              </Grid>
          )}

          <Grid item xs={12}>
            {isLoading? (
                <p className='w-100 text-center'>Đang tải dữ liệu...</p>
            ) : data && data.length > 0? (
                <GlobitsTable data={data} columns={columns} maxWidth='100%' nonePagination selection={false}/>
            ) : (
                <p className='w-100 text-center'>Chưa có tài liệu nào</p>
            )}
          </Grid>
        </Grid>

        {openCreateEditPopup && <StaffDocumentItemCUForm staffId={values?.id} onSaved={fetchStaffDocumentItem}/>}

        {openConfirmDeletePopup && (
            <GlobitsConfirmationDialog
                open={openConfirmDeletePopup}
                onConfirmDialogClose={handleClose}
                onYesClick={handleConfirmDelete}
                title={t ("confirm_dialog.delete.title")}
                text={t ("confirm_dialog.delete.text")}
                agree={t ("confirm_dialog.delete.agree")}
                cancel={t ("confirm_dialog.delete.cancel")}
            />
        )}
      </>
  );
}

export default memo (observer (StaffDocumentItems));
