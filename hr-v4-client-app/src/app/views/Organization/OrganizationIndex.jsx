import { observer } from "mobx-react";
import React , { memo , useEffect } from "react";
import { useStore } from "../../stores";
import { Grid , Button , IconButton , ButtonGroup , Tooltip } from "@material-ui/core";
import GlobitsBreadcrumb from "../../common/GlobitsBreadcrumb";
import DeleteIcon from "@material-ui/icons/Delete";
import AddIcon from "@material-ui/icons/Add";
import { useTranslation } from "react-i18next";
import GlobitsConfirmationDialog from "../../common/GlobitsConfirmationDialog";
import SearchIcon from "@material-ui/icons/Search";
import { Form , Formik } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import OrganizationCUForm from "./OrganizationCUForm";
import OrganizationList from "./OrganizationList";
import GetAppIcon from "@material-ui/icons/GetApp";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";
import GlobitsCheckBox from "../../common/form/GlobitsCheckBox";
import { OrganizationType } from "../../LocalConstants";
import GlobitsSelectInput from "../../common/form/GlobitsSelectInput";
import GlobitsDateTimePicker from "../../common/form/GlobitsDateTimePicker";

function OrganizationIndex() {
    const {organizationStore , hrRoleUtilsStore} = useStore();
    const {t} = useTranslation();

    const {
        handleDeleteList ,
        pagingOrganization ,
        handleOpenCreateEdit ,
        openConfirmDeleteListPopup ,
        openConfirmDeletePopup ,
        openCreateEditPopup ,
        handleClose ,
        handleConfirmDelete ,
        handleConfirmDeleteList ,
        searchObject ,
        listOnDelete ,
        resetStore ,
        handleSetSearchObject ,
        handleDownloadOrganizationTemplate ,
        uploadFileExcel ,
        handlExportExcelOrgData ,
        openViewPopup
    } = organizationStore;

    const {
        checkAllUserRoles
    } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
        pagingOrganization();
        return resetStore;
    } , []);

    async function handleFilter(values) {
        const newSearchObject = {
            ... values ,
            pageIndex:1 ,
        };
        handleSetSearchObject(newSearchObject);
        await pagingOrganization();
    }

    const {isAdmin, isManager} = useStore().hrRoleUtilsStore;
    return (
        <div className='content-index'>
            <div className='index-breadcrumb py-6'>
                <GlobitsBreadcrumb
                    routeSegments={[
                        {name:t("navigation.organization.title")} ,
                        {name:t("navigation.organizationalDirectory.title")} ,
                        {name:t("navigation.organizationalUnits.title")} ,
                    ]}
                />
            </div>
            <Grid className='index-card' container spacing={2}>
                <Grid item xs={12}>
                    <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
                        {({resetForm , values , setFieldValue , setValues}) => {
                            return (
                                <Form autoComplete='off'>
                                    <div className='mb-12'>
                                        <Grid container spacing={2} className='align-center mainBarFilter'>

                                            <Grid item xs={12} lg={6}>
                                                {(isAdmin || isManager) && (
                                                    <ButtonGroup
                                                        color="container"
                                                        aria-label="outlined primary button group"
                                                    >
                                                        <Tooltip placement="top" title={"Thêm mới đơn vị"} arrow>
                                                            <Button
                                                                startIcon={<AddIcon/>}
                                                                onClick={() => handleOpenCreateEdit()}
                                                            >
                                                                {t("general.button.add")}
                                                            </Button>
                                                        </Tooltip>

                                                        <Tooltip
                                                            placement="top"
                                                            title={"Tải mẫu nhập danh sách đơn vị"}
                                                            arrow
                                                        >
                                                            <Button
                                                                startIcon={<GetAppIcon/>}
                                                                onClick={handleDownloadOrganizationTemplate}
                                                            >
                                                                Tải mẫu nhập
                                                            </Button>
                                                        </Tooltip>

                                                        <Tooltip
                                                            placement="top"
                                                            title={"Nhập danh sách đơn vị"}
                                                            arrow
                                                        >
                                                            <Button
                                                                startIcon={<CloudUploadIcon/>}
                                                                onClick={() => document.getElementById("fileExcel").click()}
                                                            >
                                                                {t("general.button.importExcel")}
                                                            </Button>
                                                        </Tooltip>

                                                        <Tooltip
                                                            placement="top"
                                                            title={"Tải xuống Excel danh sách đơn vị theo bộ lọc"}
                                                            arrow
                                                        >
                                                            <Button
                                                                startIcon={<CloudDownloadIcon/>}
                                                                onClick={() => handlExportExcelOrgData()}
                                                            >
                                                                Xuất Excel
                                                            </Button>
                                                        </Tooltip>


                                                        <Tooltip
                                                            placement="top"
                                                            title={"Xóa đơn vị đã chọn"}
                                                            arrow
                                                        >
                                                            <Button
                                                                disabled={listOnDelete?.length <= 0}
                                                                startIcon={<DeleteIcon/>}
                                                                onClick={handleDeleteList}
                                                            >
                                                                {t("general.button.delete")}
                                                            </Button>
                                                        </Tooltip>
                                                    </ButtonGroup>
                                                )}
                                                <input
                                                    type="file"
                                                    id="fileExcel"
                                                    style={{display:"none"}}
                                                    onChange={uploadFileExcel}
                                                />
                                            </Grid>

                                            <Grid item xs={12} lg={6}>
                                                <Grid container spacing={2}>
                                                    <Grid item xs={4}>
                                                        <div className='flex justify-end align-center'>
                                                            <div className='flex flex-center w-100'>
                                                                <Grid container spacing={2}>
                                                                    <Grid item xs={12} sm={6} md={4}>
                                                                        <div
                                                                            className='flex items-center h-100 flex-end'>
                                                                            <p className='no-wrap-text'>
                                                                                <b>Loại đơn vị</b>
                                                                            </p>
                                                                        </div>
                                                                    </Grid>

                                                                    <Grid item xs={12} sm={6} md={8}>
                                                                        <GlobitsSelectInput
                                                                            name='organizationType'
                                                                            keyValue={"value"}
                                                                            options={OrganizationType.getListData()}
                                                                        />
                                                                    </Grid>
                                                                </Grid>
                                                            </div>
                                                        </div>

                                                    </Grid>
                                                    <Grid item xs={8}>
                                                        <div className="flex justify-between align-center">
                                                            <GlobitsTextField
                                                                placeholder="Tìm kiếm theo từ khóa"
                                                                name="keyword"
                                                                variant="outlined"
                                                                notDelay
                                                            />
                                                            <ButtonGroup
                                                                className="filterButtonV4"
                                                                color="container"
                                                                aria-label="outlined primary button group"
                                                            >
                                                                <Button
                                                                    startIcon={<SearchIcon/>}
                                                                    className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                                    type="submit"
                                                                >
                                                                    Tìm kiếm
                                                                </Button>
                                                            </ButtonGroup>
                                                        </div>
                                                    </Grid>
                                                </Grid>
                                            </Grid>
                                        </Grid>

                                    </div>
                                </Form>
                            );
                        }}
                    </Formik>
                </Grid>

                <Grid item xs={12}>
                    <OrganizationList/>
                </Grid>
            </Grid>

            {openCreateEditPopup && (
                <OrganizationCUForm/>
            )}
            {openViewPopup && (
                <OrganizationCUForm readOnly={true}/>
            )}

            {openConfirmDeletePopup && (
                <GlobitsConfirmationDialog open={openConfirmDeletePopup} onConfirmDialogClose={handleClose}
                                           onYesClick={handleConfirmDelete} title={t("confirm_dialog.delete.title")}
                                           text={t("confirm_dialog.delete.text")}
                                           agree={t("confirm_dialog.delete.agree")}
                                           cancel={t("confirm_dialog.delete.cancel")}/>
            )}

            {openConfirmDeleteListPopup && (
                <GlobitsConfirmationDialog
                    open={openConfirmDeleteListPopup}
                    onConfirmDialogClose={handleClose}
                    onYesClick={handleConfirmDeleteList}
                    title={t("confirm_dialog.delete_list.title")}
                    text={t("confirm_dialog.delete_list.text")}
                    agree={t("confirm_dialog.delete_list.agree")}
                    cancel={t("confirm_dialog.delete_list.cancel")}
                />
            )}
        </div>
    );
}

export default memo(observer(OrganizationIndex));
