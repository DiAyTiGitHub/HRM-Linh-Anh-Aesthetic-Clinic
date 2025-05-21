import {observer} from "mobx-react";
import React, {memo, useState} from "react";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import SearchIcon from '@material-ui/icons/Search';
import {Form, Formik} from "formik";
import {Grid, Button, ButtonGroup, Tooltip} from "@material-ui/core";
import {useStore} from "app/stores";
import {useTranslation} from "react-i18next";
import FilterListIcon from '@material-ui/icons/FilterList';
import SalaryStaffsFilter from "./PopupStaffFilter";
import SalaryStaffsList from "./PopupStaffList";
import SaveOutlinedIcon from "@material-ui/icons/SaveOutlined";
import BlockIcon from "@material-ui/icons/Block";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";

function PopupStaffToolbar() {
    const {popupStaffStore} = useStore();
    const {t} = useTranslation();

    const {
        searchObject,
        pagingStaff,
        handleSetSearchObject,
    } = popupStaffStore;

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
        await pagingStaff();
    }

    const [isOpenFilter, setIsOpenFilter] = useState(true);

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
            {({resetForm, values, isSubmitting, setFieldValue, setValues}) => {

                return (
                    <Form autoComplete="off">
                        <Grid item xs={12} className={"pb-10"}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <div className="flex justify-between align-center">
                                        <Tooltip placement="top" title="Tìm kiếm theo từ khóa...">
                                            <GlobitsTextField
                                                placeholder="Tìm kiếm theo từ khóa..."
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
                                                startIcon={<SearchIcon className={``}/>}
                                                className="ml-8 d-inline-flex py-2 px-8 btnHrStyle"
                                                type="submit"
                                            >
                                                Tìm kiếm
                                            </Button>
                                            <Button
                                                startIcon={<FilterListIcon
                                                    className={` filterRotateIcon ${isOpenFilter && 'onRotate'}`}/>}
                                                className=" d-inline-flex py-2 px-8 btnHrStyle"
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid>
                            </Grid>
                            <SalaryStaffsFilter
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

export default memo(observer(PopupStaffToolbar));