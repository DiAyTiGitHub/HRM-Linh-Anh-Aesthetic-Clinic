import React , { memo , useEffect , useState } from "react";
import { Form , Formik } from "formik";
import { Button , DialogActions , DialogContent , Grid } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "../../stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import SelectDepartmentComponent from "../../common/SelectComponent/SelectDepartment/SelectDepartmentComponent";

function HrDepartmentIpCUForm(props) {
    const {hrDepartmentIpStore} = useStore();
    const {t} = useTranslation();
    const {readOnly} = props;
    const {
        handleClose ,
        saveDepartmentIp ,
        pagingHrDepartmentIp ,
        selectedDepartmentIp ,
        openCreateEditPopup ,
        openViewPopup
    } = hrDepartmentIpStore;

    const validationSchema = Yup.object({
        // department:Yup.object()
        //     .required(t("validation.required"))
        //     .nullable() ,
        ipAddress:Yup.string().required(t("validation.required")).nullable() ,
    });

    async function handleSaveForm(values) {
        await saveDepartmentIp(values);
        await pagingHrDepartmentIp();
    }

    const [initialValues , setInitialValues] = useState(selectedDepartmentIp);

    useEffect(function () {
        setInitialValues(selectedDepartmentIp);
    } , [selectedDepartmentIp , selectedDepartmentIp?.id]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="xs"
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={openViewPopup ? ("Xem chi tiết " + t("navigation.hrDepartmentIp.title")) : (selectedDepartmentIp?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("navigation.hrDepartmentIp.title")}
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
                                                label="Địa chỉ IP"
                                                name="ipAddress"
                                                required
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        {/* <Grid item xs={12}>
                                            <SelectDepartmentComponent
                                                name={"department"}
                                                label={t("Phòng ban sử dụng")}
                                                clearFields={["title"]}
                                                disabled={readOnly}
                                                disabledTextFieldOnly={true}
                                                readOnly={readOnly}
                                                placeholder={"Chọn phòng ban sử dụng"}
                                            />
                                        </Grid> */}

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
                                        <Button
                                            startIcon={<BlockIcon/>}
                                            variant="contained"
                                            className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                            color="secondary"
                                            onClick={handleClose}
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>
                                        {!readOnly && (<Button
                                            startIcon={<SaveIcon/>}
                                            className="mr-0 btn btn-primary d-inline-flex"
                                            variant="contained"
                                            color="primary"
                                            type="submit"
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.save")}
                                        </Button>)}
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

export default memo(observer(HrDepartmentIpCUForm));