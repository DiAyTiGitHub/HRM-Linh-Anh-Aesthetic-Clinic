import React , { memo , useEffect , useState } from "react";
import { Form , Formik } from "formik";
import { Button , DialogActions , DialogContent , Grid , makeStyles } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsVNDCurrencyInput from "app/common/form/GlobitsVNDCurrencyInput";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";

const useStyles = makeStyles((theme) => ({
    root:{
        "& .MuiAccordion-rounded":{
            borderRadius:"5px" ,
        } ,

        "& .MuiPaper-root":{
            borderRadius:"5px" ,
        } ,

        "& .MuiAccordionSummary-root":{
            borderRadius:"5px" ,
            // backgroundColor: "#EBF3F9",
            color:"#5899d1 " ,
            fontWeight:"400" ,

            "& .MuiTypography-root":{
                fontSize:"1rem" ,
            } ,
        } ,

        "& .Mui-expanded":{
            "& .MuiAccordionSummary-root":{
                backgroundColor:"#EBF3F9" ,
                color:"#5899d1 " ,
                // borderLeft: "3px solid rgba(88, 153, 202, 0.84) !important",
                fontWeight:"700" ,
                maxHeight:"50px !important" ,
                minHeight:"50px !important" ,
            } ,
            "& .MuiTypography-root":{
                fontWeight:700 ,
            } ,
        } ,

        "& .MuiButton-root":{
            borderRadius:"0.125rem !important" ,
        } ,
    } ,
}));

function SalaryAreaCUForm({readOnly}) {
    const {salaryAreaStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose ,
        saveSalaryArea ,
        pagingSalaryArea ,
        selectedSalaryArea ,
        openCreateEditPopup ,
        openViewPopup
    } = salaryAreaStore;

    const validationSchema = Yup.object({
        code:Yup.string().required(t("validation.code")).nullable() ,
        name:Yup.string().required(t("validation.name")).nullable() ,
    });

    async function handleSaveForm(values) {
        await saveSalaryArea(values);
        await pagingSalaryArea();
        // handleClose();
    }

    const [initialValues , setInitialValues] = useState(selectedSalaryArea);

    useEffect(function () {
        setInitialValues(selectedSalaryArea);
    } , [selectedSalaryArea , selectedSalaryArea?.id])
    const {isManager , isAdmin} = hrRoleUtilsStore

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="sm"
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={openViewPopup ? (t("Xem chi tiết") + ' ' + t("vùng lương")) : (selectedSalaryArea?.id ? t("general.button.edit") : t("general.button.add") + ' ' + t("vùng lương"))}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}
            >
                {({isSubmitting , values , setFieldValue , initialValues}) => {

                    return (
                        <Form autoComplete='off'>
                            <DialogContent className='dialog-body p-12'>
                                <Grid container spacing={2}>
                                    <FormikFocusError/>
                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label="Mã vùng lương"
                                            name="code"
                                            required
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField
                                            validate
                                            label="Tên vùng lương"
                                            name="name"
                                            required
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsVNDCurrencyInput
                                            label="Mức lương tối thiểu tháng (Đơn vị: đồng/tháng)"
                                            name="minMonth"
                                            readOnly={readOnly}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsVNDCurrencyInput
                                            label="Mức lương tối thiểu giờ (Đơn vị: đồng/giờ)"
                                            name="minHour"
                                            readOnly={readOnly}
                                        />
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
                                                variant='contained' color='primary' type='submit'
                                                disabled={isSubmitting}>
                                            {t("general.button.save")}
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

export default memo(observer(SalaryAreaCUForm));