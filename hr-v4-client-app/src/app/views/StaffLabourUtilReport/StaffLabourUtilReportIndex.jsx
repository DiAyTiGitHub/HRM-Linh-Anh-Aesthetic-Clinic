import { Button, ButtonGroup, Grid } from "@material-ui/core";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { Form, Formik } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import StaffLabourUtilReportList from "./StaffLabourUtilReportList";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import StaffLabourUtilReportToolbar from "./StaffLabourUtilReportToolbar";
import GlobitsColorfulThemePopup from "app/common/GlobitsColorfulThemePopup";

function StaffLabourUtilReportIndex() {
    const {
        staffLabourUtilReportStore,
        hrRoleUtilsStore

    } = useStore();

    const { t } = useTranslation();

    const {
        resetStore,
        pagingStaffLabourUtilReport,
        handleClose,
        exportStaffLabourUtilReport,
        openConfirmExportStaffLabourUtilReport
    } = staffLabourUtilReportStore;

    const {
        checkAllUserRoles,
        isAdmin,
        isManager

    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();

        pagingStaffLabourUtilReport();

        return resetStore;
    }, []);

    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: "Pháp chế" },
                        { name: "Báo cáo tình hình sử dụng lao động" }
                    ]}
                />
            </div>

            <Grid className='index-card' container spacing={2}>
                <Grid item xs={12}>
                    <StaffLabourUtilReportToolbar />
                </Grid>

                <Grid item xs={12}>
                    <StaffLabourUtilReportList />
                </Grid>
            </Grid>

            {
                openConfirmExportStaffLabourUtilReport && (
                    <GlobitsColorfulThemePopup
                        open={openConfirmExportStaffLabourUtilReport}
                        handleClose={handleClose}
                        size={"sm"}
                        onConfirm={() => {
                            handleClose();
                            exportStaffLabourUtilReport();
                        }}>
                        <ExportExcelConfirmWarningContent />
                    </GlobitsColorfulThemePopup>
                )
            }

        </div>
    );
}

export default memo(observer(StaffLabourUtilReportIndex));



function ExportExcelConfirmWarningContent() {
    return (
        <div className='dialogScrollContent'>
            <h6 className='text-red'>
                <strong>{`Lưu ý: `}</strong>
                Bạn đang thực hiện thao tác xuất danh sách nhân viên. Hệ thống sẽ truy xuất và xuất toàn bộ dữ liệu nhân
                viên hiện có,
                <strong>{` có thể cần đến vài phút`}</strong>
                <br />
                <strong className='flex pt-6'>BẠN CÓ CHẮC MUỐN THỰC HIỆN HÀNH ĐỘNG NÀY?</strong>
            </h6>
        </div>
    );
}

