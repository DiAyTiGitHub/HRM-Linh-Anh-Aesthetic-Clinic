import { Button, DialogActions, DialogContent, Grid, Tooltip } from "@material-ui/core";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { toast } from "react-toastify";
import * as Yup from "yup";
import { useStore } from "../../stores";
import { pagingHrDocumentItem } from "../HrDocumentItem/HrDocumentItemService";
import { uploadFile } from "../HumanResourcesInformation/StaffLabourAgreementAttachmentService";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import SelectFile from "./SelectFile";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import VisibilityIcon from "@material-ui/icons/Visibility";
import GetAppIcon from "@material-ui/icons/GetApp";
import DeleteIcon from "@material-ui/icons/Delete";

function StaffDocumentItemCUForm (props) {
  const {t} = useTranslation ();
  const {staffId = null, onSaved} = props;
  const {staffDocumentItemStore} = useStore ();

  const {
    handleClose,
    saveStaffDocumentItem,
    pagingStaffDocumentItemByStaff,
    pagingStaffDocumentItem,
    selectedStaffDocumentItem,
    openCreateEditPopup,
    isAdmin,
  } = staffDocumentItemStore;

  const validationSchema = Yup.object ({
    staff:Yup.object ().required (t ("validation.required")).nullable (),
    documentItem:Yup.object ().required (t ("validation.required")).nullable (),
  });

  const [openChooseFiles, setOpenChooseFiles] = useState (false);

  function handleOpenChooseFilesPopup () {
    setOpenChooseFiles (true);
  }

  function handleCloseChooseFilesPopup () {
    setOpenChooseFiles (false);
  }

  async function handleUploadFiles (data) {
    const uploadedFiles = [];
    if (data && data?.length > 0) {
      for (let i = 0; i < data?.length; i++) {
        const file = data[i];
        // console.log("file", file);
        try {
          const response = await uploadFile (file);
          // console.log("response", response);
          uploadedFiles.push (response?.data);
        } catch (err) {
          console.error (err);
          toast.error (
              "Không thể tải file " +
              file?.name +
              ", vui lòng kiểm tra lại kích cỡ của file (không được quá lớn)"
          );
        }
      }
    }

    // console.log("uploadedFiles: ", uploadedFiles);
    // const existedFiles = values?.files || [];
    // const newFiles = [...existedFiles, ...uploadedFiles];
    // //set new files to display
    // setFieldValue("files", newFiles);
    handleCloseChooseFilesPopup ();
  }

  async function handleSaveForm (values) {
    try {
      const response = await saveStaffDocumentItem (values);

      if (onSaved) {
        onSaved ();
      } else {
        if (staffId) {
          await pagingStaffDocumentItemByStaff ({staffId:staffId});
        } else {
          await pagingStaffDocumentItem ()
        }
      }
    } catch (error) {
      console.error (error);
    }
  }

  const [initialValues, setInitialValues] = useState (selectedStaffDocumentItem);

  useEffect (
      function () {
        setInitialValues ({
          ... selectedStaffDocumentItem,
        });
      },
      [selectedStaffDocumentItem, selectedStaffDocumentItem?.id]
  );

  return (
      <GlobitsPopupV2
          size='md'
          scroll={"body"}
          open={openCreateEditPopup}
          noDialogContent
          title={
              (selectedStaffDocumentItem?.id? t ("general.button.edit") : t ("general.button.add")) +
              " " +
              "tài liệu nhân viên đã nộp"
          }
          onClosePopup={handleClose}>
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={initialValues}
            onSubmit={handleSaveForm}>
          {({isSubmitting, values, setFieldValue, initialValues}) => {
            return (
                <Form autoComplete='off'>
                  <DialogContent className='dialog-body p-12'>
                    <Grid container spacing={2}>
                      {!staffId && (
                          <Grid item xs={12} md={6}>
                            <ChooseUsingStaffSection
                                required
                                label={"Nhân viên nộp tài liệu"}
                                placeholder={""}
                            />
                          </Grid>
                      )}

                      <Grid item xs={12} md={6}>
                        <GlobitsPagingAutocompleteV2
                            name='documentItem'
                            label={"Tài liệu"}
                            api={pagingHrDocumentItem}
                            getOptionLabel={(option) =>
                                [option?.name, option?.code].filter (Boolean).join (' - ') || ''
                            }
                            searchObject={{
                              documentTemplateId:values?.staff?.documentTemplate?.id,
                            }}
                            disabled={!values?.staff?.documentTemplate?.id}
                            required
                        />
                      </Grid>
                      <Grid item xs={12} md={6}>
                        <GlobitsDateTimePicker label='Ngày nộp' name='submissionDate'/>
                      </Grid>

                      <Grid item xs={12} md={6}>
                        <GlobitsCheckBox label={"Đã nộp"} name='isSubmitted'/>
                      </Grid>
                      <Grid item xs={12}>
                        <SelectFile
                            name={"file"}
                            fileProp={values?.file}
                            showPreview={true}
                            showDowload={true}
                            showDelete={true}
                            showName={false}
                            acceptedFiles={["image/jpeg", "image/png", "application/pdf"]} // Chấp nhận cả ảnh và PDF
                            maxFileSize={5242880}
                            uploadButton={
                              <Tooltip
                                  placement='top'
                                  title='Chọn tài liệu cần nôp'
                                  style={{
                                    border:"1px solid rgba(0, 0, 0, 0.23)",
                                    marginRight:"4px",
                                  }}>
                                <Button startIcon={<CloudUploadIcon fontSize='small'/>}>
                                  Chọn tài liệu cần nôp
                                </Button>
                              </Tooltip>
                            }
                            previewButton={
                              <Tooltip placement='top' title='Xem trước'>
                                <Button
                                    style={{
                                      border:"1px solid rgba(0, 0, 0, 0.23)",
                                      marginRight:"4px",
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
                                      border:"1px solid rgba(0, 0, 0, 0.23)",
                                      marginRight:"4px",
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
                                      border:"1px solid rgba(0, 0, 0, 0.23)",
                                      marginRight:"4px",
                                    }}>
                                  <DeleteIcon className='mr-6'/>
                                  Xóa
                                </Button>
                              </Tooltip>
                            }
                        />
                      </Grid>
                    </Grid>
                  </DialogContent>

                  <DialogActions className='dialog-footer px-12'>
                    <div className='flex flex-space-between flex-middle'>
                      <Button
                          variant='contained'
                          className='mr-12 btn btn-secondary d-inline-flex'
                          color='secondary'
                          disabled={isSubmitting}
                          onClick={handleClose}>
                        {t ("general.button.close")}
                      </Button>
                      <Button
                          className='mr-0 btn btn-primary d-inline-flex'
                          variant='contained'
                          color='primary'
                          type='submit'
                          disabled={isSubmitting}>
                        {t ("general.button.save")}
                      </Button>
                    </div>
                  </DialogActions>
                </Form>
            );
          }}
        </Formik>
      </GlobitsPopupV2>
  );
}

export default memo (observer (StaffDocumentItemCUForm));
