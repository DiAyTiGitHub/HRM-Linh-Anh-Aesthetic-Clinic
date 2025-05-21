import { Button , DialogActions , DialogContent , Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import { observer } from "mobx-react";
import React , { useEffect , useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form , Formik } from "formik";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsCheckBox from "../../common/form/GlobitsCheckBox";

export default observer(function LeaveTypeForm({handleAfterSubmit , open , readOnly}) {
    const {leaveTypeStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();
    const {handleClose , saveOrUpdate , selectedLeaveType , defaultLeaveType , openViewPopup} = leaveTypeStore;

    const [leaveType , setLeaveType] = useState(defaultLeaveType);

    const validationSchema = Yup.object({
        name:Yup.string().required(t("validation.required")) ,
        code:Yup.string().required(t("validation.required")) ,

    });

    useEffect(() => {
        if (selectedLeaveType) setLeaveType(selectedLeaveType);
        else setLeaveType(defaultLeaveType);
    } , [selectedLeaveType]);

    async function handleFormSubmit(values) {
        await saveOrUpdate(values);
    }

    const convertNameToCode = (name) => {
        return name
            .normalize("NFD") // Loại bỏ dấu tiếng Việt
            .replace(/[\u0300-\u036f]/g , "")
            .toUpperCase() // Chuyển thành chữ in hoa
            .replace(/\s+/g , "_"); // Thay dấu cách bằng "_"
    };
    const {isManager , isAdmin} = hrRoleUtilsStore

    return (
        <GlobitsPopupV2
            open={open || openViewPopup}
            size='xs'
            noDialogContent
            title={openViewPopup ? ("Xem chi tiết " + t("navigation.timeSheet.leaveType")) : (selectedLeaveType?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("navigation.timeSheet.leaveType")}
            onClosePopup={handleClose}>
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={leaveType}
                onSubmit={(values) => handleFormSubmit(values)}>
                {({isSubmitting , setFieldValue , values}) => (
                    <Form autoComplete='off'>
                        <DialogContent className='dialog-body p-12'>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <GlobitsTextField
                                        label={t("leaveType.name")}
                                        name='name'
                                        onChange={(e) => {
                                            let name = e.target.value;
                                            setFieldValue("name" , name);
                                            setFieldValue("code" , convertNameToCode(name));
                                        }}
                                        readOnly={readOnly}
                                        required/>
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsTextField
                                        label={t("leaveType.code")}
                                        name='code'
                                        disabled
                                        readOnly={readOnly}
                                        required/>
                                </Grid>
                                <Grid item xs={12} className={"flex align-end"}>
                                    <GlobitsCheckBox
                                        label={"Ngày nghỉ có lương"} name='isPaid'
                                        checked={values?.isPaid}
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsTextField
                                        isTextArea={true}
                                        multiline minRows={4}
                                        label={t("leaveType.description")}
                                        readOnly={readOnly}
                                        name='description'/>
                                </Grid>
                            </Grid>
                        </DialogContent>
                        <DialogActions className='dialog-footer px-12'>
                            <div className='flex flex-space-between flex-middle'>
                                <Button startIcon={<BlockIcon/>} variant='contained'
                                        className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                        color='secondary'
                                        onClick={() => handleClose()}>
                                    {t("general.button.cancel")}
                                </Button>
                                {((isManager || isAdmin) && !readOnly) && (
                                    <Button startIcon={<SaveIcon/>} className='mr-0 btn btn-primary d-inline-flex'
                                            variant='contained' color='primary' type='submit' disabled={isSubmitting}>
                                        {t("general.button.save")}
                                    </Button>
                                )}
                            </div>
                        </DialogActions>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
});
