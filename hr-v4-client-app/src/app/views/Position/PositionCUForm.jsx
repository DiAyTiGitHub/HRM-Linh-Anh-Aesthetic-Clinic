import React, { useState, useEffect, memo } from "react";
import { Formik, Form } from "formik";
import { Grid, DialogActions, Button, DialogContent, ButtonGroup, Tooltip } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { pagingPositionTitle } from "app/views/PositionTitle/PositionTitleService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import PositionRelationShipRound from "./PositionRelationShipRound";
import GlobitsCheckBox from "app/common/form/GlobitsCheckBox";
import ChooseUsingStaffSection from "../User/UsingAccountStaff/ChooseUsingStaffSection";
import SelectDepartmentComponent from "app/common/SelectComponent/SelectDepartment/SelectDepartmentComponent";
import { convertToConstantFormat } from "app/common/CommonFunctions";
import { Add } from "@material-ui/icons";
import TouchAppIcon from "@material-ui/icons/TouchApp";
import { toast } from "react-toastify";
import LocalConstants, { CodePrefixes } from "../../LocalConstants";
import GlobitsRadioGroup from "../../common/form/GlobitsRadioGroup";

toast.configure ({
  autoClose:2000,
  draggable:false,
  limit:3,
});

function PositionCUForm (props) {
  const {isDisabled, staff, department, positionTitle, handleAfterSubmit, readOnly} = props;
  console.log (department, positionTitle);

  const {positionStore, departmentV2Store} = useStore ();
  const {t} = useTranslation ();
  const {handleClose, savePosition, pagingPosition, selectedPosition, openCreateEditPopup, openViewPopup, autoGenCode} =
      positionStore;

  const validationSchema = Yup.object ({
    // code: Yup.string().required(t("validation.code")).nullable(),
    name:Yup.string ().required (t ("validation.name")).nullable (),
    code:Yup.string ().required (t ("validation.code")).nullable (),
    title:Yup.object ().required (t ("Chưa nhập chức danh")).nullable (),
  });

  async function handleSaveForm (values) {
    let newValues = values;
    if (staff != null) {
      newValues = {... newValues, staff:staff};
    }
    if (values?.mainOrConcurrent === LocalConstants.Position.MAIN.value) {
      newValues = {
        ... newValues,
        isMain:true,
        isConcurrent:false
      }
    }
    if (values?.mainOrConcurrent === LocalConstants.Position.CONCURRENT.value) {
      newValues = {
        ... newValues,
        isMain:false,
        isConcurrent:true
      }
    }

    savePosition (newValues)
        .then ((result) => {
          pagingPosition ();
          return result;
        })
        .then ((data) => {
          if (typeof handleAfterSubmit === "function") {
            handleAfterSubmit (data);
          }
        })
        .catch ((err) => {
        });
  }

  const [initialValues, setInitialValues] = useState (selectedPosition);
  const autoGenCodeFunc = async () => {
    const code = await autoGenCode (CodePrefixes.VI_TRI);
    if (code) {
      // Tạo object mới để tránh thay đổi trực tiếp state
      const updated = {... selectedPosition, ... {code:code}};
      setInitialValues (updated);
    }
  };
  useEffect (() => {
    if (!selectedPosition?.id) {
      autoGenCodeFunc ();
    }
  }, []);
  useEffect (() => {
    if (selectedPosition?.id) {
      const updated = {
        ... selectedPosition,
        mainOrConcurrent:
            selectedPosition?.isMain === true
                ? LocalConstants.Position.MAIN.value
                : selectedPosition?.isConcurrent === true
                    ? LocalConstants.Position.CONCURRENT.value
                    : null,
      };
      setInitialValues (updated);
    } else {
      let dtoInit = {
        ... selectedPosition,
        staff:staff,
        department:department,
        title:positionTitle,
        mainOrConcurrent:null, // hoặc để logic tự gán nếu có flag nào
      };
      setInitialValues (dtoInit);
    }
  }, [selectedPosition, selectedPosition?.id]);


  const [openDepartmentPopup, setOpenDepartmentPopup] = useState (false);

  function handelOpenDepartmentPopup () {
    setOpenDepartmentPopup (true);
  }

  function handleCloseDepartmentPopup () {
    setOpenDepartmentPopup (false);
  }

  const {isAdmin} = useStore ().hrRoleUtilsStore;

  const {handleOpenCreateEdit} = departmentV2Store;

  const handleOpenEditDepartment = (id) => {
    if (id != null && id?.length > 0) {
      handleOpenCreateEdit (id);
    } else {
      toast.warning ("Phòng ban không được để trống");
    }
  };

  return (
      <GlobitsPopupV2
          scroll={"body"}
          size='md'
          open={openCreateEditPopup || openViewPopup}
          noDialogContent
          title={
            openViewPopup
                ? "Xem chi tiết vị trí"
                : (selectedPosition?.id? t ("general.button.edit") : t ("general.button.add")) + " " + "vị trí"
          }
          onClosePopup={handleClose}>
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={initialValues}
            onSubmit={handleSaveForm}>
          {({isSubmitting, values, setFieldValue, initialValues}) => {
            function handleAutoRenderCode (_, title) {
              setFieldValue ("title", title);
              setFieldValue ("name", title?.name);
              const positionCode = convertToConstantFormat (title?.name);
            }

            return (
                <Form autoComplete='off' autocomplete='off'>
                  <div className='dialog-body'>
                    <DialogContent className='p-12'>
                      <FormikFocusError/>

                      <Grid container spacing={2}>
                        <Grid item xs={12} sm={6} md={3}>
                          <SelectDepartmentComponent
                              name={"department"}
                              clearFields={["title"]}
                              disabled={isDisabled || readOnly}
                              disabledTextFieldOnly={true}
                              readOnly={readOnly || department}
                          />
                        </Grid>

                        <Grid item xs={12} sm={6} md={3} container spacing={2}>
                          <Grid item xs={9}>
                            <GlobitsPagingAutocompleteV2
                                label='Chức danh'
                                name='title'
                                api={pagingPositionTitle}
                                disabled={!values?.department || isDisabled}
                                searchObject={{
                                  pageIndex:1,
                                  keyword:"",
                                  departmentId:values?.department?.id,
                                }}
                                handleChange={handleAutoRenderCode}
                                getOptionLabel={(option) => {
                                  return option?.rankTitle?.name
                                      ? `${option?.name} - ${option?.rankTitle?.name}`
                                      : option?.name;
                                }}
                                required
                                readOnly={readOnly || positionTitle?.id}
                            />
                          </Grid>
                          <Grid item xs={3} className={"flex align-end"}>
                            <Tooltip placement='top' title='Thêm chức danh thuộc phòng ban'>
                              <Button
                                  type={"button"}
                                  fullWidth
                                  variant='contained'
                                  className='btn bgc-lighter-dark-blue text-white d-inline-flex my-2'
                                  style={{marginTop:"25px"}}
                                  onClick={() => {
                                    handleOpenEditDepartment (values?.department?.id);
                                  }}
                                  disabled={readOnly}>
                                <TouchAppIcon className='text-white'/>
                              </Button>
                            </Tooltip>
                          </Grid>
                        </Grid>
                        {staff == null? (
                            <Grid item xs={12} sm={6} md={3}>
                              <ChooseUsingStaffSection
                                  label={"Nhân viên"}
                                  disabled={isDisabled}
                                  disabledTextFieldOnly={true}
                                  readOnly={readOnly}
                              />
                            </Grid>
                        ) : (
                            <Grid item xs={12} sm={6} md={3}>
                              <GlobitsTextField
                                  label={"Nhân viên"}
                                  name='staff'
                                  // disabled={true}
                                  value={staff?.displayName + " - " + staff?.staffCode}
                                  readOnly={true}
                              />
                            </Grid>
                        )}

                        <Grid item xs={12} sm={6} md={3}>
                          <GlobitsTextField
                              validate
                              label='Tên vị trí'
                              name='name'
                              disabled={isDisabled}
                              readOnly={readOnly}
                          />
                        </Grid>

                        <Grid item xs={12} sm={6} md={3}>
                          <GlobitsTextField
                              validate
                              label='Mã vị trí'
                              disabled={isDisabled}
                              name='code'
                              readOnly={readOnly}
                          />
                        </Grid>
                        <Grid item xs={12} sm={6} md={4} className={"flex align-center"}>
                          <GlobitsRadioGroup
                              name="mainOrConcurrent"
                              keyValue="value"
                              options={LocalConstants.Position.getListData ()}
                          />
                        </Grid>
                        <Grid item xs={12} sm={6} md={3}>
                          <GlobitsCheckBox
                              label='Là vị tuyển lọc'
                              disabled={isDisabled}
                              name='isTemporary'
                              readOnly={readOnly}
                          />
                        </Grid>

                        <Grid item xs={12}>
                          <GlobitsTextField
                              label='Mô tả'
                              name='description'
                              disabled={isDisabled}
                              multiline
                              rows={3}
                              readOnly={readOnly}
                          />
                        </Grid>

                        <Grid item xs={12}>
                          <PositionRelationShipRound disabled={isDisabled} readOnly={readOnly}/>
                        </Grid>
                      </Grid>
                    </DialogContent>
                  </div>

                  <div className='dialog-footer dialog-footer-v2 py-8'>
                    <DialogActions className='p-0'>
                      <div className='flex flex-space-between flex-middle'>
                        <Button
                            startIcon={<BlockIcon/>}
                            variant='contained'
                            className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                            color='secondary'
                            onClick={handleClose}
                            disabled={isSubmitting || isDisabled}>
                          {t ("general.button.cancel")}
                        </Button>
                        {isAdmin && !readOnly && (
                            <Button
                                startIcon={<SaveIcon/>}
                                className='mr-0 btn btn-primary d-inline-flex'
                                variant='contained'
                                color='primary'
                                type='submit'
                                disabled={isSubmitting || isDisabled}>
                              {t ("general.button.save")}
                            </Button>
                        )}
                      </div>
                    </DialogActions>
                  </div>
                </Form>
            );
          }}
        </Formik>
      </GlobitsPopupV2>
  );
}

export default memo (observer (PositionCUForm));
