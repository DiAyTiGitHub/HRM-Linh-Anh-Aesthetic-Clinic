import React, { memo, useEffect } from "react";
import { observer } from "mobx-react";
import { Grid, DialogContent, Button, Tooltip, DialogActions, Icon } from "@material-ui/core";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import StaffWorkScheduleSummary from "./StaffWorkScheduleSummary";
import CachedIcon from "@material-ui/icons/Cached";
import BlockIcon from "@material-ui/icons/Block";
import { useTranslation } from "react-i18next";
import SaveIcon from "@material-ui/icons/Save";
import { Form, Formik } from "formik";
import { useStore } from "app/stores";
import EditStaffWorkScheduleSummary from "./EditStaffWorkScheduleSummary";
import { toast } from "react-toastify";

function StaffWorkScheduleStatisticPopup(props) {
    const { t } = useTranslation();

    const {
        hrRoleUtilsStore,
        staffWorkScheduleStore
    } = useStore();

    const {
        isAdmin,
        checkAllUserRoles,
        isManager,
        hasShiftAssignmentPermission

    } = hrRoleUtilsStore;
    const { readOnly } = props

    let canManipulateData = false;
    if (isAdmin || isManager || hasShiftAssignmentPermission) {
        canManipulateData = true;
    }


    const {
        saveScheduleStatistic,
        handleCloseEditStatistic,
        handleOpenEditStatistic,
        openEditStatistic,
        handleRecalculateStaffWorkTimeNoOpenForm,
        pagingStaffWorkSchedule,
        handleCloseViewStatisticPopup,
        selectedStaffWorkSchedule,
        openViewStatistic,
    } = staffWorkScheduleStore;

    async function handleSaveForm(rawValues) {
        if (!canManipulateData) {
            toast.info("Nhân viên không có quyền thay đổi dữ liệu");
            return;
        }

        const values = { ...rawValues };

        if (values.leavePeriod) {
            values.leavePeriods = [values.leavePeriod];
        }
        if (openEditStatistic) {
            await saveScheduleStatistic(values);
            handleCloseEditStatistic();
        } else {
            await handleRecalculateStaffWorkTimeNoOpenForm(values.id);
        }

        await pagingStaffWorkSchedule();
    }

    const handleClosePopup = () => {
        if (openEditStatistic) {
            handleCloseEditStatistic();
        } else {
            handleCloseViewStatisticPopup();
        }
    };

    useEffect(() => {
        checkAllUserRoles();
    }, []);

    return (
        <GlobitsPopupV2
            popupId={"StatisticPopup"}
            scroll={"body"}
            size='sm'
            open={openViewStatistic}
            title={"Thống kê kết quả ca làm việc"}
            noDialogContent
            onClosePopup={handleClosePopup}
        >
            <Formik
                enableReinitialize
                initialValues={selectedStaffWorkSchedule}
                onSubmit={handleSaveForm}
            >
                {({ isSubmitting, values, setFieldValue, initialValues }) => {
                    return (
                        <Form autoComplete='off' autocomplete='off'>
                            <div className='dialog-body'>
                                <DialogContent className='p-12'>
                                    <Grid container spacing={2}>
                                        <Grid item xs={12}>
                                            {openEditStatistic ? (
                                                <EditStaffWorkScheduleSummary />
                                            ) : (
                                                <StaffWorkScheduleSummary />
                                            )}
                                        </Grid>
                                    </Grid>
                                </DialogContent>
                            </div>

                            <div className='dialog-footer dialog-footer-v2 py-8'>
                                <DialogActions className='p-0'>
                                    <div className='flex flex-space-between flex-middle'>
                                        <Button
                                            startIcon={<BlockIcon />}
                                            variant='contained'
                                            className={`${!readOnly && "mr-12"} btn-secondary d-inline-flex`}
                                            color='secondary'
                                            onClick={() => {
                                                if (openEditStatistic) {
                                                    handleCloseEditStatistic();
                                                } else {
                                                    handleCloseViewStatisticPopup();
                                                }
                                            }}>
                                            {t("general.button.cancel")}
                                        </Button>

                                        {!values?.isLocked && canManipulateData && (
                                            openEditStatistic ? (
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
                                            ) : (
                                                <Button
                                                    startIcon={<Icon fontSize="small" style={{ color: "#3f51b5" }}>edit</Icon>}
                                                    onClick={() => handleOpenEditStatistic(values?.id)}
                                                    type="button"
                                                    className="mr-12 btn btn-primary d-inline-flex"
                                                    variant="contained"
                                                    color="primary"
                                                    disabled={isSubmitting}
                                                >
                                                    {t("Sửa thống kê")}
                                                </Button>
                                            )
                                        )}

                                        {!values?.isLocked && canManipulateData && (!openEditStatistic) && (
                                            <Tooltip
                                                arrow
                                                title='Thực hiện thống kê lại dữ liệu chấm công của nhân viên'
                                                placement='bottom'>
                                                <Button
                                                    startIcon={<CachedIcon />}
                                                    className='btn bgc-lighter-dark-blue d-inline-flex text-white'
                                                    type='submit'
                                                    disabled={isSubmitting}>
                                                    Thống kê lại
                                                </Button>
                                            </Tooltip>
                                        )}
                                    </div>
                                </DialogActions>
                            </div>
                        </Form>
                    );
                }}
            </Formik >
        </GlobitsPopupV2 >
    );
}

export default memo(observer(StaffWorkScheduleStatisticPopup));
