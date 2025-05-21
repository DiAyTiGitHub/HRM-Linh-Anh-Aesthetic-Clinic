import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import SelectDepartmentComponent from "app/common/SelectComponent/SelectDepartment/SelectDepartmentComponent";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import ResourcePlanItemSection from "./components/ResourcePlanItemSection";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import HrResourcePlanApproveInfo from "./HrResourcePlanApproveInfo";
import { CodePrefixes } from "../../LocalConstants";
import GlobitsCheckBox from "../../common/form/GlobitsCheckBox";

function HrResourcePlanCUForm (props) {
  const {
    hrResourcePlanStore,
    hrRoleUtilsStore
  } = useStore ();

  const {t} = useTranslation ();

  const {readOnly} = props;

  const {
    handleClose,
    saveHrResourcePlan,
    pagingHrResourcePlan,
    selectedHrResourcePlan,
    openCreateEditPopup,
    openViewPopup,
    autoGenCode
  } = hrResourcePlanStore;

  const validationSchema = Yup.object ({
    code:Yup.string ().required (t ("validation.code")).nullable (),
    name:Yup.string ().required (t ("validation.name")).nullable (),
    department:Yup.object ().required (t ("validation.required")).nullable (),
    planDate:Yup.date ()
        .transform ((castValue, originalValue) => (originalValue? new Date (originalValue) : castValue))
        // .required(t("validation.required"))
        .typeError ("Ngày định biên không đúng định dạng")
        .required ("Ngày định biên không được để trống")
        .nullable (),
  });

  async function handleSaveForm (values) {
    await saveHrResourcePlan (values);
    await pagingHrResourcePlan ();
  }

  const [initialValues, setInitialValues] = useState (selectedHrResourcePlan);
  const autoGenCodeFunc = async () => {
    const code = await autoGenCode (CodePrefixes.YEU_CAU_DINH_BIEN);
    if (code) {
      // Tạo object mới để tránh thay đổi trực tiếp state
      const updated = {... selectedHrResourcePlan, code};
      setInitialValues (updated);
    }
  };
  useEffect (
      function () {
        if (selectedHrResourcePlan?.id) {
          setInitialValues ({
            ... selectedHrResourcePlan,
          });
        } else {
          setInitialValues ({
            ... selectedHrResourcePlan,
          });
          autoGenCodeFunc ()
        }
      },
      [selectedHrResourcePlan, selectedHrResourcePlan?.id]
  );
  const {isAdmin, isPositionManager} = hrRoleUtilsStore

  return (
      <GlobitsPopupV2
          size='lg'
          scroll={"body"}
          open={openCreateEditPopup || openViewPopup}
          // open={true}
          noDialogContent
          title={openViewPopup? (t ("general.button.view") + " " + t ("Yêu cầu định biên")) : ((selectedHrResourcePlan?.id? t ("general.button.edit") : t ("general.button.add")) +
              " " +
              t ("Yêu cầu định biên"))}
          onClosePopup={handleClose}
      >
        <Formik
            validationSchema={validationSchema}
            enableReinitialize
            initialValues={initialValues}
            onSubmit={handleSaveForm}
        >
          {({isSubmitting, values, setFieldValue, initialValues}) => {
            return (
                <Form autoComplete='off'>
                  <DialogContent className='dialog-body p-12'>
                    <Grid container spacing={2}>
                      <Grid item xs={12}>
                        <p className='m-0 p-0 borderThrough2'>Thông tin yêu cầu</p>
                      </Grid>

                      <Grid item md={4} xs={12}>
                        <GlobitsTextField
                            required
                            label='Mã định biên'
                            name='code'
                            readOnly={readOnly}
                        />
                      </Grid>

                      <Grid item md={4} xs={12}>
                        <GlobitsTextField
                            required
                            label='Tên định biên'
                            name='name'
                            readOnly={readOnly}
                        />
                      </Grid>

                      <Grid item md={4} xs={12}>
                        <GlobitsDateTimePicker
                            name='planDate'
                            required
                            label='Ngày định biên'
                            readOnly={readOnly}/>
                      </Grid>
                      <Grid item md={4} xs={12}>
                        <SelectDepartmentComponent required={true} readOnly={readOnly}/>
                      </Grid>
                      <Grid item xs={12}>
                        <GlobitsTextField
                            isTextArea={true}
                            multiline
                            minRows={2}
                            label={t ("Mô tả")}
                            name='description'
                            readOnly={readOnly}/>
                      </Grid>

                      <Grid item xs={12}>
                        <p className='m-0 p-0 borderThrough2'>Các vị trí định biên trong phòng ban</p>
                      </Grid>

                      <Grid item xs={12}>
                        <ResourcePlanItemSection readOnly={readOnly}/>
                      </Grid>

                      {values?.requester?.id && (
                          <Grid item xs={12}>
                            <HrResourcePlanApproveInfo
                                readOnly={readOnly}
                            />
                          </Grid>
                      )}


                    </Grid>
                  </DialogContent>

                  <DialogActions className='dialog-footer px-12'>
                    <div className='flex flex-space-between flex-middle'>
                      <Button startIcon={<BlockIcon/>} variant='contained'
                              className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                              color='secondary'
                              onClick={() => handleClose ()}>
                        {t ("general.button.cancel")}
                      </Button>
                      {((isAdmin || isPositionManager) && !readOnly) && (
                          <Button startIcon={<SaveIcon/>}
                                  className='mr-0 btn btn-primary d-inline-flex'
                                  variant='contained' color='primary' type='submit'
                                  disabled={isSubmitting}>
                            {t ("general.button.save")}
                          </Button>
                      )}
                    </div>
                  </DialogActions>
                </Form>
            );
          }}
        </Formik>
      </GlobitsPopupV2>
  );
}

export default memo (observer (HrResourcePlanCUForm));
