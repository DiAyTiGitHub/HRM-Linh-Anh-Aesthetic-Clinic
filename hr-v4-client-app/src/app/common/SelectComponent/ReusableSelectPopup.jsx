// ReusableSelectPopup.js
import {
    Button,
    ButtonGroup,
    Checkbox,
    DialogActions,
    DialogContent,
    Grid,
    IconButton,
    Radio,
    Tooltip,
} from "@material-ui/core";
import BlockIcon from "@material-ui/icons/Block";
import SaveIcon from "@material-ui/icons/Save";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import React, { memo, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import GlobitsTextField from "../form/GlobitsTextField";
import GlobitsTable from "../GlobitsTable";

import CloseIcon from "@material-ui/icons/Close";
import FilterListIcon from "@material-ui/icons/FilterList";
import SearchIcon from "@material-ui/icons/Search";

function ReusableSelectPopup(props) {
    const { t } = useTranslation();

    const {
        multipleSelect = false,
        // Popup props
        open,
        onClose,
        onConfirm,
        title = "Chọn mục",
        size = "md",
        scroll = "body",
        confirmText = t("general.button.confirm"),
        cancelText = t("general.button.cancel"),
        isDisabled = false,

        // Data props
        store,
        storeConfig = {},
        initialSelectedItems = [], // Items đã chọn trước đó

        // Custom components
        customToolbar,
        customList,

        // Additional props
        toolbarProps = {},
        listProps = {},
        onInit = () => {},
    } = props;

    // Extract store configuration with defaults
    const { pagingMethod = "searchByPage" } = storeConfig;

    // Local states
    const [listData, setListData] = useState([]);
    const [selectedItems, setSelectedItems] = useState(initialSelectedItems || []);
    const [searchObject, setSearchObject] = useState({
        keyword: "",
        pageIndex: 0,
        pageSize: 10,
    });
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    // Extract pagingMethod from store
    const pagingData = store[pagingMethod];

    // Define handler functions
    const handleChangePage = (event, newPage) => {
        setSearchObject({
            ...searchObject,
            pageIndex: newPage,
        });
    };

    const setPageSize = (event) => {
        const newSize = parseInt(event.target.value, 10);
        setSearchObject({
            ...searchObject,
            pageSize: newSize,
            pageIndex: 0, // Reset về trang đầu tiên khi thay đổi pageSize
        });
    };

    const handleSelectItems = (items) => {
        setSelectedItems(items);
    };

    const handleSetSearchObject = (newSearchObject) => {
        setSearchObject(newSearchObject);
    };

    // Hàm để thực hiện tìm kiếm
    const performSearch = async () => {
        try {
            console.log("performSearch: ");
            if (typeof pagingData === "function") {
                const result = await pagingData(searchObject);
                if (result) {
                    // Cập nhật state với kết quả từ API
                    if (Array.isArray(result.content)) {
                        setListData(result.content);
                    } else if (result.data && Array.isArray(result.data.content)) {
                        setListData(result.data.content);
                    } else if (Array.isArray(result.data)) {
                        setListData(result.data);
                    }

                    setTotalElements(result.totalElements || result.data?.totalElements || 0);
                    setTotalPages(result.totalPages || result.data?.totalPages || 0);
                }
            }
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    };

    // Initialize component
    useEffect(() => {
        performSearch();
        onInit();
    }, [searchObject.pageIndex, searchObject.pageSize]);

    // Search when keyword changes
    useEffect(() => {
        if (searchObject.pageIndex === 0) {
            performSearch();
        } else {
            // Reset về trang đầu tiên khi thay đổi keyword
            setSearchObject({
                ...searchObject,
                pageIndex: 0,
            });
        }
    }, [searchObject.keyword]);

    // Handle confirm action
    const handleConfirm = () => {
        if (typeof onConfirm === "function") {
            console.log(selectedItems);
            onConfirm(selectedItems);
        }
    };

    return (
        <GlobitsPopupV2 scroll={scroll} size={size} open={open} noDialogContent title={title} onClosePopup={onClose}>
            <>
                <div className='dialog-body'>
                    <DialogContent className='p-12'>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                {customToolbar ? (
                                    customToolbar
                                ) : (
                                    <ReusableSelectToolbar
                                        searchObject={searchObject}
                                        handleSetSearchObject={handleSetSearchObject}
                                        pagingData={performSearch}
                                        {...toolbarProps}
                                    />
                                )}
                            </Grid>
                            <Grid item xs={12}>
                                {customList ? (
                                    customList
                                ) : (
                                    <ReusableSelectList
                                        multipleSelect={multipleSelect}
                                        data={listData}
                                        selectedItems={selectedItems}
                                        handleSelectItems={handleSelectItems}
                                        totalPages={totalPages}
                                        totalElements={totalElements}
                                        searchObject={searchObject}
                                        handleChangePage={handleChangePage}
                                        setPageSize={setPageSize}
                                        pageSize={searchObject.pageSize}
                                        page={searchObject.pageIndex}
                                        {...listProps}
                                    />
                                )}
                            </Grid>
                        </Grid>
                    </DialogContent>
                </div>

                <div className='dialog-footer dialog-footer-v2 py-8'>
                    <DialogActions className='p-0'>
                        <div className='flex flex-space-between flex-middle'>
                            <Button
                                startIcon={<BlockIcon />}
                                variant='contained'
                                className='mr-12 btn btn-secondary d-inline-flex'
                                color='secondary'
                                onClick={onClose}>
                                {cancelText}
                            </Button>
                            <Button
                                startIcon={<SaveIcon />}
                                className='mr-0 btn bgc-lighter-dark-blue d-inline-flex'
                                variant='contained'
                                color='primary'
                                disabled={isDisabled}
                                onClick={handleConfirm}>
                                {confirmText}
                            </Button>
                        </div>
                    </DialogActions>
                </div>
            </>
        </GlobitsPopupV2>
    );
}

export default memo(observer(ReusableSelectPopup));

const ReusableSelectList = (props) => {
    const { t } = useTranslation();

    const {
        multipleSelect = false,
        // Data
        data = [],
        selectedItems = [],

        // Pagination
        totalPages = 0,
        totalElements = 0,
        handleChangePage = () => {},
        setPageSize = () => {},
        pageSize = 10,
        page = 0,
        pageSizeOption = [10, 15, 25, 50, 100],

        // Selection
        handleSelectItems = () => {},
        idField = "id",

        // Columns
        columns = [],
        showCheckboxColumn = true,
        checkboxColumnTitle = "Lựa chọn",
        selectLabel = "Chọn",
        unselectLabel = "Bỏ chọn",
    } = props;

    function handleSelectItem(item) {
        console.log("handleSelectItem called with item:", item);
        console.log("multipleSelect value:", multipleSelect);
        console.log("Current selectedItems:", selectedItems);
        let newSelectedItems = [];

        if (multipleSelect) {
            // Chế độ chọn nhiều (checkbox)
            newSelectedItems = [...selectedItems];
            const isSelected = selectedItems.some((selectedItem) => selectedItem[idField] === item[idField]);

            if (isSelected) {
                newSelectedItems = selectedItems.filter((selectedItem) => selectedItem[idField] !== item[idField]);
            } else {
                newSelectedItems.push(item);
            }
        } else {
            // Chế độ chọn đơn (radio)
            newSelectedItems = [item]; // Chỉ giữ lại item mới chọn
        }

        handleSelectItems(newSelectedItems);
    }

    const selectionColumn = {
        title: checkboxColumnTitle,
        sorting: false,
        align: "center",
        width: "10%",
        cellStyle: {
            textAlign: "center",
        },
        render: (rowData) => {
            const isChecked = selectedItems?.some((item) => item[idField] === rowData[idField]);

            if (multipleSelect) {
                // Hiển thị checkbox nếu là chế độ chọn nhiều
                return (
                    <Tooltip title={isChecked ? unselectLabel : selectLabel} placement='top'>
                        <Checkbox
                            className='pr-16'
                            id={`checkbox-${rowData[idField]}`}
                            name='selected'
                            value={rowData[idField]}
                            checked={isChecked}
                            onClick={() => handleSelectItem(rowData)}
                        />
                    </Tooltip>
                );
            } else {
                // Hiển thị radio button nếu là chế độ chọn đơn
                return (
                    <Tooltip title={isChecked ? unselectLabel : selectLabel} placement='top'>
                        <Radio
                            className='pr-16'
                            id={`radio-${rowData[idField]}`}
                            name='selected'
                            value={rowData[idField]}
                            checked={isChecked}
                            onClick={() => handleSelectItem(rowData)}
                        />
                    </Tooltip>
                );
            }
        },
    };

    const tableColumns = showCheckboxColumn ? [selectionColumn, ...columns] : columns;

    return (
        <GlobitsTable
            data={data}
            columns={tableColumns}
            totalPages={totalPages}
            handleChangePage={handleChangePage}
            setRowsPerPage={setPageSize}
            pageSize={pageSize}
            pageSizeOption={pageSizeOption}
            totalElements={totalElements}
            page={page}
        />
    );
};

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
