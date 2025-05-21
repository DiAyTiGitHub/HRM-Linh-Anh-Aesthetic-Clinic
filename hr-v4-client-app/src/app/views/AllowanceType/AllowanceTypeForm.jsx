import React , { memo , useEffect , useState } from "react";
import { Form , Formik } from "formik";
import { Button , DialogActions , DialogContent , Grid } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsTextField from "../../common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

function AllowanceTypeForm(props) {
    const {allowanceTypeStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose ,
        createAllowanceType ,
        editAllowanceType ,
        selectedAllowanceType ,
        openViewPopup
    } = allowanceTypeStore;

    const {readOnly , open} = props;

    const [allowanceType , setAllowanceType] = useState({
        id:null ,
        name:null ,
        code:null ,
        otherName:null ,
        defaultValue:null ,
        defaultValueUnit:null ,
        insuranceValue:null ,
        insuranceValueUnit:null ,
        taxReductionValue:null ,
        taxReductionValueUnit:null ,
    });

    const validationSchema = Yup.object({
        name:Yup.string().required(t("validation.required")).nullable() ,
        code:Yup.string().required(t("validation.required")).nullable() ,
    });

    useEffect(() => {
        if (selectedAllowanceType) setAllowanceType(selectedAllowanceType);
    } , [selectedAllowanceType]);

    const {isManager , isAdmin} = hrRoleUtilsStore;
    return (
        <GlobitsPopupV2
            size="sm"
            scroll={"body"}
            open={openViewPopup || open}
            noDialogContent
            title={openViewPopup ? (t("Xem chi tiến") + ' ' + t("allowanceType.title")) : (selectedAllowanceType?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("allowanceType.title")}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={allowanceType}
                onSubmit={(values) => !values?.id ? createAllowanceType(values) : editAllowanceType(values)}
            >
                {({isSubmitting}) => (
                    <Form autoComplete="off">
                        <DialogContent className='dialog-body p-12'>
                            <Grid container spacing={2}>

                                <Grid item xs={12} sm={12}>
                                    <GlobitsTextField
                                        label={t("allowanceType.name")}
                                        name="name"
                                        validate
                                        readOnly={readOnly}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={12}>
                                    <GlobitsTextField
                                        label={t("allowanceType.code")}
                                        name="code"
                                        validate
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
}

export default memo(observer(AllowanceTypeForm));
