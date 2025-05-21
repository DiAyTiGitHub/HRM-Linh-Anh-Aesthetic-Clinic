import { Button , DialogActions , DialogContent , Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { observer } from "mobx-react";
import { useTranslation } from "react-i18next";
import { Form , Formik } from "formik";
import { useStore } from "app/stores";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import React , { memo , useEffect } from "react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingStaff } from "app/views/HumanResourcesInformation/StaffService";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Staff } from "app/common/Model/Staff";

function StaffSalaryTemplateForm(props) {
    const {t} = useTranslation();
    const {
        handleAfterSubmit ,
        updateListOnClose ,
        open ,
        readOnly
    } = props;

    const {
        hrRoleUtilsStore ,
        popupStaffSalaryTemplateStore ,
        salaryTemplateStore
    } = useStore();

    const {
        handleClose ,
        openEdit ,
        selectedStaffSalaryTemplate ,
        handleSaveStaffSalaryTemplate ,
    } = popupStaffSalaryTemplateStore;

    const {
        selectedSalaryTemplate ,
    } = salaryTemplateStore;

    const {
        isAdmin ,
        isManager
    } = hrRoleUtilsStore;

    async function handleFormSubmit(values) {
        const newValues = {
            ... values ,
            salaryTemplate:selectedSalaryTemplate
        }

        await handleSaveStaffSalaryTemplate(newValues);
    }

    return (<GlobitsPopupV2
        open={openEdit || readOnly}
        size='sm'
        noDialogContent
        title={readOnly ? "Xem chi tiết nhân viên sử dụng mẫu bảng lương" : "Sửa nhân viên sử dụng mẫu bảng lương"}
        onClosePopup={handleClose}>
        <Formik
            enableReinitialize
            initialValues={selectedStaffSalaryTemplate}
            onSubmit={(values) => handleFormSubmit(values)}>
            {({isSubmitting}) => (
                <Form autoComplete='off'>
                    <FormikFocusError/>
                    <div className='dialog-body'>
                        <DialogContent className='o-hidden'>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <GlobitsTextField
                                        value={selectedSalaryTemplate?.name}
                                        label={"Mẫu bảng lương sử dụng"}
                                        name={"salaryTemplateName"}
                                        disabled
                                        readOnly={readOnly}
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsPagingAutocompleteV2
                                        name="staff"
                                        label={"Nhân viên"}
                                        api={pagingStaff}
                                        getOptionLabel={(option) => `${option?.displayName}` + ' - ' + `${option?.staffCode}`}
                                        readOnly={readOnly}
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsDateTimePicker
                                        label={"Thời gian bắt đầu"}
                                        name='fromDate'
                                        required
                                        readOnly={readOnly}
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsDateTimePicker
                                        label={"Thời gian kết thúc"}
                                        name='toDate'
                                        required
                                        readOnly={readOnly}
                                    />
                                </Grid>
                            </Grid>
                        </DialogContent>
                    </div>
                    <div className='dialog-footer'>
                        <DialogActions className='p-0'>
                            <div className='flex flex-space-between flex-middle'>
                                <Button startIcon={<BlockIcon/>} variant='contained'
                                        className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                        color='secondary'
                                        onClick={() => {
                                            handleClose()
                                        }}>
                                    {t("general.button.cancel")}
                                </Button>
                                {((isAdmin || isManager) && !readOnly) && (
                                    <Button startIcon={<SaveIcon/>}
                                            className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained' color='primary' type='submit'
                                            disabled={isSubmitting}>
                                        {t("general.button.save")}
                                    </Button>
                                )}
                            </div>
                        </DialogActions>
                    </div>
                </Form>
            )}
        </Formik>
    </GlobitsPopupV2>);
}

export default memo(observer(StaffSalaryTemplateForm));