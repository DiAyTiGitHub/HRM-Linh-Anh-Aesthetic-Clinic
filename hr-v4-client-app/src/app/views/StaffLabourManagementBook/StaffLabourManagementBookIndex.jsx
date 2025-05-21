import { Button, ButtonGroup, Grid } from "@material-ui/core";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import React, { memo, useEffect } from "react";
import { useTranslation } from "react-i18next";
import GlobitsBreadcrumb from "app/common/GlobitsBreadcrumb";
import { Form, Formik } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import StaffLabourManagementBookList from "./StaffLabourManagementBookList";
import GlobitsSelectInput from "app/common/form/GlobitsSelectInput";
import LocalConstants from "app/LocalConstants";
import StaffLabourManagementBookToolbar from "./StaffLabourManagementBookToolbar";
import GlobitsColorfulThemePopup from "app/common/GlobitsColorfulThemePopup";

function StaffLabourManagementBookIndex() {
    const {
        staffLabourManagementBookStore,
        hrRoleUtilsStore

    } = useStore();

    const { t } = useTranslation();

    const {
        resetStore,
        pagingStaffLabourManagement,
        handleClose,
        exportLaborManagementBook,
        openConfirmExportLaborManagementBook
    } = staffLabourManagementBookStore;

    const {
        checkAllUserRoles,
        isAdmin,
        isManager

    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();

        pagingStaffLabourManagement();

        return resetStore;
    }, []);

    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        { name: "Sổ quản lý lao động" }
                    ]}
                />
            </div>

            <Grid className='index-card' container spacing={2}>
                <Grid item xs={12}>
                    <StaffLabourManagementBookToolbar />
                </Grid>

                <Grid item xs={12}>
                    <StaffLabourManagementBookList />
                </Grid>
            </Grid>

            {
                openConfirmExportLaborManagementBook && (
                    <GlobitsColorfulThemePopup
                        open={openConfirmExportLaborManagementBook}
                        handleClose={handleClose}
                        size={"sm"}
                        onConfirm={() => {
                            handleClose();
                            exportLaborManagementBook();
                        }}>
                        <ExportExcelConfirmWarningContent />
                    </GlobitsColorfulThemePopup>
                )
            }

        </div>
    );
}

export default memo(observer(StaffLabourManagementBookIndex));



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

