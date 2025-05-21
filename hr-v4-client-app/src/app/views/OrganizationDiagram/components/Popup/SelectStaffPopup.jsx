import React, { memo, useState, useEffect } from "react";
import { useStore } from "app/stores";
import { observer } from "mobx-react";
import { Grid, Tooltip } from "@material-ui/core";
import { useTranslation } from "react-i18next";
import { Form, Formik, useFormikContext } from "formik";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { IconButton } from "@material-ui/core";
import Search from "@material-ui/icons/Search";
import GlobitsTable from "app/common/GlobitsTable";
import { formatDate } from "app/LocalFunction";
import GlobitsPopupV2 from "app/common/GlobitsPopupV2";
import { Radio } from "@material-ui/core";

function SelectStaffPopup(props) {
    const { open, handleClose, handleSelect } = props;

    const { t } = useTranslation();

    const { userStore } = useStore();
    const {
        pagingStaff,
        listUsingStaff,
        resetUsingStaffSection,
        setPageSize,
        usingStaffSO,
        totalStaffElements,
        totalStaffPages,
        handleChangeStaffPage,
        handleSetUsingStaffSO,
    } = userStore;

    const { values, setFieldValue } = useFormikContext();

    useEffect(
        function () {
            if (open) {
                pagingStaff();
            }

            return resetUsingStaffSection;
        },
        [open]
    );

    function handleSelectUsingStaff(chosenStaff) {
        setFieldValue("staff", chosenStaff);
        handleSelect(chosenStaff);
        handleClose();
    }

    const columns = [
        {
            title: t("general.popup.select"),
            align: "center",
            cellStyle: {
                textAlign: "center",
            },
            width: "48px",
            render: (rowData) => (
                <Tooltip title='Chọn sử dụng' placement='top'>
                    <Radio
                        className='pr-16'
                        id={`radio${rowData?.id}`}
                        name='radSelected'
                        value={rowData.id}
                        checked={values?.staff?.id === rowData?.id}
                        onClick={(event) => handleSelectUsingStaff(rowData)}
                    />
                </Tooltip>
            ),
        },
        {
            title: "Mã",
            field: "staffCode",
            align: "left",
            cellStyle: {
                textAlign: "left",
            },
        },
        {
            title: "Tên nhân viên",
            field: "displayName",
            align: "left",
            cellStyle: {
                textAlign: "left",
            },
        },
        {
            title: "Ngày sinh",
            field: "birthDate",
            render: (value) => value?.birthDate && <span>{formatDate("DD/MM/YYYY", value?.birthDate)}</span>,
        },
    ];

    async function handleSearchUsingStaff({ keyword }) {
        const searchObject = {
            ...usingStaffSO,
            keyword,
            pageIndex: 1,
            organization: null,
        };

        handleSetUsingStaffSO(searchObject);
        setInitialValues(searchObject);
        await pagingStaff();
    }

    const [initialValues, setInitialValues] = useState({ keyword: null });

    return (
        <GlobitsPopupV2
            noDialogContent
            popupId={"chooseUsingStaffPopup"}
            open={open}
            title='Danh sách nhân viên'
            size='md'
            scroll={"body"}
            onClosePopup={handleClose}>
            <Grid container className='p-12'>
                <Grid item xs={12}>
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={6} lg={8}></Grid>

                        <Grid item xs={12} sm={6} lg={4}>
                            <Formik enableReinitialize initialValues={initialValues} onSubmit={handleSearchUsingStaff}>
                                {({ resetForm, values, setFieldValue, setValues }) => {
                                    return (
                                        <Form autoComplete='off'>
                                            <GlobitsTextField
                                                placeholder='Tìm kiếm nhân viên'
                                                name='keyword'
                                                variant='outlined'
                                                notDelay
                                                InputProps={{
                                                    endAdornment: (
                                                        <IconButton
                                                            className='py-0 px-4'
                                                            aria-label='search'
                                                            type='submit'>
                                                            <Search />
                                                        </IconButton>
                                                    ),
                                                }}
                                            />
                                        </Form>
                                    );
                                }}
                            </Formik>
                        </Grid>
                    </Grid>
                </Grid>

                <Grid item xs={12} className='pt-12'>
                    <GlobitsTable
                        data={listUsingStaff}
                        columns={columns}
                        totalPages={totalStaffPages}
                        handleChangePage={handleChangeStaffPage}
                        setRowsPerPage={setPageSize}
                        pageSize={usingStaffSO?.pageSize}
                        pageSizeOption={[10, 25, 50]}
                        totalElements={totalStaffElements}
                        page={usingStaffSO?.pageIndex}
                    />
                </Grid>
            </Grid>
        </GlobitsPopupV2>
    );
}

export default memo(observer(SelectStaffPopup));
