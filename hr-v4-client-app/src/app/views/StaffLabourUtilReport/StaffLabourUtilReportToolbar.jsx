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
import StaffLabourUtilReportFilter from "app/views/StaffLabourUtilReport/StaffLabourUtilReportFilter";
import { pagingAllOrg } from "app/views/Organization/OrganizationService";
import GlobitsPagingAutocompleteV2 from "app/common/form/GlobitsPagingAutocompleteV2";
import { OrganizationType } from "app/LocalConstants";

function StaffLabourUtilReportToolbar() {
    const { t } = useTranslation();

    const { staffLabourUtilReportStore } = useStore();

    const {
        handleSetSearchObject,
        pagingStaffLabourUtilReport,
        searchObject,
        handleOpenConfirmExportStaffLabourUtilReport
    } = staffLabourUtilReportStore;

    const [isOpenFilter, setIsOpenFilter] = useState(false);
    const [countFilterActive, setCountFilterActive] = useState(0);

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
        await pagingStaffLabourUtilReport();
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
                                                    disabled={!searchObject?.contractOrganizationId}
                                                    startIcon={<CloudDownloadIcon fontSize='small' />}
                                                    onClick={() => {
                                                        handleOpenConfirmExportStaffLabourUtilReport();
                                                    }}
                                                >
                                                    Xuất Excel
                                                </Button>
                                            </Tooltip>
                                        </ButtonGroup>
                                    </Grid>
                                </Grid>
                            </Grid>

                            <Grid container spacing={2} className='p-10'>
                                <Grid item xs={12}>
                                    <p className='m-0 p-0 borderThrough2'>Lọc và tìm kiếm</p>
                                </Grid>

                                <Grid item xs={12} lg={6} className='flex items-center' style={{ width: "150px" }}>
                                    <div className='flex items-center h-100 flex-end pr-10'>
                                        <p className='no-wrap-text'>
                                            <b>Pháp nhân:</b>
                                        </p>
                                    </div>
                                    <div style={{ width: "250px" }}>
                                        <GlobitsPagingAutocompleteV2
                                            name='contractOrganization'
                                            // label='Pháp nhân'
                                            api={pagingAllOrg}
                                            searchObject={{
                                                organizationType: OrganizationType.LEGAL_ENTITY.value
                                            }}
                                        />
                                    </div>
                                </Grid>


                                <Grid item xs={12} lg={6}>
                                    <div className="flex justify-between align-center">
                                        <GlobitsTextField
                                            placeholder="Tìm kiếm theo từ khóa"
                                            name="keyword"
                                            variant="outlined"
                                            notDelay
                                        />

                                        <ButtonGroup
                                            className='filterButtonV4'
                                            color='container'
                                            aria-label='outlined primary button group'
                                        >
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
                                                onClick={handleTogglePopupFilter}
                                            >
                                                Bộ lọc {countFilterActive > 0 && "(" + countFilterActive + ")"}
                                            </Button>
                                        </ButtonGroup>
                                    </div>
                                </Grid >
                            </Grid>

                            <Grid item xs={12} className="py-0">
                                <StaffLabourUtilReportFilter
                                    isOpenFilter={isOpenFilter}
                                    handleFilter={handleFilter}
                                    handleCloseFilter={handleCloseFilter}
                                    setCountFilterActive={setCountFilterActive}
                                />
                            </Grid>
                        </Grid>

                    </Form >
                );
            }}
        </Formik >
    );
}

export default memo(observer(StaffLabourUtilReportToolbar));
