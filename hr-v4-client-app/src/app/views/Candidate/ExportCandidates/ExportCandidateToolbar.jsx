import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";
import { useHistory } from "react-router-dom";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Grid, Button, IconButton, Tabs, Tab, AppBar, makeStyles, ButtonGroup, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import ExportCandidateFilter from "./ExportCandidateFilter";
import PublishIcon from '@material-ui/icons/Publish';

function ExportCandidateToolbar() {
    const history = useHistory();
    const { exportCandidateStore } = useStore();
    const { t } = useTranslation();

    const {
        pagingExportCandidate,
        searchObject,
        listChosen,
        handleSetSearchObject,
        handleDeleteList,
        handleOpenConfirmExportExcel
    } = exportCandidateStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
            organizationId: values?.organization?.id,
        };

        handleSetSearchObject(newSearchObject);
        await pagingExportCandidate();
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
        if (isOpenFilter) handleCloseFilter();
        else handleOpenFilter();
    }

    return (
        <Formik
            enableReinitialize
            initialValues={searchObject}
            onSubmit={handleFilter}
        >
            {({ resetForm, values, setFieldValue, setValues }) => {

                return (
                    <Form autoComplete="off">
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={6}>
                                    <ButtonGroup
                                        color="container"
                                        aria-label="outlined primary button group"
                                    >
                                        <Button
                                            startIcon={<PublishIcon />}
                                            onClick={handleOpenConfirmExportExcel}
                                        >
                                            Xuất danh sách
                                        </Button>

                                        <Button
                                            disabled={listChosen?.length <= 0}
                                            startIcon={<DeleteOutlineIcon />}
                                            onClick={handleDeleteList}
                                        >
                                            {t("general.button.delete")}
                                        </Button>
                                    </ButtonGroup>
                                </Grid>

                                <Grid item xs={12} md={6}>
                                    <div className="flex justify-between align-center">
                                        <Tooltip placement="top" title="Tìm kiếm theo mã, tên ứng viên">
                                            <GlobitsTextField
                                                placeholder="Tìm kiếm theo mã, tên ứng viên..."
                                                name="keyword"
                                                variant="outlined"
                                                notDelay
                                            // InputProps={{
                                            //     endAdornment: (
                                            //         <IconButton className="py-0 px-4" aria-label="search" type="submit">
                                            //             <SearchIcon />
                                            //         </IconButton>
                                            //     ),
                                            // }}
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
                                                startIcon={<FilterListIcon className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`} />}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>

                            <ExportCandidateFilter
                                isOpenFilter={isOpenFilter}
                                handleFilter={handleFilter}
                                handleCloseFilter={handleCloseFilter}
                            />
                        </Grid>
                    </Form>
                );
            }}
        </Formik>
    );
}

export default memo(observer(ExportCandidateToolbar));