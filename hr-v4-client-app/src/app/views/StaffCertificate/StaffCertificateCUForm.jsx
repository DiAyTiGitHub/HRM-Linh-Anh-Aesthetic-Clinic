import React, { memo, useEffect, useState } from "react";
import { Form, Formik } from "formik";
import { Button, DialogActions, DialogContent, Grid, Tooltip } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import SelectFile from "../StaffDocumentItem/SelectFile";
import { pagingCertificates } from "../Certificate/CertificateService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import VisibilityIcon from "@material-ui/icons/Visibility";
import GetAppIcon from "@material-ui/icons/GetApp";
import DeleteIcon from "@material-ui/icons/Delete";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";

function StaffCertificateCUForm(props) {
    const { t } = useTranslation();

    const {
        readOnly = false

    } = props;

    const {
        staffCertificateStore

    } = useStore();

    const {
        handleClose,
        saveOrUpdatePersonCertificate,
        pagingPersonCertificate,
        selectedStaffCetificate,
        openCreateEditPopup,
        openViewPopup,
    } = staffCertificateStore;

    const validationSchema = Yup.object({
        staff: Yup.object().required(t("validation.required")).nullable(),
        certificate: Yup.object().required(t("validation.required")).nullable(),
        // name:Yup.string ().required (t ("validation.required")).nullable (),
        // level: Yup.string().required(t("validation.required")).nullable(),
        issueDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .typeError("Ngày không đúng định dạng")
            .required(t("validation.required"))
            .nullable(),
    });

    async function handleSaveForm(values) {
        await saveOrUpdatePersonCertificate(values);
        await pagingPersonCertificate();
    }

    const [initialValues, setInitialValues] = useState(selectedStaffCetificate);

    useEffect(function () {
        setInitialValues(selectedStaffCetificate);
    }, [selectedStaffCetificate, selectedStaffCetificate?.id]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="sm"
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={"Thông tin chứng chỉ nhân viên"}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}
            >
                {({ isSubmitting, values, setFieldValue, initialValues }) => {

                    return (
                        <Form autoComplete="off" autocomplete="off">
                            <div className="dialog-body">
                                <DialogContent className="p-12">
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            <p className="m-0 p-0 borderThrough2">Thông tin chung</p>
                                        </Grid>

                                        <Grid item xs={12}>
                                            <GlobitsPagingAutocompleteV2
                                                name='staff'
                                                label={t("Nhân viên")}
                                                required
                                                api={pagingStaff}
                                                getOptionLabel={(option) =>
                                                    option?.displayName && option?.staffCode
                                                        ? `${option.displayName} - ${option.staffCode}`
                                                        : option?.displayName || option?.staffCode || ""
                                                }
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsPagingAutocompleteV2
                                                label={t("certificate.title")}
                                                requiredLabel
                                                name="certificate"
                                                api={pagingCertificates}
                                                readOnly={readOnly}
                                                required
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                name="name"
                                                label={t("certificate.name")}
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                name="level"
                                                label={t("certificate.level")}
                                                readOnly={readOnly}
                                            // required
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                name="issueDate"
                                                label={t("certificate.issueDate")}
                                                readOnly={readOnly}
                                                required
                                            />
                                        </Grid>

                                        <Grid item xs={12}>
                                            <p className="m-0 p-0 borderThrough2">Tài liệu đính kèm</p>
                                        </Grid>

                                        <Grid item xs={12}>
                                            <SelectFile
                                                readOnly={readOnly}
                                                name={"certificateFile"}
                                                fileProp={values?.certificateFile}
                                                showPreview={true}
                                                showDowload={true}
                                                showDelete={!readOnly}
                                                showName={false}
                                                acceptedFiles={["image/jpeg", "image/png", "application/pdf"]} // Chấp nhận cả ảnh và PDF
                                                maxFileSize={5242880}
                                                uploadButton={
                                                    <Tooltip
                                                        placement='top'
                                                        title='Chọn chứng chỉ'
                                                        style={{
                                                            border: "1px solid rgba(0, 0, 0, 0.23)",
                                                            marginRight: "4px",
                                                        }}>
                                                        <Button startIcon={<CloudUploadIcon fontSize='small' />}>
                                                            Chọn chứng chỉ
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
                                                            <VisibilityIcon className='mr-6' />
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
                                                            <GetAppIcon className='mr-6' />
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
                                                            <DeleteIcon className='mr-6' />
                                                            Xóa
                                                        </Button>
                                                    </Tooltip>
                                                }
                                            />
                                        </Grid>

                                    </Grid>

                                </DialogContent>
                            </div>

                            <div className="dialog-footer dialog-footer-v2 py-8">
                                <DialogActions className="p-0">
                                    <div className="flex flex-space-between flex-middle">
                                        <Button
                                            startIcon={<BlockIcon />}
                                            variant="contained"
                                            className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                            color="secondary"
                                            onClick={handleClose}
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>

                                        {(!readOnly && (
                                            <Button
                                                startIcon={<SaveIcon />}
                                                className="mr-0 btn btn-primary d-inline-flex"
                                                variant="contained"
                                                color="primary"
                                                type="submit"
                                                disabled={isSubmitting}
                                            >
                                                {t("general.button.save")}
                                            </Button>
                                        ))}
                                    </div>
                                </DialogActions>
                            </div>
                        </Form>
                    );
                }
                }
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(StaffCertificateCUForm));