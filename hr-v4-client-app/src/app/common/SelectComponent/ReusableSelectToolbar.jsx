// ReusableSelectToolbar.js
import { observer } from "mobx-react";
import React, { memo, useState } from "react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SearchIcon from '@material-ui/icons/Search';
import { Form, Formik } from "formik";
import { Grid, Button, ButtonGroup, Tooltip, IconButton } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import CloseIcon from '@material-ui/icons/Close';

function ReusableSelectToolbar(props) {
    const { t } = useTranslation();

    const {
        // Data and methods
        searchObject = {},
        handleSetSearchObject = () => {},
        pagingData = () => {},

        // Search options
        searchPlaceholder = "Tìm kiếm...",
        searchFieldName = "keyword",
        searchTooltip = "Tìm kiếm",
        searchButtonText = "Tìm kiếm",

        // Filter options
        showFilter = false,
        filterComponent = null,
        filterButtonText = "Bộ lọc",

        // Custom content
        leftContent = null,
        rightContent = null,
        customSearchContent = null,
    } = props;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 0, // Reset về trang đầu khi tìm kiếm
            pageSize: searchObject.pageSize,
        };
        handleSetSearchObject(newSearchObject);
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
        <Formik enableReinitialize initialValues={searchObject} onSubmit={handleFilter}>
            {({ resetForm, values, setFieldValue, setValues }) => {
                return (
                    <Form autoComplete='off'>
                        <Grid container spacing={2}>
                            {leftContent && (
                                <Grid item xs={12} md={6}>
                                    {leftContent}
                                </Grid>
                            )}

                            <Grid item xs={12} md={leftContent ? 6 : 12}>
                                {customSearchContent ? (
                                    customSearchContent
                                ) : (
                                    <div className='flex justify-between align-center'>
                                        <Tooltip placement='top' title={searchTooltip}>
                                            <GlobitsTextField
                                                placeholder={searchPlaceholder}
                                                name={searchFieldName}
                                                variant='outlined'
                                                notDelay
                                                InputProps={{
                                                    endAdornment: values[searchFieldName] ? (
                                                        <IconButton
                                                            size='small'
                                                            onClick={() => {
                                                                setFieldValue(searchFieldName, "");
                                                                const newValues = { ...values, [searchFieldName]: "" };
                                                                setValues(newValues);
                                                                handleFilter(newValues);
                                                            }}>
                                                            <CloseIcon fontSize='small' />
                                                        </IconButton>
                                                    ) : null,
                                                }}
                                            />
                                        </Tooltip>

                                        <ButtonGroup
                                            className='filterButtonV4'
                                            color='container'
                                            aria-label='outlined primary button group'>
                                            <Button
                                                startIcon={<SearchIcon />}
                                                className='ml-8 d-inline-flex py-2 px-8 btnHrStyle'
                                                type='submit'>
                                                {searchButtonText}
                                            </Button>

                                            {showFilter && filterComponent && (
                                                <Button
                                                    startIcon={
                                                        <FilterListIcon
                                                            className={`filterRotateIcon ${isOpenFilter && "onRotate"}`}
                                                        />
                                                    }
                                                    className='d-inline-flex py-2 px-8 btnHrStyle'
                                                    onClick={handleTogglePopupFilter}>
                                                    {filterButtonText}
                                                </Button>
                                            )}
                                        </ButtonGroup>
                                    </div>
                                )}
                            </Grid>
                        </Grid>

                        {showFilter &&
                            filterComponent &&
                            isOpenFilter &&
                            React.cloneElement(filterComponent, {
                                isOpenFilter,
                                handleFilter,
                                handleCloseFilter,
                                values,
                                resetForm,
                                setFieldValue,
                            })}
                    </Form>
                );
            }}
        </Formik>
    );
}


export default memo(observer(ReusableSelectToolbar));