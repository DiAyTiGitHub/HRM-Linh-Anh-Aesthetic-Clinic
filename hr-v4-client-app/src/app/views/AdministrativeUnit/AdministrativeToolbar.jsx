import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import AddIcon from "@material-ui/icons/Add";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import StaffPositionFilter from "./AdministrativeFilter";
import DeleteIcon from "@material-ui/icons/Delete";
import { useTheme } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import CloudUploadIcon from "@material-ui/icons/CloudUpload";
import GetAppIcon from "@material-ui/icons/GetApp";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";

function AdministrativeToolbar() {
    const { administrativeUnitStore } = useStore();
    const { t } = useTranslation();
    const {
        searchObject,
        search,
        handleSetSearchObject,
        handleEditAdministrative,
        handleDeleteList,
        selectedAdministrativeUnitList,
        uploadFileExcel,
        handleDownloadAdministrativeUnitTemplate,
        handleExportAdministrativeUnit
    } = administrativeUnitStore;
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

    async function handleFilter(values) {
        const newSearchObject = {
            ...values, pageIndex: 1, pageSize: searchObject.pageSize,
        }
        handleSetSearchObject(newSearchObject);
        await search();
    }

    const [isOpenFilter, setIsOpenFilter] = useState(false);

    function handleCloseFilter() {
        if (isOpenFilter) {
            setIsOpenFilter(false);
        }
    }

    function handleOpenFilter() {
        if (!isOpenFilter) {
            setIsOpenFilter(true);
        }
    }

    function handleTogglePopupFilter() {
        if (isOpenFilter) handleCloseFilter(); else handleOpenFilter();
    }

    return (<Formik
        enableReinitialize
        initialValues={searchObject}
        onSubmit={handleFilter}
    >
        {({ resetForm, values, setFieldValue, setValues }) => {

            return (<Form autoComplete="off">
                <Grid item xs={12}>
                    <Grid container spacing={2}>
                        <Grid item xs={12} xl={6}>
                            <ButtonGroup
                                color="container"
                                aria-label="outlined primary button group"
                            >
                                <Button
                                    startIcon={<AddIcon />}
                                    onClick={() => handleEditAdministrative()}
                                >
                                    {t("general.button.add")}
                                </Button>
                                <Button
                                    startIcon={<GetAppIcon />}
                                    onClick={handleDownloadAdministrativeUnitTemplate}
                                >
                                    Tải mẫu nhập
                                </Button>

                                <Tooltip
                                    placement="top"
                                    title={"Tải xuống Excel danh sách đơn vị hành chính theo bộ lọc"}
                                    arrow
                                >
                                    <Button
                                        startIcon={<CloudDownloadIcon />}
                                        onClick={() => handleExportAdministrativeUnit()}
                                    >
                                        Xuất Excel
                                    </Button>
                                </Tooltip>

                                <Tooltip
                                    placement="top"
                                    title={"Nhập dữ liệu đơn vị hành chính từ mẫu"}
                                    arrow
                                >
                                    <Button
                                        startIcon={<CloudUploadIcon />}
                                        onClick={() => document.getElementById("fileExcel").click()}
                                    >
                                        {t("general.button.importExcel")}
                                    </Button>
                                </Tooltip>

                                <Button
                                    startIcon={<DeleteIcon />}
                                    onClick={handleDeleteList}
                                    disabled={!selectedAdministrativeUnitList?.length > 0}
                                >
                                    {t("general.button.delete")}
                                </Button>
                            </ButtonGroup>
                            <input
                                type="file"
                                id="fileExcel"
                                style={{ display: "none" }}
                                onChange={uploadFileExcel}
                            />
                        </Grid>

                        <Grid item xs={12} xl={6}>
                            <div className="flex justify-between align-center">
                                <Tooltip placement="top" title="Tìm kiếm theo tên đơn vị hành chính">
                                    <GlobitsTextField
                                        placeholder="Tìm kiếm theo tên đơn vị hành chính..."
                                        name="keyword"
                                        variant="outlined"
                                        notDelay
                                    />
                                </Tooltip>

                                <ButtonGroup
                                    className="filterButtonV4"
                                    color="container"
                                    aria-label="outlined primary button group"
                                >
                                    <Button
                                        startIcon={<SearchIcon className={``} />}
                                        className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                        type="submit"
                                    >
                                        Tìm kiếm
                                    </Button>
                                    <Button
                                        startIcon={<FilterListIcon
                                            className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                                        className=" d-inline-flex py-2 px-8 btnHrStyle"
                                        onClick={handleTogglePopupFilter}
                                    >
                                        Bộ lọc
                                    </Button>
                                </ButtonGroup>
                            </div>
                        </Grid>
                    </Grid>

                    <StaffPositionFilter
                        isOpenFilter={isOpenFilter}
                        handleFilter={handleFilter}
                        handleCloseFilter={handleCloseFilter}
                    />
                </Grid>
            </Form>);
        }}
    </Formik>);
}

export default memo(observer(AdministrativeToolbar));