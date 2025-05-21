/* eslint-disable react-hooks/exhaustive-deps */
import { Button, ButtonGroup, Grid, Icon, IconButton, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate } from "app/LocalFunction";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import AssetForm from "./AssetForm";
import AssetTransferForm from "./AssetTransferForm";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";

const AssetIndex = () => {
  const {t} = useTranslation ();
  const {
    pageAsset,
    searchAsset,
    handleChangeFormSearch,
    openFormAsset,
    pagingAsset,
    resetStore,
    onOpenTransferAsset,
    selectedReturn,
    onConfirmReturnAsset,
    onOpenReturnAsset,
    handleClose,
    uploadFileExcel,
    handleDownloadAssetTemplate,
  } = useStore ().assetManagementStore.assetStore;

  const columns = [{
    title:t ("general.action"), render:(rowData) => (<div className='flex flex-middle justify-center'>
          <Tooltip title='Chỉnh sửa' placement='top'>
            <IconButton className='bg-white' size='small' onClick={() => openFormAsset (rowData.id)}>
              <Icon fontSize='small' color='primary'>edit</Icon>
            </IconButton>
          </Tooltip>

          {/* Only show return/transfer buttons if staff exists */}
          {rowData?.staff?.id && (<>
                <Tooltip title='Hoàn trả' placement='top'>
                  <IconButton
                      className='bg-white'
                      size='small'
                      onClick={() => onOpenReturnAsset (rowData.id)}
                  >
                    <Icon fontSize='small' color='success'>replay</Icon>
                  </IconButton>
                </Tooltip>
                <Tooltip title='Đổi người sử dụng' placement='top'>
                  <IconButton
                      className='bg-white'
                      size='small'
                      onClick={() => onOpenTransferAsset (rowData)}
                  >
                    <Icon fontSize='small' color='warning'>autorenew</Icon>
                  </IconButton>
                </Tooltip>
              </>)}
        </div>),
  }, {title:"Công cụ/ dụng cụ", field:"product.name"}, {title:"Nhân viên", field:"staff.displayName"}, {
    title:"Ngày bắt đầu dùng", field:"startDate", render:(value) => formatDate ("DD/MM/YYYY", value.startDate),
  }, {title:"Ngày kết thúc", field:"endDate", render:(value) => formatDate ("DD/MM/YYYY", value.endDate)},];

  useEffect (() => {
    pagingAsset ();
    checkAllUserRoles ();
    return resetStore;
  }, []);
  const {isManager, isAdmin, checkAllUserRoles} = useStore ().hrRoleUtilsStore;

  return (<div className='content-index'>
        <div className='index-breadcrumb'>
          <GlobitsBreadcrumb routeSegments={[{name:"Công cụ/Dụng cụ"}]}/>
        </div>
        <Grid className='index-card' container spacing={3}>
          <Grid item xs={12}>
            <Formik enableReinitialize initialValues={searchAsset} onSubmit={handleChangeFormSearch}>
              {({resetForm, values, setFieldValue, setValues}) => (<Form autoComplete='off'>
                    <Grid container spacing={2}>
                      <Grid item xs={12} md={6}>
                        {(isManager || isAdmin) && (
                            <ButtonGroup color='container' aria-label='outlined primary button group'>
                              <Button
                                  startIcon={<AddIcon/>}
                                  type='button'
                                  onClick={() => openFormAsset ()}>
                                Thêm mới
                              </Button>
                              <Tooltip
                                  placement="top"
                                  title={"Import công cụ, dụng cụ"}
                                  arrow
                              >
                                <Button
                                    startIcon={<CloudUploadIcon/>}
                                    onClick={() => document.getElementById ("fileExcel").click ()}
                                >
                                  {t ("general.button.importExcel")}
                                </Button>
                              </Tooltip>

                              <Tooltip
                                  placement="top"
                                  title={"Tải mẫu import công cụ, dụng cụ"}
                                  arrow
                              >
                                <Button
                                    startIcon={<CloudDownloadIcon/>}
                                    onClick={() => handleDownloadAssetTemplate ()}
                                >
                                  Xuất Excel
                                </Button>
                              </Tooltip>
                            </ButtonGroup>)}
                        {(isManager || isAdmin) && (<input
                                type="file"
                                id="fileExcel"
                                style={{display:"none"}}
                                onChange={uploadFileExcel}
                            />)}
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <div className='flex justify-between align-center'>
                          <GlobitsTextField
                              placeholder='Tìm kiếm theo từ khóa'
                              name='keyword'
                              variant='outlined'
                              notDelay
                          />
                          <ButtonGroup
                              className='filterButtonV4'
                              color='container'
                              aria-label='outlined primary button group'>
                            <Button
                                startIcon={<SearchIcon/>}
                                className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                type='submit'>
                              Tìm kiếm
                            </Button>
                          </ButtonGroup>
                        </div>
                      </Grid>
                    </Grid>
                  </Form>)}
            </Formik>
          </Grid>
          <Grid item xs={12}>
            <GlobitsTable
                columns={columns}
                data={pageAsset.content || []}
                totalPages={pageAsset.totalPages}
                totalElements={pageAsset.totalElements}
                handleChangePage={(_, pageIndex) => handleChangeFormSearch ({pageIndex})}
                setRowsPerPage={({target}) => handleChangeFormSearch ({pageSize:target.value})}
                pageSize={searchAsset.rowsPerPage}
                page={searchAsset.pageIndex}
            />
          </Grid>
        </Grid>

        <GlobitsConfirmationDialog
            open={Boolean (selectedReturn)}
            onConfirmDialogClose={handleClose}
            onYesClick={onConfirmReturnAsset}
            title='Hoàn trả công cụ/dụng cụ'
            text='Sau khi hoàn trả, nhân viên sẽ không được sử dụng công cụ/dụng cụ này nữa. Bạn có chắc chắn muốn hoàn trả?'
            agree='Đồng ý'
            cancel='Hủy'
        />

        <AssetTransferForm/>
        <AssetForm/>
      </div>);
};

export default memo (observer (AssetIndex));
