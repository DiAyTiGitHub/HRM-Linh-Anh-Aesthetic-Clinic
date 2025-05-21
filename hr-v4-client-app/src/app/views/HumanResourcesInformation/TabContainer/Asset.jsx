/* eslint-disable react-hooks/exhaustive-deps */
import { Button, ButtonGroup, DialogActions, DialogContent, Grid, Icon, IconButton, Tooltip } from "@material-ui/core";
import AddIcon from "@material-ui/icons/Add";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { formatDate } from "app/LocalFunction";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";
import GlobitsTable from "app/common/GlobitsTable";
import { Asset } from "app/common/Model/Assets";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import localStorageService from "app/services/localStorageService";
import { useStore } from "app/stores";
import { pagingProduct, returnAsset, saveAsset } from "app/views/AssetManagement/AssetManagementService";
import { Form, Formik } from "formik";
import { memo, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router-dom/cjs/react-router-dom.min";
import { toast } from "react-toastify";
import * as Yup from "yup";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";
import { pagingStaff } from "../StaffService";
import AssetTransferForm from "app/views/AssetManagement/Asset/AssetTransferForm";

const TabAsset = () => {
    const { t } = useTranslation();
    // const { values } = useFormikContext();

    const isAdmin = useMemo(() => {
        let roles = localStorageService.getLoginUser()?.user?.roles?.map((item) => item.authority) || [];
        let auth = ["HR_MANAGER", "ROLE_ADMIN", "ROLE_SUPER_ADMIN"];
        return roles.some((role) => auth.indexOf(role) !== -1);
    }, []);

    const [selectedEdit, setSelectedEdit] = useState(null);
    const [selectedDelete, setSelectedDelete] = useState(null);

    const [asset, setAsset] = useState(null);
    const { onOpenTransferAsset, handleClose } = useStore().assetManagementStore.assetStore;

    const handleSubmitForm = (value) => {
        const dto = {
            ...value,
            staff: {
                id: id,
            },
        };

        saveAsset(dto)
            .then((result) => {
                console.log(result);
                setAsset(null);
                fetchData();
            })
            .catch((err) => {
                toast.error("Có lỗi trong quá trình lưu");
            });
    };

    const { assetManagementStore } = useStore();
    const { getAssetByStaff, listAsset, handleConfirmDelete } = assetManagementStore.assetStore;
    const [listAssets, setListAssets] = useState([]);
    const { id } = useParams();
    const [selectedReturn, setSelectedReturn] = useState(null);

    const onOpenReturnAsset = (id) => {
        setSelectedReturn(id);
    };
    const handleConfirmDeletePopup = () => {
        console.log(selectedDelete);
        if (selectedDelete?.id) {
            handleConfirmDelete(selectedDelete?.id)
                .then((result) => {
                    fetchData();
                })
                .catch((err) => {});
        }
    };
    const fetchData = () => {
        getAssetByStaff(id)
            .then((result) => {
                setListAssets(result);
            })
            .catch((err) => {});
    };

    useEffect(() => {
        fetchData();
    }, [id]);
    const columns = [
        ...(isAdmin
            ? [
                  {
                      title: t("general.action"),
                      cellStyle: { textAlign: "center" },
                      render: (asset) => (
                          <>
                              <IconButton
                                  size='small'
                                  onClick={() => {
                                      setAsset({
                                          ...asset,
                                          startDate: asset.startDate ? new Date(asset.startDate) : null,
                                          endDate: asset.endDate ? new Date(asset.endDate) : null,
                                      });
                                      setSelectedEdit(asset.tableData.id);
                                  }}>
                                  <Icon fontSize='small' color='primary'>
                                      edit
                                  </Icon>
                              </IconButton>

                              <IconButton size='small' onClick={() => setSelectedDelete(asset)}>
                                  <Icon fontSize='small' color='secondary'>
                                      delete
                                  </Icon>
                              </IconButton>
                              {asset?.staff?.id && (
                                  <>
                                      <Tooltip title='Hoàn trả' placement='top'>
                                          <IconButton
                                              className='bg-white'
                                              size='small'
                                              onClick={() => onOpenReturnAsset(asset.id)}>
                                              <Icon fontSize='small' color='success'>
                                                  replay
                                              </Icon>
                                          </IconButton>
                                      </Tooltip>
                                      <Tooltip title='Đổi người sử dụng' placement='top'>
                                          <IconButton
                                              className='bg-white'
                                              size='small'
                                              onClick={() => onOpenTransferAsset(asset)}>
                                              <Icon fontSize='small' color='warning'>
                                                  autorenew
                                              </Icon>
                                          </IconButton>
                                      </Tooltip>
                                  </>
                              )}
                          </>
                      ),
                  },
              ]
            : []),
        {
            title: "Công cụ/ dụng cụ",
            field: "product.name",
            cellStyle: { textAlign: "center" },
        },
        {
            title: "Ngày bắt đầu dùng",
            field: "startDate",
            cellStyle: { textAlign: "center" },
            render: (value) => formatDate("DD/MM/YYYY", value.startDate),
        },
        {
            title: "Ngày kết thúc",
            field: "endDate",
            cellStyle: { textAlign: "center" },
            render: (value) => formatDate("DD/MM/YYYY", value.endDate),
        },
    ];
    return (
        <Grid container spacing={2}>
            {isAdmin && (
                <Grid item xs={12} md={6}>
                    <ButtonGroup color='container' aria-label='outlined primary button group'>
                        <Button startIcon={<AddIcon />} onClick={() => setAsset(new Asset())}>
                            Thêm mới
                        </Button>
                    </ButtonGroup>
                </Grid>
            )}
            <Grid item xs={12}>
                <GlobitsTable nonePagination columns={columns} data={listAssets || []} />
            </Grid>
            <GlobitsConfirmationDialog
                open={Boolean(selectedDelete) || selectedDelete === 0}
                onConfirmDialogClose={() => setSelectedDelete(null)}
                onYesClick={() => {
                    handleConfirmDeletePopup(selectedDelete);
                }}
                title={t("confirm_dialog.delete.title")}
                text={t("confirm_dialog.delete.text")}
                agree={t("confirm_dialog.delete.agree")}
                cancel={t("confirm_dialog.delete.cancel")}
            />
            {console.log(selectedReturn)}
            <GlobitsConfirmationDialog
                open={Boolean(selectedReturn)}
                onConfirmDialogClose={() => {
                    setSelectedReturn(null);
                }}
                onYesClick={async () => {
                    await returnAsset(selectedReturn);
                    setSelectedReturn(null);
                }}
                handleAfterConfirm={fetchData}
                title='Hoàn trả công cụ/dụng cụ'
                text='Sau khi hoàn trả, nhân viên sẽ không được sử dụng công cụ/dụng cụ này nữa. Bạn có chắc chắn muốn hoàn trả?'
                agree='Đồng ý'
                cancel='Hủy'
            />
            <AssetForm
                selectProduct
                asset={asset}
                onClosePopup={() => setAsset(null)}
                onSaveAsset={(value) => {
                    handleSubmitForm(value);
                }}
            />
            <AssetTransferForm handleAfterConfirm={fetchData} />
            <AssetForm />
        </Grid>
    );
};

export default memo(TabAsset);

export const AssetForm = memo(({ asset, onClosePopup, onSaveAsset, readonly, selectStaff, selectProduct }) => {
    const { t } = useTranslation();

    const validationSchema = Yup.object({
        product: Yup.object().required(t("validation.required")).nullable(),
        startDate: Yup.date()
            .transform((castValue, originalValue) => (originalValue ? new Date(originalValue) : castValue))
            .required(t("validation.required"))
            .nullable()
            .typeError("Dữ liệu sai định dạng."), // báo lỗi nếu sai định dạng
        endDate: Yup.date()
            .transform((castValue, originalValue) => (originalValue ? new Date(originalValue) : castValue))
            .nullable()
            .typeError("Dữ liệu sai định dạng.") // báo lỗi nếu sai định dạng
            .min(Yup.ref("startDate"), t("Ngày kết thúc phải lơn hơn ngày bắt đầu")),
        staff: selectStaff ? Yup.object().required(t("validation.required")).nullable() : null,
    });

    return (
        <GlobitsPopupV2
            open={Boolean(asset)}
            noDialogContent
            title='Công cụ, dụng cụ'
            onClosePopup={onClosePopup}
            size='sm'>
            <Formik enableReinitialize initialValues={asset} onSubmit={onSaveAsset} validationSchema={validationSchema}>
                <Form autoComplete='off'>
                    <DialogContent className='dialog-body p-12'>
                        <Grid container spacing={2}>
                            {selectProduct && (
                                <Grid item md={4} sm={12} xs={12}>
                                    <GlobitsPagingAutocomplete
                                        label='Công cụ, dụng cụ'
                                        name='product'
                                        api={pagingProduct}
                                        disabled={readonly}
                                        getOptionLabel={(option) =>
                                            `${option?.name} ${option?.serialNumber ? " - " + option.serialNumber : ""}`
                                        }
                                        required
                                    />
                                </Grid>
                            )}

                            {selectStaff && (
                                <Grid item md={4} sm={12} xs={12}>
                                    <GlobitsPagingAutocomplete
                                        label='Nhân viên'
                                        name='staff'
                                        api={pagingStaff}
                                        disabled={readonly}
                                        displayData='displayName'
                                    />
                                </Grid>
                            )}
                            <Grid item md={4} sm={12} xs={12}>
                                <GlobitsDateTimePicker
                                    name='startDate'
                                    label='Ngày bắt đầu dùng'
                                    disabled={readonly}
                                    required
                                />
                            </Grid>

                            <Grid item md={4} sm={12} xs={12}>
                                <GlobitsDateTimePicker name='endDate' label='Ngày kết thúc' disabled={readonly} />
                            </Grid>

                            <Grid item md={12} sm={12} xs={12}>
                                <GlobitsTextField label='Ghi chú' name='note' multiline rows={3} disabled={readonly} />
                            </Grid>
                        </Grid>
                    </DialogContent>
                    <DialogActions className='dialog-footer px-12'>
                        <div className='flex flex-space-between flex-middle'>
                            <Button
                                startIcon={<BlockIcon />}
                                variant='contained'
                                className='mr-12 btn btn-secondary d-inline-flex'
                                color='secondary'
                                onClick={onClosePopup}>
                                {t("general.button.cancel")}
                            </Button>
                            <Button
                                startIcon={<SaveIcon />}
                                className='mr-0 btn btn-primary d-inline-flex'
                                variant='contained'
                                color='primary'
                                type='submit'>
                                {t("general.button.save")}
                            </Button>
                        </div>
                    </DialogActions>
                </Form>
            </Formik>
        </GlobitsPopupV2>
    );
});
