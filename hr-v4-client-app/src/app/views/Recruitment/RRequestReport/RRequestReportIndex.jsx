import { Grid } from "@material-ui/core";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import GlobitsColorfulThemePopup from "app/common/GlobitsColorfulThemePopup";
import RRequestReportToolbar from "./RRequestReportToolbar";
import RRequestReportList from "./RRequestReportList";
import RecruitmentRequestV2Form from "../RecruitmentRequestV2/RecruitmentRequestV2Form";
import GlobitsConfirmationDialog from "app/common/GlobitsConfirmationDialog";

function RRequestReportIndex() {
    const {
        rRequestReportStore,
        hrRoleUtilsStore,
        recruitmentRequestStore

    } = useStore();

    const { t } = useTranslation();

    const {
        resetStore,
        pagingRecruitmentRequestReport,
        handleClose,
        exportRecruitmentRequestReport,
        openConfirmexportRecruitmentRequestReport
    } = rRequestReportStore;

    const {
     
        handleClose: handleCloseInRecruitmentRequest,
        handleConfirmDeleteReport,
        openCreateEditPopup,
        openViewPopup,
        openConfirmDeletePopup
    } = recruitmentRequestStore;

    const {
        checkAllUserRoles,
        isAdmin,
        isManager

    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();

        pagingRecruitmentRequestReport();

        return resetStore;
    }, []);

    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: "Báo cáo theo yêu cầu tuyển dụng" }
                    ]}
                />
            </div>

            <Grid className='index-card' container spacing={2}>
                <Grid item xs={12}>
                    <RRequestReportToolbar />
                </Grid>

                <Grid item xs={12}>
                    <RRequestReportList />
                </Grid>
            </Grid>

            {
                openConfirmexportRecruitmentRequestReport && (
                    <GlobitsColorfulThemePopup
                        open={openConfirmexportRecruitmentRequestReport}
                        handleClose={handleClose}
                        size={"sm"}
                        onConfirm={() => {
                            handleClose();
                            exportRecruitmentRequestReport();
                        }}>
                        <ExportExcelConfirmWarningContent />
                    </GlobitsColorfulThemePopup>
                )
            }


            {openCreateEditPopup && <RecruitmentRequestV2Form />}
            {openViewPopup && <RecruitmentRequestV2Form readOnly={true} />}

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeletePopup}
                    onConfirmDialogClose={handleCloseInRecruitmentRequest}
                    onYesClick={async ()=>{
                        await handleConfirmDeleteReport()
                        await pagingRecruitmentRequestReport()
                    }}
                    title={t("confirm_dialog.delete.title")}
                    text={t("confirm_dialog.delete.text")}
                    agree={t("confirm_dialog.delete.agree")}
                    cancel={t("confirm_dialog.delete.cancel")}
                />
            )}

        </div>
    );
}

export default memo(observer(RRequestReportIndex));



function ExportExcelConfirmWarningContent() {
    return (
        <div className='dialogScrollContent'>
            <h6 className='text-red'>
                <strong>{`Lưu ý: `}</strong>
                Bạn đang thực hiện thao tác xuất danh sách nhân viên. Hệ thống sẽ truy xuất và xuất toàn bộ dữ liệu hiện có,
                <strong>{` có thể cần đến vài phút`}</strong>
                <br />
                <strong className='flex pt-6'>BẠN CÓ CHẮC MUỐN THỰC HIỆN HÀNH ĐỘNG NÀY?</strong>
            </h6>
        </div>
    );
}

