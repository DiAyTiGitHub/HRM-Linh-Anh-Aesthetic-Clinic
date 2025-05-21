import { Button, DialogActions, DialogContent, Grid, Tooltip } from "@material-ui/core";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import SelectFile from "app/views/StaffDocumentItem/SelectFile";
import { Form, Formik } from "formik";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useParams } from "react-router";
import * as Yup from "yup";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import VisibilityIcon from "@material-ui/icons/Visibility";
import GetAppIcon from "@material-ui/icons/GetApp";
import DeleteIcon from "@material-ui/icons/Delete";

export default function StaffSignaturePopupAdd(props) {
    const {open} = props;
    const {t} = useTranslation();
    const {staffSignatureStore} = useStore();
    const {id} = useParams();

    const {saveStaffSignature, selectedStaffSignature, initialStaffSignature, handleClose, openCreateEditPopup} =
        staffSignatureStore;
    
    const handleSubmitNew = async (values) => {
        let dto = {
            ...values,
            staff: {
                id,
            },
        };
        await saveStaffSignature(dto);
    };

    const validationSchema = Yup.object({
        name: Yup.string().required(t("validation.required")), // Đổi object thành string
        code: Yup.string().required(t("validation.required")), // Đổi object thành string
    });

    const [formValues, setFormValues] = useState(null);

    useEffect(() => {
        if (selectedStaffSignature) {
            setFormValues({...selectedStaffSignature});
        } else {
            setFormValues({...initialStaffSignature});
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [selectedStaffSignature]);
    const handleAffterSubmitFile = (file) => {
    };
    return (
        <GlobitsPopupV2
            size={"sm"}
            open={open}
            onClosePopup={handleClose}
            noDialogContent
            title={(selectedStaffSignature?.id ? t("general.button.add") : t("general.button.edit")) + t(" Chữ ký")}>
            <Formik initialValues={formValues} onSubmit={handleSubmitNew} validationSchema={validationSchema}>
                {({isSubmitting, values}) => (
                    <Form autoComplete='off'>
                        <DialogContent className='dialog-body' style={{maxHeight: "80vh", minWidth: "300px"}}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={6}>
                                    <GlobitsTextField validate label={t("Tên chữ ký")} name='name' required/>
                                </Grid>
                                <Grid item xs={12} md={6}>
                                    <GlobitsTextField
                                        label={t("Mã chữ ký")}
                                        name='code'
                                        disabled
                                        required/>
                                </Grid>
                                <Grid item xs={12}>
                                    <SelectFile
                                        name={"file"}
                                        fileProp={values?.file}
                                        showPreview={true}
                                        showDowload={true}
                                        showDelete={true}
                                        showName={false}
                                        handleAffterSubmit={handleAffterSubmitFile}
                                        acceptedFiles={["image/jpeg", "image/png"]}
                                        maxFileSize={5242880}
                                        uploadButton={
                                            <Tooltip
                                                placement='top'
                                                title='Chọn ảnh'
                                                style={{
                                                    border: "1px solid rgba(0, 0, 0, 0.23)",
                                                    marginRight: "4px",
                                                }}>
                                                <Button startIcon={<CloudUploadIcon fontSize='small'/>}>
                                                    Chọn ảnh
                                                </Button>
                                            </Tooltip>
                                        }
                                        previewButton={
                                            <Tooltip placement='top' title='Xem trước'>
                                                <Button
                                                    style={{
                                                        border: "1px solid rgba(0, 0, 0, 0.23)",
                                                        marginRight: "4px",
                                                    }}>
                                                    <VisibilityIcon className='mr-6'/>
                                                    Xem trước
                                                </Button>
                                            </Tooltip>
                                        }
                                        downloadButton={
                                            <Tooltip placement='top' title='Tải xuống'>
                                                <Button
                                                    style={{
                                                        border: "1px solid rgba(0, 0, 0, 0.23)",
                                                        marginRight: "4px",
                                                    }}>
                                                    <GetAppIcon className='mr-6'/>
                                                    Tải xuống
                                                </Button>
                                            </Tooltip>
                                        }
                                        deleteButton={
                                            <Tooltip placement='top' title='Xóa'>
                                                <Button
                                                    style={{
                                                        border: "1px solid rgba(0, 0, 0, 0.23)",
                                                        marginRight: "4px",
                                                    }}>
                                                    <DeleteIcon className='mr-6'/>
                                                    Xóa
                                                </Button>
                                            </Tooltip>
                                        }
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsTextField label={t("Mô tả")} name='description' multiline rows={4}/>
                                </Grid>
                            </Grid>
                        </DialogContent>

                        <DialogActions className='confirmDeletePopupFooter'>
                            <div className='flex flex-space-between flex-middle'>
                                <Button
                                    variant='contained'
                                    className='mr-12 btn btn-secondary d-inline-flex'
                                    color='secondary'
                                    onClick={() => {
                                        handleClose();
                                    }}>
                                    {t("general.button.close")}
                                </Button>
                                <Button
                                    className='mr-0 btn btn-primary d-inline-flex'
                                    variant='contained'
                                    color='primary'
                                    type='submit'
                                    disabled={isSubmitting}>
                                    {t("general.button.save")}
                                </Button>
                            </div>
                        </DialogActions>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}
