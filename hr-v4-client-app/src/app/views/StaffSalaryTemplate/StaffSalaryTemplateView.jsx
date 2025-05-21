import React , { memo , useEffect , useState } from "react";
import { Form , Formik } from "formik";
import { Button , DialogActions , DialogContent , Grid , Icon } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { useStore } from "app/stores";
import * as Yup from "yup";
import { observer } from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import StaffSalaryItemValue from "./StaffSalaryItemValue";
import { saveStaffSalaryItemValueList } from "../Salary/StaffSalaryItemValue/StaffSalaryItemValueService";
import { toast } from "react-toastify";
import BlockIcon from "@material-ui/icons/Block";
import GlobitsCheckBox from "../../common/form/GlobitsCheckBox";

function StaffSalaryTemplateView(props) {
    const {hasStaff = false , readOnly} = props;
    const {staffSalaryTemplateStore} = useStore();
    const {t} = useTranslation();

    const {
        handleClose ,
        saveStaffSalaryTemplate ,
        pagingStaffSalaryTemplate ,
        selectedStaffSalaryTemplate ,
        openCreateEditPopup ,
        openViewPopup ,
        handleOpenCreateEdit ,
        handleSetOpenViewPopup
    } = staffSalaryTemplateStore;

    const validationSchema = Yup.object({
        staff:Yup.object().required(t("validation.required")).nullable() ,
        salaryTemplate:Yup.object().required(t("validation.required")).nullable() ,
    });

    async function handleSaveForm(values) {
        try {
            const payloadLabourAgreement = {
                ... values
            };

            await saveStaffSalaryTemplate(payloadLabourAgreement);

            // save staff salary item values
            const payloadStaffSalaryItemValueList = {
                staff:values?.staff ,
                salaryTemplate:values?.salaryTemplate ,
                staffSalaryItemValue:values?.staffSalaryItemValue
            };

            if (payloadStaffSalaryItemValueList.staff && payloadStaffSalaryItemValueList.salaryTemplate && Array.isArray(payloadStaffSalaryItemValueList.staffSalaryItemValue) && payloadStaffSalaryItemValueList.staffSalaryItemValue.length > 0) {
                await saveStaffSalaryItemValueList(payloadStaffSalaryItemValueList);
            }

            await pagingStaffSalaryTemplate();

            toast.success("Thông tin mẫu bảng lương áp dụng cho nhân viên đã được lưu");


            handleClose();
        } catch (error) {
            toast.error("Có lỗi xảy ra, vui lòng thử lại");

            console.error(error);
        }
    }

    const [initialValues , setInitialValues] = useState(selectedStaffSalaryTemplate);

    useEffect(function () {
        setInitialValues({
            ... selectedStaffSalaryTemplate
        });
    } , [selectedStaffSalaryTemplate , selectedStaffSalaryTemplate?.id]);


    return (<GlobitsPopupV2
        size="sm"
        scroll={"body"}
        open={openCreateEditPopup || openViewPopup}
        noDialogContent
        title={openViewPopup ? (t("Xem chi tiết ") + t("Mẫu bảng lương nhân viên")) : (selectedStaffSalaryTemplate?.id ? t("general.button.edit") : t("general.button.add")) + ' ' + t("Mẫu bảng lương nhân viên")}
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
                    <Form autoComplete="off">
                        <DialogContent className="o-hidden p-12">
                            <Grid container spacing={2}>

                                <Grid item xs={12} className="pb-0">
                                    <p className="m-0 p-0 borderThrough2">
                                        Thông tin ca làm việc
                                    </p>
                                </Grid>

                                {!hasStaff && (
                                    <Grid item xs={12} sm={6}>
                                        <div>
                                            <label><strong>{t("Nhân viên")}</strong></label>
                                            <div style={{minHeight:"35px"}} className={"flex align-center"}>
                                                {
                                                    values?.staff?.displayName && values?.staff?.staffCode
                                                        ? `${values?.staff?.displayName} - ${values?.staff?.staffCode}`
                                                        : values?.staff?.displayName || values?.staff?.staffCode || ""
                                                }</div>
                                        </div>
                                    </Grid>
                                )}

                                <Grid item xs={12} sm={6}>
                                    <div>
                                        <label><strong>{t("Mẫu bảng lương")}</strong></label>
                                        <div style={{minHeight:"35px"}} className={"flex align-center"}>
                                            {values?.salaryTemplate?.name}
                                        </div>
                                    </div>
                                </Grid>
                                <Grid item xs={12} sm={6} className={"flex align-end"}>
                                    <GlobitsCheckBox
                                        label={"Ẩn các các giá trị bằng 0"}
                                        name="displayValueEqualZero"
                                    />

                                </Grid>
                                <StaffSalaryItemValue
                                    readOnly={readOnly}
                                    displayValueEqualZero={!values?.displayValueEqualZero}/>
                            </Grid>
                        </DialogContent>

                        <DialogActions className="dialog-footer px-12">
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    variant="contained"
                                    className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                    startIcon={<BlockIcon/>}
                                    color="secondary"
                                    disabled={isSubmitting}
                                    onClick={handleClose}
                                >
                                    {t("general.button.close")}
                                </Button>
                                <Button
                                    startIcon={
                                        <Icon fontSize="small"
                                              style={{color:"#3f51b5"}}>edit</Icon>
                                    }
                                    onClick={() => {
                                        handleOpenCreateEdit(values?.id)
                                        handleSetOpenViewPopup(false)
                                    }}
                                    type="button"
                                    className="ml-12 btn btn-primary d-inline-flex"
                                    variant="contained"
                                    color="primary"
                                    disabled={isSubmitting}
                                >
                                    {t("general.button.edit")}
                                </Button>
                            </div>
                        </DialogActions>
                    </Form>
                );
            }}
        </Formik>
    </GlobitsPopupV2>);
}

export default memo(observer(StaffSalaryTemplateView));
