import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import ReplayIcon from "@material-ui/icons/Replay";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { pagingAllDepartments } from "../Department/DepartmentService";
import SelectDepartmentPopup from "../Department/SelectParent/SelectDepartmentPopup";
import { pagingDepartmentType } from "../DepartmentType/DepartmentTypeService";
import { pagingPositionTitle } from "../PositionTitle/PositionTitleService";

function DepartmentPopup({ open, formData, handleClose, handleSubmit }) {
  const { t } = useTranslation();

  const validationSchema = Yup.object({
    code: Yup.string().required(t("validation.required")).nullable(),
    name: Yup.string().required(t("validation.required")).nullable(),
    shortName: Yup.string().required(t("validation.required")).nullable(),
  });

  return (
    <GlobitsPopupV2
      scroll={"body"}
      size='lg'
      open={open}
      noDialogContent
      title={"Lựa chọn " + t("navigation.category.staff.departments")}
      onClosePopup={handleClose}
    >
      <Formik
        validationSchema={validationSchema}
        enableReinitialize
        initialValues={{
          ...formData,
          positionTitles: formData?.positionTitles ?? [],
          subDepartments: formData?.subDepartments ?? [],

        }}
        onSubmit={(values) => handleSubmit(values)}>
        {({ isSubmitting, values, setValues }) => {
          return (
            <Form autoComplete='off' autocomplete='off'>
              <div className='dialog-body'>
                <DialogContent className='p-12'>
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={6} md={4} lg={3}>
                      <GlobitsTextField
                        label={
                          <span>
                            {t("department.code")}
                            <span style={{ color: "red" }}> * </span>
                          </span>
                        }
                        name='code'
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4} lg={3}>
                      <GlobitsTextField
                        label={
                          <span>
                            Tên phòng ban
                            <span style={{ color: "red" }}> * </span>
                          </span>
                        }
                        name='name'
                      />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4} lg={3}>
                      <GlobitsTextField
                        label={
                          <span>
                            {t("department.shortName")}
                            <span style={{ color: "red" }}> * </span>
                          </span>
                        }
                        name='shortName'
                      />
                    </Grid>
                    <Grid item xs={12} sm={6} md={4} lg={3}>
                      <Grid container spacing={2}>
                        <Grid item xs={9}>
                          <GlobitsTextField
                            label={t("department.parent")}
                            name='parent.name'
                            placeholder={"Không có đơn vị cha"}
                            disabled
                            value={values?.parent ? values?.parent?.name : "Không có đơn vị cha"}
                          />
                        </Grid>

                        <Grid item xs={3}>
                          <SelectDepartmentPopup />
                        </Grid>
                      </Grid>
                    </Grid>

                    <Grid item xs={12} sm={6} md={4} lg={3}>
                      <GlobitsPagingAutocomplete name='hrDepartmentType' label={t("department.hrdepartmentType")} api={pagingDepartmentType} />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4} lg={3}>
                      <GlobitsPagingAutocomplete label={t("department.positionTitleManager")} name='positionTitleManager' api={pagingPositionTitle} />
                    </Grid>

                    <Grid item xs={12} sm={6} md={4} lg={3}>
                      <GlobitsDateTimePicker label={t("department.foundedDate")} name='foundedDate' />
                    </Grid>

                    <Grid item xs={12} sms={6} md={4} lg={3}>
                      <GlobitsTextField type='number' label={t("department.sortNumber")} name='sortNumber' />
                    </Grid>

                    <Grid item xs={12}>
                      <GlobitsTextField label={t("department.description")} name='description' multiline rows={3} />
                    </Grid>

                    <Grid item xs={12}>
                      <GlobitsPagingAutocompleteV2
                        label={t("department.positionTitles")}
                        name='positionTitles'
                        multiple
                        searchObject={{
                          pageIndex: 1,
                          pageSize: 10,
                        }}
                        api={pagingPositionTitle}
                      />
                    </Grid>

                    <Grid item xs={12}>
                      <GlobitsPagingAutocompleteV2
                        label={t("department.subDepartments")}
                        name='subDepartments'
                        multiple
                        searchObject={{
                          pageIndex: 1,
                          pageSize: 10,
                        }}
                        api={pagingAllDepartments}
                        getOptionDisabled={function (option) {
                          return values?.id == option?.id || values?.parent?.id == option?.id;
                        }}
                      />
                    </Grid>

                    {/* <Grid item xs={12}>
                      <GlobitsPagingAutocomplete name='organization' label={t("department.organization")} api={pagingAllOrg} />
                    </Grid> */}
                  </Grid>
                </DialogContent>
              </div>

              <div className='dialog-footer'>
                <DialogActions className='p-0'>
                  <div className='flex flex-space-between flex-middle'>
                    <Button startIcon={<BlockIcon />} variant='contained' className='mr-12 btn btn-secondary d-inline-flex' disabled={isSubmitting} color='secondary' onClick={() => handleClose()}>
                      {t("general.button.cancel")}
                    </Button>

                    <Button
                      startIcon={<ReplayIcon />}
                      variant='contained'
                      className='mr-12 btn btn-info d-inline-flex'
                      disabled={isSubmitting}
                      color='primary'
                      // onClick={() => setValues(JSON.parse(JSON.stringify(department)))}
                      type='reset'>
                      Hoàn tác
                    </Button>

                    <Button startIcon={<SaveIcon />} className='mr-0 btn btn-success d-inline-flex' variant='contained' type='submit' disabled={isSubmitting}>
                      {t("general.button.save")}
                    </Button>
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

export default memo(observer(DepartmentPopup));
