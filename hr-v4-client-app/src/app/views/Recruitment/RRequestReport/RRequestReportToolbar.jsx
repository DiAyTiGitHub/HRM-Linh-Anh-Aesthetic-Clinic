import { Button, ButtonGroup, Grid, Tooltip } from "@material-ui/core";
import FilterListIcon from "@material-ui/icons/FilterList";
import SearchIcon from "@material-ui/icons/Search";
import GlobitsTextField from "app/common/form/GlobitsTextField";
import { useStore } from "app/stores";
import { Form, Formik } from "formik";
import { observer } from "mobx-react";
import { memo, useState } from "react";
import { useTranslation } from "react-i18next";
import CloudDownloadIcon from "@material-ui/icons/CloudDownload";
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { OrganizationType } from "app/LocalConstants";
import RRequestReportFilter from "./RRequestReportFilter";

function RRequestReportToolbar() {
    const { t } = useTranslation();

    const { rRequestReportStore } = useStore();

    const {
        handleSetSearchObject,
        pagingRecruitmentRequestReport,
        searchObject,
        handleOpenConfirmexportRecruitmentRequestReport
    } = rRequestReportStore;

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
        await pagingRecruitmentRequestReport();
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
                    <Form autoComplete='off'>

                        <Grid container spacing={2}>
                            <Grid item xs={12} md={4}>
                                <Grid container spacing={2}>
                                    <Grid item xs={12} xl={4}>
                                        <ButtonGroup
                                            color='container'
                                            aria-label='outlined primary button group'
                                        >
                                            <Tooltip
                                                placement='top'
                                                arrow
                                                title='Xuất excel Báo cáo tình hình sử dụng lao động theo bộ lọc'
                                            >
                                                <Button
                                                    startIcon={<CloudDownloadIcon fontSize='small' />}
                                                    onClick={() => {
                                                        handleOpenConfirmexportRecruitmentRequestReport();
                                                    }}
                                                >
                                                    Xuất Excel
                                                </Button>
                                            </Tooltip>
                                        </ButtonGroup>
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

                            <Grid item xs={12} className="py-0">
                                <RRequestReportFilter
                                    isOpenFilter={isOpenFilter}
                                    handleFilter={handleFilter}
                                    handleCloseFilter={handleCloseFilter}
                                />
                            </Grid>
                        </Grid>

                    </Form >
                );
            }}
        </Formik >
    );
}

export default memo(observer(RRequestReportToolbar));
