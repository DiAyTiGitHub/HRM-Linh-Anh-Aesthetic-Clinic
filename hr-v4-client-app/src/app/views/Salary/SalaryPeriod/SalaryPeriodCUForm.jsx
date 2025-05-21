import React , { memo , useEffect , useState } from "react";
import { Form , Formik } from "formik";
import { Button , DialogActions , DialogContent , Grid } from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import moment from "moment";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import { pagingSalaryPeriod as pagingSalaryPeriodInService } from "./SalaryPeriodService";
import {CodePrefixes} from "../../../LocalConstants";

function SalaryPeriodCUForm({readOnly}) {
    const {salaryPeriodStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();
    const {
        handleClose ,
        saveSalaryPeriod ,
        pagingSalaryPeriod ,
        selectedSalaryPeriod ,
        openCreateEditPopup ,
        openViewPopup,
        autoGenCode
    } = salaryPeriodStore;

    const {
        isAdmin ,
        isManager
    } = hrRoleUtilsStore;

    const validationSchema = Yup.object({
        code:Yup.string().required(t("validation.code")).nullable() ,
        name:Yup.string().required(t("validation.name")).nullable() ,
        fromDate:Yup.date()
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày bắt đầu kỳ lương không đúng định dạng")
            .nullable() ,

        toDate:Yup.date()
            .test(
                "is-greater" ,
                "Ngày kết thúc phải lớn hơn ngày bắt đầu kỳ lương" ,
                function (value) {
                    const {fromDate} = this.parent;
                    if (fromDate && value) {
                        return moment(value).isAfter(moment(fromDate) , "date");
                    }
                    return true;
                }
            )
            .transform(function transformDate(castValue , originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày tốt nghiệp không đúng định dạng")
            .nullable() ,
    });

    async function handleSaveForm(values) {
        await saveSalaryPeriod(values);
        await pagingSalaryPeriod();
    }

    const [initialValues , setInitialValues] = useState(selectedSalaryPeriod);
    const autoGenCodeFunc = async () => {
        const code = await autoGenCode(CodePrefixes.KY_LUONG);
        if (code) {
            // Tạo object mới để tránh thay đổi trực tiếp state
            const updated = {...selectedSalaryPeriod, ...{code:code}};
            setInitialValues(updated);
        }
    };
    useEffect(() => {
        if(!selectedSalaryPeriod?.id){
            autoGenCodeFunc();
        }
    }, []);
    useEffect(function () {
        setInitialValues(selectedSalaryPeriod);
    } , [selectedSalaryPeriod , selectedSalaryPeriod?.id]);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="sm"
            open={openCreateEditPopup || openViewPopup}
            noDialogContent
            title={openViewPopup ? (t("Xem chi tiết ") + t("navigation.payroll.period")) : (selectedSalaryPeriod?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("navigation.payroll.period")}
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
                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                required
                                                label="Tên kỳ lương"
                                                name="name"
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsTextField
                                                required
                                                label="Mã kỳ lương"
                                                name="code"
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                required
                                                label={"Ngày bắt đầu"}
                                                name="fromDate"
                                                readOnly={readOnly}
                                            />
                                        </Grid>

                                        <Grid item xs={12} sm={6}>
                                            <GlobitsDateTimePicker
                                                required
                                                label={"Ngày kết thúc"}
                                                name="toDate"
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

                                        <Grid item xs={12}>
                                            <GlobitsPagingAutocomplete
                                                label={"Kỳ lương cha"}
                                                name="parentPeriod"
                                                api={pagingSalaryPeriodInService}
                                                readOnly={readOnly}
                                            />
                                        </Grid>
                                    </Grid>

                                </DialogContent>
                            </div>

                            <div className="dialog-footer dialog-footer-v2 py-8">
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
                                        {((isAdmin || isManager) && !readOnly) && (
                                            <Button
                                                startIcon={<SaveIcon/>}
                                                className="mr-0 btn btn-primary d-inline-flex"
                                                variant="contained"
                                                color="primary"
                                                type="submit"
                                                disabled={isSubmitting}
                                            >
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

export default memo(observer(SalaryPeriodCUForm));