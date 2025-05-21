import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import AddIcon from "@material-ui/icons/Add";
import { Form, Formik } from "formik";
import { Grid, Button, ButtonGroup, Tooltip } from "@material-ui/core";
import { useStore } from "app/stores";
import { useTranslation } from "react-i18next";
import GlobitsDateTimePicker from "app/common/form/GlobitsDateTimePicker";
import SearchIcon from "@material-ui/icons/Search";
import FilterListIcon from "@material-ui/icons/FilterList";
import AutorenewIcon from "@material-ui/icons/Autorenew";
import PublicHolidayDateFilter from "./PublicHolidayDateFilter";
import DeleteOutlineIcon from "@material-ui/icons/DeleteOutline";

function PublicHolidayDateIndexToolbar() {
    const { publicHolidayDateStore, hrRoleUtilsStore } = useStore();
    const { t } = useTranslation();

    const {
        handleDeleteList,
        pagingPublicHolidayDate,
        handleOpenCreateEdit,
        searchObject,
        listOnDelete,
        handleSetSearchObject,
        handleOpenPopupAutomatic,
    } = publicHolidayDateStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingPublicHolidayDate();
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

    const { isAdmin, isManager, isCompensationBenifit, checkAllUserRoles } = hrRoleUtilsStore;

    useEffect(() => {
        checkAllUserRoles();
    }, []);

    return (
        <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
            {({ resetForm, values, setFieldValue, setValues }) => {
                return (
                    <Form autoComplete='off'>
                        <Grid item xs={12}>
                            <Grid container spacing={2}>
                                <Grid item xs={12} lg={4}>
                                    {(isAdmin || isManager || isCompensationBenifit) && (
                                        <ButtonGroup color='container' aria-label='outlined primary button group'>
                                            <Tooltip
                                                placement='top'
                                                title='Tự động tạo các ngày nghỉ trong khoảng thời gian'>
                                                <Button
                                                    startIcon={<AutorenewIcon />}
                                                    onClick={() => handleOpenPopupAutomatic()}>
                                                    {t("Tự động tạo")}
                                                </Button>
                                            </Tooltip>

                                            <Button startIcon={<AddIcon />} onClick={() => handleOpenCreateEdit()}>
                                                {t("general.button.add")}
                                            </Button>

                                            <Button
                                                disabled={listOnDelete?.length === 0}
                                                startIcon={<DeleteOutlineIcon />}
                                                onClick={handleDeleteList}>
                                                {t("general.button.delete")}
                                            </Button>
                                        </ButtonGroup>
                                    )}
                                </Grid>

                                <Grid item xs={12} lg={8}>
                                    <div className='flex justify-between align-center'>
                                        <div className='flex flex-center w-100'>
                                            <Grid container spacing={2}>
                                                <Grid item xs={12} sm={6} md={4}>
                                                    <div className='flex items-center h-100 flex-end'>
                                                        <p className='no-wrap-text'>
                                                            <b>Từ ngày:</b>
                                                        </p>
                                                    </div>
                                                </Grid>

                                                <Grid item xs={12} sm={6} md={8}>
                                                    <GlobitsDateTimePicker
                                                        // label="Từ ngày"
                                                        name='fromDate'
                                                        // placeholder="Ngày từ"
                                                    />
                                                </Grid>
                                            </Grid>
                                        </div>

                                        <div className='flex flex-center w-100 mr-5'>
                                            <Grid container spacing={2}>
                                                <Grid item xs={12} sm={6} md={4}>
                                                    <div className='flex items-center h-100 flex-end'>
                                                        <p className='no-wrap-text'>
                                                            <b>Đến ngày:</b>
                                                        </p>
                                                    </div>
                                                </Grid>

                                                <Grid item xs={12} sm={6} md={8}>
                                                    <GlobitsDateTimePicker
                                                        // label="Đến ngày"
                                                        name='toDate'
                                                        // placeholder="Đến ngày"
                                                    />
                                                </Grid>
                                            </Grid>
                                        </div>
                                        {/* <Tooltip placement="top" title="Tìm kiếm theo ...">
                                            <GlobitsTextField
                                                placeholder="Tìm kiếm theo ..."
                                                name="keyword"
                                                variant="outlined"
                                                notDelay
                                            />
                                        </Tooltip> */}

                                        <ButtonGroup
                                            className='filterButtonV4'
                                            color='container'
                                            aria-label='outlined primary button group'>
                                            <Button
                                                startIcon={<SearchIcon className={``} />}
                                                className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                                type='submit'>
                                                Tìm kiếm
                                            </Button>
                                            <Button
                                                startIcon={
                                                    <FilterListIcon
                                                        className={` filterRotateIcon ${isOpenFilter && "onRotate"}`}
                                                    />
                                                }
                                                className=' d-inline-flex py-2 px-8 btnHrStyle'
                                                onClick={handleTogglePopupFilter}>
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>

                            <PublicHolidayDateFilter
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

export default memo(observer(PublicHolidayDateIndexToolbar));
