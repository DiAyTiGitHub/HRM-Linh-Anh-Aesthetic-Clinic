import React, { memo, useEffect, useState } from "react";
import { Button, DialogActions, DialogContent, Grid } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import * as Yup from "yup";
import { Form, Formik } from "formik";
import { useStore } from "app/stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { observer } from "mobx-react";
import { useParams } from "react-router-dom";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import moment from "moment";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { pagingStaff } from "../HumanResourcesInformation/StaffService";
import { LIST_YEAR } from "app/LocalConstants";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import StaffAnnualLeaveHistoryStatistics from "./StaffAnnualLeaveHistoryStatistics";

function StaffAnnualLeaveHistoryCUForm(props) {
    const {
        readOnly
    } = props;

    const { staffAnnualLeaveHistoryStore } = useStore();
    const { t } = useTranslation();
    const { id } = useParams();

    const {
        handleClose,
        saveStaffAnnualLeaveHistory,
        selectedStaffAnnualLeaveHistory,
        openCreateEditPopup,
        openViewPopup
    } = staffAnnualLeaveHistoryStore;

    const validationSchema = Yup.object({
        staff: Yup.object().required(t("validation.required")).nullable(),
        year: Yup.number().required(t("validation.required")).nullable(),
        
    });

    const [staffAnnualLeaveHistory, setStaffAnnualLeaveHistory] = useState(selectedStaffAnnualLeaveHistory);

    useEffect(() => {
        if (selectedStaffAnnualLeaveHistory) setStaffAnnualLeaveHistory(selectedStaffAnnualLeaveHistory);
        else setStaffAnnualLeaveHistory(selectedStaffAnnualLeaveHistory);
        if (id) {
            setStaffAnnualLeaveHistory(prev => ({
                ...prev,
                staff: { id: id }
            }));
        }

    }, [selectedStaffAnnualLeaveHistory?.id]);

    async function handleSubmit(values) {
        try {
            await saveStaffAnnualLeaveHistory(values);

        }
        catch (error) {
            console.error(error);
        }
    }

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size={"md"} 
            open={openCreateEditPopup || openViewPopup}
            onClosePopup={handleClose}
            noDialogContent
            title={(staffAnnualLeaveHistory?.id?.length > 0 ? t("general.button.add") : t("general.button.edit")) + " " + "thống kê nghỉ phép"}
        >
            <Formik
                initialValues={staffAnnualLeaveHistory}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}
            >
                {({ isSubmitting, values, setFieldValue }) => (
                    <Form autoComplete="off">
                        <DialogContent className='dialog-body p-12'>
                            <Grid container spacing={2}>
                                <Grid item xs={12} >
                                    <p className='m-0 p-0 borderThrough2'>Thông tin chung</p>
                                </Grid>

                                <Grid item xs={12} sm={6} >
                                    <GlobitsPagingAutocompleteV2
                                        name='staff'
                                        label={t("Nhân viên nghỉ phép")}
                                        api={pagingStaff}
                                        getOptionLabel={(option) =>
                                            option?.displayName && option?.staffCode
                                                ? `${option.displayName} - ${option.staffCode}`
                                                : option?.displayName || option?.staffCode || ""
                                        }
                                        required
                                        readOnly={readOnly}
                                    />
                                </Grid>

                                <Grid item xs={12} sm={6} >
                                    <GlobitsSelectInput
                                        name='year'
                                        label={t("Năm thống kê")}
                                        options={LIST_YEAR}
                                        readOnly={readOnly}
                                        required
                                    />
                                </Grid>

                                <Grid item xs={12} >
                                    <StaffAnnualLeaveHistoryStatistics
                                        readOnly={readOnly}
                                    />
                                </Grid>


                            </Grid>
                        </DialogContent>

                        <DialogActions className='dialog-footer px-12'>
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    startIcon={<BlockIcon />}
                                    variant="contained"
                                    className="mr-12 btn btn-secondary d-inline-flex"
                                    color="secondary"
                                    onClick={() => {
                                        handleClose();
                                    }}
                                >
                                    {t("general.button.close")}
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
                    </Form>
                )}
            </Formik>
        </GlobitsPopupV2>
    )
}

export default memo(observer(StaffAnnualLeaveHistoryCUForm));