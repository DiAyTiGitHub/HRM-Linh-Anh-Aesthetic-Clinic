import React, {useEffect, useState} from "react";
import {Button, DialogActions, DialogContent, Grid, Tooltip,} from "@material-ui/core";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import {useTranslation} from "react-i18next";
import * as Yup from "yup";
import {Form, Formik} from "formik";
import {useStore} from "app/stores";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import {observer} from "mobx-react";
import {useParams} from "react-router-dom";
import {pagingRewards} from "../../../Reward/RewardService";
import SelectFile from "../../../StaffDocumentItem/SelectFile";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import VisibilityIcon from "@material-ui/icons/Visibility";
import GetAppIcon from "@material-ui/icons/GetApp";
import DeleteIcon from "@material-ui/icons/Delete";
import GlobitsPagingAutocompleteV2 from "../../../../common/form/GlobitsPagingAutocompleteV2";
import {pagingOrganization} from "../../../Organization/OrganizationService";
import {pagingAllDepartments} from "../../../Department/DepartmentService";
import SaveIcon from "@material-ui/icons/Save";
import BlockIcon from "@material-ui/icons/Block";

export default observer(function StaffRewardHistoryForm(props) {
    const {staffRewardHistoryStore} = useStore();
    const {t} = useTranslation();
    const {id} = useParams();
    const {
        handleClose,
        saveOrUpdateStaffRewardHistory,
        selectedStaffRewardHistory,
        initialStaffRewardHistory,
        shouldOpenEditorDialog
    } = staffRewardHistoryStore;
    const validationSchema = Yup.object({
        rewardDate: Yup.date()
            .transform(function transformDate(castValue, originalValue) {
                return originalValue ? new Date(originalValue) : castValue;
            })
            .required(t("validation.required"))
            .typeError("Ngày khen thưởng không đúng định dạng")
            .nullable(),

        rewardType: Yup.object().required(t("validation.required")).nullable(),
    });
    const [staffRewardHistory, setStaffRewardHistory] = useState(initialStaffRewardHistory);

    useEffect(() => {
        if (selectedStaffRewardHistory) setStaffRewardHistory(selectedStaffRewardHistory);
        else setStaffRewardHistory(initialStaffRewardHistory);
        if (id) {
            setStaffRewardHistory(prev => ({
                ...prev,
                staff: {id: id}
            }));
        }

    }, [selectedStaffRewardHistory?.id]);

    async function handleSubmit(values) {
        await saveOrUpdateStaffRewardHistory(values);
    }

    return (
        <GlobitsPopupV2
            size={"sm"}
            open={shouldOpenEditorDialog}
            onClosePopup={handleClose}
            noDialogContent
            title={(staffRewardHistory?.id?.length > 0 ? t("general.button.add") : t("general.button.edit")) + " " + t("rewardHistory.title")}
        >
            <Formik
                initialValues={staffRewardHistory}
                onSubmit={(values) => handleSubmit(values)}
                validationSchema={validationSchema}
            >
                {({isSubmitting, values}) => (
                    <Form autoComplete="off">
                        <DialogContent className='dialog-body p-12'>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <GlobitsDateTimePicker
                                        label={t("rewardHistory.rewardDate")}
                                        name="rewardDate"
                                        disableFuture={true}
                                        required
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsPagingAutocompleteV2
                                        name="organization"
                                        label={t("rewardHistory.organization")}
                                        requiredLabel
                                        api={pagingOrganization}
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsPagingAutocompleteV2
                                        name="department"
                                        label={t("rewardHistory.department")}
                                        requiredLabel
                                        api={pagingAllDepartments}
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <GlobitsPagingAutocompleteV2
                                        name="rewardType"
                                        label={t("rewardHistory.rewardType")}
                                        requiredLabel
                                        api={pagingRewards}
                                        required
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <SelectFile
                                        name={"file"}
                                        fileProp={values?.file}
                                        showPreview={true}
                                        showDowload={true}
                                        showDelete={true}
                                        showName={false}
                                        acceptedFiles={["image/jpeg", "image/png", "application/pdf"]} // Chấp nhận cả ảnh và PDF
                                        maxFileSize={5242880}
                                        uploadButton={
                                            <Tooltip
                                                placement='top'
                                                title={t("rewardHistory.file")}
                                                style={{
                                                    border: "1px solid rgba(0, 0, 0, 0.23)",
                                                    marginRight: "4px",
                                                }}>
                                                <Button startIcon={<CloudUploadIcon fontSize='small'/>}>
                                                    {t("rewardHistory.file")}
                                                < /Button>
                                            </Tooltip>
                                        }
                                        previewButton={
                                            <Tooltip placement='top' title='Xem trước'>
                                                <Button
                                                    style={{
                                                        border: "1px solid rgba(0, 0, 0, 0.23)",
                                                        marginRight: "4px",
                                                    }}>
                                                    <VisibilityIcon className='mr-6'/>
                                                    Xem trước
                                                </Button>
                                            </Tooltip>
                                        }
                                        downloadButton={
                                            <Tooltip placement='top' title='Tải xuống'>
                                                <Button
                                                    style={{
                                                        border: "1px solid rgba(0, 0, 0, 0.23)",
                                                        marginRight: "4px",
                                                    }}>
                                                    <GetAppIcon className='mr-6'/>
                                                    Tải xuống
                                                </Button>
                                            </Tooltip>
                                        }
                                        deleteButton={
                                            <Tooltip placement='top' title='Xóa'>
                                                <Button
                                                    style={{
                                                        border: "1px solid rgba(0, 0, 0, 0.23)",
                                                        marginRight: "4px",
                                                    }}>
                                                    <DeleteIcon className='mr-6'/>
                                                    Xóa
                                                </Button>
                                            </Tooltip>
                                        }
                                    />
                                </Grid>

                            </Grid>
                        </DialogContent>

                        <DialogActions className='dialog-footer px-12'>
                            <div className="flex flex-space-between flex-middle">
                                <Button
                                    startIcon={<BlockIcon/>}
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
                                    startIcon={<SaveIcon/>}
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
})