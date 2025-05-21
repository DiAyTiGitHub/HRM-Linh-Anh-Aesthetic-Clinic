import React, {useState} from "react";
import {observer} from "mobx-react";
import {useStore} from "../../../stores";
import {Form, Formik} from "formik";
import {Button, ButtonGroup, Collapse, Grid, Tooltip} from "@material-ui/core";
import GlobitsTextField from "../../../common/form/GlobitsTextField";
import SearchIcon from "@material-ui/icons/Search";
import FilterListIcon from "@material-ui/icons/FilterList";
import GlobitsDateTimePicker from "../../../common/form/GlobitsDateTimePicker";
import RotateLeftIcon from "@material-ui/icons/RotateLeft";
import HighlightOffIcon from "@material-ui/icons/HighlightOff";

const RecruitmentRequestSummaryFilter = () => {
    const {handleSetSearchObject, payload,defaultPayload} = useStore().recruitmentRequestSummaryStore;


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

    async function handleFilter(values) {
        const newSearchObject = {
            ...values,
            pageIndex: 1,
        };
        handleSetSearchObject(newSearchObject);
    }

    function handleTogglePopupFilter() {
        if (isOpenFilter) handleCloseFilter();
        else handleOpenFilter();
    }

    return (
        <Formik
            enableReinitialize
            initialValues={defaultPayload}
            onSubmit={handleFilter}
        >
            {({resetForm, values, setFieldValue, setValues}) => {
                return (
                    <Form autoComplete='off'>
                        <Grid container spacing={2}>
                            <Grid item xs={12} md={4}>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} xl={4}>
                                        {/*<ButtonGroup*/}
                                        {/*    color='container'*/}
                                        {/*    aria-label='outlined primary button group'*/}
                                        {/*>*/}
                                        {/*    <Tooltip*/}
                                        {/*        placement='top'*/}
                                        {/*        arrow*/}
                                        {/*        title='Xuất excel Báo cáo tình hình sử dụng lao động theo bộ lọc'*/}
                                        {/*    >*/}
                                        {/*        <Button*/}
                                        {/*            startIcon={<CloudDownloadIcon fontSize='small'/>}*/}
                                        {/*        >*/}
                                        {/*            Xuất Excel*/}
                                        {/*        </Button>*/}
                                        {/*    </Tooltip>*/}
                                        {/*</ButtonGroup>*/}
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid item xs={12} md={8}>
                                <div className='flex justify-between align-center'>
                                    <Tooltip placement='top' title='Tìm kiếm theo mã, tên yêu cầu tuyển dụng'>
                                        <GlobitsTextField
                                            placeholder='Tìm kiếm theo mã, tên yêu cầu tuyển dụng...'
                                            name='keyword'
                                            variant='outlined'
                                            notDelay
                                        />
                                    </Tooltip>

                                    <ButtonGroup
                                        className='filterButtonV4'
                                        color='container'
                                        aria-label='outlined primary button group'>
                                        <Button
                                            startIcon={<SearchIcon className={``}/>}
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
                                            className='d-inline-flex py-2 px-8 btnHrStyle'
                                            onClick={handleTogglePopupFilter}>
                                            Bộ lọc
                                        </Button>
                                    </ButtonGroup>
                                </div>
                            </Grid>

                            <Grid item xs={12} className="py-0">
                                <Collapse in={isOpenFilter} className="filterPopup">
                                    <div className="flex flex-column">
                                        <Grid container spacing={2}>
                                            <Grid item xs={12}>
                                                <div className="filterContent pt-8">
                                                    <Grid container spacing={2} className={"flex"}>
                                                        <Grid item xs={12} sm={12} md={12} lg={12} className="pb-0">
                                                            <p className="m-0 p-0 borderThrough2">
                                                                Thông tin yêu cầu tuyển dụng
                                                            </p>
                                                        </Grid>
                                                        <Grid item xs={12} sm={2} md={3} lg={3}>
                                                            <GlobitsDateTimePicker
                                                                name="fromDate"
                                                                label={"Ngày bắt đầu tuyển dụng từ"}
                                                                format="dd/MM/yyyy"
                                                            />
                                                        </Grid>
                                                        <Grid item xs={12} sm={2} md={3} lg={3}>
                                                            <GlobitsDateTimePicker
                                                                name="toDate"
                                                                label={"Đến ngày"}
                                                                format="dd/MM/yyyy"
                                                            />
                                                        </Grid>
                                                        <Grid item xs={12} sm={2} md={3} lg={3}>
                                                            <GlobitsDateTimePicker
                                                                name="fromEndDate"
                                                                label={"Ngày kết thúc tuyển dụng từ"}
                                                                format="dd/MM/yyyy"
                                                            />
                                                        </Grid>
                                                        <Grid item xs={12} sm={2} md={3} lg={3}>
                                                            <GlobitsDateTimePicker
                                                                name="toEndDate"
                                                                label={"Đến ngày"}
                                                                format="dd/MM/yyyy"
                                                            />
                                                        </Grid>
                                                    </Grid>

                                                    <div className="py-8 mt-12 border-bottom-fade border-top-fade">
                                                        <div className="flex justify-end">
                                                            <ButtonGroup
                                                                color="container"
                                                                aria-label="outlined primary button group"
                                                            >
                                                                <Button
                                                                    onClick={resetForm}
                                                                    startIcon={<RotateLeftIcon />}
                                                                >
                                                                    Đặt lại
                                                                </Button>
                                                                <Button
                                                                    type="button"
                                                                    onClick={handleCloseFilter}
                                                                    startIcon={<HighlightOffIcon />}
                                                                >
                                                                    Đóng bộ lọc
                                                                </Button>
                                                            </ButtonGroup>
                                                        </div>
                                                    </div>
                                                </div>
                                            </Grid>
                                        </Grid>
                                    </div>
                                </Collapse>
                            </Grid>
                        </Grid>

                    </Form>
                );
            }}
        </Formik>
    );
}
export default observer(RecruitmentRequestSummaryFilter);