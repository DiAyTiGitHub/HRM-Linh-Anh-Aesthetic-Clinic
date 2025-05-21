import {Button, ButtonGroup, DialogActions, DialogContent, Grid, Icon, IconButton, makeStyles} from '@material-ui/core';
import {useStore} from 'app/stores';
import {FieldArray, Form, Formik} from 'formik';
import {observer} from 'mobx-react';
import React, {memo, useEffect, useState} from 'react'
import {useTranslation} from 'react-i18next';
import * as Yup from "yup";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsTextField from 'app/common/form/GlobitsTextField';
import {deleteAsset, getListAssetByProduct, pagingProductType, saveAsset} from '../AssetManagementService';
import {Asset, ProductAttribute} from 'app/common/Model/Assets';
import AddIcon from "@material-ui/icons/Add";
import {pagingAllDepartments} from 'app/views/Department/DepartmentService';
import GlobitsVNDCurrencyInput from 'app/common/form/GlobitsVNDCurrencyInput';
import {formatDate} from 'app/LocalFunction';
import {AssetForm} from 'app/views/HumanResourcesInformation/TabContainer/Asset';
import GlobitsConfirmationDialog from 'app/common/GlobitsConfirmationDialog';
import GlobitsPagingAutocomplete from 'app/common/form/GlobitsPagingAutocomplete';
import GlobitsPopupV2 from 'app/common/GlobitsPopupV2';
import {CodePrefixes} from "../../../LocalConstants";

const useStyles = makeStyles(() => ({
    root: {
        background: "#E4f5fc",
        padding: "10px 15px",
        borderRadius: "5px",
    },
    groupContainer: {
        width: "100%",
        "& .MuiOutlinedInput-root": {
            borderRadius: "0!important",
        },
    },
    tableContainer: {
        width: "100%",
        marginTop: "2px",
        overflowX: "auto",
        overflowY: "hidden",
        "& table": {
            border: "1px solid #ccc",
            borderCollapse: "collapse",
            "& td": {
                border: "1px solid #ccc",
            },
        },
    },
    tableHeader: {
        width: "100%",
        borderBottom: "1px solid #ccc",
        marginBottom: "8px",
        "& th": {
            width: "calc(100vw / 4)",
            border: "1px solid #ccc",
            padding: "8px 0 8px 4px",
        },
    },
}));

const ProductForm = () => {
    const {t} = useTranslation();
    const classes = useStyles();

    const {selectedProduct, onSaveProduct, onClosePopup,autoGenCode} = useStore().assetManagementStore.productStore;
    const [listAsset, setListAsset] = useState([]);

    const [selectedAsset, setSelectedAsset] = useState(null);
    const [selectedDelete, setSelectedDelete] = useState(null);
    const [productForm , setProductForm] = useState(selectedProduct);

    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.CONG_CU_DUNG_CU);

        if (code) {
            // Tạo object mới để tránh thay đổi trực tiếp state
            const updated = {...selectedProduct, ...{code:code}};
            setProductForm(updated);
        }
    };
    useEffect(() => {
        if(!selectedProduct?.id){
            autoGenCodeFunc();
        }
    }, []);
    const validationSchema = Yup.object({
        code: Yup.string().required(t("validation.required")).nullable(),
        name: Yup.string().required(t("validation.required")).nullable(),
        serialNumber: Yup.string().required(t("validation.required")).nullable(),
        department: Yup.object().required(t("validation.required")).nullable(),
        productType: Yup.object().required(t("validation.required")).nullable(),
    });

    useEffect(() => {
        if (selectedProduct?.id) {
            getListAssetByProduct(selectedProduct.id).then(({data}) => setListAsset(data))
        }
    }, [selectedProduct]);

    const onSaveAsset = async (value) => {
        try {
            const res = await saveAsset(value);
            if (!res?.data) {
                throw new Error("Not response")
            }

            if (selectedProduct?.id) {
                getListAssetByProduct(selectedProduct.id).then(({data}) => setListAsset(data))
            }

            setSelectedAsset(false);
        } catch (error) {
        }
    }

    const onDeleteAsset = async () => {
        try {
            const res = await deleteAsset(selectedDelete);
            if (!res?.data) {
                throw new Error("Not response")
            }

            if (selectedProduct?.id) {
                getListAssetByProduct(selectedProduct.id).then(({data}) => setListAsset(data))
            }

            setSelectedDelete(false);
        } catch (error) {
        }
    }

    return (
        Boolean(selectedProduct) ? (
            <GlobitsPopupV2
                open
                noDialogContent
                title="Sản phẩm"
                onClosePopup={onClosePopup}
                size="md"
            >
                <Formik
                    validationSchema={validationSchema}
                    enableReinitialize
                    initialValues={productForm}
                    onSubmit={onSaveProduct}
                >
                    {({isSubmitting, values}) => (
                        <Form autoComplete="off">
                            <DialogContent className='o-hidden dialog-body'>
                                <Grid container spacing={2}>
                                    <Grid item md={6}>
                                        <GlobitsTextField validate label="Tên sản phẩm" name="name" className="mt-2"/>
                                    </Grid>

                                    <Grid item md={6}>
                                        <GlobitsTextField validate label="Mã sản phẩm" name="code"/>
                                    </Grid>

                                    <Grid item md={6}>
                                        <GlobitsPagingAutocomplete required label="Loại sản phẩm" name="productType"
                                                                   api={pagingProductType}/>
                                    </Grid>

                                    <Grid item md={6}>
                                        <GlobitsTextField label="Model" name="model"/>
                                    </Grid>

                                    <Grid item md={6}>
                                        <GlobitsTextField validate label="Số seri" name="serialNumber"/>
                                    </Grid>

                                    <Grid item md={6}>
                                        <GlobitsTextField label="Nhà sản xuất" name="manufacturer"/>
                                    </Grid>

                                    <Grid item md={6}>
                                        <GlobitsVNDCurrencyInput label="Giá" name="price"/>
                                    </Grid>

                                    <Grid item md={6}>
                                        <GlobitsPagingAutocomplete required label="Phòng ban quản lý" name="department"
                                                                   api={pagingAllDepartments}/>
                                    </Grid>
                                </Grid>

                                <FieldArray name="attributes">
                                    {({push, remove}) => (
                                        <>
                                            <div className='flex items-center justify-between my-8'>
                                                <h5>Thuộc tính</h5>
                                                <ButtonGroup color='container'
                                                             aria-label='outlined primary button group'>
                                                    <Button
                                                        startIcon={<AddIcon/>}
                                                        type='button'
                                                        onClick={() => push({...new ProductAttribute()})}
                                                    >
                                                        Thêm thuộc tính
                                                    </Button>
                                                </ButtonGroup>
                                            </div>

                                            <Grid item xs={12} style={{overflowX: "auto"}}>
                                                <section className={classes.tableContainer}>
                                                    <table className={classes.table}>
                                                        <thead>
                                                        <tr className={classes.tableHeader}>
                                                            <th align='left'>Tên</th>
                                                            <th align='left'>Chi tiết</th>
                                                            <th width="10%">Thao tác</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        {values?.attributes?.length > 0 ? (
                                                            values?.attributes?.map((item, index) => (
                                                                <tr key={index} className='row-table-body'>
                                                                    <td style={{verticalAlign: "top"}}>
                                                                        <GlobitsTextField
                                                                            name={`attributes[${index}].name`}/>
                                                                    </td>
                                                                    <td>
                                                                        <GlobitsTextField multiline
                                                                                          name={`attributes[${index}].description`}/>
                                                                    </td>
                                                                    <td align='center' width="10%">
                                                                        <IconButton size="small"
                                                                                    onClick={() => remove(index)}>
                                                                            <Icon fontSize="small"
                                                                                  color="secondary">delete</Icon>
                                                                        </IconButton>
                                                                    </td>
                                                                </tr>
                                                            ))
                                                        ) : (
                                                            <tr className='row-table-body row-table-no_data'>
                                                                <td colSpan={5} align='center' className='py-8'>
                                                                    Chưa có phần tử nào
                                                                </td>
                                                            </tr>
                                                        )}
                                                        </tbody>
                                                    </table>
                                                </section>
                                            </Grid>
                                        </>
                                    )}
                                </FieldArray>

                                {values?.id && (
                                    <>
                                        <div className='flex items-center justify-between my-8'>
                                            <h5>Quá trình sử dụng</h5>
                                            <ButtonGroup color='container' aria-label='outlined primary button group'>
                                                <Button
                                                    startIcon={<AddIcon/>}
                                                    type='button'
                                                    onClick={() => setSelectedAsset({
                                                        ...new Asset(),
                                                        product: selectedProduct
                                                    })}>
                                                    Thêm quá trình sử dụng
                                                </Button>
                                            </ButtonGroup>
                                        </div>

                                        <Grid item xs={12} style={{overflowX: "auto"}}>
                                            <section className={classes.tableContainer}>
                                                <table className={classes.table}>
                                                    <thead>
                                                    <tr className={classes.tableHeader}>
                                                        <th width="10%">TT</th>
                                                        <th align='left'>Nhân viên</th>
                                                        <th>Ngày bắt đầu dùng</th>
                                                        <th>Ngày kết thúc</th>
                                                        <th width="10%">Thao tác</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    {listAsset?.map((item, index) => (
                                                        <tr key={index} className='row-table-body'>
                                                            <td align='center'>{index + 1}</td>
                                                            <td className='p-4'>{item?.staff?.displayName}</td>
                                                            <td align='center'
                                                                className='p-4'>{formatDate("DD/MM/YYYY", item.startDate)}</td>
                                                            <td align='center'
                                                                className='p-4'>{formatDate("DD/MM/YYYY", item.endDate)}</td>
                                                            <td align='center' className='p-4'>
                                                                <IconButton size="small"
                                                                            onClick={() => setSelectedAsset(item)}>
                                                                    {/* <svg width={24} height={24} viewBox="0 0 24 24" fill="none" stroke="#7CB9E8">
                                                                        <g strokeWidth="0"></g>
                                                                        <g strokeLinecap="round" strokeLinejoin="round"></g>
                                                                        <g>
                                                                            <path d="M15.0007 12C15.0007 13.6569 13.6576 15 12.0007 15C10.3439 15 9.00073 13.6569 9.00073 12C9.00073 10.3431 10.3439 9 12.0007 9C13.6576 9 15.0007 10.3431 15.0007 12Z" stroke="#7CB9E8" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"></path>
                                                                            <path d="M12.0012 5C7.52354 5 3.73326 7.94288 2.45898 12C3.73324 16.0571 7.52354 19 12.0012 19C16.4788 19 20.2691 16.0571 21.5434 12C20.2691 7.94291 16.4788 5 12.0012 5Z" stroke="#7CB9E8" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"></path>
                                                                        </g>
                                                                    </svg> */}
                                                                    <Icon fontSize="small" color="primary">edit</Icon>
                                                                </IconButton>

                                                                <IconButton size="small"
                                                                            onClick={() => setSelectedDelete(item.id)}>
                                                                    <Icon fontSize="small"
                                                                          color="secondary">delete</Icon>
                                                                </IconButton>
                                                            </td>
                                                        </tr>
                                                    ))}
                                                    </tbody>
                                                </table>
                                            </section>
                                        </Grid>
                                    </>
                                )
                                }
                            </DialogContent>


                            <DialogActions className="p-0 dialog-footer">
                                <div className="flex flex-space-between flex-middle">
                                    <Button
                                        startIcon={<BlockIcon/>}
                                        variant="contained"
                                        className="mr-12 btn btn-secondary d-inline-flex"
                                        color="secondary"
                                        onClick={() => onClosePopup()}
                                    >
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon/>}
                                        className="mr-0 btn btn-primary d-inline-flex"
                                        variant="contained"
                                        color="primary"
                                        type="submit"
                                        disabled={isSubmitting}
                                    >
                                        {t("general.button.save")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </Form>
                    )}
                </Formik>

                <AssetForm asset={selectedAsset} onClosePopup={() => setSelectedAsset(null)} selectStaff
                           onSaveAsset={onSaveAsset}/>

                <GlobitsConfirmationDialog
                    open={Boolean(selectedDelete)}
                    onConfirmDialogClose={() => setSelectedDelete(null)}
                    onYesClick={onDeleteAsset}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            </GlobitsPopupV2>
        ) : (
            <></>
        )
    )
}

export default memo(observer(ProductForm))