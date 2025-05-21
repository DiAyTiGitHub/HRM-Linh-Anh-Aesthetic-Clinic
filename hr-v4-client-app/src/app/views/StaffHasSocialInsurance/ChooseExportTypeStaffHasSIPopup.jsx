import React, {useState, useEffect, memo} from "react";
import {Formik, Form} from "formik";
import {DialogActions, Button, DialogContent, Grid} from "@material-ui/core";
import {useTranslation} from "react-i18next";
import {useStore} from "app/stores";
import {observer} from "mobx-react";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import * as Yup from "yup";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import GlobitsTable from "../../common/GlobitsTable";
import GlobitsColorfulThemePopup from "../../common/GlobitsColorfulThemePopup";

function ChooseExportTypeStaffHasSIPopup() {
    const {t} = useTranslation();

    const {
        staffHasSocialInsuranceStore
    } = useStore();

    const {
        handleClose,
        handleOpenChooseExportType,
        openChooseExportType,
        handleExportSOByType,
        handleChangeConfirmDownload,
        openConfirmDownloadPopup,
        exportType,
    } = staffHasSocialInsuranceStore;

    async function handleSaveForm(values) {
        await handleExportSOByType(values?.exportType);
    }

    const validationSchema = Yup.object({
        exportType: Yup.number().required(t("validation.required")).nullable(),
    });

    const [initialValues, setInitialValues] = useState(
        {
            exportType: null
        }
    );

    return (
        <GlobitsPopupV2
            scroll={"body"}
            size="xs"
            open={openChooseExportType}
            noDialogContent
            title={"Chọn excel nghiệp vụ BHXH"}
            onClosePopup={handleClose}
        >
            <Formik
                validationSchema={validationSchema}
                enableReinitialize
                initialValues={initialValues}
                onSubmit={handleSaveForm}  // Đảm bảo onSubmit nhận giá trị mới nhất
            >
                {({isSubmitting, values, setFieldValue}) => {
                    return (
                        <Form autoComplete="off">
                            <DialogContent className='o-hidden dialog-body p-12'>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <GlobitsSelectInput
                                            name="exportType"
                                            label="Loại xuất Excel"
                                            keyValue="value"
                                            options={LocalConstants.StaffHasSocialInsuranceExportType.getListData()}
                                            onChange={(event) => {
                                                setFieldValue("exportType", event.target.value); // Cập nhật giá trị exportType
                                            }}
                                        />
                                    </Grid>
                                </Grid>
                            </DialogContent>

                            <DialogActions className="dialog-footer px-12">
                                <div className="flex flex-space-between flex-middle">
                                    <Button
                                        variant="contained"
                                        className="mr-12 btn btn-secondary d-inline-flex"
                                        color="secondary"
                                        disabled={isSubmitting}
                                        onClick={handleClose}
                                    >
                                        {t("general.button.close")}
                                    </Button>

                                    <Button
                                        className="mr-0 btn d-inline-flex"
                                        variant="contained"
                                        disabled={isSubmitting}
                                        onClick={() => handleChangeConfirmDownload(true, values)}
                                    >
                                        Xuất Excel theo bộ lọc
                                    </Button>
                                    {/*<GlobitsConfirmationDialog*/}
                                    {/*    open={openConfirmDownloadPopup}*/}
                                    {/*    onConfirmDialogClose={handleClose}*/}
                                    {/*    onYesClick={() => handleSaveForm(exportType)}*/}
                                    {/*    title={t("confirm_dialog.confirm_download_excel.title")}*/}
                                    {/*    text={t("confirm_dialog.confirm_download_excel.text")}*/}
                                    {/*    agree={t("confirm_dialog.confirm_download_excel.agree")}*/}
                                    {/*    cancel={t("confirm_dialog.confirm_download_excel.cancel")}*/}
                                    {/*/>*/}

                                    <GlobitsColorfulThemePopup
                                        open={openConfirmDownloadPopup}
                                        handleClose={handleClose}
                                        size={"sm"}
                                        onConfirm={() => handleSaveForm(exportType)}
                                    >
                                        <ExportExcelConfirmWarningContent/>
                                    </GlobitsColorfulThemePopup>
                                </div>
                            </DialogActions>
                        </Form>
                    );
                }}
            </Formik>
        </GlobitsPopupV2>
    );
}

export default memo(observer(ChooseExportTypeStaffHasSIPopup));


function ExportExcelConfirmWarningContent() {
    return (
        <div className="dialogScrollContent">
            <h6 className="text-red">
                <strong>
                    {`Lưu ý: `}
                </strong>
                Bạn đang thực hiện hành động xuất danh sách dữ liệu theo bộ lọc, hành động này sẽ lấy dữ liệu của tất
                cả dữ liệu theo bộ lọc và
                <strong>
                    {` có thể cần đến vài phút`}
                </strong>
                <br/>
                <strong className='flex pt-6'>BẠN CÓ CHẮC MUỐN THỰC HIỆN HÀNH ĐỘNG NÀY?</strong>
            </h6>
        </div>
    );
}