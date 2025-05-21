import React , { useEffect , useState } from "react";
import { Form , Formik } from "formik";
import { Button , DialogActions , DialogContent , Grid , makeStyles , } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import * as Yup from "yup";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "../../../common/GlobitsPopupV2";

const useStyles = makeStyles((theme) => ({
    gridContainerForm:{
        maxHeight:"68vh" ,
        overflowY:"auto" ,
        marginBottom:10 ,
    } ,
    textField:{
        width:"100%" ,
        margin:"10px 0px !important" ,
    } ,
}));
export default observer(function SalaryIncrementForm({readOnly}) {
    const classes = useStyles();
    const {salaryIncrementStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose ,
        saveSalaryIncrement ,
        selectedSalaryIncrement ,
        shouldOpenEditorDialog ,
        openViewPopup
    } = salaryIncrementStore;

    const [salaryIncrement , setSalaryIncrement] = useState({
        id:"" ,
        code:"" ,
        name:"" ,
    });

    const validationSchema = Yup.object({
        code:Yup.string().required(t("validation.code")) ,
        name:Yup.string().required(t("validation.name")) ,
    });

    useEffect(() => {
        if (selectedSalaryIncrement) setSalaryIncrement(selectedSalaryIncrement);
    } , [selectedSalaryIncrement]);
    const {isManager , isAdmin} = useStore().hrRoleUtilsStore

    return (
        <GlobitsPopupV2
            noDialogContent
            size={"sm"}
            open={shouldOpenEditorDialog || openViewPopup}
            title={openViewPopup ? (t("general.button.view") + " " + t("salaryIncrement.title")) : (selectedSalaryIncrement?.id ? t("general.button.edit") : t("general.button.add")) + " " + t("salaryIncrement.title")}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={salaryIncrement}
                onSubmit={(values) => saveSalaryIncrement(values)}
            >
                {({isSubmitting}) => (
                    <Form autoComplete='off'>
                        <DialogContent className='dialog-body p-12'>
                            <Grid container spacing={2}>
                                <Grid item md={12} sm={12} xs={12}>
                                    <GlobitsTextField
                                        validate
                                        label={t("salaryItem.code")}
                                        name="code"
                                        readOnly={readOnly}
                                    />
                                </Grid>
                                <Grid item md={12} sm={12} xs={12}>
                                    <GlobitsTextField
                                        validate
                                        label={t("salaryItem.name")}
                                        name="name"
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
