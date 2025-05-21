import { Button , DialogActions , DialogContent , Grid , makeStyles } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { Form , Formik } from "formik";
import { observer } from "mobx-react";
import React , { memo , useEffect , useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import {CodePrefixes} from "../../LocalConstants";

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

function DepartmentTypeCUForm({readOnly}) {
    const {departmentTypeStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose ,
        saveDepartmentType ,
        pagingDepartmentType ,
        selectedDeparmentType ,
        openCreateEditPopup ,
        openViewPopup,
        autoGenCode
    } = departmentTypeStore;

    const validationSchema = Yup.object({
        code:Yup.string().required(t("validation.code")).nullable() ,
        name:Yup.string().required(t("validation.name")).nullable() ,
        sortNumber:Yup.number().required(t("validation.required")).nullable() ,
    });

    async function handleSaveForm(values) {
        await saveDepartmentType(values);
        await pagingDepartmentType();
        // handleClose();
    }

    const [initialValues , setInitialValues] = useState(selectedDeparmentType);
    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.LOAI_PHONG_BAN);
        if (code) {
            // Tạo object mới để tránh thay đổi trực tiếp state
            const updated = {...selectedDeparmentType, ...{code:code}};
            setInitialValues(updated);
        }
    };
    useEffect(() => {
        if(!selectedDeparmentType?.id){
            autoGenCodeFunc();
        }
    }, []);
    useEffect(function () {
        setInitialValues(selectedDeparmentType);
    } , [selectedDeparmentType , selectedDeparmentType?.id])
    const {isAdmin} = useStore().hrRoleUtilsStore

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="md"
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={openViewPopup ? (t("general.button.view") + " " + t("loại phòng ban")) : (selectedDeparmentType?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + "loại phòng ban"}
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
                        <Form autoComplete="off" autocomplete="off">
                            <div className="dialog-body">
                                <DialogContent className="p-12">
                                    <FormikFocusError/>

                                    <Grid container spacing={2}>

                                        <Grid item xs={12} sm={4}>
                                            <GlobitsTextField
                                                validate
                                                label="Mã loại phòng ban"
                                                name="code"
                                                required
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={4}>
                                            <GlobitsTextField
                                                validate
                                                label="Tên loại phòng ban"
                                                name="name"
                                                required
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={4}>
                                            <GlobitsTextField
                                                required
                                                validate
                                                type="number"
                                                label="Trọng số"
                                                name="sortNumber"
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={4}>
                                            <GlobitsTextField
                                                label="Tên viết tắt"
                                                name="shortName"
                                                readOnly={readOnly}
                                            />
                                        </Grid>


                                        <Grid item xs={12} sm={4}>
                                            <GlobitsTextField
                                                label="Tên khác"
                                                name="otherName"
                                                readOnly={readOnly}
                                            />
                                        </Grid>


                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                label="Mô tả"
                                                name="description"
                                                multiline
                                                rows={3}
                                                readOnly={readOnly}
                                            />
                                        </Grid>
                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className="dialog-footer py-8">
                                <DialogActions className="p-0">
                                    <div className="flex flex-space-between flex-middle">
                                        <Button startIcon={<BlockIcon/>} variant='contained'
                                                className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                                color='secondary'
                                                onClick={() => handleClose()}>
                                            {t("general.button.cancel")}
                                        </Button>
                                        {((isAdmin) && !readOnly) && (
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
                    );
                }
                }
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(DepartmentTypeCUForm));