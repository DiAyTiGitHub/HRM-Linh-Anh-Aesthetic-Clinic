import React, { memo, useEffect, useState } from "react";
import { Button, DialogActions, DialogContent, Grid, Tooltip, } from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingCertificates } from "app/views/Certificate/CertificateService";
import SaveIcon from '@material-ui/icons/Save';
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { Form, Formik } from "formik";
import { useStore } from "../../../stores";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";
import BlockIcon from "@material-ui/icons/Block";
import { observer } from "mobx-react";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import VisibilityIcon from "@material-ui/icons/Visibility";
import GetAppIcon from "@material-ui/icons/GetApp";
import DeleteIcon from "@material-ui/icons/Delete";
import SelectFile from "../../StaffDocumentItem/SelectFile";
import { useParams } from "react-router-dom";
import GlobitsPagingAutocompleteV2 from "../../../common/form/GlobitsPagingAutocompleteV2";
import { pagingStaff } from "../StaffService";

function PersonCertificateForm(props) {
  const { personCertificateStore } = useStore();
  const { t } = useTranslation();
  const { id } = useParams();
  const {
    handleClose,
    saveOrUpdate,
    selectedPersonCertificate,
    initialPersonCertificate,
    shouldOpenEditorDialog
  } = personCertificateStore;

  const validationSchema = Yup.object({
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
  const [personCertificate, setPersonCertificate] = useState(initialPersonCertificate);

  useEffect(() => {
    if (selectedPersonCertificate) setPersonCertificate(selectedPersonCertificate);
    else setPersonCertificate(initialPersonCertificate);
    if (id) {
      setPersonCertificate(prev => ({
        ...prev,
        person: { id: id }
      }));
    }

  }, [selectedPersonCertificate?.id]);

  async function handleFormSubmit(values) {
    await saveOrUpdate(values);
  }

  return (
    <GlobitsPopupV2
      open={shouldOpenEditorDialog}
      size='xs'
      noDialogContent
      title={(personCertificate?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("chứng chỉ, chứng nhận")}
      onClosePopup={handleClose}
    >

      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={personCertificate}
        onSubmit={(values) => handleFormSubmit(values)}
      >
        {({ isSubmitting, setFieldValue, values }) => (
          <Form autoComplete='off'>
            <div className='dialog-body'>
              <DialogContent className='o-hidden p-12'>
                <Grid container spacing={2}>
                  {/* <Grid item xs={12}>
                    <GlobitsPagingAutocompleteV2
                      name='staff'
                      label={t("Nhân viên")}
                      api={pagingStaff}
                      getOptionLabel={(option) =>
                        option?.displayName && option?.staffCode
                          ? `${option.displayName} - ${option.staffCode}`
                          : option?.displayName || option?.staffCode || ""
                      }
                      readOnly={!canChangeFilter}
                    />
                  </Grid> */}

                  <Grid item xs={12}>
                    <GlobitsPagingAutocompleteV2
                      label={t("certificate.title")}
                      requiredLabel
                      name="certificate"
                      api={pagingCertificates}

                      required
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <GlobitsPagingAutocompleteV2
                      label={t("certificate.title")}
                      requiredLabel
                      name="certificate"
                      api={pagingCertificates}
                      required
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <GlobitsTextField
                      name="name"
                      label={t("certificate.name")}
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <GlobitsTextField
                      name="level"
                      label={t("certificate.level")}
                    // required
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <GlobitsDateTimePicker
                      name="issueDate"
                      label={t("certificate.issueDate")}
                      required
                    />
                  </Grid>

                  <Grid item xs={12}>
                    <SelectFile
                      name={"certificateFile"}
                      fileProp={values?.certificateFile}
                      showPreview={true}
                      showDowload={true}
                      showDelete={true}
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

            <div className='dialog-footer'>
              <DialogActions className='p-4'>
                <div className='flex flex-space-between flex-middle'>
                  <Button startIcon={<BlockIcon />} variant='contained'
                    className='mr-12 btn btn-secondary d-inline-flex' color='secondary'
                    onClick={() => handleClose()}>
                    {t("general.button.cancel")}
                  </Button>
                  <Button startIcon={<SaveIcon />} className='mr-0 btn btn-primary d-inline-flex'
                    variant='contained' color='primary' type='submit' disabled={isSubmitting}>
                    {t("general.button.save")}
                  </Button>
                </div>
              </DialogActions>
            </div>
          </Form>
        )}
      </Formik>
    </GlobitsPopupV2>
  )
}

export default memo(observer(PersonCertificateForm));