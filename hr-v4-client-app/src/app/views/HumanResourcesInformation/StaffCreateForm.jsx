import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { t } from "app/common/CommonFunctions";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { useStore } from "app/stores";
import { Form, Formik, useFormikContext } from "formik";
import { observer } from "mobx-react";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import * as Yup from "yup";
import ConstantList from "../../appConfig";
import GlobitsPagingAutocomplete from "../../common/form/GlobitsPagingAutocomplete";
import { Staff } from "../../common/Model/Staff";
import { pagingEmployeeStatus } from "../EmployeeStatus/EmployeeStatusService";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { generateNewStaffCode } from "./StaffService";
import { toast } from "react-toastify";

function StaffCreateForm() {
    const { t } = useTranslation();
    const history = useHistory();

    const { staffStore } = useStore();

    const {
        saveStaffWithoutAccount,
        shouldOpenFormCreate,
        handleClose,
        setShouldOpenFormCreate,
    } = staffStore;

    const [staff, setStaff] = useState({ ...new Staff() });

    const validationSchema = Yup.object({
        displayName: Yup.string()
            .ensure()
            .max(100, "Không được nhập quá 100 ký tự")
            .matches(/^[^\d]*$/, "Dữ liệu không hợp lệ")
            .test("no_special_chars", "Dữ liệu chứa ký tự đặc biệt", (value) => {
                const specialChars = "<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-=";
                return ![...value].some((char) => specialChars.includes(char));
            })
            .test("length_fullName", "Dữ liệu không hợp lệ", (val) => val.trim().split(" ").length > 1)
            .required(t("validation.required"))
            .nullable(),

        status: Yup.object().required(t("validation.required")).nullable(),
        staffCode: Yup.string().required(t("validation.required")).nullable(),
    });

    async function handleFormSubmit(staff) {
        const data = await saveStaffWithoutAccount(staff);
        setShouldOpenFormCreate(false);

        if (data?.id) {
            history.push(ConstantList.ROOT_PATH + `staff/edit/${data?.id}`);
        }
    }

    return (
        <GlobitsPopupV2
            open={shouldOpenFormCreate}
            size='xs'
            noDialogContent
            title={t("general.button.add") + " nhân viên"}
            onClosePopup={handleClose}
        >

            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={staff}
                onSubmit={(values) => handleFormSubmit(values)}
            >
                {({ isSubmitting, values, setFieldValue }) => (
                    <Form autoComplete='off'>
                        <div className='dialog-body'>
                            <DialogContent className='o-hidden p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <p className="m-0 p-0 borderThrough2">
                                            Ngày vào làm việc
                                        </p>
                                    </Grid>

                                    <Grid item xs={12}>
                                        <NewStaffRecruitmentDateSection />
                                    </Grid>


                                    <Grid item xs={12}>
                                        <p className="m-0 p-0 borderThrough2">
                                            Thông tin cơ bản
                                        </p>
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsTextField required label={"Mã nhân viên"} name='staffCode' />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <DisplayNameComponent />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <GlobitsPagingAutocomplete
                                            requiredLabel
                                            required
                                            label={t("humanResourcesInformation.status")}
                                            name='status'
                                            api={pagingEmployeeStatus}
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>
                        </div>
                        <div className='dialog-footer'>
                            <DialogActions className='p-0'>
                                <div className='flex flex-space-between flex-middle'>
                                    <Button
                                        startIcon={<BlockIcon />}
                                        variant='contained'
                                        className='mr-12 btn btn-secondary d-inline-flex'
                                        color='secondary'
                                        onClick={() => handleClose()}>
                                        {t("general.button.cancel")}
                                    </Button>
                                    <Button
                                        startIcon={<SaveIcon />}
                                        className='mr-0 btn btn-primary d-inline-flex'
                                        variant='contained'
                                        color='primary'
                                        type='submit'
                                        disabled={isSubmitting}>
                                        {t("general.button.save")}
                                    </Button>
                                </div>
                            </DialogActions>
                        </div>
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(StaffCreateForm));


function NewStaffRecruitmentDateSection() {
    const {
        values,
        setFieldValue
    } = useFormikContext();

    async function handleGetNewStaffCode() {
        try {
            const { data } = await generateNewStaffCode(values);

            setFieldValue("staffCode", data);
        } catch (error) {
            console.log(error);
            toast.error("Có lỗi xảy ra khi tự động sinh mã nhân viên");
        }
    }


    useEffect(function () {
        if (values?.recruitmentDate) {
            handleGetNewStaffCode();
        }

    }, [values?.recruitmentDate]);

    return (
        <GlobitsDateTimePicker
            label="Ngày vào làm việc"
            name="recruitmentDate"
        />
    );
}

const DisplayNameComponent = () => {
    const { values, setFieldValue } = useFormikContext();
    function splitFullName(fullName = "") {
        const cleaned = fullName.trim();
        if (!cleaned) return { firstName: "", lastName: "" };

        const parts = cleaned.split(/\s+/);
        const firstName = parts.pop() || ""; // tên chính (từ cuối)
        const lastName = parts.join(" ") || ""; // họ + đệm
        return { firstName, lastName };
    }

    return (
        <GlobitsTextField
            validate
            label={t("humanResourcesInformation.name")}
            name='displayName'
            onChange={(e) => {
                const { firstName, lastName } = splitFullName(e.target.value);
                setFieldValue("displayName", e.target.value);
                setFieldValue("firstName", firstName);
                setFieldValue("lastName", lastName);
            }}
        />
    );
};
