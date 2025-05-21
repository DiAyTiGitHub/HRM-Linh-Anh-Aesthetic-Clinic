import { Button , DialogActions , DialogContent , Grid , makeStyles } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { Form , Formik } from "formik";
import { observer } from "mobx-react";
import { memo , useEffect , useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "../../stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";

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

function OrganizationalChartDataCUForm() {
    const {organizationalChartDataStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose ,
        saveOrgChartData ,
        pagingOrgChartData ,
        selectedOrgChartData ,
        openCreateEditPopup
    } = organizationalChartDataStore;

    const validationSchema = Yup.object({
        code:Yup.string().required(t("validation.code")).nullable() ,
        name:Yup.string().required(t("validation.name")).nullable()
    });

    async function handleSaveForm(values) {
        await saveOrgChartData(values);
        await pagingOrgChartData();
        // handleClose();
    }

    const [initialValues , setInitialValues] = useState(selectedOrgChartData);

    useEffect(function () {
        setInitialValues(selectedOrgChartData);
    } , [selectedOrgChartData , selectedOrgChartData?.id])

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="xs"
            open={openCreateEditPopup}
            noDialogContent
            title={(selectedOrgChartData?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + "Sơ đồ tổ chức"}
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
                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                validate
                                                label="Tên sơ đồ"
                                                name="name"
                                                required
                                            />
                                        </Grid>

                                        <Grid item xs={12}>
                                            <GlobitsTextField
                                                validate
                                                label="Mã sơ đồ"
                                                name="code"
                                                required
                                            />
                                        </Grid>

                                        {/* <Grid item xs={12} sm={4}>

                    </Grid> */}
                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className="dialog-footer py-8">
                                <DialogActions className="p-0">
                                    <div className="flex flex-space-between flex-middle">
                                        <Button
                                            startIcon={<BlockIcon/>}
                                            variant="contained"
                                            className="mr-12 btn btn-gray d-inline-flex"
                                            // color="secondary"
                                            onClick={handleClose}
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>
                                        <Button
                                            startIcon={<SaveIcon/>}
                                            className="mr-0 btn btn-success d-inline-flex"
                                            variant="contained"
                                            // color="primary"
                                            type="submit"
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.save")}
                                        </Button>
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

export default memo(observer(OrganizationalChartDataCUForm));