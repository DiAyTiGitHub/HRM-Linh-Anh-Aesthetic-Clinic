import {
    Button,
    DialogActions,
    DialogContent,
    Grid,
} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import { Form, Formik } from "formik";
import { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { useStore } from "app/stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { observer } from "mobx-react";
import FormikFocusError from "app/common/FormikFocusError";
import GlobitsPagingAutocomplete from "app/common/form/GlobitsPagingAutocomplete";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import { pagingShiftWork } from "app/views/ShiftWork/ShiftWorkService";
import { getDate } from "app/LocalFunction";
import GlobitsTable from "app/common/GlobitsTable";
import { useParams } from "react-router-dom";
import ChooseUsingStaffSection from "../../User/UsingAccountStaff/ChooseUsingStaffSection";
import LocalConstants from "../../../LocalConstants";

function ShiftRegistrationFormPopup(props) {
    const { ShiftRegistrationStore } = useStore();
    const { t } = useTranslation();
    const { id } = useParams();

    const {
        handleClose,
        pagingShiftRegistration,
        openFormShiftRegristration,
        handleSetSearchObject,
        saveShiftRegistration,
    } = ShiftRegistrationStore;

    const validationSchema = Yup.object({
        shiftWork: Yup.object().required(t("validation.required")).nullable(),
        workingDate: Yup.string().required(t("validation.required")).nullable(),
        // approvalStaff: Yup.string().required(t("validation.required")).nullable(),

    });

    async function handleSaveForm(values) {
        try {
            const initialValues = {
                ...values,
                registerStaff: {
                    id: id
                },
                registerStaffId: id
            };
            await saveShiftRegistration(initialValues);
            handleClose();
            await pagingShiftRegistration();
        } catch (error) {
            console.error(error);
        }
    }

    const [initialValues, setInitialValues] = useState({
        shiftWork: null,
        workingDate: new Date(),
        // approvalStaff: null,
    });


    useEffect(function () {
        const initialSearchObjectOfStaff = {
            pageIndex: 1,
            pageSize: 10,
            keyword: null,
            // registerStaff: {id: id},
            fromDate: null,
            toDate: null,
        };

        handleSetSearchObject(initialSearchObjectOfStaff);
        pagingShiftRegistration();
    }, []);

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="lg"
            open={openFormShiftRegristration}
            noDialogContent
            title={"Đăng ký ca làm việc"}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}
            >
                {({ isSubmitting, values, setFieldValue, initialValues }) => {

                    return (
                        <Form autoComplete="off">
                            <div className="dialog-body">
                                <DialogContent className="p-12">
                                    <FormikFocusError />

                                    <Grid container spacing={2}>
                                        <Grid item xs={12} sm={8}>

                                            <Grid container spacing={2}>
                                                <Grid item xs={12} className="pb-0">
                                                    <p className="m-0 p-0 borderThrough2">
                                                        {`Lịch sử đăng ký ca làm việc ${initialValues?.registerStaff?.displayName ? initialValues?.registerStaff?.displayName : ""}`}
                                                    </p>
                                                </Grid>

                                                <Grid item xs={12}>
                                                    <div>
                                                        <ShiftRegistrationListSection />
                                                    </div>
                                                </Grid>

                                            </Grid>

                                        </Grid>

                                        <Grid item xs={12} sm={4}>
                                            <Grid container spacing={2}>
                                                <Grid item xs={12} className="pb-0">
                                                    <p className="m-0 p-0 borderThrough2">
                                                        Đăng ký ca
                                                    </p>
                                                </Grid>
                                                <Grid item xs={12}>
                                                    <ChooseUsingStaffSection
                                                        label={t("Người phê duyệt")}
                                                        name="approvalStaff"
                                                    />
                                                </Grid>

                                                <Grid item xs={12}>
                                                    <GlobitsPagingAutocomplete
                                                        name='shiftWork' label={t("Ca làm việc")}
                                                        api={pagingShiftWork}
                                                        required
                                                    />
                                                </Grid>

                                                <Grid item xs={12}>
                                                    <GlobitsDateTimePicker
                                                        label={t("Ngày làm việc")}
                                                        name="workingDate"
                                                        required
                                                    />
                                                </Grid>
                                            </Grid>
                                        </Grid>
                                    </Grid>

                                </DialogContent>
                            </div>

                            <div className="dialog-footer dialog-footer-v2 py-8">
                                <DialogActions className="p-0">
                                    <div className="flex flex-space-between flex-middle">
                                        <Button
                                            startIcon={<BlockIcon />}
                                            variant="contained"
                                            className="mr-12 btn btn-secondary d-inline-flex"
                                            color="secondary"
                                            onClick={() => handleClose()}
                                            disabled={isSubmitting}
                                        >
                                            {t("general.button.cancel")}
                                        </Button>
                                        <Button
                                            startIcon={<SaveIcon />}
                                            className="mr-0 btn btn-primary d-inline-flex"
                                            variant="contained"
                                            color="primary"
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
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(ShiftRegistrationFormPopup));

const ShiftRegistrationListSection = () => {
    const { ShiftRegistrationStore } = useStore();

    const {
        searchObject,
        listShiftRegistrations,
        totalPages,
        handleChangePage,
        setPageSize,
        totalElements,
    } = ShiftRegistrationStore;

    const columns = [
        {
            title: "Ca làm việc",
            width: "20%",
            align: "left",
            field: "shiftWork.name",
            render: row => <span>{`${row?.shiftWork?.name}`}</span>
        },
        {
            title: "Ngày làm việc",
            field: "workingDate",
            width: "10%",
            align: "left",
            render: row => <span>{getDate(row?.workingDate)}</span>
        },
        {
            title: "Người phê duyệt",
            width: "10%",
            align: "left",
            render: row => row?.approvalStaff?.displayName
        },
        {
            title: "Trạng thái đăng ký",
            width: "10%",
            align: "left",
            render: row => {
                const status = LocalConstants.ShiftRegistrationApprovalStatus.getListData()
                    .find(item => item.value === row.approvalStatus);
                return status ? status.name : "Không xác định";
            }
        },
    ];

    return (
        <GlobitsTable
            data={listShiftRegistrations}
            columns={columns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={searchObject?.pageSize}
            pageSizeOption={[10, 15, 25, 50, 100]}
            totalElements={totalElements}
            page={searchObject?.pageIndex}
        />
    )
}